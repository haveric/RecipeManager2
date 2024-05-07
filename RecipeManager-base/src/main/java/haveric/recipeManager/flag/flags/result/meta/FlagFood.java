package haveric.recipeManager.flag.flags.result.meta;

import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.item.ItemRecipe;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;

public class FlagFood extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.FOOD;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <nutrition> | [arguments]", };
    }

    @Override
    protected String[] getDescription() {
        String[] description = new String[]{
            "Adds a food component to an item, making it consumable",
            "Using this flag more than once will overwrite the previous flag.",
            "",
            "  The <nutrition> argument sets food restored by this item when eaten. Value must be a non-negative integer. Defaults to 0.",
            "",
            "Optionally you can specify some arguments separated by | character:",
            "  saturation <float> = (default 0.0) saturation restored by this item when eaten. Value must be float value.",
            "  alwayseat [false] = (default false) if set, this item can be eaten even when not hungry.",
            "  seconds <float> = (default 0.0) time in seconds it will take for this item to be eaten.",
            "    NOTE: The default value of 0.0 will not consume the item!",
        };

        if (Version.has1_21Support()) {
            description = ObjectArrays.concat(description, new String[]{
                "  convertsto <item>[:data][:amount] = sets the item this food will convert to once eaten.",
                "  convertsto item:<name> = sets the item this food will convert to once eaten.",
                "    <name> = The name of an item recipe defined before this flag."
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[]{
            "You can specify these arguments in any order and they're completely optional.",
            "",
            "NOTE: You can also add Potion effects with " + FlagType.FOOD_POTION_EFFECT,
        }, String.class);

        return description;
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 5 | saturation 10 | seconds 0.1 // Gives 5 hunger, 10 saturation, and can be eaten in 0.1 seconds",
            "{flag} 0 | saturation 5 | seconds 2 | alwayseat // Gives 5 saturation, but no hunger, can be eaten in 2 seconds, and can always be consumed"};
    }

    private int nutrition = 0;
    private float saturation = 0.0f;
    private boolean alwaysEat = false;
    private float seconds = 0.0f;
    private ItemRecipe convertsToItemRecipe = null;

    public FlagFood() {

    }

    public FlagFood(FlagFood flag) {
        super(flag);
        nutrition = flag.nutrition;
        saturation = flag.saturation;
        alwaysEat = flag.alwaysEat;
        seconds = flag.seconds;
        convertsToItemRecipe = flag.convertsToItemRecipe;
    }

    @Override
    public FlagFood clone() {
        return new FlagFood((FlagFood) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return convertsToItemRecipe == null;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.toLowerCase().split("\\|");

        value = split[0].trim();

        try {
            nutrition = Integer.parseInt(value);
        } catch (IllegalArgumentException e) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid nutrition value: " + value);
        }

        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                value = split[i].trim();

                if (value.startsWith("saturation")) {
                    value = value.substring("saturation".length()).trim();

                    try {
                        saturation = Integer.parseInt(value);
                    } catch (IllegalArgumentException e) {
                        return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid saturation value: " + value);
                    }
                } else if (value.startsWith("alwayseat")) {
                    value = value.substring("alwayseat".length()).trim();

                    if (value.isEmpty() || value.equals("true")) {
                        alwaysEat = true;
                    } else if (value.equals("false")) {
                        alwaysEat = false;
                    }
                } else if (value.startsWith("seconds")) {
                    value = value.substring("seconds".length()).trim();

                    try {
                        seconds = Float.parseFloat(value);
                    } catch (IllegalArgumentException e) {
                        return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid seconds value: " + value);
                    }
                } else if (Version.has1_21Support() && value.startsWith("convertsto")) {
                    value = value.substring("convertsto".length()).trim();

                    if (value.startsWith("item:")) {
                        value = value.substring("item:".length());

                        ItemRecipe recipe = ItemRecipe.getRecipe(value);
                        if (recipe == null) {
                            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid item reference: " + value + "!");
                        } else {
                            convertsToItemRecipe = recipe;
                        }
                    } else {
                        ItemStack item = Tools.parseItem(value, 0);
                        if (item == null || item.getType() == Material.AIR) {
                            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid item defined!");
                        } else {
                            ItemRecipe recipe = new ItemRecipe();
                            recipe.setResult(item);
                            convertsToItemRecipe = recipe;
                        }
                    }
                } else {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + value, "Maybe it's spelled wrong, check it in " + Files.FILE_INFO_FLAGS + " file.");
                }
            }
        }

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        onCrafted(a);
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();

            if (meta != null) {
                FoodComponent food = meta.getFood();

                food.setNutrition(nutrition);
                food.setSaturation(saturation);
                food.setCanAlwaysEat(alwaysEat);
                food.setEatSeconds(seconds);
                if (Version.has1_21Support()) {
                    if (convertsToItemRecipe != null) {
                        ItemResult result = convertsToItemRecipe.getResult();
                        Args itemArgs = ArgBuilder.create(a).recipe(convertsToItemRecipe).result(result).build();
                        itemArgs.setFirstRun(true);
                        if (result.getFlags().sendCrafted(itemArgs)) {
                            food.setUsingConvertsTo(itemArgs.result().getItemStack());
                        }
                    }
                }

                meta.setFood(food);
            }

            a.result().setItemMeta(meta);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "nutrition: " + nutrition;
        toHash += "saturation: " + saturation;
        toHash += "alwaysEat: " + alwaysEat;
        toHash += "seconds: " + seconds;

        return toHash.hashCode();
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        if (meta != null && meta.hasFood()) {
            FoodComponent food = meta.getFood();

            recipeString.append(Files.NL).append("@food ").append(food.getNutrition()).append(" | saturation ").append(food.getSaturation());

            if (food.canAlwaysEat()) {
                recipeString.append(" | alwayseat");
            }

            recipeString.append(" | seconds ").append(food.getEatSeconds());
        }
    }

    // TODO: Add condition support for FlagFood
}
