package haveric.recipeManager.data;

import com.google.common.base.Preconditions;
import haveric.recipeManager.common.data.AbstractBlockID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.UUID;

public class BlockID extends AbstractBlockID {

    public BlockID(Location location) {
        parseLocation(location);
    }

    public BlockID(World world, int newX, int newY, int newZ) {
        wid = world.getUID();
        x = newX;
        y = newY;
        z = newZ;

        buildHash();
    }

    public BlockID(UUID id, String coords) {
        super(id, coords);
    }

    private void parseLocation(Location location) {
        Preconditions.checkNotNull(location, "location argument must not be null!");

        wid = location.getWorld().getUID();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();

        buildHash();
    }

    public static BlockID fromString(UUID id, String coords) {
        return new BlockID(id, coords);
    }

    public static BlockID fromLocation(Location location) {
        return new BlockID(location);
    }

    /**
     * Gets the block at the stored coordinates
     *
     * @return
     */
    public Block toBlock() {
        World world = getWorld();

        if (world == null) {
            return null;
        }

        return world.getBlockAt(x, y, z);
    }

    /**
     * Get world by the world ID stored
     *
     * @return world or null if world isn't loaded
     */
    public World getWorld() {
        return Bukkit.getWorld(wid);
    }
}
