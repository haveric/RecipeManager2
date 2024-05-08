package haveric.recipeManager;

import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.meta.FireworkEffectMeta;

public class TestMetaCharge extends TestMetaItem implements FireworkEffectMeta {
    private FireworkEffect effect;

    TestMetaCharge(TestMetaItem meta) {
        super(meta);

        if (meta instanceof TestMetaCharge testMetaCharge) {
            effect = testMetaCharge.effect;
        }
    }

    @Override
    public void setEffect(FireworkEffect effect) {
        this.effect = effect;
    }

    @Override
    public boolean hasEffect() {
        return effect != null;
    }

    @Override
    public FireworkEffect getEffect() {
        return effect;
    }

    @Override
    boolean applicableTo(Material type) {
        return type == Material.FIREWORK_STAR;
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && !hasChargeMeta();
    }

    boolean hasChargeMeta() {
        return hasEffect();
    }

    @Override
    boolean equalsCommon(TestMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof TestMetaCharge that) {
            return (hasEffect() ? that.hasEffect() && this.effect.equals(that.effect) : !that.hasEffect());
        }
        return true;
    }

    @Override
    boolean notUncommon(TestMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof TestMetaCharge || !hasChargeMeta());
    }

    @Override
    public TestMetaCharge clone() {
        return (TestMetaCharge) super.clone();
    }
}
