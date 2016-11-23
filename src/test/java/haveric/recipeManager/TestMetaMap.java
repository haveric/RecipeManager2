package haveric.recipeManager;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.MapMeta;

public class TestMetaMap extends TestMetaItem implements MapMeta {
    static final byte SCALING_EMPTY = (byte) 0;
    static final byte SCALING_TRUE = (byte) 1;
    static final byte SCALING_FALSE = (byte) 2;

    private byte scaling = SCALING_EMPTY;
    private String locName;
    private Color color;

    TestMetaMap(TestMetaItem meta) {
        super(meta);

        if (!(meta instanceof TestMetaMap)) {
            return;
        }

        TestMetaMap map = (TestMetaMap) meta;
        this.scaling = map.scaling;
        this.locName = map.locName;
        this.color = map.color;
    }

    @Override
    boolean applicableTo(Material type) {
        switch (type) {
            case MAP:
                return true;
            default:
                return false;
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isMapEmpty();
    }

    boolean isMapEmpty() {
        return !(hasScaling() | hasLocationName() || hasColor());
    }

    boolean hasScaling() {
        return scaling != SCALING_EMPTY;
    }

    public boolean isScaling() {
        return scaling == SCALING_TRUE;
    }

    public void setScaling(boolean scaling) {
        this.scaling = scaling ? SCALING_TRUE : SCALING_FALSE;
    }

    @Override
    public boolean hasLocationName() {
        return this.locName != null;
    }

    @Override
    public String getLocationName() {
        return this.locName;
    }

    @Override
    public void setLocationName(String name) {
        this.locName = name;
    }

    @Override
    public boolean hasColor() {
        return this.color != null;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    boolean equalsCommon(TestMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof TestMetaMap) {
            TestMetaMap that = (TestMetaMap) meta;

            return (this.scaling == that.scaling)
                    && (hasLocationName() ? that.hasLocationName() && this.locName.equals(that.locName) : !that.hasLocationName())
                    && (hasColor() ? that.hasColor() && this.color.equals(that.color) : !that.hasColor());
        }
        return true;
    }

    @Override
    boolean notUncommon(TestMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof TestMetaMap || isMapEmpty());
    }

    public TestMetaMap clone() {
        return (TestMetaMap) super.clone();
    }
}
