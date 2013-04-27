package ro.thehunters.digi.recipeManager.flags;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.data.Book;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagGetRecipeBook extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.GETRECIPEBOOK;
        
        A = new String[]
        {
            "{flag} <book> [#volume]",
        };
        
        D = new String[]
        {
            "Overwrites result with the specified recipe book.",
            "",
            "For the '<book>' argument you need to specify the book ID or if ID is not set for that book you can specify the title.",
            "You can write partial IDs or titles and the book will still be found.",
            "",
            "Optionally you can set the book's volume by adding '#' along with a number from 1 to max volumes of the book.",
        };
        
        E = new String[]
        {
            "{flag} recipe stuff // matches a 'Recipe Stuff' book for example.",
            "{flag} vanillarec #2 // matches a 'Vanilla Recipes Volume 2' book for example.",
        };
    }
    
    // Flag code
    
    private String bookName = null;
    private int volume = 1;
    
    public FlagGetRecipeBook()
    {
    }
    
    public FlagGetRecipeBook(FlagGetRecipeBook flag)
    {
        bookName = flag.bookName;
    }
    
    @Override
    public FlagGetRecipeBook clone()
    {
        return new FlagGetRecipeBook(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public String getBookName()
    {
        return bookName;
    }
    
    public void setBookName(String name)
    {
        Validate.notNull(name, "The 'name' argument must not be null!");
        
        bookName = name;
    }
    
    public int getVolume()
    {
        return volume;
    }
    
    public void setVolume(int volume)
    {
        this.volume = Math.max(volume, 1);
    }
    
    @Override
    protected boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getType() != Material.WRITTEN_BOOK || result.getItemMeta() instanceof BookMeta == false)
        {
            return RecipeErrorReporter.error("Flag " + getType() + " needs a WRITTEN_BOOK to work!");
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String bookName = value;
        int index = value.lastIndexOf('#');
        
        if(index > 0) // found and it's not the first char
        {
            value = value.substring(index + 1);
            bookName = bookName.substring(0, index).trim();
            
            try
            {
                setVolume(Integer.valueOf(value));
            }
            catch(NumberFormatException e)
            {
                RecipeErrorReporter.warning("Flag " + getType() + " has invalid volume number: " + value);
            }
        }
        
        setBookName(bookName);
        
        return true;
    }
    
    @Override
    protected void onRegistered()
    {
        Messages.debug("registering...");
        
        List<Book> books = RecipeManager.getRecipeBooks().getBooksPartialMatch(getBookName());
        
        if(books.isEmpty())
        {
            RecipeErrorReporter.error("Flag " + getType() + " could not find book title containing '" + bookName + "', flag removed.");
            remove();
            return;
        }
        
        Book book = books.get(0);
        
        if(books.size() > 1)
        {
            RecipeErrorReporter.warning("Flag " + getType() + " found " + books.size() + " books matching '" + bookName + "', using first: " + book.getTitle());
        }
    }
    
    @Override
    protected void onPrepare(Args a)
    {
        if(getBookName() == null)
        {
            a.addCustomReason("Book name not set!");
            return;
        }
        
        if(!a.hasResult())
        {
            a.addCustomReason("Need result!");
            return;
        }
        
        List<Book> books = RecipeManager.getRecipeBooks().getBooksPartialMatch(getBookName());
        
        if(books.isEmpty())
        {
            RecipeErrorReporter.error("Flag " + getType() + " could not find book title containing: " + bookName);
            return;
        }
        
        Book book = books.get(0);
        
        if(books.size() > 1)
        {
            RecipeErrorReporter.warning("Flag " + getType() + " found " + books.size() + " books matching '" + bookName + "', using first: " + book.getTitle());
        }
        
        ItemMeta meta = a.result().getItemMeta();
        
        if(meta instanceof BookMeta == false)
        {
            a.addCustomReason("Result not a written book!");
            RecipeErrorReporter.warning("Flag " + getType() + " was triggered on a non-writtenbook item!");
            return;
        }
        
        a.result().setItemMeta(book.getBookItem(volume).getItemMeta());
    }
    
    /*
    @Override
    protected void onCrafted(Args a)
    {
        if(book == null)
        {
            a.addCustomReason("Flag not configured; book is null!");
            return;
        }
        
        if(a.inventory() instanceof CraftingInventory)
        {
            CraftingInventory inv = (CraftingInventory)a.inventory();
            
            Messages.debug("testing workbench...");
            
            inv.setResult(new ItemStack(Material.ARROW));
            
    //            inv.setResult(book.getBookItem(volume));
            
            return;
        }
        else if(a.inventory() instanceof FurnaceInventory)
        {
            FurnaceInventory inv = (FurnaceInventory)a.inventory();
            
            Messages.debug("testing furnace...");
            
            inv.setResult(new ItemStack(Material.CACTUS));
            
            return;
        }
        else
        {
            Messages.debug("unknown inventory = " + a.inventory());
        }
        
        a.addCustomReason("Need inventory!");
    }
    */
}
