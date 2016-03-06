package haveric.recipeManager.flags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ObjectArrays;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.util.RMCUtil;

public class FlagBannerItem extends Flag {

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <basecolor> | [pattern] <color> | [...]", };
    }

    @Override
    protected String[] getDescription() {
        String[] description = new String[] {
            "Creates a custom banner",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "The <basecolor> argument is required",
            "  Values: " + RMCUtil.collectionToString(Arrays.asList(DyeColor.values())).toLowerCase(),
            "",
            "Patterns can be added after the base color and are separated by the '|' character",
            "  [pattern] is the banner pattern type",
        };

        if (Version.has18Support()) {
            description = ObjectArrays.concat(description, new String[] {
                "    Values: " + RMCUtil.collectionToString(Arrays.asList(PatternType.values())).toLowerCase(),
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[] {
            "  <color> is required for each pattern, color values are the same as <basecolor>",
            "  Multiple patterns can be added",
        }, String.class);

        return description;
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} black",
            "{flag} red | circle_middle blue | skull yellow",
            "{flag} green | half_horizontal yellow | circle_middle orange", };
    }


    private DyeColor baseColor;
    private List<Pattern> patterns = new ArrayList<Pattern>();

    public FlagBannerItem() { }

    public FlagBannerItem(FlagBannerItem flag) {
        baseColor = flag.baseColor;
        patterns.addAll(flag.patterns);
    }

    @Override
    public FlagBannerItem clone() {
        return new FlagBannerItem((FlagBannerItem) super.clone());
    }

    public DyeColor getBaseColor() {
        return baseColor;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    protected boolean onParse(String value) {
        String[] args = value.toUpperCase().split("\\|");

        baseColor = RMCUtil.parseEnum(args[0].trim(), DyeColor.values());

        if (baseColor == null) {
            ErrorReporter.warning("Flag " + getType() + " has invalid base color!");
            return false;
        }

        for (int i = 1; i < args.length; i++) {
            String pattern = args[i].trim();
            String[] split = pattern.split(" ");

            PatternType type = RMCUtil.parseEnum(split[0], PatternType.values());
            DyeColor color = RMCUtil.parseEnum(split[1], DyeColor.values());

            if (type == null) {
                ErrorReporter.warning("Flag " + getType() + " has invalid pattern: " + split[0]);
                return false;
            }
            if (color == null) {
                ErrorReporter.warning("Flag " + getType() + " has invalid color " + split[1] + " for pattern: " + split[0]);
                return false;
            }

            patterns.add(new Pattern(color, type));
        }

        return true;
    }

    @Override
    protected void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Need result!");
            return;
        }

        ItemStack item = a.result();
        ItemMeta meta = item.getItemMeta();

        if (!(meta instanceof BannerMeta)) {
            a.addCustomReason("Needs banner!");
            return;
        }

        BannerMeta banner = (BannerMeta) meta;

        banner.setBaseColor(baseColor);

        for (Pattern pattern : patterns) {
            banner.addPattern(pattern);
        }

        item.setItemMeta(banner);
    }
}