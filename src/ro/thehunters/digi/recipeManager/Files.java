package ro.thehunters.digi.recipeManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.flags.FlagType.Bit;

import com.google.common.io.CharStreams;

public class Files
{
    public static final String  NL                 = System.getProperty("line.separator");
    public static final String  PAD1               = "  ";
    public static final String  PAD2               = "    ";
    public static final String  PAD3               = "      ";
    
    private final CommandSender sender;
    private final String        DIR_PLUGIN         = RecipeManager.getPlugin().getDataFolder() + File.separator;
    
    protected static String     LASTCHANGED_CONFIG;
    protected static String     LASTCHANGED_README;
    protected static String     LASTCHANGED_ALIASES;
    protected static String     LASTCHANGED_MESSAGES;
    
    public static final String  FILE_USED_VERSION  = "used.version";
    
    public static final String  FILE_INFO_BASICS   = "info - basic recipes.txt";
    public static final String  FILE_INFO_COMMANDS = "info - commands & permissions.txt";
    public static final String  FILE_INFO_NAMES    = "info - name index.txt";
    public static final String  FILE_INFO_QA       = "info - questions-answers.txt";
    public static final String  FILE_INFO_FLAGS    = "info - recipe flags.html";
    public static final String  FILE_INFO_ERRORS   = "info - recipe errors in detail.txt";
    
    protected static void init()
    {
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(RecipeManager.getPlugin().getResource("plugin.yml"));
        
        LASTCHANGED_CONFIG = cfg.getString("lastchanged.config");
        LASTCHANGED_README = cfg.getString("lastchanged.readme");
        LASTCHANGED_ALIASES = cfg.getString("lastchanged.aliases");
        LASTCHANGED_MESSAGES = cfg.getString("lastchanged.messages");
    }
    
    protected static void reload(CommandSender sender)
    {
        new Files(sender);
    }
    
    private Files(CommandSender sender)
    {
        this.sender = sender;
        
        // TODO check versions...
        
        // TODO extract and overwrite changelog.txt too - check 4th line first
        
        createDirectories();
        
        if(isNewVersion())
        {
            createRecipeFlags();
            createNameIndex();
            
            Messages.info("<gray>New version installed, information files have been written.");
        }
    }
    
