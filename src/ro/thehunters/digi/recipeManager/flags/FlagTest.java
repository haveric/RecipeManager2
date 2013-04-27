package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;

public class FlagTest extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = null; //FlagType.TEST;
        A = null;
        D = null;
        E = null;
    }
    
    // Flag code
    
    public FlagTest()
    {
    }
    
    public FlagTest(FlagTest flag)
    {
    }
    
    @Override
    public FlagTest clone()
    {
        return new FlagTest(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        Messages.debug("testing...");
        
        Player p = a.player();
        Location l = a.location().add(0, 1, 0);
        
        /*
        LivingEntity ent = (LivingEntity)l.getWorld().spawnEntity(l, EntityType.WOLF);
        Player player = a.player();
        
        if(ent instanceof Wolf)
        {
            Wolf npc = (Wolf)ent;
            
            npc.setAngry(true);
            
            Messages.debug("wolf set angry");
        }
        
        if(ent instanceof Creature)
        {
            Creature npc = (Creature)ent;
            
            npc.setTarget(player);
            
            Messages.debug("target set to " + player);
        }
        */
        
        /*
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
        */
    }
}
