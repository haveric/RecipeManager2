package digi.recipeManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * RecipeManager's settings loaded from its config.yml, values are read-only.
 */
public class Settings
{
    public final boolean              SPECIAL_REPAIR;
    public final boolean              SPECIAL_REPAIR_ENCHANTED;
    
    public final boolean              SPECIAL_LEATHER_DYE;
    public final boolean              SPECIAL_FIREWORKS;
    public final boolean              SPECIAL_MAP_CLONING;
    public final boolean              SPECIAL_MAP_EXTENDING;
    
    public final boolean              SOUNDS_REPAIR;
    public final boolean              SOUNDS_FAILED;
    public final boolean              SOUNDS_FAILED_CLICK;
    
    public final boolean              UPDATE_BOOKS;
    public final boolean              COLOR_CONSOLE;
    public final char                 COLOR_CHAR;
    
    public final boolean              RETURN_BUCKETS;
    public final boolean              RETURN_POTIONS;
    public final boolean              RETURN_BOWL;
    
    public final boolean              FUEL_RETURN_BUCKETS;
    
    public final char                 FURNACE_SHIFT_CLICK;
    public final int                  FURNACE_TICKS;
    
    public final boolean              MULTITHREADING;
    
    public final boolean              CLEAR_RECIPES;
    
    public final boolean              METRICS;
    
    private final Map<String, String> itemAlias = new HashMap<String, String>();
    private final Map<String, String> aliasItem = new HashMap<String, String>();
    
    public Settings(CommandSender sender)
    {
        FileConfiguration cfg = RecipeManager.getPlugin().getConfig();
        
        SPECIAL_REPAIR = cfg.getBoolean("special-recipes.repair", true);
        SPECIAL_REPAIR_ENCHANTED = cfg.getBoolean("special-recipes.repair-enchanted", false);
        
        SPECIAL_LEATHER_DYE = cfg.getBoolean("special-recipes.leather-armor-dye", false);
        SPECIAL_FIREWORKS = cfg.getBoolean("special-recipes.fireworks", false);
        SPECIAL_MAP_CLONING = cfg.getBoolean("special-recipes.map-cloning", false);
        SPECIAL_MAP_EXTENDING = cfg.getBoolean("special-recipes.map-extending", false);
        
        SOUNDS_REPAIR = cfg.getBoolean("sounds.repair", true);
        SOUNDS_FAILED = cfg.getBoolean("sounds.failed", true);
        SOUNDS_FAILED_CLICK = cfg.getBoolean("sounds.failed_click", true);
        
        UPDATE_BOOKS = cfg.getBoolean("update-books", true);
        COLOR_CONSOLE = cfg.getBoolean("color-console", true);
        COLOR_CHAR = cfg.getString("color-character", "&").charAt(0);
        
        RETURN_BUCKETS = cfg.getBoolean("return-empty.buckets", true);
        RETURN_POTIONS = cfg.getBoolean("return-empty.potions", true);
        RETURN_BOWL = cfg.getBoolean("return-empty.bowl", true);
        
        FUEL_RETURN_BUCKETS = cfg.getBoolean("fuel-return-buckets", true);
        FURNACE_SHIFT_CLICK = cfg.getString("furnace-shift-click", "f").charAt(0);
        FURNACE_TICKS = cfg.getInt("furnace-ticks", 1);
        
        MULTITHREADING = cfg.getBoolean("multithreading", true);
        
        CLEAR_RECIPES = cfg.getBoolean("clear-recipes", false);
        
        METRICS = cfg.getBoolean("metrics", true);
        
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
        
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "aliases.yml");
        
        if(!file.exists())
        {
            RecipeManager.getPlugin().saveResource("aliases.yml", false);
            Messages.info(ChatColor.GREEN + "Generated 'aliases.yml' file.");
        }
        
        cfg = YamlConfiguration.loadConfiguration(file);
        aliasItem.clear();
        itemAlias.clear();
        String alias;
        String item;
        
        for(Entry<String, Object> entry : cfg.getValues(false).entrySet())
        {
            if(entry.getKey().equals("lastchanged"))
                continue;
            
            item = entry.getValue().toString().toUpperCase();
            
            if(!item.contains(":"))
                item = item + ":*";
            
            alias = entry.getKey().toUpperCase();
            
            aliasItem.put(alias, item);
            itemAlias.put(item, alias);
        }
    }
}