package ro.thehunters.digi.recipeManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

/**
 * RecipeManager's settings loaded from its config.yml, values are read-only.
 */
public class Settings
{
    public final boolean SPECIAL_REPAIR;
    public final boolean SPECIAL_REPAIR_METADATA;
    
    public final boolean SPECIAL_LEATHER_DYE;
    public final boolean SPECIAL_FIREWORKS;
    public final boolean SPECIAL_MAP_CLONING;
    public final boolean SPECIAL_MAP_EXTENDING;
    
    public final boolean SOUNDS_REPAIR;
    public final boolean SOUNDS_FAILED;
    public final boolean SOUNDS_FAILED_CLICK;
    
    public final boolean FIX_MOD_RESULTS;
    public final boolean UPDATE_BOOKS;
    public final boolean COLOR_CONSOLE;
    
    public final char FURNACE_SHIFT_CLICK;
    public final int FURNACE_TICKS;
    
    public final boolean MULTITHREADING;
    
    public final boolean CLEAR_RECIPES;
    
    public final boolean UPDATE_CHECK_ENABLED;
    public final int UPDATE_CHECK_FREQUENCY;
    
    public final boolean METRICS;
    
    protected final String LASTCHANGED;
    
    protected Map<String, Material> materialNames = new HashMap<String, Material>();
    protected Map<Material, Map<String, Short>> materialDataNames = new HashMap<Material, Map<String, Short>>();
    protected Map<String, Enchantment> enchantNames = new HashMap<String, Enchantment>();
    
    protected Map<Material, String> materialPrint = new HashMap<Material, String>();
    protected Map<Material, Map<Short, String>> materialDataPrint = new HashMap<Material, Map<Short, String>>();
    protected Map<Enchantment, String> enchantPrint = new HashMap<Enchantment, String>();
    
    public static void reload(CommandSender sender)
    {
        new Settings(sender);
    }
    
    private Settings(CommandSender sender)
    {
        RecipeManager.settings = this;
        
        // Load/reload/generate config.yml
        FileConfiguration yml = loadYML(Files.FILE_CONFIG);
        
        SPECIAL_REPAIR = yml.getBoolean("special-recipes.repair", true);
        SPECIAL_REPAIR_METADATA = yml.getBoolean("special-recipes.repair-metadata", false);
        
        SPECIAL_LEATHER_DYE = yml.getBoolean("special-recipes.leather-armor-dye", true);
        SPECIAL_FIREWORKS = yml.getBoolean("special-recipes.fireworks", true);
        SPECIAL_MAP_CLONING = yml.getBoolean("special-recipes.map-cloning", true);
        SPECIAL_MAP_EXTENDING = yml.getBoolean("special-recipes.map-extending", true);
        
        SOUNDS_REPAIR = yml.getBoolean("sounds.repair", true);
        SOUNDS_FAILED = yml.getBoolean("sounds.failed", true);
        SOUNDS_FAILED_CLICK = yml.getBoolean("sounds.failed_click", true);
        
        FIX_MOD_RESULTS = yml.getBoolean("fix-mod-results", false);
        UPDATE_BOOKS = yml.getBoolean("update-books", true);
        COLOR_CONSOLE = yml.getBoolean("color-console", true);
        
        FURNACE_SHIFT_CLICK = yml.getString("furnace-shift-click", "f").charAt(0);
        
        int ticks = yml.getInt("furnace-ticks", 1);
        
        if(ticks < 1 || ticks > 20)
        {
            Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_CONFIG + "' has invalid value for 'furnace-ticks', it must be between 1 and 20");
        }
        
        FURNACE_TICKS = ticks;
        
        MULTITHREADING = yml.getBoolean("multithreading", true);
        
        CLEAR_RECIPES = yml.getBoolean("clear-recipes", false);
        
        UPDATE_CHECK_ENABLED = yml.getBoolean("update-check.enabled", true);
        UPDATE_CHECK_FREQUENCY = Math.max(yml.getInt("update-check.frequency", 6), 0);
        
        METRICS = yml.getBoolean("metrics", true);
        
        LASTCHANGED = yml.getString("lastchanged");
        
