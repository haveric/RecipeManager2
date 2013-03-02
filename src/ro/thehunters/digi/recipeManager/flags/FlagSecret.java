package ro.thehunters.digi.recipeManager.flags;

public class FlagSecret extends Flag
{
    public FlagSecret()
    {
        type = FlagType.SECRET;
    }
    
    @Override
    public boolean onParse(String value)
    {
        return true;
    }
}