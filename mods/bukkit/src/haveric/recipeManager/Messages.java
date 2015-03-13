package haveric.recipeManager;

import haveric.recipeManager.tools.Tools;

import java.io.File;
import java.util.Collection;
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

public enum Messages {
    // Flags

    FLAG_OVERRIDE("<yellow>Overwrites another recipe."),

    FLAG_RESTRICT("<red>Recipe is disabled."),

    FLAG_HOLDITEM("<red>Need to hold: <yellow>{items}"),

    FLAG_GAMEMODE("<red>Allowed gamemodes: <yellow>{gamemodes}"),

    FLAG_HEIGHT("<red>Need height: <yellow>{height}"),

    FLAG_ONLINETIME("<red>Need online time: <yellow>{range}"),

    FLAG_PERMISSION_ALLOWED("<red>Allowed permissions: <yellow>{permissions}"),
    FLAG_PERMISSION_UNALLOWED("<red>Unallowed permissions: <yellow>{permissions}"),

    FLAG_GROUP_ALLOWED("<red>Allowed groups: <yellow>{groups}"),
    FLAG_GROUP_UNALLOWED("<red>Unallowed groups: <yellow>{groups}"),

    FLAG_WORLD_ALLOWED("<red>Allowed worlds: <yellow>{worlds}"),
    FLAG_WORLD_UNALLOWED("<red>Unallowed worlds: <yellow>{worlds}"),

    FLAG_WEATHER("<red>Needs weather: <yellow>{weather}"),
    FLAG_TEMPERATURE("<red>Needs temperature: <yellow>{temperature}<reset>. Current Temperature: <red>{actual}"),

    FLAG_BIOME_ALLOWED("<red>Allowed biomes: <yellow>{biomes}"),
    FLAG_BIOME_UNALLOWED("<red>Unallowed biomes: <yellow>{biomes}"),

    FLAG_RETURNITEM_RETURNED("<green>Returned item {item}<green> in crafting grid."),
    FLAG_RETURNITEM_MERGED("<green>Returned item {item}<green> merged in crafting grid."),
    FLAG_RETURNITEM_RECEIVED("<yellow>Returned item {item}<green> added to inventory."),
    FLAG_RETURNITEM_DROPPED("<yellow>Returned item {item}<green> dropped."),

    FLAG_BLOCKPOWERED_FURNACE("<red>Need a furnace powered by redstone."),
    FLAG_BLOCKPOWERED_WORKBENCH("<red>Need a workbench powered by redstone."),

    FLAG_LIGHTLEVEL("<red>Need to be in <yellow>{light}<red> levels of <yellow>{type}<red>."),

    FLAG_SETBLOCK_NEEDSWORKBENCH("<red>Recipe needs to be used with a workbench!"),

    FLAG_ITEMS("<red>Need in inventory: <yellow>{items}"),
    FLAG_NOITEMS("<red>Unallowed in inventory: <yellow>{items}"),
    FLAG_EQUIP("<red>Need equipped: <yellow>{items}"),
    FLAG_NOEQUIP("<red>Unallowed equipped: <yellow>{items}"),
    FLAG_HOLD("<red>Need in hand: <yellow>{items}"),
    FLAG_NOHOLD("<red>Unallowed in hand: <yellow>{items}"),

    FLAG_COMMAND_PLAYER("Executes command on crafter: <yellow>{command}"),
    FLAG_COMMAND_SERVER("Executes console command: <yellow>{command}"),

    FLAG_INGREDIENTCONDITIONS_NODATA("<yellow>{item}<red> needs data values: <yellow>{data}"),
    FLAG_INGREDIENTCONDITIONS_NOAMOUNT("<yellow>{item}<red> needs amount: <yellow>{amount}"),
    FLAG_INGREDIENTCONDITIONS_NOENCHANTS("<yellow>{item}<red> needs enchantments: <yellow>{enchants}"),
    FLAG_INGREDIENTCONDITIONS_NONAME("<yellow>{item}<red> needs name: <yellow>{name}"),
    FLAG_INGREDIENTCONDITIONS_NOLORE("<yellow>{item}<red> needs lore: <yellow>{lore}"),
    FLAG_INGREDIENTCONDITIONS_NOCOLOR("<yellow>{item}<red> needs color: <yellow>{color}"),

    FLAG_NEEDEXP("<red>Need EXP: <yellow>{exp}<reset>. Current EXP:<yellow> {playerexp}"),

