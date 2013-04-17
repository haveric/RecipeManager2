package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagLightLevel extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.LIGHTLEVEL;
        
        A = new String[]
        {
            "{flag} <min or min-max> [type] | [fail message]",
        };
        
        D = new String[]
        {
            "Checks for the light level.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "The first argument must be a number from 0 to 15 to set a minimum light level, or you can specify a number range separated - character.",
            "",
            "Optionally you can set the  [type] argument to specify light type:",
            "  any    = (default) any kind of light.",
            "  sun    = sun light only.",
            "  blocks = light form blocks (torches, furnaces, etc) only.",
            "",
            "You can also overwrite the fail message or use 'false' to hide it.",
        };
        
        E = new String[]
        {
            "{flag} 14 sun",
            "{flag} 0-4 blocks | <red>Kill the lights!",
        };
    }
    
    // Flag code
    
    private byte minLight;
    private byte maxLight;
    private char lightType;
    private String failMessage;
    
    public FlagLightLevel()
    {
    }
    
    public FlagLightLevel(FlagLightLevel flag)
    {
        minLight = flag.minLight;
        maxLight = flag.maxLight;
        lightType = flag.lightType;
        failMessage = flag.failMessage;
    }
    
    @Override
    public FlagLightLevel clone()
    {
        return new FlagLightLevel(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    /**
     * @return min light level.
     */
    public byte getMinLight()
    {
        return minLight;
    }
    
    /**
     * @return max light level, if disabled will be less than min light level.
     */
    public byte getMaxLight()
    {
        return maxLight;
    }
    
    /**
     * Minimum light level.
     * 
     * @param min
     *            0-15
     */
    public void setLightLevel(int min)
    {
        setLightLevelRange(min, 0);
    }
    
    /**
     * Light level range.
     * 
     * @param min
     *            0-15
     * @param max
     *            min-15
     */
    public void setLightLevelRange(int min, int max)
    {
        minLight = (byte)min;
        maxLight = (byte)max;
    }
    
    /**
     * @return returns 'sun', 'blocks' or 'any'.
     */
    public String getLightType()
    {
        switch(lightType)
        {
            case 's':
                return "sun";
                
            case 'b':
                return "blocks";
                
            default:
                return "any";
        }
    }
    
    /**
     * @param lightType
     *            'sun', 'blocks' or 'any'/null.
     * @throws IllegalArgumentException
     *             if any other string is specified
     */
    public void setLightType(String type)
    {
        lightType = (type == null ? 'a' : type.charAt(0));
        
        switch(lightType)
        {
            case 's':
            case 'b':
            case 'a':
                break;
            
            default:
                lightType = 'a';
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
    
    public String getLightString()
    {
        return (maxLight > minLight ? minLight + "-" + maxLight : minLight + "+");
    }
    
    public String getLightTypeString()
    {
        switch(lightType)
        {
            case 's':
                return "sun light";
                
            case 'b':
                return "block light";
                
            default:
                return "light";
        }
    }
    
    public String getFailMessage()
    {
        return failMessage;
    }
    
    public void setFailMessage(String message)
    {
        this.failMessage = message;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.split("\\|", 2);
        
        if(split.length > 1)
        {
            setFailMessage(split[1].trim());
        }
        
        value = split[0].trim().toLowerCase();
        
        int i = value.lastIndexOf(' ');
        
        if(value.length() >= (i + 1))
        {
            lightType = value.charAt(i + 1);
            
            switch(lightType)
            {
                case 's':
                case 'b':
                case 'a':
                    value = value.substring(0, i);
                    break;
                
                default:
                    lightType = 'a';
            }
        }
        
        split = value.split("-");
        
        value = split[0].trim();
        
        try
        {
            minLight = Byte.valueOf(value);
        }
        catch(NumberFormatException e)
        {
            RecipeErrorReporter.error("The " + getType() + " flag has invalid number: " + value);
            return false;
        }
        
        if(split.length > 1)
        {
            value = split[1].trim();
            
            try
            {
                maxLight = Byte.valueOf(value);
            }
            catch(NumberFormatException e)
            {
                RecipeErrorReporter.error("The " + getType() + " flag has invalid number: " + value);
                return false;
            }
        }
        
        if(minLight > 15 || maxLight > 15 || (maxLight > 0 && (minLight > maxLight || minLight < 0)))
        {
            RecipeErrorReporter.error("The " + getType() + " flag has invalid ranges: " + minLight + " to " + maxLight + "; they must be from 0 to 15 and min must be smaller than max.");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        if(!a.hasLocation())
        {
            a.addCustomReason("Needs location!");
            return;
        }
        
        Block block = a.location().getBlock();
        int light = 0;
        
        switch(lightType)
        {
            case 's':
                light = block.getLightFromSky();
                break;
            
            case 'b':
                light = block.getLightFromBlocks();
                break;
            
            default:
                light = block.getLightLevel();
        }
        
        if(light == 0)
        {
            BlockFace[] faces = BlockFace.values();
            
            for(int f = 0; f < 6; f++)
            {
                Block b = block.getRelative(faces[f]);
                int l = 0;
                
                switch(lightType)
                {
                    case 's':
                        l = b.getLightFromSky();
                        break;
                    
                    case 'b':
                        l = b.getLightFromBlocks();
                        break;
                    
                    default:
                        l = b.getLightLevel();
                }
                
                light = Math.max(light, l);
            }
        }
        
        if(light < minLight || (maxLight > minLight && light > maxLight))
        {
            a.addReason(Messages.FLAG_LIGHTLEVEL, failMessage, "{light}", getLightString(), "{type}", getLightTypeString());
        }
    }
    
    @Override
    public List<String> information()
    {
        List<String> list = new ArrayList<String>(1);
        
        list.add(Messages.FLAG_LIGHTLEVEL.get("{light}", getLightString(), "{type}", getLightTypeString()));
        
        return list;
    }
}
