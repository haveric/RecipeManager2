package haveric.recipeManager;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
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

        if (!(meta instanceof TestMetaFirework that)) {
            return;
        }

        this.power = that.power;

        if (that.hasEffects()) {
            this.effects = new ArrayList<>(that.effects);
        }
    }

    static int getNBT(Type type) {
        return switch (type) {
            case BALL -> 0;
            case BALL_LARGE -> 1;
            case STAR -> 2;
            case CREEPER -> 3;
            case BURST -> 4;
            default -> throw new AssertionError(type);
        };
    }

    static Type getEffectType(int nbt) {
        return switch (nbt) {
            case 0 -> Type.BALL;
            case 1 -> Type.BALL_LARGE;
            case 2 -> Type.STAR;
            case 3 -> Type.CREEPER;
            case 4 -> Type.BURST;
            default -> throw new AssertionError(nbt);
        };
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
        return type == Material.FIREWORK_ROCKET;
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isFireworkEmpty();
    }

    boolean isFireworkEmpty() {
        return  !(hasEffects() || hasPower());
    }

    @Override
    public boolean hasPower() {
        return power != 0;
    }

    @Override
    boolean equalsCommon(TestMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }

        if (meta instanceof TestMetaFirework that) {
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
        Preconditions.checkNotNull(effect, "Effect cannot be null");
        if (this.effects == null) {
            this.effects = new ArrayList<>();
        }
        this.effects.add(effect);
    }

    public void addEffects(FireworkEffect...effects) {
        Preconditions.checkNotNull(effects, "Effects cannot be null");
        if (effects.length == 0) {
            return;
        }

        List<FireworkEffect> list = this.effects;
        if (list == null) {
            list = this.effects = new ArrayList<>();
        }

        for (FireworkEffect effect : effects) {
            Preconditions.checkNotNull(effect, "Effect cannot be null");
            list.add(effect);
        }
    }

    public void addEffects(Iterable<FireworkEffect> effects) {
        Preconditions.checkNotNull(effects, "Effects cannot be null");
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
        Preconditions.checkArgument(power >= 0, "Power cannot be less than zero: ", power);
        Preconditions.checkArgument(power < 0x80, "Power cannot be more than 127: ", power);
        this.power = power;
    }
}
