package haveric.recipeManager.flag;

import haveric.recipeManager.flag.flags.any.*;
import haveric.recipeManager.flag.flags.recipe.*;
import haveric.recipeManager.flag.flags.result.*;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.Version;
import org.bukkit.ChatColor;

public class FlagLoader {
    public FlagLoader() {
        loadDefaultFlags();
    }

    public void loadDefaultFlags() {
        FlagFactory.getInstance().initializeFlag(FlagType.BIOME, new FlagBiome(), FlagBit.NONE);
        FlagFactory.getInstance().initializeFlag(FlagType.BLOCK_POWERED, new FlagBlockPowered(), FlagBit.NO_VALUE_REQUIRED, "poweredblock", "blockpower", "redstonepowered");
        FlagFactory.getInstance().initializeFlag(FlagType.BROADCAST, new FlagBroadcast(), FlagBit.NONE, "announce", "msgall");
        FlagFactory.getInstance().initializeFlag(FlagType.COMMAND, new FlagCommand(), FlagBit.NONE, "cmd", "commands");
        FlagFactory.getInstance().initializeFlag(FlagType.COOLDOWN, new FlagCooldown(), FlagBit.NONE, "cooltime", "delay");
        FlagFactory.getInstance().initializeFlag(FlagType.EXPLODE, new FlagExplode(), FlagBit.NONE | FlagBit.NO_VALUE_REQUIRED, "explosion", "boom", "tnt");
        FlagFactory.getInstance().initializeFlag(FlagType.FOR_CHANCE, new FlagForChance(), FlagBit.NONE, "bychance", "chance");
        FlagFactory.getInstance().initializeFlag(FlagType.FOR_PERMISSION, new FlagForPermission(), FlagBit.NONE, "forperm");
        FlagFactory.getInstance().initializeFlag(FlagType.GAMEMODE, new FlagGameMode(), FlagBit.NONE, "needgm");
        FlagFactory.getInstance().initializeFlag(FlagType.GROUP, new FlagGroup(), FlagBit.NONE, "groups", "permissiongroup", "permgroup");
        FlagFactory.getInstance().initializeFlag(FlagType.HEIGHT, new FlagHeight(), FlagBit.NONE, "depth");
        FlagFactory.getInstance().initializeFlag(FlagType.HOLD_ITEM, new FlagHoldItem(), FlagBit.NONE, "hold");
        FlagFactory.getInstance().initializeFlag(FlagType.INGREDIENT_CONDITION, new FlagIngredientCondition(), FlagBit.NONE, "ingrcondition", "ingrcond", "ifingredient", "ifingr");
        FlagFactory.getInstance().initializeFlag(FlagType.INVENTORY, new FlagInventory(), FlagBit.NONE);
        FlagFactory.getInstance().initializeFlag(FlagType.KEEP_ITEM, new FlagKeepItem(), FlagBit.NONE, "returnitem", "replaceitem");
        FlagFactory.getInstance().initializeFlag(FlagType.LAUNCH_FIREWORK, new FlagLaunchFirework(), FlagBit.ONCE_PER_SHIFT, "setfirework");
        FlagFactory.getInstance().initializeFlag(FlagType.LIGHT_LEVEL, new FlagLightLevel(), FlagBit.NONE, "blocklight", "sunlight", "light");
        FlagFactory.getInstance().initializeFlag(FlagType.MESSAGE, new FlagMessage(), FlagBit.NONE, "craftmsg", "msg");
        FlagFactory.getInstance().initializeFlag(FlagType.MOD_EXP, new FlagModExp(), FlagBit.NONE, "expmod", "modxp", "xpmod", "exp", "xp", "giveexp", "givexp", "takeexp", "takexp");
        FlagFactory.getInstance().initializeFlag(FlagType.MOD_LEVEL, new FlagModLevel(), FlagBit.NONE, "levelmod", "setlevel", "level");
        FlagFactory.getInstance().initializeFlag(FlagType.MOD_MONEY, new FlagModMoney(), FlagBit.NONE, "moneymod", "setmoney", "money");
        FlagFactory.getInstance().initializeFlag(FlagType.NEED_EXP, new FlagNeedExp(), FlagBit.NONE, "needxp", "reqexp", "expreq", "reqxp", "xpreq");
        FlagFactory.getInstance().initializeFlag(FlagType.NEED_LEVEL, new FlagNeedLevel(), FlagBit.NONE, "reqlevel", "levelreq");
        FlagFactory.getInstance().initializeFlag(FlagType.NEED_MONEY, new FlagNeedMoney(), FlagBit.NONE, "reqmoney", "moneyreq");
        FlagFactory.getInstance().initializeFlag(FlagType.PERMISSION, new FlagPermission(), FlagBit.NONE, "permissions", "perm");
        FlagFactory.getInstance().initializeFlag(FlagType.POTION_EFFECT, new FlagPotionEffect(), FlagBit.NONE, "potionfx");
        FlagFactory.getInstance().initializeFlag(FlagType.SECRET, new FlagSecret(), FlagBit.NO_VALUE_REQUIRED | FlagBit.NO_FOR);
        FlagFactory.getInstance().initializeFlag(FlagType.SET_BLOCK, new FlagSetBlock(), FlagBit.NONE, "changeblock");
        FlagFactory.getInstance().initializeFlag(FlagType.SOUND, new FlagSound(), FlagBit.ONCE_PER_SHIFT, "playsound");
        FlagFactory.getInstance().initializeFlag(FlagType.SPAWN_PARTICLE, new FlagSpawnParticle(), FlagBit.ONCE_PER_SHIFT, "particle");
        FlagFactory.getInstance().initializeFlag(FlagType.TEMPERATURE, new FlagTemperature(), FlagBit.NONE, "temp");
        FlagFactory.getInstance().initializeFlag(FlagType.SUMMON, new FlagSummon(), FlagBit.NONE, "spawn", "creature", "mob", "animal");
        FlagFactory.getInstance().initializeFlag(FlagType.WEATHER, new FlagWeather(), FlagBit.NONE);
        FlagFactory.getInstance().initializeFlag(FlagType.WORLD, new FlagWorld(), FlagBit.NONE, "needworld", "worlds");

        // TELEPORT(FlagTeleport(), "tpto", "goto"), // TODO finish flag
        // REALTIME(FlagRealTime(), FlagBit.NONE, "time", "date"),
        // ONLINETIME(FlagOnlineTime(), FlagBit.NONE, "playtime", "onlinefor"),
        // WORLDTIME(FlagWorldTime(), FlagBit.NONE),
        // PROXIMITY(FlagProximity(), FlagBit.NONE, "distance", "nearby"),
        // DEBUG(FlagDebug(), FlagBit.NO_VALUE_REQUIRED | FlagBit.NO_FOR | FlagBit.NO_SKIP_PERMISSION, "monitor", "log"),

        // Recipe only flags
        FlagFactory.getInstance().initializeFlag(FlagType.ADD_TO_BOOK, new FlagAddToBook(), FlagBit.RECIPE | FlagBit.NO_FOR | FlagBit.NO_SKIP_PERMISSION, "recipebook");
        FlagFactory.getInstance().initializeFlag(FlagType.DISPLAY_RESULT, new FlagDisplayResult(), FlagBit.RECIPE, "resultdisplay", "showresult");
        FlagFactory.getInstance().initializeFlag(FlagType.FAIL_MESSAGE, new FlagFailMessage(), FlagBit.RECIPE, "failmsg");
        FlagFactory.getInstance().initializeFlag(FlagType.INDIVIDUAL_RESULTS, new FlagIndividualResults(), FlagBit.RECIPE | FlagBit.NO_VALUE_REQUIRED, "individual");
        FlagFactory.getInstance().initializeFlag(FlagType.OVERRIDE, new FlagOverride(), FlagBit.RECIPE | FlagBit.NO_FOR | FlagBit.NO_VALUE_REQUIRED | FlagBit.NO_SKIP_PERMISSION, "edit", "overwrite", "supercede", "replace");
        FlagFactory.getInstance().initializeFlag(FlagType.REMOVE, new FlagRemove(), FlagBit.RECIPE | FlagBit.NO_FOR | FlagBit.NO_VALUE_REQUIRED | FlagBit.NO_SKIP_PERMISSION, "delete");
        FlagFactory.getInstance().initializeFlag(FlagType.RESTRICT, new FlagRestrict(), FlagBit.RECIPE | FlagBit.NO_VALUE_REQUIRED, "disable", "denied", "deny");

        // Result only flags
        FlagFactory.getInstance().initializeFlag(FlagType.NO_RESULT, new FlagNoResult(), FlagBit.RESULT | FlagBit.NO_FOR | FlagBit.NO_VALUE_REQUIRED);

        FlagFactory.getInstance().initializeFlag(FlagType.APPLY_ENCHANTMENT, new FlagApplyEnchantment(), FlagBit.RESULT | FlagBit.NO_VALUE_REQUIRED, "applyenchant", "applyenchantments", "applyenchants");
        FlagFactory.getInstance().initializeFlag(FlagType.BANNER_ITEM, new FlagBannerItem(), FlagBit.RESULT, "banner");
        FlagFactory.getInstance().initializeFlag(FlagType.BOOK_ITEM, new FlagBookItem(), FlagBit.RESULT, "book");
        FlagFactory.getInstance().initializeFlag(FlagType.CLONE_INGREDIENT, new FlagCloneIngredient(), FlagBit.RESULT | FlagBit.NONE, "clone", "copy", "copyingredient"); // TODO finish
        FlagFactory.getInstance().initializeFlag(FlagType.CUSTOM_MODEL_DATA, new FlagItemName(), FlagBit.RESULT, "modeldata");
        FlagFactory.getInstance().initializeFlag(FlagType.ENCHANTED_BOOK, new FlagEnchantedBook(), FlagBit.RESULT, "enchantbook", "enchantingbook");
        FlagFactory.getInstance().initializeFlag(FlagType.ENCHANT_ITEM, new FlagEnchantItem(), FlagBit.RESULT, "enchant", "enchantment");
        FlagFactory.getInstance().initializeFlag(FlagType.FIREWORK_ITEM, new FlagFireworkItem(), FlagBit.RESULT, "firework", "fireworkrocket");
        FlagFactory.getInstance().initializeFlag(FlagType.FIREWORK_STAR_ITEM, new FlagFireworkStarItem(), FlagBit.RESULT, "fireworkstar", "fireworkchargeitem", "fireworkcharge", "fireworkeffect");
        FlagFactory.getInstance().initializeFlag(FlagType.GET_RECIPE_BOOK, new FlagGetRecipeBook(), FlagBit.RESULT | FlagBit.NONE, "getbook", "bookresult");
        FlagFactory.getInstance().initializeFlag(FlagType.HIDE, new FlagHide(), FlagBit.RESULT);
        if (Version.has1_13Support()) {
            FlagFactory.getInstance().initializeFlag(FlagType.ITEM_ATTRIBUTE, new FlagItemAttribute(), FlagBit.RESULT, "attribute");
        }
        FlagFactory.getInstance().initializeFlag(FlagType.ITEM_LORE, new FlagItemLore(), FlagBit.RESULT | FlagBit.NO_VALUE_REQUIRED, "lore", "itemdesc");
        FlagFactory.getInstance().initializeFlag(FlagType.ITEM_NAME, new FlagItemName(), FlagBit.RESULT, "name", "displayname");
        FlagFactory.getInstance().initializeFlag(FlagType.ITEM_UNBREAKABLE, new FlagItemUnbreakable(), FlagBit.RESULT | FlagBit.NO_FALSE | FlagBit.NO_VALUE_REQUIRED, "unbreakable");
        FlagFactory.getInstance().initializeFlag(FlagType.LEATHER_COLOR, new FlagLeatherColor(), FlagBit.RESULT, "leathercolour", "color", "colour");
        FlagFactory.getInstance().initializeFlag(FlagType.LOCALIZED_NAME, new FlagLocalizedName(), FlagBit.RESULT);
        // MAPITEM(FlagMapItem(), FlagBit.RESULT, "map"), // TODO finish this flag
        FlagFactory.getInstance().initializeFlag(FlagType.MONSTER_SPAWNER, new FlagMonsterSpawner(), FlagBit.RESULT, "spawner", "mobspawner");
        FlagFactory.getInstance().initializeFlag(FlagType.POTION_ITEM, new FlagPotionItem(), FlagBit.RESULT, "potion");
        FlagFactory.getInstance().initializeFlag(FlagType.REPAIR_COST, new FlagRepairCost(), FlagBit.RESULT);
        FlagFactory.getInstance().initializeFlag(FlagType.SKULL_OWNER, new FlagSkullOwner(), FlagBit.RESULT, "skullitem");
        FlagFactory.getInstance().initializeFlag(FlagType.SPAWN_EGG, new FlagSpawnEgg(), FlagBit.RESULT, "monsteregg", "egg");
    }

    public void loadCustomFlag(String mainAlias, Flag newFlag, int bits, String... aliases) {
        if (FlagFactory.getInstance().isInitialized()) {
            MessageSender.getInstance().info(ChatColor.RED + "Custom flags must be added in your onEnable() method.");
        } else {
            FlagFactory.getInstance().initializeFlag(mainAlias, newFlag, bits, aliases);
        }
    }
}
