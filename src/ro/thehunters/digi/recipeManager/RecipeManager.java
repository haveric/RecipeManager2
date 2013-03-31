package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ro.thehunters.digi.recipeManager.apievents.RecipeManagerEnabledEvent;
import ro.thehunters.digi.recipeManager.commands.BooksCommand;
import ro.thehunters.digi.recipeManager.commands.CheckUpdatesCommand;
import ro.thehunters.digi.recipeManager.commands.ExtractCommand;
import ro.thehunters.digi.recipeManager.commands.FindItemCommand;
import ro.thehunters.digi.recipeManager.commands.GetBookCommand;
import ro.thehunters.digi.recipeManager.commands.HelpCommand;
import ro.thehunters.digi.recipeManager.commands.RecipeCommand;
import ro.thehunters.digi.recipeManager.commands.ReloadCommand;
import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;

/**
 * RecipeManager's main class<br>
 * It has static methods for the API.
 */
public class RecipeManager extends JavaPlugin
{
    protected static RecipeManager plugin;
    protected static Recipes recipes;
    protected static RecipeBooks recipeBooks;
    protected static Events events;
    protected static Settings settings;
    protected static Economy economy;
    protected static Permissions permissions;
    protected static Metrics metrics;
    
    private final HashMap<String, String> plugins = new HashMap<String, String>();
    
    // constants
    public static final Random random = new Random();
    
    public void onEnable()
    {
        if(plugin != null)
        {
            Messages.info(ChatColor.RED + "Plugin is already enabled!");
            return;
        }
        
        onEnablePost();
        
        // wait for all plugins to load then init this...
        /*
        new BukkitRunnable()
        {
            public void run()
            {
                onEnablePost();
            }
        }.runTask(this);
        */
    }
    
