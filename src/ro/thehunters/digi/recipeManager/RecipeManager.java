package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import ro.thehunters.digi.recipeManager.apievents.RecipeManagerEnabledEvent;
import ro.thehunters.digi.recipeManager.commands.CheckUpdatesCommand;
import ro.thehunters.digi.recipeManager.commands.ExtractCommand;
import ro.thehunters.digi.recipeManager.commands.HelpCommand;
import ro.thehunters.digi.recipeManager.commands.ReloadCommand;

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
    protected static Economy              economy;
    protected static Permissions          permissions;
    protected static Metrics              metrics;
    
    private final HashMap<String, String> plugins = new HashMap<String, String>();
    
    // constants
    public static final Random            random  = new Random();
    
    public void onEnable()
    {
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
        if(plugin != null)
        {
            Messages.info(ChatColor.RED + "Plugin is already enabled!");
            return;
        }
        
        plugin = this;
        
        events = new Events();
        recipes = new Recipes();
        economy = new Economy();
        permissions = new Permissions();
        
        Workbenches.init(); // avoid errors on reload if jar is changed
        FurnaceWorker.init();
        Players.init();
        Furnaces.load(); // load saved furnaces...
        
        // Register commands
        getCommand("rm").setExecutor(new HelpCommand());
        getCommand("rmrecipes").setExecutor(new HelpCommand());
        getCommand("rmfinditem").setExecutor(new HelpCommand());
        getCommand("rmcheck").setExecutor(new HelpCommand());
        getCommand("rmreload").setExecutor(new ReloadCommand());
        getCommand("rmextract").setExecutor(new ExtractCommand());
        getCommand("rmgetbook").setExecutor(new HelpCommand());
        getCommand("rmupdate").setExecutor(new CheckUpdatesCommand());
        
        // -------------------------
        
        scanPlugins(); // scan for other plugins and store them in case any use our API
        Vanilla.init(); // get initial recipes...
        recipes.index.putAll(Vanilla.initialRecipes);
        
        // Start loading data
        reload(null, false, true);
        
        // Call the enabled event to notify other plugins that use this plugin's API
        getServer().getPluginManager().callEvent(new RecipeManagerEnabledEvent());
        
        // TODO testing
        
        /*
        for(BaseRecipe r : Vanilla.initialRecipes.keySet())
        {
            if(r instanceof CraftRecipe)
            {
                CraftRecipe cr = (CraftRecipe)r;
                
                if(cr.getFirstResult().getType() == Material.STICK)
                {
                    Messages.debug(" ");
                    Messages.debug(" " + Arrays.toString(cr.getIngredients()));
                    Messages.debug(" " + ArrayUtils.toString(cr.getResults()));
                }
            }
        }
        */
        /*
        ShapedRecipe testRecipe = new ShapedRecipe(new ItemStack(Material.YELLOW_FLOWER));
        testRecipe.shape("A", " ", "F");
        testRecipe.setIngredient('A', Material.WOOD, Vanilla.DATA_WILDCARD);
        testRecipe.setIngredient('F', Material.WOOD, 0);
        
        Bukkit.addRecipe(testRecipe);
        */
        /*
        Iterator<Recipe> it = Bukkit.recipeIterator();
        
        while(it.hasNext())
        {
            Recipe r = it.next();
            
            if(r instanceof ShapedRecipe && r.getResult().getType() == Material.YELLOW_FLOWER)
            {
                ShapedRecipe sr = (ShapedRecipe)r;
                
                for(String s : sr.getShape())
                {
                    Messages.debug("     " + s);
                }
                
                Messages.debug(" ");
                
                for(Entry<Character, ItemStack> e : sr.getIngredientMap().entrySet())
                {
                    Messages.debug(e.getKey() + " = " + e.getValue());
                }
                
                break;
            }
        }
        */
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
                else
                {
                    Messages.debug("<red>Couldn't figure out plugin of package: " + packageName + " | class=" + trace.getClassName());
                    return null;
                }
            }
        }
        
        Messages.debug("<red>Couldn't find caller of registerRecipesToServer!");
        return null;
    }
    
    public void onDisable()
    {
        if(plugin == null)
            return;
        
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
        
        events = null;
        
        settings = null;
        
        permissions.clean();
        permissions = null;
        
        metrics.stop();
        metrics = null;
        
        plugin = null;
        
        Bukkit.getScheduler().cancelTasks(this);
    }
    
    /**
     * @return plugin's main class
     */
    public static RecipeManager getPlugin()
    {
        return plugin;
    }
    
    /**
     * @return Recipes class
     */
    public static Recipes getRecipes()
    {
        return recipes;
    }
    
    /**
     * @return Configured settings
     */
    public static Settings getSettings()
    {
        return settings;
    }
    
    /**
     * @return Economy methods
     */
    public static Economy getEconomy()
    {
        return economy;
    }
    
    /**
     * @return hooked permissions from Vault
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