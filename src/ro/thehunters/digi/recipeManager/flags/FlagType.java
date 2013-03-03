package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum FlagType
{
    // Shared flags
    MESSAGE(FlagMessage.class, Bit.NONE, "craftmsg"),
    COMMANDS(FlagCommands.class, Bit.NONE, "command", "cmd"),
    PERMISSION(FlagPermission.class, Bit.NONE, "perm"),
    HOLD(Flag.class, Bit.NONE),
    PLAYTIME(Flag.class, Bit.NONE),
    ONLINETIME(Flag.class, Bit.NONE),
    GAMEMODE(Flag.class, Bit.NONE),
    MODEXP(FlagModExp.class, Bit.NONE, "expmod", "modxp", "xpmod", "exp", "xp"),
    REQEXP(FlagReqExp.class, Bit.NONE, "expreq", "reqxp", "xpreq", "needexp", "needxp"),
    MODLEVEL(Flag.class, Bit.NONE, "levelmod", "level"),
    REQLEVEL(Flag.class, Bit.NONE, "levelreq", "needlevel"),
    MODMONEY(Flag.class, Bit.NONE, "moneymod", "money"),
    REQMONEY(Flag.class, Bit.NONE, "moneyreq", "needmoney"),
    LAUNCHFIREWORK(FlagLaunchFirework.class, Bit.NONE),
    SOUND(FlagSound.class, Bit.NONE, "playsound"),
    EFFECT(FlagEffect.class, Bit.NONE, "playeffect", "fx"), // TODO finish
    CREATURE(FlagCreature.class, Bit.NONE, "spawncreature"), // TODO finish
    SECRET(FlagSecret.class, Bit.NO_VALUE, "hide"),
    DEBUG(FlagDebug.class, Bit.NO_VALUE, "monitor", "log"),
    
    // Recipe only flags
    DESCRIPTION(FlagDescription.class, Bit.RECIPE, "recipeinfo", "info"),
    FAILMESSAGE(FlagFailMessage.class, Bit.RECIPE, "failmsg"),
    HIDERESULTS(FlagHideResults.class, Bit.RECIPE | Bit.NO_VALUE),
    NEEDFUEL(FlagNeedFuel.class, Bit.RECIPE, "reqfuel", "fuelreq"), // TODO Remove ?
    REMOVE(FlagRemove.class, Bit.RECIPE | Bit.NO_VALUE, "delete"),
    RESTRICT(FlagRestrict.class, Bit.RECIPE | Bit.NO_VALUE, "denied", "deny"),
    OVERRIDE(FlagOverride.class, Bit.RECIPE | Bit.NO_VALUE, "overwrite", "supercede", "replace"),
    
    // Result only flags
    NAME(FlagName.class, Bit.RESULT | Bit.NO_STORE, "itemname", "displayname"),
    LORE(FlagLore.class, Bit.RESULT | Bit.NO_STORE, "itemlore", "itemdescription"),
    COLOR(FlagColor.class, Bit.RESULT | Bit.NO_STORE, "colour", "itemcolor", "itemcolour"),
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
    private final Set<String>           aliases;
    private final int                   bits;
    
    private FlagType(Class<? extends Flag> flagClass, int bits, String... args)
    {
        this.flagClass = flagClass;
        this.bits = bits;
        this.aliases = new HashSet<String>(args.length + 1);
        this.aliases.add(name().toLowerCase());
        
        for(String arg : args)
        {
            this.aliases.add(arg);
        }
    }
    
    public boolean hasBit(int bit)
    {
        return (bits & bit) == bit;
    }
    
    /**
     * Checks if string is the name or alias of this flag.
     * 
     * If you're looping through FlagType.values() to use this, you should use {@link FlagType #get(String)} instead!
     * 
     * @param flag
     * @return
     */
    public boolean compare(String flag)
    {
        return aliases.contains(flag);
    }
    
    /**
     * @return the class asigned to this type (not the instance)
     */
    public Class<? extends Flag> getFlagClass()
    {
        return flagClass;
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
        return "@" + name().toLowerCase();
    }
    
    // Static stuff
    
    private static final Map<String, FlagType>                nameMap  = new HashMap<String, FlagType>();
    private static final Map<Class<? extends Flag>, FlagType> classMap = new HashMap<Class<? extends Flag>, FlagType>();
    
    static
    {
        for(FlagType type : values())
        {
            /*
            // TODO remove
            FlagType t = type.createFlagClass().getType();
            
            if(type != t)
                Messages.info(ChatColor.RED + "WARNING: " + ChatColor.RESET + "INVALID TYPE ON " + type + ": " + t);
            // TODO ^
            */
            
            classMap.put(type.getFlagClass(), type);
            
            for(String dir : type.aliases)
            {
                nameMap.put(dir, type);
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
        return nameMap.get(flag);
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
         */
        public static final byte NO_STORE = 1 << 4;
        
        /**
         * Disables "false" or "remove" values from removing the flag
         */
        public static final byte NO_FALSE = 1 << 5;
    }
}