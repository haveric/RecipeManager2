package haveric.recipeManager;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestMetaFirework extends TestMetaItem implements FireworkMeta {
    private List<FireworkEffect> effects;
    private int power;

    TestMetaFirework(TestMetaItem meta) {
        super(meta);

        if (!(meta instanceof TestMetaFirework)) {
            return;
        }

        TestMetaFirework that = (TestMetaFirework) meta;

        this.power = that.power;

        if (that.hasEffects()) {
            this.effects = new ArrayList<>(that.effects);
        }
    }

    static int getNBT(Type type) {
        switch (type) {
            case BALL:
                return 0;
            case BALL_LARGE:
                return 1;
            case STAR:
                return 2;
            case CREEPER:
                return 3;
            case BURST:
                return 4;
            default:
                throw new AssertionError(type);
        }
    }

    static Type getEffectType(int nbt) {
        switch (nbt) {
            case 0:
                return Type.BALL;
            case 1:
                return Type.BALL_LARGE;
            case 2:
                return Type.STAR;
            case 3:
                return Type.CREEPER;
            case 4:
                return Type.BURST;
            default:
                throw new AssertionError(nbt);
        }
    }

    public boolean hasEffects() {
        return !(effects == null || effects.isEmpty());
    }

    void safelyAddEffects(Iterable<?> collection) {
        if (collection == null || (collection instanceof Collection && ((Collection<?>) collection).isEmpty())) {
            return;
        }

        List<FireworkEffect> effects = this.effects;
        if (effects == null) {
            effects = this.effects = new ArrayList<>();
        }

        for (Object obj : collection) {
            if (obj instanceof FireworkEffect) {
                effects.add((FireworkEffect) obj);
            } else {
                throw new IllegalArgumentException(obj + " in " + collection + " is not a FireworkEffect");
            }
        }
    }

    @Override
    boolean applicableTo(Material type) {
        switch(type) {
            case FIREWORK_ROCKET:
                return true;
            default:
                return false;
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isFireworkEmpty();
    }

    boolean isFireworkEmpty() {
        return  !(hasEffects() || hasPower());
    }

    boolean hasPower() {
        return power != 0;
    }

    @Override
    boolean equalsCommon(TestMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }

        if (meta instanceof TestMetaFirework) {
            TestMetaFirework that = (TestMetaFirework) meta;

            return (hasPower() ? that.hasPower() && this.power == that.power : !that.hasPower())
                    && (hasEffects() ? that.hasEffects() && this.effects.equals(that.effects) : !that.hasEffects());
        }

        return true;
    }

    @Override
    boolean notUncommon(TestMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof TestMetaFirework || isFireworkEmpty());
    }

    @Override
    public TestMetaFirework clone() {
        TestMetaFirework meta = (TestMetaFirework) super.clone();

        if (this.effects != null) {
            meta.effects = new ArrayList<>(this.effects);
        }

        return meta;
    }

    public void addEffect(FireworkEffect effect) {
        Validate.notNull(effect, "Effect cannot be null");
        if (this.effects == null) {
            this.effects = new ArrayList<>();
        }
        this.effects.add(effect);
    }

    public void addEffects(FireworkEffect...effects) {
        Validate.notNull(effects, "Effects cannot be null");
        if (effects.length == 0) {
            return;
        }

        List<FireworkEffect> list = this.effects;
        if (list == null) {
            list = this.effects = new ArrayList<>();
        }

        for (FireworkEffect effect : effects) {
            Validate.notNull(effect, "Effect cannot be null");
            list.add(effect);
        }
    }

    public void addEffects(Iterable<FireworkEffect> effects) {
        Validate.notNull(effects, "Effects cannot be null");
        safelyAddEffects(effects);
    }

    public List<FireworkEffect> getEffects() {
        return this.effects == null ? ImmutableList.of() : ImmutableList.copyOf(this.effects);
    }

    public int getEffectsSize() {
        return this.effects == null ? 0 : this.effects.size();
    }

    public void removeEffect(int index) {
        if (this.effects == null) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
        } else {
            this.effects.remove(index);
        }
    }

    public void clearEffects() {
        this.effects = null;
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        Validate.isTrue(power >= 0, "Power cannot be less than zero: ", power);
        Validate.isTrue(power < 0x80, "Power cannot be more than 127: ", power);
        this.power = power;
    }
}
