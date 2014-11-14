package haveric.recipeManager;

import haveric.recipeManager.api.events.RecipeManagerEnabledEvent;
import haveric.recipeManager.commands.BooksCommand;
import haveric.recipeManager.commands.CheckCommand;
import haveric.recipeManager.commands.ExtractCommand;
import haveric.recipeManager.commands.ExtractRecipeCommand;
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

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * RecipeManager's main class<br>
 * It has static methods for the API.
 */
public class RecipeManager extends JavaPlugin {
    private static RecipeManager plugin;
    private static Recipes recipes;
    private static RecipeBooks recipeBooks;
    private static Events events;
    private Metrics metrics;

    private HashMap<String, String> plugins = new HashMap<String, String>();

    // constants
    public static final Random random = new Random();

    @Override
    public void onEnable() {
        plugin = this;

        PluginManager pm = getServer().getPluginManager();
        Locale.setDefault(Locale.ENGLISH); // avoid needless complications


        FurnaceData.init(); // dummy caller
        Furnaces.load(); // load saved furnaces...

        events = new Events();
        recipes = new Recipes();

        setupVault(pm);

        Vanilla.init(); // get initial recipes...

        scanPlugins(); // scan for other plugins and store them in case any use our API

        Args.init(); // dummy method to avoid errors on 'reload' with updating
        ArgBuilder.init();
        FlagType.init();
        RecipeBooks.init();

        Files.init();
        Players.init();
        Workbenches.init();

        reload(null, false, true); // load data

        pm.callEvent(new RecipeManagerEnabledEvent()); // Call the enabled event to notify other plugins that use this plugin's API

        // Register commands
        getCommand("rm").setExecutor(new HelpCommand());
        getCommand("rmrecipes").setExecutor(new RecipeCommand());
        getCommand("rmfinditem").setExecutor(new FindItemCommand());
        getCommand("rmcheck").setExecutor(new CheckCommand());
        getCommand("rmreload").setExecutor(new ReloadCommand());
        getCommand("rmreloadbooks").setExecutor(new ReloadBooksCommand());
        getCommand("rmextractrecipe").setExecutor(new ExtractRecipeCommand());
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

    private void setupVault(PluginManager pm) {
        if (pm.getPlugin("Vault") == null) {
            Messages.log("Vault was not found, economy features are not available.");
        } else {
            RegisteredServiceProvider<Economy> econProvider = getServer().getServicesManager().getRegistration(Economy.class);
            if (econProvider != null) {
                Econ.getInstance().init(econProvider.getProvider());
            }

            RegisteredServiceProvider<Permission> permProvider = getServer().getServicesManager().getRegistration(Permission.class);
            if (permProvider != null) {
                Perms.getInstance().init(permProvider.getProvider());
            }
        }
    }

    /**
     * Reload RecipeManager's settings, messages, etc and re-parse recipes.
     *
     * @param sender
     *            To whom to send the messages to, null = console.
     * @param check
     *            Set to true to only check recipes, settings are unaffected.
     */
    public void reload(CommandSender sender, boolean check, boolean firstTime) {
        Settings.getInstance().reload(sender); // (re)load settings
        Files.reload(sender); // (re)generate info files if they do not exist
        Messages.reload(sender); // (re)load messages from messages.yml

        Updater.init(32835, null);

        if (metrics == null) {
            if (Settings.getInstance().getMetrics()) { // start/stop metrics accordingly
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

        if ((firstTime && Settings.getInstance().getClearRecipes()) || !firstTime) {
            Vanilla.removeAllButSpecialRecipes();
            Recipes.getInstance().clean();
        }

        if (!firstTime && !Settings.getInstance().getClearRecipes()) {
            Vanilla.restoreInitialRecipes();
            Recipes.getInstance().index.putAll(Vanilla.initialRecipes);
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

            Workbenches.clean();
            Players.clean();
            Vanilla.clean();

            Econ.clean();

            recipes.clean();
            recipes = null;

            recipeBooks.clean();
            recipeBooks = null;

            events.clean();
            events = null;

            Settings.clean();

            Perms.getInstance().clean();

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

    public static void setRecipeBooks(RecipeBooks newRecipeBooks) {
        recipeBooks = newRecipeBooks;
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

    public static Events getEvents() {
        return events;
    }
}
