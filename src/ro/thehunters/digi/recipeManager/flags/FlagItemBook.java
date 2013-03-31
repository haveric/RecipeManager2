package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.BookMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagItemBook extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[1];
        A[0] = "{flag} <title | author or false>";
        
        D = new String[6];
        D[0] = "Changes book's title and author.";
        D[1] = "You can use 'false' to completly erase its title and pages.";
        D[2] = "Supports colors and format (e.g. <red>, <blue>, &4, &F, etc).";
        D[3] = "Title and author must not exceed 64 characters individually, colors use 2 character each.";
        D[4] = null;
        D[5] = "Specific items: written book";
        
        E = new String[2];
        E[0] = "{flag} The Art of Stealing | Gray Fox";
        E[1] = "{flag} false";
    }
    
    // Flag code
    
    public FlagItemBook()
    {
        type = FlagType.ITEMBOOK;
    }
    
    @Override
    protected boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof BookMeta == false)
        {
            RecipeErrorReporter.error("Flag " + getType() + " needs a WRITTEN_BOOK item!");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onRemove()
    {
        ItemResult result = getResult();
        BookMeta meta = (BookMeta)result.getItemMeta();
        meta.setTitle(null);
        meta.setAuthor(null);
        result.setItemMeta(meta);
    }
    
    @Override
    protected boolean onParse(String value)
    {
        ItemResult result = getResult();
        BookMeta meta = (BookMeta)result.getItemMeta();
        
        String[] split = value.split("\\|", 2);
        
        if(split.length != 2)
        {
            RecipeErrorReporter.error("Flag @" + getType() + " doesn't have title | author argument format!");
            return false;
        }
        
        String title = Tools.parseColors(split[0].trim(), false);
        String author = Tools.parseColors(split[1].trim(), false);
        
        if(title.length() > 64 || author.length() > 64)
        {
            RecipeErrorReporter.warning("Flag @" + getType() + " has title or author larger than 64 characters, trimmed to fit.");
            title = title.substring(0, (title.length() > 64 ? 64 : title.length()));
            author = author.substring(0, (author.length() > 64 ? 64 : author.length()));
        }
        
        meta.setTitle(title);
        meta.setAuthor(author);
        result.setItemMeta(meta);
        return true;
    }
}