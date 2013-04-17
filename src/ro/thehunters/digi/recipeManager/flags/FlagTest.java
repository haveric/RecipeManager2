package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import ro.thehunters.digi.recipeManager.Messages;

public class FlagTest extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = null;
        D = null;
        E = null;
    }
    
    // Flag code
    
    public FlagTest()
    {
//        type = FlagType.TEST;
    }
    
    public FlagTest(FlagTest flag)
    {
        this();
    }
    
    @Override
    public FlagTest clone()
    {
        return new FlagTest(this);
    }
    
    @Override
    protected boolean onParse(String value)
    {
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
//        Messages.debug("testing...");
        
        Block block = a.location().getBlock();
        
        Messages.debug(block.getType() + " | power = " + block.getBlockPower() + " | " + block.isBlockPowered() + " | " + block.isBlockIndirectlyPowered());
        
        BlockFace[] faces = new BlockFace[]
        {
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.UP,
            BlockFace.DOWN,
        };
        
        for(BlockFace f : faces)
        {
            Messages.debug(f + " = " + block.getBlockPower(f) + " | " + block.isBlockFacePowered(f) + " | " + block.isBlockFaceIndirectlyPowered(f));
        }
    }
}
