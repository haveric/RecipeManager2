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
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FlagBundleItem extends Flag {
    @Override
    public String getFlagType() {
        return FlagType.BUNDLE_ITEM;
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
            "{flag} Adds a stack of items to a bundle.",
            "This flag can be used more than once to add more items to bundle.",
            "",
            "You can use a predefined item from an item recipe:",
            "  Format = item:<name>",
            "  <name> = The name of an item recipe defined before this flag.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} dirt:0:40 // Add 40 dirt",
            "{flag} diamond_sword:1500 // Add an almost destroyed diamond_sword",
            "{flag} item:test sword // Will use the item from the 'test sword' recipe, assuming it's defined.", };
    }

    private final List<ItemRecipe> itemRecipes = new ArrayList<>();

    public FlagBundleItem() { }

    public FlagBundleItem(FlagBundleItem flag) {
        super(flag);
        itemRecipes.addAll(flag.itemRecipes);
    }

    @Override
    public FlagBundleItem clone() {
        return new FlagBundleItem((FlagBundleItem) super.clone());
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
        if (result != null && (result.getItemMeta() instanceof BundleMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), BundleMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a bundle item!");
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

            if (!(meta instanceof BundleMeta bundleMeta)) {
                a.addCustomReason("Needs bundle!");
                return;
            }

            for (ItemRecipe itemRecipe : itemRecipes) {
                ItemResult result = itemRecipe.getResult();
                Args itemArgs = ArgBuilder.create(a).recipe(itemRecipe).result(result).build();
                itemArgs.setFirstRun(true);

                if (result.getFlags().sendPrepare(itemArgs, true)) {
                    bundleMeta.addItem(itemArgs.result().getItemStack());
                }
            }

            a.result().setItemMeta(bundleMeta);
        }
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();

            if (!(meta instanceof BundleMeta bundleMeta)) {
                a.addCustomReason("Needs bundle!");
                return;
            }

            for (ItemRecipe itemRecipe : itemRecipes) {
                ItemResult result = itemRecipe.getResult();
                Args itemArgs = ArgBuilder.create(a).recipe(itemRecipe).result(result).build();
                itemArgs.setFirstRun(true);

                if (result.getFlags().sendCrafted(itemArgs)) {
                    bundleMeta.addItem(itemArgs.result().getItemStack());
                }
            }

            a.result().setItemMeta(bundleMeta);
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
        if (meta instanceof BundleMeta bundleMeta) {
            List<ItemStack> bundleItems = bundleMeta.getItems();
            for (ItemStack bundleItem : bundleItems) {
                recipeString.append(Files.NL).append("@bundle ").append(bundleItem.getType());
                recipeString.append(":").append(bundleItem.getDurability()).append(":").append(bundleItem.getAmount());
            }
        }
    }

    // TODO: Add condition support for FlagBundleItem
}
