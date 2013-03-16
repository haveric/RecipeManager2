package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.Map;

import ro.thehunters.digi.recipeManager.flags.Args;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;
import ro.thehunters.digi.recipeManager.recipes.WorkbenchRecipe;

public class Players
{
    private static final Map<String, ItemResult> staticResults = new HashMap<String, ItemResult>();
    
    static void init()
    {
    }
    
    static void clean()
    {
        staticResults.clear();
    }
    
    protected static ItemResult recipeGetResult(Args a, WorkbenchRecipe recipe)
    {
        ItemResult result = staticResults.get(a.playerName());
        
        if(result == null)
        {
            result = recipe.getResult(a);
            staticResults.put(a.playerName(), result);
        }
        
        return result;
    }
    
    protected static void recipeResetResult(String name)
    {
        staticResults.remove(name);
    }
}
