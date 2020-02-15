package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.block.Block;

public class FlagLightLevel extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.LIGHT_LEVEL;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <min or min-max> [type] | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Checks for the light level.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "The first argument must be a number from 0 to 15 to set a minimum light level, or you can specify a number range separated - character.",
            "",
            "Optionally you can set the  [type] argument to specify light type:",
            "  any    = (default) any kind of light.",
            "  sun    = sun light only.",
            "  blocks = light from blocks (torches, furnaces, etc) only.",
            "",
            "Be careful using blocks light level with furnace recipes as the furnace will provide light to itself.",
            "",
            "You can also overwrite the fail message or use 'false' to hide it.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 14 sun",
            "{flag} 0-4 blocks | <red>Kill the lights!", };
    }


    private byte minLight;
    private byte maxLight;
    private char lightType;
    private String failMessage;

    public FlagLightLevel() {
    }

    public FlagLightLevel(FlagLightLevel flag) {
        minLight = flag.minLight;
        maxLight = flag.maxLight;
        lightType = flag.lightType;
        failMessage = flag.failMessage;
    }

    @Override
    public FlagLightLevel clone() {
        return new FlagLightLevel((FlagLightLevel) super.clone());
    }

    /**
     * @return min light level.
     */
    public byte getMinLight() {
        return minLight;
    }

    /**
     * @return max light level, if disabled will be less than min light level.
     */
    public byte getMaxLight() {
        return maxLight;
    }

    /**
     * Minimum light level.
     *
     * @param min
     *            0-15
     */
    public void setLightLevel(int min) {
        setLightLevelRange(min, 0);
    }

    /**
     * Light level range.
     *
     * @param min
     *            0-15
     * @param max
     *            min-15
     */
    public void setLightLevelRange(int min, int max) {
        minLight = (byte) min;
        maxLight = (byte) max;
    }

    /**
     * @return returns 'sun', 'blocks' or 'any'.
     */
    public String getLightType() {
        switch (lightType) {
            case 's':
                return "sun";

            case 'b':
                return "blocks";

            default:
                return "any";
        }
    }

    /**
     * @param type
     *            'sun', 'blocks' or 'any'/null.
     * @throws IllegalArgumentException
     *             if any other string is specified
     */
    public void setLightType(String type) {
        if (type == null) {
            lightType = 'a';
        } else {
            lightType = type.charAt(0);
        }

        switch (lightType) {
            case 's':
            case 'b':
            case 'a':
                break;

            default:
                lightType = 'a';
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    public String getLightString() {
        String lightString;

        if (maxLight > minLight) {
            lightString = minLight + "-" + maxLight;
        } else {
            lightString = minLight + "+";
        }
        return lightString;
    }

    public String getLightTypeString() {
        switch (lightType) {
            case 's':
                return "sun light";

            case 'b':
                return "block light";

            default:
                return "light";
        }
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String message) {
        failMessage = message;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        String[] split = value.split("\\|", 2);

        if (split.length > 1) {
            setFailMessage(RMCUtil.trimExactQuotes(split[1]));
        } else {
            setFailMessage(null);
        }

        value = split[0].trim().toLowerCase();

        int i = value.lastIndexOf(' ');

        if (value.length() >= (i + 1)) {
            lightType = value.charAt(i + 1);

            switch (lightType) {
                case 's':
                case 'b':
                case 'a':
                    value = value.substring(0, i);
                    break;

                default:
                    lightType = 'a';
            }
        }

        split = value.split("-");

        value = split[0].trim();

        try {
            minLight = Byte.parseByte(value);
        } catch (NumberFormatException e) {
            ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid number: " + value);
            return false;
        }

        if (split.length > 1) {
            value = split[1].trim();

            try {
                maxLight = Byte.parseByte(value);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid number: " + value);
                return false;
            }
        }

        if (minLight > 15 || maxLight > 15 || (maxLight > 0 && (minLight > maxLight || minLight < 0))) {
            ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid ranges: " + minLight + " to " + maxLight + "; they must be from 0 to 15 and min must be smaller than max.");
            return false;
        }

        return true;
    }

    @Override
    public void onCheck(Args a) {
        if (!a.hasLocation()) {
            a.addCustomReason("Needs location!");
            return;
        }

        Block block = a.location().getBlock();
        int light;

        switch (lightType) {
            case 's':
                light = block.getLightFromSky();
                break;

            case 'b':
                light = block.getLightFromBlocks();
                break;

            default:
                light = block.getLightLevel();
        }

        if (light < minLight || (maxLight > minLight && light > maxLight)) {
            a.addReason("flag.lightlevel", failMessage, "{light}", getLightString(), "{type}", getLightTypeString());
        }
    }

    /*
     * @Override public List<String> information() { List<String> list = new ArrayList<String>(1);
     *
     * list.add(MessagesOld.FLAG_LIGHTLEVEL.get("{light}", getLightString(), "{type}", getLightTypeString()));
     *
     * return list; }
     */

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "minLight: " + minLight;
        toHash += "maxLight: " + maxLight;
        toHash += "lightType: " + lightType;
        toHash += "failMessage: " + failMessage;

        return toHash.hashCode();
    }
}
