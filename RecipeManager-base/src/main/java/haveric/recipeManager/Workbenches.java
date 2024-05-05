package haveric.recipeManager;

import com.google.common.base.Preconditions;
import haveric.recipeManager.data.BlockID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores in-use workbench locations to be used with flags.
 */
public class Workbenches {
    private static Map<String, BlockID> workbenches = new HashMap<>();

    private Workbenches() {
    }

    protected static void init() {
    }

    protected static void clean() {
        workbenches.clear();
    }

    public static void add(HumanEntity human, Location location) {
        if (human != null) {
            Preconditions.checkNotNull(location, "location argument must not be null!");

            workbenches.put(human.getName(), new BlockID(location));
        }
    }

    public static void remove(HumanEntity human) {
        if (human != null) {
            workbenches.remove(human.getName());
        }
    }

    /**
     * Get open workbench location of player if available.
     *
     * @param human
     *            the crafter, can be null but will make the method return null
     * @return workbench location if available or in-range, otherwise player's location or null if player is null
     */
    public static Location get(HumanEntity human) {
        if (human == null) {
            return null;
        }

        BlockID blockID = workbenches.get(human.getName());
        Location playerLoc = human.getLocation();

        if (blockID == null || !blockID.getWorldID().equals(human.getWorld().getUID())) {
            return playerLoc;
        }

        Block block = blockID.toBlock();

        if (block.getType() != Material.CRAFTING_TABLE) {
            workbenches.remove(human.getName());
            return playerLoc;
        }

        Location loc = block.getLocation();

        if (loc == null || loc.distanceSquared(playerLoc) > 36) { // 6 squared
            loc = playerLoc;
        }

        return loc;
    }
}
