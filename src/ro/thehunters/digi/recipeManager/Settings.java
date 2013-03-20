package ro.thehunters.digi.recipeManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * RecipeManager's settings loaded from its config.yml, values are read-only.
 */
public class Settings
{
    public final boolean                        SPECIAL_REPAIR;
    public final boolean                        SPECIAL_REPAIR_METADATA;
    
    public final boolean                        SPECIAL_LEATHER_DYE;
    public final boolean                        SPECIAL_FIREWORKS;
    public final boolean                        SPECIAL_MAP_CLONING;
    public final boolean                        SPECIAL_MAP_EXTENDING;
    
    public final boolean                        SOUNDS_REPAIR;
    public final boolean                        SOUNDS_FAILED;
    public final boolean                        SOUNDS_FAILED_CLICK;
    
    public final boolean                        UPDATE_BOOKS;
    public final boolean                        COLOR_CONSOLE;
    
    public final boolean                        RETURN_BUCKETS;
    public final boolean                        RETURN_POTIONS;
    public final boolean                        RETURN_BOWL;
    
    public final boolean                        FUEL_RETURN_BUCKETS;
    
    public final char                           FURNACE_SHIFT_CLICK;
    public final int                            FURNACE_TICKS;
    
    public final boolean                        MULTITHREADING;
    
    public final boolean                        CLEAR_RECIPES;
    
    public final boolean                        UPDATE_CHECK_ENABLED;
    public final int                            UPDATE_CHECK_FREQUENCY;
    
    public final boolean                        METRICS;
    
    protected final String                      LASTCHANGED;
    
    protected Map<String, Material>             nameAliases = new HashMap<String, Material>();
    protected Map<Material, Map<String, Short>> dataAliases = new HashMap<Material, Map<String, Short>>();
    
    protected Map<Material, String>             printName   = new HashMap<Material, String>();
    protected Map<Material, Map<Short, String>> printData   = new HashMap<Material, Map<Short, String>>();
    
    public static void reload(CommandSender sender)
    {
        new Settings(sender);
    }
    
