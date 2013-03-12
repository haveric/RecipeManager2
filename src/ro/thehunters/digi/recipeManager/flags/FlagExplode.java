package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Location;

import ro.thehunters.digi.recipeManager.RecipeManager;

public class FlagExplode extends Flag
{
    private float   power       = 2f;
    private float   chance      = 100;
    private boolean fireDamage  = false;
    private boolean worldDamage = false;
    
    public FlagExplode()
    {
        type = FlagType.EXPLODE;
    }
    
    public FlagExplode(FlagExplode flag)
    {
        this();
        
        // TODO clone
    }
    
    @Override
    public FlagExplode clone()
    {
        return new FlagExplode(this);
    }
    
    @Override
    protected boolean onParse(String value)
    {
        // TODO
        return true;
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        if(a.hasLocation())
            return false; // did not apply
            
        if(chance >= 100 || (RecipeManager.random.nextFloat() * 100) <= chance)
        {
            Location l = a.location();
            
            l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), power, fireDamage, worldDamage);
        }
        
        return true;
    }
}
