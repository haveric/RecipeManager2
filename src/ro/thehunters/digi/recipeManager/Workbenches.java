package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.data.BlockID;

public class Workbenches
{
    private static final Map<String, BlockID> workbenches = new HashMap<String, BlockID>();
    
    static void init()
    {
    }
    
    static void clear()
    {
        workbenches.clear();
    }
    
    public static void add(Player player, Location location)
    {
        if(player == null)
            return;
        
        Validate.notNull(location, "location argument must not be null!");
        
        workbenches.put(player.getName(), new BlockID(location));
    }
    
    public static void remove(Player player)
    {
        if(player == null)
            return;
        
        workbenches.remove(player.getName());
    }
    
    public static Location get(Player player)
    {
        if(player == null)
            return null;
        
        BlockID blockID = workbenches.get(player.getName());
        Location playerLoc = player.getLocation();
        
        if(blockID == null || !blockID.getWorldID().equals(player.getWorld().getUID()))
            return playerLoc;
        
        Block block = blockID.toBlock();
        
        if(block.getType() != Material.WORKBENCH)
        {
            // Workbench doesn't exist anymore
            workbenches.remove(player.getName());
            return playerLoc;
        }
        
        Location loc = block.getLocation();
        
        return (loc == null || loc.distanceSquared(playerLoc) > 36 ? playerLoc : loc); // 6 squared
    }
}