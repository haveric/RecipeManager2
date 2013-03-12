package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagLeatherColor extends Flag
{
    private Color color;
    
    public FlagLeatherColor()
    {
        type = FlagType.LEATHERCOLOR;
    }
    
    public FlagLeatherColor(FlagLeatherColor flag)
    {
        this();
        
        color = Color.fromRGB(flag.color.asRGB());
    }
    
    @Override
    public FlagLeatherColor clone()
    {
        return new FlagLeatherColor(this);
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public void setColor(Color color)
    {
        this.color = color;
    }
    
    public void setColor(int red, int green, int blue)
    {
        setColor(Color.fromRGB(red, green, blue));
    }
    
    @Override
    protected boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof LeatherArmorMeta == false)
        {
            RecipeErrorReporter.error("Flag " + type + " needs a leather armor item!");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        color = Tools.parseColor(value);
        
        if(color == null)
        {
            RecipeErrorReporter.error("Flag @" + type + " has invalid color numbers!", "Use 3 numbers ranging from 0 to 255, e.g. 255 128 0 for orange.");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        ItemStack result = a.result();
        
        if(result == null || result.getItemMeta() instanceof LeatherArmorMeta == false)
            return false;
        
        LeatherArmorMeta meta = (LeatherArmorMeta)result.getItemMeta();
        meta.setColor(color);
        result.setItemMeta(meta);
        
        return true;
    }
}