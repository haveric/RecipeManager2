package ro.thehunters.digi.recipeManager.flags;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;

import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.data.Book;
import ro.thehunters.digi.recipeManager.ErrorReporter;
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
            "{flag} <book> [volume <num>]",
        };
        
        D = new String[]
        {
            "Overwrites result with the specified recipe book.",
            "",
            "For the '<book>' argument you need to specify the book ID or if ID is not set for that book you can specify the title.",
            "You can write partial IDs or titles and the book will still be found.",
            "",
            "Optionally you can set which volume to give, will give first by default, using a bigger number thant the number of volumes will pick the last volume.",
        };
        
        E = new String[]
        {
            "{flag} recipestu // matches a 'Recipe Stuff' book for example.",
            "{flag} vanilla rec volume 2 // matches a 'Vanilla Recipes Volume 2' book for example.",
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
            return ErrorReporter.error("Flag " + getType() + " needs a WRITTEN_BOOK to work!");
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        value = value.toLowerCase();
        String bookName = value;
        int index = value.lastIndexOf("volume");
        
        if(index > 0)
        {
            value = value.substring(index + "volume".length()).trim();
            
            try
            {
                setVolume(Integer.valueOf(value));
                bookName = bookName.substring(0, index).trim();
            }
            catch(NumberFormatException e)
            {
            }
        }
        
        setBookName(bookName);
        
        return true;
    }
    
    @Override
    protected void onRegistered()
    {
        List<Book> books = RecipeManager.getRecipeBooks().getBooksPartialMatch(getBookName());
        
        if(books.isEmpty())
        {
            ErrorReporter.warning("Flag " + getType() + " could not find book title containing '" + bookName + "', flag ignored.");
            remove();
            return;
        }
        
        Book book = books.get(0);
        
        if(books.size() > 1)
        {
            ErrorReporter.warning("Flag " + getType() + " found " + books.size() + " books matching '" + bookName + "', using first: " + book.getTitle());
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
            return;
        }
        
        Book book = books.get(0);
        
        a.result().setItemMeta(book.getBookItem(volume).getItemMeta());
    }
}
