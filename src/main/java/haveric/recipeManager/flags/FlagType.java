package haveric.recipeManager.flags;

import haveric.recipeManager.Messages;
import haveric.recipeManager.Perms;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum FlagType {
    // Shared flags
    COMMAND(FlagCommand.class, Bit.NONE, "cmd", "commands"),
    KEEPITEM(FlagKeepItem.class, Bit.NO_SHIFT, "returnitem", "replaceitem"),
    INGREDIENTCONDITION(FlagIngredientCondition.class, Bit.NO_SHIFT, "ingrcondition", "ingrcond", "ifingredient", "ifingr"),
    PERMISSION(FlagPermission.class, Bit.NONE, "permissions", "perm"),
    FORPERMISSION(FlagForPermission.class, Bit.NO_SHIFT, "forperm"),
    FORCHANCE(FlagForChance.class, Bit.NO_SHIFT, "bychance", "chance"),
    GROUP(FlagGroup.class, Bit.NONE, "groups", "permissiongroup", "permgroup"),
    WORLD(FlagWorld.class, Bit.NONE, "needworld", "worlds"),
    HEIGHT(FlagHeight.class, Bit.NONE, "depth"),
    MODEXP(FlagModExp.class, Bit.NO_SHIFT, "expmod", "modxp", "xpmod", "exp", "xp", "giveexp", "givexp", "takeexp", "takexp"),
    NEEDEXP(FlagNeedExp.class, Bit.NONE, "needxp", "reqexp", "expreq", "reqxp", "xpreq"),
    MODLEVEL(FlagModLevel.class, Bit.NO_SHIFT, "levelmod", "setlevel", "level"),
    NEEDLEVEL(FlagNeedLevel.class, Bit.NONE, "reqlevel", "levelreq"),
    MODMONEY(FlagModMoney.class, Bit.NO_SHIFT, "moneymod", "setmoney", "money"),
    NEEDMONEY(FlagNeedMoney.class, Bit.NONE, "reqmoney", "moneyreq"),
    COOLDOWN(FlagCooldown.class, Bit.NO_SHIFT, "cooltime", "delay"),
    HOLDITEM(FlagHoldItem.class, Bit.NONE, "hold"),
    GAMEMODE(FlagGameMode.class, Bit.NONE, "needgm"),
    LIGHTLEVEL(FlagLightLevel.class, Bit.NONE, "blocklight", "sunlight", "light"),
    BIOME(FlagBiome.class, Bit.NONE),
    WEATHER(FlagWeather.class, Bit.NONE),
    EXPLODE(FlagExplode.class, Bit.NO_SHIFT | Bit.NO_VALUE, "explosion", "boom", "tnt"),
    SOUND(FlagSound.class, Bit.NO_SHIFT, "playsound"),
    SUMMON(FlagSummon.class, Bit.NO_SHIFT, "spawn", "creature", "mob", "animal"),
    BLOCKPOWERED(FlagBlockPowered.class, Bit.NO_VALUE, "poweredblock", "blockpower", "redstonepowered"),
    POTIONEFFECT(FlagPotionEffect.class, Bit.NONE, "potionfx"),
    LAUNCHFIREWORK(FlagLaunchFirework.class, Bit.NO_SHIFT, "setfirework"),
    SETBLOCK(FlagSetBlock.class, Bit.NO_SHIFT, "changeblock"),
    MESSAGE(FlagMessage.class, Bit.NONE, "craftmsg", "msg"),
    BROADCAST(FlagBroadcast.class, Bit.NONE, "announce", "msgall"),
    SECRET(FlagSecret.class, Bit.NO_VALUE | Bit.NO_FOR, "hide"),
    TEMPERATURE(FlagTemperature.class, Bit.NONE, "temp"),
    // TELEPORT(FlagTeleport.class, Bit.NO_SHIFT, "tpto", "goto"), // TODO finish flag
    // REALTIME(FlagRealTime.class, Bit.NONE, "time", "date"),
    // ONLINETIME(FlagOnlineTime.class, Bit.NONE, "playtime", "onlinefor"),
    // WORLDTIME(FlagWorldTime.class, Bit.NONE),
    // PROXIMITY(FlagProximity.class, Bit.NONE, "distance", "nearby"),
    // DEBUG(FlagDebug.class, Bit.NO_VALUE | Bit.NO_FOR | Bit.NO_SKIP_PERMISSION, "monitor", "log"),

    // Recipe only flags
    ADDTOBOOK(FlagAddToBook.class, Bit.RECIPE | Bit.NO_FOR | Bit.NO_SKIP_PERMISSION, "recipebook"),
    FAILMESSAGE(FlagFailMessage.class, Bit.RECIPE, "failmsg"),
    DISPLAYRESULT(FlagDisplayResult.class, Bit.RECIPE, "resultdisplay", "showresult"),
    REMOVE(FlagRemove.class, Bit.RECIPE | Bit.NO_FOR | Bit.NO_VALUE | Bit.NO_SKIP_PERMISSION, "delete"),
    RESTRICT(FlagRestrict.class, Bit.RECIPE | Bit.NO_VALUE, "disable", "denied", "deny"),
    OVERRIDE(FlagOverride.class, Bit.RECIPE | Bit.NO_FOR | Bit.NO_VALUE | Bit.NO_SKIP_PERMISSION, "edit", "overwrite", "supercede", "replace"),

    // Result only flags
    CLONEINGREDIENT(FlagCloneIngredient.class, Bit.RESULT | Bit.NO_SHIFT, "clone", "copy", "copyingredient"), // TODO finish
    ITEMNAME(FlagItemName.class, Bit.RESULT, "name", "displayname"),
    ITEMLORE(FlagItemLore.class, Bit.RESULT, "lore", "itemdesc"),
    LEATHERCOLOR(FlagLeatherColor.class, Bit.RESULT, "leathercolour", "color", "colour"),
    BOOKITEM(FlagBookItem.class, Bit.RESULT, "book"),
    // MAPITEM(FlagMapItem.class, Bit.RESULT, "map"), // TODO finish this flag
    FIREWORKITEM(FlagFireworkItem.class, Bit.RESULT, "firework", "fireworkrocket"),
    FIREWORKCHARGEITEM(FlagFireworkChargeItem.class, Bit.RESULT, "fireworkcharge", "fireworkeffect"),
    SKULLOWNER(FlagSkullOwner.class, Bit.RESULT, "skullitem"),
    POTIONITEM(FlagPotionItem.class, Bit.RESULT, "potion"),
    ENCHANTITEM(FlagEnchantItem.class, Bit.RESULT, "enchant", "enchantment"),
    ENCHANTEDBOOK(FlagEnchantedBook.class, Bit.RESULT, "enchantbook", "enchantingbook"),
    GETRECIPEBOOK(FlagGetRecipeBook.class, Bit.RESULT | Bit.NO_SHIFT, "getbook", "bookresult");

    /*
     * FlagType related methods
     */

    private final Class<? extends Flag> flagClass;
    private final String[] names;
    private final int bits;

    private FlagType(Class<? extends Flag> newFlagClass, int newBits, String... aliases) {
        flagClass = newFlagClass;
        bits = newBits;

        names = new String[aliases.length + 1];
        names[0] = name().toLowerCase();

        for (int i = 0; i < aliases.length; i++) {
            names[i + 1] = aliases[i];
        }
    }

    /**
     * Checks if flag type has a special bit.
     *
     * @param bit
     *            See {@link Bit}
     * @return
     */
    public boolean hasBit(int bit) {
        return (bits & bit) == bit;
    }

    /**
     * @return the class assigned to this type (not the instance)
     */
    public Class<? extends Flag> getFlagClass() {
        return flagClass;
    }

    /**
     * @return array of flags names, index 0 is always the main name
     */
    public String[] getNames() {
        return names.clone();
    }

    /**
     * @return the first name of the flag
     */
    public String getName() {
        return names[0];
    }

    /**
     * @return a new instance of the class assigned to this type or null if failed and prints stack trace.
     */
    public Flag createFlagClass() {
        try {
            return flagClass.newInstance();
        } catch (Throwable e) {
            Messages.error(null, e, null);
        }

        return null;
    }

    private String[] getField(String name) {
        if (flagClass.equals(FlagSummon.class) && name.equals("D")) {
            return FlagSummon.getDescription();
        }

        try {
            return (String[]) flagClass.getDeclaredField(name).get(null);
        } catch (Throwable e) {
            Messages.debug("flag " + toString() + " does not have '" + name + "' field!");
        }

        return null;
    }

    public String[] getArguments() {
        return getField("A");
    }

    public String[] getExamples() {
        return getField("E");
    }

    public String[] getDescription() {
        return getField("D");
    }

    /**
     * Gets the <code>@flag</code> style flag name
     */
    @Override
    public String toString() {
        return '@' + names[0];
    }

    private static final Map<String, FlagType> nameMap = new HashMap<String, FlagType>();
    private static final Map<Class<? extends Flag>, FlagType> classMap = new HashMap<Class<? extends Flag>, FlagType>();

    /**
     * You should not call this method.<br> <br>
     * It is used by the plugin to add the flags to an index map then create and add individual no-flag permissions.
     */
    public static void init() {
        Permission parent = Bukkit.getPluginManager().getPermission(Perms.FLAG_ALL);

        if (parent == null) {
            parent = new Permission(Perms.FLAG_ALL, PermissionDefault.TRUE);
            parent.setDescription("Allows use of flag.");

            Bukkit.getPluginManager().addPermission(parent);
        }

        Permission p;

        for (FlagType type : values()) {
            classMap.put(type.getFlagClass(), type);

            for (String name : type.names) {
                nameMap.put(name, type);

                if (type.hasBit(Bit.NO_SKIP_PERMISSION)) {
                    continue;
                }

                if (Bukkit.getPluginManager().getPermission(Perms.FLAG_PREFIX + name) != null) {
                    continue;
                }

                p = new Permission(Perms.FLAG_PREFIX + name, PermissionDefault.TRUE);
                p.setDescription("Allows use of the " + type + " flag.");
                p.addParent(parent, true);
                Bukkit.getPluginManager().addPermission(p);
            }
        }
    }

    /**
     * Get the FlagType object for a flag name or alias.
     *
     * @param flag
     *            flag name or alias
     * @return FlagType if found or null
     */
    public static FlagType getByName(String flag) {
        Validate.notNull(flag);

        if (flag.charAt(0) != '@') {
            throw new IllegalArgumentException("Flag string must start with @");
        }

        return nameMap.get(flag.substring(1).toLowerCase());
    }

    /**
     * Get the FlagType object for the specified class.
     *
     * @param flagClass
     *            flag's .class
     * @return FlagType if found or null
     */
    public static FlagType getByClass(Class<? extends Flag> flagClass) {
        return classMap.get(flagClass);
    }

    /**
     * Flag bits to configure special behavior
     */
    public class Bit {
        public static final byte NONE = 0;

        /**
         * Flag only works in recipes.
         */
        public static final byte RECIPE = 1 << 0;

        /**
         * Flag only works on results.
         */
        public static final byte RESULT = 1 << 1;

        /**
         * No value is allowed for this flag.
         */
        public static final byte NO_VALUE = 1 << 2;

        /**
         * Disables flag from being stored - used on flags that directly affect result's metadata.
         */
        public static final byte NO_FOR = 1 << 3;

        /**
         * Disables "false" or "remove" values from removing the flag.
         */
        public static final byte NO_FALSE = 1 << 4;

        /**
         * Disables shift+click on the recipe if there is at least one flag with this bit.
         */
        public static final byte NO_SHIFT = 1 << 5;

        /**
         * Disables generating a skip permission for this flag
         */
        public static final byte NO_SKIP_PERMISSION = 1 << 6;
    }
}
