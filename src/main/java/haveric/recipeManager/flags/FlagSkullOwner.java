package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.recipes.ItemResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Iterator;

public class FlagSkullOwner extends Flag {

    @Override
    protected String getFlagType() {
        return FlagType.SKULL_OWNER;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <name>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Sets the human skull's owner to apply the skin.",
            "If you set it to '{player}' then it will use crafter's name.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} Notch",
            "{flag} {player}", };
    }


    private String owner;

    public FlagSkullOwner() {
    }

    public FlagSkullOwner(FlagSkullOwner flag) {
        owner = flag.owner;
    }

    @Override
    public FlagSkullOwner clone() {
        return new FlagSkullOwner((FlagSkullOwner) super.clone());
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String newOwner) {
        owner = newOwner;
    }

    @Override
    protected boolean onValidate() {
        ItemResult result = getResult();

        if (result == null || !(result.getItemMeta() instanceof SkullMeta) || result.getDurability() != 3) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a SKULL_ITEM with data value 3 to work!");
        }

        return true;
    }

    @Override
    protected boolean onParse(String value) {
        setOwner(value);
        return true;
    }

    @Override
    protected void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        String owner;
        if (getOwner().equalsIgnoreCase("{player}")) {
            if (!a.hasPlayerName()) {
                a.addCustomReason("Needs player name!");
                return;
            }

            owner = a.playerName();
        } else {
            owner = getOwner();
        }

        Player player = a.player();
        Location playerLocation = player.getLocation();
        Location loc = new Location(player.getWorld(), playerLocation.getBlockX(), 0, playerLocation.getBlockZ());
        Block block = loc.getBlock();
        BlockState originalState = block.getState();

        // If the block is an inventory, we don't want to replace it in order to prevent item loss.
        // Instead, let the non-updated texture be set with default setOwner behavior.
        // Ideally, this block will always be bedrock or air, so this won't be needed.
        if (originalState instanceof InventoryHolder) {
            SkullMeta meta = (SkullMeta) a.result().getItemMeta();
            meta.setOwner(owner);
            a.result().setItemMeta(meta);
        } else {
            // Sets the block to the skull and retrieves the updated ItemStack from the drops.
            // This is needed because setOwner will not update the inventory texture.
            block.setType(Material.SKULL);
            block.setData((byte) 3);
            Skull s = (Skull) loc.getBlock().getState();
            s.setOwner(owner);
            s.update();

            Iterator<ItemStack> iter = loc.getBlock().getDrops().iterator();
            ItemStack result = iter.next();

            SkullMeta meta = (SkullMeta) result.getItemMeta();
            if (!meta.hasOwner()) {
                meta.setOwner(owner);
                result.setItemMeta(meta);
            }

            ItemMeta cloned = result.getItemMeta().clone();
            a.result().setItemMeta(cloned);

            block.setType(originalState.getType());
            block.setData(originalState.getRawData());
            originalState.update();
        }
    }
}
