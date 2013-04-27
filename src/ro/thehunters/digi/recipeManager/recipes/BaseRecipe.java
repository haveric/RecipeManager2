package ro.thehunters.digi.recipeManager.recipes;

import org.bukkit.inventory.Recipe;

import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Recipes;
import ro.thehunters.digi.recipeManager.flags.Args;
import ro.thehunters.digi.recipeManager.flags.Flag;
import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.flags.Flaggable;
import ro.thehunters.digi.recipeManager.flags.Flags;

public class BaseRecipe implements Flaggable
{
    public enum RecipeType
    {
        ANY(null),
        CRAFT("craft"),
        COMBINE("combine"),
        WORKBENCH(null),
        SMELT("smelt"),
        FUEL("fuel"),
        SPECIAL("special");
        
        private final String directive;
        
        private RecipeType(String directive)
        {
            this.directive = directive;
        }
        
        public String getDirective()
        {
            return directive;
        }
    }
    
    protected String name;
    protected boolean customName;
    private Flags flags;
    protected int hash;
    
    public BaseRecipe()
    {
    }
    
    public BaseRecipe(BaseRecipe recipe)
    {
        this.flags = recipe.getFlags().clone(this);
    }
    
    public BaseRecipe(Flags flags)
    {
        this.flags = flags.clone(this);
    }
    
    /**
     * See: {@link Recipes #getRecipeInfo(BaseRecipe)}
     * 
     * @return
     *         Recipe info or null if doesn't exist
     */
    public RecipeInfo getInfo()
    {
        return RecipeManager.getRecipes().getRecipeInfo(this);
    }
    
    public RecipeType getType()
    {
        return null;
    }
    
    /**
     * Returns the auto-generated name or the custom name (if set) of the recipe.
     * 
     * @return recipe name, never null.
     */
    public String getName()
    {
        if(name == null)
        {
            resetName();
        }
        
        return name;
    }
    
    /**
     * If it's the auto-generated name it returns it without the ingredients.<br>
     * Returns full name if it's a custom name or a fuel recipe.
     * 
     * @return parsed generated name or original custom name
     */
    public String getNameNoIngredients()
    {
        String name = getName();
        
        if(customName || this instanceof FuelRecipe)
        {
            return name;
        }
        
        int first = name.indexOf('(') - 1; // trim a space too
        int last = name.lastIndexOf(')') + 1;
        
        if(first > 0 && last > 0)
        {
            name = name.substring(0, first) + name.substring(last);
        }
        
        return name;
    }
    
    /**
     * @return true if recipe has custom name or false if it's auto-generated.
     */
    public boolean hasCustomName()
    {
        return customName;
    }
    
    /**
     * Set the name of this recipe.
     * 
     * @param name
     *            should be an UNIQUE name
     */
    public void setName(String name)
    {
        this.name = name;
        customName = true;
    }
    
    /**
     * Reset name to the auto-generated one.
     */
    public void resetName()
    {
        name = "unknown recipe";
        customName = false;
    }
    
    public boolean isValid()
    {
        return false; // empty recipe, invalid!
    }
    
    public int getIndex()
    {
        return hash;
    }
    
    @Override
    public int hashCode()
    {
        return hash;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(obj == this)
        {
            return true;
        }
        
        if(obj == null || obj instanceof BaseRecipe == false)
        {
            return false;
        }
        
        return obj.hashCode() == hashCode();
    }
    
    /**
     * Register recipe with the server and RecipeManager.<br>
     * Alias for RecipeManager.getRecipes().registerRecipe(this);
     */
    public void register()
    {
        if(!isValid())
        {
            throw new IllegalArgumentException("Recipe is invalid ! Check ingredients and results.");
        }
        
        RecipeManager.getRecipes().registerRecipe(this);
    }
    
    /**
     * Remove this recipe from the server and from RecipeManager.<br>
     * Alias for: RecipeManager.getRecipes().removeRecipe(this);
     * 
     * @return if recipe was succesfully removed
     */
    public boolean remove()
    {
        return RecipeManager.getRecipes().removeRecipe(this);
    }
    
    /**
     * You usually won't need this, but just in case you do, here it is.
     * 
     * @return
     *         Bukkit API version of the recipe
     */
    public Recipe getBukkitRecipe()
    {
        return null;
    }
    
    // From Flaggable interface
    
    @Override
    public boolean hasFlag(FlagType type)
    {
        return (flags == null ? false : flags.hasFlag(type));
    }
    
    @Override
    public boolean hasFlags()
    {
        return (flags != null);
    }
    
    @Override
    public boolean hasNoShiftBit()
    {
        return (flags == null ? true : flags.hasNoShiftBit());
    }
    
    @Override
    public Flag getFlag(FlagType type)
    {
        return flags.getFlag(type);
    }
    
    @Override
    public <T extends Flag>T getFlag(Class<T> flagClass)
    {
        return flags.getFlag(flagClass);
    }
    
    @Override
    public Flags getFlags()
    {
        if(flags == null)
        {
            flags = new Flags(this);
        }
        
        return flags;
    }
    
    @Override
    public void addFlag(Flag flag)
    {
        flags.addFlag(flag);
    }
    
    @Override
    public boolean checkFlags(Args a)
    {
        return (flags == null ? true : flags.checkFlags(a));
    }
    
    @Override
    public boolean sendCrafted(Args a)
    {
        return (flags == null ? true : flags.sendCrafted(a));
    }
    
    @Override
    public boolean sendPrepare(Args a)
    {
        return (flags == null ? true : flags.sendPrepare(a));
    }
    
    /**
     * Notify flags that the recipe failed.
     * 
     * @param a
     */
    public void sendFailed(Args a)
    {
        if(flags != null)
        {
            flags.sendFailed(a);
        }
    }
    
    /**
     * @return Recipe short string for book contents index
     */
    public String printBookIndex()
    {
        return "undefined";
    }
    
    /**
     * @return Recipe detail string that can fit inside a book.
     */
    public String printBook()
    {
        return "undefined";
    }
}
