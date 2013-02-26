package ro.thehunters.digi.recipeManager.recipes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.flags.Flags;


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
    
    public CraftRecipe(BaseRecipe recipe)
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
     * This also calculates the width and height of the shape matrix.<br>
     * <b>NOTE: Array must have exacly 9 elements, use null for empty slots.</b>
     * 
     * @param ingredients
     *            ingredients matrix, this also defines the shape, width and height.
     */
    public void setIngredients(ItemStack[] ingredients)
    {
        if(ingredients.length != 9)
            throw new IllegalArgumentException("Recipe must have exacly 9 items, use null to specify empty slots!");
        
        this.ingredients = ingredients;
        calculate();
    }
    
    /**
     * Sets an ingredient slot to material with wildcard data value.<br>
     * Slots are like:<br>
     * <code>| 0 1 2 |<br>
     * | 3 4 5 |<br>
     * | 6 7 8 |</code> <br>
     * Null slots are ignored and allow the recipe to be used in a smaller grid (inventory's 2x2 for example)<br>
     * <br>
     * <b>NOTE: always start with index 0 !</b> Then you can use whatever index you want up to 8.<br>
     * This is required because ingredients are shifted to top-left corner of the 2D matrix on each call of this method.
     * 
     * @param slot
     *            start with 0, then use any index from 1 to 8
     * @param type
     */
    public void setIngredient(int slot, Material type)
    {
        setIngredient(slot, type, -1);
    }
    
    /**
     * Sets an ingredient slot to material with specific data value.<br>
     * Slots are like:<br>
     * <code>| 0 1 2 |<br>
     * | 3 4 5 |<br>
     * | 6 7 8 |</code> <br>
     * Null slots are ignored and allow the recipe to be used in a smaller grid (inventory's 2x2 for example)<br>
     * <br>
     * <b>NOTE: always start with index 0 !</b> Then you can use whatever index you want up to 8.<br>
     * This is required because ingredients are shifted to top-left corner of the 2D matrix on each call of this method.
     * 
     * @param slot
     *            start with 0, then use any index from 1 to 8
     * @param type
     * @param data
     */
    public void setIngredient(int slot, Material type, int data)
    {
        if(ingredients == null)
            ingredients = new ItemStack[9];
        
        if(slot != 0 && ingredients[0] == null)
        {
            Messages.info(ChatColor.RED + "A plugin is using setIngredient() with index NOT starting at 0, shape is corupted!!!");
            new Exception().printStackTrace();
            return;
        }
        
        ingredients[slot] = new ItemStack(type, 1, (short)data);
        calculate();
    }
    
    private void calculate()
    {
        StringBuilder str = new StringBuilder("craft");
        ItemStack item;
        
        // Trim the item matrix, shift ingredients to top-left corner
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
    
    @Override
    public RecipeType getRecipeType()
    {
        return RecipeType.CRAFT;
    }
}
