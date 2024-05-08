package haveric.recipeManager.recipes.cooking.campfire;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.BaseRecipeEvents;
import haveric.recipeManager.recipes.cooking.campfire.data.RMCampfireData;
import haveric.recipeManager.recipes.cooking.campfire.data.RMCampfires;
import org.bukkit.block.Block;
import org.bukkit.block.Campfire;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.inventory.ItemStack;

public class RMCampfireStartEvent extends BaseRecipeEvents {
    public RMCampfireStartEvent() { }

    @EventHandler(priority = EventPriority.LOW)
    public void rmCampfireStartEvent(CampfireStartEvent event) {
        Block block = event.getBlock();
        Campfire campfire = (Campfire) block.getState();
        RMCampfireData data = RMCampfires.get(campfire.getLocation());

        int slot = data.getLastUsedSlot();
        if (slot != -1) {
            ItemStack ingredient = event.getSource();
            BaseRecipe baseRecipe = RecipeManager.getRecipes().getRecipe(RMCRecipeType.CAMPFIRE, ingredient);
            if (baseRecipe instanceof RMCampfireRecipe recipe) {
                int cookTicks = recipe.getCookTicks();
                event.setTotalCookTime(cookTicks);
            }
        }
    }
}
