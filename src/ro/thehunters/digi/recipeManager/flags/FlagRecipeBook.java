package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagRecipeBook extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.RECIPEBOOK;
        
        A = new String[]
        {
            "{flag} <title>",
            "{flag} <description>",
        };
        
        D = new String[]
        {
            "Define the book name and description that will host this recipe(s).",
            "This flag can only be used on file headers, can't be used on individual recipes.",
            "",
            "The first time specifying the argument will be the title, the 2nd time and onward will add to the book description lines.",
            "",
            "If you define the title of an existing book, that book will be used.",
        };
        
        E = new String[]
        {
            "{flag} Some New Stuff",
            "{flag} This book is awesome!",
            "{flag} Go to nex page!!!",
        };
    }
    
    // Flag code
    
    private String title;
    private String description;
    
    public FlagRecipeBook()
    {
    }
    
    public FlagRecipeBook(FlagRecipeBook flag)
    {
        title = flag.title;
        description = flag.description;
    }
    
    @Override
    public FlagRecipeBook clone()
    {
        return new FlagRecipeBook(this);
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
    
    /**
     * Set the book name.<br>
     * Setting this to null, "false" or "remove" will remove the flag from its container.
     * 
     * @param title
     */
    public void setTitle(String title)
    {
        if(title == null || title.equalsIgnoreCase("false") || title.equalsIgnoreCase("remove"))
        {
            remove();
            return;
        }
        
        this.title = title;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    @Override
    protected boolean onValidate()
    {
        BaseRecipe recipe = getRecipe();
        ItemResult result = getResult();
        
        if(recipe != null || result != null)
        {
            RecipeErrorReporter.error("Flag " + getType() + " can only be used in file header! (before any recipe is defined)");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        if(getTitle() == null)
        {
            setTitle(value);
        }
        else
        {
            setDescription((getDescription() == null ? "" : getDescription()) + value + '\n');
        }
        
        return true;
    }
}