    FLAG_MODEXP_ADD("<green>+{amount}<reset> EXP"),
    FLAG_MODEXP_SUB("<yellow>-{amount}<reset> EXP"),
    FLAG_MODEXP_SET("<reset>EXP set to <yellow>{amount}"),

    FLAG_NEEDLEVEL("<red>Need level: <yellow>{level}"),

    FLAG_MODLEVEL_ADD("<green>+{amount}<reset> level(s)"),
    FLAG_MODLEVEL_SUB("<red>-{amount}<reset> level(s)"),
    FLAG_MODLEVEL_SET("<reset>Level set to <yellow>{amount}"),

    FLAG_NEEDMONEY("<red>Need money: <yellow>{money}"),

    FLAG_MODMONEY_ADD("<green>+{money}"),
    FLAG_MODMONEY_SUB("<red>-{money}"),
    FLAG_MODMONEY_SET("<reset>Money set to <yellow>{money}"),

    FLAG_PLAYERBUKKITMETA("<red>You need to be special..."),
    FLAG_NOPLAYERBUKKITMETA("<red>You're too special..."),

    FLAG_BLOCKBUKKITMETA("<red>Needs special block..."),
    FLAG_NOBLOCKBUKKITMETA("<red>Block to special..."),

    FLAG_POTIONEFFECTS("<red>Need potion effect: {effects}"),
    FLAG_NOPOTIONEFFECTS("<red>Unallowed potion effect: {effects}"),

    FLAG_REALTIME("<red>Allowed between {mindate} and {maxdate}"),

    FLAG_COOLDOWN_FAIL_PERPLAYER("<red>Personal cooldown: {time}"),
    FLAG_COOLDOWN_FAIL_GLOBAL("<red>Global cooldown: {time}"),
    FLAG_COOLDOWN_SET_PERPLAYER("<yellow>Personal cooldown set to {time}"),
    FLAG_COOLDOWN_SET_GLOBAL("<yellow>Global cooldown set to {time}"),

    FLAG_CLONE_RESULTDISPLAY("<dark_aqua><italic>(clone)"),

    FLAG_PREFIX_RECIPE("<gray>(Recipe) <reset>"),
    FLAG_PREFIX_RESULT("<gray>(Result {item}<gray>) <reset>"),
    FLAG_PREFIX_FURNACE("<gray>(Furnace {location}) <reset>"),

    // Crafting

    CRAFT_REPAIR_DISABLED("<red>Repair recipes disabled."),

    CRAFT_SPECIAL_LEATHERDYE("<red>Leather dyeing is disabled."),
    CRAFT_SPECIAL_FIREWORKS("<red>Firework crafting is disabled."),
    CRAFT_SPECIAL_MAP_CLONING("<red>Map cloning is disabled."),
    CRAFT_SPECIAL_MAP_EXTENDING("<red>Map extending is disabled."),
    CRAFT_SPECIAL_BOOK_CLONING("<red>Book cloning is disabled."),
    CRAFT_SPECIAL_BANNER("<red>Banner crafting is disabled."),

    CRAFT_RESULT_DENIED_TITLE("<yellow><underline>You can't craft this recipe!"),
    CRAFT_RESULT_DENIED_INFO("<green>See chat for reasons."),
    CRAFT_RESULT_NORECEIVE_TITLE("<yellow><underline>You can't craft any results from this recipe!"),
    CRAFT_RESULT_NORECEIVE_INFO("<green>See chat for reasons."),
    CRAFT_RESULT_RECEIVE_TITLE_UNKNOWN("<light_purple><underline>You will get an unknown item!"),
    CRAFT_RESULT_RECEIVE_TITLE_RANDOM("<light_purple><underline>You will get a random item:"),
    CRAFT_RESULT_LIST_ITEM("<dark_green>{chance} <green>{item} {clone}"),
    CRAFT_RESULT_LIST_SECRETS("<dark_aqua>{num} secret item(s)"),
    CRAFT_RESULT_LIST_FAILURE("<red>{chance} Failure chance"),
    CRAFT_RESULT_LIST_UNAVAILABLE("<dark_red>{num} unavailable item(s)"),

