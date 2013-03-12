package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
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
    
    public boolean addEntity(EntityType ent, int amount)
    {
        Validate.notNull(ent);
        
        if(!ent.isSpawnable())
        {
            RecipeErrorReporter.error("The " + type + " flag has unspawnable entity type: " + ent);
            return false;
        }
        
        if(amount <= 0)
        {
            amount = 1;
            RecipeErrorReporter.warning("The " + type + " flag can't have less than 1 amount, set to 1.");
        }
        
        entities.put(ent, amount);
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.split(" ");
        value = split[0].trim();
        EntityType ent;
        int amount = 1;
        
        try
        {
            ent = EntityType.valueOf(value.toUpperCase());
        }
        catch(Exception e)
        {
            RecipeErrorReporter.error("The " + type + " flag has invalid entity type: " + value);
            return false;
        }
        
        if(split.length > 1)
        {
            value = split[1].trim();
            
            try
            {
                amount = Integer.valueOf(value);
            }
            catch(Exception e)
            {
                RecipeErrorReporter.error("The " + type + " flag has invalid amount number: " + value);
                return false;
            }
        }
        
        return addEntity(ent, amount);
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        Location l = a.location();
        
        if(l == null)
            return false;
        
        l = l.add(0, 1, 0);
        World w = l.getWorld();
        
        for(Entry<EntityType, Integer> e : entities.entrySet())
        {
            for(int i = 0; i < e.getValue(); i++)
            {
                w.spawnEntity(l, e.getKey()).playEffect(EntityEffect.WOLF_SMOKE);
            }
        }
        
        return true;
    }
}