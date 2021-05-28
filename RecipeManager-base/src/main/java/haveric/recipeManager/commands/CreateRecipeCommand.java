package haveric.recipeManager.commands;

import com.google.common.collect.Multimap;
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
import haveric.recipeManager.tools.Version;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
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
import java.util.Set;

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
                ingredients[0] = inventory.getItem(9);
                ingredients[1] = inventory.getItem(10);
                ingredients[2] = inventory.getItem(11);

                ingredients[3] = inventory.getItem(18);
                ingredients[4] = inventory.getItem(19);
                ingredients[5] = inventory.getItem(20);

                ingredients[6] = inventory.getItem(27);
                ingredients[7] = inventory.getItem(28);
                ingredients[8] = inventory.getItem(29);

                int numNulls = 0;
                for (int i = 0; i <= 8; i++) {
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

                parseIngredient(ingredients[0], craftString, conditionString);
                if (width > 1) {
                    craftString.append(" + ");
                    parseIngredient(ingredients[1], craftString, conditionString);
                }
                if (width > 2) {
                    craftString.append(" + ");
                    parseIngredient(ingredients[2], craftString, conditionString);
                }
                craftString.append(Files.NL);

                if (height > 1) {
                    parseIngredient(ingredients[3], craftString, conditionString);
                    if (width > 1) {
                        craftString.append(" + ");
                        parseIngredient(ingredients[4], craftString, conditionString);
                    }
                    if (width > 2) {
                        craftString.append(" + ");
                        parseIngredient(ingredients[5], craftString, conditionString);
                    }
                    craftString.append(Files.NL);
                }

                if (height > 2) {
                    parseIngredient(ingredients[6], craftString, conditionString);
                    if (width > 1) {
                        craftString.append(" + ");
                        parseIngredient(ingredients[7], craftString, conditionString);
                    }
                    if (width > 2) {
                        craftString.append(" + ");
                        parseIngredient(ingredients[8], craftString, conditionString);
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

    private void parseIngredient(ItemStack item, StringBuilder recipeString, StringBuilder conditionString) {
        parseIngredientName(item, recipeString);

        if (item != null && item.getType() != Material.AIR) {
            String ingredientCondition = "";

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (meta.hasDisplayName()) {
                    ingredientCondition += " | name " + meta.getDisplayName();
                }

                if (meta.hasLore()) {
                    List<String> lores = meta.getLore();
                    for (String lore : lores) {
                        ingredientCondition += " | lore " + lore;
                    }
                }

                // TODO: Add FlagHide support to Conditions

                if (meta.hasCustomModelData()) {
                    recipeString.append(" | custommodeldata ").append(meta.getCustomModelData());
                }

                if (meta.hasLocalizedName()) {
                    recipeString.append(" | localizedname ").append(meta.getLocalizedName());
                }

                if (Version.has1_11Support() && meta.isUnbreakable()) {
                    recipeString.append(" | unbreakable");
                }

                // TODO: Add FlagItemAttribute support to Conditions

                if (meta instanceof BannerMeta) {
                    BannerMeta bannerMeta = (BannerMeta) meta;

                    DyeColor bannerColor = bannerMeta.getBaseColor();
                    recipeString.append(" | banneritem color ").append(bannerColor.name());

                    for (Pattern pattern : bannerMeta.getPatterns()) {
                        PatternType patternType = pattern.getPattern();
                        DyeColor patternColor = pattern.getColor();
                        recipeString.append(" | banneritem pattern").append(patternType.name()).append(" ").append(patternColor.name());
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

                            recipeString.append(" | bookenchant ").append(enchantment.toString());

                            if (level != enchantment.getStartLevel()) {
                                recipeString.append(" ").append(level);
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
                        ingredientCondition += " | color " + color.getRed() + "," + color.getGreen() + "," + color.getBlue();
                    }
                }

                if (meta instanceof PotionMeta) {
                    PotionMeta potionMeta = (PotionMeta) meta;
                    PotionData potionData = potionMeta.getBasePotionData();
                    PotionType potionType = potionData.getType();

                    recipeString.append(" | potion type ").append(potionType);
                    if (potionData.isUpgraded()) {
                        recipeString.append(", level 2");
                    }
                    if (potionData.isExtended()) {
                        recipeString.append(", extended");
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
                            PotionEffectType effectType = effect.getType();
                            recipeString.append(" | potioneffect type ").append(effectType);

                            int duration = effect.getDuration();
                            if (duration != 20) {
                                float durationInSeconds = (float) (duration / 20);
                                recipeString.append(", duration ").append(durationInSeconds);
                            }
                            int amplifier = effect.getAmplifier();
                            if (amplifier != 0) {
                                recipeString.append(", amplifier ").append(amplifier);
                            }
                            if (!effect.isAmbient()) {
                                recipeString.append(", !ambient");
                            }
                            if (!effect.hasParticles()) {
                                recipeString.append(", !particles");
                            }

                            if (Version.has1_13BasicSupport()) {
                                if (!effect.hasIcon()) {
                                    recipeString.append(", !icon");
                                }
                            }
                        }
                    }
                }

                // TODO: Add FlagRepairCost to Conditions
                // TODO: Add FlagSkullOwner to Conditions

                if (!Version.has1_13BasicSupport() && meta instanceof SpawnEggMeta) {
                    SpawnEggMeta spawnEggMeta = (SpawnEggMeta) meta;
                    EntityType spawnedType = spawnEggMeta.getSpawnedType();
                    if (spawnedType != null) {
                        recipeString.append(Files.NL).append("| spawnegg ").append(spawnedType.name());
                    }
                }

                if (Supports.suspiciousStewMeta() && meta instanceof SuspiciousStewMeta) {
                    SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) meta;
                    if (stewMeta.hasCustomEffects()) {
                        List<PotionEffect> potionEffects = stewMeta.getCustomEffects();
                        for (PotionEffect effect : potionEffects) {
                            PotionEffectType effectType = effect.getType();
                            recipeString.append(" | suspiciousstew type ").append(effectType);

                            int duration = effect.getDuration();
                            if (duration != 20) {
                                float durationInSeconds = (float) (duration / 20);
                                recipeString.append(", duration ").append(durationInSeconds);
                            }
                            int amplifier = effect.getAmplifier();
                            if (amplifier != 0) {
                                recipeString.append(", amplifier ").append(amplifier);
                            }
                            if (!effect.isAmbient()) {
                                recipeString.append(", !ambient");
                            }
                            if (!effect.hasParticles()) {
                                recipeString.append(", !particles");
                            }

                            if (Version.has1_13BasicSupport()) {
                                if (!effect.hasIcon()) {
                                    recipeString.append(", !icon");
                                }
                            }
                        }
                    }
                }
            }

            if (item.getAmount() != 1) {
                ingredientCondition += " | amount " + item.getAmount();
            }

            if (item.getEnchantments().size() > 0) {
                for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                    ingredientCondition += " | enchant " + entry.getKey().getName() + " " + entry.getValue();
                }
            }

            if (ingredientCondition.length() > 0) {
                conditionString.append(FlagType.INGREDIENT_CONDITION).append(' ').append(item.getType().toString().toLowerCase()).append(ingredientCondition);
                conditionString.append(Files.NL);
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

        if (result.getEnchantments().size() > 0) {
            for (Entry<Enchantment, Integer> entry : result.getEnchantments().entrySet()) {
                recipeString.append(Files.NL).append("@enchant ").append(entry.getKey().getName()).append(' ').append(entry.getValue());
            }
        }

        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                recipeString.append(Files.NL).append("@name ").append(meta.getDisplayName());
            }

            if (meta.hasLore()) {
                List<String> lores = meta.getLore();
                for (String lore : lores) {
                    recipeString.append(Files.NL).append("@lore ").append(lore);
                }
            }

            Set<ItemFlag> itemFlags = meta.getItemFlags();
            if (!itemFlags.isEmpty()) {
                recipeString.append(Files.NL).append("@hide ");

                boolean first = true;
                for (ItemFlag itemFlag : itemFlags) {
                    if (!first) {
                        recipeString.append(" | ");
                    }
                    recipeString.append(itemFlag.toString().toLowerCase());

                    first = false;
                }
            }

            if (meta.hasCustomModelData()) {
                recipeString.append(Files.NL).append("@custommodeldata ").append(meta.getCustomModelData());
            }

            if (meta.hasLocalizedName()) {
                recipeString.append(Files.NL).append("@localizedname ").append(meta.getLocalizedName());
            }

            if (Version.has1_11Support() && meta.isUnbreakable()) {
                recipeString.append(Files.NL).append("@itemunbreakable");
            }

            if (Version.has1_13BasicSupport() && meta.hasAttributeModifiers()) {
                Multimap<Attribute, AttributeModifier> attributeModifiers = meta.getAttributeModifiers();
                if (attributeModifiers != null) {
                    for (Map.Entry<Attribute, AttributeModifier> entry : attributeModifiers.entries()) {
                        Attribute attribute = entry.getKey();
                        AttributeModifier attributeModifier = entry.getValue();

                        recipeString.append(Files.NL).append("@itemattribute ").append(attribute.name());

                        AttributeModifier.Operation operation = attributeModifier.getOperation();
                        String modifier = " ";

                        switch(operation) {
                            case ADD_SCALAR:
                                modifier += "x";
                                break;

                            case MULTIPLY_SCALAR_1:
                                // TODO: Add MULTIPLY_SCALAR_1 option to FlagItemAttribute
                                break;
                            case ADD_NUMBER:
                            default:
                                break;
                        }

                        recipeString.append(modifier).append(attributeModifier.getAmount());

                        EquipmentSlot slot = attributeModifier.getSlot();
                        if (slot != null) {
                            recipeString.append(" | slot ").append(slot.name());
                        }

                        // TODO: Add name and uuid options to FlagItemAttribute
                        /*
                        String name = attributeModifier.getName();
                        UUID uuid = attributeModifier.getUniqueId();
                        */
                    }
                }
            }

            if (meta instanceof BannerMeta) {
                BannerMeta bannerMeta = (BannerMeta) meta;

                DyeColor bannerColor = bannerMeta.getBaseColor();
                recipeString.append(Files.NL).append("@banneritem ").append(bannerColor.name());

                for (Pattern pattern : bannerMeta.getPatterns()) {
                    PatternType patternType = pattern.getPattern();
                    DyeColor patternColor = pattern.getColor();
                    recipeString.append(" | ").append(patternType.name()).append(" ").append(patternColor.name());
                }
            }

            if (meta instanceof BlockStateMeta) {
                BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
                BlockState blockState = blockStateMeta.getBlockState();

                if (blockState instanceof CreatureSpawner) {
                    CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;

                    recipeString.append(Files.NL).append("@monsterspawner ").append(creatureSpawner.getSpawnedType().name());

                    int delay = creatureSpawner.getDelay();
                    if (delay != 20) {
                        recipeString.append(" | delay ").append(delay);
                    }

                    if (Version.has1_12Support()) {
                        int minDelay = creatureSpawner.getMinSpawnDelay();
                        if (minDelay != 200) {
                            recipeString.append(" | mindelay ").append(minDelay);
                        }

                        int maxDelay = creatureSpawner.getMaxSpawnDelay();
                        if (maxDelay != 800) {
                            recipeString.append(" | maxdelay ").append(maxDelay);
                        }

                        int maxNearbyEntities = creatureSpawner.getMaxNearbyEntities();
                        if (maxNearbyEntities != 6) {
                            recipeString.append(" | maxnearbyentities ").append(maxNearbyEntities);
                        }

                        int playerRange = creatureSpawner.getRequiredPlayerRange();
                        if (playerRange != 16) {
                            recipeString.append(" | playerrange ").append(playerRange);
                        }

                        int spawnRange = creatureSpawner.getSpawnRange();
                        if (spawnRange != 4) {
                            recipeString.append(" | spawnrange ").append(spawnRange);
                        }

                        int spawnCount = creatureSpawner.getSpawnCount();
                        if (spawnCount != 4) {
                            recipeString.append(" | spawncount ").append(spawnCount);
                        }
                    }
                }
            }

            if (meta instanceof BookMeta) {
                BookMeta bookMeta = (BookMeta) meta;

                if (bookMeta.hasTitle()) {
                    recipeString.append(Files.NL).append("@bookitem title ").append(bookMeta.getTitle());
                }

                if (bookMeta.hasAuthor()) {
                    recipeString.append(Files.NL).append("@bookitem author ").append(bookMeta.getAuthor());
                }

                if (bookMeta.hasGeneration()) {
                    BookMeta.Generation generation = bookMeta.getGeneration();
                    if (generation != null) {
                        recipeString.append(Files.NL).append("@bookitem generation ").append(generation.name());
                    }
                }

                List<String> pages = bookMeta.getPages();
                for (String page : pages) {
                    recipeString.append(Files.NL).append("@bookitem addpage ").append(page);
                }
            }

            if (Supports.compassMeta() && meta instanceof CompassMeta) {
                CompassMeta compassMeta = (CompassMeta) meta;

                if (compassMeta.hasLodestone()) {
                    Location location = compassMeta.getLodestone();
                    if (location != null) {
                        World world = location.getWorld();
                        if (world != null) {
                            recipeString.append(Files.NL).append("@compassitem ").append(world.getName()).append(" ").append(location.getX()).append(" ").append(location.getY()).append(" ").append(location.getZ());

                            if (compassMeta.isLodestoneTracked()) {
                                recipeString.append(" | true");
                            }
                        }
                    }
                }
            }

            if (meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;

                if (enchantmentStorageMeta.hasStoredEnchants()) {
                    Map<Enchantment, Integer> storedEnchants = enchantmentStorageMeta.getStoredEnchants();
                    for (Entry<Enchantment, Integer> entry : storedEnchants.entrySet()) {
                        Enchantment enchantment = entry.getKey();
                        Integer level = entry.getValue();

                        recipeString.append(Files.NL).append("@enchantedbook ").append(enchantment.toString());

                        if (level != enchantment.getStartLevel()) {
                            recipeString.append(" ").append(level);
                        }
                    }
                }
            }

            if (meta instanceof FireworkMeta) {
                FireworkMeta fireworkMeta = (FireworkMeta) meta;

                if (fireworkMeta.hasEffects()) {
                    for (FireworkEffect effect : fireworkMeta.getEffects()) {
                        FireworkEffect.Type type = effect.getType();
                        recipeString.append(Files.NL).append("@fireworkitem type ").append(type.name());

                        List<Color> colors = effect.getColors();
                        if (!colors.isEmpty()) {
                            recipeString.append(" | color ");
                            boolean first = true;
                            for (Color color : colors) {
                                if (!first) {
                                    recipeString.append(", ");
                                }
                                recipeString.append(color.getRed()).append(" ").append(color.getGreen()).append(" ").append(color.getBlue());

                                first = false;
                            }
                        }

                        List<Color> fadeColors = effect.getFadeColors();
                        if (!fadeColors.isEmpty()) {
                            recipeString.append(" | fadecolor ");
                            boolean first = true;
                            for (Color fadeColor : fadeColors) {
                                if (!first) {
                                    recipeString.append(", ");
                                }
                                recipeString.append(fadeColor.getRed()).append(" ").append(fadeColor.getGreen()).append(" ").append(fadeColor.getBlue());

                                first = false;
                            }
                        }

                        if (effect.hasTrail()) {
                            recipeString.append(" | trail");
                        }

                        if (effect.hasFlicker()) {
                            recipeString.append(" | flicker");
                        }
                    }
                }

                int power = fireworkMeta.getPower();
                if (power != 2) {
                    recipeString.append(Files.NL).append("@fireworkitem power ").append(power);
                }
            }

            if (meta instanceof FireworkEffectMeta) {
                FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) meta;
                if (fireworkEffectMeta.hasEffect()) {
                    FireworkEffect effect = fireworkEffectMeta.getEffect();
                    if (effect != null) {
                        FireworkEffect.Type type = effect.getType();
                        recipeString.append(Files.NL).append("@fireworkstaritem type ").append(type.name());

                        List<Color> colors = effect.getColors();
                        if (!colors.isEmpty()) {
                            recipeString.append(" | color ");
                            boolean first = true;
                            for (Color color : colors) {
                                if (!first) {
                                    recipeString.append(", ");
                                }
                                recipeString.append(color.getRed()).append(" ").append(color.getGreen()).append(" ").append(color.getBlue());

                                first = false;
                            }
                        }

                        List<Color> fadeColors = effect.getFadeColors();
                        if (!fadeColors.isEmpty()) {
                            recipeString.append(" | fadecolor ");
                            boolean first = true;
                            for (Color fadeColor : fadeColors) {
                                if (!first) {
                                    recipeString.append(", ");
                                }
                                recipeString.append(fadeColor.getRed()).append(" ").append(fadeColor.getGreen()).append(" ").append(fadeColor.getBlue());

                                first = false;
                            }
                        }

                        if (effect.hasTrail()) {
                            recipeString.append(" | trail");
                        }

                        if (effect.hasFlicker()) {
                            recipeString.append(" | flicker");
                        }
                    }
                }
            }

            if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
                Color color = leatherMeta.getColor();

                if (!color.equals(Bukkit.getItemFactory().getDefaultLeatherColor())) {
                    recipeString.append(Files.NL).append("@leathercolor ").append(color.getRed()).append(' ').append(color.getGreen()).append(' ').append(color.getBlue());
                }
            }

            if (meta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) meta;
                PotionData potionData = potionMeta.getBasePotionData();
                PotionType potionType = potionData.getType();

                recipeString.append(Files.NL).append("@potionitem type").append(potionType);
                if (potionData.isUpgraded()) {
                    recipeString.append(" | level 2");
                }
                if (potionData.isExtended()) {
                    recipeString.append(" | extended");
                }

                if (potionMeta.hasColor()) {
                    Color potionColor = potionMeta.getColor();
                    if (potionColor != null) {
                        recipeString.append(" | color ").append(potionColor.getRed()).append(" ").append(potionColor.getGreen()).append(" ").append(potionColor.getBlue());
                    }
                }

                if (potionMeta.hasCustomEffects()) {
                    List<PotionEffect> potionEffects = potionMeta.getCustomEffects();
                    for (PotionEffect effect : potionEffects) {
                        PotionEffectType effectType = effect.getType();
                        recipeString.append(Files.NL).append("@potionitem custom ").append(effectType);

                        int duration = effect.getDuration();
                        if (duration != 20) {
                            float durationInSeconds = (float) (duration / 20);
                            recipeString.append(" | duration ").append(durationInSeconds);
                        }
                        int amplifier = effect.getAmplifier();
                        if (amplifier != 0) {
                            recipeString.append(" | amplifier ").append(amplifier);
                        }
                        if (!effect.isAmbient()) {
                            recipeString.append(" | !ambient");
                        }
                        if (!effect.hasParticles()) {
                            recipeString.append(" | !particles");
                        }

                        if (Version.has1_13BasicSupport()) {
                            if (!effect.hasIcon()) {
                                recipeString.append(" | !icon");
                            }
                        }
                    }
                }
            }

            if (meta instanceof Repairable) {
                Repairable repairableMeta = (Repairable) meta;
                if (repairableMeta.hasRepairCost()) {
                    recipeString.append(Files.NL).append("@repaircost ").append(repairableMeta.getRepairCost());
                }
            }

            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                if (skullMeta.hasOwner()) {
                    OfflinePlayer player = skullMeta.getOwningPlayer();
                    if (player != null) {
                        recipeString.append(Files.NL).append("@skullowner ").append(player.getUniqueId());
                    }
                }

                // TODO: Handle texture somehow
            }

            if (!Version.has1_13BasicSupport() && meta instanceof SpawnEggMeta) {
                SpawnEggMeta spawnEggMeta = (SpawnEggMeta) meta;
                EntityType spawnedType = spawnEggMeta.getSpawnedType();
                if (spawnedType != null) {
                    recipeString.append(Files.NL).append("@spawnegg ").append(spawnedType.name());
                }
            }

            if (Supports.suspiciousStewMeta() && meta instanceof SuspiciousStewMeta) {
                SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) meta;
                if (stewMeta.hasCustomEffects()) {
                    List<PotionEffect> potionEffects = stewMeta.getCustomEffects();
                    for (PotionEffect effect : potionEffects) {
                        PotionEffectType effectType = effect.getType();
                        recipeString.append(Files.NL).append("@suspiciousstewitem ").append(effectType);

                        int duration = effect.getDuration();
                        if (duration != 20) {
                            float durationInSeconds = (float) (duration / 20);
                            recipeString.append(" | duration ").append(durationInSeconds);
                        }
                        int amplifier = effect.getAmplifier();
                        if (amplifier != 0) {
                            recipeString.append(" | amplifier ").append(amplifier);
                        }
                        if (!effect.isAmbient()) {
                            recipeString.append(" | !ambient");
                        }
                        if (!effect.hasParticles()) {
                            recipeString.append(" | !particles");
                        }

                        if (Version.has1_13BasicSupport()) {
                            if (!effect.hasIcon()) {
                                recipeString.append(" | !icon");
                            }
                        }
                    }
                }
            }
        }

        recipeString.append(Files.NL);
    }
}