    private void onEnablePost()
    {
        Locale.setDefault(Locale.ENGLISH); // avoid needless complications
        
        plugin = this;
        
        events = new Events();
        recipes = new Recipes();
        economy = new Economy();
        permissions = new Permissions();
        
        scanPlugins(); // scan for other plugins and store them in case any use our API
        Vanilla.init(); // get initial recipes...
        FlagType.init();
        Files.init();
        Workbenches.init(); // avoid errors on reload if jar is changed
        RecipeBooks.init();
        FurnaceWorker.init();
//        Furnaces.load(); // load saved furnaces...
        
        // Register commands
        getCommand("rm").setExecutor(new HelpCommand());
        getCommand("rmrecipes").setExecutor(new RecipeCommand());
        getCommand("rmfinditem").setExecutor(new FindItemCommand());
//        getCommand("rmcheck").setExecutor(new HelpCommand());
        getCommand("rmreload").setExecutor(new ReloadCommand());
        getCommand("rmextract").setExecutor(new ExtractCommand());
        getCommand("rmgetbook").setExecutor(new GetBookCommand());
        getCommand("rmbooks").setExecutor(new BooksCommand());
        getCommand("rmupdate").setExecutor(new CheckUpdatesCommand());
        
        for(BaseRecipe r : recipes.index.keySet())
        {
            if(r instanceof FuelRecipe)
            {
                recipes.indexFuels.put(((FuelRecipe)r).getIndexString(), (FuelRecipe)r);
            }
        }
        
        reload(null, false, true); // Start loading data
        
        getServer().getPluginManager().callEvent(new RecipeManagerEnabledEvent()); // Call the enabled event to notify other plugins that use this plugin's API
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
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
     * @param force
     *            Force re-loading/checking all recipes
     */
    public void reload(CommandSender sender, boolean check, boolean force)
    {
        boolean previousClearRecipes = (settings == null ? false : settings.CLEAR_RECIPES);
        
        Settings.reload(sender); // (re)load settings
        Files.reload(sender); // (re)generate info files if they do not exist
        Messages.reload(sender); // (re)load messages from messages.yml
        
        if(settings.UPDATE_CHECK_ENABLED)
        {
            UpdateChecker.start();
            
            new UpdateChecker(sender);
        }
        
        if(metrics == null)
        {
            if(settings.METRICS) // start/stop metrics accordingly
            {
                metrics = new Metrics(this);
                metrics.start();
            }
        }
        else if(metrics != null)
        {
            metrics.stop();
        }
        
        if(previousClearRecipes != settings.CLEAR_RECIPES)
        {
            if(settings.CLEAR_RECIPES)
            {
                Bukkit.clearRecipes();
                Recipes.getInstance().clean();
            }
            else
            {
                Vanilla.restoreInitialRecipes();
                Recipes.getInstance().index.putAll(Vanilla.initialRecipes);
                
                Messages.info("<green>Previous recipes restored! <gray>(due to clear-recipes set from true to false)");
            }
        }
        
        RecipeProcessor.reload(sender, check, force); // (re)parse recipe files
        Events.reload(sender); // (re)register events
    }
    
    private void scanPlugins()
    {
        String packageName;
        
        for(Plugin plugin : getServer().getPluginManager().getPlugins())
        {
            if(plugin instanceof RecipeManager)
                continue;
            
            packageName = plugin.getDescription().getMain();
            packageName = packageName.substring(0, packageName.lastIndexOf('.'));
            plugins.put(packageName, plugin.getName());
        }
    }
    
    protected String getPluginCaller(String method)
    {
        String packageName;
        String pluginName;
        StackTraceElement[] traces = new Exception().getStackTrace();
        StackTraceElement trace;
        
        Messages.debug("tracing...");
        
        for(int i = 0; i < traces.length; i++)
        {
            trace = traces[i];
            
            Messages.debug(trace.getClassName() + " | " + trace.getMethodName());
            
            if(trace.getMethodName().equals(method) && traces.length >= i)
            {
                trace = traces[++i];
                
                packageName = trace.getClassName();
                packageName = packageName.substring(0, packageName.lastIndexOf('.'));
                pluginName = plugins.get(packageName);
                
                if(pluginName != null)
                {
                    return pluginName;
                }
                
                Messages.debug("<red>Couldn't figure out plugin of package: " + packageName + " | class=" + trace.getClassName());
                return null;
            }
        }
        
        Messages.debug("<red>Couldn't find caller of " + method + " !");
        return null;
    }
    
    public void onDisable()
    {
        Bukkit.getScheduler().cancelTasks(this);
        
        if(plugin == null)
            return;
        
        Vanilla.removeCustomRecipes();
        
//        Furnaces.save();
//        Furnaces.clean();
        
        FurnaceWorker.clean();
        Workbenches.clean();
        Vanilla.clean();
        UpdateChecker.clean();
        
        economy.clear();
        economy = null;
        
        recipes.clean();
        recipes = null;
        
        recipeBooks.clean();
        recipeBooks = null;
        
        events = null;
        
        settings = null;
        
        permissions.clean();
        permissions = null;
        
        metrics.stop();
        metrics = null;
        
        plugin = null;
    }
    
    /**
     * @return plugin's main class
     * @throws
     */
    public static RecipeManager getPlugin()
    {
        validatePluginEnabled();
        return plugin;
    }
    
    /**
     * @return Recipes class
     */
    public static Recipes getRecipes()
    {
        validatePluginEnabled();
        return recipes;
    }
    
    /**
     * NOTE: Changes to a new instance on 'rmreload', do not store.
     * 
     * @return RecipeBooks class
     */
    public static RecipeBooks getRecipeBooks()
    {
        validatePluginEnabled();
        return recipeBooks;
    }
    
    /**
     * NOTE: Changes to a new instance on 'rmreload', do not store.
     * 
     * @return Configured settings
     */
    public static Settings getSettings()
    {
        validatePluginEnabled();
        return settings;
    }
    
    /**
     * NOTE: Changes to a new instance on 'rmreload', do not store.
     * 
     * @return Economy methods
     */
    public static Economy getEconomy()
    {
        validatePluginEnabled();
        return economy;
    }
    
    /**
     * NOTE: Changes to a new instance on 'rmreload', do not store.
     * 
     * @return hooked permissions from Vault
     */
    public static Permissions getPermissions()
    {
        validatePluginEnabled();
        return permissions;
    }
    
    private static void validatePluginEnabled()
    {
        if(!isPluginFullyEnabled())
        {
            throw new IllegalAccessError("RecipeManager is not fully enabled at this point! Listen to RecipeManagerEnabledEvent.");
        }
    }
    
    /**
     * @return True if plugin is fully enabled and can be used
     */
    public static boolean isPluginFullyEnabled()
    {
        return plugin != null;
    }
    
    /**
     * Checks sender's <i>recipemanager.craft</i> permission
     * 
     * @param sender
     * @return True if sender has the permission.
     */
    public boolean canCraft(CommandSender sender)
    {
        return (sender == null ? true : sender.hasPermission("recipemanager.craft"));
    }
}