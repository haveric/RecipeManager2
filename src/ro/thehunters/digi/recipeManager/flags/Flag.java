package ro.thehunters.digi.recipeManager.flags;

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
    
    // Tools/final methods
    
    /**
     * Get flag type
     * 
     * @return
     */
    final public FlagType getType()
    {
        return type;
    }
    
    /**
     * Removes the flag from its flag list container.<br>
     * If the flag hasn't been added to any flag list, this method won't do anything.
     */
    final public void removeFlag()
    {
        if(flagsContainer != null)
            flagsContainer.removeFlag(this);
    }
    
    /**
     * @return Flags object that contains this flag.
     */
    final public Flags getFlagsContainer()
    {
        return flagsContainer;
    }
    
    final protected Flaggable getFlaggable()
    {
        return (flagsContainer != null ? flagsContainer.flaggable : null);
    }
    
    final protected BaseRecipe getRecipe()
    {
        Flaggable flaggable = getFlaggable();
        
        return (flaggable instanceof BaseRecipe ? (BaseRecipe)flaggable : null);
    }
    
    final protected ItemResult getResult()
    {
        Flaggable flaggable = getFlaggable();
        
        return (flaggable instanceof ItemResult ? (ItemResult)flaggable : null);
    }
    
    final public boolean validateAdd()
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
    
    final public boolean validateParse(String value)
    {
        if(getType() == null)
            return false;
        
        if(!getType().hasBit(Bit.NO_VALUE) && value == null)
        {
            RecipeErrorReporter.error("Flag " + getType() + " needs a value!");
            return false;
        }
        
        if(!getType().hasBit(Bit.NO_FALSE) && value != null && (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("remove")))
        {
            removeFlag();
            return false;
        }
        
        return validateAdd();
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
    
    // Overwriteable methods/events
    
    /**
     * The last flag-specific validation if it can be added to flag list
     * 
     * @return
     */
    public boolean onValidate()
    {
        return (getType() != null);
    }
    
    /**
     * Parses a string to get the values for this flag.
     * Has diferent effects for each extension of Flag object.
     * 
     * @param value
     *            the flag's value (not containing the <code>@flag</code> string)
     * @param recipeType
     *            for verification, can be null
     * @param item
     *            for verification, can be null
     * @return false if an error occured and the flag should not be added
     */
    public boolean onParse(String value)
    {
        return false; // it didn't parse anything
    }
    
    /**
     * Triggered when flag is removed
     */
    public void onRemove()
    {
    }
    
    /**
     * Check if the flag allows to craft with these arguments.<br>
     * Any and all arguments can be null if you don't have values for them.<br>
     * To make the check fail you <b>must</b> add a reason to the argument!
     * 
     * @param a
     *            the arguments class for easily maintainable argument class
     */
    public void onCheck(Arguments a)
    {
    }
    
    /**
     * Apply the flag's effects to the arguments.<br>
     * Any and all arguments can be null if you don't have values for them.<br>
     * To make the check fail you <b>must</b> add a reason to the argument!
     * 
     * @param a
     *            the arguments class for easily maintainable argument class
     */
    public void onApply(Arguments a)
    {
    }
    
    /**
     * Triggered when recipe fails by chance (multi-result)
     * Any and all arguments can be null if you don't have values for them.<br>
     * Adding reasons to this will display them to the crafter.
     * 
     * @param a
     */
    public void onFailed(Arguments a)
    {
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
            throw new RuntimeException("This should be impossible...");
        }
    }
}