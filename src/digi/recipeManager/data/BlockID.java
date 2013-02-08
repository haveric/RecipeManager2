package digi.recipeManager.data;

import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;

public class BlockID implements Serializable
{
    private static final long serialVersionUID = 2935391237205153652L;
    private transient int     hash;
    
    private UUID              w;
    private int               x;
    private int               y;
    private int               z;
    
    public BlockID(Block block)
    {
        fromLocation(block.getLocation());
    }
    
    public BlockID(Location location)
    {
        fromLocation(location);
    }
    
    private void fromLocation(Location location)
    {
        w = location.getWorld().getUID();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();
        
        hash = new HashCodeBuilder().append(w).append(x).append(y).append(z).toHashCode();
    }
    
    public Block toBlock()
    {
        World world = Bukkit.getWorld(w);
        
        if(world == null)
            return null;
        
        return world.getBlockAt(x, y, z);
    }
    
    @Override
    public int hashCode()
    {
        return hash;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        
        if(obj == null)
            return false;
        
        if(obj instanceof BlockID == false)
            return false;
        
        BlockID b = (BlockID)obj;
        
        return (b.x == x && b.y == y && b.z == z && b.w.equals(w));
    }
}