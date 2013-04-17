package ro.thehunters.digi.recipeManager.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class Book
{
    private String title;
    private String description;
    private BookMeta[] volumes;
    
    public Book(String title)
    {
        this.title = title;
    }
    
    public Book(String title, String description, BookMeta[] volumes)
    {
        this.title = title;
        this.description = description;
        setVolumes(volumes);
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public BookMeta[] getVolumes()
    {
        return volumes;
    }
    
    public void setVolumes(BookMeta[] volumes)
    {
        this.volumes = volumes;
    }
    
    public ItemStack getBookItem()
    {
        return getBookItem(1);
    }
    
    public ItemStack getBookItem(int volume)
    {
        volume = Math.min(Math.max(volume, 1), volumes.length) - 1;
        
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        
        item.setItemMeta(volumes[volume]);
        
        return item;
    }
}