    private Settings(CommandSender sender)
    {
        RecipeManager.settings = this;
        
        // Load/reload/generate config.yml
        FileConfiguration yml = loadYML(sender, "config.yml");
        
        RecipeManager.plugin.reloadConfig();
        
        SPECIAL_REPAIR = yml.getBoolean("special-recipes.repair", true);
        SPECIAL_REPAIR_METADATA = yml.getBoolean("special-recipes.repair-metadata", false);
        
        SPECIAL_LEATHER_DYE = yml.getBoolean("special-recipes.leather-armor-dye", true);
        SPECIAL_FIREWORKS = yml.getBoolean("special-recipes.fireworks", true);
        SPECIAL_MAP_CLONING = yml.getBoolean("special-recipes.map-cloning", true);
        SPECIAL_MAP_EXTENDING = yml.getBoolean("special-recipes.map-extending", true);
        
        SOUNDS_REPAIR = yml.getBoolean("sounds.repair", true);
        SOUNDS_FAILED = yml.getBoolean("sounds.failed", true);
        SOUNDS_FAILED_CLICK = yml.getBoolean("sounds.failed_click", true);
        
        UPDATE_BOOKS = yml.getBoolean("update-books", true);
        COLOR_CONSOLE = yml.getBoolean("color-console", true);
        
        RETURN_BUCKETS = yml.getBoolean("return-empty.buckets", true);
        RETURN_POTIONS = yml.getBoolean("return-empty.potions", true);
        RETURN_BOWL = yml.getBoolean("return-empty.bowl", true);
        
        FUEL_RETURN_BUCKETS = yml.getBoolean("fuel-return-buckets", true);
        FURNACE_SHIFT_CLICK = yml.getString("furnace-shift-click", "f").charAt(0);
        
        int ticks = yml.getInt("furnace-ticks", 1);
        
        if(ticks < 1 || ticks > 20)
        {
            Messages.send(sender, "<yellow>WARNING: <reset>config.yml's 'furnace-ticks' must be between 1 and 20");
        }
        
        FURNACE_TICKS = ticks;
        
        MULTITHREADING = yml.getBoolean("multithreading", true);
        
        CLEAR_RECIPES = yml.getBoolean("clear-recipes", false);
        
        UPDATE_CHECK_ENABLED = yml.getBoolean("update_check.enabled", true);
        UPDATE_CHECK_FREQUENCY = Math.max(yml.getInt("update_check.frequency", 6), 0);
        
        METRICS = yml.getBoolean("metrics", true);
        
        LASTCHANGED = yml.getString("lastchanged");
        
        if(!Files.LASTCHANGED_CONFIG.equals(LASTCHANGED))
        {
            Messages.send(sender, "<yellow>NOTE: <reset>config.yml file is outdated, please delete it to allow it to be generated again.");
        }
        
        Messages.log("config.yml settings:");
        Messages.log("    special-recipes.repair: " + SPECIAL_REPAIR);
        Messages.log("    special-recipes.repair-metadata: " + SPECIAL_REPAIR_METADATA);
        Messages.log("    special-recipes.leather-dye: " + SPECIAL_LEATHER_DYE);
        Messages.log("    special-recipes.fireworks: " + SPECIAL_FIREWORKS);
        Messages.log("    special-recipes.map-cloning: " + SPECIAL_MAP_CLONING);
        Messages.log("    special-recipes.map-extending: " + SPECIAL_MAP_EXTENDING);
        Messages.log("    update-books: " + UPDATE_BOOKS);
        Messages.log("    color-console: " + COLOR_CONSOLE);
        Messages.log("    return-empty.buckets: " + RETURN_BUCKETS);
        Messages.log("    return-empty.potions: " + RETURN_POTIONS);
        Messages.log("    return-empty.bowl: " + RETURN_BOWL);
        Messages.log("    fuel-return-buckets: " + FUEL_RETURN_BUCKETS);
        Messages.log("    furnace-shift-click: " + FURNACE_SHIFT_CLICK);
        Messages.log("    furnace-ticks: " + FURNACE_TICKS);
        Messages.log("    metrics: " + METRICS);
        
        yml = loadYML(sender, "aliases.yml");
        
        if(!Files.LASTCHANGED_CONFIG.equals(yml.get("lastchanged")))
        {
            Messages.send(sender, "<yellow>NOTE: <reset>aliases.yml file is outdated, please delete it to allow it to be generated again.");
        }
        
        for(String materialString : yml.getKeys(false))
        {
            if(materialString.equals("lastchanged"))
                continue;
            
            Material material = Material.matchMaterial(materialString);
            
            if(material == null)
            {
                Messages.info("<yellow>WARNING: <reset>aliases.yml has invalid material definition: " + materialString);
                continue;
            }
            
            Object value = yml.get(materialString);
            
            if(value instanceof String)
            {
                parseNames((String)value, material);
            }
            else if(value instanceof ConfigurationSection)
            {
                ConfigurationSection section = (ConfigurationSection)value;
                
                for(String key : section.getKeys(false))
                {
                    if(key.equals("names"))
                    {
                        parseNames(section.getString(key), material);
                    }
                    else
                    {
                        try
                        {
                            parseDataNames(section.getString(key), Short.valueOf(key), material);
                        }
                        catch(NumberFormatException e)
                        {
                            Messages.info("<yellow>WARNING: <reset>aliases.yml has invalid data value number: " + key + " for material: " + material);
                            continue;
                        }
                    }
                }
            }
            else
            {
                Messages.info("<yellow>WARNING: <reset>aliases.yml has invalid data type at: " + materialString);
                continue;
            }
        }
    }
    
    private void parseNames(String names, Material material)
    {
        if(names == null)
            return;
        
        String[] split = names.split(",");
        
        for(String str : split)
        {
            String parsed = Tools.parseAliasName(str);
            
            if(nameAliases.containsKey(parsed))
            {
                Messages.info("<yellow>WARNING: <reset>aliases.yml has duplicate material alias '" + str + "' for material " + material);
                continue;
            }
            
            nameAliases.put(parsed, material);
            
            if(!printName.containsKey(material))
            {
                printName.put(material, Tools.parseAliasPrint(str));
            }
        }
    }
    
    private void parseDataNames(String names, short data, Material material)
    {
        if(names == null)
            return;
        
        String[] split = names.split(",");
        
        for(String str : split)
        {
            Map<String, Short> dataMap = dataAliases.get(material);
            
            if(dataMap == null)
            {
                dataMap = new HashMap<String, Short>();
                dataAliases.put(material, dataMap);
            }
            
            String parsed = Tools.parseAliasName(str);
            
            if(dataMap.containsKey(parsed))
            {
                Messages.info("<yellow>WARNING: <reset>aliases.yml has duplicate data alias '" + str + "' for material " + material + " and data value " + data);
                continue;
            }
            
            dataMap.put(parsed, data);
            
            Map<Short, String> printMap = printData.get(material);
            
            if(printMap == null)
            {
                printMap = new HashMap<Short, String>();
                printData.put(material, printMap);
            }
            
            if(!printMap.containsKey(data))
            {
                printMap.put(data, Tools.parseAliasPrint(str));
            }
        }
    }
    
    private FileConfiguration loadYML(CommandSender sender, String fileName)
    {
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + fileName);
        
        if(!file.exists())
        {
            RecipeManager.getPlugin().saveResource(fileName, false);
            Messages.log("Generated and loaded '" + fileName + "' file.");
        }
        else
        {
            Messages.log("Loaded '" + fileName + "' file.");
        }
        
        return YamlConfiguration.loadConfiguration(file);
    }
}