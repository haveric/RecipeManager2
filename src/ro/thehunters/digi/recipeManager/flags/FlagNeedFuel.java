package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;

public class FlagNeedFuel extends Flag
{
    private List<ItemStack> fuels = new ArrayList<ItemStack>();
    private String          message;
    
    public FlagNeedFuel()
    {
        type = FlagType.NEEDFUEL;
    }
    
    public FlagNeedFuel(FlagNeedFuel flag)
    {
        this();
        
        for(ItemStack i : flag.fuels)
        {
            fuels.add(i.clone());
        }
    }
    
    @Override
    public FlagNeedFuel clone()
    {
        return new FlagNeedFuel(this);
    }
    
    public List<ItemStack> getFuels()
    {
        return fuels;
    }
    
    public void setFuels(List<ItemStack> fuels)
    {
        this.fuels = fuels;
    }
    
    public void addFuel(ItemStack item)
    {
        fuels.add(item);
    }
    
    @Override
    public boolean onValidate()
    {
        BaseRecipe recipe = getRecipe();
        
        if(recipe instanceof SmeltRecipe == false)
        {
            RecipeErrorReporter.error("Flag " + type + " only works on SMELT recipes!");
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean onParse(String value)
    {
        // TODO parse single list
        ItemStack item = Tools.convertStringToItemStack(value, -1, true, false, false);
        
        if(item == null)
            return false;
        
        addFuel(item);
        
        return true;
    }
    
    @Override
    public void onCheck(Arguments a)
    {
        if(a.hasLocation())
        {
            Block block = a.getLocation().getBlock();
            
            switch(block.getType())
            {
                case FURNACE:
                case BURNING_FURNACE:
                {
                    BlockState blockState = block.getState();
                    
                    if(blockState instanceof Furnace)
                    {
                        Furnace furnace = (Furnace)blockState;
                        
                        ItemStack fuel = furnace.getInventory().getFuel();
                        
                        for(ItemStack i : fuels)
                        {
                            if(fuel.isSimilar(i))
                            {
                                // found the required fuel, no need to add the message.
                                return;
                            }
                        }
                    }
                }
            }
        }
        
        a.addReason(Messages.CRAFT_FLAG_NEEDFUEL, message, "{fuels}", Tools.convertListToString(fuels));
    }
}
