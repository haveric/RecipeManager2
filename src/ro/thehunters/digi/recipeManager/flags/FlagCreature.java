package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagCreature extends Flag
{
    Map<EntityType, Integer> entities = new HashMap<EntityType, Integer>();
    
    public FlagCreature()
    {
        type = FlagType.CREATURE;
    }
    
    public FlagCreature(FlagCreature flag)
    {
        this();
        
        entities.putAll(flag.entities);
    }
    
    @Override
    public FlagCreature clone()
    {
        return new FlagCreature(this);
    }
    
    public Map<EntityType, Integer> getEntities()
    {
        return entities;
    }
    
    public void setEntities(Map<EntityType, Integer> entities)
    {
        this.entities = entities;
    }
    
    public void addEntity(EntityType ent, int amount)
    {
        if(!ent.isSpawnable())
        {
            RecipeErrorReporter.error("The @" + type + " flag has unspawnable entity type!");
            return;
        }
        
        entities.put(ent, amount);
    }
    
    @Override
    protected boolean onParse(String value)
    {
        EntityType ent;
        int num = 1;
        
        // TODO
        
        try
        {
            ent = EntityType.valueOf(value.toUpperCase());
        }
        catch(Exception e)
        {
            RecipeErrorReporter.error("The @" + type + " flag has invalid entity type: " + value);
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onApply(Arguments a)
    {
        Location l = a.location();
        
        if(l == null)
            return;
        
        l = l.add(0, 1, 0);
        World w = l.getWorld();
        
        for(Entry<EntityType, Integer> e : entities.entrySet())
        {
            for(int i = 0; i < e.getValue(); i++)
            {
                w.spawnEntity(l, e.getKey()).playEffect(EntityEffect.WOLF_SMOKE);
            }
        }
    }
}