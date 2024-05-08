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
        return switch (type) {
            case BLACK_BANNER, BLACK_WALL_BANNER, BLUE_BANNER, BLUE_WALL_BANNER, BROWN_BANNER, BROWN_WALL_BANNER,
                 CYAN_BANNER, CYAN_WALL_BANNER, GRAY_BANNER, GRAY_WALL_BANNER, GREEN_BANNER, GREEN_WALL_BANNER,
                 LIGHT_BLUE_BANNER, LIGHT_BLUE_WALL_BANNER, LIGHT_GRAY_BANNER, LIGHT_GRAY_WALL_BANNER, LIME_BANNER,
                 LIME_WALL_BANNER, MAGENTA_BANNER, MAGENTA_WALL_BANNER, ORANGE_BANNER, ORANGE_WALL_BANNER, PINK_BANNER,
                 PINK_WALL_BANNER, PURPLE_BANNER, PURPLE_WALL_BANNER, RED_BANNER, RED_WALL_BANNER, WHITE_BANNER,
                 WHITE_WALL_BANNER, YELLOW_BANNER, YELLOW_WALL_BANNER -> true;
            default -> false;
        };
    }
}
