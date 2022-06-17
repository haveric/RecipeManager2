package haveric.recipeManager.tools;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.ItemResult;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

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
            return RMCChatColor.GRAY + "(air)";
        }

        String name;
        String itemData = null;

        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            name = RMCChatColor.ITALIC + meta.getDisplayName();
        } else {
            name = RecipeManager.getSettings().getMaterialPrint(item.getType());

            if (name == null) {
                name = Tools.parseAliasPrint(item.getType().toString());
            }
        }

        Map<Short, String> dataMap = RecipeManager.getSettings().getMaterialDataPrint(item.getType());

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
                if (data == RMCVanilla.DATA_WILDCARD) {
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
            name = RecipeManager.getSettings().getMaterialPrint(item.getType());

            if (name == null) {
                name = Tools.parseAliasPrint(item.getType().toString());
            }
        }

        Map<Short, String> dataMap = RecipeManager.getSettings().getMaterialDataPrint(item.getType());

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

    public static boolean isSameItemHash(ItemStack one, ItemStack two) {
        boolean match = one == null && two == null;

        if (!match && one != null && two != null) {
            match = one.getType() == two.getType();

            if (match) {
                int oneHash;
                int twoHash;
                if (one.getAmount() == 1) {
                    oneHash = one.hashCode();
                } else {
                    ItemStack oneClone = one.clone();
                    oneClone.setAmount(1);
                    oneHash = oneClone.hashCode();
                }

                if (two.getAmount() == 1) {
                    twoHash = two.hashCode();
                } else {
                    ItemStack twoClone = two.clone();
                    twoClone.setAmount(1);
                    twoHash = twoClone.hashCode();
                }

                match = oneHash == twoHash;
            }
        } else if (one == null && two != null && two.getType() == Material.AIR) {
            match = true;
        } else if (two == null && one != null && one.getType() == Material.AIR) {
            match = true;
        }

        return match;
    }

    public static int getFireworkEffectQuality(FireworkEffect itemEffect, FireworkEffect ingredientEffect) {
        int quality = 0;
        if (itemEffect.getType() == ingredientEffect.getType()) {
            quality ++;
        }

        if (itemEffect.hasFlicker() == ingredientEffect.hasFlicker()) {
            quality ++;
        }

        if (itemEffect.hasTrail() == ingredientEffect.hasTrail()) {
            quality ++;
        }

        List<Color> itemColors = itemEffect.getColors();
        List<Color> ingredientColors = ingredientEffect.getColors();

        if (itemColors.isEmpty() && ingredientColors.isEmpty()) {
            quality ++;
        } else if (itemColors.size() == ingredientColors.size()) {
            quality ++;

            for (int i = 0; i < itemColors.size(); i++) {
                if (itemColors.get(i).equals(ingredientColors.get(i))) {
                    quality ++;
                }
            }
        }

        List<Color> itemFadeColors = itemEffect.getFadeColors();
        List<Color> ingredientFadeColors = ingredientEffect.getFadeColors();

        if (itemFadeColors.isEmpty() && ingredientFadeColors.isEmpty()) {
            quality ++;
        } else if (itemFadeColors.size() == ingredientFadeColors.size()) {
            quality ++;

            for (int i = 0; i < itemFadeColors.size(); i++) {
                if (itemFadeColors.get(i).equals(ingredientFadeColors.get(i))) {
                    quality ++;
                }
            }
        }

        return quality;
    }

    public static int getPotionEffectsQuality(List<PotionEffect> itemPotionEffects, List<PotionEffect> ingredientPotionEffects) {
        int quality = 0;
        if (itemPotionEffects.size() == ingredientPotionEffects.size()) {
            quality ++;

            for (int i = 0; i < itemPotionEffects.size(); i++) {
                PotionEffect itemPotionEffect = itemPotionEffects.get(i);
                PotionEffect ingredientPotionEffect = ingredientPotionEffects.get(i);

                if (itemPotionEffect.getType() == ingredientPotionEffect.getType()) {
                    quality ++;
                }

                if (itemPotionEffect.getDuration() == ingredientPotionEffect.getDuration()) {
                    quality ++;
                }

                if (itemPotionEffect.getAmplifier() == ingredientPotionEffect.getAmplifier()) {
                    quality ++;
                }

                if (itemPotionEffect.hasParticles() == ingredientPotionEffect.hasParticles()) {
                    quality ++;
                }

                if (itemPotionEffect.hasIcon() == ingredientPotionEffect.hasIcon()) {
                    quality ++;
                }
            }
        }

        return quality;
    }

    public static void replaceItem(final Inventory inventory, final int slot, final ItemStack stack) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(RecipeManager.getPlugin(), () -> {
            ItemStack slotItem = inventory.getItem(slot);

            // Sanity check to make sure the new item is different;
            if ((stack != null && slotItem != null && stack.getAmount() != slotItem.getAmount()) || !isSameItem(stack, slotItem, false)) {
                inventory.setItem(slot, stack);
            }
        });
    }

    public static void updateFurnaceCookTimeDelayed(final Furnace furnace, final short time) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(RecipeManager.getPlugin(), () -> {
            // Re-get the furnace to make sure we are only updating the cook time state. Probably should be passing the block in instead.
            Block block = furnace.getBlock();
            Furnace updatedFurnace = (Furnace) block.getState();
            if (Version.has1_14Support()) {
                updatedFurnace.setCookTimeTotal(time);
            } else {
                updatedFurnace.setCookTime(time);
            }


            updatedFurnace.update();
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
        if (Version.has1_13BasicSupport()) {
            grayShulkerMaterial = Material.LIGHT_GRAY_SHULKER_BOX;
        } else {
            grayShulkerMaterial = Material.getMaterial("SILVER_SHULKER_BOX");
        }
        if (material == grayShulkerMaterial) {
            isShulkerBox = true;
        }

        return isShulkerBox;
    }

    /**
     * This is used for Early 1.15 or below to replicate the method of the same name in Material added in late 1.15
     *
     * Determines the remaining item in a crafting grid after crafting with this ingredient.
     *
     * @return the item left behind when crafting, or null if nothing is.
     */
    public static Material getCraftingRemainingItem(Material itemType) {
        Material returnedMaterial = null;

        if (Version.has1_15Support()) {
            if (itemType == Material.HONEY_BOTTLE) {
                returnedMaterial = Material.GLASS_BOTTLE;
            }
        }

        if (Version.has1_13BasicSupport()) {
            if (itemType == Material.DRAGON_BREATH) {
                returnedMaterial = Material.GLASS_BOTTLE;
            }
        }

        switch (itemType) {
            case WATER_BUCKET:
            case LAVA_BUCKET:
            case MILK_BUCKET:
                returnedMaterial = Material.BUCKET;
                break;
            default:
                break;
        }

        return returnedMaterial;
    }

    public static boolean isRepairable115_116(CraftingInventory inv) {
        ItemStack[] matrix = inv.getMatrix();

        int numItems = 0;
        ItemStack toMatch = null;
        for (ItemStack item : matrix) {
            if (item != null) {
                if (!isRepairableTool(item)) {
                    return false;
                }

                if (toMatch == null) {
                    toMatch = item;
                } else {
                    if (toMatch.getType() != item.getType()) {
                        return false;
                    }
                }

                numItems ++;

                if (numItems > 2) {
                    return false;
                }
            }
        }

        return numItems == 2;
    }

    private static boolean isRepairableTool(ItemStack item) {
        return item.getItemMeta() instanceof Damageable;
    }
}