package haveric.recipeManager.recipes;

import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.recipes.anvil.AnvilRecipe;
import haveric.recipeManager.recipes.brew.BrewRecipe;
import haveric.recipeManager.recipes.campfire.RMCampfireRecipe;
import haveric.recipeManager.recipes.cartography.CartographyRecipe;
import haveric.recipeManager.recipes.combine.CombineRecipe;
import haveric.recipeManager.recipes.combine.CombineRecipe1_13;
import haveric.recipeManager.recipes.compost.CompostRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import haveric.recipeManager.recipes.furnace.RMBlastingRecipe;
import haveric.recipeManager.recipes.furnace.RMFurnaceRecipe;
import haveric.recipeManager.recipes.furnace.RMFurnaceRecipe1_13;
import haveric.recipeManager.recipes.furnace.RMSmokingRecipe;
import haveric.recipeManager.recipes.grindstone.GrindstoneRecipe;
import haveric.recipeManager.recipes.stonecutting.RMStonecuttingRecipe;
import haveric.recipeManager.tools.Version;
import org.bukkit.ChatColor;

public class RecipeTypeLoader {
    public RecipeTypeLoader() {
        loadDefaultRecipeTypes();
    }

    private void loadDefaultRecipeTypes() {
        loadRecipeType(RMCRecipeType.ANVIL.getDirective(), new AnvilRecipe());
        loadRecipeType(RMCRecipeType.BREW.getDirective(), new BrewRecipe());
        loadRecipeType(RMCRecipeType.FUEL.getDirective(), new FuelRecipe());

        loadRecipeType(RMCRecipeType.SPECIAL.getDirective(), new RemoveResultRecipe());

        if (Version.has1_13Support()) {
            loadRecipeType(RMCRecipeType.COMBINE.getDirective(), new CombineRecipe1_13());
            loadRecipeType(RMCRecipeType.CRAFT.getDirective(), new CraftRecipe1_13());
            loadRecipeType(RMCRecipeType.SMELT.getDirective(), new RMFurnaceRecipe1_13());
        } else {
            loadRecipeType(RMCRecipeType.COMBINE.getDirective(), new CombineRecipe());
            loadRecipeType(RMCRecipeType.CRAFT.getDirective(), new CraftRecipe());
            loadRecipeType(RMCRecipeType.SMELT.getDirective(), new RMFurnaceRecipe());
        }

        if (Version.has1_14Support()) {
            loadRecipeType(RMCRecipeType.BLASTING.getDirective(), new RMBlastingRecipe());
            loadRecipeType(RMCRecipeType.CAMPFIRE.getDirective(), new RMCampfireRecipe());
            loadRecipeType(RMCRecipeType.CARTOGRAPHY.getDirective(), new CartographyRecipe());
            loadRecipeType(RMCRecipeType.GRINDSTONE.getDirective(), new GrindstoneRecipe());
            loadRecipeType(RMCRecipeType.COMPOST.getDirective(), new CompostRecipe());
            loadRecipeType(RMCRecipeType.SMOKING.getDirective(), new RMSmokingRecipe());
            loadRecipeType(RMCRecipeType.STONECUTTING.getDirective(), new RMStonecuttingRecipe());
        }
    }

    public void loadRecipeType(String recipeTypeName, BaseRecipe recipe) {
        if (RecipeTypeFactory.getInstance().isInitialized()) {
            MessageSender.getInstance().info(ChatColor.RED + "Custom recipe types must be added in your onEnable() method.");
        } else {
            RecipeTypeFactory.getInstance().initializeRecipeType(recipeTypeName, recipe);
        }
    }
}
