package ro.thehunters.digi.recipeManager.flags;

public class FlagRemove extends Flag
{
    public FlagRemove()
    {
        type = FlagType.REMOVE;
    }
    
    @Override
    public boolean onParse(String value)
    {
        return true;
    }
}