package haveric.recipeManager.flag.args;

import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ArgBuilder {
    private Args a = new Args();

    /**
     * Start building an argument class for flag events
     *
     * @return linkable methods
     */
    public static ArgBuilder create() {
        return new ArgBuilder();
    }

    public static ArgBuilder create(Args a) {
        return new ArgBuilder(a);
    }

    public static void init() { }

    /**
     * Start building an argument class for flag events
     *
     */
    public ArgBuilder() { }

    public ArgBuilder(Args newArgs) {
        a.setPlayerUUID(newArgs.playerUUID());
        a.setPlayer(newArgs.player());
        a.setLocation(newArgs.location().clone());
        a.setRecipe(newArgs.recipe());
        a.setRecipeType(newArgs.recipeType());
        a.setInventoryView(newArgs.inventoryView());
        a.setInventory(newArgs.inventory());
        a.setResult(newArgs.result().clone());
        a.setExtra(newArgs.extra());
    }

    public ArgBuilder player(UUID playerUUID) {
        a.setPlayerUUID(playerUUID);
        return this;
    }

    public ArgBuilder player(Player player) {
        a.setPlayer(player);
        return this;
    }

    public ArgBuilder location(Location location) {
        a.setLocation(location);
        return this;
    }

    public ArgBuilder recipe(BaseRecipe recipe) {
        a.setRecipe(recipe);
        return this;
    }

    public ArgBuilder recipe(RMCRecipeType type) {
        a.setRecipeType(type);
        return this;
    }

    public ArgBuilder inventoryView(InventoryView inventoryView) {
        a.setInventoryView(inventoryView);
        return this;
    }

    public ArgBuilder inventory(Inventory inventory) {
        a.setInventory(inventory);
        return this;
    }

    public ArgBuilder result(ItemStack result) {
        if (result != null) {
            if (result instanceof ItemResult) {
                a.setResult((ItemResult) result);
            } else {
                a.setResult(new ItemResult(result));
            }
        }

        return this;
    }

    public ArgBuilder result(ItemResult result) {
        a.setResult(result);
        return this;
    }

    public ArgBuilder extra(Object extra) {
        a.setExtra(extra);
        return this;
    }

    /**
     * Compile the arguments and get them.
     *
     * @return
     */
    public Args build() {
        return a.processArgs();
    }
}
