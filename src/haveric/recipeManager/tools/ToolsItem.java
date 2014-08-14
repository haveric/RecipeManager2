package haveric.recipeManager.tools;

import haveric.recipeManager.Messages;
import haveric.recipeManager.RecipeManager;
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
        return create(type, data, amount, name, (lore != null && lore.length > 0 ? Arrays.asList(lore) : null));
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
            name = RecipeManager.getSettings().materialPrint.get(item.getType());

            if (name == null) {
                name = Tools.parseAliasPrint(item.getType().toString());
            }
        }

        Map<Short, String> dataMap = RecipeManager.getSettings().materialDataPrint.get(item.getType());

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

        String amount = (alwaysShowAmount || item.getAmount() > 1 ? item.getAmount() + "x " : "");
        ChatColor color = (item.getEnchantments().size() > 0 ? ChatColor.AQUA : defColor);

        return amount + color + itemData + (endColor == null ? "" : endColor);
    }

    public static String getName(ItemStack item) {
        String name = null;
        String itemData = null;

        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            name = ChatColor.ITALIC + meta.getDisplayName();
        } else {
            name = RecipeManager.getSettings().materialPrint.get(item.getType());

            if (name == null) {
                name = Tools.parseAliasPrint(item.getType().toString());
            }
        }

        Map<Short, String> dataMap = RecipeManager.getSettings().materialDataPrint.get(item.getType());

        if (dataMap != null) {
            itemData = dataMap.get(item.getDurability());

            if (itemData != null) {
                itemData = itemData + " " + name;
            }
        }

        return (item.getEnchantments().size() > 0 ? ChatColor.AQUA : "") + (itemData == null ? name : itemData);
    }

    public static boolean isSimilarDataWildcard(ItemStack source, ItemStack item) {
        if (item == null) {
            return false;
        }

        if (item == source) {
            return true;
        }

        return source.getTypeId() == item.getTypeId() && (source.getDurability() == Vanilla.DATA_WILDCARD ? true : source.getDurability() == item.getDurability()) && source.hasItemMeta() == item.hasItemMeta() && (source.hasItemMeta() ? Bukkit.getItemFactory().equals(source.getItemMeta(), item.getItemMeta()) : true);
    }

    public static ItemStack nullIfAir(ItemStack item) {
        return (item == null || item.getType() == Material.AIR ? null : item);
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