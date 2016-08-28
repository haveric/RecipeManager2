package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class FlagBlockPowered extends Flag {

    @Override
    protected String getFlagType() {
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
    protected boolean onParse(String value) {
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
                arg = arg.substring("failmsg".length()).trim();

                setFailMessage(arg);
            } else {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + arg);
            }
        }

        return true;
    }

    @Override
    protected void onCheck(Args a) {
        if (!a.hasLocation()) {
            a.addCustomReason("Needs location!");
            return;
        }

        Block block = a.location().getBlock();

        switch (block.getType()) {
            case WORKBENCH:
            case FURNACE:
            case BURNING_FURNACE:
            case BREWING_STAND:
                boolean valid;
                if (isIndirect()) {
                    valid = block.isBlockIndirectlyPowered() || block.isBlockPowered();
                } else {
                    valid = block.isBlockPowered();
                }

                if (!valid) {
                    String reason;
                    if (block.getType() == Material.WORKBENCH) {
                        reason = "flag.blockpowered.workbench";
                    } else if (block.getType() == Material.BREWING_STAND) {
                        reason = "flag.blockpowered.brewingstand";
                    } else {
                        reason = "flag.blockpowered.furnace";
                    }
                    a.addReason(reason, failMessage);
                }
                break;
            default:
                a.addReason("flag.blockpowered.workbench", failMessage);
        }
    }
}
