package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.BookMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagBookPage extends Flag
{
    public FlagBookPage()
    {
        type = FlagType.BOOKPAGE;
    }
    
    @Override
    public boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof BookMeta == false)
        {
            RecipeErrorReporter.error("Flag " + type + " needs a WRITTEN_BOOK item!");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void onRemove()
    {
        ItemResult result = getResult();
        BookMeta meta = (BookMeta)result.getItemMeta();
        meta.setTitle(null);
        meta.setAuthor(null);
        result.setItemMeta(meta);
    }
    
    @Override
    public boolean onParse(String value)
    {
        ItemResult result = getResult();
        BookMeta meta = (BookMeta)result.getItemMeta();
        
        if(value.equalsIgnoreCase("false"))
        {
            String text = Tools.parseColors(value, false);
            
            if(text.length() > 256)
            {
                RecipeErrorReporter.warning("Flag @" + type + " has page with text longer than 256 characters, trimmed to size.", "Color codes use up that limit too, 1 character per color.");
                text = text.substring(0, 256);
            }
            
            meta.addPage(text);
        }
        else
        {
            meta.getPages().clear();
        }
        
        result.setItemMeta(meta);
        return true;
    }
}