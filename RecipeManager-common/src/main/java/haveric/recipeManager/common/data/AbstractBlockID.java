package haveric.recipeManager.common.data;

import com.google.common.base.Preconditions;

import java.util.UUID;


public class AbstractBlockID {
    private transient int hash;

    protected UUID wid;
    protected int x;
    protected int y;
    protected int z;

    public AbstractBlockID() { }

    /**
     * @param id
     * @param coords
     * @throws IllegalArgumentException
     *             if coordinate string isn't valid or id is null
     */
    public AbstractBlockID(UUID id, String coords) {
        Preconditions.checkNotNull(id, "id argument must not be null!");
        Preconditions.checkNotNull(coords, "coords argument must not be null!");

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

    protected void buildHash() {
        hash = (wid.toString() + ":" + x + ":" + y + ":" + z + ":").hashCode();
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

        if (!(obj instanceof AbstractBlockID b)) {
            return false;
        }

        return (b.x == x && b.y == y && b.z == z && b.wid.equals(wid));
    }
}
