package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.SkullMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagItemSkull extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[1];
        A[0] = "{flag} < ??? >";
        
        D = new String[1];
        D[0] = "Flag not yet documented.";
        
        E = null;
    }
    
    // Flag code
    
    public FlagItemSkull()
    {
        type = FlagType.ITEMSKULL;
    }
    
    @Override
    public boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof SkullMeta == false || result.getDurability() != 3)
        {
            return RecipeErrorReporter.error("Flag @" + type + " needs a SKULL_ITEM with data value 3 to work!");
        }
        
        return true;
    }
    
    @Override
    public void onRemove()
    {
        onParse(null);
    }
    
    @Override
    public boolean onParse(String value)
    {
        ItemResult result = getResult();
        SkullMeta skull = (SkullMeta)result.getItemMeta();
        skull.setOwner(value);
        result.setItemMeta(skull);
        
        return true;
    }
}