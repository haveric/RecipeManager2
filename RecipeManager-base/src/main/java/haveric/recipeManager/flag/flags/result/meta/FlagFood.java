package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
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
        return new String[]{
            "Adds a food component to an item, making it consumable",
            "Using this flag more than once will overwrite the previous flag.",
            "",
            "  The <nutrition> argument sets food restored by this item when eaten. Value must be a non-negative integer. Defaults to 0.",
            "",
            "Optionally you can specify some arguments separated by | character:",
            "  saturation <float> = (default 0.0) saturation restored by this item when eaten. Value must be float value.",
            "  alwayseat [false] = (default false) if set, this item can be eaten even when not hungry.",
            "You can specify these arguments in any order and they're completely optional.",
        };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 5 | saturation 10 // Gives 5 hunger, 10 saturation",
            "{flag} 0 | saturation 5 | alwayseat // Gives 5 saturation, but no hunger, and can always be consumed"};
    }

    private int nutrition = 0;
    private float saturation = 0.0f;
    private boolean alwaysEat = false;

    public FlagFood() {

    }

    public FlagFood(FlagFood flag) {
        super(flag);
        nutrition = flag.nutrition;
        saturation = flag.saturation;
        alwaysEat = flag.alwaysEat;
    }

    @Override
    public FlagFood clone() {
        return new FlagFood((FlagFood) super.clone());
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
        }
    }

    // TODO: Add condition support for FlagFood
}
