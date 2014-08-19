package haveric.recipeManager;

import haveric.recipeManager.api.events.RecipeManagerEnabledEvent;
import haveric.recipeManager.commands.BooksCommand;
import haveric.recipeManager.commands.CheckCommand;
import haveric.recipeManager.commands.ExtractCommand;
import haveric.recipeManager.commands.FindItemCommand;
import haveric.recipeManager.commands.GetBookCommand;
import haveric.recipeManager.commands.HelpCommand;
import haveric.recipeManager.commands.RecipeCommand;
import haveric.recipeManager.commands.ReloadBooksCommand;
import haveric.recipeManager.commands.ReloadCommand;
import haveric.recipeManager.commands.UpdateCommand;
import haveric.recipeManager.data.FurnaceData;
import haveric.recipeManager.flags.ArgBuilder;
import haveric.recipeManager.flags.Args;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.metrics.Metrics;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * RecipeManager's main class<br>
 * It has static methods for the API.
 */
public class RecipeManager extends JavaPlugin {
    protected static RecipeManager plugin;
    protected static Recipes recipes;
    protected static RecipeBooks recipeBooks;
    protected static Events events;
    protected static Settings settings;
    protected static Economy economy;
    protected static Permissions permissions;
    private Metrics metrics;

    private HashMap<String, String> plugins = new HashMap<String, String>();

    // constants
    public static Random random = new Random();

