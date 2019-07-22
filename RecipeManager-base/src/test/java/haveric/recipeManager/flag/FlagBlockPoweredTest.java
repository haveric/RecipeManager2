package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagBlockPowered;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.brew.BrewRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.furnace.RMFurnaceRecipe1_13;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;


public class FlagBlockPoweredTest extends FlagBlockTest {

    @Test
    public void onRecipeParseCraft() {
        File file = new File(baseRecipePath + "flagBlockPowered/flagBlockPoweredCraft.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();
            a.setLocation(unpoweredWorkbenchLoc);

            ItemResult result = recipe.getResult(a);

            FlagBlockPowered flag = (FlagBlockPowered) result.getFlag(FlagType.BLOCK_POWERED);
            flag.onCheck(a);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertTrue(a.hasReasons());
            } else if (resultType == Material.STONE_SWORD) {
                assertTrue(a.hasReasons());
                assertEquals("<red><bold>YOU HAVE NO (indirect) POWAAH!!!", flag.getFailMessage());
            }

            a.clear();
            a.setLocation(directWorkbenchLoc);
            flag.onCheck(a);

            if (resultType == Material.DIRT) {
                assertFalse(a.hasReasons());
            } else if (resultType == Material.STONE_SWORD) {
                assertFalse(a.hasReasons());
                assertEquals("<red><bold>YOU HAVE NO (indirect) POWAAH!!!", flag.getFailMessage());
            }

            a.clear();
            a.setLocation(indirectWorkbenchLoc);
            flag.onCheck(a);

            if (resultType == Material.DIRT) {
                assertTrue(a.hasReasons());
            } else if (resultType == Material.STONE_SWORD) {
                assertFalse(a.hasReasons());
                assertEquals("<red><bold>YOU HAVE NO (indirect) POWAAH!!!", flag.getFailMessage());
            }
        }
    }

    @Test
    public void onRecipeParseSmelt() {
        File file = new File(baseRecipePath + "flagBlockPowered/flagBlockPoweredSmelt.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            RMFurnaceRecipe1_13 recipe = (RMFurnaceRecipe1_13) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();
            a.setLocation(unpoweredFurnaceLoc);

            ItemResult result = recipe.getResult(a);

            FlagBlockPowered flag = (FlagBlockPowered) result.getFlag(FlagType.BLOCK_POWERED);
            flag.onCheck(a);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertTrue(a.hasReasons());
            } else if (resultType == Material.STONE_SWORD) {
                assertTrue(a.hasReasons());
            }

            a.clear();
            a.setLocation(directFurnaceLoc);
            flag.onCheck(a);

            if (resultType == Material.DIRT) {
                assertFalse(a.hasReasons());
            } else if (resultType == Material.STONE_SWORD) {
                assertFalse(a.hasReasons());
            }

            a.clear();
            a.setLocation(indirectFurnaceLoc);
            flag.onCheck(a);

            if (resultType == Material.DIRT) {
                assertTrue(a.hasReasons());
            } else if (resultType == Material.STONE_SWORD) {
                assertFalse(a.hasReasons());
            }
        }
    }

    @Test
    public void onRecipeParseBrew() {
        File file = new File(baseRecipePath + "flagBlockPowered/flagBlockPoweredBrew.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            BrewRecipe recipe = (BrewRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();
            a.setLocation(unpoweredBrewingStandLoc);

            ItemResult result = recipe.getResult(a);

            FlagBlockPowered flag = (FlagBlockPowered) result.getFlag(FlagType.BLOCK_POWERED);
            flag.onCheck(a);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertTrue(a.hasReasons());
            } else if (resultType == Material.STONE_SWORD) {
                assertTrue(a.hasReasons());
            }

            a.clear();
            a.setLocation(directBrewingStandLoc);
            flag.onCheck(a);

            if (resultType == Material.DIRT) {
                assertFalse(a.hasReasons());
            } else if (resultType == Material.STONE_SWORD) {
                assertFalse(a.hasReasons());
            }

            a.clear();
            a.setLocation(indirectBrewingStandLoc);
            flag.onCheck(a);

            if (resultType == Material.DIRT) {
                assertTrue(a.hasReasons());
            } else if (resultType == Material.STONE_SWORD) {
                assertFalse(a.hasReasons());
            }
        }
    }
}
