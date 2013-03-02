package ro.thehunters.digi.recipeManager.flags;

public class FlagOverride extends Flag
{
    public FlagOverride()
    {
        type = FlagType.OVERRIDE;
    }
    
    @Override
    public boolean onParse(String value)
    {
        return true;
    }
}