package ro.thehunters.digi.recipeManager.flags;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.data.Book;

public class FlagGetBook extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.GETBOOK;
        
        A = new String[]
        {
            "{flag} ...",
        };
        
        D = new String[]
        {
            "FLAG NOT YET IMPLEMENTED!",
        };
        
        E = new String[]
        {
            "{flag} ...",
        };
    }
    
    // Flag code
    
    private Book book = null;
    private int volume = 1;
    
    public FlagGetBook()
    {
    }
    
    public FlagGetBook(FlagGetBook flag)
    {
        // TODO clone
    }
    
    @Override
    public FlagGetBook clone()
    {
        return new FlagGetBook(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public Book getBook()
    {
        return book;
    }
    
    public void setBook(Book book)
    {
        this.book = book;
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
    protected boolean onParse(String value)
    {
        String bookName = value;
        int index = value.lastIndexOf('#');
        
        if(index > 0) // found and not the first character
        {
            value = value.substring(index + 1);
            bookName = bookName.substring(0, index);
            
            try
            {
                volume = Integer.valueOf(value);
            }
            catch(Exception e)
            {
                RecipeErrorReporter.warning("Flag " + getType() + " has invalid volume number: " + value);
            }
        }
        
        List<Book> books = RecipeManager.getRecipeBooks().getBooksPartialMatch(bookName);
        
        if(books.isEmpty())
        {
            RecipeErrorReporter.error("Flag " + getType() + " has book that does not exist: " + bookName);
            return false;
        }
        else if(books.size() > 1)
        {
            RecipeErrorReporter.warning("Flag " + getType() + " found " + books.size() + " books matching '" + bookName + "', using first: " + book.getTitle());
        }
        
        book = books.get(0);
        
        return true;
    }
    
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
}
