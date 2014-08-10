package haveric.recipeManager.data;

import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockID {
    private transient int hash;

    private UUID wid;
    private int x;
    private int y;
    private int z;

    public BlockID(Block block) {
        parseLocation(block.getLocation());
    }

    public BlockID(Location location) {
        parseLocation(location);
    }

    public BlockID(World world, int x, int y, int z) {
        wid = world.getUID();
        this.x = x;
        this.y = y;
        this.z = z;

        buildHash();
    }

    /**
     * @param id
     * @param coords
     * @throws IllegalArgumentException
     *             if coordinate string isn't valid or id is null
     */
    public BlockID(UUID id, String coords) {
        Validate.notNull(id, "id argument must not be null!");
        Validate.notNull(coords, "coords argument must not be null!");

        wid = id;

        try {
            String[] s = coords.split(",", 3);

            x = Integer.parseInt(s[0]);
            y = Integer.parseInt(s[1]);
            z = Integer.parseInt(s[2]);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Coords argument must have 3 numbers separated by commas!");
        }

        buildHash();
    }

    private void parseLocation(Location location) {
        Validate.notNull(location, "location argument must not be null!");

        wid = location.getWorld().getUID();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();

        buildHash();
    }

    private void buildHash() {
        // hash = new HashCodeBuilder().append(wid).append(x).append(y).append(z).toHashCode();
        hash = (wid.toString() + ":" + x + ":" + y + ":" + z + ":").hashCode();
    }

    public static BlockID fromString(UUID id, String coords) {
        return new BlockID(id, coords);
    }

    public static BlockID fromLocation(Location location) {
        return new BlockID(location);
    }

    public static BlockID fromBlock(Block block) {
        return fromLocation(block.getLocation());
    }

    public Location toLocation() {
        World world = getWorld();

        if (world == null) {
            return null;
        }

        return new Location(world, x, y, z);
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

    public UUID getWorldID() {
        return wid;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    /**
     * @return coordinates in x,y,z format string
     */
    public String getCoordsString() {
        return x + "," + y + "," + z;
    }

    /**
     * Get world by the world ID stored
     *
     * @return world or null if world isn't loaded
     */
    public World getWorld() {
        return Bukkit.getWorld(wid);
    }

    /**
     * Returns the world's name
     *
     * @return world name or null if world isn't loaded
     */
    public String getWorldName() {
        World world = getWorld();

        return (world == null ? null : world.getName());
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof BlockID)) {
            return false;
        }

        BlockID b = (BlockID) obj;

        return (b.x == x && b.y == y && b.z == z && b.wid.equals(wid));
    }
}
