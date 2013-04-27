package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.block.Block;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagSetBlock extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.SETBLOCK;
        
        A = new String[]
        {
            "{flag} <block material>:[data] | [arguments]",
        };
        
        D = new String[]
        {
            "Changes the workbench/furnace/block-at-player into other block type.",
            "Using this flag more than once will overwrite the previous flag.",
            "",
            "Replace '<block material>' with a block material (not item!), see '" + Files.FILE_INFO_NAMES + "' for list, IDs up to 255 are blocks, after that they're items.",
            "Optionally you can define a data value which defines its skin, direction and other stuff, see the 'Minecraft Wiki / Data Values' link from the '" + Files.FILE_INFO_NAMES + "' file.",
            "You can also use aliases for materials and data values too.",
            "",
            "Additionally you can define a set of arguments separated by | character:",
            "  drop            = breaks the existing block and drops its item.",
            "  noinv [failmsg] = prevent inventory crafting, if this is not set, the flag will set the block at player location too; optionally you can overwrite the failure message for this condition.",
        };
        
        E = new String[]
        {
            "{flag} gold_block",
            "{flag} planks:jungle | noinv // set to jungle wood planks and prevent inventory crafting",
            "{flag} air | drop | noinv <red>Only workbench! // simulate block break",
        };
    }
    
    // Flag code
    
    private int id;
    private byte data;
    private boolean drop;
    private boolean noInv;
    private String failMessage;
    
    public FlagSetBlock()
    {
    }
    
    public FlagSetBlock(FlagSetBlock flag)
    {
        id = flag.id;
        data = flag.data;
        drop = flag.drop;
        noInv = flag.noInv;
        failMessage = flag.failMessage;
    }
    
    @Override
    public FlagSetBlock clone()
    {
        return new FlagSetBlock(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] args = value.split("\\|");
        
        value = args[0].trim();
        
        ItemStack item = Tools.parseItemStack(value, 0, true, false, false);
        
        if(item == null || !item.getType().isBlock())
        {
            RecipeErrorReporter.error("Flag " + getType() + " has invalid block material type: " + value, "Note that block materials have IDs from 0 to 255.");
            return false;
        }
        
        id = item.getTypeId();
        data = (byte)item.getDurability();
        
        if(args.length > 1)
        {
            for(int i = 1; i < args.length; i++)
            {
                value = args[i].toLowerCase().trim();
                
                if(value.equals("drop"))
                {
                    drop = true;
                }
                else if(value.startsWith("noinv"))
                {
                    noInv = true;
                    
                    value = args[i].trim().substring("noinv".length()).trim();
                    
                    if(!value.isEmpty())
                    {
                        failMessage = value;
                    }
                }
            }
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        if(!a.hasLocation() || !a.hasInventory())
        {
            a.addCustomReason("Needs location and inventory!");
            return;
        }
        
        if(noInv && a.inventory() instanceof CraftingInventory)
        {
            CraftingInventory inv = (CraftingInventory)a.inventory();
            
            if(inv.getSize() < 9)
            {
                a.addReason(Messages.FLAG_SETBLOCK_NEEDSWORKBENCH, failMessage);
            }
        }
    }
    
    @Override
    protected void onCrafted(Args a)
    {
        if(!a.hasLocation())
        {
            a.addCustomReason("Needs location!");
            return;
        }
        
        Block block = a.location().getBlock();
        
        if(drop)
        {
            block.breakNaturally();
        }
        
        block.setTypeIdAndData(id, data, true);
    }
}
