package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagHoldItem extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[]
        {
            "{flag} <item or false>",
        };
        
        D = new String[]
        {
            "Makes the recipe require crafter to hold an item.",
            "",
            "This flag can be used more than once to add more items, the player will need to hold one to craft.",
            "",
            "The <item> argument can be in this format: material:data:amount | enchantment:level | ...",
            "Just like recipe results, not all values from the item are required.",
            "",
            "Using 'false' will disable the flag.",
        };
        
        E = new String[]
        {
            "{flag} iron_pickaxe // any data/damage value",
            "{flag} iron_axe:0 // only undamaged axe!",
            "{flag} chainmail_helmet | protection_fire:1 // requires chain helmet with any level of damage and fire protection enchant level 1",
            "{flag} false // makes all previous statements useless",
        };
    }
    
    // Flag code
    
    private List<ItemStack> items = new ArrayList<ItemStack>();
    private String message;
    
    public FlagHoldItem()
    {
        type = FlagType.HOLDITEM;
    }
    
    public FlagHoldItem(FlagHoldItem flag)
    {
        this();
        
        for(ItemStack i : flag.items)
        {
            items.add(i.clone());
        }
        
        message = flag.message;
    }
    
    @Override
    public FlagHoldItem clone()
    {
        return new FlagHoldItem(this);
    }
    
    public List<ItemStack> getItems()
    {
        return items;
    }
    
    public void setItems(List<ItemStack> items)
    {
        this.items = items;
    }
    
    public void addItem(ItemStack item)
    {
        items.add(item);
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.split("\\|");
        
        if(split.length > 1)
        {
            setMessage(split[1].trim());
        }
        
        value = split[0].trim();
        
        ItemStack item = Tools.parseItemStack(value, Short.MAX_VALUE, true, true, true);
        
        if(item == null)
        {
            return false;
        }
        
        addItem(item);
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        StringBuilder s = new StringBuilder();
        boolean found = false;
        
        if(a.hasPlayer())
        {
            ItemStack held = a.player().getItemInHand();
            
            if(held != null)
            {
                for(int i = 0; i < items.size(); i++)
                {
                    if(Tools.itemSimilarDataWildcard(items.get(i), held))
                    {
                        found = true;
                        break;
                    }
                    
                    if(i > 0)
                    {
                        s.append(", ");
                    }
                    
                    s.append(Tools.printItem(items.get(i)));
                }
            }
        }
        
        if(!found)
        {
            a.addReason(Messages.FLAG_HOLDITEM, message, "{items}", s.toString());
        }
    }
}
