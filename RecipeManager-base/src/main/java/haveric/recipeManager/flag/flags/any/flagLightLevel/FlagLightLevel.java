package haveric.recipeManager.flag.flags.any.flagLightLevel;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class FlagLightLevel extends Flag {

    public static final int LIGHT_LEVEL_MAX = 15;

    @Override
    public String getFlagType() {
        return FlagType.LIGHT_LEVEL;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <min or min-max> [type], [...] | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Checks for the light level.",
            "Using this flag more than once will add more options.",
            "",
            "The first argument must be a number from 0 to 15 to set a minimum light level, or you can specify a number range separated by a '-' character.",
            "",
            "Optionally you can set the [type] argument to specify light type:",
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
            "{flag} 0-4 blocks | <red>Kill the lights!",
            "{flag} 0-3 blocks, 12-15 sun | <red>Must be away from torches or in the sun!" };
    }

    private List<LightLevelOptions> lightLevelOptions = new ArrayList<>();
    private String failMessage = null;

    public FlagLightLevel() {
    }

    public FlagLightLevel(FlagLightLevel flag) {
        super(flag);
        lightLevelOptions.addAll(flag.lightLevelOptions);
        failMessage = flag.failMessage;
    }

    @Override
    public FlagLightLevel clone() {
        return new FlagLightLevel((FlagLightLevel) super.clone());
    }

    public List<LightLevelOptions> getLightLevelOptions() {
        return lightLevelOptions;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String message) {
        failMessage = message;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.split("\\|", 2);

        if (split.length > 1) {
            failMessage = RMCUtil.trimExactQuotes(split[1]);
        }

        String[] optionsSplit = split[0].split(",");
        for (String option : optionsSplit) {
            option = option.trim().toLowerCase();

            int i = option.lastIndexOf(' ');
            char lightTypeChar = 0;
            if (option.length() >= (i + 1)) {
                lightTypeChar = option.charAt(i + 1);

                switch (lightTypeChar) {
                    case 's':
                    case 'b':
                    case 'a':
                        option = option.substring(0, i);
                        break;

                    default:
                        lightTypeChar = 'a';
                }
            }

            LightType lightType = LightType.getLightType(lightTypeChar);

            String[] minMaxSplit = option.split("-");
            String minMax = minMaxSplit[0].trim();

            int minLight;
            int maxLight = -1;
            try {
                minLight = Byte.parseByte(minMax);
            } catch (NumberFormatException e) {
                return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid number: " + minMax);
            }

            if (minMaxSplit.length > 1) {
                minMax = minMaxSplit[1].trim();

                try {
                    maxLight = Byte.parseByte(minMax);
                } catch (NumberFormatException e) {
                    return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid number: " + minMax);
                }
            }

            if (minLight > LIGHT_LEVEL_MAX || maxLight > LIGHT_LEVEL_MAX || (maxLight > 0 && (minLight > maxLight || minLight < 0))) {
                return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid ranges: " + minLight + " to " + maxLight + "; they must be from 0 to 15 and min must be smaller than max.");
            }

            lightLevelOptions.add(new LightLevelOptions(minLight, maxLight, lightType));
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
        boolean anySucceed = false;
        for (LightLevelOptions option : lightLevelOptions) {
            int light = getLightFromBlock(block, option.getLightType(), false);
            if (light == 0) {
                light = getLightFromSurroundingBlocks(block, option.getLightType());
            }

            if (option.isValidLightLevel(light)) {
                anySucceed = true;
                break;
            }
        }

        if (!anySucceed) {
            LightLevelOptions option = lightLevelOptions.get(0);
            a.addReason("flag.lightlevel", failMessage, "{light}", option.getLightString(), "{type}", option.getLightTypeString());
        }
    }

    private int getLightFromBlock(Block block, LightType lightType, boolean above) {
        int light;
        switch (lightType) {
            case SUN:
                light = block.getLightFromSky();

                if (above && light < LIGHT_LEVEL_MAX) {
                    light -= 1;
                }
                break;
            case BLOCKS:
                light = block.getLightFromBlocks();
                if (above) {
                    light -= 1;
                }
                break;
            default:
                int skyLight = block.getLightFromSky();
                if (above && skyLight < LIGHT_LEVEL_MAX) {
                    skyLight -= 1;
                }
                int blockLight = block.getLightFromBlocks();
                if (above) {
                    blockLight -= 1;
                }
                light = Math.max(skyLight, blockLight);
        }

        return light;
    }

    private int getLightFromSurroundingBlocks(Block block, LightType lightType) {
        int light = 0;
        light = Math.max(light, getLightFromBlock(block.getRelative(BlockFace.UP), lightType, true));
        if (light < LIGHT_LEVEL_MAX) {
            light = Math.max(light, getLightFromBlock(block.getRelative(BlockFace.DOWN), lightType, false) - 1);
        }
        if (light < LIGHT_LEVEL_MAX) {
            light = Math.max(light, getLightFromBlock(block.getRelative(BlockFace.NORTH), lightType, false) - 1);
        }
        if (light < LIGHT_LEVEL_MAX) {
            light = Math.max(light, getLightFromBlock(block.getRelative(BlockFace.EAST), lightType, false) - 1);
        }
        if (light < LIGHT_LEVEL_MAX) {
            light = Math.max(light, getLightFromBlock(block.getRelative(BlockFace.SOUTH), lightType, false) - 1);
        }
        if (light < LIGHT_LEVEL_MAX) {
            light = Math.max(light, getLightFromBlock(block.getRelative(BlockFace.WEST), lightType, false) - 1);
        }

        return light;
    }

    @Override
    public int hashCode() {
        StringBuilder toHash = new StringBuilder(String.valueOf(super.hashCode()));

        for (LightLevelOptions option : lightLevelOptions) {
            toHash.append("Option: ");
            toHash.append("minLight: ").append(option.getMinLight());
            toHash.append("maxLight: ").append(option.getMaxLight());
            toHash.append("lightType: ").append(option.getLightType());
        }
        toHash.append("failMessage: ").append(failMessage);

        return toHash.toString().hashCode();
    }
}
