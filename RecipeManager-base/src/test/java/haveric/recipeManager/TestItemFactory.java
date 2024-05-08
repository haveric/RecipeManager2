package haveric.recipeManager;

import com.google.common.base.Preconditions;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TestItemFactory implements ItemFactory {
    public static final Color DEFAULT_LEATHER_COLOR = Color.fromRGB(0xA06540);

    public TestItemFactory() { }

    @Override
    public ItemMeta getItemMeta(Material material) {
        Preconditions.checkNotNull(material, "Material cannot be null");
        return getItemMeta(material, null);
    }

    private ItemMeta getItemMeta(Material material, TestMetaItem meta) {
        return switch (material) {
            case AIR -> null;
            case WRITTEN_BOOK -> meta instanceof TestMetaBookSigned ? meta : new TestMetaBookSigned(meta);
            case WRITABLE_BOOK ->
                    meta != null && meta.getClass().equals(TestMetaBook.class) ? meta : new TestMetaBook(meta);
            /*
            case CREEPER_HEAD:
            case CREEPER_WALL_HEAD:
            case DRAGON_HEAD:
            case DRAGON_WALL_HEAD:
            case PISTON_HEAD:
            case PLAYER_HEAD:
            case PLAYER_WALL_HEAD:
            case SKELETON_SKULL:
            case SKELETON_WALL_SKULL:
            case WITHER_SKELETON_SKULL:
            case WITHER_SKELETON_WALL_SKULL:
            case ZOMBIE_HEAD:
            case ZOMBIE_WALL_HEAD:
            */
            //return meta instanceof CraftMetaSkull ? meta : new CraftMetaSkull(meta);
            case LEATHER_HELMET, LEATHER_HORSE_ARMOR, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS ->
                    meta instanceof TestMetaLeatherArmor ? meta : new TestMetaLeatherArmor(meta);
            case POTION, SPLASH_POTION, LINGERING_POTION, TIPPED_ARROW ->
                    meta instanceof TestMetaPotion ? meta : new TestMetaPotion(meta);
            case FILLED_MAP -> meta instanceof TestMetaMap ? meta : new TestMetaMap(meta);
            case FIREWORK_ROCKET -> meta instanceof TestMetaFirework ? meta : new TestMetaFirework(meta);
            case FIREWORK_STAR -> meta instanceof TestMetaCharge ? meta : new TestMetaCharge(meta);
            case ENCHANTED_BOOK -> meta instanceof TestMetaEnchantedBook ? meta : new TestMetaEnchantedBook(meta);
            case BLACK_BANNER, BLACK_WALL_BANNER, BLUE_BANNER, BLUE_WALL_BANNER, BROWN_BANNER, BROWN_WALL_BANNER,
                 CYAN_BANNER, CYAN_WALL_BANNER, GRAY_BANNER, GRAY_WALL_BANNER, GREEN_BANNER, GREEN_WALL_BANNER,
                 LIGHT_BLUE_BANNER, LIGHT_BLUE_WALL_BANNER, LIGHT_GRAY_BANNER, LIGHT_GRAY_WALL_BANNER, LIME_BANNER,
                 LIME_WALL_BANNER, MAGENTA_BANNER, MAGENTA_WALL_BANNER, ORANGE_BANNER, ORANGE_WALL_BANNER, PINK_BANNER,
                 PINK_WALL_BANNER, PURPLE_BANNER, PURPLE_WALL_BANNER, RED_BANNER, RED_WALL_BANNER, WHITE_BANNER,
                 WHITE_WALL_BANNER, YELLOW_BANNER, YELLOW_WALL_BANNER ->
                    meta instanceof TestMetaBanner ? meta : new TestMetaBanner(meta);
            case BAT_SPAWN_EGG, BLAZE_SPAWN_EGG, CAVE_SPIDER_SPAWN_EGG, CHICKEN_SPAWN_EGG, COD_SPAWN_EGG, COW_SPAWN_EGG,
                 CREEPER_SPAWN_EGG, DOLPHIN_SPAWN_EGG, DROWNED_SPAWN_EGG, DONKEY_SPAWN_EGG, ELDER_GUARDIAN_SPAWN_EGG,
                 ENDERMAN_SPAWN_EGG, ENDERMITE_SPAWN_EGG, EVOKER_SPAWN_EGG, GHAST_SPAWN_EGG, GUARDIAN_SPAWN_EGG,
                 HOGLIN_SPAWN_EGG, HORSE_SPAWN_EGG, HUSK_SPAWN_EGG, LLAMA_SPAWN_EGG, MAGMA_CUBE_SPAWN_EGG,
                 MOOSHROOM_SPAWN_EGG, MULE_SPAWN_EGG, OCELOT_SPAWN_EGG, PARROT_SPAWN_EGG, PHANTOM_SPAWN_EGG,
                 PIGLIN_SPAWN_EGG, PIG_SPAWN_EGG, POLAR_BEAR_SPAWN_EGG, PUFFERFISH_SPAWN_EGG, RABBIT_SPAWN_EGG,
                 SALMON_SPAWN_EGG, SHEEP_SPAWN_EGG, SHULKER_SPAWN_EGG, SILVERFISH_SPAWN_EGG, SKELETON_HORSE_SPAWN_EGG,
                 SKELETON_SPAWN_EGG, SLIME_SPAWN_EGG, SPIDER_SPAWN_EGG, SQUID_SPAWN_EGG, STRAY_SPAWN_EGG,
                 STRIDER_SPAWN_EGG, TROPICAL_FISH_SPAWN_EGG, TURTLE_SPAWN_EGG, VEX_SPAWN_EGG, VILLAGER_SPAWN_EGG,
                 VINDICATOR_SPAWN_EGG, WITCH_SPAWN_EGG, WITHER_SKELETON_SPAWN_EGG, WOLF_SPAWN_EGG, ZOGLIN_SPAWN_EGG,
                 ZOMBIE_HORSE_SPAWN_EGG, ZOMBIE_SPAWN_EGG, ZOMBIE_VILLAGER_SPAWN_EGG, ZOMBIFIED_PIGLIN_SPAWN_EGG ->
                    meta instanceof TestMetaSpawnEgg ? meta : new TestMetaSpawnEgg(meta);
            //return new CraftMetaBlockState(meta, material);
            /*
            case TROPICAL_FISH_BUCKET:
                return meta instanceof CraftMetaTropicalFishBucket ? meta : new CraftMetaTropicalFishBucket(meta);
            case CROSSBOW:
                return meta instanceof CraftMetaCrossbow ? meta : new CraftMetaCrossbow(meta);
            */
            default -> new TestMetaItem(meta);
        };
    }

    @Override
    public boolean isApplicable(ItemMeta meta, ItemStack stack) throws IllegalArgumentException {
        if (stack == null) {
            return false;
        }
        return isApplicable(meta, stack.getType());
    }

    @Override
    public boolean isApplicable(ItemMeta meta, Material material) throws IllegalArgumentException {
        if (material == null || meta == null) {
            return false;
        }
        if (!(meta instanceof TestMetaItem)) {
            throw new IllegalArgumentException("Meta of " + meta.getClass() + " not created by " + TestItemFactory.class.getName());
        }

        return ((TestMetaItem) meta).applicableTo(material);
    }

    @Override
    public boolean equals(ItemMeta meta1, ItemMeta meta2) throws IllegalArgumentException {
        if (meta1 == meta2) {
            return true;
        }
        if (meta1 != null && !(meta1 instanceof TestMetaItem)) {
            throw new IllegalArgumentException("First meta of " + meta1.getClass().getName() + " does not belong to " + TestItemFactory.class.getName());
        }
        if (meta2 != null && !(meta2 instanceof TestMetaItem)) {
            throw new IllegalArgumentException("Second meta " + meta2.getClass().getName() + " does not belong to " + TestItemFactory.class.getName());
        }
        if (meta1 == null) {
            return ((TestMetaItem) meta2).isEmpty();
        }
        if (meta2 == null) {
            return ((TestMetaItem) meta1).isEmpty();
        }

        return equals((TestMetaItem) meta1, (TestMetaItem) meta2);
    }

    private boolean equals(TestMetaItem meta1, TestMetaItem meta2) {
        return meta1.equalsCommon(meta2) && meta1.notUncommon(meta2) && meta2.notUncommon(meta1);
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, ItemStack stack) throws IllegalArgumentException {
        Preconditions.checkNotNull(stack, "Stack cannot be null");
        return asMetaFor(meta, stack.getType());
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, Material material) throws IllegalArgumentException {
        Preconditions.checkNotNull(material, "Material cannot be null");
        if (!(meta instanceof TestMetaItem)) {
            throw new IllegalArgumentException("Meta of " + (meta != null ? meta.getClass().toString() : "null") + " not created by " + TestItemFactory.class.getName());
        }

        return getItemMeta(material, (TestMetaItem) meta);
    }

    @Override
    public Color getDefaultLeatherColor() {
        return Color.fromRGB(160, 101, 64);
    }

    @Override
    public ItemStack createItemStack(String input) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Material getSpawnEgg(EntityType type) {
        return null;
    }

    @Override
    public ItemStack enchantItem(Entity entity, ItemStack itemStack, int i, boolean b) {
        return null;
    }

    @Override
    public ItemStack enchantItem(World world, ItemStack itemStack, int i, boolean b) {
        return null;
    }

    @Override
    public ItemStack enchantItem(ItemStack itemStack, int i, boolean b) {
        return null;
    }
}
