package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagRecipeBook extends Flag
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
    
    private String title;
    private String description;
    
    public FlagRecipeBook()
    {
        type = FlagType.RECIPEBOOK;
    }
    
    public FlagRecipeBook(FlagRecipeBook flag)
    {
        this();
        
        title = flag.title;
        description = flag.description;
    }
    
    @Override
    public FlagRecipeBook clone()
    {
        return new FlagRecipeBook(this);
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
            RecipeErrorReporter.error("Flag " + getType() + " can only be used in file header !");
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