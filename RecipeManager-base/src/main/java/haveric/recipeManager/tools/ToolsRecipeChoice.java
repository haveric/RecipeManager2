package haveric.recipeManager.tools;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.RMCChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;

import java.util.*;

public class ToolsRecipeChoice {

    public static boolean isValidMetaType(RecipeChoice choice, Class<?> metaClass) {
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

            int numMatches = 0;
            int total = materialChoice.getChoices().size();
            for (Material material : materialChoice.getChoices()) {
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                if (meta != null && metaClass.isAssignableFrom(meta.getClass())) {
                    numMatches ++;
                }
            }

            return numMatches == total;
        } else if (choice instanceof RecipeChoice.ExactChoice) {
            RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;

            int numMatches = 0;
            int total = exactChoice.getChoices().size();
            for (ItemStack item : exactChoice.getChoices()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && metaClass.isAssignableFrom(meta.getClass())) {
                    numMatches ++;
                }
            }

            return numMatches == total;
        }

        return false;
    }

    public static String printRecipeChoice(RecipeChoice choice, RMCChatColor defColor, RMCChatColor endColor) {
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
            return printChoice(materialChoice.getChoices(), defColor, endColor);
        } else if (choice instanceof RecipeChoice.ExactChoice) {
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
            print += ToolsItem.print(items.get(i), defColor, endColor);

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

    public static List<Material> getMaterialsInRecipeChoice(RecipeChoice choice) {
        List<Material> materials = new ArrayList<>();
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

            materials.addAll(materialChoice.getChoices());

        } else if (choice instanceof RecipeChoice.ExactChoice) {
            RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
            List<ItemStack> items = exactChoice.getChoices();

            for (ItemStack item : items) {
                materials.add(item.getType());
            }
        }

        return materials;
    }

    public static String getRecipeChoiceHash(RecipeChoice choice) {
        StringBuilder s = new StringBuilder();
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

            List<Material> sorted = new ArrayList<>(materialChoice.getChoices());
            Collections.sort(sorted);

            int materialsSize = sorted.size();

            if (materialsSize == 1 && sorted.get(0) == Material.AIR) {
                s.append("AIR");
            } else {
                s.append("material:");
                for (int i = 0; i < materialsSize; i++) {
                    s.append(sorted.get(i).toString());

                    if (i + 1 < materialsSize) {
                        s.append(",");
                    }
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
            s.append("AIR");
        }

        return s.toString();
    }

    public static String getRecipeChoiceName(RecipeChoice choice) {
        StringBuilder s = new StringBuilder();

        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
            List<Material> materials = materialChoice.getChoices();
            int materialsSize = materials.size();

            if (materialsSize == 1 && materials.get(0) == Material.AIR) {
                s.append("AIR");
            } else {
                s.append("material:");
                for (int i = 0; i < materialsSize; i++) {
                    s.append(materials.get(i).toString());

                    if (i + 1 < materialsSize) {
                        s.append(",");
                    }
                }
            }
        } else if (choice instanceof RecipeChoice.ExactChoice) {
            s.append("exact:");
            RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
            List<ItemStack> items = exactChoice.getChoices();

            int itemsSize = items.size();
            for (int i = 0; i < itemsSize; i++) {
                s.append(items.get(i).getType()).append("-").append(items.get(i).hashCode());

                if (i + 1 < itemsSize) {
                    s.append(",");
                }
            }
        } else {
            s.append("AIR");
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
                                    if (ingredientEnchants.get(entry.getKey()).equals(entry.getValue())) {
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
                                        if (ingredientEnchants.get(entry.getKey()).equals(entry.getValue())) {
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
                                        quality += ToolsItem.getFireworkEffectQuality(itemEffects.get(i), ingredientEffects.get(i));
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
                                    quality += ToolsItem.getFireworkEffectQuality(itemFireworkEffectMeta.getEffect(), ingredientFireworkEffectMeta.getEffect());
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

                                quality += ToolsItem.getPotionEffectsQuality(itemPotion.getCustomEffects(), ingredientPotion.getCustomEffects());
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

                                quality += ToolsItem.getPotionEffectsQuality(itemStewMeta.getCustomEffects(), ingredientStewMeta.getCustomEffects());
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

    public static RecipeChoice convertAirMaterialChoiceToNull(RecipeChoice choice) {
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
            List<Material> materials = materialChoice.getChoices();
            if (materials.size() == 1 && materials.get(0) == Material.AIR) {
                return null;
            }
        }

        return choice;
    }
}
