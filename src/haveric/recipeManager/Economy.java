package haveric.recipeManager;

import haveric.recipeManager.uuidFetcher.UUIDFetcher;

import java.util.UUID;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Economy {
    private boolean enabled = false;
    private net.milkbowl.vault.economy.Economy vault = null;

    protected Economy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") instanceof Vault) {
            RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> service = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

            if (service != null) {
                vault = service.getProvider();

                if (vault != null) {
                    if (vault.isEnabled()) {
                        Messages.log("Vault detected and connected to " + vault.getName() + ", economy features available.");
                    } else {
                        vault = null;
                        Messages.log("Vault detected but does not have an economy plugin connected, economy features are not available.");
                    }
                }
            }
        }

        enabled = (vault != null);

        if (!enabled) {
            clear();
            Messages.log("Vault was not found, economy features are not available.");
        }
    }

    protected void clear() {
        enabled = false;
        vault = null;
    }

    /**
     * Checks if you can use economy methods.
     *
     * @return true if economy plugin detected, false otherwise
     */
    public boolean isEnabled() {
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
    public String getFormat(double amount) {
        if (!isEnabled()) {
            return null;
        }

        return vault.format(amount);
    }

    /**
     * Gets how much money a player has.<br>
     * If economy is not enabled this method will return 0.
     *
     * @param playerName
     *            player's name
     * @return money player has, 0 if no economy plugin was found
     */
    public double getMoney(String playerName) {
        double money = 0;

        if (isEnabled()) {
            try {
                UUID uuid = UUIDFetcher.getUUIDOf(playerName);
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

                money = vault.getBalance(player);
            } catch (Exception e) {}
        }

        return money;
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
    public void modMoney(String playerName, double amount) {
        if (!isEnabled() || amount == 0) {
            return;
        }

        EconomyResponse error = null;

        try {
            UUID uuid = UUIDFetcher.getUUIDOf(playerName);
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (amount > 0) {
                error = vault.depositPlayer(player, amount);
            } else {
                error = vault.withdrawPlayer(player, Math.abs(amount));
            }
        } catch (Exception e) {}

        if (error != null && !error.transactionSuccess()) {
            Messages.info("<red>Economy error: " + error.errorMessage);
        }
    }
}
