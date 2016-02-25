package haveric.recipeManager.flags;

import org.bukkit.Material;
import org.bukkit.block.Block;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Messages;

public class FlagBlockPowered extends Flag {

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
                ErrorReporter.warning("Flag " + getType() + " has unknown argument: " + arg);
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
                if (isIndirect() ? !block.isBlockIndirectlyPowered() : !block.isBlockPowered()) {
                    Messages reason;
                    if (block.getType() == Material.WORKBENCH) {
                        reason = Messages.FLAG_BLOCKPOWERED_WORKBENCH;
                    } else {
                        reason = Messages.FLAG_BLOCKPOWERED_FURNACE;
                    }
                    a.addReason(reason, failMessage);
                }
                break;
            default:
                a.addReason(Messages.FLAG_BLOCKPOWERED_WORKBENCH, failMessage);
                return;
        }
    }
}
