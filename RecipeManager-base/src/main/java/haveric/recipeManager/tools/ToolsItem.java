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
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Furnace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import java.util.*;

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

    public static String printRecipeChoice(RecipeChoice choice, RMCChatColor defColor, RMCChatColor endColor) {
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
            return printChoice(materialChoice.getChoices(), defColor, endColor);
        } else if (choice instanceof  RecipeChoice.ExactChoice) {
            RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
            return printExactChoice(exactChoice.getChoices(), defColor, endColor);
        } else {
            return RMCChatColor.GRAY + "(air)" + endColor;
        }
    }

    public static String printExactChoice(List<ItemStack> items, RMCChatColor defColor, RMCChatColor endColor) {
        String print = "";

        int size = items.size();
        for (int i = 0; i < size; i++) {
            print += print(items.get(i), defColor, endColor);

            if (i + 1 < size) {
                print += ",";
            }
        }

        return print;
    }

    public static String printChoice(List<Material> materials, RMCChatColor defColor, RMCChatColor endColor) {
        String print = "";

        int size = materials.size();
        for (int i = 0; i < size; i++) {
            print += printMaterial(materials.get(i), defColor, endColor);

            if (i + 1 < size) {
                print += ",";
            }
        }

        return print;
    }

    private static String printMaterial(Material material, RMCChatColor defColor, RMCChatColor endColor) {
        String print;
        String name;

        String startTextColor = "";
        String endTextColor = "";
        if (defColor != null) {
            startTextColor += defColor;
        }
        if (endColor != null) {
            endTextColor += endColor;
        }

        if (material == Material.AIR) {
            print = RMCChatColor.GRAY + "(air)" + endTextColor;
        } else {
            name = RecipeManager.getSettings().getMaterialPrint(material);

            if (name == null) {
                name = Tools.parseAliasPrint(material.toString());
            }

            print = startTextColor + name + endTextColor;
        }

        return print;
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

    public static int getNumMaterialsInRecipeChoice(Material type, RecipeChoice choice) {
        int found = 0;
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

            List<Material> materials = materialChoice.getChoices();

            if (materials.contains(type)) {
                found++;
            }
        } else if (choice instanceof RecipeChoice.ExactChoice) {
            RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
            List<ItemStack> items = exactChoice.getChoices();

            for (ItemStack item : items) {
                if (item.getType() == type) {
                    found++;
                }
            }
        }

        return found;
    }

    public static String getRecipeChoiceHash(RecipeChoice choice) {
        StringBuilder s = new StringBuilder();
        if (choice instanceof RecipeChoice.MaterialChoice) {
            s.append("material:");
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

            List<Material> sorted = new ArrayList<>(materialChoice.getChoices());
            Collections.sort(sorted);

            int materialsSize = sorted.size();
            for (int i = 0; i < materialsSize; i++) {
                s.append(sorted.get(i).toString());

                if (i + 1 < materialsSize) {
                    s.append(",");
                }
            }
        } else if (choice instanceof RecipeChoice.ExactChoice) {
            s.append("exact:");
            RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;

            List<ItemStack> sorted = new ArrayList<>(exactChoice.getChoices());
            sorted.sort(Comparator.comparing(ItemStack::getType));

            int itemsSize = sorted.size();
            for (int i = 0; i < itemsSize; i++) {
                s.append(sorted.get(i).hashCode());

                if (i + 1 < itemsSize) {
                    s.append(",");
                }
            }
        } else {
            s.append("air");
        }

        return s.toString();
    }

    public static String getRecipeChoiceName(RecipeChoice choice) {
        StringBuilder s = new StringBuilder();

        if (choice instanceof RecipeChoice.MaterialChoice) {
            s.append("material:");
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
            List<Material> materials = materialChoice.getChoices();
            int materialsSize = materials.size();
            for (int i = 0; i < materialsSize; i++) {
                s.append(materials.get(i).toString());

                if (i + 1 < materialsSize) {
                    s.append(",");
                }
            }
        } else if (choice instanceof RecipeChoice.ExactChoice) {
            s.append("exact:");
            RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
            List<ItemStack> items = exactChoice.getChoices();

            int itemsSize = items.size();
            for (int i = 0; i < itemsSize; i++) {
                s.append(items.get(i).getType().toString()).append("-").append(items.get(i).hashCode());

                if (i + 1 < itemsSize) {
                    s.append(",");
                }
            }
        } else {
            s.append("air");
        }

        return s.toString();
    }

    public static RecipeChoice mergeRecipeChoices(RecipeChoice choice, RecipeChoice choiceToMerge) {
        if (choiceToMerge instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choiceToMerge;
            return mergeRecipeChoiceWithMaterials(choice, materialChoice.getChoices());
        } else if (choiceToMerge instanceof RecipeChoice.ExactChoice) {
            RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choiceToMerge;
            return mergeRecipeChoiceWithItems(choice, exactChoice.getChoices());
        }

        return choice;
    }

    public static RecipeChoice mergeRecipeChoiceWithMaterials(RecipeChoice choice, Material material) {
        List<Material> materials = new ArrayList<>();
        materials.add(material);
        return mergeRecipeChoiceWithMaterials(choice, materials);
    }

    public static RecipeChoice mergeRecipeChoiceWithMaterials(RecipeChoice choice, List<Material> materials) {
        if (choice == null) {
            choice = new RecipeChoice.MaterialChoice(materials);
        } else if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
            List<Material> newMaterials = new ArrayList<>();
            newMaterials.addAll(materialChoice.getChoices());
            newMaterials.addAll(materials);

            choice = new RecipeChoice.MaterialChoice(newMaterials);
        } else if (choice instanceof RecipeChoice.ExactChoice) {
            List<ItemStack> items = new ArrayList<>();
            for (Material material : materials) {
                items.add(new ItemStack(material));
            }

            return mergeRecipeChoiceWithItems(choice, items);
        }

        return choice;
    }

    public static RecipeChoice mergeRecipeChoiceWithItems(RecipeChoice choice, ItemStack item) {
        List<ItemStack> items = new ArrayList<>();
        items.add(item);
        return mergeRecipeChoiceWithItems(choice, items);
    }

    public static RecipeChoice mergeRecipeChoiceWithItems(RecipeChoice choice, List<ItemStack> items) {
        if (choice == null) {
            choice = new RecipeChoice.ExactChoice(items);
        } else if (choice instanceof RecipeChoice.ExactChoice) {
            RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
            List<ItemStack> newItems = new ArrayList<>();
            newItems.addAll(exactChoice.getChoices());
            newItems.addAll(items);

            choice = new RecipeChoice.ExactChoice(newItems);
        } else if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
            List<ItemStack> newItems = new ArrayList<>();
            for (Material material : materialChoice.getChoices()) {
                newItems.add(new ItemStack(material));
            }
            newItems.addAll(items);

            choice = new RecipeChoice.ExactChoice(newItems);
        }

        return choice;
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
        }

        return match;
    }

    public static int getIngredientMatchQuality(ItemStack ingredient, RecipeChoice choice, boolean checkExact) {
        Material ingredientType = ingredient.getType();
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

            for (Material material : materialChoice.getChoices()) {
                if (material == ingredientType) {
                    return 1;
                }
            }
        } else if (choice instanceof RecipeChoice.ExactChoice) {
            ItemMeta ingredientMeta = ingredient.getItemMeta();

            RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
            List<ItemStack> items = exactChoice.getChoices();

            int bestQuality = 0;
            for (ItemStack item : items) {
                if (checkExact) {
                    if (ToolsItem.isSameItemHash(item, ingredient)) {
                        bestQuality = 1000;
                        break;
                    }

                    continue;
                }
                int quality = 0;
                if (item.getType() == ingredientType) {
                    ItemMeta itemMeta = item.getItemMeta();

                    if (itemMeta != null && ingredientMeta != null) {
                        if (itemMeta.hasDisplayName() && ingredientMeta.hasDisplayName()) {
                            if (itemMeta.getDisplayName().equals(ingredientMeta.getDisplayName())) {
                                quality ++; // Display name matches
                            }
                        }

                        if (itemMeta.hasLore() && ingredientMeta.hasLore()) {
                            List<String> itemLore = itemMeta.getLore();
                            List<String> ingredientLore = ingredientMeta.getLore();

                            if (itemLore != null && ingredientLore != null && itemLore.size() == ingredientLore.size()) {
                                int numMatches = 0;
                                for (int i = 0; i < itemLore.size(); i++) {
                                    if (itemLore.get(i).equals(ingredientLore.get(i))) {
                                        numMatches ++;
                                    }
                                }

                                if (numMatches == itemLore.size()) {
                                    quality ++; // All Lores match
                                }
                            }
                        }

                        if (itemMeta.hasLocalizedName() && ingredientMeta.hasLocalizedName()) {
                            if (itemMeta.getLocalizedName().equals(ingredientMeta.getLocalizedName())) {
                                quality ++; // Localized name matches
                            }
                        }

                        if (itemMeta.hasEnchants() && ingredientMeta.hasEnchants()) {
                            Map<Enchantment, Integer> itemEnchants = itemMeta.getEnchants();
                            Map<Enchantment, Integer> ingredientEnchants = ingredientMeta.getEnchants();

                            for (Map.Entry<Enchantment, Integer> entry : itemEnchants.entrySet()) {
                                if (ingredientEnchants.containsKey(entry.getKey())) {
                                    if (ingredientEnchants.get(entry.getKey()) == entry.getValue()) {
                                        quality ++; // Enchantment matches
                                    }
                                }
                            }
                        }

                        if (itemMeta.hasCustomModelData() && ingredientMeta.hasCustomModelData()) {
                            if (itemMeta.getCustomModelData() == ingredientMeta.getCustomModelData()) {
                                quality ++; // Custom Model Data matches
                            }
                        }

                        for (ItemFlag itemFlag : ItemFlag.values()) {
                            if (itemMeta.hasItemFlag(itemFlag) && ingredientMeta.hasItemFlag(itemFlag)) {
                                quality ++; // Item Flag matches
                            }
                        }

                        if (itemMeta.isUnbreakable() == ingredientMeta.isUnbreakable()) {
                            quality ++;
                        }

                        if (itemMeta instanceof BannerMeta) {
                            BannerMeta itemBannerMeta = (BannerMeta) itemMeta;
                            BannerMeta ingredientBannerMeta = (BannerMeta) ingredientMeta;

                            if (itemBannerMeta.numberOfPatterns() == ingredientBannerMeta.numberOfPatterns()) {
                                quality ++;

                                for (int i = 0; i < itemBannerMeta.numberOfPatterns(); i++) {
                                    Pattern itemPattern = itemBannerMeta.getPattern(i);
                                    Pattern ingredientPattern = ingredientBannerMeta.getPattern(i);
                                    if (itemPattern.getPattern() == ingredientPattern.getPattern()) {
                                        quality ++;
                                    }

                                    if (itemPattern.getColor() == ingredientPattern.getColor()) {
                                        quality ++;
                                    }
                                }
                            }
                        }

                        if (itemMeta instanceof BookMeta) {
                            BookMeta itemBookMeta = (BookMeta) itemMeta;
                            BookMeta ingredientBookMeta = (BookMeta) ingredientMeta;

                            if (itemBookMeta.hasAuthor() && ingredientBookMeta.hasAuthor()) {
                                String itemAuthor = itemBookMeta.getAuthor();
                                String ingredientAuthor = ingredientBookMeta.getAuthor();
                                if (itemAuthor == null && ingredientAuthor == null) {
                                    quality ++;
                                } else if (itemAuthor != null && itemAuthor.equals(ingredientAuthor)) {
                                    quality ++;
                                }
                            } else if (!itemBookMeta.hasAuthor() && !ingredientBookMeta.hasAuthor()) {
                                quality ++;
                            }

                            if (itemBookMeta.hasTitle() && ingredientBookMeta.hasTitle()) {
                                String itemTitle = itemBookMeta.getTitle();
                                String ingredientTitle = ingredientBookMeta.getTitle();
                                if (itemTitle == null && ingredientTitle == null) {
                                    quality ++;
                                } else if (itemTitle != null && itemTitle.equals(ingredientTitle)) {
                                    quality ++;
                                }
                            } else if (!itemBookMeta.hasTitle() && !ingredientBookMeta.hasTitle()) {
                                quality ++;
                            }

                            if (itemBookMeta.hasGeneration() && ingredientBookMeta.hasGeneration()) {
                                if (itemBookMeta.getGeneration() == ingredientBookMeta.getGeneration()) {
                                    quality ++;
                                }
                            }

                            if (itemBookMeta.getPageCount() == ingredientBookMeta.getPageCount()) {
                                quality ++;
                            }
                        }

                        if (itemMeta instanceof EnchantmentStorageMeta) {
                            EnchantmentStorageMeta itemStorageMeta = (EnchantmentStorageMeta) itemMeta;
                            EnchantmentStorageMeta ingredientStorageMeta = (EnchantmentStorageMeta) ingredientMeta;

                            if (itemStorageMeta.hasEnchants() && ingredientStorageMeta.hasEnchants()) {
                                Map<Enchantment, Integer> itemEnchants = itemStorageMeta.getEnchants();
                                Map<Enchantment, Integer> ingredientEnchants = ingredientStorageMeta.getEnchants();

                                for (Map.Entry<Enchantment, Integer> entry : itemEnchants.entrySet()) {
                                    if (ingredientEnchants.containsKey(entry.getKey())) {
                                        if (ingredientEnchants.get(entry.getKey()) == entry.getValue()) {
                                            quality ++; // Enchantment matches
                                        }
                                    }
                                }
                            }
                        }

                        if (itemMeta instanceof FireworkMeta) {
                            FireworkMeta itemFireworkMeta = (FireworkMeta) itemMeta;
                            FireworkMeta ingredientFireworkMeta = (FireworkMeta) ingredientMeta;

                            if (itemFireworkMeta.getPower() == ingredientFireworkMeta.getPower()) {
                                quality ++;
                            }

                            if (itemFireworkMeta.hasEffects() && ingredientFireworkMeta.hasEffects()) {
                                if (itemFireworkMeta.getEffectsSize() == ingredientFireworkMeta.getEffectsSize()) {
                                    quality ++;

                                    List<FireworkEffect> itemEffects = itemFireworkMeta.getEffects();
                                    List<FireworkEffect> ingredientEffects = ingredientFireworkMeta.getEffects();
                                    for (int i = 0; i < itemFireworkMeta.getEffectsSize(); i++) {
                                        quality += getFireworkEffectQuality(itemEffects.get(i), ingredientEffects.get(i));
                                    }
                                }
                            } else if (!itemFireworkMeta.hasEffects() && !ingredientFireworkMeta.hasEffects()) {
                                quality ++;
                            }
                        }

                        if (itemMeta instanceof FireworkEffectMeta) {
                            FireworkEffectMeta itemFireworkEffectMeta = (FireworkEffectMeta) itemMeta;
                            FireworkEffectMeta ingredientFireworkEffectMeta = (FireworkEffectMeta) ingredientMeta;

                            if (itemFireworkEffectMeta.hasEffect() == ingredientFireworkEffectMeta.hasEffect()) {
                                quality ++;

                                if (itemFireworkEffectMeta.getEffect() != null && ingredientFireworkEffectMeta.getEffect() != null) {
                                    quality += getFireworkEffectQuality(itemFireworkEffectMeta.getEffect(), ingredientFireworkEffectMeta.getEffect());
                                }
                            }
                        }

                        if (itemMeta instanceof LeatherArmorMeta) {
                            LeatherArmorMeta itemLeatherMeta = (LeatherArmorMeta) itemMeta;
                            LeatherArmorMeta ingredientLeatherMeta = (LeatherArmorMeta) ingredientMeta;

                            if (itemLeatherMeta.getColor().equals(ingredientLeatherMeta.getColor())) {
                                quality ++;
                            }
                        }

                        if (itemMeta instanceof PotionMeta) {
                            PotionMeta itemPotion = (PotionMeta) itemMeta;
                            PotionMeta ingredientPotion = (PotionMeta) ingredientMeta;

                            if (itemPotion.hasColor() && ingredientPotion.hasColor()) {
                                if (itemPotion.getColor() != null) {
                                    if (itemPotion.getColor().equals(ingredientPotion.getColor())) {
                                        quality++;
                                    }
                                }
                            }

                            PotionData itemPotionData = itemPotion.getBasePotionData();
                            PotionData ingredientPotionData = ingredientPotion.getBasePotionData();

                            if (itemPotionData.getType() == ingredientPotionData.getType()) {
                                quality ++;
                            }

                            if (itemPotionData.isExtended() == ingredientPotionData.isExtended()) {
                                quality ++;
                            }

                            if (itemPotionData.isUpgraded() == ingredientPotionData.isUpgraded()) {
                                quality ++;
                            }

                            if (itemPotion.hasCustomEffects() && ingredientPotion.hasCustomEffects()) {
                                quality ++;

                                quality += getPotionEffectsQuality(itemPotion.getCustomEffects(), ingredientPotion.getCustomEffects());
                            } else if (!itemPotion.hasCustomEffects() && !ingredientPotion.hasCustomEffects()) {
                                quality ++;
                            }
                        }

                        if (itemMeta instanceof Repairable) {
                            Repairable itemRepairable = (Repairable) itemMeta;
                            Repairable ingredientRepairable = (Repairable) ingredientMeta;

                            if (itemRepairable.hasRepairCost() && ingredientRepairable.hasRepairCost()) {
                                quality ++;

                                if (itemRepairable.getRepairCost() == ingredientRepairable.getRepairCost()) {
                                    quality++;
                                }
                            } else if (!itemRepairable.hasRepairCost() && !ingredientRepairable.hasRepairCost()) {
                                quality ++;
                            }
                        }

                        if (itemMeta instanceof BlockStateMeta) {
                            BlockStateMeta itemBlockStateMeta = (BlockStateMeta) itemMeta;
                            BlockStateMeta ingredientBlockStateMeta = (BlockStateMeta) ingredientMeta;
                            BlockState itemBlockState = itemBlockStateMeta.getBlockState();
                            BlockState ingredientBlockState = ingredientBlockStateMeta.getBlockState();

                            if (itemBlockState instanceof CreatureSpawner) {
                                CreatureSpawner itemSpawner = (CreatureSpawner) itemBlockState;
                                CreatureSpawner ingredientSpawner = (CreatureSpawner) ingredientBlockState;

                                if (itemSpawner.getSpawnedType() == ingredientSpawner.getSpawnedType()) {
                                    quality ++;
                                }

                                if (itemSpawner.getDelay() == ingredientSpawner.getDelay()) {
                                    quality ++;
                                }

                                if (itemSpawner.getMinSpawnDelay() == ingredientSpawner.getMinSpawnDelay()) {
                                    quality ++;
                                }

                                if (itemSpawner.getMaxSpawnDelay() == ingredientSpawner.getMaxSpawnDelay()) {
                                    quality ++;
                                }

                                if (itemSpawner.getMaxNearbyEntities() == ingredientSpawner.getMaxNearbyEntities()) {
                                    quality ++;
                                }

                                if (itemSpawner.getRequiredPlayerRange() == ingredientSpawner.getRequiredPlayerRange()) {
                                    quality ++;
                                }

                                if (itemSpawner.getSpawnRange() == ingredientSpawner.getSpawnRange()) {
                                    quality ++;
                                }

                                if (itemSpawner.getSpawnCount() == ingredientSpawner.getSpawnCount()) {
                                    quality ++;
                                }
                            }
                        }

                        if (itemMeta instanceof SuspiciousStewMeta) {
                            SuspiciousStewMeta itemStewMeta = (SuspiciousStewMeta) itemMeta;
                            SuspiciousStewMeta ingredientStewMeta = (SuspiciousStewMeta) ingredientMeta;

                            if (itemStewMeta.hasCustomEffects() && ingredientStewMeta.hasCustomEffects()) {
                                quality ++;

                                quality += getPotionEffectsQuality(itemStewMeta.getCustomEffects(), ingredientStewMeta.getCustomEffects());
                            } else if (!itemStewMeta.hasCustomEffects() && !ingredientStewMeta.hasCustomEffects()) {
                                quality ++;
                            }
                        }
                    }
                }

                if (quality > bestQuality) {
                    bestQuality = quality;
                }
            }

            return bestQuality;
        }

        return 0; // No item match
    }

    private static int getFireworkEffectQuality(FireworkEffect itemEffect, FireworkEffect ingredientEffect) {
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

    private static int getPotionEffectsQuality(List<PotionEffect> itemPotionEffects, List<PotionEffect> ingredientPotionEffects) {
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
}