package ro.thehunters.digi.recipeManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class RecipeBooks
{
    private Map<String, BookMeta> books = new HashMap<String, BookMeta>();
    
    public RecipeBooks()
    {
    }
    
    public ItemStack getBook()
    {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
        
        BookMeta meta = books.get("");
        
        meta.setLore(Arrays.asList("Recipe book" + ChatColor.BLACK + " by RecipeManager"));
        
        item.setItemMeta(meta);
        
        return item;
    }
}
