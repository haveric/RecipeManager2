package ro.thehunters.digi.recipeManager.flags;

public class FlagBiome extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = null;
        D = null;
        E = null;
    }
    
    // Flag code
    
    public FlagBiome()
    {
        type = FlagType.BIOME;
    }
}
