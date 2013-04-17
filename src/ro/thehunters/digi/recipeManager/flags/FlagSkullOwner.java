package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.SkullMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagSkullOwner extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.SKULLOWNER;
        
        A = new String[]
        {
            "{flag} <name>",
        };
        
        D = new String[]
        {
            "Changes skull head's owner to change its skin.",
        };
        
        E = new String[]
        {
            "{flag} Notch",
        };
    }
    
    // Flag code
    
    private String owner; // TODO
    
    public FlagSkullOwner()
    {
    }
    
    public FlagSkullOwner(FlagSkullOwner flag)
    {
        // TODO clone
    }
    
    @Override
    public FlagSkullOwner clone()
    {
        return new FlagSkullOwner(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    @Override
    protected boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof SkullMeta == false || result.getDurability() != 3)
        {
            return RecipeErrorReporter.error("Flag " + getType() + " needs a SKULL_ITEM with data value 3 to work!");
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        ItemResult result = getResult();
        SkullMeta skull = (SkullMeta)result.getItemMeta();
        skull.setOwner(value);
        result.setItemMeta(skull);
        
        return true;
    }
}
