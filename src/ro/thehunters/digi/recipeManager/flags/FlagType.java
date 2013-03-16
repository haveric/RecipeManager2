package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import ro.thehunters.digi.recipeManager.Permissions;

public enum FlagType
{
    // TODO go through each flag and check:
    // new Flag(this) clone style and must have this() in them !!!!!
    // protected on*() methods
    // ...
    
    // Shared flags
    MESSAGE(FlagMessage.class, Bit.NONE, "craftmsg"),
    COMMANDS(FlagCommands.class, Bit.NONE, "command", "cmd"),
    PERMISSION(FlagPermission.class, Bit.NONE, "perm"),
    FORPERMISSION(FlagForPermission.class, Bit.NONE, "forperm", "for"),
    INGREDIENTCONDITION(FlagIngredientCondition.class, Bit.NONE, "ingredient", "ifingr"), // TODO finish
//    HOLDITEM(FlagHoldItem.class, Bit.NONE, "hold"),
//    PLAYTIME(FlagPlayTime.class, Bit.NONE),
//    ONLINETIME(FlagOnlineTime.class, Bit.NONE),
//    GAMEMODE(FlagGameMode.class, Bit.NONE),
    MODEXP(FlagModExp.class, Bit.NONE, "expmod", "modxp", "xpmod", "exp", "xp"),
    REQEXP(FlagReqExp.class, Bit.NONE, "expreq", "reqxp", "xpreq", "needexp", "needxp"),
    MODLEVEL(FlagModLevel.class, Bit.NONE, "levelmod", "level"),
    REQLEVEL(FlagReqLevel.class, Bit.NONE, "levelreq", "needlevel"),
    MODMONEY(FlagModMoney.class, Bit.NONE, "moneymod", "money"),
    REQMONEY(FlagReqMoney.class, Bit.NONE, "moneyreq", "needmoney"),
    LAUNCHFIREWORK(FlagLaunchFirework.class, Bit.NONE),
    EXPLODE(FlagExplode.class, Bit.NONE, "explosion", "boom"),
    SOUND(FlagSound.class, Bit.NONE, "playsound"),
    EFFECT(FlagEffect.class, Bit.NONE, "playeffect", "fx"), // TODO finish
    CREATURE(FlagCreature.class, Bit.NONE, "spawncreature"), // TODO finish
    BIOME(FlagBiome.class, Bit.NONE), // TODO finish
    WEATHER(FlagWeather.class, Bit.NONE), // TODO finish
    WORLDTIME(FlagWorldTime.class, Bit.NONE), // TODO finish
    SECRET(FlagSecret.class, Bit.NO_VALUE, "hide"),
    DEBUG(FlagDebug.class, Bit.NO_VALUE, "monitor", "log"),
    REALTIME(FlagRealTime.class, Bit.NONE, "time", "timereq"),
    COOLDOWN(FlagCooldown.class, Bit.NONE, "cooltime", "delay"),
    
    // Recipe only flags
    DESCRIPTION(FlagDescription.class, Bit.RECIPE, "recipeinfo", "info"),
    FAILMESSAGE(FlagFailMessage.class, Bit.RECIPE, "failmsg"),
    HIDERESULTS(FlagHideResults.class, Bit.RECIPE | Bit.NO_VALUE),
    GETBOOK(FlagGetBook.class, Bit.RECIPE, "getrecipebook", "recipebook"), // TODO finsih
    REMOVE(FlagRemove.class, Bit.RECIPE | Bit.NO_VALUE, "delete"),
    RESTRICT(FlagRestrict.class, Bit.RECIPE | Bit.NO_VALUE, "denied", "deny"),
    OVERRIDE(FlagOverride.class, Bit.RECIPE | Bit.NO_VALUE, "overwrite", "supercede", "replace"),
    NOSHIFTCLICK(FlagNoShiftClick.class, Bit.RECIPE | Bit.NO_VALUE, "noshift"),
    
    // Result only flags
//    SETCHANCE(FlagSetChance.class, Bit.RESULT, "chance"), // TODO finish
    CLONEINGREDIENT(FlagCloneIngredient.class, Bit.RESULT, "clone", "copy", "copyingredient"), // TODO finish
    NAME(FlagName.class, Bit.RESULT | Bit.NO_STORE, "itemname", "displayname"),
    LORE(FlagLore.class, Bit.RESULT | Bit.NO_STORE, "itemlore", "itemdescription"),
    
    // TODO test as STORED flag:
    LEATHERCOLOR(FlagLeatherColor.class, Bit.RESULT, "leathercolour", "color", "colour", "itemcolor", "itemcolour"),
    
