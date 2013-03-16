package ro.thehunters.digi.recipeManager.flags;

public class FlagNoShiftClick extends Flag
{
    public FlagNoShiftClick()
    {
        type = FlagType.NOSHIFTCLICK;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        return true;
    }
}
