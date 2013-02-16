package digi.recipeManager.recipes.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffectType;

import digi.recipeManager.Messages;
import digi.recipeManager.RecipeManager;
import digi.recipeManager.Tools;
import digi.recipeManager.recipes.BaseRecipe.RecipeType;
import digi.recipeManager.recipes.ItemResult;

public class Flags implements Cloneable
{
    private String                       craftMessage       = null;
    
    private Flag<List<String>>           permissions        = null;
    private Flag<List<String>>           noPermissions      = null;
    
    private Flag<List<String>>           groups             = null;
    private Flag<List<String>>           noGroups           = null;
    
    private Flag<List<String>>           worlds             = null;
    private Flag<List<String>>           noWorlds           = null;
    
    private Flag<int[]>                  height             = null;
    private Flag<int[]>                  noHeight           = null;
    
    private Flag<List<PotionEffectType>> potionEffects      = null;
    private Flag<List<PotionEffectType>> noPotionEffect     = null;
    
    private Flag<List<ItemStack>>        items              = null;
    private Flag<List<ItemStack>>        noItems            = null;
    private Flag<List<ItemStack>>        equip              = null;
    private Flag<List<ItemStack>>        noEquip            = null;
    private Flag<List<ItemStack>>        hold               = null;
    private Flag<List<ItemStack>>        noHold             = null;
    
    private Flag<int[]>                  playTime           = null;
    private Flag<int[]>                  onlineTime         = null;
    
    private Flag<List<GameMode>>         gameMode           = null;
    
    // TODO maybe map<meta, value> ?
    private Flag<List<String>>           playerBukkitMeta   = null;
    private Flag<List<String>>           noPlayerBukkitMeta = null;
    
    private Flag<List<String>>           blockBukkitMeta    = null;
    private Flag<List<String>>           noBlockBukkitMeta  = null;
    
    private Flag<int[]>                  exp                = null;
    private Flag<Integer>                expAward           = null;
    
    private Flag<int[]>                  levelReq           = null;
    private Flag<Integer>                levelAward         = null;
    
    private Flag<double[]>               moneyReq           = null;
    private Flag<Double>                 moneyAward         = null;
    
    private Flag<Integer>                proximity          = null;
    private Flag<ItemStack[]>            blocksNear         = null;
    private Flag<ItemStack[]>            blocksUnder        = null;
    private Flag<ItemStack[]>            blocksTop          = null;
    
    private int[]                        explode            = null;
    private int[]                        fire               = null;
    private int[]                        lightning          = null;
    private String                       sound              = null;
    private String[]                     spawn              = null;
    private FireworkMeta                 launchFirework     = null;
    
    private List<String>                 commands           = null;
    private Flag<int[]>                  clone              = null;
    private String                       math               = null;
    
    private boolean                      secret             = false;
    private boolean                      log                = false;
    
    public boolean                       test               = false;
    
    public Flags()
    {
    }
    
    public Flags(Flags flags)
    {
        // TODO add all variables here !!!!
        
        craftMessage = flags.craftMessage;
        
        exp = flags.exp;
        expAward = flags.expAward;
        
        levelReq = flags.levelReq;
        levelAward = flags.levelAward;
        
        moneyReq = flags.moneyReq;
        moneyAward = flags.moneyAward;
        
        permissions = flags.permissions;
        noPermissions = flags.noPermissions;
        
        groups = flags.groups;
        noGroups = flags.noGroups;
        
        worlds = flags.worlds;
        noWorlds = flags.noWorlds;
        
        proximity = flags.proximity;
        height = flags.height;
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
        return exp;
    }
    
