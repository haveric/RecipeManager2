package digi.recipeManager.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import digi.recipeManager.Messages;
import digi.recipeManager.Tools;
import digi.recipeManager.recipes.flags.Flags;

public class WorkbenchRecipe extends BaseRecipe
{
    private List<ItemResult> results;
    
    protected WorkbenchRecipe()
    {
    }
    
    public WorkbenchRecipe(BaseRecipe recipe)
    {
        super(recipe);
    }
    
    public WorkbenchRecipe(Flags flags)
    {
        super(flags);
    }
    
    public boolean hasResults()
    {
        return results != null && !results.isEmpty();
    }
    
    public List<ItemResult> getResults()
    {
        return results;
    }
    
    public void setResult(ItemStack result)
    {
        results = new ArrayList<ItemResult>();
        results.add(new ItemResult(result, 100));
    }
    
    public void setResults(List<ItemResult> results)
    {
        this.results = results;
    }
    
    public ItemStack getFirstResult()
    {
        return results.get(0);
    }
    
    /*
    public ItemStack getCraftResult(Player player)
    {
        if(results.size() == 1)
            return getFirstResult();
        
        int maxChance = 0;
        
        for(ItemResult item : results)
        {
            if(item.canCraftResult(player))
                maxChance += item.getChance();
        }
        
        int rand = RecipeManager.rand.nextInt(maxChance);
        int chance = 0;
        ItemStack result = null;
        
        for(ItemResult item : results)
        {
            if((chance += item.getChance()) > rand)
            {
                result = item;
                break;
            }
        }
        
        return result;
    }
    */
    
