package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.DyeColor;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

import java.util.Arrays;

public class FlagTropicalFishBucketItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.TROPICAL_FISH_BUCKET_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <arguments> | [...]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Customize a tropical fish bucket",
            "",
            "Arguments can be one or more of the following, separated by | character:",
            "  bodycolor <dyecolor> = (default largest) merge action for all of the ingredients",
            "  pattern <pattern>     = (default largest) merge action applied to the result",
            "  patterncolor <dyecolor> = Ignore enchantment level restrictions",
            "",
            "<dyecolor> values: " + RMCUtil.collectionToString(Arrays.asList(DyeColor.values())).toLowerCase(),
            "<pattern> values: " + RMCUtil.collectionToString(Arrays.asList(TropicalFish.Pattern.values())).toLowerCase(), };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} bodycolor blue | pattern dasher", };
    }

    private DyeColor bodyColor;
    private TropicalFish.Pattern pattern;
    private DyeColor patternColor;

    public FlagTropicalFishBucketItem() { }

    public FlagTropicalFishBucketItem(FlagTropicalFishBucketItem flag) {
        super(flag);
        bodyColor = flag.bodyColor;
        pattern = flag.pattern;
        patternColor = flag.patternColor;
    }

    @Override
    public FlagTropicalFishBucketItem clone() {
        return new FlagTropicalFishBucketItem((FlagTropicalFishBucketItem) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    public DyeColor getBodyColor() {
        return bodyColor;
    }

    public void setBodyColor(DyeColor bodyColor) {
        this.bodyColor = bodyColor;
    }

    public boolean hasBodyColor() {
        return bodyColor != null;
    }

    public TropicalFish.Pattern getPattern() {
        return pattern;
    }

    public void setPattern(TropicalFish.Pattern pattern) {
        this.pattern = pattern;
    }

    public boolean hasPattern() {
        return pattern != null;
    }

    public DyeColor getPatternColor() {
        return patternColor;
    }

    public void setPatternColor(DyeColor patternColor) {
        this.patternColor = patternColor;
    }

    public boolean hasPatternColor() {
        return patternColor != null;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof TropicalFishBucketMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), TropicalFishBucketMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a tropical fish bucket item!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] args = value.toUpperCase().split("\\|");

        for (String s : args) {
            String arg = s.trim().toLowerCase();

            if (arg.startsWith("bodycolor")) {
                arg = arg.substring("bodycolor".length()).trim();

                DyeColor color = RMCUtil.parseEnum(arg, DyeColor.values());
                if (color == null) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid bodycolor: " + arg);
                    return false;
                } else {
                    bodyColor = color;
                }
            } else if (arg.startsWith("patterncolor")) {
                arg = arg.substring("patterncolor".length()).trim();

                DyeColor color = RMCUtil.parseEnum(arg, DyeColor.values());
                if (color == null) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid patterncolor: " + arg);
                    return false;
                } else {
                    patternColor = color;
                }
            } else if (arg.startsWith("pattern")) {
                arg = arg.substring("pattern".length()).trim();

                TropicalFish.Pattern patternArg = RMCUtil.parseEnum(arg, TropicalFish.Pattern.values());
                if (patternArg == null) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid pattern: " + arg);
                    return false;
                } else {
                    pattern = patternArg;
                }
            } else {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid arg: " + arg);
                return false;
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

            if (!(meta instanceof TropicalFishBucketMeta)) {
                a.addCustomReason("Needs tropical fish bucket!");
                return;
            }

            TropicalFishBucketMeta tropicalFishBucketMeta = (TropicalFishBucketMeta) meta;
            if (hasPattern()) {
                tropicalFishBucketMeta.setPattern(pattern);
            }

            if (hasPatternColor()) {
                tropicalFishBucketMeta.setPatternColor(patternColor);
            }

            if (hasBodyColor()) {
                tropicalFishBucketMeta.setBodyColor(bodyColor);
            }

            a.result().setItemMeta(tropicalFishBucketMeta);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "bodyColor: " + bodyColor.toString();
        toHash += "pattern: " + pattern.toString();
        toHash += "patternColor: " + patternColor.toString();

        return toHash.hashCode();
    }
}
