package haveric.recipeManager.tools;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerProfile;

import static org.bukkit.Tag.REGISTRY_BLOCKS;

public class Supports {
    static boolean itemFlagHideArmorTrim = false;
    static boolean allayDuplication = false;
    static boolean categories = false;
    static boolean campfireStartEvent = false;
    static boolean experimental1_20 = false;
    static boolean playerProfile = false;
    static boolean basePotionType = false;
    static boolean shortGrassMaterial = false;
    static boolean materialIsCompostable = false;
    static boolean fireworkMetaIncreasedPower = false;
    static boolean supports1_21_2 = false;

    public static void init() {
        checkItemFlagHideArmorTrimSupport();
        checkAllayDuplication();
        checkCategories();
        checkCampfireStartEvent();
        checkExperimental1_20();
        checkPlayerProfile();
        checkBasePotionType();
        checkShortGrassMaterial();
        checkMaterialIsCompostable();
        checkFireworkMetaIncreasedPower();
        checkSupports1_21_2();
    }

    // 1.18.1
    private static void checkPlayerProfile() {
        try {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

            if (skullMeta != null) {
                @SuppressWarnings("unused")
                PlayerProfile profile = skullMeta.getOwnerProfile();

                playerProfile = true;
            }
        } catch (NoSuchMethodError | NoClassDefFoundError | NoSuchFieldError e) {
            playerProfile = false;
        }
    }

    // 1.19.2
    private static void checkAllayDuplication() {
        try {
            @SuppressWarnings("unused")
            CreatureSpawnEvent.SpawnReason spawnReason = CreatureSpawnEvent.SpawnReason.DUPLICATION;
            allayDuplication = true;
        } catch (NoSuchFieldError e) {
            allayDuplication = false;
        }
    }

    // 1.19.3
    private static void checkCategories() {
        try {
            @SuppressWarnings("unused")
            String categoryName = CraftingBookCategory.class.getName();
            categories = true;
        } catch (NoClassDefFoundError e) {
            categories = false;
        }
    }

    // 1.19.3+
    private static void checkCampfireStartEvent() {
        try {
            @SuppressWarnings("unused")
            String eventName = CampfireStartEvent.class.getName();
            campfireStartEvent = true;
        } catch (NoClassDefFoundError e) {
            campfireStartEvent = false;
        }
    }

    // 1.19.4
    private static void checkItemFlagHideArmorTrimSupport() {
        try {
            @SuppressWarnings("unused")
            ItemFlag flag = ItemFlag.HIDE_ARMOR_TRIM;
            itemFlagHideArmorTrim = true;
        } catch (NoSuchFieldError e) {
            itemFlagHideArmorTrim = false;
        }
    }

    // 1.20 experimental
    private static void checkExperimental1_20() {
        try {
            Iterable<Tag<Material>> blockTags = Bukkit.getTags(REGISTRY_BLOCKS, Material.class);
            for (Tag<Material> tag : blockTags) {
                if (tag.getKey().getKey().equals("bamboo_blocks")) {
                    experimental1_20 = true;
                    break;
                }
            }
        } catch (NoSuchMethodError e) {
            experimental1_20 = false;
        }
    }

    // 1.20.2
    private static void checkBasePotionType() {
        try {
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
            potionMeta.setBasePotionType(PotionType.MUNDANE);

            basePotionType = true;
        } catch (NoSuchMethodError e) {
            basePotionType = false;
        }
    }

    // 1.20.3
    private static void checkShortGrassMaterial() {
        try {
            @SuppressWarnings("unused")
            ItemStack shortGrass = new ItemStack(Material.SHORT_GRASS);
            shortGrassMaterial = true;
        } catch (NoSuchFieldError | NoClassDefFoundError e) {
            shortGrassMaterial = false;
        }
    }

    // 1.20.5
    private static void checkMaterialIsCompostable() {
        try {
            Material material = Material.OAK_LOG;
            @SuppressWarnings("unused")
            boolean isCompostable = material.isCompostable();
            materialIsCompostable = true;
        } catch (NoSuchMethodError e) {
            materialIsCompostable = false;
        }
    }

    // 1.21.1
    private static void checkFireworkMetaIncreasedPower() {
        try {
            ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
            FireworkMeta meta = (FireworkMeta) firework.getItemMeta();
            @SuppressWarnings("unused")
            boolean hasPower = meta.hasPower();
            fireworkMetaIncreasedPower = true;
        } catch (NoSuchMethodError e) {
            fireworkMetaIncreasedPower = false;
        }
    }

    // 1.21.2
    private static void checkSupports1_21_2() {
        supports1_21_2 = !Attribute.class.isEnum();
    }

    public static boolean itemFlagHideArmorTrim() {
        return itemFlagHideArmorTrim;
    }

    public static boolean allayDuplication() {
        return allayDuplication;
    }
    public static boolean categories() {
        return categories;
    }

    public static boolean campfireStartEvent() {
        return campfireStartEvent;
    }

    public static boolean experimental1_20() {
        return experimental1_20;
    }

    public static boolean playerProfile() {
        return playerProfile;
    }

    public static boolean basePotionType() {
        return basePotionType;
    }

    public static boolean shortGrassMaterial() {
        return shortGrassMaterial;
    }

    public static boolean materialIsCompostable() {
        return materialIsCompostable;
    }

    public static boolean fireworkMetaIncreasedPower() {
        return fireworkMetaIncreasedPower;
    }

    public static boolean supports1_21_2() {
        return supports1_21_2;
    }
}
