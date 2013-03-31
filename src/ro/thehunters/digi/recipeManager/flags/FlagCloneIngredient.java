package ro.thehunters.digi.recipeManager.flags;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.CraftRecipe;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;
import ro.thehunters.digi.recipeManager.recipes.WorkbenchRecipe;

public class FlagCloneIngredient extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[1];
        A[0] = "{flag} < ??? >";
        
        D = new String[1];
        D[0] = "Flag not yet documented.";
        
        E = null;
    }
    
    // Flag code
    
    private byte copyBitsum;
    private int[] dataModifier;
    private int[] amountModifier;
    
    /**
     * Contains static constants that are usable in the 'copy' methods of {@link FlagCloneIngredient}.
     */
    public class Bit
    {
        public static final byte NONE = 1 << 0;
        public static final byte DATA = 1 << 1;
        public static final byte AMOUNT = 1 << 2;
        public static final byte ENCHANTS = 1 << 3;
        public static final byte NAME = 1 << 4;
        public static final byte LORE = 1 << 5;
        public static final byte SPECIAL = 1 << 6;
        public static final byte ALLMETA = ENCHANTS | NAME | LORE | SPECIAL;
        public static final byte ALL = DATA | AMOUNT | ALLMETA;
    }
    
    public FlagCloneIngredient()
    {
        type = FlagType.CLONEINGREDIENT;
    }
    
    public FlagCloneIngredient(FlagCloneIngredient flag)
    {
        this();
        
        copyBitsum = flag.copyBitsum;
        dataModifier = flag.dataModifier.clone();
        amountModifier = flag.amountModifier.clone();
    }
    
    @Override
    public FlagCloneIngredient clone()
    {
        return new FlagCloneIngredient(this);
    }
    
    /**
     * @return the bitsum of the copyable arguments.
     */
    public byte getCopyBitsum()
    {
        return copyBitsum;
    }
    
    /**
     * Checks if the copy bitsum has the requested bit.
     * 
     * @param bit
     *            use {@link Bit} enums
     * @return true if bit is present, false otherwise
     */
    public boolean hasCopyBit(byte bit)
    {
        return (copyBitsum & bit) == bit;
    }
    
    /**
     * Set what to copy from the ingredient to the result.<br>
     * You should use {@link Bit} class for values!
     * 
     * @param bitsum
     *            use {@link Bit} enums
     */
    public void setCopyBitsum(byte bitsum)
    {
        copyBitsum = bitsum;
    }
    
    /**
     * Pick something to copy from the ingredient to the result.<br>
     * You should use {@link Bit} class for values!
     * 
     * @param bit
     *            use {@link Bit} enums
     */
    private void addCopyBit(byte bit)
    {
        copyBitsum |= bit;
    }
    
    /**
     * Data value modifier for final result.
     * 
     * @return integer array of exacly 2 elements, first is the +/-/= char and second is the data value
     */
    public int[] getDataModifier()
    {
        return dataModifier;
    }
    
    /**
     * Modify the final result's data value by using symbol as math value to add/subtract/set data.
     * 
     * @param symbol
     *            can be '+', '-' or '='
     * @param data
     *            the data amount to change
     */
    public void setDataModifier(char symbol, int data)
    {
        dataModifier = new int[]
        {
            symbol,
            data
        };
    }
    
    /**
     * Amount modifier for final result.
     * 
     * @return integer array of exacly 2 elements, first is the +/-/= char and second is the amount
     */
    public int[] getAmountModifier()
    {
        return amountModifier;
    }
    
    /**
     * Modify the final result's amount by using symbol as math value to add/subtract/set amount.
     * 
     * @param symbol
     *            can be '+', '-' or '='
     * @param data
     *            the amount amount to change
     */
    public void setAmountModifier(char symbol, int data)
    {
        amountModifier = new int[]
        {
            symbol,
            data
        };
    }
    
    @Override
    protected boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getTypeId() == 0)
        {
            RecipeErrorReporter.error("Flag " + type + " can not be used on AIR results!", "The type of result defines the type of ingredient it searches for");
            return false;
        }
        
        BaseRecipe recipe = result.getRecipe();
        
        if(recipe instanceof WorkbenchRecipe == false)
        {
            RecipeErrorReporter.error("Flag " + type + " only works on workbench (craft and combine) recipes!");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.toLowerCase().split("\\|");
        String[] args;
        
        ItemResult result = getResult();
        
        if(result == null)
        {
            Messages.debug("SOMETHING IS WRONG WITH RESULT: " + result);
            return false;
        }
        
        BaseRecipe r = result.getRecipe();
        
        if(r instanceof CraftRecipe == false)
        {
            Messages.debug("SOMETHING IS WRONG WITH RECIPE: " + r);
            return false;
        }
        
        CraftRecipe recipe = (CraftRecipe)r;
        
        ItemStack[] ingredients = recipe.getIngredients();
        ItemStack ingredient = null;
        
        for(ItemStack i : ingredients)
        {
            if(i != null && i.getTypeId() == result.getTypeId())
            {
                if(ingredient == null)
                {
                    ingredient = i;
                    // continue the search for possible duplicates
                }
                else
                {
                    RecipeErrorReporter.error("Flag " + type + " has more ingredients of the cloned type: " + Tools.printItem(i), "Recipe must only have a single type of the cloned material in the ingredients!");
                    return false;
                }
            }
        }
        
        if(ingredient == null)
        {
            RecipeErrorReporter.error("Flag " + type + " has couldn't find ingredient of type: " + result.getType());
            return false;
        }
        
        for(String s : split)
        {
            s = s.trim();
            
            if(s.equals("all"))
            {
                setCopyBitsum(Bit.ALL);
                break;
            }
            else if(s.equals("allmeta"))
            {
                addCopyBit(Bit.ALLMETA);
                continue;
            }
            else if(s.equals("enchants"))
            {
                addCopyBit(Bit.ENCHANTS);
                continue;
            }
            else if(s.equals("name"))
            {
                addCopyBit(Bit.NAME);
                continue;
            }
            else if(s.equals("lore"))
            {
                addCopyBit(Bit.LORE);
                continue;
            }
            else if(s.equals("special"))
            {
                addCopyBit(Bit.SPECIAL);
                continue;
            }
            
            boolean isDataArg = s.startsWith("data");
            
            if(isDataArg || s.startsWith("amount"))
            {
                addCopyBit(isDataArg ? Bit.DATA : Bit.AMOUNT);
                
                Pattern pattern = Pattern.compile("[+-=]");
                Matcher match = pattern.matcher(s);
                
                if(match.find())
                {
                    args = pattern.split(s, 2);
                    value = args[1].trim();
                    
                    try
                    {
                        if(isDataArg)
                        {
                            setDataModifier(match.group(0).charAt(0), Math.abs(Integer.valueOf(value)));
                        }
                        else
                        {
                            setAmountModifier(match.group(0).charAt(0), Math.abs(Integer.valueOf(value)));
                        }
                    }
                    catch(Exception e)
                    {
                        RecipeErrorReporter.warning("Flag " + type + " has '" + (isDataArg ? "data" : "amount") + "' argument with invalid number: " + value);
                        continue;
                    }
                }
                
                continue;
            }
            
            RecipeErrorReporter.warning("Flag " + type + " has unknown argument: " + s);
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
    }
    
    @Override
    protected boolean onPrepare(Args a)
    {
        ItemStack result = getResult();
        boolean cloned = cloneIngredientToResult(result, a);
        
        Messages.debug("cloned " + cloned); // TODO remove
        a.addCustomEffect("[debug] cloned " + cloned);
        
        return cloned;
    }
    
    private boolean cloneIngredientToResult(ItemStack result, Args a)
    {
        if(result == null || a.inventory() instanceof CraftingInventory == false)
        {
            Messages.debug("no inventory or no result set!");
            return false;
        }
        
        ItemStack ingredient = null;
        
        CraftingInventory craftInv = (CraftingInventory)a.inventory();
        
        for(ItemStack i : craftInv.getMatrix())
        {
            if(i != null && result.getTypeId() == i.getTypeId())
            {
                ingredient = i;
                break;
            }
        }
        
        if(ingredient == null)
        {
            Messages.debug("Couldn't find target ingredient!");
            return false;
        }
        
        if(this.hasCopyBit(Bit.DATA))
        {
            short data = ingredient.getDurability();
            int[] dataMod = this.getDataModifier();
            
            if(dataMod != null)
            {
                switch(dataMod[0])
                {
                    case '-':
                        data -= dataMod[1];
                        break;
                    
                    // TODO remove setting data because it's redundant ?
                    case '=':
                        data = (short)dataMod[1];
                        break;
                    
                    default: // default adds to data
                        data += dataMod[1];
                        break;
                }
            }
            
            Messages.debug("data: " + (dataMod == null ? null : dataMod[0]) + " " + data);
            
            result.setDurability(data);
        }
        
        if(this.hasCopyBit(Bit.AMOUNT))
        {
            int amount = ingredient.getAmount();
            int[] amountMod = this.getDataModifier();
            
            if(amountMod != null)
            {
                switch(amountMod[0])
                {
                    case '+':
                        amount += amountMod[1];
                        break;
                    
                    case '-':
                        amount -= amountMod[1];
                        break;
                    
                    case '=':
                        amount = (short)amountMod[1];
                        break;
                }
            }
            
            result.setAmount(amount);
        }
        
        if(this.hasCopyBit(Bit.SPECIAL))
        {
            ItemMeta resultMeta = result.getItemMeta();
            
            // Clear ItemMeta copy's other data
            ItemMeta ingrMeta = ingredient.getItemMeta();
            ingrMeta.setDisplayName(resultMeta.getDisplayName());
            ingrMeta.setLore(resultMeta.getLore());
            
            for(Enchantment e : ingrMeta.getEnchants().keySet())
            {
                ingrMeta.removeEnchant(e);
            }
            
            result.setItemMeta(ingrMeta);
        }
        
        if(this.hasCopyBit(Bit.ENCHANTS))
        {
            result.addUnsafeEnchantments(ingredient.getEnchantments());
        }
        
        if(this.hasCopyBit(Bit.NAME))
        {
            ItemMeta meta = result.getItemMeta();
            meta.setDisplayName(ingredient.getItemMeta().getDisplayName());
            result.setItemMeta(meta);
        }
        
        if(this.hasCopyBit(Bit.LORE))
        {
            ItemMeta meta = result.getItemMeta();
            meta.setLore(ingredient.getItemMeta().getLore());
            result.setItemMeta(meta);
        }
        
        return true;
    }
}
