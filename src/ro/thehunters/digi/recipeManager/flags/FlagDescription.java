package ro.thehunters.digi.recipeManager.flags;

public class FlagDescription extends Flag
{
    private String description;
    
    public FlagDescription()
    {
        type = FlagType.DESCRIPTION;
    }
    
    @Override
    public FlagDescription clone()
    {
        FlagDescription clone = new FlagDescription();
        
        clone.description = description;
        
        return clone;
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
    public boolean onParse(String value)
    {
        setDescription(value);
        return true;
    }
}
