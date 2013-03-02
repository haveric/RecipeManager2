package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.BookMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagBook extends Flag
{
    public FlagBook(FlagBook flag)
    {
        type = FlagType.BOOK;
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
        
        String[] split = value.split("\\|", 2);
        
        if(split.length != 2)
        {
            RecipeErrorReporter.error("Flag @" + type + " doesn't have title | author argument format!");
            return false;
        }
        
        String title = Tools.parseColors(split[0].trim(), false);
        String author = Tools.parseColors(split[1].trim(), false);
        
        if(title.length() > 64 || author.length() > 64)
        {
            RecipeErrorReporter.warning("Flag @" + type + " has title or author larger than 64 characters, trimmed to fit.");
            title = title.substring(0, (title.length() > 64 ? 64 : title.length()));
            author = author.substring(0, (author.length() > 64 ? 64 : author.length()));
        }
        
        meta.setTitle(title);
        meta.setAuthor(author);
        result.setItemMeta(meta);
        return true;
    }
}