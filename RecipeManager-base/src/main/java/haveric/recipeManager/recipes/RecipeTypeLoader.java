package haveric.recipeManager.recipes;

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
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.bukkit.ChatColor;

public class RecipeTypeLoader {
    public RecipeTypeLoader() {
        loadDefaultRecipeTypes();
    }

    private void loadDefaultRecipeTypes() {
        RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.ANVIL.getDirective(), new AnvilRecipe());
        RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.BREW.getDirective(), new BrewRecipe());
        RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.FUEL.getDirective(), new FuelRecipe());

        RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.SPECIAL.getDirective(), new RemoveResultRecipe());

        if (Version.has1_13Support()) {
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.COMBINE.getDirective(), new CombineRecipe1_13());
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.CRAFT.getDirective(), new CraftRecipe1_13());
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.SMELT.getDirective(), new RMFurnaceRecipe1_13());
        } else {
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.COMBINE.getDirective(), new CombineRecipe());
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.CRAFT.getDirective(), new CraftRecipe());
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.SMELT.getDirective(), new RMFurnaceRecipe());
        }

        if (Version.has1_14Support()) {
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.BLASTING.getDirective(), new RMBlastingRecipe());
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.CAMPFIRE.getDirective(), new RMCampfireRecipe());
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.CARTOGRAPHY.getDirective(), new CartographyRecipe());
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.GRINDSTONE.getDirective(), new GrindstoneRecipe());
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.COMPOST.getDirective(), new CompostRecipe());
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.SMOKING.getDirective(), new RMSmokingRecipe());
            RecipeTypeFactory.getInstance().initializeRecipeType(RMCRecipeType.STONECUTTING.getDirective(), new RMStonecuttingRecipe());
        }
    }

    public void loadCustomRecipeType(String recipeTypeName, BaseRecipe recipe) {
        if (RecipeTypeFactory.getInstance().isInitialized()) {
            MessageSender.getInstance().info(ChatColor.RED + "Custom recipe types must be added in your onEnable() method.");
        } else {
            RecipeTypeFactory.getInstance().initializeRecipeType(recipeTypeName, recipe);
        }
    }
}
