package digi.recipeManager;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import digi.recipeManager.commands.ExtractCommand;
import digi.recipeManager.commands.ReloadCommand;

/**
 * RecipeManager's main class<br>
 * It has static methods for the API.
 */
public class RecipeManager extends JavaPlugin
{
    private static RecipeManager  plugin;
    protected static Recipes      recipes;
    protected static Events       events;
    private static Settings       settings;
    private static Permissions    permissions;
    private static Metrics        metrics;
    
    // constants
    public static final Random    random                = new Random();
    
    protected static final String LAST_CHANGED_MESSAGES = "v2.0";
    
    public void onEnable()
    {
        getCommand("test").setExecutor(new TEST()); // TODO REMOVE
        
        // wait for all plugins to load...
        getServer().getScheduler().runTask(this, new Runnable()
        {
            public void run()
            {
                init();
            }
        });
    }
    
    private void init()
    {
        if(plugin != null)
        {
            Messages.info(ChatColor.RED + "Plugin is already enabled!");
            return;
        }
        
        plugin = this;
        permissions = new Permissions();
        
        BukkitRecipes.init(); // get initial recipes...
        
        // Register commands
        getCommand("rmreload").setExecutor(new ReloadCommand());
        getCommand("rmextract").setExecutor(new ExtractCommand());
        
        // Start loading data
        reload(null, false);
    }
    
    /**
     * Reload RecipeManager's settings, messages, etc and re-parse recipes.
     * 
     * @param sender
     *            To whom to send the messages to, null = console.
     * @param check
     *            Set to true to only check recipes, settings are un affected.
     */
    public void reload(CommandSender sender, boolean check)
    {
        boolean previousClearRecipes = (settings == null ? false : settings.CLEAR_RECIPES);
        
        settings = new Settings(sender); // (re)load settings
        events = new Events(); // (re)register events
        
        new InfoFiles(sender); // (re)generate info files if they do not exist
        Messages.reload(); // (re)load messages from messages.yml
        
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
//            BukkitRecipes.restoreInitialRecipes();
            Bukkit.getServer().resetRecipes(); // TODO test
            Messages.info("<green>Previous recipes restored due to 'clear-recipes' beeing set from true to false.");
        }
        
        new RecipeProcessor(sender, check); // (re)parse recipe files
    }
    
    @Override
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
        metrics = null;
    }
    
    public static RecipeManager getPlugin()
    {
        return plugin;
    }
    
    public static Recipes getRecipes()
    {
        return recipes;
    }
    
    public static Settings getSettings()
    {
        return settings;
    }
    
    public static Permissions getPermissions()
    {
        return permissions;
    }
    
    public boolean canCraft(Player player)
    {
        return player.hasPermission("recipemanager.craft");
    }
}
