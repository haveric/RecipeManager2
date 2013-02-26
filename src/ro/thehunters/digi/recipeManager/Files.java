package ro.thehunters.digi.recipeManager;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class Files
{
    private final CommandSender   sender;
    private final String          DIR_PLUGIN       = RecipeManager.getPlugin().getDataFolder() + File.separator;
    private final String          NL               = System.getProperty("line.separator");
    
    protected static final String LASTCHANGED_CONFIG;
    protected static final String LASTCHANGED_README;
    protected static final String LASTCHANGED_ALIASES;
    protected static final String LASTCHANGED_MESSAGES;
    
    protected static final String FILE_INFO_BASICS = "info - basic recipes.txt";
    protected static final String FILE_INFO_NAMES  = "info - names.txt";
    protected static final String FILE_INFO_QA     = "info - questions-answers.txt";
    protected static final String FILE_INFO_FLAGS  = "info - recipe flags.html";
    protected static final String FILE_INFO_ERRORS = "info - recipe errors in detail.txt";
    
    static
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
        
        fileRecipeFlags();
        fileNames();
    }
    
    private void fileRecipeFlags()
    {
        File file = new File(DIR_PLUGIN + FILE_INFO_FLAGS);
        
        if(file.exists())
            return;
        
        StringBuilder buffer = new StringBuilder("<pre style=\"font-family:Verdana\">Information about recipe flags.");
        
        buffer.append(NL).append("WHAT ARE FLAGS ?");
        buffer.append(NL).append("  Flags are the stuff that make a recipe very special ! You can add various features to a recipe by using flags.");
        buffer.append(NL);
        buffer.append(NL).append("USING FLAGS");
        buffer.append(NL).append("  Flags can be added in 3 'zones':");
        buffer.append(NL).append("  - at the begining of the file - which are copied to all recipes from that file");
        buffer.append(NL).append("  - after recipe type (CRAFT, COMBINE, etc) - where they affect that specific recipe, you may even overwrite file flags for that specific recipe!");
        buffer.append(NL).append("  - after recipe's individual results - to apply flags for the result items.");
        buffer.append(NL);
        buffer.append(NL).append("ABOUT ARGUMENTS");
        buffer.append(NL).append("  Flags have arguments but not always are they all required.");
        buffer.append(NL).append("  Arguments enclosed between &lt; and &gt; are required and those enclosed between [ and ] are optional.");
        buffer.append(NL).append("  Some arguments may have 'or false', that means you can just type false in there to make it do something special (most likely disable the flag or a feature)");
        buffer.append(NL);
        buffer.append(NL).append("ALIASES");
        buffer.append(NL).append("  They're just other names for the flag that you can use, they have no special effect if used, only for your preference.");
        buffer.append(NL);
        buffer.append(NL).append("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        buffer.append(NL);
        buffer.append(NL).append(String.format(" %-5s %-24s %-5s %s", "ID", "Name", "Stack", "Durability"));
        
        buffer.append(NL).append("</pre>");
        
        Tools.saveTextToFile(buffer.toString(), DIR_PLUGIN + FILE_INFO_FLAGS);
        
        Messages.send(sender, ChatColor.GREEN + "Generated '" + FILE_INFO_FLAGS + "' file.");
    }
    
    private void fileNames()
    {
        File file = new File(DIR_PLUGIN + FILE_INFO_NAMES);
        
        if(file.exists())
            return;
        
        StringBuilder buffer = new StringBuilder("List of name constants");
        buffer.append(NL).append("Data extracted from your server and it may contain names added by other plugins/mods !");
        buffer.append(NL).append("If you want to update this file just delete it and use 'rmreload' in server console or just start the server.");
        buffer.append(NL);
        buffer.append(NL).append("Item data/damage values are listed on Minecraft wiki: http://www.minecraftwiki.net/wiki/Data_value#Data");
        buffer.append(NL);
        buffer.append(NL);
        buffer.append(NL).append("MATERIAL LIST:");
        buffer.append(NL);
        buffer.append(NL).append(String.format(" %-5s %-24s %-5s %s", "ID", "Name", "Stack", "Durability"));
        
        for(Material m : Material.values())
        {
            buffer.append(NL).append(String.format(" %-5d %-24s %-5d %s", m.getId(), m.toString(), m.getMaxStackSize(), m.getMaxDurability()));
        }
        
        buffer.append(NL);
        buffer.append(NL);
        buffer.append(NL).append("ENCHANTMENTS LIST:");
        buffer.append(NL);
        buffer.append(NL).append(String.format(" %-5s %-26s %-12s %s", "ID", "Name", "Item type", "Level range"));
        
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
            buffer.append(NL).append(String.format(" %-5d %-26s %-12s %s", e.getId(), e.getName(), e.getItemTarget().toString(), e.getStartLevel() + " to " + e.getMaxLevel()));
        }
        
        buffer.append(NL);
        buffer.append(NL);
        buffer.append(NL).append("POTION TYPE NAME LIST:");
        buffer.append(NL);
        buffer.append(NL).append(String.format(" %-5s %-24s %-10s %-10s %-16s %s", "ID", "Name", "Instant ?", "Max level", "Effect type", "Data value"));
        
        for(PotionType t : PotionType.values())
        {
            if(t != null)
                buffer.append(NL).append(String.format(" %-5d %-24s %-10s %-10d %-16s %d", t.ordinal(), t.toString(), t.isInstant(), t.getMaxLevel(), (t.getEffectType() == null ? "" : t.getEffectType().getName()), t.getDamageValue()));
        }
        
        buffer.append(NL);
        buffer.append(NL);
        buffer.append(NL).append("POTION EFFECT TYPE NAME LIST:");
        buffer.append(NL);
        buffer.append(NL).append(String.format(" %-5s %-24s %-10s %s", "ID", "Name", "Instant ?", "Duration modifier"));
        
        for(PotionEffectType t : PotionEffectType.values())
        {
            if(t != null)
                buffer.append(NL).append(String.format(" %-5d %-24s %-10s %f", t.getId(), t.getName(), t.isInstant(), t.getDurationModifier()));
        }
        
        buffer.append(NL);
        buffer.append(NL).append("More about potions, effects and custom effects: http://www.minecraftwiki.net/wiki/Potion_effects");
        buffer.append(NL);
        buffer.append(NL);
        buffer.append(NL).append("FIREWORK EFFECT TYPE NAME LIST:");
        buffer.append(NL);
        
        for(FireworkEffect.Type t : FireworkEffect.Type.values())
        {
            buffer.append(NL).append(" ").append(t.toString());
        }
        
        buffer.append(NL);
        buffer.append(NL);
        buffer.append(NL).append("CHAT COLOR NAMES LIST:");
        buffer.append(NL);
        buffer.append(NL).append(String.format(" %-16s %s", "Name", "Color character"));
        
        for(ChatColor c : ChatColor.values())
        {
            buffer.append(NL).append(String.format(" %-16s %s", c.name(), c.getChar()));
        }
        
        buffer.append(NL);
        buffer.append(NL);
        buffer.append(NL).append("BukkitAPI for these names:");
        buffer.append(NL).append("Item names: http://jd.bukkit.org/rb/apidocs/org/bukkit/Material.html");
        buffer.append(NL).append("Enchantments: http://jd.bukkit.org/rb/apidocs/org/bukkit/enchantments/Enchantment.html");
        buffer.append(NL).append("Potion types: http://jd.bukkit.org/rb/apidocs/org/bukkit/potion/PotionType.html");
        buffer.append(NL).append("Potion effect types: http://jd.bukkit.org/rb/apidocs/org/bukkit/potion/PotionEffect.html");
        buffer.append(NL).append("Firework effect types: http://jd.bukkit.org/rb/apidocs/org/bukkit/FireworkEffect.Type.html");
        buffer.append(NL).append("ChatColors: http://jd.bukkit.org/rb/apidocs/org/bukkit/ChatColor.html");
        buffer.append(NL);
        buffer.append(NL).append("EOF");
        
        Tools.saveTextToFile(buffer.toString(), DIR_PLUGIN + FILE_INFO_NAMES);
        
        Messages.send(sender, ChatColor.GREEN + "Generated '" + FILE_INFO_NAMES + "' file.");
    }
}