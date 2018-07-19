package haveric.recipeManager.flag.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManagerCommon.util.ParseBit;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class FlagSetBlock extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.SET_BLOCK;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <block material>:[data] | [arguments]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Changes the workbench/furnace/block-at-player into other block type.",
            "Using this flag more than once will overwrite the previous flag.",
            "",
            "Replace '<block material>' with a block material (not item!), see '" + Files.FILE_INFO_NAMES + "' for list, IDs up to 255 are blocks, after that they're items.",
            "Optionally you can define a data value which defines its skin, direction and other stuff, see the 'Minecraft Wiki / Data Values' link from the '" + Files.FILE_INFO_NAMES + "' file.",
            "You can also use aliases for materials and data values too.",
            "",
            "Additionally you can define a set of arguments separated by | character:",
            "  drop            = breaks the existing block and drops its item.",
            "  noinv [failmsg] = prevent inventory crafting, if this is not set, the flag will set the block at player location too; optionally you can overwrite the failure message for this condition.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} gold_block",
            "{flag} planks:jungle | noinv // set to jungle wood planks and prevent inventory crafting",
            "{flag} air | drop | noinv <red>Only workbench! // simulate block break", };
    }


    private Material type;
    private byte data;
    private boolean drop;
    private boolean noInv;
    private String failMessage;

    public FlagSetBlock() {
    }

    public FlagSetBlock(FlagSetBlock flag) {
        type = flag.type;
        data = flag.data;
        drop = flag.drop;
        noInv = flag.noInv;
        failMessage = flag.failMessage;
    }

    @Override
    public FlagSetBlock clone() {
        return new FlagSetBlock((FlagSetBlock) super.clone());
    }

    @Override
    public boolean onParse(String value) {
        String[] args = value.split("\\|");

        value = args[0].trim();

        ItemStack item = Tools.parseItem(value, 0, ParseBit.NO_AMOUNT | ParseBit.NO_META);

        if (item == null || !item.getType().isBlock()) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid block material type: " + value, "Note that block materials have IDs from 0 to 255.");
            return false;
        }

        type = item.getType();
        data = (byte) item.getDurability();

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                value = args[i].toLowerCase().trim();

                if (value.equals("drop")) {
                    drop = true;
                } else if (value.startsWith("noinv")) {
                    noInv = true;

                    value = RMCUtil.trimExactQuotes(args[i].trim().substring("noinv".length()));

                    if (!value.isEmpty()) {
                        failMessage = value;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void onCheck(Args a) {
        if (!a.hasLocation() || !a.hasInventory()) {
            a.addCustomReason("Needs location and inventory!");
            return;
        }

        if (noInv && a.inventory() instanceof CraftingInventory) {
            CraftingInventory inv = (CraftingInventory) a.inventory();

            if (inv.getSize() < 9) {
                a.addReason("flag.setblock.needsworkbench", failMessage);
            }
        }
    }

    @Override
    public void onCrafted(Args a) {
        if (!a.hasLocation()) {
            a.addCustomReason("Needs location!");
            return;
        }

        Block block = a.location().getBlock();

        if (drop) {
            block.breakNaturally();
        }


        block.setType(type, true);
        // block.setData(data, true); // TODO: Replace data
    }
}
