package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import haveric.recipeManager.tools.Version;
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
            "{flag} <item>[:data][:amount]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "{flag} Adds a stack of items to a bundle.",
            "This flag can be used more than once to add more items to bundle.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} dirt:0:40 // Add 40 dirt",
            "{flag} diamond_sword:1500 // Add an almost destroyed diamond_sword", };
    }

    private final List<ItemStack> items = new ArrayList<>();

    public FlagBundleItem() { }

    public FlagBundleItem(FlagBundleItem flag) {
        super(flag);
        items.addAll(flag.items);
    }

    @Override
    public FlagBundleItem clone() {
        return new FlagBundleItem((FlagBundleItem) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof BundleMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        if (Version.has1_13BasicSupport()) {
            FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();

            if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), BundleMeta.class)) {
                validFlaggable = true;
            }
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a bundle item!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);

        ItemStack item = Tools.parseItem(value, RMCVanilla.DATA_WILDCARD, ParseBit.NO_META);
        MessageSender.getInstance().info("Item: " + item);
        if (item != null) {
            items.add(item);
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

            if (!(meta instanceof BundleMeta)) {
                a.addCustomReason("Needs bundle!");
                return;
            }

            BundleMeta bundleMeta = (BundleMeta) meta;
            for (ItemStack item : items) {
                bundleMeta.addItem(item);
            }

            a.result().setItemMeta(bundleMeta);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        for (ItemStack item : items) {
            toHash += "item: " + item.hashCode();
        }

        return toHash.hashCode();
    }
}
