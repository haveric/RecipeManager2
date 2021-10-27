package haveric.recipeManager.tools;

import com.google.common.collect.Multimap;
import haveric.recipeManager.Files;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ToolsFlag {

    public static void parseItemMeta(ItemStack item, StringBuilder recipeString) {
        if (item.getEnchantments().size() > 0) {
            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                recipeString.append(Files.NL).append("@enchant ").append(entry.getKey().getName()).append(' ').append(entry.getValue());
            }
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                recipeString.append(Files.NL).append("@name ").append(meta.getDisplayName());
            }

            if (meta.hasLore()) {
                List<String> lores = meta.getLore();
                if (lores != null) {
                    for (String lore : lores) {
                        recipeString.append(Files.NL).append("@lore ").append(lore);
                    }
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

            if (Version.has1_14Support() && meta.hasCustomModelData()) {
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

            if (Supports.axolotlBucketMeta() && meta instanceof AxolotlBucketMeta) {
                AxolotlBucketMeta axolotlBucketMeta = (AxolotlBucketMeta) meta;
                if (axolotlBucketMeta.hasVariant()) {
                    recipeString.append(Files.NL).append("@axolotlbucketitem ").append(axolotlBucketMeta.getVariant());
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

            if (Version.has1_17Support() && meta instanceof BundleMeta) {
                BundleMeta bundleMeta = (BundleMeta) meta;
                List<ItemStack> bundleItems = bundleMeta.getItems();
                for (ItemStack bundleItem : bundleItems) {
                    recipeString.append(Files.NL).append("@bundle ").append(bundleItem.getType());
                    recipeString.append(":").append(bundleItem.getDurability()).append(":").append(bundleItem.getAmount());
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
                    for (Map.Entry<Enchantment, Integer> entry : storedEnchants.entrySet()) {
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

            if (Supports.knowledgeBookMeta() && meta instanceof KnowledgeBookMeta) {
                KnowledgeBookMeta knowledgeBookMeta = (KnowledgeBookMeta) meta;
                if (knowledgeBookMeta.hasRecipes()) {
                    List<NamespacedKey> recipes = knowledgeBookMeta.getRecipes();
                    if (!recipes.isEmpty()) {
                        recipeString.append(Files.NL).append("@knowledgebook ");
                        boolean first = true;
                        for (NamespacedKey recipe : recipes) {
                            if (!first) {
                                recipeString.append(", ");
                            }
                            String namespace = recipe.getNamespace();
                            if (!namespace.equals(NamespacedKey.MINECRAFT)) {
                                recipeString.append(namespace).append(":");
                            }
                            String key = recipe.getKey();
                            recipeString.append(key);

                            first = false;
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

            if (meta instanceof MapMeta) {
                MapMeta mapMeta = (MapMeta) meta;
                recipeString.append(Files.NL).append("@map scaling");
                if (!mapMeta.isScaling()) {
                    recipeString.append(" false");
                }

                if (Version.has1_11Support()) {
                    if (mapMeta.hasLocationName()) {
                        String locationName = mapMeta.getLocationName();
                        recipeString.append(" | locationname ").append(locationName);
                    }

                    if (mapMeta.hasColor()) {
                        Color mapColor = mapMeta.getColor();
                        if (mapColor != null) {
                            recipeString.append(" | color ").append(mapColor.getRed()).append(mapColor.getGreen()).append(mapColor.getBlue());
                        }
                    }
                }

                if (Version.has1_13Support()) {
                    MapView mapView = mapMeta.getMapView();
                    if (mapView != null) {
                        World world = mapView.getWorld();
                        if (world != null) {
                            recipeString.append(" | world ").append(world.getName());
                        }

                        int centerX = mapView.getCenterX();
                        recipeString.append(" | centerx ").append(centerX);

                        int centerZ = mapView.getCenterZ();
                        recipeString.append(" | centerz ").append(centerZ);

                        MapView.Scale scale = mapView.getScale();
                        recipeString.append(" | scale ").append(scale.name());


                        if (mapView.isLocked()) {
                            recipeString.append(" | locked");
                        }

                        if (mapView.isTrackingPosition()) {
                            recipeString.append(" | trackingposition");
                        }

                        if (mapView.isUnlimitedTracking()) {
                            recipeString.append(" | unlimitedtracking");
                        }

                        /* TODO: Custom renderer, warn user?
                        if (mapView.isVirtual()) {

                        }
                        */
                    }
                }
            }

            if (meta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) meta;
                PotionData potionData = potionMeta.getBasePotionData();
                PotionType potionType = potionData.getType();

                recipeString.append(Files.NL).append("@potionitem type ").append(potionType);
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
                        recipeString.append(Files.NL).append("@potionitem custom ");

                        parsePotionEffectForItemMeta(recipeString, effect);
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
                        recipeString.append(Files.NL).append("@suspiciousstewitem ");

                        parsePotionEffectForItemMeta(recipeString, effect);
                    }
                }
            }

            if (Supports.tropicalFishBucketMeta() && meta instanceof TropicalFishBucketMeta) {
                TropicalFishBucketMeta tropicalFishBucketMeta = (TropicalFishBucketMeta) meta;
                if (tropicalFishBucketMeta.hasVariant()) {
                    DyeColor bodyColor = tropicalFishBucketMeta.getBodyColor();
                    TropicalFish.Pattern pattern = tropicalFishBucketMeta.getPattern();
                    DyeColor patternColor = tropicalFishBucketMeta.getPatternColor();

                    recipeString.append(Files.NL).append("@tropicalfishbucket bodycolor ").append(bodyColor.name()).append(" | pattern ").append(pattern.name()).append(" | patterncolor ").append(patternColor.name());
                }
            }
        }

        recipeString.append(Files.NL);
    }

    private static void parsePotionEffectForItemMeta(StringBuilder recipeString, PotionEffect effect) {
        recipeString.append(effect.getType());

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