    public ItemStack getResult(Player player, String playerName, Location location, boolean display)
    {
        /*
        if(results.size() == 1)
            return getFirstResult();
        
        ItemStack item = new ItemStack(Material.PORTAL, 0);
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<String>();
        int secretChance = 0;
        
        for(ItemResult result : results)
        {
            if(result.canSeeResult(player))
                secretChance += result.getChance();
            else
                lore.add(result.print());
        }
        
        if(!lore.isEmpty())
        {
            lore.add(String.format("%s %3d%% %sSecret item(s)...", ChatColor.DARK_RED, secretChance, ChatColor.RED));
            meta.setDisplayName(ChatColor.GOLD + "You will get a random item:");
            meta.setLore(lore);
        }
        else
            meta.setDisplayName(ChatColor.GOLD + "You will get an unknown item!");
        
        item.setItemMeta(meta);
        
        return item;
        */
        
        // TODO
        
        ItemStack result = getFirstResult(); //null;
        
        if(display)
        {
            List<String> reasons = new ArrayList<String>();
            
            checkFlags(player, playerName, location, reasons);
            
            if(!reasons.isEmpty())
            {
                return Tools.generateItemStackWithMeta(Material.FIRE, 0, 1, Messages.CRAFT_RESULT_FAILED_TITLE.get(), reasons);
            }
            
            List<String> noCraftReasons = new ArrayList<String>();
            List<ItemResult> viewable = new ArrayList<ItemResult>();
            Map<ItemResult, String> unallowed = new HashMap<ItemResult, String>();
            int unallowedChance = 0;
            int viewableChance = 0;
            int secretChance = 0;
            
            for(ItemResult r : results)
            {
                if(r.getFlags().isSecret())
                {
                    secretChance += r.getChance();
                }
                else
                {
                    r.checkFlags(player, playerName, location, reasons);
                    
                    if(reasons == null)
                    {
                        viewable.add(r);
                        viewableChance += r.getChance();
                    }
                    else
                    {
                        unallowed.put(r, reasons.get(0));
                        unallowedChance += r.getChance();
                    }
                }
            }
            
            List<String> lore = new ArrayList<String>();
            
            for(ItemResult r : viewable)
            {
                lore.add(Messages.CRAFT_RESULT_RECIEVE_ITEM.get("{chance}", String.format("%3d%%", r.getChance()), "{item}", r.print()));
            }
            
            if(secretChance > 0)
            {
                lore.add(Messages.CRAFT_RESULT_RECIEVE_SECRETS.get("{chance}", String.format("%3d%%", secretChance)));
            }
            
            if(unallowed.size() > 0)
            {
                if(getFlags().isHideUnallowed())
                {
                    lore.add(Messages.CRAFT_RESULT_UNALLOWED_HIDDEN.get("{chance}", String.format("%3d%%", unallowedChance)));
                }
                else
                {
                    lore.add("");
                    lore.add(Messages.CRAFT_RESULT_UNALLOWED_TITLE.get());
                    
                    for(Entry<ItemResult, String> entry : unallowed.entrySet())
                    {
                        lore.add(Messages.CRAFT_RESULT_UNALLOWED_ITEM.get("{chance}", String.format("%3d%%", entry.getKey().getChance()), "{item}", entry.getKey().print(), "{reason}", entry.getValue()));
                    }
                }
            }
            
            if(viewable.isEmpty())
            {
                if(secretChance > 0)
                    return Tools.generateItemStackWithMeta(Material.PORTAL, 0, 1, Messages.CRAFT_RESULT_UNKNOWN.get(), lore);
                else
                {
                    lore.clear();
                    
                    for(String s : noCraftReasons)
                    {
                        lore.add(Messages.CRAFT_RESULT_FAILED_REASON.get("{reason}", s));
                    }
                    
                    return Tools.generateItemStackWithMeta(Material.PORTAL, 0, 1, Messages.CRAFT_RESULT_FAILED_TITLE.get(), lore);
                }
            }
            else
                return Tools.generateItemStackWithMeta(Material.PORTAL, 0, 1, Messages.CRAFT_RESULT_RECIEVE_TITLE.get(), lore);
            
            // -------------------------------------------
            
            /*
            List<String> noCraftReasons = new ArrayList<String>();
            List<ItemResult> viewable = new ArrayList<ItemResult>();
            Map<ItemResult, String> unallowed = new HashMap<ItemResult, String>();
            String reason = isCraftable(player, location);
            int unallowedChance = 0;
            int viewableChance = 0;
            int secretChance = 0;
            
            if(reason != null)
                noCraftReasons.add(reason);
            
            for(ItemResult r : results)
            {
                if(r.getFlags().isSecret())
                {
                    secretChance += r.getChance();
                }
                else
                {
                    reason = r.canCraftResult(player);
                    
                    if(reason == null)
                    {
                        viewable.add(r);
                        viewableChance += r.getChance();
                    }
                    else
                    {
                        unallowed.put(r, reason);
                        unallowedChance += r.getChance();
                    }
                }
            }
            
            List<String> lore = new ArrayList<String>();
            
            for(ItemResult r : viewable)
            {
                lore.add(Messages.CRAFT_MULTIRESULT_RECIEVE_ITEM.get("{chance}", String.format("%3d%%", r.getChance()), "{item}", r.print()));
            }
            
            if(secretChance > 0)
            {
                lore.add(Messages.CRAFT_MULTIRESULT_RECIEVE_SECRETS.get("{chance}", String.format("%3d%%", secretChance)));
            }
            
            if(unallowed.size() > 0)
            {
                if(getFlags().isHideUnallowed())
                {
                    lore.add(Messages.CRAFT_MULTIRESULT_UNALLOWED_HIDDEN.get("{chance}", String.format("%3d%%", unallowedChance)));
                }
                else
                {
                    lore.add("");
                    lore.add(Messages.CRAFT_MULTIRESULT_UNALLOWED_TITLE.get());
                    
                    for(Entry<ItemResult, String> entry : unallowed.entrySet())
                    {
                        lore.add(Messages.CRAFT_MULTIRESULT_UNALLOWED_ITEM.get("{chance}", String.format("%3d%%", entry.getKey().getChance()), "{item}", entry.getKey().print(), "{reason}", entry.getValue()));
                    }
                }
            }
            
            if(viewable.isEmpty())
            {
                if(secretChance > 0)
                    return generateItemStackWithMeta(Material.PORTAL, 0, 1, Messages.CRAFT_MULTIRESULT_UNKNOWN.get(), lore);
                else
                {
                    lore.clear();
                    
                    for(String s : noCraftReasons)
                    {
                        lore.add(Messages.CRAFT_MULTIRESULT_NONE_REASON.get("{reason}", s));
                    }
                    
                    return generateItemStackWithMeta(Material.PORTAL, 0, 1, Messages.CRAFT_MULTIRESULT_NONE_TITLE.get(), lore);
                }
            }
            else
                return generateItemStackWithMeta(Material.PORTAL, 0, 1, Messages.CRAFT_MULTIRESULT_RECIEVE_TITLE.get(), lore);
            */
        }
        else
        {
            /*
            List<ItemResult> pick = new ArrayList<ItemResult>();
            int totalChance = 0;
            
            for(ItemResult r : results) // put a list together of what results player is allowed to craft...
            {
                if(r.canCraftResult(player))
                {
                    pick.add(r);
                    totalChance += r.getChance();
                }
            }
            
            if(totalChance <= 0)
                return null; // can't craft anything from the results
                
            int rand = RecipeManager.rand.nextInt(totalChance);
            int chance = 0;
            
            for(ItemResult r : pick)
            {
                if((chance += r.getChance()) > rand)
                {
                    result = r;
                    break;
                }
            }
            */
        }
        
        return result;
    }
}