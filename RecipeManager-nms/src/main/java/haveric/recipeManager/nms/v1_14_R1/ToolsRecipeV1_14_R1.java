package haveric.recipeManager.nms.v1_14_R1;

import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.nms.tools.BaseToolsRecipe;
import haveric.recipeManagerCommon.RMCVanilla;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.inventory.*;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.Recipe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Minecraft v1.13 NMS solution for matching if a RM recipe is already represented by an MC recipe.
 *
 * Basically duplicates the "internal" matching code.
 **/
public class ToolsRecipeV1_14_R1 extends BaseToolsRecipe {

    @Override
    public boolean matchesBlasting(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack blastingIngredient) {
        if (bukkitRecipe instanceof CraftSmokingRecipe) {
            CraftSmokingRecipe smokingRecipe = (CraftSmokingRecipe) bukkitRecipe;

            // recipes are indexed by input, not result. So, if you overlap on input we don't care about output in terms of
            // the server's understanding of recipes.
            return blastingIngredient.getType() == smokingRecipe.getInput().getType()
                    && (blastingIngredient.getDurability() == RMCVanilla.DATA_WILDCARD ||
                    smokingRecipe.getInput().getDurability() == RMCVanilla.DATA_WILDCARD ||
                    blastingIngredient.getDurability() == smokingRecipe.getInput().getDurability());
        }

        return false;
    }

    @Override
    public boolean matchesSmoking(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack smokingIngredient) {
        if (bukkitRecipe instanceof CraftSmokingRecipe) {
            CraftSmokingRecipe smokingRecipe = (CraftSmokingRecipe) bukkitRecipe;

            // recipes are indexed by input, not result. So, if you overlap on input we don't care about output in terms of
            // the server's understanding of recipes.
            return smokingIngredient.getType() == smokingRecipe.getInput().getType()
                    && (smokingIngredient.getDurability() == RMCVanilla.DATA_WILDCARD ||
                    smokingRecipe.getInput().getDurability() == RMCVanilla.DATA_WILDCARD ||
                    smokingIngredient.getDurability() == smokingRecipe.getInput().getDurability());
        }

        return false;
    }

    @Override
    public boolean matchesCampfire(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack campfireIngredient) {
        if (bukkitRecipe instanceof CraftCampfireRecipe) {
            CraftCampfireRecipe campfireRecipe = (CraftCampfireRecipe) bukkitRecipe;

            // recipes are indexed by input, not result. So, if you overlap on input we don't care about output in terms of
            // the server's understanding of recipes.
            return campfireIngredient.getType() == campfireRecipe.getInput().getType()
                    && (campfireIngredient.getDurability() == RMCVanilla.DATA_WILDCARD ||
                    campfireRecipe.getInput().getDurability() == RMCVanilla.DATA_WILDCARD ||
                    campfireIngredient.getDurability() == campfireRecipe.getInput().getDurability());
        }

        return false;
    }

    @Override
    public boolean matchesStonecutting(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack stonecuttingIngredient, org.bukkit.inventory.ItemStack stonecuttingResult) {
        if (bukkitRecipe instanceof CraftStonecuttingRecipe) {
            CraftStonecuttingRecipe stonecuttingRecipe = (CraftStonecuttingRecipe) bukkitRecipe;

            return stonecuttingIngredient.getType() == stonecuttingRecipe.getInput().getType()
                    && (stonecuttingIngredient.getDurability() == RMCVanilla.DATA_WILDCARD || stonecuttingRecipe.getInput().getDurability() == RMCVanilla.DATA_WILDCARD || stonecuttingIngredient.getDurability() == stonecuttingRecipe.getInput().getDurability())
                    && stonecuttingResult.getType() == stonecuttingRecipe.getResult().getType()
                    && (stonecuttingResult.getDurability() == RMCVanilla.DATA_WILDCARD || stonecuttingRecipe.getResult().getDurability() == RMCVanilla.DATA_WILDCARD || stonecuttingResult.getDurability() == stonecuttingRecipe.getResult().getDurability());
        }

        return false;
    }

    @Override
    public boolean matchesFurnace(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack furnaceIngredient) {
        if (bukkitRecipe instanceof CraftFurnaceRecipe) {
            CraftFurnaceRecipe furnaceRecipe = (CraftFurnaceRecipe) bukkitRecipe;

            // recipes are indexed by input, not result. So, if you overlap on input we don't care about output in terms of
            // the server's understanding of recipes.
            return furnaceIngredient.getType() == furnaceRecipe.getInput().getType()
                    && (furnaceIngredient.getDurability() == RMCVanilla.DATA_WILDCARD ||
                    furnaceRecipe.getInput().getDurability() == RMCVanilla.DATA_WILDCARD ||
                    furnaceIngredient.getDurability() == furnaceRecipe.getInput().getDurability());
        }

        return false;
    }

