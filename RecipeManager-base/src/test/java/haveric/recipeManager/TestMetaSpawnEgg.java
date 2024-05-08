package haveric.recipeManager;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.SpawnEggMeta;

public class TestMetaSpawnEgg extends TestMetaItem implements SpawnEggMeta {
    private EntityType spawnedType;

    TestMetaSpawnEgg(TestMetaItem meta) {
        super(meta);

        if (!(meta instanceof TestMetaSpawnEgg egg)) {
            return;
        }

        this.spawnedType = egg.spawnedType;
    }

    @Override
    boolean applicableTo(Material type) {
        return switch (type) {
            case BAT_SPAWN_EGG, BEE_SPAWN_EGG, BLAZE_SPAWN_EGG, CAT_SPAWN_EGG, CAVE_SPIDER_SPAWN_EGG, CHICKEN_SPAWN_EGG,
                 COD_SPAWN_EGG, COW_SPAWN_EGG, CREEPER_SPAWN_EGG, DOLPHIN_SPAWN_EGG, DONKEY_SPAWN_EGG,
                 DROWNED_SPAWN_EGG, ELDER_GUARDIAN_SPAWN_EGG, ENDERMAN_SPAWN_EGG, ENDERMITE_SPAWN_EGG, EVOKER_SPAWN_EGG,
                 FOX_SPAWN_EGG, GHAST_SPAWN_EGG, GUARDIAN_SPAWN_EGG, HOGLIN_SPAWN_EGG, HORSE_SPAWN_EGG, HUSK_SPAWN_EGG,
                 LLAMA_SPAWN_EGG, MAGMA_CUBE_SPAWN_EGG, MOOSHROOM_SPAWN_EGG, MULE_SPAWN_EGG, OCELOT_SPAWN_EGG,
                 PANDA_SPAWN_EGG, PARROT_SPAWN_EGG, PHANTOM_SPAWN_EGG, PIGLIN_SPAWN_EGG, PIG_SPAWN_EGG,
                 PILLAGER_SPAWN_EGG, POLAR_BEAR_SPAWN_EGG, PUFFERFISH_SPAWN_EGG, RABBIT_SPAWN_EGG, RAVAGER_SPAWN_EGG,
                 SALMON_SPAWN_EGG, SHEEP_SPAWN_EGG, SHULKER_SPAWN_EGG, SILVERFISH_SPAWN_EGG, SKELETON_HORSE_SPAWN_EGG,
                 SKELETON_SPAWN_EGG, SLIME_SPAWN_EGG, SPIDER_SPAWN_EGG, SQUID_SPAWN_EGG, STRAY_SPAWN_EGG,
                 STRIDER_SPAWN_EGG, TRADER_LLAMA_SPAWN_EGG, TROPICAL_FISH_SPAWN_EGG, TURTLE_SPAWN_EGG, VEX_SPAWN_EGG,
                 VILLAGER_SPAWN_EGG, VINDICATOR_SPAWN_EGG, WANDERING_TRADER_SPAWN_EGG, WITCH_SPAWN_EGG,
                 WITHER_SKELETON_SPAWN_EGG, WOLF_SPAWN_EGG, ZOGLIN_SPAWN_EGG, ZOMBIE_HORSE_SPAWN_EGG, ZOMBIE_SPAWN_EGG,
                 ZOMBIE_VILLAGER_SPAWN_EGG, ZOMBIFIED_PIGLIN_SPAWN_EGG -> true;
            default -> false;
        };
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
    public EntitySnapshot getSpawnedEntity() {
        return null;
    }

    @Override
    public void setSpawnedEntity(EntitySnapshot entitySnapshot) {

    }

    @Override
    boolean equalsCommon(TestMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof TestMetaSpawnEgg that) {
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
