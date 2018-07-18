package haveric.recipeManager.flag.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.util.RMCUtil;
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
    public boolean onParse(String value) {
        if (value == null) {
            return true; // null value supported
        }

        String[] split = value.split("\\|");

        for (String arg : split) {
            arg = arg.trim();
            String check = arg.toLowerCase();

            if (check.equals("indirect")) {
                setIndirect(true);
            } else if (check.startsWith("failmsg")) {
                setFailMessage(RMCUtil.trimExactQuotes(arg.substring("failmsg".length())));
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
        if (Version.has1_13Support()) {
            craftingTableMaterial = Material.CRAFTING_TABLE;
        } else {
            craftingTableMaterial = Material.getMaterial("WORKBENCH");
        }
        if (blockType == Material.FURNACE || blockType == Material.BREWING_STAND || blockType == craftingTableMaterial ||
                (!Version.has1_13Support() && blockType == Material.getMaterial("BURNING_FURNACE"))) {

            boolean valid;
            if (isIndirect()) {
                valid = block.isBlockIndirectlyPowered() || block.isBlockPowered();
            } else {
                valid = block.isBlockPowered();
            }

            if (!valid) {
                String reason;
                if (blockType == craftingTableMaterial) {
                    reason = "flag.blockpowered.workbench";
                } else if (blockType == Material.BREWING_STAND) {
                    reason = "flag.blockpowered.brewingstand";
                } else {
                    reason = "flag.blockpowered.furnace";
                }
                a.addReason(reason, failMessage);
            }
        } else {
            a.addReason("flag.blockpowered.workbench", failMessage);
        }
    }
}
