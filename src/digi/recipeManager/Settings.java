package digi.recipeManager;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings
{
    public final char    EXISTING_RECIPES;
    
    public final boolean SPECIAL_REPAIR;
    public final boolean SPECIAL_REPAIR_ENCHANTED;
    public final boolean SPECIAL_LEATHER_DYE;
    public final boolean SPECIAL_FIREWORKS;
    public final boolean SPECIAL_MAP_CLONING;
    public final boolean SPECIAL_MAP_EXTENDING;
    
    public final boolean UPDATE_BOOKS;
    public final boolean COLOR_CONSOLE;
    
    public final boolean RETURN_BUCKETS;
    public final boolean RETURN_POTIONS;
    public final boolean RETURN_BOWL;
    
    public final boolean FUEL_RETURN_BUCKETS;
    
    public final char    FURNACE_SHIFT_CLICK;
    public final int     FURNACE_TICKS;
    
    public final boolean METRICS;
    
    public final char    COLOR_CHAR;
    
    public Settings(CommandSender sender)
    {
        FileConfiguration cfg = RecipeManager.getPlugin().getConfig();
        
        EXISTING_RECIPES = cfg.getString("existing-recipes", "" + 'r').charAt(0);
        
        SPECIAL_REPAIR = cfg.getBoolean("special-recipes.repair", true);
        SPECIAL_REPAIR_ENCHANTED = cfg.getBoolean("special-recipes.repair-enchanted", false);
        SPECIAL_LEATHER_DYE = cfg.getBoolean("special-recipes.leather-armor-dye", false);
        SPECIAL_FIREWORKS = cfg.getBoolean("special-recipes.fireworks", false);
        SPECIAL_MAP_CLONING = cfg.getBoolean("special-recipes.map-cloning", false);
        SPECIAL_MAP_EXTENDING = cfg.getBoolean("special-recipes.map-extending", false);
        
        UPDATE_BOOKS = cfg.getBoolean("update-books", true);
        COLOR_CONSOLE = cfg.getBoolean("color-console", true);
        
        RETURN_BUCKETS = cfg.getBoolean("return-empty.buckets", true);
        RETURN_POTIONS = cfg.getBoolean("return-empty.potions", true);
        RETURN_BOWL = cfg.getBoolean("return-empty.bowl", true);
        
        FUEL_RETURN_BUCKETS = cfg.getBoolean("fuel-return-buckets", true);
        FURNACE_SHIFT_CLICK = cfg.getString("furnace-shift-click", "f").charAt(0);
        FURNACE_TICKS = cfg.getInt("furnace-ticks", 1);
        METRICS = cfg.getBoolean("metrics", true);
        COLOR_CHAR = cfg.getString("color-character", "&").charAt(0);
        
        /* TODO ?
        Logger log = Bukkit.getLogger();
        
        log.fine("config.yml settings:");
        log.fine("    existing-recipes: " + EXISTING_RECIPES);
        log.fine("    special-recipes.repair: " + SPECIAL_REPAIR);
        log.fine("    special-recipes.repair-enchanted: " + SPECIAL_REPAIR_ENCHANTED);
        log.fine("    update-books: " + UPDATE_BOOKS);
        log.fine("    color-console: " + COLOR_CONSOLE);
        log.fine("    return-empty.buckets: " + RETURN_BUCKETS);
        log.fine("    return-empty.potions: " + RETURN_POTIONS);
        log.fine("    return-empty.bowl: " + RETURN_BOWL);
        log.fine("    fuel-return-buckets: " + FUEL_RETURN_BUCKETS);
        log.fine("    furnace-shift-click: " + FURNACE_SHIFT_CLICK);
        log.fine("    furnace-ticks: " + FURNACE_TICKS);
        log.fine("    metrics: " + METRICS);
        */
    }
}