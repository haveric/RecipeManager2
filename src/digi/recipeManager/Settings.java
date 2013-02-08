package digi.recipeManager;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings
{
    public char    EXISTING_RECIPES         = 'r';
    public boolean SPECIAL_REPAIR           = true;
    public boolean SPECIAL_REPAIR_ENCHANTED = false;
    public boolean SPECIAL_LEATHER_DYE      = true; // TODO read from CFG below
    public boolean SPECIAL_FIREWORKS        = true;
    public boolean SPECIAL_MAP_CLONING      = true;
    public boolean SPECIAL_MAP_EXTENDING    = true;
    public boolean UPDATE_BOOKS             = true;
    public boolean COLOR_CONSOLE            = true;
    public boolean RETURN_BUCKETS           = true;
    public boolean RETURN_POTIONS           = true;
    public boolean RETURN_BOWL              = true;
    public boolean FUEL_RETURN_BUCKETS      = true;
    public char    FURNACE_SHIFT_CLICK      = 'f';
    public int     FURNACE_TICKS            = 1;
    public boolean METRICS                  = true;
    public boolean DEBUG                    = false;
    
    public Settings(CommandSender sender)
    {
        FileConfiguration cfg = RecipeManager.getPlugin().getConfig();
        
        EXISTING_RECIPES = cfg.getString("existing-recipes", "" + EXISTING_RECIPES).charAt(0);
        SPECIAL_REPAIR = cfg.getBoolean("special-recipes.repair", SPECIAL_REPAIR);
        SPECIAL_REPAIR_ENCHANTED = cfg.getBoolean("special-recipes.repair-enchanted", SPECIAL_REPAIR_ENCHANTED);
        UPDATE_BOOKS = cfg.getBoolean("update-books", UPDATE_BOOKS);
        COLOR_CONSOLE = cfg.getBoolean("color-console", COLOR_CONSOLE);
        RETURN_BUCKETS = cfg.getBoolean("return-empty.buckets", RETURN_BUCKETS);
        RETURN_POTIONS = cfg.getBoolean("return-empty.potions", RETURN_POTIONS);
        RETURN_BOWL = cfg.getBoolean("return-empty.bowl", RETURN_BOWL);
        FUEL_RETURN_BUCKETS = cfg.getBoolean("fuel-return-buckets", FUEL_RETURN_BUCKETS);
        FURNACE_SHIFT_CLICK = cfg.getString("furnace-shift-click", "" + FURNACE_SHIFT_CLICK).charAt(0);
        FURNACE_TICKS = cfg.getInt("furnace-ticks", FURNACE_TICKS);
        METRICS = cfg.getBoolean("metrics", METRICS);
        
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
    }
}