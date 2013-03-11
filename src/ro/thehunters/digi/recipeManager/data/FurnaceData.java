package ro.thehunters.digi.recipeManager.data;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

@SerializableAs("RM_FurnaceData")
public class FurnaceData implements ConfigurationSerializable
{
    static
    {
        ConfigurationSerialization.registerClass(FurnaceData.class, "RM_FurnaceData");
    }
    
    private String    smelter;
    private String    fueler;
    
    private ItemStack smelt;
    private ItemStack fuel;
    
    private int       burnTime;
    private float     cookTime;
    
    public FurnaceData()
    {
    }
    
    public FurnaceData(String smelter, String fueler, ItemStack smelt, ItemStack fuel)
    {
        this.smelter = smelter;
        this.fueler = fueler;
        this.smelt = smelt;
        this.fuel = fuel;
    }
    
    @SuppressWarnings("unchecked")
    public FurnaceData(Map<String, Object> map)
    {
        try
        {
            smelter = (map.containsKey("smelter") ? (String)map.get("smelter") : null);
            fueler = (map.containsKey("fueler") ? (String)map.get("fueler") : null);
            
            smelt = (map.containsKey("smelt") ? ItemStack.deserialize((Map<String, Object>)map.get("smelt")) : null);
            fuel = (map.containsKey("fuel") ? ItemStack.deserialize((Map<String, Object>)map.get("fuel")) : null);
        }
        catch(Exception e)
        {
        }
    }
    
    public String getFueler()
    {
        return fueler;
    }
    
    public void setFueler(String fueler)
    {
        this.fueler = fueler;
    }
    
    public String getSmelter()
    {
        return smelter;
    }
    
    public void setSmelter(String smelter)
    {
        this.smelter = smelter;
    }
    
    public ItemStack getSmelt()
    {
        return smelt;
    }
    
    public void setSmelt(ItemStack smelt)
    {
        this.smelt = smelt;
    }
    
    public ItemStack getFuel()
    {
        return fuel;
    }
    
    public void setFuel(ItemStack fuel)
    {
        this.fuel = fuel;
    }
    
    public int getBurnTime()
    {
        return burnTime;
    }
    
    public boolean isBurning()
    {
        return burnTime > 0;
    }
    
    public void setBurnTime(int burnTime)
    {
        this.burnTime = burnTime;
    }
    
    public float getCookTime()
    {
        return cookTime;
    }
    
    public void setCookTime(float cookTime)
    {
        this.cookTime = cookTime;
    }
    
    @Override
    public Map<String, Object> serialize()
    {
        Map<String, Object> map = new HashMap<String, Object>(4);
        
        if(smelter != null)
            map.put("smelter", smelter);
        
        if(fueler != null)
            map.put("fueler", fueler);
        
        if(smelt != null)
            map.put("smelt", smelt.serialize());
        
        if(fuel != null)
            map.put("fuel", fuel.serialize());
        
        return map;
    }
    
    public static FurnaceData deserialize(Map<String, Object> map)
    {
        return new FurnaceData(map);
    }
    
    public static FurnaceData valueOf(Map<String, Object> map)
    {
        return new FurnaceData(map);
    }
}