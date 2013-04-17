package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagBiome extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.BIOME;
        
        A = null;
        D = null;
        E = null;
    }
    
    // Flag code
    
    private Map<Biome, Boolean> biomes = new HashMap<Biome, Boolean>();
    private Map<Short, Boolean> humidity = new HashMap<Short, Boolean>();
    private Map<Short, Boolean> temperature = new HashMap<Short, Boolean>();
    
    public FlagBiome()
    {
    }
    
    public FlagBiome(FlagBiome flag)
    {
        // TODO clone
    }
    
    @Override
    public FlagBiome clone()
    {
        return new FlagBiome(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        // TODO
        
        RecipeErrorReporter.warning("Flag " + getType() + " is not yet coded.");
        
        return false;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        if(!a.hasLocation())
        {
            a.addCustomReason("Needs location!");
            return;
        }
        
        Block block = a.location().getBlock();
        
        block.getBiome();
        block.getHumidity();
        block.getTemperature();
        
        // TODO !
    }
}
