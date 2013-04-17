package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Files;

public class FlagDocumentation extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.DESCRIPTION;
        
        A = new String[]
        {
            "{flag} [true or false]",
        };
        
        D = new String[]
        {
            "IMPLEMENTATION NOT DONE, FLAG DOES NOTHING.",
            "",
            "Exports the recipe to '" + Files.FILE_RECIPES + "' file.",
            "",
            "Should be used at the begining of file to affect all recipes in the file, but can be used on individual recipes too.",
            "",
            "Using 'false' will disable the flag on the specific recipe.",
        };
        
        E = new String[]
        {
            "{flag}",
            "{flag} false",
        };
    }
    
    // Flag code
    
    public FlagDocumentation()
    {
    }
    
    @Override
    protected boolean onParse(String value)
    {
        return true;
    }
}
