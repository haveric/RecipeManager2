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
        baseColor = banner.getBaseColor();
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
        return type == Material.BANNER;
    }
}
