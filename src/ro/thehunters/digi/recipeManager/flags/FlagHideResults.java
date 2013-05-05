package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.ErrorReporter;

public class FlagHideResults extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = null; //FlagType.HIDERESULTS; // TODO
        
        A = new String[]
        {
            "{flag} ...",
        };
        
        D = new String[]
        {
            "FLAG NOT IMPLEMENTED",
        };
        
        E = new String[]
        {
            "{flag} ...",
        };
    }
    
    // Flag code
    
    /**
     * Contains bit settings for {@link FlagHideResults}'s bitsum
     */
    public class Bits
    {
        /**
         * Hide
         */
        public static final byte SECRET = 1 << 1;
        public static final byte UNALLOWED = 1 << 2;
        public static final byte CRAFTABLE = 1 << 3;
        /**
         * All settings into one for accessibility
         */
        public static final byte ALL = SECRET | UNALLOWED | CRAFTABLE;
    }
    
    private int hideBitsum;
    
    public FlagHideResults()
    {
    }
    
    public FlagHideResults(FlagHideResults flag)
    {
        hideBitsum = flag.hideBitsum;
    }
    
    @Override
    public FlagHideResults clone()
    {
        return new FlagHideResults(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    /**
     * Use Bits inner class for this.
     * 
     * @param bit
     */
    public void setBitsum(int bit)
    {
        hideBitsum = bit;
    }
    
    /**
     * Adds a bit to the bitsum.
     * Use {@link Bits} inner class for this
     * 
     * @param bit
     */
    public void addBit(int bit)
    {
        hideBitsum |= bit;
    }
    
    /**
     * Checks if a bit is in the bitsum
     * 
     * @param bit
     * @return
     */
    public boolean hasBit(int bit)
    {
        return (hideBitsum & bit) == bit;
    }
    
    /**
     * Returns bitsum of hide result settings.
     * 
     * @return
     */
    public int getBitsum()
    {
        return hideBitsum;
    }
    
    @Override
    public boolean onParse(String value)
    {
        String[] split = value.toLowerCase().split("\\|");
        
        for(String s : split)
        {
            if(s.equals("all"))
            {
                setBitsum(Bits.ALL);
                break;
            }
            else if(s.equals("secret"))
            {
                addBit(Bits.SECRET);
            }
            else if(s.equals("unallowed"))
            {
                addBit(Bits.UNALLOWED);
            }
            else if(s.equals("craftable"))
            {
                addBit(Bits.CRAFTABLE);
            }
            else
            {
                ErrorReporter.warning("Flag " + getType() + " has unknown argument: " + s, "Maybe it's spelled wrong, check it in " + Files.FILE_INFO_FLAGS + " file.");
            }
        }
        
        return true;
    }
}
