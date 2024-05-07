package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.entity.Axolotl;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class FlagAxolotlBucketItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.AXOLOTL_BUCKET_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <variant>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Customize an axolotl bucket item",
            "",
            "<variant> is required and defines the variant of the axolotl in the bucket",
            "  values: " + RMCUtil.collectionToString(Arrays.asList(Axolotl.Variant.values())).toLowerCase(), };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} blue", };
    }

    private Axolotl.Variant variant;

    public FlagAxolotlBucketItem() { }

    public FlagAxolotlBucketItem(FlagAxolotlBucketItem flag) {
        super(flag);
        variant = flag.variant;
    }

    @Override
    public FlagAxolotlBucketItem clone() {
        return new FlagAxolotlBucketItem((FlagAxolotlBucketItem) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    private boolean hasVariant() {
        return variant != null;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof AxolotlBucketMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), AxolotlBucketMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs an axolotl bucket item!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);

        variant = RMCUtil.parseEnum(value, Axolotl.Variant.values());

        if (variant == null) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid variant: " + value);
            return false;
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

            if (!(meta instanceof AxolotlBucketMeta)) {
                a.addCustomReason("Needs axolotl bucket item!");
                return;
            }

            AxolotlBucketMeta axolotlBucketMeta = (AxolotlBucketMeta) meta;
            if (hasVariant()) {
                axolotlBucketMeta.setVariant(variant);
            }

            a.result().setItemMeta(axolotlBucketMeta);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "variant: " + variant.toString();

        return toHash.hashCode();
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        if (Supports.axolotlBucketMeta() && meta instanceof AxolotlBucketMeta) {
            AxolotlBucketMeta axolotlBucketMeta = (AxolotlBucketMeta) meta;
            if (axolotlBucketMeta.hasVariant()) {
                recipeString.append(Files.NL).append("@axolotlbucketitem ").append(axolotlBucketMeta.getVariant());
            }
        }
    }

    // TODO: Add condition support for FlagAxolotlBucketItem
}
