package haveric.recipeManager;

import haveric.recipeManager.uuidFetcher.UUIDFetcher;

import java.util.UUID;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Econ {
    private Economy economy = null;

    private static Econ instance;

    protected Econ() {
        // Exists only to defeat instantiation.
    }

    public static Econ getInstance() {
        if (instance == null) {
            instance = new Econ();
        }

        return instance;
    }

    public void init(Economy newEconomy) {
        if (newEconomy != null) {
            if (newEconomy.isEnabled()) {
                economy = newEconomy;
                Messages.log("Vault detected and connected to " + economy.getName() + ", economy features available.");
            } else {
                Messages.log("Vault detected but does not have an economy plugin connected, economy features are not available.");
            }
        }
    }

    protected void clean() {
        economy = null;
    }

    /**
     * Checks if you can use economy methods.
     *
     * @return true if economy plugin detected, false otherwise
     */
    public boolean isEnabled() {
        return economy != null;
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

        return economy.format(amount);
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

                money = economy.getBalance(player);
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
                error = economy.depositPlayer(player, amount);
            } else {
                error = economy.withdrawPlayer(player, Math.abs(amount));
            }
        } catch (Exception e) {}

        if (error != null && !error.transactionSuccess()) {
            Messages.info("<red>Economy error: " + error.errorMessage);
        }
    }
}
