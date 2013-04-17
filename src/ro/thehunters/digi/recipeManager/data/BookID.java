package ro.thehunters.digi.recipeManager.data;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.flags.FlagRecipeBook;
import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;

public class BookID
{
    private String id;
    private String title;
    private String description;
    private int hash;
    
    public BookID(String name)
    {
        setTitle(name);
    }
    
    public BookID(BaseRecipe recipe, RecipeInfo info)
    {
        if(recipe.hasFlag(FlagType.RECIPEBOOK))
        {
            FlagRecipeBook flag = recipe.getFlag(FlagRecipeBook.class);
            
            title = flag.getTitle();
            description = Tools.parseColors(flag.getDescription(), false);
        }
        
        if(title == null)
        {
            title = info.getAdder().toString().toLowerCase();
            
            int start = Math.max(title.lastIndexOf('/'), 0);
            int end = Math.min(title.lastIndexOf('.'), title.length());
            title = title.substring(start, end);
            
            title = WordUtils.capitalize(title);
        }
        
        setTitle(title);
    }
    
    private void setTitle(String title)
    {
        this.title = Tools.parseColors(title, false);
        this.id = ChatColor.stripColor(this.title).toLowerCase().replaceAll("[\\W\\s]+", "");
        this.hash = id.hashCode();
    }
    
    public String getID()
    {
        return id;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public String getDescription()
    {
        return description;
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
        {
            return true;
        }
        
        if(obj == null || obj instanceof BookID == false)
        {
            return false;
        }
        
        return obj.hashCode() == hashCode();
    }
}
