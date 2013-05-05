package ro.thehunters.digi.recipeManager.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeBooks;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;

import com.google.common.collect.ImmutableList;

public class RecipeBook
{
    private String id;
    private String title;
    private String description;
    private List<Set<String>> volumes = new ArrayList<Set<String>>();
    private int recipesPerVolume = 50;
    private boolean cover = true;
    private boolean contents = true;
    private boolean end = true;
    
    /**
     * Blank recipe book.<br>
     * Use methods to add recipes to book, set title, etc.<br>
     * Then register/update it on {@link RecipeBooks} class.
     */
    public RecipeBook(String id)
    {
        this.id = id;
    }
    
    /*
    /**
     * Loads book data from file.<br>
     * You need to register/update it on {@link RecipeBooks} class.
     * 
     * @param file
     */
    /*
    public RecipeBook(File file)
    {
        RecipeManager.getRecipeBooks().load(file);
    }
    */
    
    /**
     * @return True if book is valid, false otherwise.
     */
    public boolean isValid()
    {
        return (id != null && title != null && !volumes.isEmpty());
    }
    
    /**
     * @return Book ID (usually file name without extension)
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * @return Book title - can be equal to ID if not defined in YML file.
     */
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    /**
     * @param description
     *            Book description for first page.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public int getRecipesPerVolume()
    {
        return recipesPerVolume;
    }
    
    /**
     * Sets how many recipes are added per volume.<br>
     * This only affects recipes that are added by {@link #addRecipe(BaseRecipe)} method or 'recipe' node in the yml file.
     * 
     * @param recipesPerVolume
     */
    public void setRecipesPerVolume(int recipesPerVolume)
    {
        this.recipesPerVolume = recipesPerVolume;
    }
    
    public boolean hasCoverPage()
    {
        return cover;
    }
    
    public void setCoverPage(boolean set)
    {
        cover = set;
    }
    
    public boolean hasContentsPage()
    {
        return contents;
    }
    
    public void setContentsPage(boolean set)
    {
        contents = set;
    }
    
    public boolean hasEndPage()
    {
        return end;
    }
    
    public void setEndPage(boolean set)
    {
        end = set;
    }
    
    /**
     * Add specified recipe to the book.
     * 
     * @param recipe
     *            a valid recipe.
     * @return true if added, false if it already exists in the book.
     * @throws IllegalArgumentException
     *             if recipe is invalid.
     */
    public boolean addRecipe(BaseRecipe recipe)
    {
        if(!recipe.isValid())
        {
            throw new IllegalArgumentException("Invalid recipe object - needs data!");
        }
        
        for(Set<String> recipes : volumes)
        {
            if(recipes.contains(recipe.getName()))
            {
                return false;
            }
        }
        
        addRecipe(recipe.getName());
        
        return true;
    }
    
    private void addRecipe(String name)
    {
        Set<String> recipes = null;
        
        if(!volumes.isEmpty())
        {
            recipes = volumes.get(volumes.size() - 1);
        }
        
        if(recipes == null || recipes.size() >= recipesPerVolume)
        {
            recipes = new LinkedHashSet<String>();
            volumes.add(recipes);
        }
        
        recipes.add(name);
    }
    
    public int addVolume(Collection<String> recipes)
    {
        volumes.add(new LinkedHashSet<String>(recipes));
        return volumes.size() - 1;
    }
    
    /**
     * @param volume
     *            volume to get
     * @return WrittenBook item.
     * @throws IllegalAccessError
     *             if book is not valid yet.
     */
    public ItemStack getBookItem(int volume)
    {
        if(!isValid())
        {
            throw new IllegalAccessError("Book is not yet valid!");
        }
        
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
        item.setItemMeta(getBookMeta(volume));
        
        return item;
    }
    
    /**
     * @return immutable list of volumes
     */
    public List<Set<String>> getVolumes()
    {
        return ImmutableList.copyOf(volumes);
    }
    
    public Set<String> getVolumeRecipes(int volume)
    {
        volume = Math.min(Math.max(volume, 1), getVolumesNum());
        
        return volumes.get(volume);
    }
    
    public int getVolumesNum()
    {
        return volumes.size();
    }
    
    public BookMeta getBookMeta(int volume)
    {
        if(!isValid())
        {
            throw new IllegalAccessError("Book is not yet valid!");
        }
        
        volume = Math.min(Math.max(volume, 1), getVolumesNum());
        int volumeID = volume - 1;
        BookMeta meta = (BookMeta)Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
        
        meta.setTitle(this.getTitle() + (getVolumesNum() > 1 ? " - " + Messages.RECIPEBOOK_VOLUME.get("{volume}", volume) : ""));
        meta.setAuthor(RecipeBooks.BOOK_MARKER + Tools.hideString(" " + this.getId() + " " + volume + " " + (System.currentTimeMillis() / 1000)));
        
        // Cover page
        if(cover)
        {
            StringBuilder cover = new StringBuilder(256);
            
            cover.append('\n').append(ChatColor.BLACK).append(ChatColor.BOLD).append(ChatColor.UNDERLINE).append(this.getTitle());
            
            if(getVolumesNum() > 1)
            {
                cover.append('\n').append(ChatColor.BLACK).append("  ").append(Messages.RECIPEBOOK_VOLUMEOFVOLUMES.get("{volume}", volume, "{volumes}", getVolumesNum()));
            }
            
            cover.append('\n').append(ChatColor.GRAY).append("       Published by\n        RecipeManager");
            
            if(this.getDescription() != null)
            {
                cover.append('\n').append(ChatColor.DARK_BLUE).append(this.getDescription());
            }
            
            meta.addPage(cover.toString());
        }
        
        // Build contents index and page content
        List<StringBuilder> index = null;
        
        List<String> pages = new ArrayList<String>();
        int i = 0;
        int r = 2;
        int p = (int)Math.ceil(volumes.get(volumeID).size() / 13.0) + 2;
        
        if(contents)
        {
            index = new ArrayList<StringBuilder>();
            index.add(new StringBuilder(256).append(Messages.RECIPEBOOK_HEADER_CONTENTS.get()).append("\n\n").append(ChatColor.BLACK));
        }
        
        for(String name : volumes.get(volumeID))
        {
            BaseRecipe recipe = RecipeManager.getRecipes().getRecipeByName(name);
            
            if(contents)
            {
                if(r > 13)
                {
                    r = 0;
                    i++;
                    index.add(new StringBuilder(256).append(ChatColor.BLACK));
                }
                
                String indexName = recipe.printBookIndex();
                index.get(i).append(p).append(". ").append(indexName).append(ChatColor.BLACK).append('\n');
                r += (indexName.length() >= 18 ? 2 : 1);
                p += 1;
            }
            
            String page = recipe.printBook();
            
            if(page.length() >= 255)
            {
                int x = page.indexOf('\n', 220);
                
                if(x < 0 || x > 255)
                {
                    x = 255;
                }
                
                pages.add(page.substring(0, x));
                pages.add(page.substring(x + 1));
                p++;
            }
            else
            {
                pages.add(page);
            }
        }
        
        if(contents)
        {
            for(StringBuilder s : index)
            {
                meta.addPage(s.toString());
            }
        }
        
        for(String s : pages)
        {
            meta.addPage(s);
        }
        
        if(end)
        {
            meta.addPage(String.format("\n\n\n\n\n\n        %s%s%s", ChatColor.BOLD, ChatColor.UNDERLINE, "THE END"));
        }
        
        return meta;
    }
}