    BOOK(FlagBook.class, Bit.RESULT | Bit.NO_STORE, "bookitem", "itembook"),
    BOOKPAGE(FlagBookPage.class, Bit.RESULT | Bit.NO_STORE, "bookitempage", "page", "addpage"),
    MAP(FlagMap.class, Bit.RESULT | Bit.NO_STORE, "mapitem", "itemmap"),
    FIREWORK(FlagFirework.class, Bit.RESULT | Bit.NO_STORE, "fireworkrocket"),
    FIREWORKCHARGE(FlagFireworkCharge.class, Bit.RESULT | Bit.NO_STORE, "fireworkeffect"),
    SKULL(FlagSkull.class, Bit.RESULT | Bit.NO_STORE, "skullowner"),
    POTION(FlagPotion.class, Bit.RESULT | Bit.NO_STORE, "potionitem"),
    ENCHANT(FlagEnchant.class, Bit.RESULT | Bit.NO_STORE, "enchantment"),
    ENCHANTBOOK(FlagEnchantBook.class, Bit.RESULT | Bit.NO_STORE, "enchantedbook");
    
    private final Class<? extends Flag> flagClass;
    private final String[]              names;
    private final int                   bits;
    
    private FlagType(Class<? extends Flag> flagClass, int bits, String... aliases)
    {
        this.flagClass = flagClass;
        this.bits = bits;
        
        this.names = new String[aliases.length + 1];
        this.names[0] = name().toLowerCase();
        
        for(int i = 0; i < aliases.length; i++)
        {
            this.names[i + 1] = aliases[i];
        }
    }
    
    public boolean hasBit(int bit)
    {
        return (bits & bit) == bit;
    }
    
    /**
     * @return the class asigned to this type (not the instance)
     */
    public Class<? extends Flag> getFlagClass()
    {
        return flagClass;
    }
    
    /**
     * @return array of flags names, index 0 is always the main name
     */
    public String[] getNames()
    {
        return names;
    }
    
    /**
     * @return a new instance of the class asigned to this type or null if failed and prints stack trace.
     */
    public Flag createFlagClass()
    {
        try
        {
            return flagClass.newInstance();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets the <code>@flag</code> style flag name
     */
    public String toString()
    {
        return "@" + names[0];
    }
    
    // Static stuff
    
    private static final Map<String, FlagType>                nameMap  = new HashMap<String, FlagType>();
    private static final Map<Class<? extends Flag>, FlagType> classMap = new HashMap<Class<? extends Flag>, FlagType>();
    
    static
    {
        Permission parent = new Permission(Permissions.SKIPFLAG_ALL, PermissionDefault.FALSE);
        parent.setDescription("Permission to ignore all recipe flags.");
        Bukkit.getPluginManager().addPermission(parent);
        Permission p;
        
        for(FlagType type : values())
        {
            classMap.put(type.getFlagClass(), type);
            
            for(String name : type.names)
            {
                nameMap.put(name, type);
                
                if(!type.hasBit(Bit.NO_STORE))
                {
                    p = new Permission(Permissions.SKIPFLAG_PREFIX + name, PermissionDefault.FALSE);
                    p.setDescription("Permission to ignore " + name + "  recipe flag.");
                    p.addParent(parent, true);
                    Bukkit.getPluginManager().addPermission(p);
                }
            }
        }
    }
    
    /**
     * Get the FlagType object for inputted flag name or alias.<br>
     * This method is faster than {@link #compare(String)} because it uses a HashMap to look for the name.
     * 
     * @param flag
     * @return
     */
    public static FlagType getByName(String flag)
    {
        Validate.notNull(flag);
        
        if(flag.charAt(0) != '@')
            throw new IllegalArgumentException("Flag string must start with @");
        
        return nameMap.get(flag.substring(1).toLowerCase());
    }
    
    public static FlagType getByClass(Class<? extends Flag> flagClass)
    {
        return classMap.get(flagClass);
    }
    
    /**
     * Flag bits to configure special behaviour
     */
    public class Bit
    {
        public static final byte NONE     = 0;
        
        /**
         * Flag only works in recipes
         */
        public static final byte RECIPE   = 1 << 1;
        
        /**
         * Flag only works on results
         */
        public static final byte RESULT   = 1 << 2;
        
        /**
         * No value is allowed for this flag
         */
        public static final byte NO_VALUE = 1 << 3;
        
        /**
         * Disables flag from being stored - used on flags that directly affect result's metadata
         * <p />
         * TODO remove this ?
         */
        public static final byte NO_STORE = 1 << 4;
        
        /**
         * Disables "false" or "remove" values from removing the flag
         */
        public static final byte NO_FALSE = 1 << 5;
    }
}