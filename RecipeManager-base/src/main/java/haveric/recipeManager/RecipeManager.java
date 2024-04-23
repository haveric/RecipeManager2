package haveric.recipeManager;

import haveric.recipeManager.api.events.RecipeManagerEnabledEvent;
import haveric.recipeManager.commands.*;
import haveric.recipeManager.commands.recipe.RecipeCommand;
import haveric.recipeManager.commands.recipe.RecipeNextCommand;
import haveric.recipeManager.commands.recipe.RecipePrevCommand;
import haveric.recipeManager.flag.FlagFactory;
import haveric.recipeManager.flag.FlagLoader;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.any.flagCooldown.CooldownData;
import haveric.recipeManager.flag.flags.any.flagCooldown.Cooldowns;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.RecipeTypeFactory;
import haveric.recipeManager.recipes.RecipeTypeLoader;
import haveric.recipeManager.recipes.anvil.data.Anvils;
import haveric.recipeManager.recipes.brew.BrewInventoryUtil;
import haveric.recipeManager.recipes.brew.data.BrewingStandData;
import haveric.recipeManager.recipes.brew.data.BrewingStands;
import haveric.recipeManager.recipes.compost.data.ComposterData;
import haveric.recipeManager.recipes.compost.data.Composters;
import haveric.recipeManager.recipes.cooking.campfire.data.RMCampfireData;
import haveric.recipeManager.recipes.cooking.campfire.data.RMCampfires;
import haveric.recipeManager.recipes.cooking.furnace.data.FurnaceData;
import haveric.recipeManager.recipes.cooking.furnace.data.Furnaces;
import haveric.recipeManager.settings.BaseSettings;
import haveric.recipeManager.settings.SettingsYaml;
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.Version;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

/**
 * RecipeManager's main class<br>
 * It has static methods for the API.
 */
public class RecipeManager extends JavaPlugin {
    private static BaseSettings settings;
    private static RecipeManager plugin;
    private static Recipes recipes;
    private static RecipeBooks recipeBooks;
    private static LocalDateTime lastReload = null;

    private static Events events;

    private HashMap<String, String> plugins = new HashMap<>();

    // constants
    public static final Random random = new Random();
    private boolean loaded = false;
    public static FlagLoader flagLoader = null;
    public static RecipeTypeLoader recipeTypeLoader = null;

    @Override
    public void onEnable() {
        if (loaded) {
            MessageSender.getInstance().info(ChatColor.RED + "Plugin is already enabled");
            return;
        }

        plugin = this;
        Locale.setDefault(Locale.ENGLISH); // avoid needless complications

        PluginManager pm = getServer().getPluginManager();
        Supports.init();

        FurnaceData.init(); // dummy caller to initialize Serialization class
        BrewingStandData.init();
        CooldownData.init();

        if (Version.has1_14Support()) {
            RMCampfireData.init(); // dummy caller to initialize Serialization class
            ComposterData.init();
        }

        events = new Events();
        recipeTypeLoader = new RecipeTypeLoader();

        recipes = new Recipes();

        setupVault(pm);

        Vanilla.init(); // get initial recipes...

        Args.init(); // dummy method to avoid errors on 'reload' with updating
        ArgBuilder.init();

        flagLoader = new FlagLoader();

        // wait for all plugins to load then enable this
        new BukkitRunnable() {
            public void run() {
                onEnablePost();
            }
        }.runTask(this);
    }