    CRAFT_RECIPE_MULTI_FAILED("<yellow>NOTE: <white>That sound was the recipe failing by chance! See 'fail chance' in the result description."),
    CRAFT_RECIPE_MULTI_NOSHIFTCLICK("<yellow>NOTE: <white>Recipe has more than one result, shift+clicking will only craft it once."),
    CRAFT_RECIPE_MULTI_CURSORFULL("<yellow>NOTE: <white>Cursor is full or not same type as result, put the held item in inventory or use Shift+Click to craft one by one to inventory."),

    CRAFT_RECIPE_FLAG_NOSHIFTCLICK("<yellow>NOTE: <white>Recipe is special, shift-clicking will only craft it once."),

    SMELT_FUEL_NEEDINGREDIENT("<red>Fuel {fuel}<red> needs specific ingredient: {ingredient}"),
    SMELT_FUEL_NEEDFUEL("<red>Ingredient {ingredient}<red> needs specific fuel: {fuel}"),

    SMELT_FROZEN("<red>Furnace at <yellow>{location} <red>will be frozen until you re-place the ingredient."),

    ITEM_ANYDATA("<gray>any"),

    RECIPEBOOK_VOLUME("Volume {volume}"),
    RECIPEBOOK_VOLUMEOFVOLUMES("Volume {volume} of {volumes}"),
    RECIPEBOOK_HEADER_CONTENTS("<black><bold><underline>CONTENTS INDEX"),
    RECIPEBOOK_HEADER_SHAPED("<black><bold>SHAPED RECIPE"),
    RECIPEBOOK_HEADER_SHAPELESS("<black><bold>SHAPELESS RECIPE"),
    RECIPEBOOK_HEADER_SMELT("<black><bold>FURNACE RECIPE"),
    RECIPEBOOK_HEADER_FUEL("<black><bold>FURNACE FUEL"),
    RECIPEBOOK_HEADER_SHAPE("<black><underline>Shape"),
    RECIPEBOOK_HEADER_INGREDIENTS("<black><underline>Ingredients"),
    RECIPEBOOK_HEADER_INGREDIENT("<black><underline>Ingredient"),
    RECIPEBOOK_HEADER_COOKTIME("<black><underline>Cooking time"),
    RECIPEBOOK_HEADER_BURNTIME("<black><underline>Burning time"),
    RECIPEBOOK_HEADER_REQUIREFUEL("<black><underline>As fuel"),
    RECIPEBOOK_MORERESULTS("<dark_green>+{amount} more results"),
    RECIPEBOOK_SMELT_TIME_NORMAL("<black>Normal <gray>(<dark_red>{time} <gray>seconds)"),
    RECIPEBOOK_SMELT_TIME_INSTANT("<dark_green>Instant <gray>(0 seconds)"),
    RECIPEBOOK_SMELT_TIME_FIXED("<red>{time} <black>seconds"),
    RECIPEBOOK_SMELT_TIME_RANDOM("<red>{min} <black>to <red>{max} <black>seconds"),
    RECIPEBOOK_FUEL_TIME_FIXED("<dark_green>{time} <black>seconds"),
    RECIPEBOOK_FUEL_TIME_RANDOM("<dark_green>{min} <black>to <dark_green>{max} <black>seconds"),
    RECIPEBOOK_UPDATE_EXTINCT("<red>Your '<yellow>{title}<red>' recipe book does not exist any more, it won't be updated further."),
    RECIPEBOOK_UPDATE_NOVOLUME("<red>Your '<yellow>{title}<red>' recipe book does not have <yellow>volume {volume}<red> any more, it won't be updated further."),
    RECIPEBOOK_UPDATE_DONE("<gray>Your held recipe book has been updated!"),
    RECIPEBOOK_UPDATE_CHANGED_TITLE("<gray>Title changed from '<reset>{oldtitle}<gray>' to '<reset>{newtitle}<gray>'."),
    RECIPEBOOK_UPDATE_CHANGED_PAGES("<gray>Pages change from <red>{oldpages}<gray> pages to <green>{newpages}<gray> pages."),

    CMD_GETBOOK_INVALIDNUMBER("<red>Volume argument must be a number!"),
    CMD_GETBOOK_NOTEXIST("<red>No books found by '{arg}'."),
    CMD_GETBOOK_MANYMATCHES("<red>Found {num} books by '{arg}':"),
    CMD_GETBOOK_GIVEN("<green>Got book: {title}"),

    CMD_BOOKS_NOBOOKS("<red>No generated books."),
    CMD_BOOKS_HEADER("<yellow>Generated recipe books ({number}):"),
    CMD_BOOKS_ITEM("<white>{title} <gray>(volumes: {volumes})"),

