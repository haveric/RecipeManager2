package ro.thehunters.digi.recipeManager.flags;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;

import ro.thehunters.digi.recipeManager.ErrorReporter;
import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.flags.FlagType.Bit;

public class FlagForPermission extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.FORPERMISSION;

        A = new String[] { "{flag} <permission node> @<flag declaration>", };

        D = new String[] { "Adds other flags with permission requirements.", "You can specify this flag more than once to add more permissions or more flags to a permission.", "", "Basically this is a storage for flags and will only trigger them if the crafter has the required permission.", "This is useful for using diferent values for flags on the same recipe but for diferent permissions.", "", "The '<permission node>' argument must be a permission node.", "The '<flag declaration>' must be a flag that will work on the current recipe or result.", "For extra awesomeness you can even add this flag inside itself !", "", "NOTE: This will trigger all flags that player has permission for which means that flag effects will stack up.", };

        E = new String[] { "@exp -2                      // you can use original flag as is for players that do not have the permission", "{flag} farmer.newbs @exp 4   // add 4 exp to the original -2 exp so player will have +2 exp", "{flag} farmer.uber @exp 50   // add 50 exp to the original -2 exp and also add 4 exp if the player has that node too", "{flag} farmer.uber @level 1  // if has required  give the crafter 1 level", };
    }

    // Flag code

    private Map<String, Map<FlagType, Flag>> flagMap = new LinkedHashMap<String, Map<FlagType, Flag>>();

    public FlagForPermission() {
    }

    public FlagForPermission(FlagForPermission flag) {
        for (Entry<String, Map<FlagType, Flag>> e : flag.flagMap.entrySet()) {
            Map<FlagType, Flag> flags = new LinkedHashMap<FlagType, Flag>();

            for (Flag f : e.getValue().values()) {
                flags.put(f.getType(), f.clone(this.getFlagsContainer()));
            }

            flagMap.put(e.getKey(), flags);
        }
    }

    @Override
    public FlagForPermission clone() {
        return new FlagForPermission(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public Map<String, Map<FlagType, Flag>> getFlagMap() {
        return flagMap;
    }

    public Flag getFlagForPermission(String permission, FlagType type) {
        Map<FlagType, Flag> flags = flagMap.get(permission);

        if (flags != null) {
            return flags.get(type);
        }

        return null;
    }

    public void setFlagMap(Map<String, Map<FlagType, Flag>> map) {
        flagMap = map;
    }

    public void setFlagsForPermission(String permission, Map<FlagType, Flag> flags) {
        flagMap.put(permission, flags);
    }

    /**
     * Checks if the flag can be added to this flag list.<br>
     * 
     * @param flag
     * @return false if flag can only be added on specific flaggables
     */
    public boolean canAdd(Flag flag) {
        return flag != null && flag.validate() && !flag.getType().hasBit(Bit.NO_FOR);
    }

    /**
     * Attempts to add a flag to this flag list for the permission.<br> Adds an error to the {@link ErrorReporter} class if flag is not compatible with recipe/result.
     * 
     * @param permission
     * @param flag
     */
    public void addFlag(String permission, Flag flag) {
        Validate.notNull(flag, "Argument flag must not be null!");

        if (canAdd(flag)) {
            Map<FlagType, Flag> flags = flagMap.get(permission);

            if (flags == null) {
                flags = new LinkedHashMap<FlagType, Flag>();
                flagMap.put(permission, flags);
            }

            flag.flagsContainer = this.getFlagsContainer();
            flags.put(flag.getType(), flag);
        }
    }

    @Override
    protected boolean onParse(String value) {
        String[] split = value.split("@");

        if (split.length <= 1) {
            return ErrorReporter.error("Flag " + getType() + " is missing the inner flag declaration !");
        }

        String permission = split[0].trim(); // store permission node for later use
        split = split[1].trim().split("[:\\s]+", 2); // split by space or : char
        String flagString = '@' + split[0].trim(); // format flag name
        FlagType type = FlagType.getByName(flagString); // Find the current flag

        if (type == null) // If no valid flag was found
        {
            return ErrorReporter.error("Flag " + getType() + " has unknown flag: " + flagString, "Name might be diferent, check '" + Files.FILE_INFO_FLAGS + "' for flag list.");
        }

        if (type.hasBit(Bit.NO_FOR)) {
            return ErrorReporter.error("Flag " + getType() + "'s flag " + flagString + " can not be used with this!");
        }

        Flag flag = getFlagForPermission(permission, type); // get existing flag, if any

        if (flag == null) {
            flag = type.createFlagClass(); // create a new instance of the flag does not exist
            flag.flagsContainer = this.getFlagsContainer(); // set container before hand to allow checks
        }

        value = (split.length > 1 ? split[1].trim() : null);

        // make sure the flag can be added to this flag list
        if (!flag.validateParse(value)) {
            return false;
        }

        // check if parsed flag had valid values and needs to be added to flag list
        if (!flag.onParse(value)) {
            return false;
        }

        addFlag(permission, flag);

        return true;
    }

    @Override
    protected void onCheck(Args a) {
        event(a, 'c');
    }

    @Override
    protected void onFailed(Args a) {
        event(a, 'f');
    }

    @Override
    protected void onPrepare(Args a) {
        event(a, 'p');
    }

    @Override
    protected void onCrafted(Args a) {
        event(a, 'r');
    }

    private void event(Args a, char method) {
        if (!a.hasPlayer()) {
            // no fail, optional flag
            return;
        }

        for (Entry<String, Map<FlagType, Flag>> e : flagMap.entrySet()) {
            if (a.player().hasPermission(e.getKey())) {
                for (Flag f : e.getValue().values()) {
                    switch (method) {
                        case 'c':
                            f.check(a);
                            break;

                        case 'p':
                            f.prepare(a);
                            break;

                        case 'r':
                            f.crafted(a);
                            break;

                        case 'f':
                            f.failed(a);
                            break;
                    }
                }
            }
        }
    }
}
