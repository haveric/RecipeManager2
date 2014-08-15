package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Messages;

import org.bukkit.Material;
import org.bukkit.block.Block;


public class FlagBlockPowered extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.BLOCKPOWERED;

        A = new String[] { "{flag} [arguments]", };

        D = new String[] { "Requires the workbench or furnace block to be powered by redstone.", "", "Optionally you can use the following arguments separated by | character and in any order:", "  indirect          = check for indirect redstone power, through other blocks.", "  failmsg <message> = overwrite the failure message.", };

        E = new String[] { "{flag}", "{flag} failmsg <red><bold>YOU HAVE NO (indirect) POWAAH!!! | indirect", };
    }

    // Flag code

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
        return new FlagBlockPowered(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public boolean isIndirect() {
        return indirect;
    }

    public void setIndirect(boolean indirect) {
        this.indirect = indirect;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
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
