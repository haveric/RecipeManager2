package ro.thehunters.digi.recipeManager;

import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Checks the latest uploaded version of this plugin.
 */
public class UpdateChecker extends BukkitRunnable
{
    private static final String URL_PLUGIN = "http://dev.bukkit.org/server-mods/recipemanager/";
    private static final String URL_FILES = URL_PLUGIN + "files";
    private static final String URL_FILES_RSS = URL_FILES + ".rss";
    private static UpdateChecker instance;
    private static String newVersion;
    private static String newLink;
    private CommandSender sender;
    
    protected static void init()
    {
    }
    
    /**
     * Constructor for calling update check one time only
     * 
     * @param sender
     *            who to send messages to, use null for server console
     */
    public UpdateChecker(CommandSender sender)
    {
        this.sender = sender;
        runTaskAsynchronously(RecipeManager.getPlugin());
    }
    
    /**
     * Constructor for private repeating instance
     */
    private UpdateChecker()
    {
        instance = this;
        
        int time = RecipeManager.getSettings().UPDATE_CHECK_FREQUENCY;
        
        if(time == 0)
        {
            stop();
            return;
        }
        
        time *= 60 * 60 * 20;
        runTaskTimerAsynchronously(RecipeManager.getPlugin(), time, time); // auto-update check every 12 hours
    }
    
    protected static void clean()
    {
        newVersion = null;
        newLink = null;
        stop();
    }
    
    /**
     * (Re)Start checker schedule
     */
    protected static void start()
    {
        stop();
        new UpdateChecker();
    }
    
    /**
     * Stop if started
     */
    protected static void stop()
    {
        if(instance != null)
        {
            instance.cancel();
            instance = null;
        }
    }
    
    protected static String getNewVersion()
    {
        return newVersion;
    }
    
    protected static String getNewLink()
    {
        return newLink;
    }
    
    @Override
    public void run()
    {
        if(!getLatestVersion())
        {
            newVersion = null;
            newLink = null;
        }
        
        if(!RecipeManager.isPluginFullyEnabled())
        {
            return;
        }
        
        if(newVersion == null)
        {
            if(sender != null) // send this message only if it's a requested update check
            {
                Messages.sendAndLog(sender, "<dark_red>Unable to check for updates, please check manually by visiting: " + URL_FILES);
            }
            else
            {
                return; // block the disable message
            }
        }
        else
        {
            if(RecipeManager.getPlugin() == null)
            {
                return;
            }
            
            String currentVersion = RecipeManager.getPlugin().getDescription().getVersion();
            
            if(currentVersion.equalsIgnoreCase(newVersion))
            {
                if(sender != null) // send this message only if it's a requested update check
                {
                    Messages.sendAndLog(sender, "<gray>Using the latest version: " + newVersion);
                }
                else
                {
                    return; // block the disable message
                }
            }
            else
            {
                Messages.sendAndLog(sender, "New version: <green>" + newVersion + "<reset> ! You're using <yellow>" + currentVersion);
                Messages.sendAndLog(sender, "Grab it at: <green>" + newLink);
            }
        }
        
        if(sender == null)
        {
            Messages.sendAndLog(sender, "<gray>You can disable this check from config.yml.");
        }
    }
    
    private boolean getLatestVersion()
    {
        try
        {
            // Credits to Vault for simple files.rss reading technique
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new URL(URL_FILES_RSS).openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("item");
            Node node = nodes.item(0);
            
            if(node.getNodeType() != Node.ELEMENT_NODE)
            {
                return false;
            }
            
            nodes = ((Element)node).getElementsByTagName("title");
            nodes = ((Element)nodes.item(0)).getChildNodes();
            String version = nodes.item(0).getNodeValue();
            
            newVersion = version.substring(1);
            
            nodes = ((Element)node).getElementsByTagName("link");
            nodes = ((Element)nodes.item(0)).getChildNodes();
            newLink = nodes.item(0).getNodeValue();
            
            return true;
        }
        catch(Throwable e)
        {
        }
        
        return false;
    }
}