    public void setExpReq(Flag<int[]> expReq)
    {
        this.exp = expReq;
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
    
    public Flag<Integer> getProximity()
    {
        return proximity;
    }
    
    public void setProximity(Flag<Integer> proximity)
    {
        this.proximity = proximity;
    }
    
    public Flag<int[]> getHeight()
    {
        return height;
    }
    
    public void setHeight(Flag<int[]> height)
    {
        this.height = height;
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
    
    public Flag<List<String>> getPermissions()
    {
        return permissions;
    }
    
    public void setPermissions(Flag<List<String>> permissions)
    {
        this.permissions = permissions;
    }
    
    public Flag<List<String>> getNoPermissions()
    {
        return noPermissions;
    }
    
    public void setNoPermissions(Flag<List<String>> noPermissions)
    {
        this.noPermissions = noPermissions;
    }
    
    public Flag<List<String>> getGroups()
    {
        return groups;
    }
    
    public void setGroups(Flag<List<String>> groups)
    {
        this.groups = groups;
    }
    
    public Flag<List<String>> getNoGroups()
    {
        return noGroups;
    }
    
    public void setNoGroups(Flag<List<String>> noGroups)
    {
        this.noGroups = noGroups;
    }
    
    public Flag<List<String>> getWorlds()
    {
        return worlds;
    }
    
    public void setWorlds(Flag<List<String>> worlds)
    {
        this.worlds = worlds;
    }
    
    public Flag<List<String>> getNoWorlds()
    {
        return noWorlds;
    }
    
    public void setNoWorlds(Flag<List<String>> noWorlds)
    {
        this.noWorlds = noWorlds;
    }
    
    public Flag<List<PotionEffectType>> getPotionEffects()
    {
        return potionEffects;
    }
    
    public void setPotionEffects(Flag<List<PotionEffectType>> potionEffects)
    {
        this.potionEffects = potionEffects;
    }
    
    public Flag<List<PotionEffectType>> getNoPotionEffect()
    {
        return noPotionEffect;
    }
    
    public void setNoPotionEffect(Flag<List<PotionEffectType>> noPotionEffect)
    {
        this.noPotionEffect = noPotionEffect;
    }
    
    public Flag<List<ItemStack>> getItems()
    {
        return items;
    }
    
    public void setItems(Flag<List<ItemStack>> items)
    {
        this.items = items;
    }
    
    public Flag<List<ItemStack>> getNoItems()
    {
        return noItems;
    }
    
    public void setNoItems(Flag<List<ItemStack>> noItems)
    {
        this.noItems = noItems;
    }
    
    public Flag<List<ItemStack>> getEquip()
    {
        return equip;
    }
    
    public void setEquip(Flag<List<ItemStack>> equip)
    {
        this.equip = equip;
    }
    
    public Flag<List<ItemStack>> getNoEquip()
    {
        return noEquip;
    }
    
    public void setNoEquip(Flag<List<ItemStack>> noEquip)
    {
        this.noEquip = noEquip;
    }
    
    public Flag<List<ItemStack>> getHold()
    {
        return hold;
    }
    
    public void setHold(Flag<List<ItemStack>> hold)
    {
        this.hold = hold;
    }
    
    public Flag<List<ItemStack>> getNoHold()
    {
        return noHold;
    }
    
    public void setNoHold(Flag<List<ItemStack>> noHold)
    {
        this.noHold = noHold;
    }
    
    public Flag<int[]> getPlayTime()
    {
        return playTime;
    }
    
    public void setPlayTime(Flag<int[]> playTime)
    {
        this.playTime = playTime;
    }
    
    public Flag<int[]> getOnlineTime()
    {
        return onlineTime;
    }
    
    public void setOnlineTime(Flag<int[]> onlineTime)
    {
        this.onlineTime = onlineTime;
    }
    
    public Flag<List<GameMode>> getGameMode()
    {
        return gameMode;
    }
    
    public void setGameMode(Flag<List<GameMode>> gameMode)
    {
        this.gameMode = gameMode;
    }
    
    public Flag<List<String>> getPlayerBukkitMeta()
    {
        return playerBukkitMeta;
    }
    
    public void setPlayerBukkitMeta(Flag<List<String>> playerBukkitMeta)
    {
        this.playerBukkitMeta = playerBukkitMeta;
    }
    
    public Flag<List<String>> getNoPlayerBukkitMeta()
    {
        return noPlayerBukkitMeta;
    }
    
    public void setNoPlayerBukkitMeta(Flag<List<String>> noPlayerBukkitMeta)
    {
        this.noPlayerBukkitMeta = noPlayerBukkitMeta;
    }
    
    public Flag<List<String>> getBlockBukkitMeta()
    {
        return blockBukkitMeta;
    }
    
    public void setBlockBukkitMeta(Flag<List<String>> blockBukkitMeta)
    {
        this.blockBukkitMeta = blockBukkitMeta;
    }
    
    public Flag<List<String>> getNoBlockBukkitMeta()
    {
        return noBlockBukkitMeta;
    }
    
    public void setNoBlockBukkitMeta(Flag<List<String>> noBlockBukkitMeta)
    {
        this.noBlockBukkitMeta = noBlockBukkitMeta;
    }
    
    public boolean isTest()
    {
        return test;
    }
    
    public void setTest(boolean test)
    {
        this.test = test;
    }
    
    // ------------------------------------------------------------------------------------------
    
    public boolean checkExp(Player player, List<String> reasons)
    {
        if(player == null)
            return false; // player dependant
            
        if(exp != null)
        {
            int[] range = exp.getValue();
            int exp = player.getTotalExperience();
            
            if(range[0] > exp || (range.length > 1 ? range[1] < exp : false))
            {
                Messages.CRAFT_FLAG_EXP.addReason(reasons, playTime.getFailMessage(), "{time}", range[0] + (range.length > 1 ? " - " + range[1] : ""));
                return false;
            }
        }
        
        return true;
    }
    
    public boolean checkGameMode(Player player, List<String> reasons)
    {
        if(player == null)
            return false; // player dependant flag
            
        if(gameMode != null && !gameMode.getValue().contains(player.getGameMode()))
        {
            Messages.CRAFT_FLAG_GAMEMODE.addReason(reasons, gameMode.getFailMessage(), "{gamemodes}", Tools.convertListToString(gameMode.getValue()));
            return false;
        }
        
        return true;
    }
    
    public boolean checkHeight(Player player, Location location, RecipeType recipeType, List<String> reasons)
    {
        // Only allow location to be null if player not null and crafting a workbench recipe
        Integer y = (location == null ? (player != null && (recipeType == RecipeType.CRAFT || recipeType == RecipeType.COMBINE) ? player.getLocation().getBlockY() : null) : location.getBlockY());
        
        if(y == null)
            return false; // location dependant
            
        boolean okHeight = true;
        boolean okNoHeight = true;
        
        if(height != null)
        {
            int[] range = height.getValue();
            
            if(range[0] > y || (range.length > 1 ? range[1] < y : false))
            {
                Messages.CRAFT_FLAG_HEIGHT.addReason(reasons, height.getFailMessage(), "{height}", range[0] + (range.length > 1 ? " - " + range[1] : ""));
                okHeight = false;
            }
        }
        
        if(noHeight != null)
        {
            int[] range = noHeight.getValue();
            
            if(range[0] <= y || (range.length > 1 ? range[1] >= y : false))
            {
                Messages.CRAFT_FLAG_NOHEIGHT.addReason(reasons, noHeight.getFailMessage(), "{height}", range[0] + (range.length > 1 ? " - " + range[1] : ""));
                okNoHeight = false;
            }
        }
        
        return okHeight && okNoHeight;
    }
    
    public boolean checkTime(Player player, String playerName, List<String> reasons)
    {
        boolean okPlayTime = true;
        boolean okOnlineTime = true;
        
        if(playTime != null)
        {
            if(playerName == null)
                return false; // player name dependant
                
            int[] range = playTime.getValue();
            
            OfflinePlayer p = Bukkit.getOfflinePlayer(playerName);
            int time = Math.round(p.getFirstPlayed() / 1000);
            
            if(range[0] > time || (range.length > 1 ? range[1] < time : false))
            {
                Messages.CRAFT_FLAG_PLAYTIME.addReason(reasons, playTime.getFailMessage(), "{time}", range[0] + (range.length > 1 ? " - " + range[1] : ""));
                okPlayTime = false;
            }
        }
        
        if(onlineTime != null)
        {
            if(player == null)
                return false; // player dependant
                
            int[] range = onlineTime.getValue();
            int time = Math.round((System.currentTimeMillis() - player.getLastPlayed()) / 1000);
            
            if(range[0] <= time || (range.length > 1 ? range[1] >= time : false))
            {
                Messages.CRAFT_FLAG_ONLINETIME.addReason(reasons, onlineTime.getFailMessage(), "{time}", range[0] + (range.length > 1 ? " - " + range[1] : ""));
                okOnlineTime = false;
            }
        }
        
        return okPlayTime && okOnlineTime;
    }
    
    public boolean checkInventory(Player player, List<String> reasons)
    {
        if(player == null)
            return false; // player dependant flag
            
        boolean okItems = false;
        boolean okNoItems = true;
        boolean okEquip = false;
        boolean okNoEquip = true;
        boolean okHold = false;
        boolean okNoHold = true;
        
        PlayerInventory inv = player.getInventory();
        ItemStack[] armor = inv.getArmorContents();
        ItemStack hand = inv.getItemInHand();
        
        if(items != null)
        {
            for(ItemStack i : items.getValue())
            {
                if(inv.containsAtLeast(i, i.getAmount()))
                {
                    okItems = true;
                    break;
                }
            }
            
            if(!okItems)
                Messages.CRAFT_FLAG_ITEMS.addReason(reasons, items.getFailMessage(), "{items}", Tools.convertListToString(items.getValue()));
        }
        
        if(noItems != null)
        {
            for(ItemStack i : noItems.getValue())
            {
                if(inv.containsAtLeast(i, i.getAmount()))
                {
                    okNoItems = false;
                    break;
                }
            }
            
            if(!okNoItems)
                Messages.CRAFT_FLAG_NOITEMS.addReason(reasons, noItems.getFailMessage(), "{items}", Tools.convertListToString(noItems.getValue()));
        }
        
        if(equip != null)
        {
            for(ItemStack i : equip.getValue())
            {
                for(ItemStack a : armor)
                {
                    if(i.isSimilar(a))
                    {
                        okEquip = true;
                        break;
                    }
                }
            }
            
            if(!okEquip)
                Messages.CRAFT_FLAG_EQUIP.addReason(reasons, equip.getFailMessage(), "{items}", Tools.convertListToString(equip.getValue()));
        }
        
        if(noEquip != null)
        {
            for(ItemStack i : noItems.getValue())
            {
                for(ItemStack a : armor)
                {
                    if(i.isSimilar(a))
                    {
                        okNoEquip = false;
                        break;
                    }
                }
            }
            
            if(!okNoEquip)
                Messages.CRAFT_FLAG_NOEQUIP.addReason(reasons, noEquip.getFailMessage(), "{items}", Tools.convertListToString(noEquip.getValue()));
        }
        
        if(hold != null)
        {
            for(ItemStack i : hold.getValue())
            {
                if(i.isSimilar(hand))
                {
                    okHold = true;
                    break;
                }
            }
            
            if(!okHold)
                Messages.CRAFT_FLAG_HOLD.addReason(reasons, hold.getFailMessage(), "{items}", Tools.convertListToString(hold.getValue()));
        }
        
        if(noHold != null)
        {
            for(ItemStack i : noHold.getValue())
            {
                if(i.isSimilar(hand))
                {
                    okNoHold = false;
                    break;
                }
            }
            
            if(!okNoHold)
                Messages.CRAFT_FLAG_NOHOLD.addReason(reasons, noHold.getFailMessage(), "{items}", Tools.convertListToString(noHold.getValue()));
        }
        
        return okItems && okNoItems && okEquip && okNoEquip && okHold && okNoHold;
    }
    
    public boolean checkPotionEffects(Player player, List<String> reasons)
    {
        if(player == null)
            return false; // player dependant flag
            
        boolean okPotion = false;
        boolean okNoPotion = true;
        
        if(potionEffects != null)
        {
            for(PotionEffectType e : potionEffects.getValue())
            {
                if(player.hasPotionEffect(e))
                {
                    okPotion = true;
                    break;
                }
            }
            
            if(!okPotion)
                Messages.CRAFT_FLAG_POTIONEFFECTS.addReason(reasons, potionEffects.getFailMessage(), "{effects}", Tools.convertListToString(potionEffects.getValue()));
        }
        
        if(noPotionEffect != null)
        {
            for(PotionEffectType e : noPotionEffect.getValue())
            {
                if(player.hasPotionEffect(e))
                {
                    okNoPotion = false;
                    break;
                }
            }
            
            if(!okNoPotion)
                Messages.CRAFT_FLAG_NOPOTIONEFFECTS.addReason(reasons, noPotionEffect.getFailMessage(), "{effects}", Tools.convertListToString(noPotionEffect.getValue()));
        }
        
        return okPotion && okNoPotion;
    }
    
    public boolean checkPermissions(Player player, List<String> reasons)
    {
        if(player == null)
            return false; // player dependant flag
            
        boolean okPermissions = false;
        boolean okNoPermissions = true;
        
        if(permissions != null)
        {
            for(String s : permissions.getValue())
            {
                if(player.hasPermission(s))
                {
                    okPermissions = true;
                    break;
                }
            }
            
            if(!okPermissions)
                Messages.CRAFT_FLAG_PERMISSIONS.addReason(reasons, permissions.getFailMessage(), "{permissions}", Tools.convertListToString(permissions.getValue()));
        }
        
        if(noPermissions != null)
        {
            for(String s : noPermissions.getValue())
            {
                if(player.hasPermission(s))
                {
                    okNoPermissions = false;
                    break;
                }
            }
            
            if(!okNoPermissions)
                Messages.CRAFT_FLAG_NOPERMISSIONS.addReason(reasons, noPermissions.getFailMessage(), "{permissions}", Tools.convertListToString(noPermissions.getValue()));
        }
        
        return okPermissions && okNoPermissions;
    }
    
    public boolean checkGroups(Player player, List<String> reasons)
    {
        if(player == null)
            return false; // player dependant flag
            
        if(!RecipeManager.getPermissions().isEnabled()) // if we don't have a vault-connected group plugin then just allow the recipe...
            return true;
        
        String name = player.getName();
        boolean okGroups = false;
        boolean okNoGroups = true;
        
        if(groups != null)
        {
            for(String s : groups.getValue())
            {
                if(RecipeManager.getPermissions().playerInGroup(name, s))
                {
                    okGroups = true;
                    break;
                }
            }
            
            if(!okGroups)
                Messages.CRAFT_FLAG_GROUPS.addReason(reasons, groups.getFailMessage(), "{groups}", Tools.convertListToString(groups.getValue()));
        }
        
        if(noGroups != null)
        {
            for(String s : noGroups.getValue())
            {
                if(RecipeManager.getPermissions().playerInGroup(name, s))
                {
                    okNoGroups = false;
                    break;
                }
            }
            
            if(!okNoGroups)
                Messages.CRAFT_FLAG_NOGROUPS.addReason(reasons, noGroups.getFailMessage(), "{groups}", Tools.convertListToString(noGroups.getValue()));
        }
        
        return okGroups && okNoGroups;
    }
    
    public boolean checkWorlds(Player player, Location location, RecipeType recipeType, List<String> reasons)
    {
        // Only allow location to be null if player not null and crafting a workbench recipe
        String world = (location == null ? (player != null && (recipeType == RecipeType.CRAFT || recipeType == RecipeType.COMBINE) ? player.getLocation().getWorld().getName() : null) : location.getWorld().getName());
        
        if(world == null)
            return false; // location dependant
            
        boolean ok = true;
        
        if(!worlds.getValue().contains(world)) // Not in any worlds specified
        {
            Messages.CRAFT_FLAG_WORLDS.addReason(reasons, worlds.getFailMessage(), "{worlds}", Tools.convertListToString(worlds.getValue()));
            ok = false;
        }
        
        if(noWorlds.getValue().contains(world)) // Is in one of the restricted worlds
        {
            Messages.CRAFT_FLAG_NOWORLDS.addReason(reasons, noWorlds.getFailMessage(), "{worlds}", Tools.convertListToString(noWorlds.getValue()));
            ok = false;
        }
        
        return ok;
    }
    
    public boolean checkBukkitMeta(Player player, Location location, List<String> reasons)
    {
        boolean okPlayerBukkitMeta = true;
        boolean okNoPlayerBukkitMeta = true;
        boolean okBlockBukkitMeta = true;
        boolean okNoBlockBukkitMeta = true;
        
        if(player != null)
        {
            okPlayerBukkitMeta = false;
            
            if(playerBukkitMeta != null)
            {
                for(String s : playerBukkitMeta.getValue())
                {
                    if(player.hasMetadata(s))
                    {
                        okPlayerBukkitMeta = true;
                        break;
                    }
                }
                
                if(!okPlayerBukkitMeta)
                    Messages.CRAFT_FLAG_PLAYERBUKKITMETA.addReason(reasons, playerBukkitMeta.getFailMessage());
            }
            
            if(noPlayerBukkitMeta != null)
            {
                for(String s : noPlayerBukkitMeta.getValue())
                {
                    if(player.hasMetadata(s))
                    {
                        okNoPlayerBukkitMeta = false;
                        break;
                    }
                }
                
                if(!okNoPlayerBukkitMeta)
                    Messages.CRAFT_FLAG_NOPLAYERBUKKITMETA.addReason(reasons, noPlayerBukkitMeta.getFailMessage());
            }
        }
        else
        {
            okPlayerBukkitMeta = false;
            okNoPlayerBukkitMeta = true;
        }
        
        if(location != null)
        {
            Block block = location.getBlock();
            okBlockBukkitMeta = false;
            
            if(blockBukkitMeta != null)
            {
                for(String s : blockBukkitMeta.getValue())
                {
                    if(block.hasMetadata(s))
                    {
                        okBlockBukkitMeta = true;
                        break;
                    }
                }
                
                if(!okBlockBukkitMeta)
                    Messages.CRAFT_FLAG_BLOCKBUKKITMETA.addReason(reasons, blockBukkitMeta.getFailMessage());
            }
            
            if(noBlockBukkitMeta != null)
            {
                for(String s : noBlockBukkitMeta.getValue())
                {
                    if(block.hasMetadata(s))
                    {
                        okNoBlockBukkitMeta = false;
                        break;
                    }
                }
                
                if(!okNoBlockBukkitMeta)
                    Messages.CRAFT_FLAG_NOBLOCKBUKKITMETA.addReason(reasons, noBlockBukkitMeta.getFailMessage());
            }
        }
        else
        {
            okBlockBukkitMeta = false;
            okNoBlockBukkitMeta = true;
        }
        
        return okPlayerBukkitMeta && okNoPlayerBukkitMeta && okBlockBukkitMeta && okNoBlockBukkitMeta;
    }
    
    public void applyCraftMessage(Player player)
    {
        if(player != null && craftMessage != null)
            Messages.send(player, craftMessage);
    }
    
    public void applyLaunchFirework(Location location)
    {
        if(location == null || launchFirework == null)
            return;
        
        Firework ent = (Firework)location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        ent.setFireworkMeta(launchFirework);
    }
    
    public boolean applyExp(Player player)
    {
        if(player == null)
            return false; // player dependant
            
        if(expAward != null)
        {
            Integer award = expAward.getValue();
            
            if(award < 0)
            {
                award += player.getTotalExperience();
                player.setTotalExperience(0);
                player.setLevel(0);
            }
            
            if(award > 0)
                player.giveExp(award);
        }
        
        return true;
    }
    
    public boolean checkFlags(Player player, String playerName, Location location, RecipeType recipeType, ItemResult result, List<String> reasons)
    {
        if(reasons != null)
            reasons.clear();
        
        checkPermissions(player, reasons);
        checkGroups(player, reasons);
        checkWorlds(player, location, recipeType, reasons);
        checkGameMode(player, reasons);
        checkHeight(player, location, recipeType, reasons);
        checkInventory(player, reasons);
        checkTime(player, playerName, reasons);
        checkBukkitMeta(player, location, reasons);
        
        // TODO add all flag checks
        
        if(this instanceof RecipeFlags)
        {
            RecipeFlags f = (RecipeFlags)this;
            
            // ...
        }
        
        if(this instanceof ItemFlags)
        {
            ItemFlags f = (ItemFlags)this;
            
            // ...
        }
        
        return reasons.isEmpty();
    }
    
    public boolean applyFlags(Player player, String playerName, Location location, RecipeType recipeType, ItemResult result, List<String> reasons)
    {
        if(reasons != null)
            reasons.clear();
        
        applyExp(player);
        applyCraftMessage(player);
        applyLaunchFirework(location);
        
        if(this instanceof RecipeFlags)
        {
            RecipeFlags f = (RecipeFlags)this;
            
            // ...
        }
        
        if(this instanceof ItemFlags)
        {
            ItemFlags f = (ItemFlags)this;
            
            // ...
        }
        
        return reasons.isEmpty();
    }
}