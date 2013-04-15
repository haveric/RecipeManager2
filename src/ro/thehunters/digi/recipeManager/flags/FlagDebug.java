package ro.thehunters.digi.recipeManager.flags;

import java.util.List;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagDebug extends Flag
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
            "Prints what the recipe and flag does in the server console.",
            "",
            "This flag is merely a tool to help you analyse the recipe's code flow.",
            "",
            "Value is optional and can be either true or false.",
        };
        
        E = null;
    }
    
    // Flag code
    
    public FlagDebug()
    {
        type = FlagType.DEBUG;
    }
    
    @Override
    public boolean onParse(String value)
    {
        return true;
    }
    
    public static void grabReasons(String player, Flaggable flaggable, List<String> reasons)
    {
        grab(player, flaggable, reasons, "failed crafting, reasons:");
    }
    
    public static void grabEffects(String player, Flaggable flaggable, List<String> effects)
    {
        grab(player, flaggable, effects, "succesfully crafted, effects:");
    }
    
    private static void grab(String player, Flaggable flaggable, List<String> list, String message)
    {
        if(list != null && !list.isEmpty() && flaggable.hasFlag(FlagType.DEBUG))
        {
            String type = "unknown";
            
            if(flaggable instanceof BaseRecipe)
            {
                type = ((BaseRecipe)flaggable).getType().toString();
            }
            else if(flaggable instanceof ItemResult)
            {
                type = Tools.printItem(((ItemResult)flaggable));
            }
            
            String prefix = "[@debug|" + type + "] ";
            Messages.info(prefix + (player == null ? "unknown player" : player) + " " + message);
            
            for(String s : list)
            {
                if(s != null)
                {
                    Messages.info(prefix + s);
                }
            }
        }
    }
}