    @Override
    protected boolean matchesShapedMatrix(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack[] ingredients, int width, int height) {
        if (bukkitRecipe instanceof CraftShapedRecipe) {
            CraftShapedRecipe shapedRecipe = (CraftShapedRecipe) bukkitRecipe;

            // Shortcut; if recipes don't have same geometry, not the same.
            if (width != shapedRecipe.getShape()[0].length() || height != shapedRecipe.getShape().length) {
                return false;
            }

            // No such luck, but we do know geometry is the same and that is useful.
            // roughly, we want to test, for each unique item type in the recipe, test it
            // against every variation (until first successful match) of item types
            // hidden by bukkit within the recipe definition.

            try {
                Field recipeF = CraftShapedRecipe.class.getDeclaredField("recipe"); // pointer to MC recipe.
                recipeF.setAccessible(true);
                ShapedRecipes recipe = (ShapedRecipes) recipeF.get(shapedRecipe);

                Field itemsF = ShapedRecipes.class.getDeclaredField("items"); // pointer to _full_ list of items, hidden by Bukkit.
                itemsF.setAccessible(true);
                NonNullList<RecipeItemStack> items = (NonNullList<RecipeItemStack>) itemsF.get(recipe);

                char c = 'a';
                int i = 0;

                for (RecipeItemStack list : items) {
                    org.bukkit.inventory.ItemStack baseItem = ingredients[(i / width) * 3 + (i % width)];
                    if (baseItem == null) {
                        baseItem = new org.bukkit.inventory.ItemStack(Material.AIR);
                    }
                    if (list != null && list.choices.length > 0) {
                        boolean match = false;
                        for (ItemStack stack : list.choices) {
                            org.bukkit.inventory.ItemStack bukkitItem = new org.bukkit.inventory.ItemStack(
                                    CraftMagicNumbers
                                            .getMaterial(stack.getItem()),1);
                            if (bukkitItem.getType() == baseItem.getType()
                                    && (baseItem.getDurability() == RMCVanilla.DATA_WILDCARD
                                    || bukkitItem.getDurability() == RMCVanilla.DATA_WILDCARD
                                    || baseItem.getDurability() == bukkitItem.getDurability())) {
                                match = true;
                                break; // we need find only one match from all items. Stop when we have.
                            }
                        }
                        if (!match) {
                            return false; // fast fail.
                        }
                    } else {
                        if (baseItem.getType() != Material.AIR) {
                            return false; // fast fail.
                        }
                    }
                    c++;
                    i++;
                }
                // if we made it through the whole list, and never failed to match, we have a match.
                    /*MessageSender.getInstance().info("NMS for 1.13 matched recipe " + baseRecipe.getName() + " with shaped " +
                            recipe.key.toString() + ":" + bukkitRecipe.getResult());*/
                return true;
            } catch (Exception e) {
                // MessageSender.getInstance().error(null, e, "Failed during craft recipe lookup");
            }
        }

        return false;
    }

    @Override
    public boolean matchesShapeless(Recipe bukkitRecipe, List<org.bukkit.inventory.ItemStack> ingredients, List<org.bukkit.inventory.ItemStack> ingredientsList) {
        if (bukkitRecipe instanceof CraftShapelessRecipe) {
            CraftShapelessRecipe shapelessRecipe = (CraftShapelessRecipe) bukkitRecipe;

            // Shortcut; if recipes don't have same # of itemtypes, not the same.
            if (ingredientsList.size() != shapelessRecipe.getIngredientList().size()) {
                return false;
            }

            // No such luck, but we do know item size is the same and that is useful.
            // roughly, we want to test, for each unique item type in the recipe, test it
            // against every variation (until first successful match) of item types
            // hidden by bukkit within the recipe definition.

            try {
                Field recipeF = CraftShapelessRecipe.class.getDeclaredField("recipe"); // pointer to MC recipe.
                recipeF.setAccessible(true);
                ShapelessRecipes recipe = (ShapelessRecipes) recipeF.get(shapelessRecipe);

                Field itemsF = ShapelessRecipes.class.getDeclaredField("ingredients"); // pointer to _full_ list of items, hidden by Bukkit.
                itemsF.setAccessible(true);
                NonNullList<RecipeItemStack> items = (NonNullList<RecipeItemStack>) itemsF.get(recipe);
                ArrayList<RecipeItemStack> copy = new ArrayList<>(items);

                // from Bukkit / spigot...
                for (org.bukkit.inventory.ItemStack baseItem : ingredientsList) {
                    boolean match = false;
                    if (copy.size() == 0) { // we ran out of things to match against but still have baseItems.
                            /*MessageSender.getInstance().info("NMS for 1.13 did not match recipe " + baseRecipe.getName() + " with shapeless " +
                                    recipe.key.toString() + ":" + bukkitRecipe.getResult());*/
                        return false;
                    }
                    for (RecipeItemStack list : copy) {
                        if (list != null && list.choices.length > 0) {
                            for (ItemStack stack : list.choices) {
                                org.bukkit.inventory.ItemStack bukkitItem = new org.bukkit.inventory.ItemStack(
                                        CraftMagicNumbers
                                                .getMaterial(stack.getItem()), 1);
                                if (bukkitItem.getType() == baseItem.getType()
                                        && (baseItem.getDurability() == RMCVanilla.DATA_WILDCARD
                                        || bukkitItem.getDurability() == RMCVanilla.DATA_WILDCARD
                                        || baseItem.getDurability() == bukkitItem.getDurability())) {
                                    match = true;
                                    copy.remove(list);
                                    break;
                                }
                            }
                        }
                        if (match) break; // we found a match for this recipeitemstack.
                    }
                    if (!match) {
                            /*MessageSender.getInstance().info("NMS for 1.13 did not match recipe " + baseRecipe.getName() + " with shapeless " +
                                    recipe.key.toString() + ":" + bukkitRecipe.getResult());*/
                        return false;
                    }
                }
                if (copy.size() == 0) { // yay, we've run out of base items AND run out of recipeItemStacks.
                    // every base item has matched against a unique RecipeItemStack.
                        /*MessageSender.getInstance().info("NMS for 1.13 matched recipe " + baseRecipe.getName() + " with shapeless " +
                                recipe.key.toString() + ":" + bukkitRecipe.getResult());*/
                    return true;
                } else {
                        /*MessageSender.getInstance().info("NMS for 1.13 did not match recipe " + baseRecipe.getName() + " with shapeless " +
                                recipe.key.toString() + ":" + bukkitRecipe.getResult());*/
                    return false;
                }
            } catch (Exception e) {
                MessageSender.getInstance().error(null, e, "Failed during combine recipe lookup");
            }
        }

        return false;
    }
}
