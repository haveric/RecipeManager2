package haveric.recipeManager.tools;

import haveric.recipeManager.Messages;
import haveric.recipeManager.Settings;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.recipes.ItemResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
     * NOTE: Will have a RESET color at the end, use {@link #print(ItemStack, ChatColor)} to use a different end-color instead.
     *
     * @param item
     *            the item to print, can be null
     * @return user-friendly item print
     */
    public static String print(ItemStack item) {
        return print(item, ChatColor.WHITE, ChatColor.RESET, false);
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
    public static String print(ItemStack item, ChatColor defColor, ChatColor endColor, boolean alwaysShowAmount) {
        if (item == null || item.getType() == Material.AIR) {
            return ChatColor.GRAY + "(nothing)";
        }

        String name = null;
        String itemData = null;

        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            name = ChatColor.ITALIC + meta.getDisplayName();
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

            if (data != 0) {
                if (data == Vanilla.DATA_WILDCARD) {
                    itemData = name + ChatColor.GRAY + ":" + Messages.ITEM_ANYDATA.get();
                } else {
                    itemData = name + ChatColor.GRAY + ":" + data;
                }
            } else {
                itemData = name;
            }
        }

        String amount = "";
        if (alwaysShowAmount || item.getAmount() > 1) {
            amount = item.getAmount() + "x ";
        }

        ChatColor color;
        if (item.getEnchantments().size() > 0) {
            color = ChatColor.AQUA;
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
        String name = null;
        String itemData = null;

        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            name = ChatColor.ITALIC + meta.getDisplayName();
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
            enchantsName += ChatColor.AQUA;
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
            if (item == source) {
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

        if (item.isSimilar(into) && item.getAmount() <= (into.getMaxStackSize() - into.getAmount())) {
            ItemStack clone = item.clone();

            clone.setAmount(into.getAmount() + item.getAmount());

            return clone;
        }

        return null;
    }

    public static boolean canMerge(ItemStack intoItem, ItemStack item) {
        if (intoItem == null || intoItem.getType() == Material.AIR) {
            return true;
        }

        if (intoItem.isSimilar(item) && item.getAmount() <= (intoItem.getMaxStackSize() - intoItem.getAmount())) {
            return true;
        }

        return false;
    }
}