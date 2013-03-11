package ro.thehunters.digi.recipeManager.flags;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
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
    private byte  copyBitsum;
    private int[] dataModifier;
    private int[] amountModifier;
    
    /**
     * Contains static constants that are usable in the 'copy' methods of {@link FlagCloneIngredient}.
     */
    public class Bit
    {
        public static final byte NONE     = 1 << 0;
        public static final byte DATA     = 1 << 1;
        public static final byte AMOUNT   = 1 << 2;
        public static final byte ENCHANTS = 1 << 3;
        public static final byte NAME     = 1 << 4;
        public static final byte LORE     = 1 << 5;
        public static final byte SPECIAL  = 1 << 6;
        public static final byte ALLMETA  = ENCHANTS | NAME | LORE | SPECIAL;
        public static final byte ALL      = DATA | AMOUNT | ALLMETA;
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
        dataModifier = new int[] { symbol, data };
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
        amountModifier = new int[] { symbol, data };
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
                    RecipeErrorReporter.error("Flag " + type + " has more ingredients of the cloned type: " + Tools.printItemStack(i), "Recipe must only have a single type of the cloned material in the ingredients!");
                    return false;
                }
            }
        }
        
        if(ingredient == null)
        {
            RecipeErrorReporter.error("Flag " + type + " has couldn't find ingredient of type: " + result.getType());
            return false;
        }
        
        /*
        args = s.split(" ");
        
        if(args.length > 1)
        {
            value = args[1].trim();
            
            try
            {
                slot = Byte.valueOf(value);
            }
            catch(Exception e)
            {
                RecipeErrorReporter.error("Flag " + type + " has 'slot' argument with invalid number: " + value);
                return false;
            }
            
            if(slot < 0 || slot > maxSlot)
            {
                RecipeErrorReporter.error("Flag " + type + " has 'slot' argument with invalid range: " + slot + ", it must be between 1 and " + maxSlot + " !", "max slot size is determined by the recipe's ingredients");
                return false;
            }
            
        }
        else
        {
            RecipeErrorReporter.error("Flag " + type + " has 'slot' argument without a value!");
            return false;
        }
        
        if(slot == 0)
        {
            RecipeErrorReporter.error("Flag " + type + " needs the 'slot' argument !");
            return false;
        }
        */
        
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
                            setDataModifier(match.group(0).charAt(0), Math.abs(Integer.valueOf(value)));
                        else
                            setAmountModifier(match.group(0).charAt(0), Math.abs(Integer.valueOf(value)));
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
    protected void onCheck(Arguments a)
    {
    }
    
    public static ItemStack getClonedItem(Flag flag, CraftingInventory inventory)
    {
        if(flag instanceof FlagCloneIngredient == false)
            return null;
        
        FlagCloneIngredient cloneFlag = (FlagCloneIngredient)flag;
        ItemResult result = cloneFlag.getResult();
        ItemStack ingredient = null;
        
        Validate.notNull(result, "Flag has NULL result pointer!");
        
        for(ItemStack i : inventory.getContents())
        {
            if(i != null && result.getTypeId() == i.getTypeId())
            {
                ingredient = i;
                break;
            }
        }
        
        result = new ItemResult(result);
        
        if(ingredient == null)
        {
            Messages.debug("Couldn't find target ingredient!");
            return null;
        }
        
        if(cloneFlag.hasCopyBit(Bit.DATA))
        {
            short data = ingredient.getDurability();
            int[] dataMod = cloneFlag.getDataModifier();
            
            if(dataMod != null)
            {
                switch(dataMod[0])
                {
                    case '+':
                        data += dataMod[1];
                        break;
                    
                    case '-':
                        data -= dataMod[1];
                        break;
                    
                    case '=':
                        data = (short)dataMod[1];
                        break;
                }
            }
            
            result.setDurability(data);
        }
        
        if(cloneFlag.hasCopyBit(Bit.AMOUNT))
        {
            int amount = ingredient.getAmount();
            int[] amountMod = cloneFlag.getDataModifier();
            
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
        
        if(cloneFlag.hasCopyBit(Bit.SPECIAL))
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
        
        if(cloneFlag.hasCopyBit(Bit.ENCHANTS))
        {
            result.addUnsafeEnchantments(ingredient.getEnchantments());
        }
        
        if(cloneFlag.hasCopyBit(Bit.NAME))
        {
            ItemMeta meta = result.getItemMeta();
            meta.setDisplayName(ingredient.getItemMeta().getDisplayName());
            result.setItemMeta(meta);
        }
        
        if(cloneFlag.hasCopyBit(Bit.LORE))
        {
            ItemMeta meta = result.getItemMeta();
            meta.setLore(ingredient.getItemMeta().getLore());
            result.setItemMeta(meta);
        }
        
        return null;
    }
}