        if(!Files.LASTCHANGED_CONFIG.equals(LASTCHANGED))
        {
            Messages.sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_CONFIG + "' file is outdated, please delete it to allow it to be generated again.");
        }
        
        // TODO fill all settings into these
        Messages.log("config.yml settings:");
        Messages.log("    special-recipes.repair: " + SPECIAL_REPAIR);
        Messages.log("    special-recipes.repair-metadata: " + SPECIAL_REPAIR_METADATA);
        Messages.log("    special-recipes.leather-dye: " + SPECIAL_LEATHER_DYE);
        Messages.log("    special-recipes.fireworks: " + SPECIAL_FIREWORKS);
        Messages.log("    special-recipes.map-cloning: " + SPECIAL_MAP_CLONING);
        Messages.log("    special-recipes.map-extending: " + SPECIAL_MAP_EXTENDING);
        Messages.log("    fix-mod-results: " + FIX_MOD_RESULTS);
        Messages.log("    update-books: " + UPDATE_BOOKS);
        Messages.log("    color-console: " + COLOR_CONSOLE);
        Messages.log("    furnace-shift-click: " + FURNACE_SHIFT_CLICK);
        Messages.log("    furnace-ticks: " + FURNACE_TICKS);
        Messages.log("    metrics: " + METRICS);
        
        yml = loadYML(Files.FILE_ITEM_ALIASES);
        
        if(!Files.LASTCHANGED_ITEM_ALIASES.equals(yml.get("lastchanged")))
        {
            Messages.sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_ITEM_ALIASES + "' file is outdated, please delete it to allow it to be generated again.");
        }
        
        /* TODO remove
        for(Material m : Material.values())
        {
            materialNames.put(String.valueOf(m.getId()), m);
            materialNames.put(Tools.parseAliasName(m.toString()), m);
            materialPrint.put(m, Tools.parseAliasPrint(m.toString()));
        }
        */
        
        for(String arg : yml.getKeys(false))
        {
            if(arg.equals("lastchanged"))
            {
                continue;
            }
            
            Material material = Material.matchMaterial(arg);
            
            if(material == null)
            {
                Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has invalid material definition: " + arg);
                continue;
            }
            
            Object value = yml.get(arg);
            
            if(value instanceof String)
            {
                parseMaterialNames(sender, (String)value, material);
            }
            else if(value instanceof ConfigurationSection)
            {
                ConfigurationSection section = (ConfigurationSection)value;
                
                for(String key : section.getKeys(false))
                {
                    if(key.equals("names"))
                    {
                        parseMaterialNames(sender, section.getString(key), material);
                    }
                    else
                    {
                        try
                        {
                            parseMaterialDataNames(sender, section.getString(key), Short.valueOf(key), material);
                        }
                        catch(NumberFormatException e)
                        {
                            Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has invalid data value number: " + key + " for material: " + material);
                            continue;
                        }
                    }
                }
            }
            else
            {
                Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has invalid data type at: " + arg);
                continue;
            }
        }
        
        yml = loadYML(Files.FILE_ENCHANT_ALIASES);
        
        if(!Files.LASTCHANGED_ENCHANT_ALIASES.equals(yml.get("lastchanged")))
        {
            Messages.sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_ENCHANT_ALIASES + "' file is outdated, please delete it to allow it to be generated again.");
        }
        
        /* TODO remove
        for(Enchantment e : Enchantment.values())
        {
            enchantNames.put(String.valueOf(e.getId()), e);
            enchantNames.put(Tools.parseAliasName(e.toString()), e);
            enchantPrint.put(e, Tools.parseAliasPrint(e.toString()));
        }
        */
        
        for(String arg : yml.getKeys(false))
        {
            if(arg.equals("lastchanged"))
            {
                continue;
            }
            
            Enchantment enchant = Enchantment.getByName(arg.toUpperCase());
            
            if(enchant == null)
            {
                Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ENCHANT_ALIASES + "' has invalid enchant definition: " + arg);
                continue;
            }
            
            String names = yml.getString(arg);
            String[] split = names.split(",");
            
            for(String str : split)
            {
                str = str.trim();
                String parsed = Tools.parseAliasName(str);
                
                if(enchantNames.containsKey(parsed))
                {
                    Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ENCHANT_ALIASES + "' has duplicate enchant alias '" + str + "' for enchant " + enchant);
                    continue;
                }
                
                enchantNames.put(parsed, enchant);
                
                if(!enchantPrint.containsKey(enchant))
                {
                    enchantPrint.put(enchant, Tools.parseAliasPrint(str));
                }
            }
        }
    }
    
    private void parseMaterialNames(CommandSender sender, String names, Material material)
    {
        if(names == null)
        {
            return;
        }
        
        String[] split = names.split(",");
        
        for(String str : split)
        {
            str = str.trim();
            String parsed = Tools.parseAliasName(str);
            
            if(materialNames.containsKey(parsed))
            {
                Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has duplicate material alias '" + str + "' for material " + material);
                continue;
            }
            
            materialNames.put(parsed, material);
            
            if(!materialPrint.containsKey(material))
            {
                materialPrint.put(material, Tools.parseAliasPrint(str));
            }
        }
    }
    
    private void parseMaterialDataNames(CommandSender sender, String names, short data, Material material)
    {
        if(names == null)
        {
            return;
        }
        
        String[] split = names.split(",");
        
        for(String str : split)
        {
            str = str.trim();
            Map<String, Short> dataMap = materialDataNames.get(material);
            
            if(dataMap == null)
            {
                dataMap = new HashMap<String, Short>();
                materialDataNames.put(material, dataMap);
            }
            
            String parsed = Tools.parseAliasName(str);
            
            if(dataMap.containsKey(parsed))
            {
                Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has duplicate data alias '" + str + "' for material " + material + " and data value " + data);
                continue;
            }
            
            dataMap.put(parsed, data);
            
            Map<Short, String> printMap = materialDataPrint.get(material);
            
            if(printMap == null)
            {
                printMap = new HashMap<Short, String>();
                materialDataPrint.put(material, printMap);
            }
            
            if(!printMap.containsKey(data))
            {
                printMap.put(data, Tools.parseAliasPrint(str));
            }
        }
    }
    
    private FileConfiguration loadYML(String fileName)
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
