package haveric.recipeManager;

import haveric.recipeManager.api.events.RecipeManagerEnabledEvent;
import haveric.recipeManager.commands.*;
import haveric.recipeManager.flag.FlagFactory;
import haveric.recipeManager.flag.FlagLoader;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.anvil.AnvilEvents;
import haveric.recipeManager.recipes.anvil.data.Anvils;
import haveric.recipeManager.recipes.brew.BrewEvents;
import haveric.recipeManager.recipes.brew.data.BrewingStandData;
import haveric.recipeManager.recipes.brew.data.BrewingStands;
import haveric.recipeManager.recipes.campfire.RMCampfireEvents;
import haveric.recipeManager.recipes.campfire.data.RMCampfireData;
import haveric.recipeManager.recipes.campfire.data.RMCampfires;
import haveric.recipeManager.recipes.cartography.CartographyEvents;
import haveric.recipeManager.recipes.compost.CompostEvents;
import haveric.recipeManager.recipes.compost.data.ComposterData;
import haveric.recipeManager.recipes.compost.data.Composters;
import haveric.recipeManager.recipes.furnace.RMBaseFurnaceEvents;
import haveric.recipeManager.recipes.furnace.data.FurnaceData;
import haveric.recipeManager.recipes.furnace.data.Furnaces;
import haveric.recipeManager.recipes.grindstone.GrindstoneEvents;
import haveric.recipeManager.recipes.stonecutting.RMStonecuttingEvents;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

/**
 * RecipeManager's main class<br>
 * It has static methods for the API.
 */
public class RecipeManager extends JavaPlugin {
    private static RecipeManager plugin;
    private static Recipes recipes;
    private static RecipeBooks recipeBooks;
    private static Events events;
    private static BrewEvents brewEvents;
    private static RMBaseFurnaceEvents furnaceEvents;
    private static RMCampfireEvents campfireEvents;
    private static RMStonecuttingEvents stonecuttingEvents;
    private static CompostEvents compostEvents;
    private static AnvilEvents anvilEvents;
    private static GrindstoneEvents grindstoneEvents;
    private static CartographyEvents cartographyEvents;
    private HashMap<String, String> plugins = new HashMap<>();

    // constants
    public static final Random random = new Random();
    private boolean loaded = false;
    public static FlagLoader flagLoader = null;

    @Override
    public void onEnable() {
        if (loaded) {
            MessageSender.getInstance().info(ChatColor.RED + "Plugin is already enabled");
            return;
        }

        plugin = this;
        Locale.setDefault(Locale.ENGLISH); // avoid needless complications

        PluginManager pm = getServer().getPluginManager();
        FurnaceData.init(); // dummy caller to initialize Serialization class
        BrewingStandData.init();

        if (Version.has1_14Support()) {
            RMCampfireData.init(); // dummy caller to initialize Serialization class
            ComposterData.init();
        }

        Furnaces.load(); // load saved furnaces...
        BrewingStands.load();

        if (Version.has1_14Support()) {
            RMCampfires.load();
            Composters.load();
        }

        events = new Events();
        brewEvents = new BrewEvents();
        furnaceEvents = new RMBaseFurnaceEvents();
        anvilEvents = new AnvilEvents();

        if (Version.has1_14Support()) {
            campfireEvents = new RMCampfireEvents();
            stonecuttingEvents = new RMStonecuttingEvents();
            compostEvents = new CompostEvents();
            grindstoneEvents = new GrindstoneEvents();
            cartographyEvents = new CartographyEvents();
        }

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
        RecipeBooks.getInstance().init();
        RecipeBooks.getInstance().reload(null);

        Files.init();
        Players.init();
        Workbenches.init();
        Anvils.init();

        reload(null, false, true); // load data

        pm.callEvent(new RecipeManagerEnabledEvent()); // Call the enabled event to notify other plugins that use this plugin's API

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
        getCommand("rmcreaterecipe").setExecutor(new CreateRecipeCommand());
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
     * Reload RecipeManager's settings, messages, etc and re-parse recipes.
     *
     * @param sender
     *            To whom to send the messages to, null = console.
     * @param check
     *            Set to true to only check recipes, settings are unaffected.
     */
    public void reload(CommandSender sender, boolean check, boolean firstTime) {
        Settings.clearInit();

        Settings.getInstance().reload(sender); // (re)load settings
        Messages.getInstance().reload(sender); // (re)load messages from messages.yml
        Files.reload(sender); // (re)generate info files if they do not exist

        Updater.init(this, 32835, null);

        if (!check) {
            if (Settings.getInstance().getClearRecipes() || !firstTime) {
                Vanilla.removeAllButSpecialRecipes();
                Recipes.getInstance().clean();
            }

            if (!firstTime && !Settings.getInstance().getClearRecipes()) {
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

        RecipeProcessor.reload(sender, check); // (re)parse recipe files
        Events.reload(); // (re)register events
        BrewEvents.reload();
        RMBaseFurnaceEvents.reload();
        AnvilEvents.reload();

        if (Version.has1_14Support()) {
            RMCampfireEvents.reload();
            RMStonecuttingEvents.reload();
            CompostEvents.reload();
            GrindstoneEvents.reload();
            CartographyEvents.reload();
        }
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

            if (trace.getMethodName().equals(method) && tracesLength >= i) {
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

            RMCampfires.save();
            RMCampfires.clean();

            Composters.save();
            Composters.clean();

            Workbenches.clean();
            Anvils.clean();
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

            if (brewEvents != null) {
                brewEvents.clean();
            }
            brewEvents = null;

            if (furnaceEvents != null) {
                furnaceEvents.clean();
            }
            furnaceEvents = null;

            if (campfireEvents != null) {
                campfireEvents.clean();
            }
            campfireEvents = null;

            if (stonecuttingEvents != null) {
                stonecuttingEvents.clean();
            }
            stonecuttingEvents = null;

            if (compostEvents != null) {
                compostEvents.clean();
            }
            compostEvents = null;

            if (anvilEvents != null) {
                anvilEvents.clean();
            }
            anvilEvents = null;

            if (grindstoneEvents != null) {
                grindstoneEvents.clean();
            }
            grindstoneEvents = null;

            if (cartographyEvents != null) {
                cartographyEvents.clean();
            }
            cartographyEvents = null;

            Settings.clean();

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

    public static BrewEvents getBrewEvents() {
        return brewEvents;
    }

    public static RMBaseFurnaceEvents getRMFurnaceEvents() {
        return furnaceEvents;
    }

    public static RMCampfireEvents getRMCampfireEvents() {
        return campfireEvents;
    }

    public static RMStonecuttingEvents getRMStonecuttingEvents() {
        return stonecuttingEvents;
    }

    public static CompostEvents getCompostEvents() {
        return compostEvents;
    }

    public static AnvilEvents getAnvilEvents() {
        return anvilEvents;
    }

    public static GrindstoneEvents getGrindstoneEvents() {
        return grindstoneEvents;
    }

    public static CartographyEvents getCartographyEvents() {
        return cartographyEvents;
    }

    public static FlagLoader getFlagLoader() {
        return flagLoader;
    }
}
