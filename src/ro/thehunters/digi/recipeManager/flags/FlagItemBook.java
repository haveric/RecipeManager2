package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.BookMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagItemBook extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.ITEMBOOK;
        
        A = new String[]
        {
            "{flag} <title | author>",
        };
        
        D = new String[]
        {
            "Changes book's title and author.",
            "",
            "Supports colors and format (e.g. <red>, <blue>, &4, &F, etc).",
            "Title and author must not exceed 64 characters individually, colors use 2 character each.",
            "",
            "Specific items: written book",
        };
        
        E = new String[]
        {
            "{flag} The Art of Stealing | Gray Fox",
        };
    }
    
    // Flag code
    
    private BookMeta bookMeta; // TODO
    
    public FlagItemBook()
    {
    }
    
    public FlagItemBook(FlagItemBook flag)
    {
        // TODO clone
    }
    
    @Override
    public FlagItemBook clone()
    {
        return new FlagItemBook(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
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
    
    // TODO pages too
    /*
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
        
        if(value.equalsIgnoreCase("false"))
        {
            String text = Tools.parseColors(value, false);
            
            if(text.length() > 256)
            {
                RecipeErrorReporter.warning("Flag @" + getType() + " has page with text longer than 256 characters, trimmed to size.", "Color codes use up that limit too, 1 character per color.");
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
    */
}
