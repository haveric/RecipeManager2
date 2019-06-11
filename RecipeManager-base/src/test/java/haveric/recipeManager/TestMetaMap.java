package haveric.recipeManager;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

public class TestMetaMap extends TestMetaItem implements MapMeta {
    static final byte SCALING_EMPTY = (byte) 0;
    static final byte SCALING_TRUE = (byte) 1;
    static final byte SCALING_FALSE = (byte) 2;

    private Integer mapId;
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
        return type == Material.MAP;
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isMapEmpty();
    }

    boolean isMapEmpty() {
        return !(hasScaling() | hasLocationName() || hasColor());
    }



    @Override
    public boolean hasMapId() {
        return mapId != null;
    }

    @Override
    public int getMapId() {
        return mapId;
    }

    @Override
    public void setMapId(int id) {
        this.mapId = id;
    }

    @Override
    public boolean hasMapView() {
        return mapId != null;
    }

    @Override
    public MapView getMapView() {
        Preconditions.checkState(hasMapView(), "Item does not have map associated - check hasMapView() first!");
        return Bukkit.getMap(mapId);
    }

    @Override
    public void setMapView(MapView map) {
        this.mapId = (map != null) ? map.getId() : null;
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
