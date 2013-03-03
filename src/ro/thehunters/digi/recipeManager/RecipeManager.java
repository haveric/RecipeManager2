package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import ro.thehunters.digi.recipeManager.commands.ExtractCommand;
import ro.thehunters.digi.recipeManager.commands.ReloadCommand;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;

/**
 * RecipeManager's main class<br>
 * It has static methods for the API.
 */
public class RecipeManager extends JavaPlugin
{
    @Override
    protected void finalize() throws Throwable // TODO REMOVE
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + getClass().getName() + " :: finalize()");
        
        super.finalize();
    }
    
    protected static RecipeManager        plugin;
    protected static Recipes              recipes;
    protected static Events               events;
    protected static Settings             settings;
    protected static Permissions          permissions;
    protected static Metrics              metrics;
    
    private final HashMap<String, String> plugins = new HashMap<String, String>();
    
    // constants
    public static final Random            random  = new Random();
    
    public void onEnable()
    {
        if(plugin != null)
        {
            Messages.info(ChatColor.RED + "Plugin is already enabled!");
            return;
        }
        
        plugin = this;
        recipes = new Recipes();
        permissions = new Permissions();
        events = new Events();
        
        BukkitRecipes.init(); // get initial recipes...
        recipes.index.putAll(BukkitRecipes.initialRecipes);
        
        // Register commands
        getCommand("test").setExecutor(new TEST()); // TODO REMOVE
        getCommand("rmreload").setExecutor(new ReloadCommand());
        getCommand("rmextract").setExecutor(new ExtractCommand());
        
        // wait for all plugins to load then init this...
        new BukkitRunnable()
        {
            public void run()
            {
                onEnablePost();
            }
        }.runTask(this);
    }
    
    private void onEnablePost()
    {
        scanPlugins(); // scan for other plugins and store them in case any use our API
        BukkitRecipes.init(); // update initial recipes...
        recipes.index.putAll(BukkitRecipes.initialRecipes);
        
        // Start loading data
        reload(null, false, true);
        
        // TEST
        SmeltRecipe r = new SmeltRecipe();
        
        r.setIngredient(new ItemStack(Material.SEEDS));
        
        r.setFuel(new ItemStack(Material.POTION, 1, new Potion(PotionType.INSTANT_HEAL).toDamageValue()));
        
        r.setMinTime(2);
        
        r.setResult(new ItemStack(Material.POTION, 1, new Potion(PotionType.INSTANT_HEAL).splash().toDamageValue()));
        
        r.register();
    }
    
    /**
     * Reload RecipeManager's settings, messages, etc and re-parse recipes.
     * 
     * @param sender
     *            To whom to send the messages to, null = console.
     * @param check
     *            Set to true to only check recipes, settings are un affected.
     * @param force
     */
    public void reload(CommandSender sender, boolean check, boolean force)
    {
        boolean previousClearRecipes = (settings == null ? false : settings.CLEAR_RECIPES);
        
        Settings.reload(sender); // (re)load settings
        Files.reload(sender); // (re)generate info files if they do not exist
        Messages.reload(sender); // (re)load messages from messages.yml
        
        if(settings.METRICS) // start/stop metrics accordingly
        {
            metrics = new Metrics(this);
            metrics.start();
        }
        else if(metrics != null)
        {
            metrics.stop();
        }
        
        if(previousClearRecipes != false && RecipeManager.getSettings().CLEAR_RECIPES == false)
        {
            BukkitRecipes.restoreInitialRecipes();
            Messages.info("<green>Previous recipes restored! <gray>(due to clear-recipes set from true to false)");
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
        
        Messages.info(ChatColor.GRAY + "[debug] tracing...");
        
        for(int i = 0; i < traces.length; i++)
        {
            trace = traces[i];
            
            Messages.info(ChatColor.GRAY + "[debug] " + trace.getClassName() + " | " + trace.getMethodName());
            
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
                else
                {
                    Messages.info(ChatColor.RED + "[debug] Couldn't figure out plugin of package: " + packageName + " | class=" + trace.getClassName());
                    return null;
                }
            }
        }
        
        Messages.info(ChatColor.RED + "[debug] Couldn't find caller of registerRecipesToServer!");
        return null;
    }
    
    public void onDisable()
    {
        if(plugin == null)
            return;
        
        FurnaceWorker.stop();
        BukkitRecipes.clean();
        Bukkit.getScheduler().cancelTasks(this);
        
        plugin = null;
        recipes = null;
        events = null;
        settings = null;
        permissions = null;
        metrics = null;
    }
    
    /**
     * Get the plugin instance for more methods
     * 
     * @return
     */
    public static RecipeManager getPlugin()
    {
        return plugin;
    }
    
    /**
     * Get recipes class
     * 
     * @return
     */
    public static Recipes getRecipes()
    {
        return recipes;
    }
    
    /**
     * Get configured settings
     * 
     * @return
     */
    public static Settings getSettings()
    {
        return settings;
    }
    
    /**
     * Get hooked permissions from Vault
     * 
     * @return
     */
    public static Permissions getPermissions()
    {
        return permissions;
    }
    
    /**
     * Checks sender's <i>recipemanager.craft</i> permission
     * 
     * @param sender
     * @return
     */
    public boolean canCraft(CommandSender sender)
    {
        return (sender == null ? true : sender.hasPermission("recipemanager.craft"));
    }
}