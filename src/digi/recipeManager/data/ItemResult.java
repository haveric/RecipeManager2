package digi.recipeManager.data;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemResult extends ItemStack
{
    private Flags flags  = new Flags();
    private int   chance = -1;
    
    public ItemResult()
    {
    }
    
    public ItemResult(ItemStack item)
    {
        super(item);
    }
    
    public ItemResult(ItemStack item, int chance)
    {
        super(item);
        
        setChance(chance);
    }
    
    public ItemResult(Material type, int amount, int data, int chance)
    {
        super(type, amount, (short)data);
        
        setChance(chance);
    }
    
    public ItemResult(ItemStack item, Flags flags)
    {
        super(item);
        
        this.flags = flags;
    }
    
    public void setItemStack(ItemStack item)
    {
        setTypeId(item.getTypeId());
        setDurability(item.getDurability());
        setAmount(item.getAmount());
        setItemMeta(item.getItemMeta());
    }
    
    public Flags getFlags()
    {
        return flags;
    }
    
    public void setChance(int chance)
    {
        this.chance = chance;
    }
    
    public int getChance()
    {
        return chance;
    }
    
    public boolean canSeeResult(Player player)
    {
        if(player == null)
            return true;
        
        if(flags.isSecret())
            return false;
        
        return canCraftResult(player);
    }
    
    public boolean canCraftResult(Player player)
    {
        if(player == null)
            return true;
        
        // TODO
        
        return true;
    }
    
    public String print()
    {
        return String.format("%s %3d%% %s%s%s%s", ChatColor.DARK_RED, getChance(), (getEnchantments().size() > 0 ? ChatColor.LIGHT_PURPLE : ChatColor.GREEN), getType().toString(), (getDurability() > 0 ? ":" + getDurability() : ""), (getAmount() > 1 ? " x " + getAmount() : ""));
    }
}
