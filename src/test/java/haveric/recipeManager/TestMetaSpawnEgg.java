package haveric.recipeManager;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.SpawnEggMeta;

public class TestMetaSpawnEgg extends TestMetaItem implements SpawnEggMeta {
    private EntityType spawnedType;

    TestMetaSpawnEgg(TestMetaItem meta) {
        super(meta);

        if (!(meta instanceof TestMetaSpawnEgg)) {
            return;
        }

        TestMetaSpawnEgg egg = (TestMetaSpawnEgg) meta;
        this.spawnedType = egg.spawnedType;
    }

    @Override
    boolean applicableTo(Material type) {
        switch (type) {
            case MONSTER_EGG:
                return true;
            default:
                return false;
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isSpawnEggEmpty();
    }

    boolean isSpawnEggEmpty() {
        return !(hasSpawnedType());
    }

    boolean hasSpawnedType() {
        return spawnedType != null;
    }

    @Override
    public EntityType getSpawnedType() {
        return spawnedType;
    }

    @Override
    public void setSpawnedType(EntityType type) {
        Preconditions.checkArgument(type == null || type.getName() != null, "Spawn egg type must have name (%s)", type);

        this.spawnedType = type;
    }

    @Override
    boolean equalsCommon(TestMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof TestMetaSpawnEgg) {
            TestMetaSpawnEgg that = (TestMetaSpawnEgg) meta;

            return hasSpawnedType() ? that.hasSpawnedType() && this.spawnedType.equals(that.spawnedType) : !that.hasSpawnedType();
        }
        return true;
    }

    @Override
    boolean notUncommon(TestMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof TestMetaSpawnEgg || isSpawnEggEmpty());
    }

    @Override
    public TestMetaSpawnEgg clone() {
        TestMetaSpawnEgg clone = (TestMetaSpawnEgg) super.clone();

        clone.spawnedType = spawnedType;

        return clone;
    }
}