    @Override
    public void onEnable() {
        if (plugin != null) {
            Messages.info(ChatColor.RED + "Plugin is already enabled!");
            return;
        }

        Locale.setDefault(Locale.ENGLISH); // avoid needless complications

        plugin = this;

        FurnaceData.init(); // dummy caller
        Furnaces.load(); // load saved furnaces...

        events = new Events();
        recipes = new Recipes();
        economy = new Economy();
        permissions = new Permissions();

        Vanilla.init(); // get initial recipes...

        scanPlugins(); // scan for other plugins and store them in case any use our API

        Args.init(); // dummy method to avoid errors on 'reload' with updating
        ArgBuilder.init();
        FlagType.init();
        RecipeBooks.init();
        FurnaceWorker.init();

        Files.init();
        Players.init();
        Workbenches.init();

        reload(null, false); // load data

        FurnaceWorker.start(); // keep furnace worker running at all times because it has a lot of jobs

        getServer().getPluginManager().callEvent(new RecipeManagerEnabledEvent()); // Call the enabled event to notify other plugins that use this plugin's API

        // Register commands
        getCommand("rm").setExecutor(new HelpCommand());
        getCommand("rmrecipes").setExecutor(new RecipeCommand());
        getCommand("rmfinditem").setExecutor(new FindItemCommand());
        getCommand("rmcheck").setExecutor(new CheckCommand());
        getCommand("rmreload").setExecutor(new ReloadCommand());
        getCommand("rmreloadbooks").setExecutor(new ReloadBooksCommand());
        getCommand("rmextract").setExecutor(new ExtractCommand());
        getCommand("rmgetbook").setExecutor(new GetBookCommand());
        getCommand("rmbooks").setExecutor(new BooksCommand());
        getCommand("rmupdate").setExecutor(new UpdateCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Please wait for the plugin to fully (re)initialize...");
        return true;
    }

    /**
     * Reload RecipeManager's settings, messages, etc and re-parse recipes.
     *
     * @param sender
     *            To whom to send the messages to, null = console.
     * @param check
     *            Set to true to only check recipes, settings are un affected.
     */
    public void reload(CommandSender sender, boolean check) {
        boolean previousClearRecipes;
        if (settings == null) {
            previousClearRecipes = false;
        } else {
            previousClearRecipes = settings.CLEAR_RECIPES;
        }

        Settings.reload(sender); // (re)load settings
        Files.reload(sender); // (re)generate info files if they do not exist
        Messages.reload(sender); // (re)load messages from messages.yml

        Updater.init(32835, null);

        if (metrics == null) {
            if (settings.METRICS) { // start/stop metrics accordingly
                try {
                    metrics = new Metrics(this);
                    metrics.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            metrics.stop();
        }

        if (previousClearRecipes != settings.CLEAR_RECIPES) {
            if (settings.CLEAR_RECIPES) {
                Vanilla.removeAllButSpecialRecipes();
                Recipes.getInstance().clean();
            } else {
                Vanilla.restoreInitialRecipes();
                Recipes.getInstance().index.putAll(Vanilla.initialRecipes);

                Messages.sendAndLog(sender, "<green>Previous recipes restored! <gray>(due to clear-recipes set from true to false)");
            }
        }

        RecipeProcessor.reload(sender, check); // (re)parse recipe files
        Events.reload(); // (re)register events
    }

    private void scanPlugins() {
        String packageName;

        for (Plugin scanPlugin : getServer().getPluginManager().getPlugins()) {
            if (scanPlugin instanceof RecipeManager) {
                continue;
            }

            packageName = scanPlugin.getDescription().getMain();
            int i = packageName.lastIndexOf('.');
            if (i > 0) {
                packageName = packageName.substring(0, i);
            }
            plugins.put(packageName, scanPlugin.getName());
        }
    }

    protected String getPluginCaller(String method) {
        String packageName;
        String pluginName;
        StackTraceElement[] traces = new Exception().getStackTrace();
        StackTraceElement trace;

        for (int i = 0; i < traces.length; i++) {
            trace = traces[i];

            if (trace.getMethodName().equals(method) && traces.length >= i) {
                trace = traces[++i];

                packageName = trace.getClassName();
                packageName = packageName.substring(0, packageName.lastIndexOf('.'));
                pluginName = plugins.get(packageName);

                if (pluginName != null) {
                    return pluginName;
                }

                Messages.debug("<red>Couldn't figure out plugin of package: " + packageName + " | class=" + trace.getClassName());
                return null;
            }
        }

        Messages.debug("<red>Couldn't find caller of " + method + "!");
        return null;
    }

    @Override
    public void onDisable() {
        try {
            Bukkit.getScheduler().cancelTasks(this);

            if (plugin == null) {
                return;
            }

            Vanilla.removeCustomRecipes();

            Furnaces.save();
            Furnaces.clean();

            FurnaceWorker.clean();
            Workbenches.clean();
            Players.clean();
            Vanilla.clean();

            economy.clear();
            economy = null;

            recipes.clean();
            recipes = null;

            recipeBooks.clean();
            recipeBooks = null;

            events.clean();
            events = null;

            settings = null;

            permissions.clean();
            permissions = null;

            if (metrics != null) {
                metrics.stop();
                metrics = null;
            }

            plugin = null;
        } catch (Throwable e) {
            Messages.error(null, e, null);
        }
    }

    /**
     * @return plugin's main class
     * @throws
     */
    public static RecipeManager getPlugin() {
        validatePluginEnabled();
        return plugin;
    }

    /**
     * @return Recipes class
     */
    public static Recipes getRecipes() {
        validatePluginEnabled();
        return recipes;
    }

    /**
     * NOTE: Changes to a new instance on 'rmreload', do not store.
     *
     * @return RecipeBooks class
     */
    public static RecipeBooks getRecipeBooks() {
        validatePluginEnabled();
        return recipeBooks;
    }

    /**
     * NOTE: Changes to a new instance on 'rmreload', do not store.
     *
     * @return Configured settings
     */
    public static Settings getSettings() {
        validatePluginEnabled();
        return settings;
    }

    /**
     * NOTE: Changes to a new instance on 'rmreload', do not store.
     *
     * @return Economy methods
     */
    public static Economy getEconomy() {
        validatePluginEnabled();
        return economy;
    }

    /**
     * NOTE: Changes to a new instance on 'rmreload', do not store.
     *
     * @return hooked permissions from Vault
     */
    public static Permissions getPermissions() {
        validatePluginEnabled();
        return permissions;
    }

    private static void validatePluginEnabled() {
        if (!isPluginFullyEnabled()) {
            throw new IllegalAccessError("RecipeManager is not fully enabled at this point! Listen to RecipeManagerEnabledEvent.");
        }
    }

    /**
     * @return True if plugin is fully enabled and can be used
     */
    public static boolean isPluginFullyEnabled() {
        return plugin != null;
    }

    /**
     * Checks sender's <i>recipemanager.craft</i> permission
     *
     * @param sender
     * @return True if sender has the permission.
     */
    public boolean canCraft(CommandSender sender) {
        boolean canCraft = true;

        if (sender != null) {
            canCraft = sender.hasPermission("recipemanager.craft");
        }

        return canCraft;
    }
}
