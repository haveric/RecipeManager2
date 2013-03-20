package ro.thehunters.digi.recipeManager.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Utility;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Vanilla;

// TODO maybe remove this or implement it ???

public class ItemIngredient extends ItemStack
{
    /**
     * This method is the same as equals, but does not consider stack size (amount).<br>
     * This also does not consider data value if this stack's data value is the wildcard value.<br>
     * Use {@link Vanilla#DATA_WILDCARD} if you need to know that wildcard data value.
     * 
     * @param stack
     *            the item stack to compare to
     * @return true if the two stacks are equal, ignoring the amount
     */
    @Utility
    @Override
    public boolean isSimilar(ItemStack item)
    {
        if(item == null)
            return false;
        
        if(item == this)
            return true;
        
        return getTypeId() == item.getTypeId() && (getDurability() == Vanilla.DATA_WILDCARD ? true : getDurability() == item.getDurability()) && hasItemMeta() == item.hasItemMeta() && (hasItemMeta() ? Bukkit.getItemFactory().equals(getItemMeta(), item.getItemMeta()) : true);
    }
}
