package ro.thehunters.digi.recipeManager.flags;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;

import ro.thehunters.digi.recipeManager.ErrorReporter;
import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.flags.FlagType.Bit;

public class Flags implements Cloneable {
    private Map<FlagType, Flag> flags = new LinkedHashMap<FlagType, Flag>();
    protected Flaggable flaggable;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(flags.size() * 24);
        boolean first = true;

        for (Flag f : flags.values()) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }

            s.append(f.getType());
        }

        return (s.length() > 0 ? s.toString() : "empty");
    }

    public Flags() {
    }

    public Flags(Flaggable flaggable) {
        this.flaggable = flaggable;
    }

    public boolean hasFlag(Class<? extends Flag> flagClass) {
        return flags.containsKey(flagClass);
    }

    public boolean hasNoShiftBit() {
        for (FlagType t : flags.keySet()) {
            if (t.hasBit(Bit.NO_SHIFT)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets a flag by its type.<br> For automated casting you should use {@link #getFlag(Class)}
     *
     * @param type
     * @return Flag object
     */
    public Flag getFlag(FlagType type) {
        return flags.get(type);
    }

    /**
     * Gets a flag by its class name.<br> This is useful for easy auto-casting, example:<br> <br> <code>FlagCommands flag = flags.getFlag(FlagCommands.class);</code>
     *
     * @param flagClass
     *            the class of the flag
     * @return Flag object
     */
    public <T extends Flag> T getFlag(Class<T> flagClass) {
        return flagClass.cast(flags.get(FlagType.getByClass(flagClass)));
    }

    /**
     * Checks if flag exists in this flag list.
     *
     * @param type
     * @return
     */
    public boolean hasFlag(FlagType type) {
        return flags.containsKey(type);
    }

    /**
     * Checks if the flag can be added to this flag list.<br>
     *
     * @param flag
     * @return false if flag can only be added on specific flaggables
     */
    public boolean canAdd(Flag flag) {
        return flag != null && flag.validate();
    }

    /**
     * Attempts to add a flag to this flag list.<br> Adds an error to the {@link ErrorReporter} class if flag is not compatible with recipe/result.
     *
     * @param flag
     */
    public void addFlag(Flag flag) {
        Flags prevContainer = flag.flagsContainer;
        flag.flagsContainer = this;

        if (canAdd(flag)) {
            flags.put(flag.getType(), flag);
        } else {
            flag.flagsContainer = prevContainer;
        }
    }

    /**
     * Parses a string to create/get a flag and add to/update the list.<br> This is used by RecipeManager's file processor.
     *
     * @param value
     *            flag expression string like the ones in recipe files
     */
    public void parseFlag(String value) {
        Validate.notNull(value, "Input value must not be null!");
        value = value.trim();

        // check if it's really a flag because this is a public method
        if (value.charAt(0) != '@') {
            ErrorReporter.warning("Flags must start with @ character!");
            return;
        }

        String[] split = value.split("[:\\s]+", 2); // split by space or : char
        String flagString = split[0].trim(); // format flag name
        FlagType type = FlagType.getByName(flagString); // Find the current flag

        // If no valid flag was found
        if (type == null) {
            ErrorReporter.warning("Unknown flag: " + flagString, "Name might be different, check '" + Files.FILE_INFO_FLAGS + "' for flag list.");
            return;
        }

        Flag flag = flags.get(type); // get existing flag, if any

        if (flag == null) {
            flag = type.createFlagClass(); // create a new instance of the flag does not exist
        }

        flag.flagsContainer = this; // set container before hand to allow checks
        value = (split.length > 1 ? split[1].trim() : null);

        // make sure the flag can be added to this flag list
        if (!flag.validateParse(value)) {
            return;
        }

        // check if parsed flag had valid values and needs to be added to flag list
        if (flag.onParse(value)) {
            flags.put(flag.getType(), flag);
        }
    }

    /**
     * Removes the specified flag from this flag list.<br> Alias for {@link #removeFlag(FlagType)}
     *
     * @param flag
     */
    public void removeFlag(Flag flag) {
        if (flag == null) {
            return;
        }

        removeFlag(flag.getType());
    }

    /**
     * Removes the specified flag type from this flag list
     *
     * @param type
     */
    public void removeFlag(FlagType type) {
        if (type == null) {
            return;
        }

        Flag flag = flags.remove(type);

        if (flag != null) {
            flag.onRemove();
            flag.flagsContainer = null;
        }
    }

    /**
     * Gets the Recipe or ItemResult that uses this flag list.<br> You must check and cast accordingly.
     *
     * @return Flaggable object or null if undefined
     */
    public Flaggable getFlaggable() {
        return flaggable;
    }

    /**
     * Checks all flags and compiles a list of failure reasons while returning if the list is empty (no errors). Note: not all arguments are used, you may use null wherever you don't have anything to
     * give.
     *
     * @param a
     *            arguments class
     * @return true if recipe/result can be crafted by the arguments with the current flags
     */
    public boolean checkFlags(Args a) {
        a.clear();

        for (Flag flag : flags.values()) {
            flag.check(a);
        }

        return !a.hasReasons();
    }

    public boolean sendPrepare(Args a) {
        a.clear();

        for (Flag flag : flags.values()) {
            flag.prepare(a);
        }

        return !a.hasReasons();
    }

    /**
     * Applies all flags to player/location/result and compiles a list of failure reasons while returning if the list is empty (no errors). Note: not all arguments are used, you may use null wherever
     * you don't have anything to give.
     *
     * @param a
     *            arguments class
     * @return false if something was absolutely required and crafting should be cancelled
     */
    public boolean sendCrafted(Args a) {
        a.clear();

        for (Flag flag : flags.values()) {
            flag.crafted(a);
        }

        return !a.hasReasons();
    }

    /**
     * Sends failure notification to all flags
     *
     * @param a
     *            arguments class
     */
    public void sendFailed(Args a) {
        a.clear();

        for (Flag flag : flags.values()) {
            flag.failed(a);
        }
    }

    /**
     * Notifies all flags that the recipe was registered.<br> Shouldn't really be triggered manually.
     */
    public void sendRegistered() {
        for (Flag flag : flags.values()) {
            flag.registered();
        }
    }

    /**
     * Copy this flag storage and give it a new container.<br>
     *
     * @param newContainer
     * @return
     */
    public Flags clone(Flaggable newContainer) {
        Flags clone = clone();
        clone.flaggable = newContainer;
        return clone;
    }

    @Override
    public Flags clone() {
        Flags clone = new Flags();

        for (Flag f : flags.values()) {
            f = f.clone();
            f.flagsContainer = clone;
            clone.flags.put(f.getType(), f);
        }

        return clone;
    }
}
