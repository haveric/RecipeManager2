package haveric.recipeManager.flag;

import haveric.recipeManager.messages.MessageSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlagDescriptor {
    private List<String> names = new ArrayList<>();
    private int bits;
    private Flag flag;

    public FlagDescriptor(String mainAlias, Flag newFlag, int newBits, String... aliases) {
        flag = newFlag;
        bits = newBits;

        names.add(mainAlias.toLowerCase());
        Collections.addAll(names, aliases);
    }

    /**
     * @return the first name of the flag
     */
    public String getName() {
        return names.get(0);
    }

    public String getNameDisplay() {
        return '@' + getName();
    }

    /**
     * @return array of flags names, index 0 is always the main name
     */
    public ArrayList<String> getNames() {
        return new ArrayList<>(names);
    }

    public ArrayList<String> getAliases() {
        ArrayList<String> cloned = new ArrayList<>(names);
        cloned.remove(0);

        return cloned;
    }

    /**
     * Checks if flag type has a special bit.
     *
     * @param bit
     *            See {@link FlagBit}
     * @return
     */
    public boolean hasBit(int bit) {
        return (bits & bit) == bit;
    }

    public Flag getFlag() {
        return flag;
    }

    public Flag createFlagClass() {
        try {
            return getFlag().getClass().newInstance();
        } catch (Throwable e) {
            MessageSender.getInstance().error(null, e, null);
        }

        return null;
    }

    public String[] getArguments() {
        return flag.getArguments();
    }

    public String[] getExamples() {
        return flag.getExamples();
    }

    public String[] getDescription() {
        return flag.getDescription();
    }
}
