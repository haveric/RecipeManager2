package ro.thehunters.digi.recipeManager.flags;

import org.apache.commons.lang.Validate;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.flags.FlagType.Bit;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class Flag implements Cloneable
{
    protected FlagType type;
    protected Flags    flagsContainer;
    
    public Flag()
    {
    }
    
    /*
     *  Public tools/final methods
     */
    
    /**
     * @return Flags object that contains this flag.
     */
    final public Flags getFlagsContainer()
    {
        return flagsContainer;
    }
    
    /**
     * Parses a string to get the values for this flag.
     * Has diferent effects for each extension of Flag object.
     * 
     * @param value
     *            the flag's value (not containing the <code>@flag</code> string)
     * @return false if an error occured and the flag should not be added
     */
    final public void parse(String value)
    {
        onParse(value);
    }
    
    /**
     * Check if the flag allows to craft with these arguments.<br>
     * Any and all arguments can be null if you don't have values for them.<br>
     * To make the check fail you <b>must</b> add a reason to the argument!
     * 
     * @param a
     *            the arguments class for easily maintainable argument class
     */
    final public void check(Arguments a)
    {
        onCheck(a);
    }
    
    /**
     * Apply the flag's effects to the arguments.<br>
     * Any and all arguments can be null if you don't have values for them.<br>
     * To make the check fail you <b>must</b> add a reason to the argument!
     * 
     * @param a
     *            the arguments class for easily maintainable argument class
     */
    final public void apply(Arguments a)
    {
        onApply(a);
    }
    
    /**
     * Trigger flag failure as if it failed due to multi-result chance.
     * Any and all arguments can be null if you don't have values for them.<br>
     * Adding reasons to this will display them to the crafter.
     * 
     * @param a
     */
    final public void failed(Arguments a)
    {
        onFailed(a);
    }
    
    /**
     * Removes the flag from its flag list container.<br>
     * This also notifies the flag of removal, it might do some stuff before removal.<br>
     * If the flag hasn't been added to any flag list, this method won't do anything.
     */
    final public void remove()
    {
        if(flagsContainer != null)
        {
            flagsContainer.removeFlag(this);
            onRemove();
        }
    }
    
    /**
     * Clones the flag and asigns it to a new flag container
     * 
     * @param container
     * @return
     */
    final public Flag clone(Flags container)
    {
        Flag flag = clone();
        flag.flagsContainer = container;
        return flag;
    }
    
    /*
     *  Non-public tools/final methods
     */
    
    final protected Flaggable getFlaggable()
    {
        return (flagsContainer != null ? flagsContainer.flaggable : null);
    }
    
    final protected BaseRecipe getRecipe()
    {
        Flaggable flaggable = getFlaggable();
        
        return (flaggable instanceof BaseRecipe ? (BaseRecipe)flaggable : null);
    }
    
    /*
    final protected BaseRecipe getRecipeDeep()
    {
        Flaggable flaggable = getFlaggable();
        
        if(flaggable instanceof BaseRecipe)
        {
            return (BaseRecipe)flaggable;
        }
        else
        {
            ItemResult result = getResult();
            
            if(result != null)
            {
                return result.getRecipe();
            }
        }
        
        return null;
    }
    */
    
    final protected ItemResult getResult()
    {
        Flaggable flaggable = getFlaggable();
        
        return (flaggable instanceof ItemResult ? (ItemResult)flaggable : null);
    }
    
    final protected boolean validateParse(String value)
    {
        Validate.notNull(getType());
        
        if(!getType().hasBit(Bit.NO_VALUE) && value == null)
        {
            RecipeErrorReporter.error("Flag " + getType() + " needs a value!");
            return false;
        }
        
        if(!getType().hasBit(Bit.NO_FALSE) && value != null && (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("remove")))
        {
            remove();
            return false;
        }
        
        return validate();
    }
    
    final protected boolean validate()
    {
        Flaggable flaggable = getFlaggable();
        
        if(getType().hasBit(Bit.RECIPE) && flaggable instanceof BaseRecipe == false)
        {
            RecipeErrorReporter.error("Flag " + getType() + " only works on recipes!");
            return false;
        }
        
        if(getType().hasBit(Bit.RESULT) && flaggable instanceof ItemResult == false)
        {
            RecipeErrorReporter.error("Flag " + getType() + " only works on results!");
            return false;
        }
        
        return onValidate();
    }
    
    /*
     *  Overwriteable methods/events
     */
    
    /**
     * Get flag type
     * 
     * @return
     */
    public FlagType getType()
    {
        return type;
    }
    
    @Override
    public Flag clone()
    {
        // TODO test
        // TODO maybe return new Flag() instead
        
        try
        {
            return (Flag)super.clone();
        }
        catch(CloneNotSupportedException e)
        {
            throw new RuntimeException("Unsupported cloning !");
        }
    }
    
    protected boolean onValidate()
    {
        return (getType() != null);
    }
    
    protected boolean onParse(String value)
    {
        return false; // it didn't parse anything
    }
    
    protected void onRemove()
    {
    }
    
    protected void onCheck(Arguments a)
    {
    }
    
    protected void onApply(Arguments a)
    {
    }
    
    protected void onFailed(Arguments a)
    {
    }
}