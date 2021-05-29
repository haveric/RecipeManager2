package haveric.recipeManager.commands;

import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.tools.RMBukkitTools;
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.ToolsFlag;
import haveric.recipeManager.tools.Version;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CreateRecipeCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender);
            ItemStack holdingStack;
            if (Version.has1_12Support()) {
                holdingStack = player.getInventory().getItemInMainHand();
            } else {
                holdingStack = player.getItemInHand();
            }
            PlayerInventory inventory = player.getInventory();

            if (holdingStack == null || holdingStack.getType() == Material.AIR) {
                MessageSender.getInstance().send(sender, "No item to extract a recipe from.");
            } else {
                File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "extracted" + File.separator + "extracted recipe (" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + ").txt");

                if (file.exists()) {
                    Messages.getInstance().send(sender, "cmd.extract.wait");
                    return true;
                }

                StringBuilder craftString = new StringBuilder(RMCRecipeType.CRAFT.getDirective()).append(Files.NL);
                StringBuilder combineString = new StringBuilder(RMCRecipeType.COMBINE.getDirective()).append(Files.NL);
                StringBuilder conditionString = new StringBuilder();

                parseResult(holdingStack, conditionString);

                ItemStack[] ingredients = new ItemStack[9];

                int inventoryOffset = 9;
                int numNulls = 0;
                for (int i = 0; i <= 8; i++) {
                    if (i > 5) {
                        inventoryOffset = 21;
                    } else if (i > 2) {
                        inventoryOffset = 15;
                    }
                    ingredients[i] = inventory.getItem(i + inventoryOffset);

                    if (ingredients[i] == null) {
                        numNulls ++;
                    }
                }

                if (numNulls == 9) {
                    MessageSender.getInstance().send(player, RMCChatColor.RED + "No ingredients found in the left 3x3 inventory slots.");
                    return false;
                }

                RMBukkitTools.trimItemMatrix(ingredients);

                int width = 0;
                int height = 0;

                for (int h = 0; h < 3; h++) {
                    for (int w = 0; w < 3; w++) {
                        ItemStack item = ingredients[(h * 3) + w];

                        if (item != null) {
                            width = Math.max(width, w);
                            height = Math.max(height, h);
                        }
                    }
                }

                width++;
                height++;

                for (int i = 0; i < height; i++) {
                    int rowStart = 3 * i;
                    parseIngredientForConditions(ingredients[rowStart], craftString, conditionString);
                    for (int j = 1; j < width; j++) {
                        craftString.append(" + ");
                        parseIngredientForConditions(ingredients[rowStart + j], craftString, conditionString);
                    }
                    craftString.append(Files.NL);
                }

                boolean first = true;
                for (ItemStack item : ingredients) {
                    if (item != null) {
                        if (first) {
                            first = false;
                        } else {
                            combineString.append(" + ");
                        }
                        parseIngredientName(item, combineString);
                    }
                }
                combineString.append(Files.NL);

                StringBuilder recipeString = new StringBuilder();
                recipeString.append(craftString).append(conditionString).append(Files.NL);
                recipeString.append(combineString).append(conditionString).append(Files.NL);

                file.getParentFile().mkdirs();

                try {
                    BufferedWriter stream = new BufferedWriter(new FileWriter(file, false));

                    stream.write(recipeString.toString());

                    stream.close();

                    Messages.getInstance().send(sender, "cmd.extractrecipe.done", "{file}", file.getPath().replace(RecipeManager.getPlugin().getDataFolder().toString(), ""));
                } catch (IOException e) {
                    MessageSender.getInstance().error(sender, e, "Error writing '" + file.getName() + "'");
                }
            }
        }

        return true;
    }

    private void parseIngredientForConditions(ItemStack item, StringBuilder recipeString, StringBuilder conditionString) {
        parseIngredientName(item, recipeString);

        if (item != null && item.getType() != Material.AIR) {
            StringBuilder ingredientCondition = new StringBuilder(100);

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (meta.hasDisplayName()) {
                    ingredientCondition.append(" | name ").append(meta.getDisplayName());
                }

                if (meta.hasLore()) {
                    List<String> lores = meta.getLore();
                    if (lores != null) {
                        for (String lore : lores) {
                            ingredientCondition.append(" | lore ").append(lore);
                        }
                    }
                }

                // TODO: Add FlagHide support to Conditions

                if (meta.hasCustomModelData()) {
                    ingredientCondition.append(" | custommodeldata ").append(meta.getCustomModelData());
                }

                if (meta.hasLocalizedName()) {
                    ingredientCondition.append(" | localizedname ").append(meta.getLocalizedName());
                }

                if (Version.has1_11Support() && meta.isUnbreakable()) {
                    ingredientCondition.append(" | unbreakable");
                }

                // TODO: Add FlagItemAttribute support to Conditions

                if (meta instanceof BannerMeta) {
                    BannerMeta bannerMeta = (BannerMeta) meta;

                    DyeColor bannerColor = bannerMeta.getBaseColor();
                    ingredientCondition.append(" | banner color ").append(bannerColor.name());

                    for (Pattern pattern : bannerMeta.getPatterns()) {
                        PatternType patternType = pattern.getPattern();
                        DyeColor patternColor = pattern.getColor();
                        ingredientCondition.append(", pattern ").append(patternType.name()).append(" ").append(patternColor.name());
                    }
                }

                // TODO: Add FlagMonsterSpawner support to Conditions
                // TODO: Add FlagBookItem support to Conditions
                // TODO: Add FlagCompassItem support to Conditions

                if (meta instanceof EnchantmentStorageMeta) {
                    EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;

                    if (enchantmentStorageMeta.hasStoredEnchants()) {
                        Map<Enchantment, Integer> storedEnchants = enchantmentStorageMeta.getStoredEnchants();
                        for (Entry<Enchantment, Integer> entry : storedEnchants.entrySet()) {
                            Enchantment enchantment = entry.getKey();
                            Integer level = entry.getValue();

                            ingredientCondition.append(" | bookenchant ").append(enchantment.toString());

                            if (level != enchantment.getStartLevel()) {
                                ingredientCondition.append(" ").append(level);
                            }
                        }
                    }
                }

                // TODO: Add FlagFireworkItem support to Conditions
                // TODO: Add FlagFireworkStarItem support to Conditions

                if (meta instanceof LeatherArmorMeta) {
                    LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
                    Color color = leatherMeta.getColor();

                    if (!color.equals(Bukkit.getItemFactory().getDefaultLeatherColor())) {
                        ingredientCondition.append(" | color ").append(color.getRed()).append(",").append(color.getGreen()).append(",").append(color.getBlue());
                    }
                }

                if (meta instanceof PotionMeta) {
                    PotionMeta potionMeta = (PotionMeta) meta;
                    PotionData potionData = potionMeta.getBasePotionData();
                    PotionType potionType = potionData.getType();

                    ingredientCondition.append(" | potion type ").append(potionType);
                    if (potionData.isUpgraded()) {
                        ingredientCondition.append(", level 2");
                    }
                    if (potionData.isExtended()) {
                        ingredientCondition.append(", extended");
                    }

                    // TODO: Add color support to ConditionPotion
                    /*
                    if (potionMeta.hasColor()) {
                        Color potionColor = potionMeta.getColor();
                        if (potionColor != null) {
                            recipeString.append(", color ").append(potionColor.getRed()).append(" ").append(potionColor.getGreen()).append(" ").append(potionColor.getBlue());
                        }
                    }
                    */

                    if (potionMeta.hasCustomEffects()) {
                        List<PotionEffect> potionEffects = potionMeta.getCustomEffects();
                        for (PotionEffect effect : potionEffects) {
                            ingredientCondition.append(" | potioneffect type ");

                            parsePotionEffectForCondition(effect, ingredientCondition);
                        }
                    }
                }

                // TODO: Add FlagRepairCost to Conditions
                // TODO: Add FlagSkullOwner to Conditions

                if (!Version.has1_13BasicSupport() && meta instanceof SpawnEggMeta) {
                    SpawnEggMeta spawnEggMeta = (SpawnEggMeta) meta;
                    EntityType spawnedType = spawnEggMeta.getSpawnedType();
                    if (spawnedType != null) {
                        ingredientCondition.append(Files.NL).append("| spawnegg ").append(spawnedType.name());
                    }
                }

                if (Supports.suspiciousStewMeta() && meta instanceof SuspiciousStewMeta) {
                    SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) meta;
                    if (stewMeta.hasCustomEffects()) {
                        List<PotionEffect> potionEffects = stewMeta.getCustomEffects();
                        for (PotionEffect effect : potionEffects) {
                            ingredientCondition.append(" | suspiciousstew type ");

                            parsePotionEffectForCondition(effect, ingredientCondition);
                        }
                    }
                }
            }

            if (item.getAmount() != 1) {
                ingredientCondition.append(" | amount ").append(item.getAmount());
            }

            if (item.getEnchantments().size() > 0) {
                for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                    ingredientCondition.append(" | enchant ").append(entry.getKey().getName()).append(" ").append(entry.getValue());
                }
            }

            String finalIngredientCondition = ingredientCondition.toString();
            if (finalIngredientCondition.length() > 0) {
                conditionString.append(FlagType.INGREDIENT_CONDITION).append(' ').append(item.getType().toString().toLowerCase()).append(finalIngredientCondition);
                conditionString.append(Files.NL);
            }
        }
    }

    private void parsePotionEffectForCondition(PotionEffect effect, StringBuilder ingredientCondition) {
        PotionEffectType effectType = effect.getType();
        ingredientCondition.append(effectType);
        int duration = effect.getDuration();
        if (duration != 20) {
            float durationInSeconds = (float) (duration / 20);
            ingredientCondition.append(", duration ").append(durationInSeconds);
        }
        int amplifier = effect.getAmplifier();
        if (amplifier != 0) {
            ingredientCondition.append(", amplifier ").append(amplifier);
        }
        if (!effect.isAmbient()) {
            ingredientCondition.append(", !ambient");
        }
        if (!effect.hasParticles()) {
            ingredientCondition.append(", !particles");
        }

        if (Version.has1_13BasicSupport()) {
            if (!effect.hasIcon()) {
                ingredientCondition.append(", !icon");
            }
        }
    }

    private void parseIngredientName(ItemStack item, StringBuilder recipeString) {
        String name;

        if (item == null || item.getType() == Material.AIR) {
            name = "air";
        } else {
            name = item.getType().toString().toLowerCase();

            if (item.getDurability() == -1 || item.getDurability() == RMCVanilla.DATA_WILDCARD) {
                name += ":*";
            } else if (item.getDurability() != 0) {
                name += ":" + item.getDurability();
            }
        }

        recipeString.append(name);
    }

    private void parseResult(ItemStack result, StringBuilder recipeString) {
        recipeString.append("= ").append(result.getType().toString().toLowerCase());
        if (result.getDurability() != 0 || result.getAmount() > 1) {
            recipeString.append(':').append(result.getDurability());
        }
        if (result.getAmount() > 1) {
            recipeString.append(':').append(result.getAmount());
        }

        ToolsFlag.parseItemMeta(result, recipeString);
    }
}
