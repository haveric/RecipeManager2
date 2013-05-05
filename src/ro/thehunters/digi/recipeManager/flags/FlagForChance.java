package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.ErrorReporter;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.flags.FlagType.Bit;

public class FlagForChance extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.FORCHANCE;
        
        A = new String[]
        {
            "{flag} <group> [0.01-100]% [[!]@flag declaration]",
            "{flag} <0.01-100>% <[!]@flag declaration>",
            "{flag} false",
        };
        
        D = new String[]
        {
            // TODO rewrite this...
            "Adds other flags that are triggered by random chance.",
            "Basically this is a storage for flags and will only trigger them by specified chance.",
            "You can add a pile of random flags to be triggered how they want or you can use groups to trigger only one of the specified flag from a group depending on its chance.",
            "",
            "First off, grouped flags:",
            "The <group> argument can any combination of letters and numbers (except '<num>%' which is the 2nd type of arguments).",
            "Grouping more flags into a group would pick only one flag per trigger.",
            "The '0.01-100%' arg is the chance, which it's applied to the individual flag defined later, if you don't define it then the chance will be evenly calculated.",
            "Flags in a group must have a total of 100% chance.",
            "The 'flag declaration' is optional but if defined it must be a flag that can be applied on the current recipe or result.",
            "Not specifying a flag declaration would allow you to set a chance for the group to do absolutely nothing.",
            "",
            "For extra awesomeness you can nest this flag with itself to make groups of groups, see examples.",
            "",
            "For individually and randomly picked flags you can just specify chance and flag declaration.",
            "This will make flags have individual chance and will trigger if their chance was picked regardless if any other flag was triggered too.",
            "The <flag declaration> must be a flag that will work on the current recipe or result.",
            "",
            "Also, you can add a '!' before the flag declaration to create a new flag instead of adding to/overwriting the previous same flag declaration.",
            "",
            "Using 'false' will disable the flag.",
        };
        
        E = new String[]
        {
            "// some simple examples",
            "{flag} 25% " + FlagType.EXPLODE + " // 25% chance to explode",
            "// add 2 new individual flags that will trigger at their individual chances, they can both trigger or even none at all, depends on the chance.",
            "{flag} 80% " + FlagType.COMMAND + " say high chance message...",
            "{flag} 50% !" + FlagType.COMMAND + " say 50-50 message... // this is prefixed with '!' and makes a new flag instead of adding command to the previous command flag",
            "{flag} " + FlagType.COMMAND + " say rare message ! // this is triggered along with the 50% one !",
            "// all flags in a group must have a total of 100% chance since only one triggers, in this case the chance is calculated and it would be 33.33% for each.",
            "{flag} mystuff !" + FlagType.SOUND + " level_up",
            "{flag} mystuff !" + FlagType.SOUND + " note_bass",
            "{flag} mystuff !" + FlagType.SOUND + " hurt",
        };
    }
    
    // Flag code
    
    public class ChanceFlag implements Cloneable
    {
        private Flag flag;
        private float chance;
        private boolean autoChance = false;
        
        public ChanceFlag(Flag flag, Float chance)
        {
            this.flag = flag;
            
            if(chance == null)
            {
                this.autoChance = true;
            }
            else
            {
                this.chance = chance;
            }
        }
        
        public Flag getFlag()
        {
            return flag;
        }
        
        public void setFlag(Flag flag, FlagForChance holder)
        {
            flag.flagsContainer = holder.getFlagsContainer();
            this.flag = flag;
        }
        
        public float getChance()
        {
            return chance;
        }
        
        public boolean isAutoChance()
        {
            return autoChance;
        }
    }
    
    private Map<String, List<ChanceFlag>> flagMap = new HashMap<String, List<ChanceFlag>>();
    
    public FlagForChance()
    {
    }
    
    public FlagForChance(FlagForChance flag)
    {
        for(Entry<String, List<ChanceFlag>> e : flag.flagMap.entrySet())
        {
            List<ChanceFlag> flags = new ArrayList<ChanceFlag>();
            
            for(ChanceFlag c : e.getValue())
            {
                if(c.getFlag() == null)
                {
                    flags.add(null);
                }
                else
                {
                    flags.add(new ChanceFlag(c.getFlag().clone(this.getFlagsContainer()), c.isAutoChance() ? null : c.getChance()));
                }
            }
            
            recalculateChances(e.getKey(), flags);
            flagMap.put(e.getKey(), flags);
        }
    }
    
    @Override
    public FlagForChance clone()
    {
        return new FlagForChance(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    /**
     * The flag map, map keys are groups and values are lists of ChanceFlag classes which contain a Flag object and a chance float value.
     * 
     * @return
     */
    public Map<String, List<ChanceFlag>> getFlagMap()
    {
        return flagMap;
    }
    
    /**
     * Gets the first flag matching FlagType or the last if reverse is set to true.<br>
     * There can be more instances of the same type of flag.
     * 
     * @param group
     *            the chance group, can be null for no group/individual
     * @return flag list or null if group doesn't exist
     */
    public List<ChanceFlag> getFlagsFromGroup(String group)
    {
        return flagMap.get(group);
    }
    
    /**
     * @param group
     *            the chance group, can be null for no group/individual
     * @param flags
     *            the list of flags
     */
    public void setFlagsForGroup(String group, List<ChanceFlag> flags)
    {
        flagMap.put(group, flags);
    }
    
    /**
     * Checks if the flag can be added to this flag list.<br>
     * 
     * @param flag
     * @return false if flag can only be added on specific flaggables
     */
    public boolean canAdd(Flag flag)
    {
        return flag != null && flag.validate() && !flag.getType().hasBit(Bit.NO_FOR);
    }
    
    /**
     * Attempts to add a flag to this flag list for the chance group.<br>
     * Adds an error to the {@link ErrorReporter} class if flag is not compatible with recipe/result.
     * 
     * @param group
     *            the chance group, can be null for no group/individual
     * @param flag
     *            the flag
     */
    public void addFlag(String group, Flag flag)
    {
        addFlag(group, flag, 100.0f);
    }
    
    /**
     * Attempts to add a flag to this flag list for the chance group.<br>
     * Adds an error to the {@link ErrorReporter} class if flag is not compatible with recipe/result.
     * 
     * @param group
     *            the chance group, can be null for no group/individual
     * @param flag
     *            the flag
     * @param chance
     *            trigger chance, valid values between 0.01f to 100.0f or null to auto-calculate
     */
    public void addFlag(String group, Flag flag, Float chance)
    {
        Validate.notNull(flag, "Argument flag must not be null!");
        
        if(canAdd(flag))
        {
            List<ChanceFlag> flags = flagMap.get(group);
            
            if(flags == null)
            {
                flags = new ArrayList<ChanceFlag>();
                flagMap.put(group, flags);
            }
            
            flag.flagsContainer = this.getFlagsContainer();
            
            if(chance != null)
            {
                chance = Math.min(Math.max(chance, 0.01f), 100.0f);
            }
            
            flags.add(new ChanceFlag(flag, chance));
            recalculateChances(group, flags);
        }
    }
    
    @Override
    protected boolean onParse(String value)
    {
        int i = value.indexOf(' '); // get position of first space
        String flagDeclaration = null;
        String group = null;
        Float chance = null;
        boolean newFlag = false;
        
        if(i < 0)
        {
            group = value;
        }
        else
        {
            String arg = value.substring(0, i);
            
            if(arg.charAt(i - 1) == '%') // check if character before space is a '%'
            {
                arg = arg.substring(0, i - 1).trim(); // get the string between begining of string and the space - 1 character to skip the '%' char
                
                try
                {
                    chance = Float.valueOf(arg);
                }
                catch(NumberFormatException e)
                {
                    ErrorReporter.error("Flag " + getType() + " has invalid chance number: " + arg);
                    return false;
                }
                
                if(chance < 0.01f || chance > 100)
                {
                    chance = Math.min(Math.max(chance, 0.01f), 100.0f);
                    
                    ErrorReporter.warning("Flag " + getType() + " is lower than 0.01 or higher than 100%, trimmed.");
                }
                
                arg = value.substring(i + 1).trim(); // get the string after the first space
                
                if(!arg.startsWith("@") && !arg.startsWith("!@")) // we need a flag declaration at this point
                {
                    ErrorReporter.error("Flag " + getType() + " has chance as first argument but not a flag as second argument: " + arg);
                    return false;
                }
                
                if(arg.charAt(0) == '!')
                {
                    arg = arg.substring(1);
                    newFlag = true;
                }
                
                flagDeclaration = arg;
            }
            else
            {
                group = arg; // otherwise it must be a group!
                
                arg = value.substring(i + 1).trim(); // get the string after the space
                
                if(arg.startsWith("@") || arg.startsWith("!@"))
                {
                    if(arg.charAt(0) == '!')
                    {
                        arg = arg.substring(1);
                        newFlag = true;
                    }
                    
                    flagDeclaration = arg;
                }
                else
                {
                    i = arg.indexOf('%'); // get location of first '%' char...
                    
                    if(i == -1)
                    {
                        ErrorReporter.error("Flag " + getType() + " has neither a flag nor a chance argument: " + value);
                        return false;
                    }
                    
                    String chanceString = arg.substring(0, i); // get string between group and '%' char...
                    
                    try
                    {
                        chance = Float.valueOf(chanceString);
                    }
                    catch(NumberFormatException e)
                    {
                        ErrorReporter.error("Flag " + getType() + " has invalid chance number: " + chanceString);
                        return false;
                    }
                    
                    if(chance < 0.01f || chance > 100)
                    {
                        chance = Math.min(Math.max(chance, 0.01f), 100.0f);
                        
                        ErrorReporter.warning("Flag " + getType() + " is lower than 0.01 or higher than 100%, trimmed.");
                    }
                    
                    if(arg.length() > (i + 1))
                    {
                        arg = arg.substring(i + 1).trim(); // get string after '%' char
                        
                        if(arg.startsWith("@") || arg.startsWith("!@"))
                        {
                            if(arg.charAt(0) == '!')
                            {
                                arg = arg.substring(1);
                                newFlag = true;
                            }
                            
                            flagDeclaration = arg;
                        }
                        else
                        {
                            ErrorReporter.warning("Flag " + getType() + " has unknown last argument, expected flag: " + arg);
                        }
                    }
                }
            }
        }
        
        List<ChanceFlag> flags = flagMap.get(group); // get flags list for group even if group is null
        ChanceFlag flagChance = null;
        
        if(flagDeclaration != null)
        {
            String[] split = flagDeclaration.split("[:\\s]+", 2); // split by space or : char
            String flagString = split[0].trim(); // format flag name
            
            FlagType type = FlagType.getByName(flagString); // Find the current flag
            
            if(type == null)
            {
                ErrorReporter.error("Flag " + getType() + " has unknown flag type: " + flagString);
                return false;
            }
            
            if(type.hasBit(Bit.NO_FOR))
            {
                return ErrorReporter.error("Flag " + getType() + "'s flag " + flagString + " can not be used with this!");
            }
            
            if(flags != null)
            {
                if(!newFlag)
                {
                    // Loop through flags backwards to get the last added flag
                    for(i = flags.size() - 1; i >= 0; i--)
                    {
                        ChanceFlag c = flags.get(i);
                        
                        if(c.getFlag() != null && c.getFlag().getType() == type)
                        {
                            flagChance = c;
                            
                            if(chance != null)
                            {
                                ErrorReporter.warning("Flag " + getType() + " has flag " + flagChance.getFlag().getType() + " with chance defined, chance will be ignored because flag will be added to/overwritten in the storage !", "Prefix the flag with '!' character to create a new fresh flag instead, see '" + Files.FILE_INFO_FLAGS + "' for details about the prefix.");
                            }
                            
                            break;
                        }
                    }
                }
            }
            else
            {
                flags = new ArrayList<ChanceFlag>();
                flagMap.put(group, flags);
            }
            
            Flag flag = null;
            
            if(newFlag || flagChance == null)
            {
                if(chance != null)
                {
                    float totalChance = 0;
                    
                    for(ChanceFlag c : flags)
                    {
                        totalChance += c.getChance();
                    }
                    
                    if(totalChance >= 100)
                    {
                        ErrorReporter.error("Flag " + getType() + " already has 100% chance for this group!");
                        return false;
                    }
                    
                    chance = 100 - totalChance;
                }
                
                flag = type.createFlagClass(); // create a new instance of the flag does not exist
                flag.flagsContainer = this.getFlagsContainer(); // set container before hand to allow checks
                flagChance = new ChanceFlag(flag, chance);
            }
            else
            {
                flag = flagChance.getFlag();
            }
            
            value = (split.length > 1 ? split[1].trim() : null);
            
            // make sure the flag can be added to this flag list
            if(!flag.validateParse(value))
            {
                return false;
            }
            
            // check if parsed flag had valid values and needs to be added to flag list
            if(!flag.onParse(value))
            {
                return false;
            }
            
            flags.add(flagChance);
            recalculateChances(group, flags);
        }
        else
        {
            if(flags != null)
            {
                for(ChanceFlag c : flags)
                {
                    if(c.getFlag() == null)
                    {
                        ErrorReporter.error("Flag " + getType() + " already has a blank flag for this group!");
                        return false;
                    }
                }
            }
            else
            {
                flags = new ArrayList<ChanceFlag>();
                flagMap.put(group, flags);
            }
            
            flagChance = new ChanceFlag(null, chance);
            flags.add(flagChance);
            recalculateChances(group, flags);
        }
        
        return true;
    }
    
    private void recalculateChances(String group, List<ChanceFlag> flags)
    {
        if(group == null)
        {
            return;
        }
        
        float totalChance = 100;
        int num = 0;
        
        for(ChanceFlag c : flags)
        {
            if(c.isAutoChance())
            {
                num++;
            }
            else
            {
                totalChance -= c.getChance();
            }
        }
        
        if(num > 0)
        {
            float chance = totalChance / num;
            
            for(ChanceFlag c : flags)
            {
                if(c.isAutoChance())
                {
                    c.chance = chance;
                }
            }
        }
        
        /*
        List<ChanceFlag> calc = new ArrayList<ChanceFlag>();
        
        for(Entry<String, List<ChanceFlag>> e : flagMap.entrySet())
        {
            if(e.getKey() == null)
            {
                continue;
            }
            
            float totalChance = 100;
            
            for(ChanceFlag c : e.getValue())
            {
                if(c.isAutoChance())
                {
                    calc.add(c);
                }
                else
                {
                    totalChance -= c.getChance();
                }
            }
            
            if(!calc.isEmpty())
            {
                float chance = totalChance / calc.size();
                float extra = totalChance % calc.size();
                
                Messages.debug(e.getKey() + " | chance=" + chance + " | extra=" + extra);
                
                for(int i = 0; i < calc.size(); i++)
                {
                    if(i == 0 && extra > 0)
                    {
                        calc.get(0).chance = (chance + extra);
                    }
                    else
                    {
                        calc.get(0).chance = chance;
                    }
                }
                
                calc.clear();
            }
        }
        */
    }
    
    @Override
    protected void onCheck(Args a)
    {
        event(a, 'c');
    }
    
    @Override
    protected void onFailed(Args a)
    {
        event(a, 'f');
    }
    
    @Override
    protected void onPrepare(Args a)
    {
        event(a, 'p');
    }
    
    @Override
    protected void onCrafted(Args a)
    {
        event(a, 'r');
    }
    
    private void event(Args a, char method)
    {
        for(Entry<String, List<ChanceFlag>> e : flagMap.entrySet())
        {
            List<ChanceFlag> flags = e.getValue();
            
            if(e.getKey() == null)
            {
                for(ChanceFlag c : flags)
                {
                    if(c.getChance() >= (RecipeManager.random.nextFloat() * 100.0f))
                    {
                        trigger(c.getFlag(), a, method);
                    }
                }
            }
            else
            {
                float totalChance = 0;
                
                for(ChanceFlag c : flags)
                {
                    totalChance += c.getChance();
                }
                
                if(totalChance < 100)
                {
                    ErrorReporter.warning("Flag " + getType() + " has total chance less than 100% " + (e.getKey() == null ? "" : "for group '" + e.getKey() + "'") + ".");
                }
                
                float random = RecipeManager.random.nextFloat() * totalChance;
                float chance = 0;
                
                for(ChanceFlag c : flags)
                {
                    if((chance += c.getChance()) >= random)
                    {
                        trigger(c.getFlag(), a, method);
                        break;
                    }
                }
            }
        }
    }
    
    private void trigger(Flag flag, Args a, char method)
    {
        if(flag == null)
        {
            return;
        }
        
        switch(method)
        {
            case 'c':
                flag.check(a);
                break;
            
            case 'p':
                flag.prepare(a);
                break;
            
            case 'r':
                flag.crafted(a);
                break;
            
            case 'f':
                flag.failed(a);
                break;
        }
    }
}
