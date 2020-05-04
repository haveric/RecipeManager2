package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlagBannerItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.BANNER_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <basecolor> | [pattern] <color> | [...]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Creates a custom banner",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "The <basecolor> argument is required",
            "  Values: " + RMCUtil.collectionToString(Arrays.asList(DyeColor.values())).toLowerCase(),
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
            "{flag} red | circle_middle blue | skull yellow",
            "{flag} green | half_horizontal yellow | circle_middle orange", };
    }


    private DyeColor baseColor;
    private List<Pattern> patterns = new ArrayList<>();

    public FlagBannerItem() { }

    public FlagBannerItem(FlagBannerItem flag) {
        baseColor = flag.baseColor;
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

    public DyeColor getBaseColor() {
        return baseColor;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        String[] args = value.toUpperCase().split("\\|");

        baseColor = RMCUtil.parseEnum(args[0].trim(), DyeColor.values());

        if (baseColor == null) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid base color!");
            return false;
        }

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

            if (!(meta instanceof BannerMeta)) {
                a.addCustomReason("Needs banner!");
                return;
            }

            BannerMeta banner = (BannerMeta) meta;

            banner.setBaseColor(baseColor);

            for (Pattern pattern : patterns) {
                banner.addPattern(pattern);
            }

            a.result().setItemMeta(banner);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "baseColor: " + baseColor.toString();

        for (Pattern pattern : patterns) {
            toHash += pattern.hashCode();
        }

        return toHash.hashCode();
    }
}