    private void onEnablePost() {
        loaded = true;

        PluginManager pm = getServer().getPluginManager();

        scanPlugins(); // scan for other plugins and store them in case any use our API

        FlagFactory.getInstance().init();
        FlagFactory.getInstance().initPermissions();
        RecipeTypeFactory.getInstance().init();
        RecipeBooks.getInstance().init();
        RecipeBooks.getInstance().reload(null);

        Files.init();
        Players.init();
        Workbenches.init();
        Anvils.init();

        BrewInventoryUtil.init();

        reload(null, false, true); // load data

        pm.callEvent(new RecipeManagerEnabledEvent()); // Call the enabled event to notify other plugins that use this plugin's API

        // Register commands
        getCommand("rm").setExecutor(new HelpCommand());
        getCommand("rmrecipes").setExecutor(new RecipeCommand());
        getCommand("rmnext").setExecutor(new RecipeNextCommand());
        getCommand("rmprev").setExecutor(new RecipePrevCommand());
        getCommand("rmfinditem").setExecutor(new FindItemCommand());
        getCommand("rmcheck").setExecutor(new CheckCommand());
        getCommand("rmreload").setExecutor(new ReloadCommand());
        getCommand("rmreloadbooks").setExecutor(new ReloadBooksCommand());
        getCommand("rmextract").setExecutor(new ExtractCommand());
        getCommand("rmgetbook").setExecutor(new GetBookCommand());
        getCommand("rmbooks").setExecutor(new BooksCommand());
        getCommand("rmupdate").setExecutor(new UpdateCommand());
        getCommand("rmcreaterecipe").setExecutor(new CreateRecipeCommand());
        getCommand("rmdebug").setExecutor(new DebugCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Please wait for the plugin to fully (re)initialize...");
        return true;
    }

    private void setupVault(PluginManager pm) {
        if (pm.getPlugin("Vault") == null) {
            MessageSender.getInstance().log("Vault was not found, economy features are not available.");
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
     * Reload RecipeManager's settings, messages, etc. and reparse recipes.
     *
     * @param sender
     *            To whom to send the messages to, null = console.
     * @param check
     *            Set to true to only check recipes, settings are unaffected.
     */
    public void reload(CommandSender sender, boolean check, boolean firstTime) {
        lastReload = LocalDateTime.now();

        if (settings == null) {
            settings = new SettingsYaml(true);
        } else {
            settings.clearInit();
        }

        settings.reload(sender); // (re)load settings

        if (!firstTime) {
            Cooldowns.save();
            Furnaces.save();
            BrewingStands.save();

            if (Version.has1_14Support()) {
                RMCampfires.save();
                Composters.save();
            }
        }

        // Load saved datas
        Cooldowns.load();
        Furnaces.load();
        BrewingStands.load();

        if (Version.has1_14Support()) {
            RMCampfires.load();
            Composters.load();
        }

        Messages.getInstance().reload(sender); // (re)load messages from messages.yml
        Files.reload(sender); // (re)generate info files if they do not exist

        Updater.init(this, 32835, null);

        if (!check) {
            if (settings.getClearRecipes() || !firstTime) {
                Vanilla.removeAllButSpecialRecipes();
                Recipes.getInstance().clean();
            }

            if (!firstTime && !settings.getClearRecipes()) {
                if (!Version.has1_12Support()) {
                    Vanilla.restoreAllButSpecialRecipes();
                    Recipes.getInstance().index.putAll(Vanilla.initialRecipes);
                } else {
                    Vanilla.removeCustomRecipes();
                    // Basically does server recipe reset and vanilla re-init, but also
                    // tries to save any other recipes.
                }
            }
        }

        BrewInventoryUtil.clean();

        RecipeProcessor.reload(sender, check); // (re)parse recipe files
        Events.reload();
        RecipeTypeFactory.getInstance().reloadEvents();
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

        int tracesLength = traces.length;
        for (int i = 0; i < tracesLength; i++) {
            trace = traces[i];

            if (trace.getMethodName().equals(method)) {
                trace = traces[++i];

                packageName = trace.getClassName();
                packageName = packageName.substring(0, packageName.lastIndexOf('.'));
                pluginName = plugins.get(packageName);

                if (pluginName != null) {
                    return pluginName;
                }

                MessageSender.getInstance().debug("<red>Couldn't figure out plugin of package: " + packageName + " | class=" + trace.getClassName());
                return null;
            }
        }

        MessageSender.getInstance().debug("<red>Couldn't find caller of " + method + "!");
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

            BrewingStands.save();
            BrewingStands.clean();

            Cooldowns.save();
            Cooldowns.clean();

            if (Version.has1_14Support()) {
                RMCampfires.save();
                RMCampfires.clean();

                Composters.save();
                Composters.clean();
            }

            Workbenches.clean();
            Anvils.clean();
            BrewInventoryUtil.clean();

            Players.clean();
            Vanilla.clean();


            if (recipes != null) {
                recipes.clean();
            }
            recipes = null;

            if (recipeBooks != null) {
                recipeBooks.clean();
            }
            recipeBooks = null;

            if (events != null) {
                events.clean();
            }
            events = null;

            RecipeTypeFactory.getInstance().cleanEvents();

            try {
                Econ.getInstance().clean();
            } catch (NoClassDefFoundError e) {
                // Ignore missing class on stop
            }

            try {
                Perms.getInstance().clean();
            } catch (NoClassDefFoundError e) {
                // Ignore missing class on stop
            }

            plugin = null;
        } catch (Throwable e) {
            MessageSender.getInstance().error(null, e, null);
        }
    }

    /**
     * @return plugin's main class
     * @throws
     */
    public static RecipeManager getPlugin() {
        return plugin;
    }

    /**
     * @return Recipes class
     */
    public static Recipes getRecipes() {
        return recipes;
    }

    public static void setRecipes(Recipes newRecipes) {
        recipes = newRecipes;
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

    public static FlagLoader getFlagLoader() {
        return flagLoader;
    }

    public static RecipeTypeLoader getRecipeTypeLoader() {
        return recipeTypeLoader;
    }

    public static BaseSettings getSettings() {
        return settings;
    }

    public static LocalDateTime getLastReload() {
        return lastReload;
    }
}
