package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import ro.thehunters.digi.recipeManager.Messages;

public class FlagTest extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = null;
        D = null;
        E = null;
    }
    
    // Flag code
    
    public FlagTest()
    {
        type = FlagType.TEST;
    }
    
    public FlagTest(FlagTest flag)
    {
        this();
    }
    
    @Override
    public FlagTest clone()
    {
        return new FlagTest(this);
    }
    
    @Override
    protected boolean onParse(String value)
    {
        return true;
    }
    
    @Override
    protected void onCrafted(Args a)
    {
        Messages.debug("testing...");
        
        LivingEntity ent = (LivingEntity)a.location().getWorld().spawnEntity(a.location().add(0.5, 1, 0.5), EntityType.SHEEP);
        
        /*
        if(ent instanceof Creature)
        {
            ((Creature)ent).setTarget(a.player());
        }
        */
    }
}
