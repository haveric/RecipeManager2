package haveric.recipeManager.tools;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftShapelessRecipe;
import org.bukkit.inventory.Recipe;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CombineRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.SmeltRecipe;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NonNullList;
import net.minecraft.server.v1_12_R1.RecipeItemStack;
import net.minecraft.server.v1_12_R1.ShapedRecipes;
import net.minecraft.server.v1_12_R1.ShapelessRecipes;

public class ToolsRecipeV1_12 {
    public static boolean matches(BaseRecipe baseRecipe, Recipe bukkitRecipe) {

        if (bukkitRecipe instanceof CraftFurnaceRecipe) {
            CraftFurnaceRecipe furnaceRecipe = (CraftFurnaceRecipe) bukkitRecipe;
            if (baseRecipe instanceof SmeltRecipe) {
                SmeltRecipe furnaceBase = (SmeltRecipe) baseRecipe;
                // recipes are indexed by input, not result. So, if you overlap on input we don't care about output in terms of
                // the server's understanding of recipes.
                return furnaceBase.getIngredient().getType() == furnaceRecipe.getInput().getType()
                        && (furnaceBase.getIngredient().getDurability() == Vanilla.DATA_WILDCARD || furnaceBase
                                .getIngredient().getDurability() == furnaceRecipe.getInput().getDurability());
            }
        } else if (bukkitRecipe instanceof CraftShapedRecipe) {
            CraftShapedRecipe shapedRecipe = (CraftShapedRecipe) bukkitRecipe;
            if (baseRecipe instanceof CraftRecipe) {
                CraftRecipe craftBase = (CraftRecipe) baseRecipe;

                // Shortcut; if recipes don't have same geometry, not the same.
                if (craftBase.getWidth() != shapedRecipe.getShape()[0].length()
                        || craftBase.getHeight() != shapedRecipe.getShape().length) {
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

                    // from Bukkit / spigot...
                    StringBuffer compair = new StringBuffer("   ");
                    char c = 'a';
                    int i = 0;
                    org.bukkit.inventory.ItemStack[] baseItems = craftBase.getIngredients();
                    compair.append(baseItems.length).append("v").append(items.size()).append("\n   ");
                    for (RecipeItemStack list : items) {
                        org.bukkit.inventory.ItemStack baseItem = baseItems[(i / craftBase.getWidth()) * 3
                                + (i % craftBase.getWidth())];
                        if (baseItem == null) {
                            baseItem = new org.bukkit.inventory.ItemStack(Material.AIR);
                        }
                        compair.append(" ").append(c).append("[").append(baseItem.toString()).append("]v[");
                        if (list != null && list.choices.length > 0) {
                            boolean match = false;
                            for (ItemStack stack : list.choices) {
                                org.bukkit.inventory.ItemStack bukkitItem = new org.bukkit.inventory.ItemStack(
                                        org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers
                                                .getMaterial(stack.getItem()),
                                        1, (short) stack.getData());
                                compair.append(bukkitItem.toString()).append(",");
                                if (bukkitItem.getType() == baseItem.getType()
                                        && (baseItem.getDurability() == Vanilla.DATA_WILDCARD
                                                || bukkitItem.getDurability() == Vanilla.DATA_WILDCARD
                                                || baseItem.getDurability() == bukkitItem.getDurability())) {
                                    match = true;
                                    break; // we need find only one match from all items. Stop when we have.
                                }
                            }
                            compair.setLength(compair.length() - 1);
                            if (!match) {
                                compair.append("**FAIL*");
                                try {
                                    // RecipeManager.getPlugin().getLogger().info(compair.toString());
                                } catch (Exception e) {
                                }
                                return false; // fast fail.
                            }
                        } else {
                            if (baseItem.getType() == Material.AIR) {
                                compair.append("AIR");
                            } else {
                                compair.append("**FAIL*");
                                try {
                                    // RecipeManager.getPlugin().getLogger().info(compair.toString());
                                } catch (Exception e) {
                                }
                                return false; // fast fail.
                            }
                        }
                        compair.append("]");
                        c++;
                        i++;
                        if (i % craftBase.getWidth() == 0) {
                            compair.append("\n   ");
                        }
                    }
                    compair.append("\n   **MATCH*");
                    try {
                        // RecipeManager.getPlugin().getLogger().info(compair.toString());
                    } catch (Exception e) {
                    }
                    // if we made it through the whole list, and never failed to match, we have a match.
                    return true;

                } catch (Exception e) {
                    try {
                        RecipeManager.getPlugin().getLogger().log(Level.WARNING, "Failed during recipe lookup", e);
                    } catch (Exception f) {

                    }
                }
            }
        } else if (bukkitRecipe instanceof CraftShapelessRecipe) {
            CraftShapelessRecipe shapelessRecipe = (CraftShapelessRecipe) bukkitRecipe;
            if (baseRecipe instanceof CombineRecipe) {
                CombineRecipe combineBase = (CombineRecipe) baseRecipe;

                // Shortcut; if recipes don't have same # of itemtypes, not the same.
                if (combineBase.getIngredients().size() != shapelessRecipe.getIngredientList().size()) {
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

                    // from Bukkit / spigot...
                    int i = 0;
                    List<org.bukkit.inventory.ItemStack> baseItems = combineBase.getIngredients();
                    for (RecipeItemStack list : items) {
                        org.bukkit.inventory.ItemStack baseItem = baseItems.get(i);
                        if (list != null && list.choices.length > 0) {
                            boolean match = false;
                            for (ItemStack stack : list.choices) {
                                org.bukkit.inventory.ItemStack bukkitItem = new org.bukkit.inventory.ItemStack(
                                        org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers
                                                .getMaterial(stack.getItem()),
                                        1, (short) stack.getData());
                                if (bukkitItem.getType() == baseItem.getType()
                                        && (baseItem.getDurability() == Vanilla.DATA_WILDCARD
                                                || bukkitItem.getDurability() == Vanilla.DATA_WILDCARD
                                                || baseItem.getDurability() == bukkitItem.getDurability())) {
                                    match = true;
                                    break; // we need find only one match from all items. Stop when we have.
                                }
                            }
                            if (!match) {
                                return false; // fast fail.
                            }
                        }
                        i++;
                    }
                    // if we made it through the whole list, and never failed to match, we have a match.
                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
}
