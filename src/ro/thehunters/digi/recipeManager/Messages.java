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
    /*
    GENERAL_RECIPE("general.recipe", "recipe"),
    GENERAL_RECIPES("general.recipes", "recipes"),
    GENERAL_WORKBENCH("general.workbench", "workbench"),
    GENERAL_FURNACE("general.furnace", "furnace"),
    GENERAL_FUEL("general.fuel", "fuel"),
    GENERAL_BOX("general.box", "box"),
    GENERAL_SPHERE("general.sphere", "sphere"),
    
    CRAFT_CANTCRAFTANTHING("craft.cantcraftanything", "<dark_red>You don't have permission to use <red>ANY recipes<dark_red>!"),
    CRAFT_NOREPAIR("craft.norepair", "<dark_red>The repairing recipes are disabled!"),
    CRAFT_FAILURE("craft.failure", "<dark_red>Recipe failed! <gray>(<white>{chance} chance<gray>)"),
    CRAFT_DROPPED("craft.dropped", "<dark_green>Some items couldn't fit in your inventory, they were dropped on the floor!"),
    CRAFT_NOSMELT("craft.nosmelt", "<dark_red>Furnace at <gold>{location}<dark_red> lacks requirements to smelt recipe!"),
    CRAFT_NOFUEL("craft.nofuel", "<dark_red>Furnace at <gold>{location}<dark_red> lacks requirements for fuel!"),
    
    CRAFT_NOPERMISSION("craft.nopermission", "<dark_red>You don't have the required permission for this recipe!"),
    CRAFT_NOGROUP("craft.nogroup", "<dark_red>You're not in the required group to use this recipe!"),
    CRAFT_NOWORLD("craft.noworld", "<dark_red>You can't use this recipe in this world!"),
    CRAFT_NOHEIGHT("craft.noheight", "<dark_red>Recipe needs the {toolblock} to be between depth of {min} and {max} !"),
    
    CRAFT_NOPROXIMITY("craft.noproximity", "<dark_red>Furnace at <gold>{location}<dark_red> uses a recipe that requires you to be at most {distance} blocks away!"),
    CRAFT_WARNDISTANCE("craft.warndistance", "<dark_green>Recipe will only work as long as you're within {distance} blocks from the furnace!"),
    CRAFT_WARNONLINE("craft.warnonline", "<dark_green>Recipe will only work as long as you're online in the server!"),
    
    CRAFT_EXPLODE("craft.explode", "<dark_green>Recipe exploded! <gray>(<white>{chance} chance of explosion<gray>)"),
    CRAFT_EXPLODEONSUCCESS("craft.explodeonsuccess", "<dark_green>Recipe exploded! <gray>(<white>{chance} chance of explosion when recipe works<gray>)"),
    CRAFT_EXPLODEONFAILURE("craft.explodeonfailure", "<dark_red>Recipe exploded! <gray>(<white>{chance} chance of explosion when recipe fails<gray>)"),
    
    CRAFT_GIVEEXP("craft.giveexp", "<green>Got {amount} experience<dark_green> for crafting recipe."),
    CRAFT_TAKEEXP("craft.takeexp", "<gold>Lost {amount} experience<dark_red> for crafting recipe."),
    CRAFT_COSTEXP("craft.costexp", "<dark_red>Recipe <gold>costs {amount} experience<dark_red> to craft it!"),
    CRAFT_MINEXP("craft.minexp", "<dark_red>Recipe usable if you have <gold>at least {amount} experience<dark_red>!"),
    CRAFT_MAXEXP("craft.maxexp", "<dark_red>Recipe usable if you have <gold>less than {amount} experience<dark_red>!"),
    
    CRAFT_GIVELEVEL("craft.givelevel", "<green>Got {amount} level(s)<dark_green> for crafting recipe."),
    CRAFT_TAKELEVEL("craft.takelevel", "<gold>Lost {amount} level(s)<dark_red> for crafting recipe."),
    CRAFT_COSTLEVEL("craft.costlevel", "<dark_red>Recipe <gold>costs {amount} level(s)<dark_red> to craft it!"),
    CRAFT_MINLEVEL("craft.minlevel", "<dark_red>Recipe usable if you be <gold>at least level {amount}<dark_red>!"),
    CRAFT_MAXLEVEL("craft.maxlevel", "<dark_red>Recipe usable if you be <gold>less than level {amount}<dark_red>!"),
    
    CRAFT_GIVEMONEY("craft.givemoney", "<green>Got {money}<dark_green> for crafting recipe."),
    CRAFT_TAKEMONEY("craft.takemoney", "<gold>Lost {money}<dark_red> for crafting recipe."),
    CRAFT_COSTMONEY("craft.costmoney", "<dark_red>Recipe <gold>costs {money}<dark_red> to craft it!"),
    CRAFT_MINMONEY("craft.minmoney", "<dark_red>Recipe usable if you have <gold>at least {money}<dark_red>!"),
    CRAFT_MAXMONEY("craft.maxmoney", "<dark_red>Recipe usable if you have <gold>less than {money}<dark_red>!"),
    
    CRAFT_NEEDITEMS_REQ("craft.needitems.req", "<dark_red>Recipe needs you to have {items} in your inventory !"),
    CRAFT_NEEDITEMS_HAND("craft.needitems.hand", "<dark_red>Recipe needs you to have {items} in your hand !"),
    CRAFT_NEEDITEMS_ANY("craft.needitems.any", "<dark_red>Recipe needs you to have any of {items} in your inventory !"),
    
    CRAFT_NOBLOCKSNEARBY_ANY("craft.noblocksnearby.any", "<dark_red>Recipe needs any of {blocks} in a {radius}m {shape} radius near the {toolblock} !"),
    CRAFT_NOBLOCKSNEARBY_REQ("craft.noblocksnearby.req", "<dark_red>Recipe needs {blocks} in a {radius}m {shape} radius near the {toolblock} !"),
    CRAFT_NOBLOCKSTOP_ANY("craft.noblockstop.any", "<dark_red>Recipe needs any of {blocks} to be on top of the {toolblock} !"),
    CRAFT_NOBLOCKSTOP_STACK("craft.noblockstop.stack", "<dark_red>Recipe needs {blocks} to be stacked on top of the {toolblock} !"),
    CRAFT_NOBLOCKSUNDER_ANY("craft.noblocksunder.any", "<dark_red>Recipe needs any of {blocks} to be underneath the {toolblock} !"),
    CRAFT_NOBLOCKSUNDER_STACK("craft.noblocksunder.stack", "<dark_red>Recipe needs {blocks} to be stacked underneath the {toolblock} !"),
    
    NOSHIFTCLICK_MULTIPLERESULTS("noshiftclick.multipleresults", "<dark_red>Can't Shift+Click recipes with multiple results <underline>for now<reset><dark_red>, sorry."),
    NOSHIFTCLICK_REWARDS("noshiftclick.rewards", "<dark_red>Can't Shift+Click recipes that give rewards <underline>for now<reset><dark_red>, sorry."),
    NOSHIFTCLICK_FURNACEINVENTORY("noshiftclick.furnaceinventory", "<dark_red>Can't Shift+Click in furnace interfaces <underline>for now<reset><dark_red>, sorry."),
    
    RECIPE_CRAFT_HEADER("recipe.craft.header", "<yellow><underline>Workbench shaped recipe:"),
    RECIPE_CRAFT_INGREDIENT("recipe.craft.ingredient", "  <blue>{char} <gray>= <white>{ingredient}"),
    RECIPE_COMBINE_HEADER("recipe.combine.header", "<yellow><underline>Workbench shapeless recipe:"),
    RECIPE_RESULT("recipe.result", " <green>=> <white>{result}"),
    RECIPE_SMELT_HEADER("recipe.smelt.header", "<yellow><underline>Furnace smelting recipe:"),
    RECIPE_SMELT_FORMAT("recipe.smelt.format", "<blue>{ingredient} <green>=> <white>{result} <gray>({time}<gray>)"),
    RECIPE_SMELT_TIME_NORMAL("recipe.smelt.time.normal", "<yellow>normal <white>smelting time"),
    RECIPE_SMELT_TIME_INSTANT("recipe.smelt.time.instant", "<green>instant <white>smelting"),
    RECIPE_SMELT_TIME_SECONDS("recipe.smelt.time.seconds", "<red>{seconds} <white>seconds"),
    RECIPE_FUEL_HEADER("recipe.fuel.header", "<yellow><underline>Furnace fuel recipe:"),
    RECIPE_FUEL_FORMAT("recipe.fuel.format", "<blue>{ingredient} <gray>(<white>burns for <red>{seconds} <white>seconds<gray>)"),
    
    BOOK_PAGE_HEADER("book.page.header", "<black><bold>{title}"),
    BOOK_PAGE_INFO("book.page.info", "<dark_green>{info}"),
    BOOK_PAGE_INGREDIENTS("book.page.ingredients", "<dark_gray><bold>Ingredients:"),
    BOOK_PAGE_SMELTTIME("book.page.smelttime", "<dark_gray><bold>Smelt Time:"),
    BOOK_PAGE_BURNTIME("book.page.burntime", "<dark_gray><bold>Burn Time:"),
    BOOK_PAGE_RESULTS("book.page.results", "<dark_gray><bold>Results:"),
    
    COMMAND_RMRECIPES_USAGE("command.rmrecipes.usage", "<white>Type <green>{command} <blue>item <gray>[i] <white>to search for recipes. Use this as item to search for held item. Specify i to search in ingredients instead of results."),
    COMMAND_RMRECIPES_WORKBENCHRECIPES("command.rmrecipes.workbenchrecipes", "<white>Workbench has <gold>{craftrecipes} shaped <white> and <gold>{combinerecipes} shapeless<white> recipes."),
    COMMAND_RMRECIPES_FURNACERECIPES("command.rmrecipes.furnacerecipes", "<white>Furnace has <gold>{smeltrecipes} recipes<white> and <gold>{fuels} fuels<white>."),
    COMMAND_RMRECIPES_INVALIDITEM("command.rmrecipes.invaliditem", "<red>Invalid item: <gray>{item}<red>!"),
    COMMAND_RMRECIPES_INVALIDHELDITEM("command.rmrecipes.invalidhelditem", "<red>You must hold an item to use this command like this."),
    COMMAND_RMRECIPES_NOINGREDIENT("command.rmrecipes.noingredient", "<yellow>No recipes that have <blue>{item}<yellow> as ingredient."),
    COMMAND_RMRECIPES_NORESULT("command.rmrecipes.noresult", "<yellow>No recipes that make <blue>{item}<yellow>."),
    COMMAND_RMRECIPES_LISTINGREDIENT("command.rmrecipes.listingredient", "<light_purple>The <blue>{item}<light_purple> item can be used as an ingredient in <white>{recipes}<light_purple>:"),
    COMMAND_RMRECIPES_LISTRESULT("command.rmrecipes.listresult", "<light_purple>The <blue>{item}<light_purple> item can be created from <white>{recipes}<light_purple>:"),
    COMMAND_RMRECIPES_PAGEOFPAGES("command.rmrecipes.pageofpages", "<gray>(Page <white>{page}<gray> of <white>{pages}<gray>)"),
    COMMAND_RMRECIPES_NEXTAVAILABLE("command.rmrecipes.nextavailable", "<yellow>End of page <white>{page}<yellow> of <white>{pages}<yellow>. Type <green>{command}<yellow> for next page."),
    COMMAND_RMRECIPES_PREVAVAILABLE("command.rmrecipes.prevavailable", "<yellow>End of page <white>{page}<yellow> of <white>{pages}<yellow>. Type <green>{command}<yellow> for previous page."),
    
    COMMAND_RMFINDITEM_USAGE("command.rmfinditem.usage", "<white>Type <green>{command} <blue>item <white>to search for items by IDs or partial names. Use this as item to search for held item."),
    COMMAND_RMFINDITEM_INVALIDHELDITEM("command.rmfinditem.invaliditem", "<red>You must hold an item to use this command like this."),
    COMMAND_RMFINDITEM_HEADER("command.rmfinditem.header", "Found {matches} materials matching '{argument}':"),
    COMMAND_RMFINDITEM_LIST("command.rmfinditem.list", "  <gray>[<red>{id}<gray>] <green>{material} <gray>(<white>max data: {maxdata}, max stack: {maxstack}<gray>)"),
    COMMAND_RMFINDITEM_NOTFOUND("command.rmfinditem.notfound", "<red>Couldn't find any item matching '{argument}'."),
    
    COMMAND_RMCHECK_CHECKING("command.rmcheck.checking", "<white>Checking all files inside the '{folder}' folder..."),
    COMMAND_RMCHECK_VALID("command.rmcheck.valid", "<green>All recipes are valid, no errors reported."),
    COMMAND_RMCHECK_ERRORS("command.rmcheck.errors", "<red>There were errors processing the files, check server log!"),
    
    COMMAND_RMRELOAD_RELOADING("command.rmreload.reloading", "<white>Reloading all settings, recipes and language file..."),
    COMMAND_RMRELOAD_DONE("command.rmreload.done", "<green>Everything reloaded succesfully, now there are {recipes} recipes."),
    COMMAND_RMRELOAD_ERRORS("command.rmreload.errors", "<red>There were errors processing the files, check server log!"),
    
    COMMAND_RMGETBOOK_USAGE("command.rmgetbook.usage", "<white>Type <green>{command} <blue>title<white> |<blue> author <white>to get the recipe book with that title and author."),
    COMMAND_RMGETBOOK_NOTFOUND("command.rmgetbook.notfound", "<red>Couldn't find any recipe book titled '{title}' by '{author}'"),
    COMMAND_RMGETBOOK_GOT("command.rmgetbook.got", "Got the recipe book titled '{title}' by '{author}'."),
    */
    
    // ---------- ^ old ones
    
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
    
    FLAG_REQEXP("<red>Need EXP: {exp}"),
    
    FLAG_MODEXP_ADD("<green>+{exp} EXP"),
    FLAG_MODEXP_SUB("<red>-{exp} EXP"),
    FLAG_MODEXP_SET("<yellow>EXP set to {exp}"),
    
    FLAG_REQLEVEL("<red>Need level: {level}"),
    
    FLAG_MODLEVEL("{color}{level} levels"),
    
    FLAG_REQMONEY("<red>Need money: {amount}"),
    FLAG_MODMONEY("{color}{amount}"),
    
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
    
    LASTCHANGED(Files.LASTCHANGED_MESSAGES);
    
    private static final Map<String, Set<String>> sent = new HashMap<String, Set<String>>();
    private static FileConfiguration              yml;
    
    private String                                path;
    private String                                message;
    
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
            
            // TODO re-enable
            /*
            try
            {
                messages.save(file);
            }
            catch(Exception e)
            {
                error(sender, e, "Couldn't save 'messages.yml' !");
            }
            
            send(sender, ChatColor.GREEN + "Generated 'messages.yml' file.");
            */
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
            if(LASTCHANGED == null || !LASTCHANGED.equals(Files.LASTCHANGED_MESSAGES))
                send(sender, "<yellow>messages.yml has changed! You should delete it, use 'rmreload' to re-generate it and then re-configure it, and then rmreload again.");
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
    
    /**
     * Gets the message for the selected enum.<br>
     * Processes colors and variables as well.
     * 
     * @return
     */
    public String get(String... variables)
    {
        return Tools.replaceVariables(Tools.parseColors(message, false), variables);
    }
    
    public String getCustom(String customMessage, String... variables)
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
    public void print(CommandSender sender, String recipeMessage, String... variables)
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
            print(sender);
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
            sender = Bukkit.getConsoleSender();
        
        if(sender instanceof ConsoleCommandSender)
            message = ChatColor.GRAY + "[RecipeManager] " + ChatColor.RESET + message;
        
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
            info(message);
        
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