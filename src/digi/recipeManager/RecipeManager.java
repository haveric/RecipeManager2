package digi.recipeManager;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * RecipeManager's main class<br>
 * It has static methods for the API.
 */
public class RecipeManager extends JavaPlugin
{
    protected static RecipeManager plugin;
    protected static Recipes       recipes;
    protected static Events        events;
    protected static Settings      settings;
    
    protected static boolean       updatingRecipes       = false;
    
    // constants
    public static final Random     rand                  = new Random();
    public static final String     LAST_CHANGED_MESSAGES = "2.0";
    
    @Override
    public void onEnable()
    {
        if(plugin != null)
        {
            Messages.info(ChatColor.RED + "Plugin is already enabled!");
            return;
        }
        
        plugin = this;
        recipes = null;
        events = new Events();
        
        getServer().getScheduler().runTaskLater(this, new Runnable()
        {
            public void run()
            {
                init();
            }
        }, 20);
    }
    
    private void init()
    {
        BukkitRecipes.init();
        loadData(null, false);
    }
    
    protected void loadData(CommandSender sender, boolean check)
    {
        settings = new Settings(sender);
        new InfoFiles(sender);
        
//        if(settings.METRICS)
//            new Metrics(this);
        
        new RecipeProcessor(sender, check);
        
        getServer().getScheduler().runTaskTimer(this, new FurnaceWorker(RecipeManager.settings.FURNACE_TICKS), 0, RecipeManager.settings.FURNACE_TICKS);
    }
    
    @Override
    public void onDisable()
    {
        Bukkit.getScheduler().cancelTasks(this);
        
        BukkitRecipes.clean();
        
        plugin = null;
        recipes = null;
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
    
    public boolean canCraft(Player player)
    {
        return true; // TODO
        /*
        if(player == null)
            return true;
        
        return !player.hasPermission("recipemanager.nocraft");
        */
    }
}
