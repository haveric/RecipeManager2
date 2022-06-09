package haveric.recipeManager;

import com.google.common.base.Preconditions;
import org.bukkit.Color;
import org.bukkit.Material;
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
        switch (material) {
            case AIR:
                return null;
            case WRITTEN_BOOK:
                return meta instanceof TestMetaBookSigned ? meta : new TestMetaBookSigned(meta);
            case WRITABLE_BOOK:
                return meta != null && meta.getClass().equals(TestMetaBook.class) ? meta : new TestMetaBook(meta);
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
            case LEATHER_HELMET:
            case LEATHER_HORSE_ARMOR:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return meta instanceof TestMetaLeatherArmor ? meta : new TestMetaLeatherArmor(meta);
            case POTION:
            case SPLASH_POTION:
            case LINGERING_POTION:
            case TIPPED_ARROW:
                return meta instanceof TestMetaPotion ? meta : new TestMetaPotion(meta);
            case FILLED_MAP:
                return meta instanceof TestMetaMap ? meta : new TestMetaMap(meta);
            case FIREWORK_ROCKET:
                return meta instanceof TestMetaFirework ? meta : new TestMetaFirework(meta);
            case FIREWORK_STAR:
                return meta instanceof TestMetaCharge ? meta : new TestMetaCharge(meta);
            case ENCHANTED_BOOK:
                return meta instanceof TestMetaEnchantedBook ? meta : new TestMetaEnchantedBook(meta);
            case BLACK_BANNER:
            case BLACK_WALL_BANNER:
            case BLUE_BANNER:
            case BLUE_WALL_BANNER:
            case BROWN_BANNER:
            case BROWN_WALL_BANNER:
            case CYAN_BANNER:
            case CYAN_WALL_BANNER:
            case GRAY_BANNER:
            case GRAY_WALL_BANNER:
            case GREEN_BANNER:
            case GREEN_WALL_BANNER:
            case LIGHT_BLUE_BANNER:
            case LIGHT_BLUE_WALL_BANNER:
            case LIGHT_GRAY_BANNER:
            case LIGHT_GRAY_WALL_BANNER:
            case LIME_BANNER:
            case LIME_WALL_BANNER:
            case MAGENTA_BANNER:
            case MAGENTA_WALL_BANNER:
            case ORANGE_BANNER:
            case ORANGE_WALL_BANNER:
            case PINK_BANNER:
            case PINK_WALL_BANNER:
            case PURPLE_BANNER:
            case PURPLE_WALL_BANNER:
            case RED_BANNER:
            case RED_WALL_BANNER:
            case WHITE_BANNER:
            case WHITE_WALL_BANNER:
            case YELLOW_BANNER:
            case YELLOW_WALL_BANNER:
                return meta instanceof TestMetaBanner ? meta : new TestMetaBanner(meta);
            case BAT_SPAWN_EGG:
            case BLAZE_SPAWN_EGG:
            case CAVE_SPIDER_SPAWN_EGG:
            case CHICKEN_SPAWN_EGG:
            case COD_SPAWN_EGG:
            case COW_SPAWN_EGG:
            case CREEPER_SPAWN_EGG:
            case DOLPHIN_SPAWN_EGG:
            case DROWNED_SPAWN_EGG:
            case DONKEY_SPAWN_EGG:
            case ELDER_GUARDIAN_SPAWN_EGG:
            case ENDERMAN_SPAWN_EGG:
            case ENDERMITE_SPAWN_EGG:
            case EVOKER_SPAWN_EGG:
            case GHAST_SPAWN_EGG:
            case GUARDIAN_SPAWN_EGG:
            case HOGLIN_SPAWN_EGG:
            case HORSE_SPAWN_EGG:
            case HUSK_SPAWN_EGG:
            case LLAMA_SPAWN_EGG:
            case MAGMA_CUBE_SPAWN_EGG:
            case MOOSHROOM_SPAWN_EGG:
            case MULE_SPAWN_EGG:
            case OCELOT_SPAWN_EGG:
            case PARROT_SPAWN_EGG:
            case PHANTOM_SPAWN_EGG:
            case PIGLIN_SPAWN_EGG:
            case PIG_SPAWN_EGG:
            case POLAR_BEAR_SPAWN_EGG:
            case PUFFERFISH_SPAWN_EGG:
            case RABBIT_SPAWN_EGG:
            case SALMON_SPAWN_EGG:
            case SHEEP_SPAWN_EGG:
            case SHULKER_SPAWN_EGG:
            case SILVERFISH_SPAWN_EGG:
            case SKELETON_HORSE_SPAWN_EGG:
            case SKELETON_SPAWN_EGG:
            case SLIME_SPAWN_EGG:
            case SPIDER_SPAWN_EGG:
            case SQUID_SPAWN_EGG:
            case STRAY_SPAWN_EGG:
            case STRIDER_SPAWN_EGG:
            case TROPICAL_FISH_SPAWN_EGG:
            case TURTLE_SPAWN_EGG:
            case VEX_SPAWN_EGG:
            case VILLAGER_SPAWN_EGG:
            case VINDICATOR_SPAWN_EGG:
            case WITCH_SPAWN_EGG:
            case WITHER_SKELETON_SPAWN_EGG:
            case WOLF_SPAWN_EGG:
            case ZOGLIN_SPAWN_EGG:
            case ZOMBIE_HORSE_SPAWN_EGG:
            case ZOMBIE_SPAWN_EGG:
            case ZOMBIE_VILLAGER_SPAWN_EGG:
            case ZOMBIFIED_PIGLIN_SPAWN_EGG:
                return meta instanceof TestMetaSpawnEgg ? meta : new TestMetaSpawnEgg(meta);
            case FURNACE:
            case CHEST:
            case TRAPPED_CHEST:
            case JUKEBOX:
            case DISPENSER:
            case DROPPER:
            case ACACIA_SIGN:
            case ACACIA_WALL_SIGN:
            case BIRCH_SIGN:
            case BIRCH_WALL_SIGN:
            case DARK_OAK_SIGN:
            case DARK_OAK_WALL_SIGN:
            case JUNGLE_SIGN:
            case JUNGLE_WALL_SIGN:
            case OAK_SIGN:
            case OAK_WALL_SIGN:
            case SPRUCE_SIGN:
            case SPRUCE_WALL_SIGN:
            case SPAWNER:
            case NOTE_BLOCK:
            case BREWING_STAND:
            case ENCHANTING_TABLE:
            case COMMAND_BLOCK:
            case REPEATING_COMMAND_BLOCK:
            case CHAIN_COMMAND_BLOCK:
            case BEACON:
            case DAYLIGHT_DETECTOR:
            case HOPPER:
            case COMPARATOR:
            case SHIELD:
            case STRUCTURE_BLOCK:
            case SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case BLACK_SHULKER_BOX:
            case ENDER_CHEST:
            case BARREL:
            case BELL:
            case BLAST_FURNACE:
            case CAMPFIRE:
            case JIGSAW:
            case LECTERN:
            case SMOKER:
                //return new CraftMetaBlockState(meta, material);
            /*
            case TROPICAL_FISH_BUCKET:
                return meta instanceof CraftMetaTropicalFishBucket ? meta : new CraftMetaTropicalFishBucket(meta);
            case CROSSBOW:
                return meta instanceof CraftMetaCrossbow ? meta : new CraftMetaCrossbow(meta);
            */
            default:
                return new TestMetaItem(meta);
        }
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
            throw new IllegalArgumentException("Meta of " + meta.getClass().toString() + " not created by " + TestItemFactory.class.getName());
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
    public Material updateMaterial(ItemMeta meta, Material material) throws IllegalArgumentException {
        return material;
    }
}
