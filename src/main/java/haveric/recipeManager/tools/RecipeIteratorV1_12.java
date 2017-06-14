package haveric.recipeManager.tools;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.inventory.Recipe;

import net.minecraft.server.v1_12_R1.CraftingManager;
import net.minecraft.server.v1_12_R1.IRecipe;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.RecipesFurnace;
import net.minecraft.server.v1_12_R1.RegistryMaterials;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.inventory.RecipeIterator;

public class RecipeIteratorV1_12 implements Iterator<Recipe> {
	private RecipeIterator backing = null;
	private Iterator<IRecipe> recipes = null;
	private List<MinecraftKey> recipeRemoves = new LinkedList<MinecraftKey>();
	private Iterator<ItemStack> smeltingCustom = null;
	private List<ItemStack> recipeSmeltingCustom = new LinkedList<ItemStack>();
	private Iterator<ItemStack> smeltingVanilla = null;
	private List<ItemStack> recipeSmeltingVanilla = new LinkedList<ItemStack>();
	enum RemoveFrom {
		RECIPES,
		CUSTOM,
		VANILLA
	}
	RemoveFrom removeFrom = null;
	IRecipe removeRecipe = null;
	ItemStack removeItem = null;
	
	public RecipeIteratorV1_12(Iterator<Recipe> backing) {
		if (backing instanceof RecipeIterator) {
			backing = (RecipeIterator) backing;
			recipes = CraftingManager.recipes.iterator();
			smeltingCustom = RecipesFurnace.getInstance().customRecipes.keySet().iterator();
			smeltingVanilla = RecipesFurnace.getInstance().recipes.keySet().iterator();
		} else {
			throw new IllegalArgumentException("This version is not supported.");
		}
	}
	
	/**
	 * If nothing more is accessible, finalize any removals before informing caller of nothing new.
	 */
	@Override
	public boolean hasNext() {
		boolean next = recipes.hasNext() || smeltingCustom.hasNext() || smeltingVanilla.hasNext();
		if (!next) {
			finish();
		}
		return next;
	}

	@Override
    public Recipe next() {
        if (recipes.hasNext()) {
            removeFrom = RemoveFrom.RECIPES;
            removeRecipe = recipes.next();
            return removeRecipe.toBukkitRecipe();
        } else {
            ItemStack item;
            if (smeltingCustom.hasNext()) {
                removeFrom = RemoveFrom.CUSTOM;
                item = smeltingCustom.next();
            } else {
                removeFrom = RemoveFrom.VANILLA;
                item = smeltingVanilla.next();
            }
            removeItem = item;

            CraftItemStack stack = CraftItemStack.asCraftMirror(RecipesFurnace.getInstance().getResult(item));

            return new CraftFurnaceRecipe(stack, CraftItemStack.asCraftMirror(item));
        }
    }

	/**
	 * Backing list is now immutable in 1.12. 
	 * 
	 * Instead of removing directly, we accrue removals, and apply them when instructed to
	 * or when we reach the end of the list.
	 */
    public void remove() {
        if (removeFrom == null) {
            throw new IllegalStateException();
        }
        switch(removeFrom) {
        case RECIPES:
        	try {
				Field keyF = removeRecipe.getClass().getField("key");
				MinecraftKey key = (MinecraftKey) keyF.get(removeRecipe);
				System.err.println("Registered to remove " + key.toString());
				recipeRemoves.add(key);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        case CUSTOM:
        	recipeSmeltingCustom.add(removeItem);
        case VANILLA:
        	recipeSmeltingVanilla.add(removeItem);
        }
    }

    public void finish() {
    	if (!recipeRemoves.isEmpty()) {
    		RegistryMaterials<MinecraftKey, IRecipe> existing = CraftingManager.recipes;
    		CraftingManager.recipes = new RegistryMaterials<MinecraftKey, IRecipe>();
    		try {
    			Field c = CraftingManager.class.getDeclaredField("c");
    			c.setAccessible(true);
    			c.set(null, (Integer) 0); // reset recipe ID count.
    			
    			existing.iterator().forEachRemaining(recipe -> {
					try {
						Field keyF = recipe.getClass().getField("key");
						MinecraftKey key = (MinecraftKey) keyF.get(recipe);
						if (!recipeRemoves.contains(key)) {
							CraftingManager.a(key, recipe); // just don't add ones we want to remove.
						}
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			});
    			
    		} catch (Exception e) {
				throw new IllegalStateException("Unable to access craft manager!");
			}
    	}
    	if (!recipeSmeltingCustom.isEmpty()) {
    		RecipesFurnace furnaces = RecipesFurnace.getInstance();
    		recipeSmeltingCustom.forEach(
    				item -> {
    					furnaces.customRecipes.remove(item);
    					furnaces.customExperience.remove(item);
    				});
    	}
    	if (!recipeSmeltingVanilla.isEmpty()) {
    		RecipesFurnace furnaces = RecipesFurnace.getInstance();
    		try {
    			Field experienceF = RecipesFurnace.class.getDeclaredField("experience");
    			experienceF.setAccessible(true);
    			@SuppressWarnings("unchecked")
				Map<ItemStack, Float> experience = (Map<ItemStack, Float>) experienceF.get(furnaces); 
	    		recipeSmeltingVanilla.forEach(
	    				item -> {
	    					furnaces.recipes.remove(item);
	    					experience.remove(item);
	    				});
    		}catch (Exception e) {
				// TODO: handle exception
    			e.printStackTrace();
			}
	    		
    	}
    }
}
