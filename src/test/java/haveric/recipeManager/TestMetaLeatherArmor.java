package haveric.recipeManager;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import static haveric.recipeManager.TestItemFactory.DEFAULT_LEATHER_COLOR;

public class TestMetaLeatherArmor extends TestMetaItem implements LeatherArmorMeta {

    private Color color = DEFAULT_LEATHER_COLOR;

    TestMetaLeatherArmor(TestMetaItem meta) {
        super(meta);
        if (!(meta instanceof TestMetaLeatherArmor)) {
            return;
        }

        TestMetaLeatherArmor armorMeta = (TestMetaLeatherArmor) meta;
        this.color = armorMeta.color;
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isLeatherArmorEmpty();
    }

    boolean isLeatherArmorEmpty() {
        return !(hasColor());
    }

    @Override
    boolean applicableTo(Material type) {
        switch(type) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return true;
            default:
                return false;
        }
    }

    @Override
    public TestMetaLeatherArmor clone() {
        return (TestMetaLeatherArmor) super.clone();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color == null ? DEFAULT_LEATHER_COLOR : color;
    }

    boolean hasColor() {
        return !DEFAULT_LEATHER_COLOR.equals(color);
    }

    @Override
    boolean equalsCommon(TestMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof TestMetaLeatherArmor) {
            TestMetaLeatherArmor that = (TestMetaLeatherArmor) meta;

            return color.equals(that.color);
        }
        return true;
    }

    @Override
    boolean notUncommon(TestMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof TestMetaLeatherArmor || isLeatherArmorEmpty());
    }
}
