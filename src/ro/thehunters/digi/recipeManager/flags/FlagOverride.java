package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagOverride extends Flag
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
            "Overwrites an existing recipe from vanilla Minecraft or other plugins/mods.",
            "The recipe definition must have the exact ingredients of the recipe you want to overwrite.",
            "",
            "You may set whatever result(s) you want and add any other flags, this flag allows RecipeManager to take control over that recipe.",
            "If you don't know the exact ingredients you can use 'rmextract' command to extract all existing recipes in RecipeManager format.",
            "",
            "Value is optional, if value is not specified it will just be enabled.",
        };
        
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
    
    @Override
    public List<String> information()
    {
        List<String> list = new ArrayList<String>(1);
        
        list.add(Messages.FLAG_OVERRIDE.get());
        
        return list;
    }
}
