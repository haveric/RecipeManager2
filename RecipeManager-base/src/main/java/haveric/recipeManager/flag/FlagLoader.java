package haveric.recipeManager.flag;

import haveric.recipeManager.flag.flags.any.*;
import haveric.recipeManager.flag.flags.recipe.*;
import haveric.recipeManager.flag.flags.result.*;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.Version;
import org.bukkit.ChatColor;

public class FlagLoader {
    public FlagLoader() {
        loadDefaultFlags(false);
    }

    public FlagLoader(boolean force) {
        loadDefaultFlags(force);
    }

    private void loadDefaultFlags(boolean force) {
        loadFlag(FlagType.BIOME, new FlagBiome(), FlagBit.NO_DELAY);
        loadFlag(FlagType.BLOCK_POWERED, new FlagBlockPowered(), FlagBit.NO_VALUE_REQUIRED | FlagBit.NO_DELAY, "poweredblock", "blockpower", "redstonepowered");
        loadFlag(FlagType.BROADCAST, new FlagBroadcast(), FlagBit.NONE, "announce", "msgall");
        loadFlag(FlagType.COMMAND, new FlagCommand(), FlagBit.NONE, "cmd", "commands");
        loadFlag(FlagType.COOLDOWN, new FlagCooldown(), FlagBit.NO_DELAY, "cooltime");
        loadFlag(FlagType.EXPLODE, new FlagExplode(), FlagBit.NONE | FlagBit.NO_VALUE_REQUIRED, "explosion", "boom", "tnt");
        loadFlag(FlagType.FOR_CHANCE, new FlagForChance(), FlagBit.NONE, "bychance", "chance");
        loadFlag(FlagType.FOR_DELAY, new FlagForDelay(), FlagBit.NONE, "delay");
        loadFlag(FlagType.FOR_PERMISSION, new FlagForPermission(), FlagBit.NONE, "forperm");
        loadFlag(FlagType.FOR_REPEAT, new FlagForRepeat(), FlagBit.NONE, "repeat");
        loadFlag(FlagType.GAMEMODE, new FlagGameMode(), FlagBit.NO_DELAY, "needgm");
        loadFlag(FlagType.GROUP, new FlagGroup(), FlagBit.NO_DELAY, "groups", "permissiongroup", "permgroup");
        loadFlag(FlagType.HEIGHT, new FlagHeight(), FlagBit.NO_DELAY, "depth");
        loadFlag(FlagType.HOLD_ITEM, new FlagHoldItem(), FlagBit.NO_DELAY, "hold");
        loadFlag(FlagType.ITEM_LORE, new FlagItemLore(), FlagBit.NO_VALUE_REQUIRED | FlagBit.NO_DELAY, "lore", "itemdesc");
        loadFlag(FlagType.ITEM_NAME, new FlagItemName(), FlagBit.NO_DELAY, "name", "displayname");
        loadFlag(FlagType.ITEM_NBT, new FlagItemNBT(), FlagBit.NO_DELAY, "nbt");
        loadFlag(FlagType.INGREDIENT_CONDITION, new FlagIngredientCondition(), FlagBit.NO_DELAY, "ingrcondition", "ingrcond", "ifingredient", "ifingr");
        loadFlag(FlagType.INVENTORY, new FlagInventory(), FlagBit.NO_DELAY);
        loadFlag(FlagType.KEEP_ITEM, new FlagKeepItem(), FlagBit.NO_DELAY, "returnitem", "replaceitem");
        loadFlag(FlagType.LAUNCH_FIREWORK, new FlagLaunchFirework(), FlagBit.ONCE_PER_SHIFT, "setfirework");
        loadFlag(FlagType.LIGHT_LEVEL, new FlagLightLevel(), FlagBit.NO_DELAY, "blocklight", "sunlight", "light");
        loadFlag(FlagType.MESSAGE, new FlagMessage(), FlagBit.NONE, "craftmsg", "msg");
        loadFlag(FlagType.MOD_EXP, new FlagModExp(), FlagBit.NONE, "expmod", "modxp", "xpmod", "exp", "xp", "giveexp", "givexp", "takeexp", "takexp");
        loadFlag(FlagType.MOD_LEVEL, new FlagModLevel(), FlagBit.NONE, "levelmod", "setlevel", "level");
        loadFlag(FlagType.MOD_MONEY, new FlagModMoney(), FlagBit.NONE, "moneymod", "setmoney", "money");
        loadFlag(FlagType.NEED_EXP, new FlagNeedExp(), FlagBit.NO_DELAY, "needxp", "reqexp", "expreq", "reqxp", "xpreq");
        loadFlag(FlagType.NEED_LEVEL, new FlagNeedLevel(), FlagBit.NO_DELAY, "reqlevel", "levelreq");
        loadFlag(FlagType.NEED_MONEY, new FlagNeedMoney(), FlagBit.NO_DELAY, "reqmoney", "moneyreq");
        loadFlag(FlagType.PERMISSION, new FlagPermission(), FlagBit.NO_DELAY, "permissions", "perm");
        loadFlag(FlagType.POTION_EFFECT, new FlagPotionEffect(), FlagBit.NO_DELAY, "potionfx");
        loadFlag(FlagType.SECRET, new FlagSecret(), FlagBit.NO_VALUE_REQUIRED | FlagBit.NO_FOR | FlagBit.NO_DELAY);
        loadFlag(FlagType.SET_BLOCK, new FlagSetBlock(), FlagBit.NO_DELAY, "changeblock");
        loadFlag(FlagType.SOUND, new FlagSound(), FlagBit.ONCE_PER_SHIFT, "playsound");
        loadFlag(FlagType.SPAWN_PARTICLE, new FlagSpawnParticle(), FlagBit.ONCE_PER_SHIFT, "particle");
        loadFlag(FlagType.SUMMON, new FlagSummon(), FlagBit.NONE, "spawn", "creature", "mob", "animal");
        loadFlag(FlagType.TEMPERATURE, new FlagTemperature(), FlagBit.NO_DELAY, "temp");
        loadFlag(FlagType.WEATHER, new FlagWeather(), FlagBit.NO_DELAY);
        loadFlag(FlagType.WORLD, new FlagWorld(), FlagBit.NO_DELAY, "needworld", "worlds");

        // TELEPORT(FlagTeleport(), "tpto", "goto"), // TODO finish flag
        // REALTIME(FlagRealTime(), FlagBit.NONE, "time", "date"),
        // ONLINETIME(FlagOnlineTime(), FlagBit.NONE, "playtime", "onlinefor"),
        // WORLDTIME(FlagWorldTime(), FlagBit.NONE),
        // PROXIMITY(FlagProximity(), FlagBit.NONE, "distance", "nearby"),
        // DEBUG(FlagDebug(), FlagBit.NO_VALUE_REQUIRED | FlagBit.NO_FOR | FlagBit.NO_SKIP_PERMISSION, "monitor", "log"),

        // Recipe only flags
        loadFlag(FlagType.ADD_TO_BOOK, new FlagAddToBook(), FlagBit.RECIPE | FlagBit.NO_FOR | FlagBit.NO_SKIP_PERMISSION, "recipebook");
        loadFlag(FlagType.DISPLAY_RESULT, new FlagDisplayResult(), FlagBit.RECIPE, "resultdisplay", "showresult");
        loadFlag(FlagType.FAIL_MESSAGE, new FlagFailMessage(), FlagBit.RECIPE, "failmsg");
        loadFlag(FlagType.INDIVIDUAL_RESULTS, new FlagIndividualResults(), FlagBit.RECIPE | FlagBit.NO_VALUE_REQUIRED, "individual");
        loadFlag(FlagType.OVERRIDE, new FlagOverride(), FlagBit.RECIPE | FlagBit.NO_FOR | FlagBit.NO_VALUE_REQUIRED | FlagBit.NO_SKIP_PERMISSION, "edit", "overwrite", "supercede", "replace");
        loadFlag(FlagType.REMOVE, new FlagRemove(), FlagBit.RECIPE | FlagBit.NO_FOR | FlagBit.NO_VALUE_REQUIRED | FlagBit.NO_SKIP_PERMISSION, "delete");
        loadFlag(FlagType.RESTRICT, new FlagRestrict(), FlagBit.RECIPE | FlagBit.NO_VALUE_REQUIRED, "disable", "denied", "deny");

        // Result only flags
        loadFlag(FlagType.NO_RESULT, new FlagNoResult(), FlagBit.RESULT | FlagBit.NO_FOR | FlagBit.NO_VALUE_REQUIRED);

        loadFlag(FlagType.APPLY_ENCHANTMENT, new FlagApplyEnchantment(), FlagBit.RESULT | FlagBit.NO_VALUE_REQUIRED, "applyenchant", "applyenchantments", "applyenchants");
        loadFlag(FlagType.BANNER_ITEM, new FlagBannerItem(), FlagBit.RESULT, "banner");
        loadFlag(FlagType.BOOK_ITEM, new FlagBookItem(), FlagBit.RESULT, "book");
        loadFlag(FlagType.CLONE_INGREDIENT, new FlagCloneIngredient(), FlagBit.RESULT | FlagBit.NONE, "clone", "copy", "copyingredient"); // TODO finish
        loadFlag(FlagType.CUSTOM_MODEL_DATA, new FlagCustomModelData(), FlagBit.RESULT, "modeldata");
        loadFlag(FlagType.ENCHANTED_BOOK, new FlagEnchantedBook(), FlagBit.RESULT, "enchantbook", "enchantingbook");
        loadFlag(FlagType.ENCHANT_ITEM, new FlagEnchantItem(), FlagBit.RESULT, "enchant", "enchantment");
        loadFlag(FlagType.FIREWORK_ITEM, new FlagFireworkItem(), FlagBit.RESULT, "firework", "fireworkrocket");
        loadFlag(FlagType.FIREWORK_STAR_ITEM, new FlagFireworkStarItem(), FlagBit.RESULT, "fireworkstar", "fireworkchargeitem", "fireworkcharge", "fireworkeffect");
        loadFlag(FlagType.GET_RECIPE_BOOK, new FlagGetRecipeBook(), FlagBit.RESULT | FlagBit.NONE, "getbook", "bookresult");
        loadFlag(FlagType.HIDE, new FlagHide(), FlagBit.RESULT);
        if (Version.has1_13BasicSupport()) {
            loadFlag(FlagType.ITEM_ATTRIBUTE, new FlagItemAttribute(), FlagBit.RESULT, "attribute");
        }
        loadFlag(FlagType.ITEM_UNBREAKABLE, new FlagItemUnbreakable(), FlagBit.RESULT | FlagBit.NO_FALSE | FlagBit.NO_VALUE_REQUIRED, "unbreakable");
        loadFlag(FlagType.LEATHER_COLOR, new FlagLeatherColor(), FlagBit.RESULT, "leathercolour", "color", "colour");
        loadFlag(FlagType.LOCALIZED_NAME, new FlagLocalizedName(), FlagBit.RESULT);
        // MAPITEM(FlagMapItem(), FlagBit.RESULT, "map"), // TODO finish this flag
        loadFlag(FlagType.MONSTER_SPAWNER, new FlagMonsterSpawner(), FlagBit.RESULT, "spawner", "mobspawner");
        loadFlag(FlagType.POTION_ITEM, new FlagPotionItem(), FlagBit.RESULT, "potion");
        loadFlag(FlagType.REPAIR_COST, new FlagRepairCost(), FlagBit.RESULT);
        loadFlag(FlagType.SKULL_OWNER, new FlagSkullOwner(), FlagBit.RESULT, "skullitem, skull, head");

        if (!Version.has1_13BasicSupport() || force) {
            loadFlag(FlagType.SPAWN_EGG, new FlagSpawnEgg(), FlagBit.RESULT, "monsteregg", "egg");
        }

        if (Version.has1_14PlusSupport()) {
            loadFlag(FlagType.SUSPICIOUS_STEW_ITEM, new FlagSuspiciousStewItem(), FlagBit.RESULT, "suspicioussoupitem", "suspiciousstew", "suspicioussoup");
        }
    }

    public void loadFlag(String mainAlias, Flag newFlag, String aliases) {
        loadFlag(mainAlias, newFlag, FlagBit.NONE, aliases);
    }

    public void loadFlag(String mainAlias, Flag newFlag, int bits, String... aliases) {
        if (FlagFactory.getInstance().isInitialized()) {
            MessageSender.getInstance().info(ChatColor.RED + "Custom flags must be added in your onEnable() method.");
        } else {
            FlagFactory.getInstance().initializeFlag(mainAlias, newFlag, bits, aliases);
        }
    }
}
