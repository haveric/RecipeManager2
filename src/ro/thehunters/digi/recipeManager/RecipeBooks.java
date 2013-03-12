package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class RecipeBooks
{
    public class BookID
    {
        private String id;
        private String name;
        private int    hash;
        
        public BookID(String name)
        {
            setName(name);
        }
        
        public void setName(String name)
        {
            this.name = name;
            this.id = name.replaceAll("[\\W\\s]+", "");
            this.hash = name.hashCode();
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
            
            if(obj == null || obj instanceof BookID == false)
                return false;
            
            return obj.hashCode() == hashCode();
        }
    }
    
    private Map<BookID, BookMeta[]> books = new HashMap<BookID, BookMeta[]>();
    
    public RecipeBooks()
    {
    }
    
    public void setBook(String name, List<String> contents)
    {
        BookID id = new BookID(name);
        
        BookMeta[] volumes = books.get(id);
        
        if(volumes == null)
        {
            volumes = new BookMeta[3];
            
            volumes[0] = (BookMeta)Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
        }
        
        /*
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "Recipe book" + ChatColor.BLACK + " by RecipeManager");
        meta.setLore(lore);
        */
    }
    
    public ItemStack getBook(String name)
    {
        return getBook(name, 1);
    }
    
    public ItemStack getBook(String name, int volume)
    {
        BookMeta[] volumes = books.get(new BookID(name));
        
        if(volumes == null)
            return null;
        
        volume = Math.min(Math.max(volume, 1), volumes.length) - 1;
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        item.setItemMeta(volumes[volume]);
        return item;
    }
}
