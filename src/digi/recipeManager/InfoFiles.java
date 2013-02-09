package digi.recipeManager;

import java.io.File;
import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

public class InfoFiles
{
    // constants
    private final CommandSender sender;
    private final String        DIR_PLUGIN      = RecipeManager.getPlugin().getDataFolder() + File.separator;
    private final String        NL              = System.getProperty("line.separator");
    
//    private final String        FILE_VERSION    = "version";
    
    // public constants
    public static final String  FILE_INFOBASICS = "info - basic recipes.txt";
    public static final String  FILE_INFOITEMS  = "info - items, enchants, colors.txt";
    public static final String  FILE_INFOQA     = "info - questions-answers.txt";
    public static final String  FILE_INFOFLAGS  = "info - recipe flags.txt";
    public static final String  FILE_INFOERRORS = "info - recipe errors in detail.txt";
    
    public InfoFiles(CommandSender sender)
    {
        this.sender = sender;
        
        // TODO check FILE_VERSION...
        
        fileItemData(false);
    }
    
    private void fileItemData(boolean overwrite)
    {
        File file = new File(DIR_PLUGIN + FILE_INFOITEMS);
        
        if(!overwrite && file.exists())
            return;
        
        StringBuilder buffer = new StringBuilder("List of item, enchantment and chatcolor names.");
        buffer.append(NL).append("Data extracted from your server and it may contain names added by other plugins/mods !");
        buffer.append(NL).append("If you want to update this file just delete it and use 'rmreload' in server console or just start the server.");
        buffer.append(NL);
        buffer.append(NL).append("Item data/damage values are listed on Minecraft wiki: http://www.minecraftwiki.net/wiki/Data_value#Data");
        buffer.append(NL);
        buffer.append(NL);
        buffer.append(NL).append("MATERIAL LIST:");
        buffer.append(NL);
        buffer.append(NL).append(String.format(" %-5s %-24s %-5s %s", "ID#", "Name", "Stack", "Durability"));
        
        for(Material m : Material.values())
        {
            buffer.append(NL).append(String.format(" %-5d %-24s %-5d %s", m.getId(), m.toString(), m.getMaxStackSize(), m.getMaxDurability()));
        }
        
        buffer.append(NL);
        buffer.append(NL);
        buffer.append(NL).append("ENCHANTMENTS LIST:");
        buffer.append(NL);
        buffer.append(NL).append(String.format(" %-5s %-26s %-12s %s", "ID#", "Name", "Item type", "Level range"));
        
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
        buffer.append(NL).append("CHAT COLOR NAMES LIST:");
        buffer.append(NL);
        buffer.append(NL).append(String.format(" %-16s %s", "Name", "Color character"));
        
        List<ChatColor> colors = Arrays.asList(ChatColor.values());
        
        for(ChatColor c : colors)
        {
            buffer.append(NL).append(String.format(" %-16s %s", c.toString(), c.getChar()));
        }
        
        buffer.append(NL);
        buffer.append(NL);
        buffer.append(NL).append("BukkitAPI for these names:");
        buffer.append(NL).append("Item names: http://jd.bukkit.org/rb/apidocs/org/bukkit/Material.html");
        buffer.append(NL).append("Enchantments: http://jd.bukkit.org/rb/apidocs/org/bukkit/enchantments/Enchantment.html");
        buffer.append(NL).append("ChatColors: http://jd.bukkit.org/rb/apidocs/org/bukkit/ChatColor.html");
        buffer.append(NL);
        buffer.append(NL).append("EOF");
        
        Tools.saveTextToFile(buffer.toString(), DIR_PLUGIN + FILE_INFOITEMS);
        
        Messages.send(sender, ChatColor.GREEN + "Generated '" + FILE_INFOITEMS + "' file.");
    }
}