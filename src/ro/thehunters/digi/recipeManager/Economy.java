package ro.thehunters.digi.recipeManager;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.earth2me.essentials.Essentials;
import com.iCo6.iConomy;
import com.iCo6.system.Accounts;

public class Economy
{
    private boolean enabled = false;
    private net.milkbowl.vault.economy.Economy vault = null;
    private com.iCo6.system.Accounts iConomy = null;
    private boolean essentials = false;
    
    protected Economy()
    {
        Plugin plugin;
        
        if((plugin = Bukkit.getPluginManager().getPlugin("Vault")) instanceof Vault)
        {
            RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> service = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            
            if(service != null)
            {
                vault = service.getProvider();
                
                if(vault != null)
                {
                    if(vault.isEnabled())
                    {
                        Messages.log("Vault detected and connected to " + vault.getName() + ", economy features available.");
                    }
                    else
                    {
                        vault = null;
                        Messages.log("Vault detected but does not have an economy plugin connected, economy features are not available.");
                    }
                }
            }
        }
        else if((plugin = Bukkit.getPluginManager().getPlugin("iConomy")) instanceof iConomy)
        {
            iConomy = new Accounts();
            
            if(iConomy != null)
            {
                Messages.log("iConomy detected and connected, economy features are available.");
            }
        }
        else if((plugin = Bukkit.getPluginManager().getPlugin("Essentials")) instanceof Essentials)
        {
            if(plugin.isEnabled())
            {
                essentials = true;
                Messages.log("Essentials detected and connected, economy features are available.");
            }
            else
            {
                Messages.log("Essentials detected but it's not enabled, economy features are not available.");
            }
        }
        
        enabled = (essentials || vault != null || iConomy != null);
        
        if(!enabled)
        {
            Messages.log("Vault nor iConomy nor EssentialsEco was not found, economy features are not available.");
            clear();
        }
    }
    
    protected void clear()
    {
        enabled = false;
        vault = null;
        iConomy = null;
        essentials = false;
    }
    
    /**
     * Checks if you can use economy methods.
     * 
     * @return true if economy plugin detected, false otherwise
     */
    public boolean isEnabled()
    {
        return enabled;
    }
    
    /**
     * Gets the format of the money, defined by the economy plugin used.<br>
     * If economy is not enabled this method will return null.
     * 
     * @param amount
     *            money amount to format
     * @return String with formatted money
     */
    public String getFormat(double amount)
    {
        if(!isEnabled())
        {
            return null;
        }
        
        if(vault != null)
        {
            return vault.format(amount);
        }
        else if(iConomy != null)
        {
            return com.iCo6.iConomy.format(amount);
        }
        else if(essentials)
        {
            return com.earth2me.essentials.api.Economy.format(amount);
        }
        
        return null;
    }
    
    /**
     * Gets how much money a player has.<br>
     * If economy is not enabled this method will return 0.
     * 
     * @param playerName
     *            player's name
     * @return money player has, 0 if no economy plugin was found
     */
    public double getMoney(String playerName)
    {
        if(!isEnabled())
        {
            return 0;
        }
        
        if(vault != null)
        {
            return vault.getBalance(playerName);
        }
        else if(iConomy != null)
        {
            return iConomy.get(playerName).getHoldings().getBalance();
        }
        else if(essentials)
        {
            if(com.earth2me.essentials.api.Economy.playerExists(playerName))
            {
                try
                {
                    return com.earth2me.essentials.api.Economy.getMoney(playerName);
                }
                catch(Throwable e)
                {
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Give or take money.<br>
     * Use negative values to take money.<br>
     * If economy is not enabled or amount is 0, this method won't do anything
     * 
     * @param playerName
     *            player's name
     * @param amount
     *            amount to give
     */
    public void modMoney(String playerName, double amount)
    {
        if(!isEnabled() || amount == 0)
        {
            return;
        }
        
        if(vault != null)
        {
            EconomyResponse error;
            
            if(amount > 0)
            {
                error = vault.depositPlayer(playerName, amount);
            }
            else
            {
                error = vault.withdrawPlayer(playerName, Math.abs(amount));
            }
            
            if(error != null && !error.transactionSuccess())
            {
                Messages.info("<red>Economy error: " + error.errorMessage);
            }
        }
        else if(iConomy != null)
        {
            if(amount > 0)
            {
                iConomy.get(playerName).getHoldings().add(amount);
            }
            else
            {
                iConomy.get(playerName).getHoldings().subtract(Math.abs(amount));
            }
        }
        else if(essentials)
        {
            if(com.earth2me.essentials.api.Economy.playerExists(playerName))
            {
                try
                {
                    if(amount > 0)
                    {
                        com.earth2me.essentials.api.Economy.add(playerName, amount);
                    }
                    else
                    {
                        com.earth2me.essentials.api.Economy.subtract(playerName, Math.abs(amount));
                    }
                }
                catch(Throwable e)
                {
                }
            }
        }
    }
}
