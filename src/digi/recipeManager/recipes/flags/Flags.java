package digi.recipeManager.recipes.flags;

import java.util.*;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class Flags implements Cloneable
{
    private String             craftMessage   = null;
    
    private Flag<int[]>        expReq         = null;
    private Flag<Integer>      expAward       = null;
    
    private Flag<int[]>        levelReq       = null;
    private Flag<Integer>      levelAward     = null;
    
    private Flag<double[]>     moneyReq       = null;
    private Flag<Double>       moneyAward     = null;
    
    private Flag<String>       permission     = null;
    private Flag<List<String>> groups         = null;
    private Flag<Set<String>>  worlds         = null;
    
    private Flag<Integer>      proximity      = null;
    private Flag<ItemStack[]>  itemsReq       = null;
    private Flag<int[]>        heightReq      = null;
    private Flag<ItemStack[]>  blocksNear     = null;
    private Flag<ItemStack[]>  blocksUnder    = null;
    private Flag<ItemStack[]>  blocksTop      = null;
    
    private int[]              explode        = null;
    private int[]              fire           = null;
    private int[]              lightning      = null;
    private String             sound          = null;
    private String[]           spawn          = null;
    private FireworkMeta       launchFirework = null;
    
    private List<String>       commands       = null;
    private Flag<int[]>        clone          = null;
    private String             math           = null;
    
    private String             remove         = null;
    private boolean            override       = false;
    private boolean            secret         = false;
    private boolean            log            = false;
    
    public Flags()
    {
    }
    
    public Flags(Flags flags)
    {
        // TODO add all variables here !!!!
        
        craftMessage = flags.craftMessage;
        
        expReq = flags.expReq;
        expAward = flags.expAward;
        
        levelReq = flags.levelReq;
        levelAward = flags.levelAward;
        
        moneyReq = flags.moneyReq;
        moneyAward = flags.moneyAward;
        
        permission = flags.permission;
        groups = flags.groups;
        worlds = flags.worlds;
        
        proximity = flags.proximity;
        itemsReq = flags.itemsReq;
        heightReq = flags.heightReq;
        blocksNear = flags.blocksNear;
        blocksUnder = flags.blocksUnder;
        blocksTop = flags.blocksTop;
        
        explode = flags.explode;
        fire = flags.fire;
        lightning = flags.lightning;
        sound = flags.sound;
        spawn = flags.spawn;
        
        commands = flags.commands;
        clone = flags.clone;
        math = flags.math;
        
        remove = flags.remove;
        override = flags.override;
        secret = flags.secret;
        log = flags.log;
    }
    
    @Override
    public Flags clone()
    {
        return new Flags(this);
    }
    
    public String getCraftMessage()
    {
        return craftMessage;
    }
    
    public void setCraftMessage(String message)
    {
        this.craftMessage = message;
    }
    
    public Flag<int[]> getLevelReq()
    {
        return levelReq;
    }
    
    public void setLevelReq(Flag<int[]> levelReq)
    {
        this.levelReq = levelReq;
    }
    
    public Flag<int[]> getExpReq()
    {
        return expReq;
    }
    
    public void setExpReq(Flag<int[]> expReq)
    {
        this.expReq = expReq;
    }
    
    public Flag<double[]> getMoneyReq()
    {
        return moneyReq;
    }
    
    public void setMoneyReq(Flag<double[]> moneyReq)
    {
        this.moneyReq = moneyReq;
    }
    
    public Flag<Integer> getLevelAward()
    {
        return levelAward;
    }
    
    public void setLevelAward(Flag<Integer> levelAward)
    {
        this.levelAward = levelAward;
    }
    
    public Flag<Integer> getExpAward()
    {
        return expAward;
    }
    
    public void setExpAward(Flag<Integer> expAward)
    {
        this.expAward = expAward;
    }
    
    public Flag<Double> getMoneyAward()
    {
        return moneyAward;
    }
    
    public void setMoneyAward(Flag<Double> moneyAward)
    {
        this.moneyAward = moneyAward;
    }
    
    public Flag<String> getPermission()
    {
        return permission;
    }
    
    public void setPermission(Flag<String> permission)
    {
        this.permission = permission;
    }
    
    public Flag<List<String>> getGroups()
    {
        return groups;
    }
    
    public void setGroups(Flag<List<String>> groups)
    {
        this.groups = groups;
    }
    
    public Flag<Set<String>> getWorlds()
    {
        return worlds;
    }
    
    public void setWorlds(Flag<Set<String>> worlds)
    {
        this.worlds = worlds;
    }
    
    public Flag<Integer> getProximity()
    {
        return proximity;
    }
    
    public void setProximity(Flag<Integer> proximity)
    {
        this.proximity = proximity;
    }
    
    public Flag<int[]> getHeightReq()
    {
        return heightReq;
    }
    
    public void setHeightReq(Flag<int[]> heightReq)
    {
        this.heightReq = heightReq;
    }
    
    public Flag<ItemStack[]> getItemsReq()
    {
        return itemsReq;
    }
    
    public void setItemsReq(Flag<ItemStack[]> itemsReq)
    {
        this.itemsReq = itemsReq;
    }
    
    public Flag<ItemStack[]> getBlocksNear()
    {
        return blocksNear;
    }
    
    public void setBlocksNear(Flag<ItemStack[]> blocksNear)
    {
        this.blocksNear = blocksNear;
    }
    
    public Flag<ItemStack[]> getBlocksUnder()
    {
        return blocksUnder;
    }
    
    public void setBlocksUnder(Flag<ItemStack[]> blocksUnder)
    {
        this.blocksUnder = blocksUnder;
    }
    
    public Flag<ItemStack[]> getBlocksTop()
    {
        return blocksTop;
    }
    
    public void setBlocksTop(Flag<ItemStack[]> blocksTop)
    {
        this.blocksTop = blocksTop;
    }
    
    public int[] getExplode()
    {
        return explode;
    }
    
    public void setExplode(int[] explode)
    {
        this.explode = explode;
    }
    
    public List<String> getCommands()
    {
        return commands;
    }
    
    public void setCommands(List<String> commands)
    {
        this.commands = commands;
    }
    
    public void addCommand(String command)
    {
        if(commands == null)
            commands = new ArrayList<String>();
        
        commands.add(command);
    }
    
    public Flag<int[]> getClone()
    {
        return clone;
    }
    
    public void setClone(Flag<int[]> clone)
    {
        this.clone = clone;
    }
    
    public String getMath()
    {
        return math;
    }
    
    public void setMath(String math)
    {
        this.math = math;
    }
    
    public int[] getLightning()
    {
        return lightning;
    }
    
    public void setLightning(int[] lightning)
    {
        this.lightning = lightning;
    }
    
    public String getSound()
    {
        return sound;
    }
    
    public void setSound(String sound)
    {
        this.sound = sound;
    }
    
    public boolean isSecret()
    {
        return secret;
    }
    
    public void setSecret(boolean secret)
    {
        this.secret = secret;
    }
    
    public boolean isOverride()
    {
        return override;
    }
    
    public void setOverride(boolean override)
    {
        this.override = override;
    }
    
    public boolean isRemove()
    {
        return remove != null;
    }
    
    public void setRemove(String message)
    {
        remove = message;
    }
    
    public boolean isLog()
    {
        return log;
    }
    
    public void setLog(boolean log)
    {
        this.log = log;
    }
    
    public int[] getFire()
    {
        return fire;
    }
    
    public void setFire(int[] fire)
    {
        this.fire = fire;
    }
    
    public String[] getSpawn()
    {
        return spawn;
    }
    
    public void setSpawn(String[] spawn)
    {
        this.spawn = spawn;
    }
    
    public FireworkMeta getLaunchFirework()
    {
        return launchFirework;
    }
    
    public void setLaunchFirework(FireworkMeta firework)
    {
        this.launchFirework = firework;
    }
}