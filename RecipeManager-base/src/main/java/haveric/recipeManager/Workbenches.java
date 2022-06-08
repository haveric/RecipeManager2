package haveric.recipeManager;

import haveric.recipeManager.data.BlockID;
import haveric.recipeManager.tools.Version;
import org.apache.commons.lang3.Validate;
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
            Validate.notNull(location, "location argument must not be null!");

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

        Material craftingTableMaterial;
        if (Version.has1_13BasicSupport()) {
            craftingTableMaterial = Material.CRAFTING_TABLE;
        } else {
            craftingTableMaterial = Material.getMaterial("WORKBENCH");
        }
        if (block.getType() != craftingTableMaterial) { // Workbench doesn't exist any more
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