    CMD_EXTRACT_WAIT("<red>Command re-used too fast, wait a second."),
    CMD_EXTRACT_UNKNOWNARG("<red>Unknown argument: <yellow>{arg}"),
    CMD_EXTRACT_CONVERTING("<gray>Searching and converting recipes..."),
    CMD_EXTRACT_NORECIPES("<yellow>No recipes to extract."),
    CMD_EXTRACT_DONE("<green>Done! Recipes saved to '<white>{file}<green>'."),

    CMD_EXTRACTRECIPE_DONE("<green>Done! Recipe saved to '<white>{file}<green>'."),

    CMD_RECIPES_USAGE("<yellow>Usage: <gray>/{command} <white><material>:[data]:[amount]"),
    CMD_RECIPES_STATS_MC("Minecraft: <green>{num}"), CMD_RECIPES_STATS_RM("RecipeManager: <green>{num}"),
    CMD_RECIPES_STATS_OTHER("Other plugins/mods: <green>{num}"),
    CMD_RECIPES_HEADER("<yellow>----- <white>Recipes for <green>{item} <white>({num} of {total})<yellow>-----"),
    CMD_RECIPES_MORE("<yellow>----- <white><blue>{cmdnext} <white>for next, <blue>{cmdprev} <white>for previous <yellow>-----"),
    CMD_RECIPES_END("<yellow>----- <white>No more recipes <yellow>-----"),
    CMD_RECIPES_NONEXT("<red>No more recipes next, type <blue>{command} <red>to see the previous recipe."),
    CMD_RECIPES_NOPREV("<red>Can't go backwards more than this, type <blue>{command} <red>to see the next recipe."),
    CMD_RECIPES_NEEDQUERY("<red>No search progress! Use the command with an item name to search."),
    CMD_RECIPES_NORESULTS("<red>No results for <yellow>{item}"),
    CMD_RECIPES_NOHAND("<red>You don't have anything in your hand therefore you can't use 'this' argument."),
    CMD_RECIPES_INVALIDITEM("<red>Invalid item: <yellow>{arg}"),

    CMD_FINDITEM_USAGE("<yellow>Usage: <gray>/{command} <white><item partial name>"),
    CMD_FINDITEM_INVALIDHELDITEM("<yellow>You need to hold an item to use the '<white>this<yellow>' argument."),
    CMD_FINDITEM_HEADER("Found <green>{matches}<white> materials matching '<green>{argument}<white>':"),
    CMD_FINDITEM_LIST("<gray>#<red>{id} <green>{material}<gray>, max durability <yellow>{maxdata}<gray>, max stack <yellow>{maxstack}"),
    CMD_FINDITEM_FOUNDMORE("<yellow>... and <green>{matches}<yellow> more, be more specific in your search."),
    CMD_FINDITEM_NOTFOUND("<yellow>No material found by '<white>{argument}<yellow>'."),

    LASTCHANGED(null);

    private static Map<String, Set<String>> sent = new HashMap<String, Set<String>>();
    private static FileConfiguration yml;

    private String path;
    private String message;

    private Messages(String newMessage) {
        path = name().replace('_', '.').toLowerCase();
        message = newMessage;
    }

    private void assign() {
        message = yml.getString(path, message); // get the message or use the predefined one if doesn't exist

        if (message != null && (message.isEmpty() || message.equals("false"))) {
            message = null; // disable message if empty or 'false'
        }
    }

    /**
     * (Re)Loads all messages
     *
     * @param force
     */
    public static void reload(CommandSender sender) {
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + Files.FILE_MESSAGES);

        if (!file.exists()) {
            yml = new YamlConfiguration();
            yml.options().header("Configurable messages.\nParts surrounded by { and } are variables that get replaced in-game, you can move them around or even remove them if you want.\nTo disable messages you can just delete the message or use 'false'.");
            yml.options().copyHeader(true);

            for (Messages msg : values()) {
                yml.set(msg.path, msg.message);
            }

            yml.set("lastchanged", Files.LASTCHANGED_MESSAGES);

            try {
                yml.save(file);
            } catch (Throwable e) {
                error(sender, e, "Couldn't save '" + Files.FILE_MESSAGES + "' file!");
            }

            sendAndLog(sender, ChatColor.GREEN + "Generated '" + Files.FILE_MESSAGES + "' file.");
        } else {
            yml = YamlConfiguration.loadConfiguration(file);
        }

