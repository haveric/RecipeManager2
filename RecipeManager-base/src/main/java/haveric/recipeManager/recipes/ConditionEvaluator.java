package haveric.recipeManager.recipes;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.Settings;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.flags.FlagOverride;
import haveric.recipeManager.nms.NMSVersionHandler;
import haveric.recipeManager.recipes.campfire.RMCampfireRecipe;
import haveric.recipeManager.recipes.combine.CombineRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.furnace.RMBlastingRecipe;
import haveric.recipeManager.recipes.furnace.RMFurnaceRecipe;
import haveric.recipeManager.recipes.furnace.RMSmokingRecipe;
import haveric.recipeManager.recipes.stonecutting.RMStonecuttingRecipe;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo.RecipeOwner;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Map;

public class ConditionEvaluator {

    private RecipeRegistrator registrator;

    public ConditionEvaluator(RecipeRegistrator registrator) {
        this.registrator = registrator;
    }

    public boolean checkIngredients(ItemStack... ingredients) {
        Material toolType = null;

        for (ItemStack i : ingredients) {
            if (i != null && i.getType().getMaxDurability() > 0) {
                if (toolType == i.getType()) {
                    ErrorReporter.getInstance().error("Recipes can't have exactly 2 ingredients that are identical repairable items!", "Add another ingredient to make it work or even another tool and use " + FlagType.KEEP_ITEM + " flag to keep it.");
                    return false;
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
                recipe.getFlags().removeFlag(FlagType.REMOVE);
                recipe.getFlags().removeFlag(FlagType.OVERRIDE);

                ErrorReporter.getInstance().warning("Recipe was not found, can't override/remove it! Added as new recipe.", "Use 'rmextract' command to see the exact ingredients needed");

                return true; // allow recipe to be added
            } else if (registered.getOwner() == RMCRecipeInfo.RecipeOwner.RECIPEMANAGER && registered.getStatus() == null) {
                ErrorReporter.getInstance().error("Can't override/remove RecipeManager's recipes - just edit the recipe files!");

                return false; // can't re-add recipes
            }

            return true; // all ok, allow it to be added
        }

        if (registered != null) {
            if (registered.getOwner() == RMCRecipeInfo.RecipeOwner.RECIPEMANAGER) {
                if (!currentFile.equals(registered.getAdder())) {
                    ErrorReporter.getInstance().error("Recipe already created with this plugin, file: " + registered.getAdder());

                    return false; // can't re-add recipes
                }
            } else {
                recipe.getFlags().addFlag(new FlagOverride());

                if (!(recipe.hasFlag(FlagType.RESTRICT) || Settings.getInstance().getDisableOverrideWarnings())) {
                    ErrorReporter.getInstance().warning("Recipe already created by " + registered.getOwner() + ", recipe overwritten!", "You can use @override flag to overwrite the recipe or @remove to just remove it.");
                }

                return true; // allow to be added since we're overwriting it
            }
        }

        RMCRecipeInfo queued = getRecipeFromMap(recipe, registrator.getQueuedRecipes());

        if (queued != null) {
            ErrorReporter.getInstance().error("Recipe already created with this plugin, file: " + queued.getAdder());

            return false; // can't re-add recipes
        }

        return true; // nothing found, let it be added
    }

    private RMCRecipeInfo getRecipeFromMap(BaseRecipe recipe, Map<BaseRecipe, RMCRecipeInfo> map) {
        if (Version.has1_12Support()) {
            for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : map.entrySet()) {
                // Let's only use this special logic for recipes where RMCRecipeInfo has the bukkit pointer.
                if (entry.getValue().getOwner() == RecipeOwner.MINECRAFT && !entry.getKey().isVanillaSpecialRecipe()) {
                    Recipe bukkit = entry.getKey().getBukkitRecipe(true);
                    if (recipe instanceof CraftRecipe) {
                        if (bukkit instanceof ShapedRecipe) {
                            CraftRecipe craftRecipe = (CraftRecipe) recipe;
                            ItemStack[] matrix = craftRecipe.getIngredients();
                            Tools.trimItemMatrix(matrix);
                            ItemStack[] matrixMirror = Tools.mirrorItemMatrix(matrix);
                            int height = craftRecipe.getHeight();
                            int width = craftRecipe.getWidth();

                            if (NMSVersionHandler.getToolsRecipe().matchesShaped(bukkit, matrix, matrixMirror, width, height)) {
                                return entry.getValue();
                            }
                        }
                    } else if (recipe instanceof CombineRecipe) {
                        if (bukkit instanceof ShapelessRecipe) {
                            CombineRecipe combineRecipe = (CombineRecipe) recipe;
                            if (Version.has1_13Support()) {
                                if (NMSVersionHandler.getToolsRecipe().matchesShapeless(bukkit, combineRecipe.getIngredientChoiceList())) {
                                    return entry.getValue();
                                }
                            } else {
                                if (NMSVersionHandler.getToolsRecipe().matchesShapelessLegacy(bukkit, combineRecipe.getIngredients())) {
                                    return entry.getValue();
                                }
                            }
                        }
                    } else if (recipe instanceof RMFurnaceRecipe) {
                        RMFurnaceRecipe smeltRecipe = (RMFurnaceRecipe) recipe;
                        if (Version.has1_13Support()) {
                            if (NMSVersionHandler.getToolsRecipe().matchesFurnace(bukkit, new ItemStack(smeltRecipe.getIngredientChoice().get(0)))) {
                                return entry.getValue();
                            }
                        } else {
                            if (NMSVersionHandler.getToolsRecipe().matchesFurnace(bukkit, smeltRecipe.getIngredient())) {
                                return entry.getValue();
                            }
                        }
                    } else if (recipe instanceof RMBlastingRecipe) {
                        RMBlastingRecipe blastingRecipe = (RMBlastingRecipe) recipe;
                        if (NMSVersionHandler.getToolsRecipe().matchesBlasting(bukkit, new ItemStack(blastingRecipe.getIngredientChoice().get(0)))) {
                            return entry.getValue();
                        }
                    } else if (recipe instanceof RMSmokingRecipe) {
                        RMSmokingRecipe smokingRecipe = (RMSmokingRecipe) recipe;
                        if (NMSVersionHandler.getToolsRecipe().matchesSmoking(bukkit, new ItemStack(smokingRecipe.getIngredientChoice().get(0)))) {
                            return entry.getValue();
                        }
                    } else if (recipe instanceof RMCampfireRecipe) {
                        RMCampfireRecipe campfireRecipe = (RMCampfireRecipe) recipe;
                        if (NMSVersionHandler.getToolsRecipe().matchesCampfire(bukkit, new ItemStack(campfireRecipe.getIngredientChoice().get(0)))) {
                            return entry.getValue();
                        }
                    } else if (recipe instanceof RMStonecuttingRecipe) {
                        RMStonecuttingRecipe stonecuttingRecipe = (RMStonecuttingRecipe) recipe;
                        if (NMSVersionHandler.getToolsRecipe().matchesStonecutting(bukkit, new ItemStack(stonecuttingRecipe.getIngredientChoice().get(0)), stonecuttingRecipe.getResult())) {
                            return entry.getValue();
                        }
                    }

                }
            }
        }

        RMCRecipeInfo info = map.get(recipe);

        if (info == null && recipe instanceof CraftRecipe) {
            CraftRecipe cr = (CraftRecipe) recipe;
            cr.setMirrorShape(true);

            info = map.get(recipe);
        }

        return info;
    }
}
