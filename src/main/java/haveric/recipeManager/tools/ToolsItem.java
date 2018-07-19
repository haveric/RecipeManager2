package haveric.recipeManager.tools;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Settings;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.RMCChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ToolsItem {
    public static ItemResult create(Material type, int data, int amount, String name, String... lore) {
        List<String> loreArray;

        if (lore != null && lore.length > 0) {
            loreArray = Arrays.asList(lore);
        } else {
            loreArray = null;
        }

        return create(type, data, amount, name, loreArray);
    }

    public static ItemResult create(Material type, int data, int amount, String name, List<String> lore) {
        ItemResult item = new ItemResult(type, amount, (short) data, 100);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        if (lore != null) {
            meta.setLore(lore);
        }

        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Displays the ItemStack in a user-friendly and colorful manner.<br>
     * If item is null or air it will print "nothing" in gray.<br>
     * If item is enchanted it will have aqua color instead of white.<br>
     * Uses aliases to display data values as well.<br>
     * Uses item's display name in italic font if available.<br> <br>
     * NOTE: Will have a RESET color at the end
     *
     * @param item
     *            the item to print, can be null
     * @return user-friendly item print
     */
    public static String print(ItemStack item) {
        return print(item, RMCChatColor.WHITE, RMCChatColor.RESET);
    }

    /**
     * Displays the ItemStack in a user-friendly and colorful manner.<br>
     * If item is null or air it will print "nothing" in gray.<br>
     * If item is enchanted it will have aqua color instead of white.<br>
     * Uses aliases to display data values as well.<br>
     * Uses item's display name in italic font if available.
     *
     * @param item
     *            the item to print, can be null
     * @param defColor
     *            default color, usually white
     * @param endColor
     *            will be appended at the end of string, should be your text color
     * @return user-friendly item print
     */
    public static String print(ItemStack item, RMCChatColor defColor, RMCChatColor endColor) {
        if (item == null || item.getType() == Material.AIR) {
            return RMCChatColor.GRAY + "(nothing)";
        }

        String name;
        String itemData = null;

        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            name = RMCChatColor.ITALIC + meta.getDisplayName();
        } else {
            name = Settings.getInstance().getMaterialPrint(item.getType());

            if (name == null) {
                name = Tools.parseAliasPrint(item.getType().toString());
            }
        }

        Map<Short, String> dataMap = Settings.getInstance().getMaterialDataPrint(item.getType());

        if (dataMap != null) {
            itemData = dataMap.get(item.getDurability());

            if (itemData != null) {
                itemData = itemData + " " + name;
            }
        }

        if (itemData == null) {
            short data = item.getDurability();

            if (data == 0) {
                itemData = name;
            } else {
                if (data == Vanilla.DATA_WILDCARD) {
                    itemData = name + RMCChatColor.GRAY;

                    if (item.getType().getMaxDurability() > 0) {
                        itemData +=  ":" + Messages.getInstance().parse("item.anydata");
                    }
                } else {
                    itemData = name + RMCChatColor.GRAY + ":" + data;
                }
            }
        }

        String amount = "";
        if (item.getAmount() > 1) {
            amount = item.getAmount() + "x ";
        }

        RMCChatColor color;
        if (item.getEnchantments().size() > 0) {
            color = RMCChatColor.AQUA;
        } else {
            color = defColor;
        }

        String endTextColor = "";
        if (endColor != null) {
            endTextColor += endColor;
        }
        return amount + color + itemData + endTextColor;
    }

    public static String getName(ItemStack item) {
        String name;
        String itemData = null;

        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            name = RMCChatColor.ITALIC + meta.getDisplayName();
        } else {
            name = Settings.getInstance().getMaterialPrint(item.getType());

            if (name == null) {
                name = Tools.parseAliasPrint(item.getType().toString());
            }
        }

        Map<Short, String> dataMap = Settings.getInstance().getMaterialDataPrint(item.getType());

        if (dataMap != null) {
            itemData = dataMap.get(item.getDurability());

            if (itemData != null) {
                itemData = itemData + " " + name;
            }
        }

        String enchantsName = "";
        if (item.getEnchantments().size() > 0) {
            enchantsName += RMCChatColor.AQUA;
        }
        if (itemData == null) {
            enchantsName += name;
        } else {
            enchantsName += itemData;
        }
        return enchantsName;
    }

    public static boolean isSimilarDataWildcard(ItemStack source, ItemStack item) {
        boolean isSimilar = false;

        if (item != null) {
            if (item.equals(source)) {
                isSimilar = true;
            } else {
                if (source.getType() == item.getType()) {
                    if (source.getDurability() == Vanilla.DATA_WILDCARD || source.getDurability() == item.getDurability()) {
                        if (source.hasItemMeta() == item.hasItemMeta()) {
                            if (source.hasItemMeta()) {
                                isSimilar = Bukkit.getItemFactory().equals(source.getItemMeta(), item.getItemMeta());
                            } else {
                                isSimilar = true;
                            }
                        }
                    }
                }
            }
        }

        return isSimilar;
    }

    public static ItemStack nullIfAir(ItemStack item) {
        ItemStack nullIfAir;

        if (item == null || item.getType() == Material.AIR) {
            nullIfAir = null;
        } else {
            nullIfAir = item;
        }

        return nullIfAir;
    }

    public static ItemStack merge(ItemStack into, ItemStack item) {
        if (into == null || into.getType() == Material.AIR) {
            return item;
        }

        if (ToolsItem.isSameItem(into, item, true) && item.getAmount() <= (into.getMaxStackSize() - into.getAmount())) {
            ItemStack clone = item.clone();

            clone.setAmount(into.getAmount() + item.getAmount());

            return clone;
        }

        return null;
    }

    public static boolean canMerge(ItemStack intoItem, ItemStack item) {
        return intoItem == null || intoItem.getType() == Material.AIR || ToolsItem.isSameItem(intoItem, item, true) && item.getAmount() <= (intoItem.getMaxStackSize() - intoItem.getAmount());
    }

    public static boolean isSameItem(ItemStack one, ItemStack two, boolean negativeDurAllowed) {
        boolean same = false;

        if (one != null && two != null) {
            boolean sameType = one.getType() == two.getType();
            boolean sameDur = one.getDurability() == two.getDurability();
            boolean negativeDur = (one.getDurability() == Short.MAX_VALUE) || (two.getDurability() == Short.MAX_VALUE);

            boolean sameEnchant = false;
            boolean noEnchant = one.getEnchantments() == null && two.getEnchantments() == null;
            if (!noEnchant) {
                sameEnchant = one.getEnchantments().equals(two.getEnchantments());
            }

            boolean sameMeta = false;
            boolean noMeta = one.getItemMeta() == null && two.getItemMeta() == null;

            if (!noMeta) {
                // Handles an empty slot being compared
                if (one.getItemMeta() == null || two.getItemMeta() == null) {
                    sameMeta = false;
                } else {
                    sameMeta = one.getItemMeta().equals(two.getItemMeta());
                }
            }

            if (sameType && (sameDur || (negativeDurAllowed && negativeDur)) && (sameEnchant || noEnchant) && (sameMeta || noMeta)) {
                same = true;
            }
        }
        return same;
    }

    public static void replaceItem(final Inventory inventory, final int slot, final ItemStack stack) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(RecipeManager.getPlugin(), new Runnable() {
            public void run() {
                ItemStack slotItem = inventory.getItem(slot);

                // Sanity check to make sure the new item is different;
                if ((stack != null && slotItem != null && stack.getAmount() != slotItem.getAmount()) || !isSameItem(stack, slotItem, false)) {
                    inventory.setItem(slot, stack);
                }
            }
        });
    }

    public static void updateFurnaceCookTimeDelayed(final Furnace furnace, final short time) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(RecipeManager.getPlugin(), new Runnable() {
            public void run() {
                // Re-get the furnace to make sure we are only updating the cook time state. Probably should be passing the block in instead.
                Block block = furnace.getBlock();
                Furnace updatedFurnace = (Furnace) block.getState();
                updatedFurnace.setCookTime(time);
                updatedFurnace.update();
            }
        });
    }

    public static boolean isShulkerBox(Material material) {
        boolean isShulkerBox;

        switch(material) {
            case BLACK_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
                isShulkerBox = true;
                break;
            default:
                isShulkerBox = false;
                break;
        }

        Material grayShulkerMaterial;
        if (Version.has1_13Support()) {
            grayShulkerMaterial = Material.LIGHT_GRAY_SHULKER_BOX;
        } else {
            grayShulkerMaterial = Material.getMaterial("SILVER_SHULKER_BOX");
        }
        if (material == grayShulkerMaterial) {
            isShulkerBox = true;
        }

        return isShulkerBox;
    }
}