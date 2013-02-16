package digi.recipeManager.recipes.flags;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

public class ItemFlags extends Flags
{
    private Object[] map = null;
    
    public ItemFlags()
    {
    }
    
    public ItemFlags(Flags flags)
    {
        super(flags);
        
        if(flags instanceof ItemFlags)
        {
            ItemFlags f = (ItemFlags)flags;
            
            map = f.map;
        }
    }
    
    @Override
    public ItemFlags clone()
    {
        return new ItemFlags(this);
    }
    
    public Object[] getMap()
    {
        return map;
    }
    
    public void setMap(Object[] map)
    {
        this.map = map;
    }
    
    public boolean applyMapScale(ItemStack item)
    {
        if(map == null)
            return true;
        
        if(item == null || item.getType() != Material.MAP)
            return false;
        
        MapView mapView = Bukkit.getMap(item.getDurability());
        
        Scale scale = (Scale)map[0];
        String world = (String)map[1];
        int x = (Integer)map[2];
        int z = (Integer)map[3];
        
        mapView.setScale(scale);
        mapView.setWorld(Bukkit.getWorld(world));
        mapView.setCenterX(x);
        mapView.setCenterZ(z);
        
        return true;
    }
}