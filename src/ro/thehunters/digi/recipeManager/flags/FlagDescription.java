package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;

public class FlagDescription extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.DESCRIPTION;
        
        A = new String[]
        {
            "{flag} <text>",
        };
        
        D = new String[]
        {
            "Used to share a small description for the recipe, used in recipe books and recipe search.",
            "",
            "Using a description that is too long might damage the format of the book page.",
            "Please review the book that holds the recipe with this flag to be sure that it displays properly.",
        };
        
        E = new String[]
        {
            "{flag} <gray>Common recipe.",
        };
    }
    
    // Flag code
    
    private String description;
    
    public FlagDescription()
    {
    }
    
    public FlagDescription(FlagDescription flag)
    {
        description = flag.description;
    }
    
    @Override
    public FlagDescription clone()
    {
        return new FlagDescription(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
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
        
        if(recipe instanceof FuelRecipe)
        {
            RecipeErrorReporter.warning("Flag " + getType() + " does nothing on fuel recipes since fuels are listed in a single page in recipe books.");
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean onParse(String value)
    {
        setDescription(value);
        return true;
    }
}
