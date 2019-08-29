package haveric.recipeManager.tools;

import org.bukkit.entity.Player;

/**
 * Proper experience methods.
 *
 * @author Essentials<br>
 *         https://github.com/essentials/Essentials/blob/2.x/Essentials/src/com/earth2me/essentials/craftbukkit/SetExpFix.java
 */
public class ToolsExp {
    // This method is used to update both the recorded total experience and displayed total experience.
    // We reset both types to prevent issues.
    public static void setTotalExperience(final Player player, final int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("Experience is negative!");
        }

        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        // This following code is technically redundant now, as Bukkit now calculates levels more or less correctly
        // At larger numbers however... player.getExp(3000), only seems to give 2999, putting the below calculations off.
        int amount = exp;

        while (amount > 0) {
            final int expToLevel = getExpAtLevel(player);
            amount -= expToLevel;

            if (amount >= 0) {
                // give until next level
                player.giveExp(expToLevel);
            } else {
                // give the rest
                amount += expToLevel;
                player.giveExp(amount);
                amount = 0;
            }
        }
    }

    private static int getExpAtLevel(final Player player) {
        return getExpAtLevel(player.getLevel());
    }

    private static int getExpAtLevel(final int level) {
        if (level > 30) {
            return (9 * level) - 158;
        }

        if (level > 15) {
            return (5 * level) - 38;
        }

        return (2 * level) + 7;
    }

    public static int getExpToLevel(final int level) {
        int currentLevel = 0;
        int exp = 0;

        while (currentLevel < level) {
            exp += getExpAtLevel(currentLevel);
            currentLevel++;
        }

        return exp;
    }

    // This method is required because the Bukkit player.getTotalExperience() method, shows exp that has been 'spent'.
    // Without this people would be able to use exp and then still sell it.
    public static int getTotalExperience(final Player player) {
        int exp = Math.round(getExpAtLevel(player) * player.getExp());
        int currentLevel = player.getLevel();

        while (currentLevel > 0) {
            currentLevel--;
            exp += getExpAtLevel(currentLevel);
        }

        return exp;
    }

    public static int getExpUntilNextLevel(final Player player) {
        int exp = Math.round(getExpAtLevel(player) * player.getExp());
        int nextLevel = player.getLevel();

        return getExpAtLevel(nextLevel) - exp;
    }
}