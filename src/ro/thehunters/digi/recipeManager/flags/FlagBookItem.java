package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.ErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagBookItem extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.BOOKITEM;
        
        A = new String[]
        {
            "{flag} title [text]",
            "{flag} author [text]",
            "{flag} addpage [text]",
        };
        
        D = new String[]
        {
            "Changes book's contents.",
            "Using this flag more than once will configure the same flag.",
            "",
            "Supports colors and format (e.g. <red>, <blue>, &4, &F, etc).",
            "",
            "Use 'title <text>' and 'author <text>' only on written books, it doesn't work on book and quill therefore they're optional.",
            "Title and author must not exceed 64 characters, colors included (2 chars each).",
            "",
            "Use 'addpage <text>' to add a new page, the text can contain \\n to add new lines to it, but it mainly word-wraps itself.",
            "Page contents must not exceed 256 characters, colors (2 chars each) and new line (1 char each) included.",
            "Optionally you can leave the text blank to add a blank page.",
            "",
            "Supported items: written book, book and quill.",
        };
        
        E = new String[]
        {
            "{flag} title The Art of Stealing",
            "{flag} author Gray Fox",
            "{flag} addpage <bold>O<reset>nce upon a time...",
            "{flag} addpage // added blank page",
            "{flag} addpage \\n\\n\\n\\n<italic>      The End.",
        };
    }
    
    // Flag code
    
    private String title;
    private String author;
    private List<String> pages = new ArrayList<String>(50);
    
    public FlagBookItem()
    {
    }
    
    public FlagBookItem(FlagBookItem flag)
    {
        title = flag.title;
        author = flag.author;
        pages.addAll(flag.pages);
    }
    
    @Override
    public FlagBookItem clone()
    {
        return new FlagBookItem(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = Tools.parseColors(title, false);
    }
    
    public String getAuthor()
    {
        return author;
    }
    
    public void setAuthor(String author)
    {
        this.author = Tools.parseColors(author, false);
    }
    
    public List<String> getPages()
    {
        return pages;
    }
    
    public void setPages(List<String> pages)
    {
        Validate.notNull(pages, "The 'pages' argument must not be null!");
        
        this.pages.clear();
        
        for(String page : pages)
        {
            addPage(page);
        }
    }
    
    public void addPage(String page)
    {
        this.pages.add(Tools.parseColors(page.replace("\\n", "\n"), false));
    }
    
    @Override
    protected boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof BookMeta == false)
        {
            ErrorReporter.error("Flag " + getType() + " needs a WRITTEN_BOOK or BOOK_AND_QUILL item!");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        int i = value.indexOf(' ');
        String key;
        
        if(i >= 0)
        {
            key = value.substring(0, i).trim().toLowerCase();
            value = value.substring(i).trim();
        }
        else
        {
            key = value.toLowerCase();
            value = "";
        }
        
        ItemStack result = getResult();
        boolean setTitle = key.equals("title");
        boolean setAuthor = !setTitle && key.equals("author");
        
        if(setTitle || setAuthor)
        {
            if(result.getType() == Material.BOOK_AND_QUILL)
            {
                ErrorReporter.warning("Flag " + getType() + " can not have title or author set on BOOK_AND_QUILL, only WRITTEN_BOOK.");
                return true;
            }
            
            if(value.length() > 64)
            {
                ErrorReporter.warning("Flag " + getType() + " has '" + (setTitle ? "title" : "author") + "' with over 64 characters, trimmed.");
                value = value.substring(0, 64);
            }
            
            if(setTitle)
            {
                setTitle(value);
            }
            else
            {
                setAuthor(value);
            }
        }
        else if(key.equals("addpage"))
        {
            if(pages.size() == 50)
            {
                ErrorReporter.warning("Flag " + getType() + " has over 50 pages added, they will be trimmed.");
            }
            
            if(value.length() > 256)
            {
                ErrorReporter.warning("Flag " + getType() + " has 'addpage' with over 256 characters! It will be trimmed.");
            }
            
            addPage(value);
        }
        
        return true;
    }
    
    @Override
    protected void onPrepare(Args a)
    {
        if(!a.hasResult())
        {
            a.addCustomReason("Need result!");
            return;
        }
        
        ItemMeta meta = a.result().getItemMeta();
        
        if(meta instanceof BookMeta == false)
        {
            a.addCustomReason("Needs BookMeta supported item!");
            return;
        }
        
        BookMeta bookMeta = (BookMeta)meta;
        
        bookMeta.setTitle(title);
        bookMeta.setAuthor(author);
        bookMeta.setPages(pages);
        
        a.result().setItemMeta(bookMeta);
    }
}
