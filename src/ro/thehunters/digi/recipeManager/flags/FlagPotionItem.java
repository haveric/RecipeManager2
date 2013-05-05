package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.ErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagPotionItem extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.POTIONITEM;
        
        A = new String[]
        {
            "{flag} <basic effect>",
            "{flag} custom <custom effect>",
            "{flag} false",
        };
        
        D = new String[]
        {
            "Builds a potion item, only works with POTION item.",
            "",
            "There are 2 types of potions... basic potions which have 1 effect and custom potions which can have multiple effects.",
            "",
            "Building a basic potion:",
            "",
            "Instead of <basic effect> argument you must enter a series of arguments separated by | character, in any order.",
            "Arguments for basic effect:",
            "  type <potion type>     = (REQUIRED) Type of potion, read '" + Files.FILE_INFO_NAMES + "' at 'POTION TYPES' section (not POTION EFFECT TYPE !)",
            "  level <number or max>  = (optional) Potion's level/tier, usually 1(default) or 2, you can enter 'max' to set it at highest supported level",
            "  extended               = (optional) Potion has extended duration",
            "  splash                 = (optional) Throwable/breakable potion instead of drinkable",
            "",
            "",
            "Building a custom potion requires adding individual effects.",
            "",
            "However, basic potion still affect the custom potion:",
            "- If no basic potion is defined the bottle will look like 'water bottle' with no effects listed, effects still apply when drank",
            "- Basic potion's type affects bottle liquid color",
            "- Basic potion's splash still affects if the bottle is throwable instead of drinkable",
            "- Basic potion's extended and level do absolutely nothing.",
            "- The first custom effect added is the potion's name, rest of effects are in description (of course you can use @name to change the item name)",
            "",
            "Once you understand that, you may use @potion custom as many times to add as many effects you want.",
            "Similar syntax to basic effect, arguments separated by | character, can be in any order.",
            "",
            "Arguments for custom effect:",
            "  type <effect type>  = (REQUIRED) Type of potion effect, read '" + Files.FILE_INFO_NAMES + "' at 'POTION EFFECT TYPE' section (not POTION TYPE !)",
            "  duration <float>    = (optional) Duration of the potion effect in seconds, default 1 (does not work on HEAL and HARM)",
            "  amplify <number>    = (optional) Amplify the effects of the potion, default 0 (e.g. 2 = <PotionName> III, numbers after potion's max level will display potion.potency.number instead)",
            "  ambient             = (optional) Adds extra visual particles",
            "",
            "Setting to 'false' will remove all potion effects - reverting it to plain water bottle.",
        };
        
        E = new String[]
        {
            "{flag} level max | type FIRE_RESISTANCE | extended // basic extended fire resistance potion",
            "// advanced potion example:",
            "{flag} type POISON | splash // set the bottle design and set it as splash",
            "{flag} custom type WITHER | duration 10 // add wither effect",
            "{flag} custom duration 2.5 | type BLINDNESS | amplify 5 // add blindness effect",
        };
    }
    
    // Flag code
    
    private short data;
    private List<PotionEffect> effects = new ArrayList<PotionEffect>();
    
    public FlagPotionItem()
    {
    }
    
    public FlagPotionItem(FlagPotionItem flag)
    {
        data = flag.data;
        effects.addAll(flag.effects);
    }
    
    @Override
    public FlagPotionItem clone()
    {
        return new FlagPotionItem(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public short getData()
    {
        return data;
    }
    
    public void setData(short data)
    {
        this.data = data;
    }
    
    public List<PotionEffect> getEffects()
    {
        return effects;
    }
    
    public void setEffects(List<PotionEffect> effects)
    {
        if(effects == null)
        {
            this.effects.clear();
        }
        else
        {
            this.effects = effects;
        }
    }
    
    public void addEffect(PotionEffect effect)
    {
        this.effects.add(effect);
    }
    
    @Override
    protected boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof PotionMeta == false)
        {
            ErrorReporter.error("Flag " + getType() + " needs a POTION item!");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        if(value.startsWith("custom"))
        {
            value = value.substring("custom".length()).trim();
            PotionEffect effect = Tools.parsePotionEffect(value, getType());
            
            if(effect != null)
            {
                addEffect(effect);
            }
        }
        else
        {
            Potion p = Tools.parsePotion(value, getType());
            
            if(p != null)
            {
                setData(p.toDamageValue());
            }
        }
        
        return true;
    }
    
    @Override
    protected void onPrepare(Args a)
    {
        if(!a.hasResult())
        {
            a.addCustomReason("Needs result!");
            return;
        }
        
        if(data != 0)
        {
            a.result().setDurability(data);
        }
        
        if(!getEffects().isEmpty())
        {
            PotionMeta meta = (PotionMeta)a.result().getItemMeta();
            
            for(PotionEffect effect : getEffects())
            {
                meta.addCustomEffect(effect, true);
            }
            
            a.result().setItemMeta(meta);
        }
    }
}
