package ro.thehunters.digi.recipeManager.flags;

public class FlagDebug extends Flag
{
    public FlagDebug()
    {
        type = FlagType.DEBUG;
    }
    
    @Override
    public boolean onParse(String value)
    {
        return true;
    }
}
