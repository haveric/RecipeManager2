package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Tools;

public class FlagFailMessage extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[1];
        A[0] = "{flag} < ??? >";
        
        D = new String[1];
        D[0] = "Flag not yet documented.";
        
        E = null;
    }
    
    // Flag code
    
    private String message;
    
    public FlagFailMessage()
    {
        type = FlagType.FAILMESSAGE;
    }
    
    @Override
    public FlagFailMessage clone()
    {
        FlagFailMessage clone = new FlagFailMessage();
        
        clone.message = message;
        
        return clone;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    @Override
    public boolean onParse(String value)
    {
        setMessage(Tools.parseColors(value.replaceAll("\\n", "\n"), false));
        return true;
    }
    
    @Override
    public void onFailed(Args a)
    {
        a.addCustomReason(message);
    }
}
