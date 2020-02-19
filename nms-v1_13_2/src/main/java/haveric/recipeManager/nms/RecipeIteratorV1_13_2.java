package haveric.recipeManager.nms;

import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.BaseRecipeIterator;
import haveric.recipeManagerCommon.recipes.AbstractBaseRecipe;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_13_R2.inventory.RecipeIterator;
import org.bukkit.inventory.Recipe;

import java.lang.reflect.Field;
import java.util.Iterator;

public class RecipeIteratorV1_13_2 extends BaseRecipeIterator implements Iterator<Recipe> {
    private Iterator<IRecipe> recipes;

    private IRecipe removeRecipe = null;

    public RecipeIteratorV1_13_2() {
        Iterator<Recipe> backing = getBukkitRecipeIterator();
        if (backing instanceof RecipeIterator) {
            recipes = ((CraftServer)Bukkit.getServer()).getServer().getCraftingManager().b().iterator();
        } else {
            throw new IllegalArgumentException("This version is not supported.");
        }
    }

    /**
     * If nothing more is accessible, finalize any removals before informing caller of nothing new.
     */
    @Override
    public boolean hasNext() {
        boolean next = recipes.hasNext();
        if (!next) {
            finish();
        }
        return next;
    }

    @Override
    public Recipe next() {
        if (recipes.hasNext()) {
            removeRecipe = recipes.next();
            try {
                return removeRecipe.toBukkitRecipe();
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                try {
                    Field keyF = removeRecipe.getClass().getField("key");
                    MinecraftKey key = (MinecraftKey) keyF.get(removeRecipe);
                    MessageSender.getInstance().error(null, aioobe, "Failure while traversing iterator on recipe " + key.toString());
                } catch (Exception e) {
                    MessageSender.getInstance().error(null, e, "Failure while traversing iterator, unable to determine recipe.");
                }
            }
        }

        return null;
    }

    /**
     * Backing list is now immutable in 1.12 - 1.14.
     * 
     * We have two modes of operation. For recipes, we don't remove, we simply replace them with dummy data
     * that can never be matched. In this way, ID ordering is preserved and we avoid any unpleasantness with 
     * the fact that the Client and Server both assume recipes have a specific ID order sequence to them.
     *
     * For Smelting, we register the requests to remove, and perform those removals when we are done iterating
     * or are requested to finalize. 
     */
    public void remove() {
        // MessageSender.getInstance().info("NMS for 1.13 removing recipe " + removeRecipe);
        try {
            if (removeRecipe instanceof ShapedRecipes) {
                ShapedRecipes shaped = (ShapedRecipes) removeRecipe;
                Field widthF = stripPrivateFinal(ShapedRecipes.class, "width");
                Field heightF = stripPrivateFinal(ShapedRecipes.class, "height");
                Field itemsF = stripPrivateFinal(ShapedRecipes.class, "items");
                Field resultF = stripPrivateFinal(ShapedRecipes.class, "result");

                // now for the _real_ fun, modifying an unmodifiable recipe.
                // So for shaped recipes, my thought is just to replace the ItemStack with something
                // nonsensical, set height and width to 1, and hope it isn't cached in too many places.
                // Oh, and set result to air.
                widthF.setInt(shaped, 1);
                heightF.setInt(shaped, 1);
                resultF.set(shaped, new ItemStack(Items.AIR, 1));
                itemsF.set(shaped, NonNullList.a(1, RecipeItemStack.a(new Item[] {new ItemNameTag(new Item.Info())})));
            } else if (removeRecipe instanceof ShapelessRecipes) {
                ShapelessRecipes shapeless = (ShapelessRecipes) removeRecipe;
                Field ingredientsF = stripPrivateFinal(ShapelessRecipes.class, "ingredients");
                Field resultF = stripPrivateFinal(ShapelessRecipes.class, "result");

                resultF.set(shapeless, new ItemStack(Items.AIR, 1));
                ingredientsF.set(shapeless, NonNullList.a(1, RecipeItemStack.a(new Item[]{new ItemNameTag(new Item.Info())})));
            } else if (removeRecipe instanceof FurnaceRecipe) {
                recipes.remove();
            } else {
                throw new IllegalStateException("You cannot replace a grid recipe with a " + removeRecipe.getClass().getName() + " recipe!");
            }
        } catch (Exception e) {
            MessageSender.getInstance().error(null, e, "NMS failure for v1.13 support during grid recipe removal");
        }
    }

    @Override
    public Iterator<Recipe> getIterator() {
        return this;
    }

    /**
     * Backing list is now immutable in 1.12 - 1.14.
     * 
     * To prevent bad linking to RM unique recipes, we add a new mode "replace" which can be leveraged 
     * instead of remove, to link the MC recipe to the RM recipe directly. We don't actually then
     * add the RM recipe to Bukkit, only to our indexes.
     * 
     * For Smelting, use traditional remove / add.
     */
    @Override
    public void replace(AbstractBaseRecipe recipe, org.bukkit.inventory.ItemStack overrideItem) {
        // A _key_ assumption with replace is that the original items and shape is _unchanged_. Only result is overridden.
        try {
            // MessageSender.getInstance().info("NMS for 1.13 replacing recipe " + recipe.getName());
            if (removeRecipe instanceof ShapedRecipes) {
                ShapedRecipes shaped = (ShapedRecipes) removeRecipe;
                Field resultF = stripPrivateFinal(ShapedRecipes.class, "result");

                ItemStack overrideF = CraftItemStack.asNMSCopy(overrideItem);
                resultF.set(shaped, overrideF);
            } else if (removeRecipe instanceof ShapelessRecipes) {
                ShapelessRecipes shapeless = (ShapelessRecipes) removeRecipe;
                Field resultF = stripPrivateFinal(ShapelessRecipes.class, "result");

                ItemStack overrideF = CraftItemStack.asNMSCopy(overrideItem);
                resultF.set(shapeless, overrideF);
            } else if (removeRecipe instanceof FurnaceRecipe) {
                FurnaceRecipe furnace = (FurnaceRecipe) removeRecipe;
                Field resultF = stripPrivateFinal(FurnaceRecipe.class, "result");

                ItemStack overrideF = CraftItemStack.asNMSCopy(overrideItem);
                resultF.set(furnace, overrideF);
            } else {
                throw new IllegalStateException("You cannot replace a grid recipe with a " + removeRecipe.getClass().getName() + " recipe!");
            }
        } catch (Exception e) {
            MessageSender.getInstance().error(null, e, "NMS failure for v1.13 support during grid recipe replace");
        }
    }

    @Override
    public void finish() {
    }
}
