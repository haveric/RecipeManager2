package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Location;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.RecipeManager;

public class FlagExplode extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[]
        {
            "{flag} <arguments or false>",
        };
        
        D = new String[]
        {
            "Makes the workbench/furnace/player explode when recipe is crafted.",
            "This flag can only be declared once per recipe and once per result.",
            "",
            "Replace <arguments> with the following arguments separated by | character:",
            "  power <0.0 to ...>     = Set the explosion power. TNT has 4.0 (default 2.0)",
            "  chance <0.0 to 100.0>% = Chance of the explosion to occur (default 100.0%)",
            "  fire                   = Explosion sets fires (defualt not set)",
            "  nobreak                = Makes explosion not break blocks (defualt not set)",
            "  fail                   = Explode if recipe failed as opposed to succeed (defualt not set)",
            "All arguments are optional and you can specify these arguments in any order.",
            "",
            "Using 'false' instead of arguments will disable the flag.",
        };
        
        E = new String[]
        {
            "{flag} // will explode when recipe succeeeds with power 2, 100% chance and breaks blocks",
            "{flag} nobreak | fire | chance 25% | power 6 // will explode 25% of time without block damage but sets fires",
            "{flag} fail | power 2 | chance 75% // will explode 75% of the time when recipe fails",
        };
    }
    
    // Flag code
    
    private float power = 2.0f;
    private float chance = 100.0f;
    private boolean fire = false;
    private boolean noBreak = false;
    private boolean failure = false;
    
    public FlagExplode()
    {
        type = FlagType.EXPLODE;
    }
    
    public FlagExplode(FlagExplode flag)
    {
        this();
        
        power = flag.power;
        chance = flag.chance;
        fire = flag.fire;
        noBreak = flag.noBreak;
        failure = flag.failure;
    }
    
    @Override
    public FlagExplode clone()
    {
        return new FlagExplode(this);
    }
    
    public float getPower()
    {
        return power;
    }
    
    public void setPower(float power)
    {
        this.power = power;
    }
    
    public float getChance()
    {
        return chance;
    }
    
    public void setChance(float chance)
    {
        this.chance = chance;
    }
    
    public boolean getFire()
    {
        return fire;
    }
    
    public void setFire(boolean fire)
    {
        this.fire = fire;
    }
    
    public boolean getFailure()
    {
        return failure;
    }
    
    public void setFailure(boolean failure)
    {
        this.failure = failure;
    }
    
    public boolean getNoBreak()
    {
        return noBreak;
    }
    
    public void setNoBreak(boolean noBreak)
    {
        this.noBreak = noBreak;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        if(value == null)
        {
            return true; // accepts null value
        }
        
        String[] args = value.toLowerCase().split("\\|");
        
        for(String arg : args)
        {
            arg = arg.trim();
            
            if(arg.equals("fire"))
            {
                setFire(true);
            }
            else if(arg.equals("fail"))
            {
                setFailure(true);
            }
            else if(arg.equals("nobreak"))
            {
                setNoBreak(true);
            }
            else if(arg.startsWith("power"))
            {
                String[] data = arg.split(" ", 2);
                
                if(data.length > 2)
                {
                    RecipeErrorReporter.warning("Flag " + getType() + " has 'power' argument with no value.");
                    continue;
                }
                
                value = data[1];
                
                try
                {
                    setPower(Float.valueOf(value));
                }
                catch(NumberFormatException e)
                {
                    RecipeErrorReporter.warning("Flag " + getType() + " has 'power' argument with invalid number: " + value);
                    continue;
                }
            }
            else if(arg.startsWith("chance"))
            {
                String[] data = arg.split(" ", 2);
                
                if(data.length > 2)
                {
                    RecipeErrorReporter.warning("Flag " + getType() + " has 'chance' argument with no value.");
                    continue;
                }
                
                value = data[1].trim();
                
                if(value.endsWith("%"))
                {
                    value = value.replace('%', ' ');
                }
                
                try
                {
                    setChance(Float.valueOf(value));
                }
                catch(NumberFormatException e)
                {
                    RecipeErrorReporter.warning("Flag " + getType() + " has 'chance' argument with invalid number: " + value);
                    continue;
                }
            }
            else
            {
                RecipeErrorReporter.warning("Flag " + getType() + " has unknown argument: " + arg);
            }
        }
        
        return true;
    }
    
    @Override
    protected void onCrafted(Args a)
    {
        if(!a.hasLocation())
        {
            a.addCustomReason("Need a location!");
            return;
        }
        
        if((failure && !a.hasResult()) || !failure)
        {
            if(getChance() >= 100 || (RecipeManager.random.nextFloat() * 100) <= chance)
            {
                Location l = a.location();
                
                l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), getPower(), getFire(), !getNoBreak());
            }
        }
    }
}
