package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagOverride extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[1];
        A[0] = "{flag} [true or false]";
        
        D = new String[7];
        D[0] = "Overwrites an existing recipe from vanilla Minecraft or other plugins/mods.";
        D[1] = "The recipe definition must have the exact ingredients of the recipe you want to overwrite.";
        D[2] = null;
        D[3] = "You may set whatever result(s) you want and add any other flags, this flag allows RecipeManager to take control over that recipe.";
        D[4] = "If you don't know the exact ingredients you can use 'rmextract' command to extract all existing recipes in RecipeManager format.";
        D[5] = null;
        D[6] = "Value is optional, if value is not specified it will just be enabled.";
        
        E = null;
    }
    
    // Flag code
    
    public FlagOverride()
    {
        type = FlagType.OVERRIDE;
    }
    
    @Override
    public Flag clone()
    {
        return new FlagOverride();
    }
    
    @Override
    protected boolean onValidate()
    {
        if(getFlagsContainer().hasFlag(FlagType.REMOVE))
        {
            return RecipeErrorReporter.error("Flag " + getType() + " can't work with @remove flag!");
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        return true;
    }
}