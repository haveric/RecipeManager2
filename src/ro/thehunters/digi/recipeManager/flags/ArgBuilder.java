package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe.RecipeType;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class ArgBuilder {
    private Args a = new Args();

    public static void init() {
    }

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

    /**
     * Start building an argument class for flag events
     * 
     * @return linkable methods
     */
    public ArgBuilder() {
    }

    public ArgBuilder(Args a) {
        a.setPlayerName(a.playerName());
        a.setPlayer(a.player());
        a.setLocation(a.location().clone());
        a.setRecipe(a.recipe());
        a.setRecipeType(a.recipeType());
        a.setInventory(a.inventory());
        a.setResult(a.result().clone());
        a.setExtra(a.extra());
    }

    public ArgBuilder player(String player) {
        a.setPlayerName(player);
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

    public ArgBuilder recipe(RecipeType type) {
        a.setRecipeType(type);
        return this;
    }

    public ArgBuilder inventory(Inventory inventory) {
        a.setInventory(inventory);
        return this;
    }

    public ArgBuilder result(ItemStack result) {
        if (result != null) {
            a.setResult(result instanceof ItemResult ? (ItemResult) result : new ItemResult(result));
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
