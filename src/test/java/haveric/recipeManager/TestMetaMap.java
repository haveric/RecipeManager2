package haveric.recipeManager;

import org.bukkit.Material;
import org.bukkit.inventory.meta.MapMeta;

public class TestMetaMap extends TestMetaItem implements MapMeta {
    static final byte SCALING_EMPTY = (byte) 0;
    static final byte SCALING_TRUE = (byte) 1;
    static final byte SCALING_FALSE = (byte) 2;

    private byte scaling = SCALING_EMPTY;

    TestMetaMap(TestMetaItem meta) {
        super(meta);

        if (!(meta instanceof TestMetaMap)) {
            return;
        }

        TestMetaMap map = (TestMetaMap) meta;
        this.scaling = map.scaling;
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
        return !hasScaling();
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
    boolean equalsCommon(TestMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof TestMetaMap) {
            TestMetaMap that = (TestMetaMap) meta;

            return (this.scaling == that.scaling);
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
