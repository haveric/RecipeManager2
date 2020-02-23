package haveric.recipeManager;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
import java.util.List;

public class TestMetaBanner extends TestMetaItem implements BannerMeta {
    private DyeColor baseColor;
    private List<Pattern> patterns = new ArrayList<>();

    public TestMetaBanner(TestMetaItem meta) {
        super(meta);

        if (!(meta instanceof TestMetaBanner)) {
            return;
        }

        TestMetaBanner banner = (TestMetaBanner) meta;
        baseColor = banner.baseColor;
        patterns = new ArrayList<>(banner.getPatterns());
    }

    public DyeColor getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(DyeColor color) {
        baseColor = color;
    }

    public List<Pattern> getPatterns() {
        return new ArrayList<>(patterns);
    }

    public void setPatterns(List<Pattern> patterns) {
        this.patterns = new ArrayList<>(patterns);
    }

    public void addPattern(Pattern pattern) {
        patterns.add(pattern);
    }

    public Pattern getPattern(int i) {
        return patterns.get(i);
    }

    public Pattern removePattern(int i) {
        return patterns.remove(i);
    }

    public void setPattern(int i, Pattern pattern) {
        patterns.set(i, pattern);
    }

    public int numberOfPatterns() {
        return patterns.size();
    }

    boolean applicableTo(Material type) {
        switch (type) {
            case BLACK_BANNER:
            case BLACK_WALL_BANNER:
            case BLUE_BANNER:
            case BLUE_WALL_BANNER:
            case BROWN_BANNER:
            case BROWN_WALL_BANNER:
            case CYAN_BANNER:
            case CYAN_WALL_BANNER:
            case GRAY_BANNER:
            case GRAY_WALL_BANNER:
            case GREEN_BANNER:
            case GREEN_WALL_BANNER:
            case LIGHT_BLUE_BANNER:
            case LIGHT_BLUE_WALL_BANNER:
            case LIGHT_GRAY_BANNER:
            case LIGHT_GRAY_WALL_BANNER:
            case LIME_BANNER:
            case LIME_WALL_BANNER:
            case MAGENTA_BANNER:
            case MAGENTA_WALL_BANNER:
            case ORANGE_BANNER:
            case ORANGE_WALL_BANNER:
            case PINK_BANNER:
            case PINK_WALL_BANNER:
            case PURPLE_BANNER:
            case PURPLE_WALL_BANNER:
            case RED_BANNER:
            case RED_WALL_BANNER:
            case WHITE_BANNER:
            case WHITE_WALL_BANNER:
            case YELLOW_BANNER:
            case YELLOW_WALL_BANNER:
                return true;
            default:
                return false;
        }
    }
}
