package haveric.recipeManager;

import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.common.recipes.RMCRecipeInfo.RecipeOwner;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.recipes.BaseRecipe;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RecipeRegistrator {
    private Map<BaseRecipe, RMCRecipeInfo> queuedRecipes = new HashMap<>();
    private Map<BaseRecipe, RMCRecipeInfo> queuedRemovedRecipes = new HashMap<>();
    private boolean registered = false;

    protected RecipeRegistrator() {
    }

    public void queueRecipe(BaseRecipe recipe, String adder) {
        if (registered) {
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        }

        if (!recipe.isValid()) {
            throw new IllegalArgumentException(recipe.getInvalidErrorMessage());
        }

        if (recipe.hasFlag(FlagType.REMOVE)) {
            queuedRemovedRecipes.remove(recipe); // if exists, update key too!
            queuedRemovedRecipes.put(recipe, new RMCRecipeInfo(RecipeOwner.RECIPEMANAGER, adder));
        } else {
            queuedRecipes.remove(recipe); // if exists, update key too!
            queuedRecipes.put(recipe, new RMCRecipeInfo(RecipeOwner.RECIPEMANAGER, adder));
        }
    }

    protected void registerRecipesToServer(CommandSender sender, long start) {
        if (registered) {
            throw new IllegalAccessError("This class is already registered, create a new one!");
        }

        Entry<BaseRecipe, RMCRecipeInfo> entry;
        BaseRecipe recipe;
        RMCRecipeInfo info;

        // Remove old custom recipes/re-add old original recipes
        Iterator<Entry<BaseRecipe, RMCRecipeInfo>> oldIterator = RecipeManager.getRecipes().index.entrySet().iterator();

        while (oldIterator.hasNext()) {
            entry = oldIterator.next();
            info = entry.getValue();
            recipe = entry.getKey();

            if (info.getOwner() == RecipeOwner.RECIPEMANAGER) {
                oldIterator.remove();
                recipe.remove();
            }
        }

        // TODO registering event or something to re-register plugin recipes

        Iterator<Entry<BaseRecipe, RMCRecipeInfo>> removedIterator = queuedRemovedRecipes.entrySet().iterator();
        Iterator<Entry<BaseRecipe, RMCRecipeInfo>> iterator = queuedRecipes.entrySet().iterator();
        long lastDisplay = System.currentTimeMillis();
        long time;
        int processed = 0;
        int size = queuedRemovedRecipes.size() + queuedRecipes.size();

        // Handle remove recipes first to avoid removing new recipes
        while (removedIterator.hasNext()) {
            entry = removedIterator.next();

            RecipeManager.getRecipes().registerRecipe(entry.getKey(), entry.getValue());

            time = System.currentTimeMillis();

            if (time > lastDisplay + 1000) {
                MessageSender.getInstance().sendAndLog(sender, String.format("%sRegistering recipes %d%%...", RMCChatColor.YELLOW, ((processed * 100) / size)));
                lastDisplay = time;
            }

            processed++;
        }

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
        // clear the queues to let the classes vanish
        queuedRemovedRecipes.clear();
        queuedRecipes.clear();

        RecipeBooks.getInstance().reloadAfterRecipes(sender); // (re)create recipe books for recipes

        int numSimple = Recipes.getInstance().getNumRecipesSimple();
        int numRM = Recipes.getInstance().getNumRecipesRequireRecipeManager();
        int numRemoved = Recipes.getInstance().getNumRemovedRecipes();

        String details = "";
        if (numSimple > 0) {
            details += "Simple: " + numSimple;
        }

        if (numRM > 0) {
            if (numSimple > 0) {
                details += " - ";
            }
            details += "RM: " + numRM;
        }
        if (numRemoved > 0) {
            if (numSimple > 0 || numRM > 0) {
                details += " - ";
            }
            details += "Removed: " + numRemoved;
        }

        if (numSimple > 0 || numRM > 0 || numRemoved > 0) {
            details = "(" + details + ")";
        }

        String recipeString;
        if (processed > 1) {
            recipeString = "recipes";
        } else {
            recipeString = "recipe";
        }
        MessageSender.getInstance().send(sender, String.format("All done in %.3f seconds, %d %s processed.%s", ((System.currentTimeMillis() - start) / 1000.0), processed, recipeString, details));
    }

    public Map<BaseRecipe, RMCRecipeInfo> getQueuedRecipes() {
        return queuedRecipes;
    }

    public Map<BaseRecipe, RMCRecipeInfo> getQueuedRemovedRecipes() {
        return queuedRemovedRecipes;
    }

    public int getNumQueuedRecipes() {
        return queuedRemovedRecipes.size() + queuedRecipes.size();
    }
}
