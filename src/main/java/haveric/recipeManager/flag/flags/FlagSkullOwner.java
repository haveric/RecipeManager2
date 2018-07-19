package haveric.recipeManager.flag.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Version;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Iterator;
import java.util.UUID;

public class FlagSkullOwner extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.SKULL_OWNER;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <name>",
            "{flag} <uuid>"};
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
    private UUID ownerUUID;

    public FlagSkullOwner() {
    }

    public FlagSkullOwner(FlagSkullOwner flag) {
        owner = flag.owner;
        ownerUUID = flag.ownerUUID;
    }

    @Override
    public FlagSkullOwner clone() {
        return new FlagSkullOwner((FlagSkullOwner) super.clone());
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String newOwner) {
        owner = newOwner;
    }

    public boolean hasOwnerUUID() {
        return ownerUUID != null;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID newOwnerUUID) {
        ownerUUID = newOwnerUUID;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();

        if (result == null || !(result.getItemMeta() instanceof SkullMeta) || result.getDurability() != 3) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a SKULL_ITEM with data value 3 to work!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value) {
        String[] components = value.split("-");
        if (components.length == 5) {
            setOwnerUUID(UUID.fromString(value));
        } else {
            setOwner(value);
        }

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        String owner = null;
        OfflinePlayer offlinePlayer = null;
        if (hasOwner()) {
            if (getOwner().equalsIgnoreCase("{player}")) {
                if (!a.hasPlayerUUID()) {
                    a.addCustomReason("Needs player UUID!");
                    return;
                }

                offlinePlayer = Bukkit.getOfflinePlayer(a.playerUUID());
                owner = offlinePlayer.getName();
            } else {
                owner = getOwner();
            }
        } else if (hasOwnerUUID()) {
            offlinePlayer = Bukkit.getOfflinePlayer(getOwnerUUID());
        }

        Player player = a.player();
        Location playerLocation = player.getLocation();
        Location loc = new Location(player.getWorld(), playerLocation.getBlockX(), 0, playerLocation.getBlockZ());
        Block block = loc.getBlock();
        BlockState originalState = block.getState();

        // Sets the block to the skull and retrieves the updated ItemStack from the drops.
        // This is needed because setOwner will not update the inventory texture.

        Material skullMaterial;
        if (Version.has1_13Support()) {
            skullMaterial = Material.PLAYER_HEAD;
        } else {
            skullMaterial = Material.getMaterial("SKULL");
        }

        block.setType(skullMaterial);
        BlockState newState = block.getState();
        if (Version.has1_12Support() && offlinePlayer != null) {
            Skull skull = (Skull) newState;
            skull.setSkullType(SkullType.PLAYER);
            skull.setOwningPlayer(offlinePlayer);
            skull.update();
        } else {
            //block.setData((byte) 3); // TODO: Replace data
            Skull skull = (Skull) loc.getBlock().getState();
            skull.setOwner(owner);
            skull.update();
        }

        Iterator<ItemStack> iter = loc.getBlock().getDrops().iterator();
        ItemStack result = iter.next();

        SkullMeta meta = (SkullMeta) result.getItemMeta();
        if (!meta.hasOwner()) {
            if (owner != null) {
                meta.setOwner(owner);
            }
            if (offlinePlayer != null) {
                meta.setOwningPlayer(offlinePlayer);
            }
            result.setItemMeta(meta);
        }

        ItemMeta cloned = result.getItemMeta().clone();
        a.result().setItemMeta(cloned);

        block.setType(originalState.getType());
        //block.setData(originalState.getRawData()); // TODO: Replace data
        originalState.update();
    }
}
