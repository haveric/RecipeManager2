package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.CombineRecipe;
import ro.thehunters.digi.recipeManager.recipes.CraftRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeOwner;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;

public class RecipeRegistrator {
    protected Map<BaseRecipe, RecipeInfo> queuedRecipes = new HashMap<BaseRecipe, RecipeInfo>();
    private boolean registered = false;

    protected RecipeRegistrator() {
    }

    protected void queueRecipe(BaseRecipe recipe, String adder) {
        if (recipe instanceof CraftRecipe) {
            queueCraftRecipe((CraftRecipe) recipe, adder);
        } else if (recipe instanceof CombineRecipe) {
            queueCombineRecipe((CombineRecipe) recipe, adder);
        } else if (recipe instanceof SmeltRecipe) {
            queueSmeltRecipe((SmeltRecipe) recipe, adder);
        } else if (recipe instanceof FuelRecipe) {
            queuFuelRecipe((FuelRecipe) recipe, adder);
        } else {
            throw new IllegalArgumentException("Unknown recipe!");
        }
    }

    protected void queueCraftRecipe(CraftRecipe recipe, String adder) {
        if (registered) {
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        }

        if (!recipe.isValid()) {
            throw new IllegalArgumentException("Recipe is invalid ! Needs at least one result and exacly 9 ingredient slots, empty ones can be null.");
        }

        queuedRecipes.remove(recipe); // if exists, update key too !
        queuedRecipes.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder));
    }

    protected void queueCombineRecipe(CombineRecipe recipe, String adder) {
        if (registered) {
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        }

        if (!recipe.isValid()) {
            throw new IllegalArgumentException("Recipe is invalid ! Needs at least one result and ingredient!");
        }

        queuedRecipes.remove(recipe);
        queuedRecipes.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder));
    }

    protected void queueSmeltRecipe(SmeltRecipe recipe, String adder) {
        if (registered) {
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        }

        if (!recipe.isValid()) {
            throw new IllegalArgumentException("Recipe is invalid ! Needs a result and ingredient!");
        }

        queuedRecipes.remove(recipe);
        queuedRecipes.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder));
    }

    protected void queuFuelRecipe(FuelRecipe recipe, String adder) {
        if (registered) {
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        }

        if (!recipe.isValid()) {
            throw new IllegalArgumentException("Recipe is invalid ! Needs an ingredient!");
        }

        queuedRecipes.remove(recipe);
        queuedRecipes.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder));
    }

    protected void registerRecipesToServer(CommandSender sender, long start) {
        if (registered) {
            throw new IllegalAccessError("This class is already registered, create a new one!");
        }

        Iterator<Entry<BaseRecipe, RecipeInfo>> iterator;
        Entry<BaseRecipe, RecipeInfo> entry;
        BaseRecipe recipe;
        RecipeInfo info;

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
                Messages.sendAndLog(sender, String.format("%sRegistering recipes %d%%...", ChatColor.YELLOW, ((processed * 100) / size)));
                lastDisplay = time;
            }

            processed++;
        }

        registered = true; // mark this class as registered so it doesn't get re-registered
        queuedRecipes.clear(); // clear the queue to let the class vanish

        RecipeManager.getRecipeBooks().reload(sender); // (re)create recipe books for recipes

        Messages.send(sender, String.format("All done in %.3f seconds, %d recipes processed.", ((System.currentTimeMillis() - start) / 1000.0), processed));
    }
}
