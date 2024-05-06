package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.item.ItemRecipe;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FlagCrossbowItem extends Flag {
    @Override
    public String getFlagType() {
        return FlagType.CROSSBOW_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <item>[:data][:amount]",
            "{flag} item:<name>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "{flag} Adds a charged projectile to a crossbow.",
            "This flag can be used more than once to add more charged projectiles.",
            "  NOTE: The item must be a valid crossbow projectile, such as an arrow or firework rocket.",
            "",
            "You can use a predefined item from an item recipe:",
            "  Format = item:<name>",
            "  <name> = The name of an item recipe defined before this flag.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} arrow:0:40 // Add 40 arrows",
            "{flag} firework_rocket // Adds a firework rocket",
            "{flag} item:customrocket // Will use the item from the 'customrocket' recipe, assuming it's defined.", };
    }

    private final List<ItemRecipe> itemRecipes = new ArrayList<>();

    public FlagCrossbowItem() { }

    public FlagCrossbowItem(FlagCrossbowItem flag) {
        super(flag);
        itemRecipes.addAll(flag.itemRecipes);
    }

    @Override
    public FlagCrossbowItem clone() {
        return new FlagCrossbowItem((FlagCrossbowItem) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        boolean requiresModification = false;
        for (ItemRecipe itemRecipe : itemRecipes) {
            if (itemRecipe.requiresRecipeManagerModification()) {
                requiresModification = true;
                break;
            }
        }

        return requiresModification;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof CrossbowMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), CrossbowMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a crossbow item!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);

        String valueLower = value.trim().toLowerCase();
        if (valueLower.startsWith("item:")) {
            value = value.substring("item:".length());

            ItemRecipe recipe = ItemRecipe.getRecipe(value);
            if (recipe == null) {
                return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid item reference: " + value + "!");
            } else {
                itemRecipes.add(recipe);
            }
        } else {
            ItemStack item = Tools.parseItem(value, 0);
            if (item == null || item.getType() == Material.AIR) {
                return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid item defined!");
            } else {
                ItemRecipe recipe = new ItemRecipe();
                recipe.setResult(item);
                itemRecipes.add(recipe);
            }
        }

        return true;
    }


    @Override
    public void onPrepare(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();

            if (!(meta instanceof CrossbowMeta)) {
                a.addCustomReason("Needs crossbow!");
                return;
            }

            CrossbowMeta crossbowMeta = (CrossbowMeta) meta;

            for (ItemRecipe itemRecipe : itemRecipes) {
                ItemResult result = itemRecipe.getResult();
                Args itemArgs = ArgBuilder.create(a).recipe(itemRecipe).result(result).build();
                itemArgs.setFirstRun(true);

                if (result.getFlags().sendPrepare(itemArgs, true)) {
                    try {
                        crossbowMeta.addChargedProjectile(itemArgs.result().getItemStack());
                    } catch (IllegalArgumentException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has an invalid charged projectile: " + result + "!");
                    }
                }
            }

            a.result().setItemMeta(crossbowMeta);
        }
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();

            if (!(meta instanceof CrossbowMeta)) {
                a.addCustomReason("Needs crossbow!");
                return;
            }

            CrossbowMeta crossbowMeta = (CrossbowMeta) meta;

            for (ItemRecipe itemRecipe : itemRecipes) {
                ItemResult result = itemRecipe.getResult();
                Args itemArgs = ArgBuilder.create(a).recipe(itemRecipe).result(result).build();
                itemArgs.setFirstRun(true);

                if (result.getFlags().sendCrafted(itemArgs)) {
                    try {
                        crossbowMeta.addChargedProjectile(itemArgs.result().getItemStack());
                    } catch (IllegalArgumentException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has an invalid charged projectile: " + result + "!");
                    }
                }
            }

            a.result().setItemMeta(crossbowMeta);
        }
    }

    @Override
    public int hashCode() {
        StringBuilder toHash = new StringBuilder(super.hashCode());

        for (ItemRecipe itemRecipe : itemRecipes) {
            toHash.append("itemRecipe: ").append(itemRecipe.hashCode());
            toHash.append("item: ").append(itemRecipe.getResult().hashCode());
        }

        return toHash.toString().hashCode();
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        if (meta instanceof CrossbowMeta) {
            CrossbowMeta crossbowMeta = (CrossbowMeta) meta;

            if (crossbowMeta.hasChargedProjectiles()) {
                for (ItemStack projectile : crossbowMeta.getChargedProjectiles()) {
                    recipeString.append(Files.NL).append("@crossbow ").append(Tools.convertItemToStringId(projectile));

                    // TODO: Handle projectile meta
                }
            }
        }
    }
}
