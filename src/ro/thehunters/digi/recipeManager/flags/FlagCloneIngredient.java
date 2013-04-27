package ro.thehunters.digi.recipeManager.flags;

import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagCloneIngredient extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.CLONEINGREDIENT;
        
        A = new String[]
        {
            "{flag} <arguments>",
        };
        
        D = new String[]
        {
            "Clones the ingredient matching material of the result used on.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "As '<arguments>' you must define at least one feature to copy from the ingredient to the result.",
            "Arguments can be one or more of the following, separated by | character:",
            "  data [<mod> <value>]   = copy data value with optional modifier, <mod> can be +,-,/,* or % as math operator and <value> a number.",
            "  amount [<mod> <value>] = copy stack amount with optional modifier, <mod> can be +,-,/,* or % as math operator and <value> a number.",
            "  enchants               = copies the enchantments.",
            "  name                   = copies the custom item name.",
            "  lore                   = copies the custom item lore/description.",
            "  special                = copies item's special feature like leather armor color, firework effects, book contents, skull owner, etc.",
            "  allmeta                = copies enchants, name, lore and special.",
            "  all                    = copies entire item (data, amount, enchants, name, lore, special)",
            "",
            "NOTE: If the result's material is present in the ingredients more than once, when using the recipe it will clone the details of first item in the grid.",
            "",
            "To apply conditions for ingredients (ranged data values, specific names, etc) then you can use the " + FlagType.INGREDIENTCONDITION + " flag too.",
        };
        
        E = new String[]
        {
            "{flag} data // just copy data value",
            "{flag} data +2 // copy data value and add 2 to it",
            "{flag} amount * 2 // copy amount and multiply it by 2",
            "{flag} data % 2 // get the remainer from data divided by 2.",
            "{flag} data | amount | lore // only copy these things",
            "{flag} all // copy entire ingredient",
        };
    }
    
    // Flag code
    
    private byte copyBitsum;
    private int[] dataModifier = new int[2];
    private int[] amountModifier = new int[2];
    
    /**
     * Contains static constants that are usable in the 'copy' methods of {@link FlagCloneIngredient}.
     */
    public class Bit
    {
        public static final byte NONE = 0;
        public static final byte DATA = 1 << 0;
        public static final byte AMOUNT = 1 << 1;
        public static final byte ENCHANTS = 1 << 2;
        public static final byte NAME = 1 << 3;
        public static final byte LORE = 1 << 4;
        public static final byte SPECIAL = 1 << 5;
        public static final byte ALLMETA = ENCHANTS | NAME | LORE | SPECIAL;
        public static final byte ALL = DATA | AMOUNT | ALLMETA;
    }
    
    public FlagCloneIngredient()
    {
    }
    
    public FlagCloneIngredient(FlagCloneIngredient flag)
    {
        copyBitsum = flag.copyBitsum;
        System.arraycopy(flag.dataModifier, 0, dataModifier, 0, dataModifier.length);
        System.arraycopy(flag.amountModifier, 0, amountModifier, 0, amountModifier.length);
    }
    
    @Override
    public FlagCloneIngredient clone()
    {
        return new FlagCloneIngredient(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
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
     * Modify the final result's data value by using symbol as math operator.
     * 
     * @param symbol
     *            can be: +, -, *, /, %
     * @param data
     *            the data amount to change
     */
    public void setDataModifier(char symbol, int data)
    {
        dataModifier[0] = symbol;
        dataModifier[1] = data;
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
     * Modify the final result's amount by using symbol as math operator.
     * 
     * @param symbol
     *            can be: +, -, *, /, %
     * @param data
     *            the amount amount to change
     */
    public void setAmountModifier(char symbol, int data)
    {
        amountModifier[0] = symbol;
        amountModifier[1] = data;
    }
    
    @Override
    protected boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getTypeId() == 0)
        {
            RecipeErrorReporter.error("Flag " + getType() + " can not be used on AIR results!", "The type of result defines the type of ingredient it searches for");
            return false;
        }
        
        /*
        BaseRecipe recipe = result.getRecipe();
        
        if(recipe instanceof WorkbenchRecipe == false)
        {
            RecipeErrorReporter.error("Flag " + getType() + " only works on workbench (craft and combine) recipes!");
            return false;
        }
        */
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] args = value.toLowerCase().split("\\|");
        
        int found = Tools.findItemInIngredients(getRecipe(), getResult().getType(), null);
        
        if(found == 0)
        {
            RecipeErrorReporter.error("Flag " + getType() + " has couldn't find ingredient: " + Tools.Item.print(getResult()));
            return false;
        }
        else if(found > 1)
        {
            RecipeErrorReporter.warning("Flag " + getType() + " has found the " + Tools.Item.print(getResult()) + " ingredient more than once, only data from the first one will be cloned!");
        }
        
        for(String arg : args)
        {
            arg = arg.trim();
            
            if(arg.equals("all"))
            {
                setCopyBitsum(Bit.ALL);
                break;
            }
            else if(arg.equals("allmeta"))
            {
                addCopyBit(Bit.ALLMETA);
                continue;
            }
            else if(arg.equals("enchants"))
            {
                addCopyBit(Bit.ENCHANTS);
                continue;
            }
            else if(arg.equals("name"))
            {
                addCopyBit(Bit.NAME);
                continue;
            }
            else if(arg.equals("lore"))
            {
                addCopyBit(Bit.LORE);
                continue;
            }
            else if(arg.equals("special"))
            {
                addCopyBit(Bit.SPECIAL);
                continue;
            }
            
            boolean isDataArg = arg.startsWith("data");
            
            if(isDataArg || arg.startsWith("amount"))
            {
                addCopyBit(isDataArg ? Bit.DATA : Bit.AMOUNT);
                
                Pattern pattern = Pattern.compile("[+-*/%]");
                Matcher match = pattern.matcher(arg);
                
                if(match.find())
                {
                    args = pattern.split(arg, 2);
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
                    catch(Throwable e)
                    {
                        RecipeErrorReporter.warning("Flag " + getType() + " has '" + (isDataArg ? "data" : "amount") + "' argument with invalid number: " + value);
                        continue;
                    }
                }
                
                continue;
            }
            
            RecipeErrorReporter.warning("Flag " + getType() + " has unknown argument: " + arg);
        }
        
        return true;
    }
    
    @Override
    protected void onPrepare(Args a)
    {
        if(!a.hasResult() || !a.hasInventory()) // || a.inventory() instanceof CraftingInventory == false)
        {
            Messages.debug("Needs inventory and result!");
            return;
        }
        
        boolean cloned = cloneIngredientToResult(a.result(), a);
        
        if(!cloned)
        {
            a.addCustomReason("Failed to clone ingredient."); // TODO remove ?
        }
    }
    
    private boolean cloneIngredientToResult(ItemStack result, Args a)
    {
        ItemStack ingredient = null;
        
        if(a.inventory() instanceof CraftingInventory)
        {
            CraftingInventory inv = (CraftingInventory)a.inventory();
            
            for(ItemStack i : inv.getMatrix())
            {
                if(i != null && result.getTypeId() == i.getTypeId())
                {
                    ingredient = i;
                    break;
                }
            }
        }
        else if(a.inventory() instanceof FurnaceInventory)
        {
            FurnaceInventory inv = (FurnaceInventory)a.inventory();
            ItemStack i = inv.getSmelting();
            
            if(i != null && result.getTypeId() == i.getTypeId())
            {
                ingredient = i;
            }
        }
        else
        {
            a.addCustomReason("Unknown inventory type: " + a.inventory());
            return false;
        }
        
        if(ingredient == null)
        {
            a.addCustomReason("Couldn't find target ingredient!");
            return false;
        }
        
        if(this.hasCopyBit(Bit.DATA))
        {
            int data = ingredient.getDurability();
            int[] mod = this.getDataModifier();
            
            switch(mod[0])
            {
                case '-':
                    data -= mod[1];
                    break;
                
                case '*':
                    data *= mod[1];
                    break;
                
                case '/':
                    data /= mod[1];
                    break;
                
                case '%':
                    data %= mod[1];
                    break;
                
                default:
                    data += mod[1];
                    break;
            }
            
            result.setDurability((short)data);
        }
        
        if(this.hasCopyBit(Bit.AMOUNT))
        {
            int amount = ingredient.getAmount();
            int[] mod = this.getAmountModifier();
            
            switch(mod[0])
            {
                case '-':
                    amount -= mod[1];
                    break;
                
                case '*':
                    amount *= mod[1];
                    break;
                
                case '/':
                    amount /= mod[1];
                    break;
                
                case '%':
                    amount %= mod[1];
                    break;
                
                default:
                    amount += mod[1];
                    break;
            }
            
            result.setAmount(amount);
        }
        
        ItemMeta ingredientMeta = ingredient.getItemMeta();
        ItemMeta resultMeta = result.getItemMeta();
        
        if(this.hasCopyBit(Bit.ENCHANTS))
        {
            for(Entry<Enchantment, Integer> e : ingredientMeta.getEnchants().entrySet())
            {
                resultMeta.addEnchant(e.getKey(), e.getValue(), true);
            }
        }
        
        if(this.hasCopyBit(Bit.NAME))
        {
            resultMeta.setDisplayName(ingredientMeta.getDisplayName());
        }
        
        if(this.hasCopyBit(Bit.LORE))
        {
            resultMeta.setLore(ingredientMeta.getLore());
        }
        
        if(this.hasCopyBit(Bit.SPECIAL))
        {
            ingredientMeta.setDisplayName(resultMeta.getDisplayName());
            ingredientMeta.setLore(resultMeta.getLore());
            
            if(ingredientMeta.hasEnchants())
            {
                for(Enchantment e : ingredientMeta.getEnchants().keySet())
                {
                    ingredientMeta.removeEnchant(e);
                }
            }
            
            for(Entry<Enchantment, Integer> e : resultMeta.getEnchants().entrySet())
            {
                ingredientMeta.addEnchant(e.getKey(), e.getValue(), true);
            }
            
            resultMeta = ingredientMeta;
        }
        
        result.setItemMeta(resultMeta);
        
        return true;
    }
}
