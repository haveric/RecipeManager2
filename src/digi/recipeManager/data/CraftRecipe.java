package digi.recipeManager.data;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import digi.recipeManager.Tools;

public class CraftRecipe extends WorkbenchRecipe
{
    private ItemStack[]  ingredients;
    private int          width;
    private int          height;
    
    private ShapedRecipe bukkitRecipe;
    
    public CraftRecipe()
    {
    }
    
    public CraftRecipe(ShapedRecipe recipe)
    {
        bukkitRecipe = recipe;
        setIngredients(Tools.convertShapedRecipeToItemMatrix(recipe));
        setResult(recipe.getResult());
    }
    
    public CraftRecipe(RmRecipe recipe)
    {
        super(recipe);
    }
    
    public CraftRecipe(Flags flags)
    {
        super(flags);
    }
    
    public ItemStack[] getIngredients()
    {
        return ingredients;
    }
    
    /**
     * Set the ingredients matrix. <br>
     * This also calculates the width and height of the shape matrix.
     * 
     * @param ingredients
     *            ingredients matrix, this also defines the shape, width and height.
     */
    public void setIngredients(ItemStack[] ingredients)
    {
        if(ingredients.length != 9)
            throw new IllegalArgumentException("Recipe must have exacly 9 items, use null to specify empty slots!");
        
        this.ingredients = ingredients;
        StringBuilder str = new StringBuilder("craft");
        ItemStack item;
        
        Tools.trimItemMatrix(ingredients);
        
        // Calculate width and height of the shape and build the ingredient string for hashing
        for(int h = 0; h < 3; h++)
        {
            for(int w = 0; w < 3; w++)
            {
                item = ingredients[(h * 3) + w];
                
                if(item != null)
                {
                    width = Math.max(width, w);
                    height = Math.max(height, h);
                    
                    str.append(item.getTypeId()).append(':').append(item.getDurability());
                }
                
                str.append(';');
            }
        }
        
        width++;
        height++;
        hash = str.toString().hashCode();
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public ShapedRecipe getBukkitRecipe()
    {
        return bukkitRecipe == null ? toShapedRecipe() : bukkitRecipe;
    }
    
    public ShapedRecipe toShapedRecipe()
    {
        return toShapedRecipe(0);
    }
    
    public ShapedRecipe toShapedRecipe(int markResultId)
    {
        ShapedRecipe bukkitRecipe = new ShapedRecipe(markResultId >= 0 ? Tools.generateRecipeIdResult(getFirstResult(), markResultId) : getFirstResult());
        
        switch(height)
        {
            case 1:
            {
                switch(width)
                {
                    case 1:
                        bukkitRecipe.shape("a");
                        break;
                    
                    case 2:
                        bukkitRecipe.shape("ab");
                        break;
                    
                    case 3:
                        bukkitRecipe.shape("abc");
                }
                
                break;
            }
            
            case 2:
            {
                switch(width)
                {
                    case 1:
                        bukkitRecipe.shape("a", "b");
                        break;
                    
                    case 2:
                        bukkitRecipe.shape("ab", "cd");
                        break;
                    
                    case 3:
                        bukkitRecipe.shape("abc", "def");
                }
                break;
            }
            
            case 3:
            {
                switch(width)
                {
                    case 1:
                        bukkitRecipe.shape("a", "b", "c");
                        break;
                    
                    case 2:
                        bukkitRecipe.shape("ab", "cd", "ef");
                        break;
                    
                    case 3:
                        bukkitRecipe.shape("abc", "def", "ghi");
                }
            }
        }
        
        ItemStack item;
        char key = 'a';
        
        for(int h = 0; h < height; h++)
        {
            for(int w = 0; w < width; w++)
            {
                item = ingredients[(h * 3) + w];
                
                if(item != null)
                    bukkitRecipe.setIngredient(key, item.getType(), item.getDurability());
                
                key++;
            }
        }
        
        return bukkitRecipe;
    }
    
    public boolean hasIngredients()
    {
        return ingredients != null && ingredients.length == 9;
    }
    
    @Override
    public boolean isValid()
    {
        return hasIngredients() && hasResults();
    }
}
