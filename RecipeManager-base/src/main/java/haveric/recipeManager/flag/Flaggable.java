package haveric.recipeManager.flag;

import haveric.recipeManager.flag.args.Args;

public interface Flaggable {
    /**
     * Shortcut for {@link Flags#hasFlag(String)}
     */
    boolean hasFlag(String type);

    /**
     * Checks if flag storage is null.<br>
     * This is useful to check if {@link #getFlags()} would create a new Flags object when called.
     *
     * @return flags != null
     */
    boolean hasFlags();

    /**
     * Shortcut for {@link Flags#getFlag(String)}
     */
    Flag getFlag(String type);

    /**
     * Gets the Flag object that holds a list of flags.<br>
     * Can't be null but creates a new instance of Flag when called.<br>
     * You can check if flags is null with {@link #hasFlags()}
     *
     * @return Flag object, never null
     */
    Flags getFlags();

    /**
     * Removes all flags.
     */
    void clearFlags();

    /**
     * Shortcut for {@link Flags#addFlag(Flag, int)}
     */
    void addFlag(Flag flag);

    /**
     * Check with flags if recipe/result can be crafted/used
     *
     * @param a
     *            use {@link Args#create()}
     * @return if recipe can be crafted
     */
    boolean checkFlags(Args a);

    /**
     * Apply flags when recipe/result is crafted/taken
     *
     * @param a
     *            use {@link Args#create()}
     * @return
     */
    boolean sendCrafted(Args a);

    /**
     * Apply flags when recipe/result is prepared/displayed
     *
     * @param a
     *            {@link Args#create()}
     * @return
     */
    boolean sendPrepare(Args a);

    boolean sendFuelRandom(Args a);

    boolean sendFuelEnd(Args a);
}
