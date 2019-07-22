package haveric.recipeManager;

import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.brew.BrewRecipe;
import haveric.recipeManager.recipes.campfire.RMCampfireRecipe;
import haveric.recipeManager.recipes.combine.CombineRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import haveric.recipeManager.recipes.furnace.RMBlastingRecipe;
import haveric.recipeManager.recipes.furnace.RMFurnaceRecipe;
import haveric.recipeManager.recipes.furnace.RMFurnaceRecipe1_13;
import haveric.recipeManager.recipes.furnace.RMSmokingRecipe;
import haveric.recipeManager.recipes.stonecutting.RMStonecuttingRecipe;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo.RecipeOwner;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RecipeRegistrator {
    private Map<BaseRecipe, RMCRecipeInfo> queuedRecipes = new HashMap<>();
    private boolean registered = false;

    protected RecipeRegistrator() {
    }
    
    public void queueRecipe(BaseRecipe recipe, String adder) {
        if (recipe instanceof CraftRecipe) {
            queueRecipe(recipe, adder, "Recipe " + recipe.getName() + " is invalid! Needs at least one result and exactly 9 ingredient slots, empty ones can be null.");
        } else if (recipe instanceof CombineRecipe) {
            queueRecipe(recipe, adder, "Recipe " + recipe.getName() + " is invalid! Needs at least one result and ingredient!");
        } else if (recipe instanceof RMFurnaceRecipe || recipe instanceof RMFurnaceRecipe1_13) {
            queueRecipe(recipe, adder, "Recipe " + recipe.getName() + " is invalid! Needs a result and ingredient!");
        } else if (recipe instanceof RMBlastingRecipe) {
            queueRecipe(recipe, adder, "Recipe " + recipe.getName() + " is invalid! Needs a result and ingredient!");
        } else if (recipe instanceof RMSmokingRecipe) {
            queueRecipe(recipe, adder, "Recipe " + recipe.getName() + " is invalid! Needs a result and ingredient!");
        } else if (recipe instanceof RMCampfireRecipe) {
            queueRecipe(recipe, adder, "Recipe " + recipe.getName() + " is invalid! Needs a result and ingredient!");
        } else if (recipe instanceof RMStonecuttingRecipe) {
            queueRecipe(recipe, adder, "Recipe " + recipe.getName() + " is invalid! Needs a result and ingredient!");
        } else if (recipe instanceof BrewRecipe) {
            queueRecipe(recipe, adder, "Recipe " + recipe.getName() + " is invalid! Needs a result and ingredient!");
        } else if (recipe instanceof FuelRecipe) {
            queueRecipe(recipe, adder, "Recipe " + recipe.getName() + " is invalid! Needs an ingredient!");
        } else {
            throw new IllegalArgumentException("Unknown recipe! " + recipe.toString());
        }
    }

    private void queueRecipe(BaseRecipe recipe, String adder, String error) {
        if (registered) {
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        }

        if (!recipe.isValid()) {
            throw new IllegalArgumentException(error);
        }

        queuedRecipes.remove(recipe); // if exists, update key too!
        queuedRecipes.put(recipe, new RMCRecipeInfo(RecipeOwner.RECIPEMANAGER, adder));
    }

    protected void registerRecipesToServer(CommandSender sender, long start) {
        if (registered) {
            throw new IllegalAccessError("This class is already registered, create a new one!");
        }

        Iterator<Entry<BaseRecipe, RMCRecipeInfo>> iterator;
        Entry<BaseRecipe, RMCRecipeInfo> entry;
        BaseRecipe recipe;
        RMCRecipeInfo info;

        // Remove old custom recipes/re-add old original recipes
        iterator = RecipeManager.getRecipes().index.entrySet().iterator();

        while (iterator.hasNext()) {
            entry = iterator.next();
            info = entry.getValue();
            recipe = entry.getKey();

            if (info.getOwner() == RecipeOwner.RECIPEMANAGER) {
                iterator.remove();
                recipe.remove();
            }
        }

        // TODO registering event or something to re-register plugin recipes

        iterator = queuedRecipes.entrySet().iterator();
        long lastDisplay = System.currentTimeMillis();
        long time;
        int processed = 0;
        int size = queuedRecipes.size();

        while (iterator.hasNext()) {
            entry = iterator.next();

            RecipeManager.getRecipes().registerRecipe(entry.getKey(), entry.getValue());

            time = System.currentTimeMillis();

            if (time > lastDisplay + 1000) {
                MessageSender.getInstance().sendAndLog(sender, String.format("%sRegistering recipes %d%%...", RMCChatColor.YELLOW, ((processed * 100) / size)));
                lastDisplay = time;
            }

            processed++;
        }

        registered = true; // mark this class as registered so it doesn't get re-registered
        queuedRecipes.clear(); // clear the queue to let the class vanish

        RecipeBooks.getInstance().reloadAfterRecipes(sender); // (re)create recipe books for recipes
        
        MessageSender.getInstance().send(sender, String.format("All done in %.3f seconds, %d recipes processed.", ((System.currentTimeMillis() - start) / 1000.0), processed));
    }

    public Map<BaseRecipe, RMCRecipeInfo> getQueuedRecipes() {
        return queuedRecipes;
    }

    public int getNumQueuedRecipes() {
        return queuedRecipes.size();
    }
}