    private boolean isNewVersion()
    {
        boolean newVersion = true;
        
        try
        {
            File file = new File(DIR_PLUGIN + FILE_USED_VERSION);
            String currentVersion = RecipeManager.getPlugin().getDescription().getVersion();
            
            if(file.exists())
            {
                BufferedReader b = new BufferedReader(new FileReader(file));
                String version = b.readLine();
                b.close();
                newVersion = (version == null || !version.equals(currentVersion));
            }
            
            if(newVersion || file.exists())
            {
                BufferedWriter b = new BufferedWriter(new FileWriter(file, false));
                b.write(currentVersion);
                b.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return newVersion;
    }
    
    private void createDirectories()
    {
        // Create base directories
        File file = new File(DIR_PLUGIN + "recipes" + File.separator + "disabled");
        file.mkdirs();
        
        // Create base info files
        file = new File(file.getPath() + File.separator + "Place recipe files here to prevent them from beeing loaded");
        
        if(!file.exists())
        {
            Tools.saveTextToFile("In the disabled folder you can place recipe files you don't want to load, instead of deleting them.", file.getPath());
        }
    }
    
    private String getTemplate(String name)
    {
        InputStream stream = getClass().getResourceAsStream("resources/templates/" + name + ".txt");
        
        if(stream != null)
        {
            try
            {
                InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
                
                String string = CharStreams.toString(reader);
                
                reader.close();
                
                return string;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    private void createRecipeFlags()
    {
        StringBuilder s = new StringBuilder();
        String tpl;
        boolean first = true;
        
        s.append(getTemplate("flags_header"));
        
        s.append(NL).append("==========================================================================================");
        s.append(NL).append(" SHARED FLAGS");
        s.append(NL).append("  Usable on anything - file header, recipe header or result items.");
        s.append(NL).append("==========================================================================================");
        s.append(NL).append(NL);
        
        for(FlagType flag : FlagType.values())
        {
            if(flag.hasBit(Bit.RECIPE) || flag.hasBit(Bit.RESULT))
            {
                continue;
            }
            
            if(first)
            {
                first = false;
            }
            else
            {
                s.append(NL).append(NL);
                s.append(NL).append(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
                s.append(NL).append(NL);
            }
            
            tpl = getTemplate("flag_" + flag.getName());
            
            if(tpl == null)
            {
                s.append("(resources/templates/flag_" + flag.getName() + ".txt not found)");
                continue;
            }
            
            tpl = tpl.replace("{flag}", flag.toString());
            tpl = tpl.replace("{aliases}", Tools.listToString(Arrays.asList(flag.getNames()), ", ", "@"));
            
            s.append(tpl);
        }
        
        /*
        if(s != null)
        {
            s = s.replace("{flag}", "@" + "test");
            s = s.replace("{aliases}", Tools.convertListToString(Arrays.asList(args), ", ", "@"));
        }
        */
        
        /*
        StringBuilder s = new StringBuilder("<pre style=\"font-family:Verdana\">Information about recipe flags.");
        s.append(NL);
        s.append(NL).append("WHAT ARE FLAGS ?");
        s.append(NL).append("  Flags are the stuff that make a recipe very special ! You can add various features to a recipe by using flags.");
        s.append(NL);
        s.append(NL).append("USING FLAGS");
        s.append(NL).append("  Flags can be added in 3 'zones':");
        s.append(NL).append("  - at the begining of the file - which are copied to all recipes from that file");
        s.append(NL).append("  - after recipe type (CRAFT, COMBINE, etc) - where they affect that specific recipe, you may even overwrite file flags for that specific recipe!");
        s.append(NL).append("  - after recipe's individual results - to apply flags for the result items.");
        s.append(NL);
        s.append(NL).append("ABOUT ARGUMENTS");
        s.append(NL).append("  Flags have arguments but not always are they all required.");
        s.append(NL).append("  Arguments enclosed between &lt; and &gt; are required and those enclosed between [ and ] are optional.");
        s.append(NL).append("  Some arguments may have 'or false', that means you can just type false in there to make it do something special (most likely disable the flag or a feature)");
        s.append(NL);
        s.append(NL).append("ALIASES");
        s.append(NL).append("  They're just other names for the flag that you can use, they have no special effect if used, only for your preference.");
        s.append(NL);
        s.append(NL).append("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        s.append(NL);
        s.append(NL).append(String.format(" %-5s %-24s %-5s %s", "ID", "Name", "Stack", "Durability"));
        
        s.append(NL).append("</pre>");
        */
        
        Tools.saveTextToFile(s.toString(), DIR_PLUGIN + FILE_INFO_FLAGS);
        
        Messages.send(sender, ChatColor.GREEN + "Generated '" + FILE_INFO_FLAGS + "' file.");
    }
    
    private void createNameIndex()
    {
        StringBuilder s = new StringBuilder("List of name constants");
        s.append(NL).append("Data extracted from your server and it may contain names added by other plugins/mods !");
        s.append(NL).append("If you want to update this file just delete it and use 'rmreload' in server console or just start the server.");
        s.append(NL);
        s.append(NL).append("Item data/damage values are listed on Minecraft wiki: http://www.minecraftwiki.net/wiki/Data_value#Data");
        s.append(NL);
        s.append(NL);
        s.append(NL).append("MATERIAL LIST:");
        s.append(NL);
        s.append(NL).append(String.format(" %-5s %-24s %-5s %s", "ID", "Name", "Stack", "Durability"));
        
        for(Material m : Material.values())
        {
            s.append(NL).append(String.format(" %-5d %-24s %-5d %s", m.getId(), m.toString(), m.getMaxStackSize(), m.getMaxDurability()));
        }
        
        s.append(NL);
        s.append(NL);
        s.append(NL).append("ENCHANTMENTS LIST:");
        s.append(NL);
        s.append(NL).append(String.format(" %-5s %-26s %-12s %s", "ID", "Name", "Item type", "Level range"));
        
        List<Enchantment> enchantments = Arrays.asList(Enchantment.values());
        
        Collections.sort(enchantments, new Comparator<Enchantment>()
        {
            @Override
            public int compare(Enchantment e1, Enchantment e2)
            {
                return (e1.getId() > e2.getId() ? 1 : -1);
            }
        });
        
        for(Enchantment e : enchantments)
        {
            s.append(NL).append(String.format(" %-5d %-26s %-12s %s", e.getId(), e.getName(), e.getItemTarget().toString(), e.getStartLevel() + " to " + e.getMaxLevel()));
        }
        
        s.append(NL);
        s.append(NL);
        s.append(NL).append("POTION TYPE NAME LIST:");
        s.append(NL);
        s.append(NL).append(String.format(" %-5s %-24s %-10s %-10s %-16s %s", "ID", "Name", "Instant ?", "Max level", "Effect type", "Data value"));
        
        for(PotionType t : PotionType.values())
        {
            if(t != null)
                s.append(NL).append(String.format(" %-5d %-24s %-10s %-10d %-16s %d", t.ordinal(), t.toString(), t.isInstant(), t.getMaxLevel(), (t.getEffectType() == null ? "" : t.getEffectType().getName()), t.getDamageValue()));
        }
        
        s.append(NL);
        s.append(NL);
        s.append(NL).append("POTION EFFECT TYPE NAME LIST:");
        s.append(NL);
        s.append(NL).append(String.format(" %-5s %-24s %-10s %s", "ID", "Name", "Instant ?", "Duration modifier"));
        
        for(PotionEffectType t : PotionEffectType.values())
        {
            if(t != null)
                s.append(NL).append(String.format(" %-5d %-24s %-10s %f", t.getId(), t.getName(), t.isInstant(), t.getDurationModifier()));
        }
        
        s.append(NL);
        s.append(NL).append("More about potions, effects and custom effects: http://www.minecraftwiki.net/wiki/Potion_effects");
        s.append(NL);
        s.append(NL);
        s.append(NL).append("FIREWORK EFFECT TYPE NAME LIST:");
        s.append(NL);
        
        for(FireworkEffect.Type t : FireworkEffect.Type.values())
        {
            s.append(NL).append(" ").append(t.toString());
        }
        
        s.append(NL);
        s.append(NL);
        s.append(NL).append("SOUND NAME LIST:");
        s.append(NL);
        
        Sound[] sounds = Sound.values();
        
        for(int i = 0; i < sounds.length; i += 4)
        {
            s.append(NL).append(String.format(" %-24s%-24s%-24s%s", sounds[i].name(), (i + 1 < sounds.length ? sounds[i + 1].name() : ""), (i + 2 < sounds.length ? sounds[i + 2].name() : ""), (i + 3 < sounds.length ? sounds[i + 3].name() : "")));
        }
        
        s.append(NL);
        s.append(NL);
        s.append(NL).append("CHAT COLOR NAMES LIST:");
        s.append(NL);
        s.append(NL).append(String.format(" %-16s %s", "Name", "Color character"));
        
        for(ChatColor c : ChatColor.values())
        {
            s.append(NL).append(String.format(" %-16s %s", c.name(), c.getChar()));
        }
        
        s.append(NL);
        s.append(NL);
        s.append(NL).append("BukkitAPI for these names:");
        s.append(NL).append("Item names: http://jd.bukkit.org/rb/apidocs/org/bukkit/Material.html");
        s.append(NL).append("Enchantments: http://jd.bukkit.org/rb/apidocs/org/bukkit/enchantments/Enchantment.html");
        s.append(NL).append("Potion types: http://jd.bukkit.org/rb/apidocs/org/bukkit/potion/PotionType.html");
        s.append(NL).append("Potion effect types: http://jd.bukkit.org/rb/apidocs/org/bukkit/potion/PotionEffect.html");
        s.append(NL).append("Firework effect types: http://jd.bukkit.org/rb/apidocs/org/bukkit/FireworkEffect.Type.html");
        s.append(NL).append("ChatColors: http://jd.bukkit.org/rb/apidocs/org/bukkit/ChatColor.html");
        s.append(NL);
        s.append(NL).append("EOF");
        
        Tools.saveTextToFile(s.toString(), DIR_PLUGIN + FILE_INFO_NAMES);
        
        Messages.send(sender, ChatColor.GREEN + "Generated '" + FILE_INFO_NAMES + "' file.");
    }
}