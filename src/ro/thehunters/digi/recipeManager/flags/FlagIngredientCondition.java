package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberRange;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.Vanilla;

public class FlagIngredientCondition extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[]
        {
            "{flag}",
        };
        
        D = new String[]
        {
            "Flag not yet documented.",
        };
        
        E = null;
    }
    
    // Flag code
    
    public class Conditions
    {
        private NumberRange data;
        private int amount;
//        private NumberRange                   enchantsNum;
//        private Map<Enchantment, NumberRange> enchants;
        
        private String name;
        private String lore;
        private String message;
        
        public Conditions()
        {
        }
        
        public void setData(int data)
        {
            setData(data, data);
        }
        
        public void setData(int min, int max)
        {
            this.data = new NumberRange(min, max);
        }
        
        public int getAmount()
        {
            return amount;
        }
        
        public void setAmount(int amount)
        {
            this.amount = amount;
        }
        
        public boolean hasAmount(int amount)
        {
            return (amount >= this.amount);
        }
    }
    
    Map<String, Conditions> conditions = new HashMap<String, Conditions>();
    
    public FlagIngredientCondition()
    {
        type = FlagType.INGREDIENTCONDITION;
    }
    
    public FlagIngredientCondition(FlagIngredientCondition flag)
    {
        this();
        
        // TODO clone conditions
    }
    
    @Override
    public FlagIngredientCondition clone()
    {
        return new FlagIngredientCondition(this);
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] args = value.toLowerCase().split("\\|");
        
        if(args.length <= 1)
        {
            return RecipeErrorReporter.error("Flag " + getType() + " needs an item and some arguments for conditions !", "Read '" + Files.FILE_INFO_FLAGS + "' for more info.");
        }
        
        ItemStack item = Tools.parseItemStack(args[0], Vanilla.DATA_WILDCARD, true, false, false);
        
        if(item == null)
        {
            return false;
        }
        
        Conditions cond = getConditions(item);
        
        if(cond == null)
        {
            cond = new Conditions();
            setConditions(item, cond);
        }
        
        for(int i = 1; i < args.length; i++)
        {
            String arg = args[i].trim();
            
            if(arg.startsWith("amount"))
            {
                String[] data = arg.split(" ", 2);
                
                if(data.length > 2)
                {
                    RecipeErrorReporter.warning("Flag " + getType() + " has 'amount' argument with no value.");
                    continue;
                }
                
                try
                {
                    cond.setAmount(Integer.valueOf(data[1]));
                }
                catch(NumberFormatException e)
                {
                    RecipeErrorReporter.warning("Flag " + getType() + " has 'amount' argument with invalid number: " + value);
                    continue;
                }
            }
            else if(arg.startsWith("failm"))
            {
            }
            else
            {
                RecipeErrorReporter.warning("Flag " + getType() + " has unknown argument: " + args[i]);
            }
        }
        
        return true;
    }
    
    public void setConditions(ItemStack item, Conditions conditions)
    {
        Validate.notNull(item, "item argument must not be null!");
        Validate.notNull(conditions, "conditions argument must not be null!");
        
        this.conditions.put(Tools.convertItemToStringId(item), conditions);
    }
    
    public Conditions getConditions(ItemStack item)
    {
        if(item == null)
        {
            return null;
        }
        
        Conditions cond = conditions.get(item.getTypeId() + ":" + item.getDurability());
        
        if(cond == null)
        {
            cond = conditions.get(String.valueOf(item.getTypeId()));
        }
        
        return cond;
    }
    
    public String checkConditions(ItemStack item)
    {
        if(item == null)
        {
            return null;
        }
        
        Conditions cond = getConditions(item);
        
        if(cond == null)
        {
            return null;
        }
        
        if(!cond.hasAmount(item.getAmount()))
        {
            return Messages.FLAG_INGREDIENTCONDITIONS_NOAMOUNT.get("{amount}", cond.getAmount());
        }
        
        return null;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        Inventory inv = a.inventory();
        
        if(inv instanceof CraftingInventory)
        {
            for(int i = 1; i < 10; i++)
            {
                ItemStack item = inv.getItem(i);
                
                if(item != null)
                {
                    String reason = checkConditions(item);
                    
                    Messages.debug(Tools.printItem(item) + " reason = " + reason);
                    
                    if(reason != null)
                    {
                        a.addCustomReason(reason);
                    }
                }
            }
        }
        else if(inv instanceof FurnaceInventory)
        {
            
        }
        
//      a.addReason(globalMessage, customMessage, variables) // < TODO
    }
}