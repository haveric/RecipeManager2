package haveric.recipeManager.recipes;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.common.recipes.RMCRecipeInfo.RecipeOwner;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.flags.recipe.FlagOverride;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConditionEvaluator {

    private RecipeRegistrator registrator;

    public ConditionEvaluator(RecipeRegistrator registrator) {
        this.registrator = registrator;
    }

    public boolean checkRecipeChoices(Map<Character, RecipeChoice> ingredientsChoiceMap) {
        List<Material> repairableItems = new ArrayList<>();

        for (Map.Entry<Character, RecipeChoice> entry : ingredientsChoiceMap.entrySet()) {
            RecipeChoice choice = entry.getValue();

            if (choice instanceof RecipeChoice.MaterialChoice) {
                RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
                List<Material> materials = materialChoice.getChoices();

                for (Material material : materials) {
                    if (material.getMaxDurability() > 0) {
                        if (repairableItems.contains(material)) {
                            return ErrorReporter.getInstance().error("Recipes can't have exactly 2 ingredients that are identical repairable items!", "Add another ingredient to make it work or even another tool and use " + FlagType.KEEP_ITEM + " flag to keep it.");
                        } else {
                            repairableItems.add(material);
                        }
                    }
                }
            } else if (choice instanceof RecipeChoice.ExactChoice) {
                RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
                List<ItemStack> items = exactChoice.getChoices();

                for (ItemStack item : items) {
                    Material material = item.getType();
                    if (material.getMaxDurability() > 0) {
                        if (repairableItems.contains(material)) {
                            return ErrorReporter.getInstance().error("Recipes can't have exactly 2 ingredients that are identical repairable items!", "Add another ingredient to make it work or even another tool and use " + FlagType.KEEP_ITEM + " flag to keep it.");
                        } else {
                            repairableItems.add(material);
                        }
                    }
                }
            }
        }

        return true;
    }

    public boolean checkMaterialChoices(Map<Character, List<Material>> ingredientsChoiceMap) {
        List<Material> repairableItems = new ArrayList<>();

        for (Map.Entry<Character, List<Material>> entry : ingredientsChoiceMap.entrySet()) {
            List<Material> materials = entry.getValue();

            for (Material material : materials) {
                if (material.getMaxDurability() > 0) {
                    if (repairableItems.contains(material)) {
                        return ErrorReporter.getInstance().error("Recipes can't have exactly 2 ingredients that are identical repairable items!", "Add another ingredient to make it work or even another tool and use " + FlagType.KEEP_ITEM + " flag to keep it.");
                    } else {
                        repairableItems.add(material);
                    }
                }
            }
        }

        return true;
    }

    public boolean checkIngredients(ItemStack... ingredients) {
        Material toolType = null;

        for (ItemStack i : ingredients) {
            if (i != null && i.getType().getMaxDurability() > 0) {
                if (toolType == i.getType()) {
                    return ErrorReporter.getInstance().error("Recipes can't have exactly 2 ingredients that are identical repairable items!", "Add another ingredient to make it work or even another tool and use " + FlagType.KEEP_ITEM + " flag to keep it.");
                }

                toolType = i.getType();
            }
        }

        return true;
    }

    public boolean recipeExists(BaseRecipe recipe, int directiveLine, String currentFile) {
        ErrorReporter.getInstance().setLine(directiveLine); // set the line to point to the directive rather than the last read line!
        RMCRecipeInfo registered = getRecipeFromMap(recipe, RecipeManager.getRecipes().getIndex());

        if (recipe.hasFlag(FlagType.OVERRIDE) || recipe.hasFlag(FlagType.REMOVE)) {
            if (registered == null) {
                if (recipe.hasFlag(FlagType.REMOVE)) {
                    ErrorReporter.getInstance().warning("Recipe was not found, can't remove it!", "Use 'rmextract' command to see the exact ingredients needed");
                    return false; // Don't add a remove recipe
                } else {
                    recipe.getFlags().removeFlag(FlagType.OVERRIDE);
                    ErrorReporter.getInstance().warning("Recipe was not found, can't override it! Added as new recipe.", "Use 'rmextract' command to see the exact ingredients needed");
                    return true; // allow recipe to be added
                }
            } else if (registered.getOwner() == RMCRecipeInfo.RecipeOwner.RECIPEMANAGER && registered.getStatus() == null) {
                // can't re-add recipes
                return ErrorReporter.getInstance().error("Can't override/remove RecipeManager's recipes - just edit the recipe files!");
            }

            return true; // all ok, allow it to be added
        }

        if (registered != null) {
            if (registered.getOwner() == RMCRecipeInfo.RecipeOwner.RECIPEMANAGER) {
                if (!currentFile.equals(registered.getAdder())) {
                    // can't re-add recipes
                    return ErrorReporter.getInstance().error("Recipe already created with this plugin, file: " + registered.getAdder());
                }
            } else {
                recipe.getFlags().addFlag(new FlagOverride(), FlagBit.RECIPE);

                if (!(recipe.hasFlag(FlagType.RESTRICT) || RecipeManager.getSettings().getDisableOverrideWarnings())) {
                    ErrorReporter.getInstance().warning("Recipe already created by " + registered.getOwner() + ", recipe overwritten!", "You can use @override flag to overwrite the recipe or @remove to just remove it.");
                }

                return true; // allow to be added since we're overwriting it
            }
        }

        RMCRecipeInfo queued = getRecipeFromMap(recipe, registrator.getQueuedRecipes());
        RMCRecipeInfo queuedRemovedRecipes = getRecipeFromMap(recipe, registrator.getQueuedRemovedRecipes());

        if (queued != null && queuedRemovedRecipes != null) {
            // can't re-add recipes
            return ErrorReporter.getInstance().error("Recipe already created with this plugin, file: " + queued.getAdder());
        }

        return true; // nothing found, let it be added
    }

    private RMCRecipeInfo getRecipeFromMap(BaseRecipe recipe, Map<BaseRecipe, RMCRecipeInfo> map) {
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : map.entrySet()) {
            // Let's only use this special logic for recipes where RMCRecipeInfo has the bukkit pointer.
            if (entry.getValue().getOwner() == RecipeOwner.MINECRAFT && !entry.getKey().isVanillaSpecialRecipe()) {
                if (recipe.hashCode() == entry.getKey().hashCode()) {
                    return entry.getValue();
                }
            }
        }

        return map.get(recipe);
    }
}
