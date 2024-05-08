package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FlagBannerItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.BANNER_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} | [pattern] <color> | [...]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Creates a custom banner",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "Patterns can be added after the base color and are separated by the '|' character",
            "  [pattern] is the banner pattern type",
            "    Values: " + Files.getNameIndexHashLink("bannerpattern"),
            "  <color> is required for each pattern, color values are the same as <basecolor>",
            "  Multiple patterns can be added", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} black",
            "{flag} red | circle blue | skull yellow",
            "{flag} green | half_horizontal yellow | circle orange", };
    }

    private List<Pattern> patterns = new ArrayList<>();

    public FlagBannerItem() { }

    public FlagBannerItem(FlagBannerItem flag) {
        super(flag);
        patterns.addAll(flag.patterns);
    }

    @Override
    public FlagBannerItem clone() {
        return new FlagBannerItem((FlagBannerItem) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;

        if (result != null && (result.getItemMeta() instanceof BannerMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), BannerMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a banner item!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] args = value.toUpperCase().split("\\|");

        patterns.clear();

        for (int i = 1; i < args.length; i++) {
            String pattern = args[i].trim();
            String[] split = pattern.split(" ");

            PatternType type = RMCUtil.parseEnum(split[0], PatternType.values());
            DyeColor color = RMCUtil.parseEnum(split[1], DyeColor.values());

            if (type == null) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid pattern: " + split[0]);
                return false;
            }
            if (color == null) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid color " + split[1] + " for pattern: " + split[0]);
                return false;
            }

            patterns.add(new Pattern(color, type));
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

            if (!(meta instanceof BannerMeta banner)) {
                a.addCustomReason("Needs banner!");
                return;
            }

            for (Pattern pattern : patterns) {
                banner.addPattern(pattern);
            }

            a.result().setItemMeta(banner);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        for (Pattern pattern : patterns) {
            toHash += pattern.hashCode();
        }

        return toHash.hashCode();
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        parse(meta, recipeString, Files.NL + "@banneritem ", "", " | ");
    }

    @Override
    public void parseIngredientForConditions(ItemStack item, ItemMeta meta, StringBuilder ingredientCondition) {
        parse(meta, ingredientCondition, " | banner ", ",", " pattern ");
    }

    private void parse(ItemMeta meta, StringBuilder builder, String prefix, String afterFirst, String patternPrefix) {
        if (meta instanceof BannerMeta bannerMeta) {
            builder.append(prefix);

            boolean first = true;
            for (Pattern pattern : bannerMeta.getPatterns()) {
                PatternType patternType = pattern.getPattern();
                DyeColor patternColor = pattern.getColor();

                if (!first) {
                    builder.append(afterFirst);
                }
                builder.append(patternPrefix).append(patternType.name()).append(" ").append(patternColor.name());

                first = false;
            }
        }
    }
}