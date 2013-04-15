package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Files;

public class FlagDocumentation extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[]
        {
            "{flag} [true or false]",
        };
        
        D = new String[]
        {
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
        type = FlagType.DOCUMENTATION;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        return true;
    }
}
