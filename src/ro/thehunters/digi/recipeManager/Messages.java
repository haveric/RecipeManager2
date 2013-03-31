package ro.thehunters.digi.recipeManager;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public enum Messages
{
    FLAG_DISABLED("<red>Recipe is disabled."),
    
    FLAG_GAMEMODE("<red>Allowed gamemodes: {gamemodes}"),
    
    FLAG_HEIGHT("<red>Need height: {height}"),
    FLAG_NOHEIGHT("<red>Unallowed height: {height}"),
    
    FLAG_ONLINETIME("<red>Need online time: {time}"),
    FLAG_PLAYTIME("<red>Need total play time: {time}"),
    
    FLAG_ITEMS("<red>Need in inventory: {items}"),
    FLAG_NOITEMS("<red>Unallowed in inventory: {items}"),
    FLAG_EQUIP("<red>Need equipped: {items}"),
    FLAG_NOEQUIP("<red>Unallowed equipped: {items}"),
    FLAG_HOLD("<red>Need in hand: {items}"),
    FLAG_NOHOLD("<red>Unallowed in hand: {items}"),
    
    FLAG_INGREDIENTCONDITIONS_NOAMOUNT("<red>{item} needs to have {amount} amount"),
    
    FLAG_REQEXP("<red>Need EXP: {exp}"),
    
    FLAG_MODEXP_ADD("<green>+{amount} EXP"),
    FLAG_MODEXP_SUB("<red>-{amount} EXP"),
    FLAG_MODEXP_SET("<yellow>EXP set to {amount}"),
    
    FLAG_REQLEVEL("<red>Need level: {level}"),
    
    FLAG_MODLEVEL_ADD("<green>+{amount} level"),
    FLAG_MODLEVEL_SUB("<red>-{amount} level"),
    FLAG_MODLEVEL_SET("<yellow>Level set to {amount}"),
    
    FLAG_REQMONEY("<red>Need money: {amount}"),
    
    FLAG_MODMONEY_ADD("<green>+{money}"),
    FLAG_MODMONEY_SUB("<red>-{money}"),
    FLAG_MODMONEY_SET("<yellow>Money set to {money}"),
    
    FLAG_PERMISSION_NEED("<red>Allowed permissions: {permissions}"),
    FLAG_PERMISSION_UNALLOWED("<red>Unallowed permissions: {permissions}"),
    
    FLAG_GROUPS("<red>Allowed groups: {groups}"),
    FLAG_NOGROUPS("<red>Unallowed groups: {groups}"),
    
    FLAG_WORLDS("<red>Allowed worlds: {worlds}"),
    FLAG_NOWORLDS("<red>Unallowed worlds: {worlds}"),
    
    FLAG_PLAYERBUKKITMETA("<red>You need to be special..."),
    FLAG_NOPLAYERBUKKITMETA("<red>You're too special..."),
    
    FLAG_BLOCKBUKKITMETA("<red>Needs special block..."),
    FLAG_NOBLOCKBUKKITMETA("<red>Block to special..."),
    
    FLAG_POTIONEFFECTS("<red>Need potion effect: {effects}"),
    FLAG_NOPOTIONEFFECTS("<red>Unallowed potion effect: {effects}"),
    
    FLAG_REALTIME("<red>Allowed between {mindate} and {maxdate}"),
    
    FLAG_COOLDOWN_FAIL("<red>Cooldown: {time}"),
    FLAG_COOLDOWN_CRAFT("<yellow>Cooldown set to {time}"),
    
    FLAG_CLONE_RESULTDISPLAY("<dark_aqua><italic>(clone)"),
    
    FLAG_PREFIX_RECIPE("<gray>(Recipe) <reset>"),
    FLAG_PREFIX_RESULT("<gray>(Result) <reset>"),
    
    CRAFT_REPAIR_DISABLED("<red>Repair recipes disabled."),
    
    CRAFT_SPECIAL_LEATHERDYE("<red>Leather dyeing is disabled."),
    CRAFT_SPECIAL_FIREWORKS("<red>Firework crafting is disabled."),
    CRAFT_SPECIAL_MAP_CLONING("<red>Map cloning is disabled."),
    CRAFT_SPECIAL_MAP_EXTENDING("<red>Map extending is disabled."),
    
    CRAFT_RESULT_DENIED_TITLE("<yellow><underline>Unable to craft:"),
    CRAFT_RESULT_DENIED_REASON("<aqua><bold>* <reset>{reason}"),
    
    CRAFT_RESULT_RECIEVE_UNKNOWN("<light_purple><underline>You will get an unknown item!"),
    CRAFT_RESULT_RECIEVE_TITLE("<light_purple><underline>You will get a random item:"),
    CRAFT_RESULT_RECIEVE_ITEM("<dark_green>{chance} <green>{item} {clone}"),
    CRAFT_RESULT_RECIEVE_SECRETS("<dark_aqua>{num} secret item(s)..."),
    CRAFT_RESULT_RECIEVE_NOTHING("<red>{chance} Failure chance"),
    CRAFT_RESULT_UNAVAILABLE("<dark_red>{num} unavailable item(s)..."),
    
    CRAFT_RECIPE_MULTI_FAILED("<yellow>NOTE: <white>That sound was the recipe failing by chance! See 'fail chance' in the result description."),
    CRAFT_RECIPE_MULTI_NOSHIFTCLICK("<yellow>NOTE: <white>Recipe has more than one result, shift+clicking will only craft it once."),
    CRAFT_RECIPE_MULTI_CURSORFULL("<yellow>NOTE: <white>Cursor is full or not same type as result, put the held item in inventory or use Shift+Click to craft one by one to inventory."),
    
    CRAFT_RECIPE_FLAG_NOSHIFTCLICK("<yellow>NOTE: <white>Recipe is special, shift-clicking will only craft it once."),
    
    SMELT_FUEL_NEEDINGREDIENT("<red>Fuel {fuel}<red> needs specific ingredient: {ingredient}"),
    SMELT_FUEL_NEEDFUEL("<red>Ingredient {ingredient}<red> needs specific fuel: {fuel}"),
    
    ITEM_ANYDATA("<gray>any"),
    
    LASTCHANGED(Files.LASTCHANGED_MESSAGES),
    
    RECIPEBOOK_VOLUME("Volume {volume}"),
    RECIPEBOOK_VOLUMEOFVOLUMES("Volume {volume} of {volumes}"),
    RECIPEBOOK_HEADER_SHAPED("<black><bold>SHAPED RECIPE"),
    RECIPEBOOK_HEADER_SHAPELESS("<black><bold>SHAPELESS RECIPE"),
    RECIPEBOOK_HEADER_SMELT("<black><bold>FURNACE RECIPE"),
    RECIPEBOOK_HEADER_FUELS("<black><bold>FURNACE FUELS"),
    RECIPEBOOK_HEADER_SHAPE("<black><underline>Shape"),
    RECIPEBOOK_HEADER_INGREDIENTS("<black><underline>Ingredients"),
    RECIPEBOOK_HEADER_INGREDIENT("<black><underline>Ingredient"),
    RECIPEBOOK_HEADER_REQUIREFUEL("<black><underline>Require Fuel"),
    RECIPEBOOK_MORERESULTS("<dark_green>+{amount} more results"),
    
    CMD_GETBOOK_INVALIDNUMBER("<red>Volume argument must be a number!"),
    CMD_GETBOOK_NOTEXIST("<red>No books found by '{arg}'."),
    CMD_GETBOOK_MANYMATCHES("<red>Found {num} books by '{arg}':"),
    CMD_GETBOOK_GIVEN("<green>Got book: {title}"),
    
    EVENTS_UPDATEBOOK_EXTINCT("<red>Your '<white>{title}<red>' recipe book does not exist anymore, it won't be updated anymore."),
    EVENTS_UPDATEBOOK_DONE("<gray>Your '<white>{title}<gray>' recipe book has been updated."),
    
    CMD_BOOKS_NOBOOKS("<red>No generated books."),
    CMD_BOOKS_HEADER("<yellow>Generated recipe books ({number}):"),
    CMD_BOOKS_ITEM("<white>{title} <gray>(volumes: {volumes})"),
    
    CMD_RMFINDITEM_USAGE("<yellow>Usage: <white>/{command} <gray>"),
    CMD_RMFINDITEM_INVALIDHELDITEM(""),
    CMD_RMFINDITEM_HEADER(""),
    CMD_RMFINDITEM_LIST(""),
    CMD_RMFINDITEM_NOTFOUND(""),
    
    ;
    
    private static final Map<String, Set<String>> sent = new HashMap<String, Set<String>>();
    private static FileConfiguration yml;
    
    private String path;
    private String message;
    
    private Messages(String message)
    {
        this.path = name().replace('_', '.').toLowerCase();
        this.message = message;
    }
    
    private void asign()
    {
        message = yml.getString(path, message); // get the message or use the predefined one if doesn't exist
        
        if(message != null && (message.isEmpty() || message.equals("false")))
        {
            message = null; // disable message if empty or 'false'
        }
    }
    
    /**
     * (Re)Loads all messages from messages.yml
     * 
     * @param force
     */
    public static void reload(CommandSender sender)
    {
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "messages.yml");
        
        if(!file.exists())
        {
            yml = new YamlConfiguration();
            yml.options().header("Configurable messages.\nParts surrounded by { and } are variables that get replaced in-game, you can move them around or even remove them if you want.\nTo disable messages you can just delete the message or use 'false'.");
            yml.options().copyHeader(true);
            
            for(Messages msg : values())
            {
                yml.set(msg.path, msg.message);
            }
            
            try
            {
                yml.save(file);
            }
            catch(Exception e)
            {
                error(sender, e, "Couldn't save 'messages.yml' !");
            }
            
            send(sender, ChatColor.GREEN + "Generated 'messages.yml' file.");
        }
        else
        {
            yml = YamlConfiguration.loadConfiguration(file);
        }
        
        for(Messages msg : values())
        {
            msg.asign();
        }
        
        try
        {
            if(LASTCHANGED == null || LASTCHANGED.message == null || !LASTCHANGED.message.equals(Files.LASTCHANGED_MESSAGES))
            {
                send(sender, "<yellow>messages.yml has changed! You should delete it, use 'rmreload' to re-generate it and then re-configure it, and then rmreload again.");
            }
        }
        catch(Exception e)
        {
            send(sender, "<yellow>Error reading messages.yml's version! You should delete it to allow it to re-generate the newest version!");
        }
    }
    
    /**
     * Gets the message for the selected enum.<br>
     * Processes colors as well.
     * 
     * @return
     */
    public String get()
    {
        return Tools.parseColors(message, false);
    }
    
    @Override
    public String toString()
    {
        return get();
    }
    
    /**
     * Gets the message for the selected enum.<br>
     * Processes colors and variables as well.
     * 
     * @return
     */
    public String get(Object... variables)
    {
        return Tools.replaceVariables(Tools.parseColors(message, false), variables);
    }
    
    public String getCustom(String customMessage, Object... variables)
    {
        String msg = get();
        
        if(customMessage != null) // recipe has custom message
        {
            // if flag message is set to "false" then don't show the message
            msg = (customMessage.equals("false") ? null : customMessage);
        }
        else if(msg != null && msg.equals("false"))
        {
            // message from messages.yml is "false", don't show the message
            msg = null;
        }
        
        return msg == null ? null : Tools.replaceVariables(msg, variables);
    }
    
    /**
     * Send the selected enum message to a player or console. <br>
     * Will not be displayed if the message is set to "false" in the messages.yml.
     * 
     * @param sender
     *            player or console
     */
    public void print(CommandSender sender)
    {
        if(sender == null)
            return;
        
        if(message != null)
            send(sender, message);
    }
    
    /**
     * Send the selected enum message to a player or console with an overwriteable message from a recipe. <br>
     * The recipeMessage has priority if it's not null. <br>
     * If the priority message is "false" it will not be displayed.
     * 
     * @param sender
     *            player or console
     * @param recipeMessage
     *            overwrite message, ignored if null, don't display if "false"
     */
    public void print(CommandSender sender, String recipeMessage)
    {
        if(sender == null)
            return;
        
        if(recipeMessage != null) // recipe has custom message ?
        {
            if(!recipeMessage.equals("false")) // if it's not "false" send it, otherwise don't.
                send(sender, recipeMessage);
        }
        else if(message != null) // message not set to "false" in messages.yml (replaced with null to save memory)
            send(sender, message);
    }
    
    /**
     * Send the selected enum message to a player or console with an overwriteable message from a recipe. <br>
     * The recipeMessage has priority if it's not null. <br>
     * If the priority message is "false" it will not be displayed. <br>
     * Additionally you can specify variables to replace in the message. <br>
     * The variable param must be a 2D String array that has pairs of 2 strings, variable and replacement value.
     * 
     * @param sender
     *            player or console
     * @param recipeMessage
     *            overwrite message, ignored if null, don't display if "false"
     * @param variables
     *            the variables array
     */
    public void print(CommandSender sender, String recipeMessage, Object... variables)
    {
        if(sender == null)
            return;
        
        String msg = message;
        
        if(recipeMessage != null) // recipe has custom message
        {
            if(recipeMessage.equals("false")) // if recipe message is set to "false" then don't show the message
                return;
            
            msg = recipeMessage;
        }
        else if(msg == null) // message from messages.yml is "false", don't show the message
            return;
        
        msg = Tools.replaceVariables(msg, variables);
        
        send(sender, msg);
    }
    
    /**
     * Send this message only once per connection.
     * 
     * @param sender
     */
    public void printOnce(CommandSender sender)
    {
        printOnce(sender, null);
    }
    
    /**
     * Send this message only once per connection.
     * 
     * @param sender
     * @param recipeMessage
     * @param variables
     */
    public void printOnce(CommandSender sender, String recipeMessage, Object... variables)
    {
        if(sender == null)
            return;
        
        Set<String> set = sent.get(sender.getName());
        
        if(set == null)
        {
            set = new HashSet<String>();
            sent.put(sender.getName(), set);
        }
        
        if(!set.contains(path))
        {
            set.add(path);
            print(sender, recipeMessage, variables);
        }
    }
    
    protected static void clearPlayer(String name)
    {
        sent.remove(name);
    }
    
    /**
     * Sends an array of messages to a player or console.<br>
     * Message supports &lt;color&gt; codes.
     * 
     * @param sender
     * @param messages
     */
    public static void send(CommandSender sender, String[] messages)
    {
        if(sender == null)
            sender = Bukkit.getConsoleSender();
        
        boolean removeColors = (!RecipeManager.getSettings().COLOR_CONSOLE && sender instanceof ConsoleCommandSender);
        
        for(String message : messages)
        {
            message = Tools.parseColors(message, removeColors);
        }
        
        sender.sendMessage(messages);
    }
    
    /**
     * Sends a message to a player or console.<br>
     * Message supports &lt;color&gt; codes.
     * 
     * @param sender
     * @param message
     */
    public static void send(CommandSender sender, String message)
    {
        if(sender == null)
        {
            sender = Bukkit.getConsoleSender();
        }
        
        if(sender instanceof ConsoleCommandSender)
        {
            message = "[RecipeManager] " + message;
        }
        
        sender.sendMessage(Tools.parseColors(message, (sender instanceof ConsoleCommandSender && !RecipeManager.getSettings().COLOR_CONSOLE)));
    }
    
    public static void sendDenySound(Player player, Location location)
    {
        if(player != null && RecipeManager.getSettings().SOUNDS_FAILED_CLICK)
        {
            player.playSound(location, Sound.NOTE_BASS, 0.8f, 255);
        }
    }
    
    public static void sendFailSound(Player player, Location location)
    {
        if(RecipeManager.getSettings().SOUNDS_FAILED && player != null)
        {
            player.playSound(location, Sound.NOTE_PLING, 0.8f, 10);
        }
    }
    
    /**
     * Used by plugin to log messages, shouldn't be used by other plugins unless really needed to send e message tagged by RecipeManager
     * 
     * @param message
     */
    public static void info(String message)
    {
        send(null, message);
    }
    
    public static void log(String message)
    {
        Bukkit.getLogger().fine(Tools.parseColors("[RecipeManager] " + message, true));
    }
    
    public static void error(CommandSender sender, Exception exception, String message)
    {
        message = "<red>" + (message == null ? exception.getMessage() : message + " (" + exception.getMessage() + ")");
        
        if(sender != null)
        {
            info(message);
        }
        
        send(sender, message);
        notifyDebuggers(message);
        
        exception.printStackTrace();
        
        message = ChatColor.LIGHT_PURPLE + "If you're using the latest version you should report this error at: http://dev.bukkit.org/server-mods/recipemanager/create-ticket/";
        info(message);
        notifyDebuggers(message);
    }
    
    /**
     * Notifies all online operators and people having "recipemanager.debugger" permission
     * 
     * @param message
     */
    protected static void notifyDebuggers(String message)
    {
        message = ChatColor.DARK_RED + "(RecipeManager debug) " + ChatColor.RESET + message;
        
        for(Player p : Bukkit.getOnlinePlayers())
        {
            if(p.hasPermission("recipemanager.debugger"))
            {
                send(p, message);
            }
        }
    }
    
    public static void debug(String message)
    {
        // TODO print stack trace
        // TODO debug switch
        StackTraceElement[] e = new Exception().getStackTrace();
        
        Bukkit.getConsoleSender().sendMessage(Tools.parseColors(ChatColor.AQUA + "" + ChatColor.UNDERLINE + e[1].getFileName() + ":" + e[1].getLineNumber() + ChatColor.RESET + " " + ChatColor.RED + e[1].getMethodName() + "() " + ChatColor.WHITE + Tools.parseColors(message, false), false));
    }
}