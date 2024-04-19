package haveric.recipeManager.flag.flags.any;

import com.google.common.base.Preconditions;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.*;
import haveric.recipeManager.flag.args.Args;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FlagForPermission extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.FOR_PERMISSION;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <permission node> @<flag declaration>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Adds other flags with permission requirements.",
            "You can specify this flag more than once to add more permissions or more flags to a permission.",
            "",
            "Basically this is a storage for flags and will only trigger them if the crafter has the required permission.",
            "This is useful for using different values for flags on the same recipe but for different permissions.",
            "",
            "The '<permission node>' argument must be a permission node.",
            "The '<flag declaration>' must be a flag that will work on the current recipe or result.",
            "For extra awesomeness you can even add this flag inside itself!",
            "",
            "NOTE: This will trigger all flags that player has permission for which means that flag effects will stack up.", };
    }

    protected String[] getExamples() {
        return new String[] {
            "@exp -2                      // you can use original flag as is for players that do not have the permission",
            "{flag} farmer.newbs @exp 4   // add 4 exp to the original -2 exp so player will have +2 exp",
            "{flag} farmer.uber @exp 50   // add 50 exp to the original -2 exp and also add 4 exp if the player has that node too",
            "{flag} farmer.uber @level 1  // if has required  give the crafter 1 level", };
    }


    private Map<String, Map<String, Flag>> flagMap = new LinkedHashMap<>();

    public FlagForPermission() {
    }

    public FlagForPermission(FlagForPermission flag) {
        super(flag);
        for (Entry<String, Map<String, Flag>> e : flag.flagMap.entrySet()) {
            Map<String, Flag> flags = new LinkedHashMap<>();

            for (Flag f : e.getValue().values()) {
                flags.put(f.getFlagType(), f.clone(getFlagsContainer()));
            }

            flagMap.put(e.getKey(), flags);
        }
    }

    @Override
    public FlagForPermission clone() {
        return new FlagForPermission((FlagForPermission) super.clone());
    }

    public Map<String, Map<String, Flag>> getFlagMap() {
        return flagMap;
    }

    public Flag getFlagForPermission(String permission, String type) {
        Map<String, Flag> flags = flagMap.get(permission);

        if (flags != null) {
            return flags.get(type);
        }

        return null;
    }

    public void setFlagMap(Map<String, Map<String, Flag>> map) {
        flagMap = map;
    }

    public void setFlagsForPermission(String permission, Map<String, Flag> flags) {
        flagMap.put(permission, flags);
    }

    /**
     * Checks if the flag can be added to this flag list.
     *
     * @param flag
     * @return false if flag can only be added on specific flaggables
     */
    public boolean canAdd(Flag flag, int restrictedBit) {
        return flag != null && flag.validate(restrictedBit) && !FlagFactory.getInstance().getFlagByName(flag.getFlagType()).hasBit(FlagBit.NO_FOR);
    }

    /**
     * Attempts to add a flag to this flag list for the permission.<br>
     * Adds an error to the {@link ErrorReporter} class if flag is not compatible with recipe/result.
     *
     * @param permission
     * @param flag
     */
    public void addFlag(String permission, Flag flag, int restrictedBit) {
        Preconditions.checkNotNull(flag, "Argument flag must not be null!");

        if (canAdd(flag, restrictedBit)) {
            Map<String, Flag> flags = flagMap.computeIfAbsent(permission, k -> new LinkedHashMap<>());

            flag.setFlagsContainer(getFlagsContainer());
            flags.put(flag.getFlagType(), flag);
        }
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.split("@");

        if (split.length <= 1) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " is missing the inner flag declaration!");
        }

        String permission = split[0].trim(); // store permission node for later use
        split = split[1].trim().split("[:\\s]+", 2); // split by space or : char
        String flagString = split[0].trim(); // format flag name
        FlagDescriptor type = FlagFactory.getInstance().getFlagByName(flagString); // Find the current flag

        if (type == null) { // If no valid flag was found
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has unknown flag: " + flagString, "Name might be different, check '" + Files.FILE_INFO_FLAGS + "' for flag list.");
        }

        if (type.hasBit(FlagBit.NO_FOR)) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + "'s flag " + flagString + " can not be used with this!");
        }

        Flag flag = getFlagForPermission(permission, type.getName()); // get existing flag, if any

        if (flag == null) {
            flag = type.createFlagClass(); // create a new instance of the flag if it does not exist
            flag.setFlagsContainer(getFlagsContainer()); // set container beforehand to allow checks
        }

        if (split.length > 1) {
            value = split[1].trim();
        } else {
            value = null;
        }

        // make sure the flag can be added to this flag list
        if (!flag.validateParse(value, restrictedBit)) {
            return false;
        }

        // check if parsed flag had valid values and needs to be added to flag list
        if (!flag.onParse(value, fileName, lineNum, restrictedBit)) {
            return false;
        }

        addFlag(permission, flag, restrictedBit);

        return true;
    }

    @Override
    public void onCheck(Args a) {
        event(a, 'c');
    }

    @Override
    public void onFailed(Args a) {
        event(a, 'f');
    }

    @Override
    public void onPrepare(Args a) {
        event(a, 'p');
    }

    @Override
    public void onCrafted(Args a) {
        event(a, 'r');
    }

    private void event(Args a, char method) {
        if (!a.hasPlayer()) {
            // no fail, optional flag
            return;
        }

        for (Entry<String, Map<String, Flag>> e : flagMap.entrySet()) {
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
                        default:
                            break;
                    }
                }
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        for (Map.Entry<String, Map<String, Flag>> entry : flagMap.entrySet()) {
            toHash += entry.getKey();

            for (Map.Entry<String, Flag> entry2 : entry.getValue().entrySet()) {
                toHash += entry2.getKey();
                toHash += entry2.getValue().hashCode();
            }
        }

        return toHash.hashCode();
    }
}