        for (Messages msg : values()) {
            msg.assign();
        }

        if (LASTCHANGED == null || LASTCHANGED.message == null || !LASTCHANGED.message.equals(Files.LASTCHANGED_MESSAGES)) {
            sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_MESSAGES + "' file is outdated, please delete it to allow it to be generated again.");
        }
    }

    /**
     * Gets the message for the selected enum.<br>
     * Processes colors as well.
     *
     * @return
     */
    public String get() {
        return Tools.parseColors(message, false);
    }

    @Override
    public String toString() {
        return get();
    }

    /**
     * Gets the message for the selected enum.<br>
     * Processes colors and variables as well.
     *
     * @return
     */
    public String get(Object... variables) {
        return Tools.replaceVariables(Tools.parseColors(message, false), variables);
    }

    public String getCustom(String customMessage, Object... variables) {
        String msg = get();

        if (customMessage != null) { // has custom message
            // if flag message is set to "false" then don't show the message
            if (customMessage.equals("false")) {
                msg = null;
            } else {
                msg = customMessage;
            }
        } else if (msg != null && msg.equals("false")) {
            // message is "false", don't show the message
            msg = null;
        }

        String finalCustom;
        if (msg == null) {
            finalCustom = null;
        } else {
            finalCustom = Tools.replaceVariables(msg, variables);
        }

        return finalCustom;
    }

    /**
     * Send the selected enum message to a player or console. <br>
     * Will not be displayed if the message is set to "false".
     *
     * @param sender
     *            player or console
     */
    public void print(CommandSender sender) {
        if (sender != null && message != null) {
            send(sender, message);
        }
    }

    /**
     * Send the selected enum message to a player or console with an overwritable message.<br>
     * The customMessage has priority if it's not null.<br>
     * If the priority message is "false" it will not be displayed.
     *
     * @param sender
     *            player or console
     * @param customMessage
     *            overwrite message, ignored if null, don't display if "false"
     */
    public void print(CommandSender sender, String customMessage) {
        if (sender != null) {
            if (customMessage != null) { // has custom message ?
                if (!customMessage.equals("false")) { // if it's not "false" send it, otherwise don't.
                    send(sender, customMessage);
                }
            } else if (message != null) { // message not set to "false" (replaced with null to save memory)
                send(sender, message);
            }
        }
    }

    /**
     * Send the selected enum message to a player or console with an overwritable message.<br>
     * The customMessage has priority if it's not null.<br>
     * If the priority message is "false" it will not be displayed.<br>
     * Additionally you can specify variables to replace in the message.<br>
     * The variable param must be a 2D String array that has pairs of 2 strings, variable and replacement value.
     *
     * @param sender
     *            player or console
     * @param customMessage
     *            overwrite message, ignored if null, don't display if "false"
     * @param variables
     *            the variables array
     */
    public void print(CommandSender sender, String customMessage, Object... variables) {
        if (sender != null) {
            String msg = message;

            if (customMessage != null) { // has custom message
                if (customMessage.equals("false")) { // if custom message is set to "false" then don't show the message
                    return;
                }

                msg = customMessage;
            } else if (msg == null) { // message is "false", don't show the message
                return;
            }

            msg = Tools.replaceVariables(msg, variables);

            send(sender, msg);
        }
    }

    /**
     * Send this message only once per connection.
     *
     * @param sender
     */
    public void printOnce(CommandSender sender) {
        printOnce(sender, null);
    }

    /**
     * Send this message only once per connection.
     *
     * @param sender
     * @param customMessage
     * @param variables
     */
    public void printOnce(CommandSender sender, String customMessage, Object... variables) {
        if (sender != null) {
            Set<String> set = sent.get(sender.getName());

            if (set == null) {
                set = new HashSet<String>();
                sent.put(sender.getName(), set);
            }

            if (!set.contains(path)) {
                set.add(path);
                print(sender, customMessage, variables);
            }
        }
    }

    protected static void clearPlayer(String name) {
        sent.remove(name);
    }

    /**
     * Sends an array of messages to a player or console.<br>
     * Message supports &lt;color&gt; codes.
     *
     * @param sender
     * @param messages
     */
    public static void send(CommandSender sender, String[] messages) {
        if (sender == null) {
            sender = Bukkit.getConsoleSender();
        }

        boolean removeColors = (!Settings.getInstance().getColorConsole() && sender instanceof ConsoleCommandSender);

        int messagesLength = messages.length;
        for (int i = 0; i < messagesLength; i++) {
            messages[i] = Tools.parseColors(messages[i], removeColors);
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
    public static void send(CommandSender sender, String message) {
        if (sender == null) {
            sender = Bukkit.getConsoleSender();
        }

        if (sender instanceof ConsoleCommandSender) {
            message = "[RecipeManager] " + message;
        }

        sender.sendMessage(Tools.parseColors(message, (sender instanceof ConsoleCommandSender && !Settings.getInstance().getColorConsole())));
    }

    public static void sendAndLog(CommandSender sender, String message) {
        if (sender instanceof Player) {
            send(sender, message);
        }

        info(message);
    }

    public static void sendDenySound(Player player, Location location) {
        sendSound(player, location, Sound.NOTE_BASS, 0.8f, 4, Settings.getInstance().getSoundsFailedClick());
    }

    public static void sendFailSound(Player player, Location location) {
        sendSound(player, location, Sound.NOTE_PLING, 0.8f, 4, Settings.getInstance().getSoundsFailed());
    }

    public static void sendRepairSound(Player player, Location location) {
        sendSound(player, location, Sound.ANVIL_USE, 0.8f, 4, Settings.getInstance().getSoundsRepair());
    }

    private static void sendSound(Player player, Location location, Sound sound, float volume, float pitch, boolean condition) {
        if (player != null && condition) {
            if (location == null) {
                location = player.getLocation();
            }
            player.playSound(location, sound, volume, pitch);
        }
    }

    /**
     * Used by plugin to log messages, shouldn't be used by other plugins unless really needed to send e message tagged by RecipeManager
     *
     * @param message
     */
    public static void info(String message) {
        send(null, message);
    }

    public static void log(String message) {
        Bukkit.getLogger().fine(Tools.parseColors("[RecipeManager] " + message, true));
    }

    public static void error(CommandSender sender, Throwable thrown, String message) {
        String reportMessage = "If you're using the latest version you should report this error at: http://dev.bukkit.org/server-mods/recipemanager/create-ticket/";
        try {
            if (message == null) {
                message = "<red>" + thrown.getMessage();
            } else {
                message = "<red>" + message + " (" + thrown.getMessage() + ")";
            }

            sendAndLog(sender, message);
            notifyDebuggers(message);

            thrown.printStackTrace();

            message = ChatColor.LIGHT_PURPLE + reportMessage;
            info(message);
            notifyDebuggers(message);
        } catch (Throwable e) {
            System.out.print("Error while printing error!");
            System.out.print("Initial error:");
            thrown.printStackTrace();

            System.out.print("Error printing error:");
            e.printStackTrace();

            System.out.print(reportMessage);
        }
    }

    /**
     * Notifies all online operators and people having "recipemanager.debugger" permission
     *
     * @param message
     */
    protected static void notifyDebuggers(String message) {
        message = ChatColor.DARK_RED + "(RecipeManager debug) " + ChatColor.RESET + message;

        try {
            // Use reflection to use the proper version of getOnlinePlayers - credit to Maxim Roncacé (ShadyPotato)
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
                Collection<?> onlinePlayers = ((Collection<?>) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));

                for (Object p : onlinePlayers) {
                    if (p instanceof Player) {
                        Player player = (Player) p;
                        if (player.hasPermission("recipemanager.debugger")) {
                            send(player, message);
                        }
                    }
                }
            } else {
                Player[] onlinePlayers = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                for (Player p : onlinePlayers) {
                    if (p.hasPermission("recipemanager.debugger")) {
                        send(p, message);
                    }
                }
            }
        } catch (Exception e) { }
    }

    public static void debug(String message) {
        StackTraceElement[] e = new Exception().getStackTrace();
        int i = 1;
        Bukkit.getConsoleSender().sendMessage(Tools.parseColors(ChatColor.GREEN + "[DEBUG]" + ChatColor.AQUA + "" + ChatColor.UNDERLINE + e[i].getFileName() + ":" + e[i].getLineNumber() + ChatColor.RESET + " " + ChatColor.RED + e[i].getMethodName() + "() " + ChatColor.WHITE + Tools.parseColors(message, false), false));
    }
}
