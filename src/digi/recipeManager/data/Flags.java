package digi.recipeManager.data;

import java.util.List;
import java.util.Set;

import org.bukkit.inventory.ItemStack;

public class Flags implements Cloneable
{
    private String             failMessage     = null;
    private String             craftMessage    = null;
    
    private String             itemName        = null;
    private String[]           itemDescription = null;
    
    private String             color           = null;
    
    private String             bookTitle       = null;
    private List<String>       bookPages       = null;
    
    private List<String>       meta            = null;
    
    private Flag<int[]>        levelReq        = null;
    private Flag<int[]>        expReq          = null;
    private Flag<double[]>     moneyReq        = null;
    private Flag<Integer>      levelAward      = null;
    private Flag<Integer>      expAward        = null;
    private Flag<Double>       moneyAward      = null;
    
    private Flag<String>       permission      = null;
    private Flag<List<String>> groups          = null;
    private Flag<Set<String>>  worlds          = null;
    
    private Flag<Integer>      proximity       = null;
    private Flag<int[]>        heightReq       = null;
    private Flag<ItemStack[]>  itemsReq        = null;
    private Flag<ItemStack[]>  blocksNear      = null;
    private Flag<ItemStack[]>  blocksUnder     = null;
    private Flag<ItemStack[]>  blocksTop       = null;
    
    private Flag<int[]>        explode         = null;
    
    private List<String>       commands        = null;
    private List<String>       messages        = null;
    
    private Flag<int[]>        clone           = null;
    
    private String             math            = null;
    
    private Flag<int[]>        lightning       = null;
    private Flag<String>       sound           = null;
    
    private boolean            secret          = false;
    private boolean            override        = false;
    private boolean            remove          = false;
    private boolean            log             = false;
    
    public Flags()
    {
    }
    
    public Flags(Flags flags)
    {
        failMessage = flags.failMessage;
        itemName = flags.itemName;
        itemDescription = flags.itemDescription;
        
        levelReq = flags.levelReq;
        expReq = flags.expReq;
        moneyReq = flags.moneyReq;
        levelAward = flags.levelAward;
        expAward = flags.expAward;
        moneyAward = flags.moneyAward;
        
        permission = flags.permission;
        groups = flags.groups;
        worlds = flags.worlds;
        
        proximity = flags.proximity;
        heightReq = flags.heightReq;
        itemsReq = flags.itemsReq;
        blocksNear = flags.blocksNear;
        blocksUnder = flags.blocksUnder;
        blocksTop = flags.blocksTop;
        
        explode = flags.explode;
        
        commands = flags.commands;
        messages = flags.messages;
        
        override = flags.override;
        log = flags.log;
    }
    
    @Override
    public Flags clone()
    {
        return new Flags(this);
    }
    
    public String getFailMessage()
    {
        return failMessage;
    }
    
    public void setFailMessage(String failMessage)
    {
        this.failMessage = failMessage;
    }
    
    public String getCraftMessage()
    {
        return craftMessage;
    }
    
    public void setCraftMessage(String craftMessage)
    {
        this.craftMessage = craftMessage;
    }
    
    public String getItemName()
    {
        return itemName;
    }
    
    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }
    
    public String[] getItemDescription()
    {
        return itemDescription;
    }
    
    public void setItemDescription(String[] itemDescription)
    {
        this.itemDescription = itemDescription;
    }
    
    public String getColor()
    {
        return color;
    }
    
    public void setColor(String color)
    {
        this.color = color;
    }
    
    public String getBookTitle()
    {
        return bookTitle;
    }
    
    public void setBookTitle(String bookTitle)
    {
        this.bookTitle = bookTitle;
    }
    
    public List<String> getBookPages()
    {
        return bookPages;
    }
    
    public void setBookPages(List<String> bookPages)
    {
        this.bookPages = bookPages;
    }
    
    public List<String> getMeta()
    {
        return meta;
    }
    
    public void setMeta(List<String> meta)
    {
        this.meta = meta;
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
    
    public Flag<int[]> getExplode()
    {
        return explode;
    }
    
    public void setExplode(Flag<int[]> explode)
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
    
    public List<String> getMessages()
    {
        return messages;
    }
    
    public void setMessages(List<String> messages)
    {
        this.messages = messages;
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
    
    public Flag<int[]> getLightning()
    {
        return lightning;
    }
    
    public void setLightning(Flag<int[]> lightning)
    {
        this.lightning = lightning;
    }
    
    public Flag<String> getSound()
    {
        return sound;
    }
    
    public void setSound(Flag<String> sound)
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
        return remove;
    }
    
    public void setRemove(boolean remove)
    {
        this.remove = remove;
    }
    
    public boolean isLog()
    {
        return log;
    }
    
    public void setLog(boolean log)
    {
        this.log = log;
    }
}
