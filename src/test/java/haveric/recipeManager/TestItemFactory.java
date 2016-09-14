package haveric.recipeManager;

import org.apache.commons.lang.Validate;
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
        Validate.notNull(material, "Material cannot be null");
        return getItemMeta(material, null);
    }

    private ItemMeta getItemMeta(Material material, TestMetaItem meta) {
        switch (material) {
            case AIR:
                return null;
            case WRITTEN_BOOK:
                return meta instanceof TestMetaBookSigned ? meta : new TestMetaBookSigned(meta);
            case BOOK_AND_QUILL:
                return meta != null && meta.getClass().equals(TestMetaBook.class) ? meta : new TestMetaBook(meta);
            case SKULL_ITEM:
                //return meta instanceof CraftMetaSkull ? meta : new CraftMetaSkull(meta);
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return meta instanceof TestMetaLeatherArmor ? meta : new TestMetaLeatherArmor(meta);
            case POTION:
            case SPLASH_POTION:
            case LINGERING_POTION:
            case TIPPED_ARROW:
                return meta instanceof TestMetaPotion ? meta : new TestMetaPotion(meta);
            case MAP:
                return meta instanceof TestMetaMap ? meta : new TestMetaMap(meta);
            case FIREWORK:
                return meta instanceof TestMetaFirework ? meta : new TestMetaFirework(meta);
            case FIREWORK_CHARGE:
                return meta instanceof TestMetaCharge ? meta : new TestMetaCharge(meta);
            case ENCHANTED_BOOK:
                return meta instanceof TestMetaEnchantedBook ? meta : new TestMetaEnchantedBook(meta);
            case BANNER:
                return meta instanceof TestMetaBanner ? meta : new TestMetaBanner(meta);
            case FURNACE:
            case CHEST:
            case TRAPPED_CHEST:
            case JUKEBOX:
            case DISPENSER:
            case DROPPER:
            case SIGN:
            case MOB_SPAWNER:
            case NOTE_BLOCK:
            case PISTON_BASE:
            case BREWING_STAND_ITEM:
            case ENCHANTMENT_TABLE:
            case COMMAND:
            case COMMAND_REPEATING:
            case COMMAND_CHAIN:
            case BEACON:
            case DAYLIGHT_DETECTOR:
            case DAYLIGHT_DETECTOR_INVERTED:
            case HOPPER:
            case REDSTONE_COMPARATOR:
            case FLOWER_POT_ITEM:
            case SHIELD:
                //return new CraftMetaBlockState(meta, material);
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
        Validate.notNull(stack, "Stack cannot be null");
        return asMetaFor(meta, stack.getType());
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, Material material) throws IllegalArgumentException {
        Validate.notNull(material, "Material cannot be null");
        if (!(meta instanceof TestMetaItem)) {
            throw new IllegalArgumentException("Meta of " + (meta != null ? meta.getClass().toString() : "null") + " not created by " + TestItemFactory.class.getName());
        }
        return getItemMeta(material, (TestMetaItem) meta);
    }

    @Override
    public Color getDefaultLeatherColor() {
        return Color.fromRGB(160, 101, 64);
    }
}
