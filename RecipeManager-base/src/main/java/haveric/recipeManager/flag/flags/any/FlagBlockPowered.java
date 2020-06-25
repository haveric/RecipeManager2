package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Version;
import haveric.recipeManager.common.util.RMCUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class FlagBlockPowered extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.BLOCK_POWERED;
    }

    @Override
    protected  String[] getArguments() {
        return new String[] {
            "{flag} [arguments]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Requires the workbench or furnace block to be powered by redstone.",
            "",
            "Optionally you can use the following arguments separated by | character and in any order:",
            "  indirect          = check for indirect redstone power, through other blocks.",
            "  failmsg <message> = overwrite the failure message.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] { "{flag}",
            "{flag} failmsg <red><bold>YOU HAVE NO (indirect) POWAAH!!! | indirect", };
    }


    private String failMessage = null;
    private boolean indirect = false;

    public FlagBlockPowered() {
    }

    public FlagBlockPowered(FlagBlockPowered flag) {
        failMessage = flag.failMessage;
        indirect = flag.indirect;
    }

    @Override
    public FlagBlockPowered clone() {
        return new FlagBlockPowered((FlagBlockPowered) super.clone());
    }

    public boolean isIndirect() {
        return indirect;
    }

    public void setIndirect(boolean newIndirect) {
        indirect = newIndirect;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String newFailMessage) {
        failMessage = newFailMessage;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        if (value == null) {
            return true; // null value supported
        }

        String[] split = value.split("\\|");

        for (String arg : split) {
            arg = arg.trim();
            String check = arg.toLowerCase();

            if (check.equals("indirect")) {
                indirect = true;
            } else if (check.startsWith("failmsg")) {
                failMessage = RMCUtil.trimExactQuotes(arg.substring("failmsg".length()));
            } else {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + arg);
            }
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
        Material blockType = block.getType();

        Material craftingTableMaterial;
        if (Version.has1_13BasicSupport()) {
            craftingTableMaterial = Material.CRAFTING_TABLE;
        } else {
            craftingTableMaterial = Material.getMaterial("WORKBENCH");
        }

        if (blockType == Material.FURNACE || blockType == Material.BREWING_STAND || blockType == craftingTableMaterial ||
                (!Version.has1_13BasicSupport() && blockType == Material.getMaterial("BURNING_FURNACE")) ||
                (Version.has1_14Support() && (blockType == Material.BLAST_FURNACE || blockType == Material.SMOKER || blockType == Material.STONECUTTER || blockType == Material.CAMPFIRE))) {

            boolean valid;
            if (indirect) {
                valid = block.isBlockIndirectlyPowered() || block.isBlockPowered();
            } else {
                valid = block.isBlockPowered();
            }

            if (!valid) {
                String blockName = null;
                if (blockType == craftingTableMaterial) {
                    blockName = "workbench";
                } else if (blockType == Material.BREWING_STAND) {
                    blockName = "brewing stand";
                } else if (blockType == Material.FURNACE || (!Version.has1_13BasicSupport() && blockType == Material.getMaterial("BURNING_FURNACE"))) {
                    blockName = "furnace";
                } else if (Version.has1_14Support()) {
                    if (blockType == Material.BLAST_FURNACE) {
                        blockName = "blast furnace";
                    } else if (blockType == Material.SMOKER) {
                        blockName = "smoker";
                    } else if (blockType == Material.STONECUTTER) {
                        blockName = "stonecutter";
                    } else if (blockType == Material.CAMPFIRE) {
                        blockName = "campfire";
                    }
                } else if (Version.has1_16Support()) {
                    if (blockType == Material.SOUL_CAMPFIRE) {
                        blockName = "soul campfire";
                    }
                }

                if (blockName == null) {
                    blockName = blockType.toString().toLowerCase();
                }

                a.addReason("flag.blockpowered", failMessage, "{blockname}", blockName);
            }
        } else {
            a.addReason("flag.blockpowered", failMessage,"{blockname}", blockType.toString().toLowerCase());
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "indirect: " + indirect;

        toHash += failMessage;

        return toHash.hashCode();
    }
}
