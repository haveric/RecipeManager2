package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.any.FlagBlockPowered;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.brew.BrewRecipe;
import haveric.recipeManager.recipes.cooking.furnace.RMFurnaceRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class FlagBlockPoweredTest extends FlagBaseTest {
    @Mock
    protected Location unpoweredWorkbenchLoc;
    @Mock
    protected Location directWorkbenchLoc;
    @Mock
    protected Location indirectWorkbenchLoc;

    @Mock
    protected Location unpoweredFurnaceLoc;
    @Mock
    protected Location directFurnaceLoc;
    @Mock
    protected Location indirectFurnaceLoc;

    @Mock
    protected Location unpoweredBrewingStandLoc;
    @Mock
    protected Location directBrewingStandLoc;
    @Mock
    protected Location indirectBrewingStandLoc;

    @Test
    public void onRecipeParseCraft() {
        Block unpoweredWorkbench = mock(Block.class);
        when(unpoweredWorkbench.getType()).thenReturn(Material.CRAFTING_TABLE);
        when(unpoweredWorkbenchLoc.getBlock()).thenReturn(unpoweredWorkbench);

        Block directWorkbench = mock(Block.class);
        when(directWorkbench.getType()).thenReturn(Material.CRAFTING_TABLE);
        when(directWorkbench.isBlockPowered()).thenReturn(true);
        when(directWorkbench.isBlockIndirectlyPowered()).thenReturn(false);
        when(directWorkbenchLoc.getBlock()).thenReturn(directWorkbench);

        Block indirectWorkbench = mock(Block.class);
        when(indirectWorkbench.getType()).thenReturn(Material.CRAFTING_TABLE);
        when(indirectWorkbench.isBlockPowered()).thenReturn(false);
        when(indirectWorkbench.isBlockIndirectlyPowered()).thenReturn(true);
        when(indirectWorkbenchLoc.getBlock()).thenReturn(indirectWorkbench);

        File file = new File(baseRecipePath + "flagBlockPowered/flagBlockPoweredCraft.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).location(unpoweredWorkbenchLoc).build();

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
    }

    @Test
    public void onRecipeParseSmelt() {
        Block unpoweredFurnace = mock(Block.class);
        when(unpoweredFurnace.getType()).thenReturn(Material.FURNACE);
        when(unpoweredFurnaceLoc.getBlock()).thenReturn(unpoweredFurnace);

        Block directFurnace = mock(Block.class);
        when(directFurnace.getType()).thenReturn(Material.FURNACE);
        when(directFurnace.isBlockPowered()).thenReturn(true);
        when(directFurnace.isBlockIndirectlyPowered()).thenReturn(false);
        when(directFurnaceLoc.getBlock()).thenReturn(directFurnace);

        Block indirectFurnace = mock(Block.class);
        when(indirectFurnace.getType()).thenReturn(Material.FURNACE);
        when(indirectFurnace.isBlockPowered()).thenReturn(false);
        when(indirectFurnace.isBlockIndirectlyPowered()).thenReturn(true);
        when(indirectFurnaceLoc.getBlock()).thenReturn(indirectFurnace);

        File file = new File(baseRecipePath + "flagBlockPowered/flagBlockPoweredSmelt.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            RMFurnaceRecipe recipe = (RMFurnaceRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
                mockedBukkit.when(() -> Bukkit.getOfflinePlayer(testUUID)).thenReturn(player);

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
    }

    @Test
    public void onRecipeParseBrew() {
        Block unpoweredBrewingStand = mock(Block.class);
        when(unpoweredBrewingStand.getType()).thenReturn(Material.BREWING_STAND);
        when(unpoweredBrewingStandLoc.getBlock()).thenReturn(unpoweredBrewingStand);

        Block directBrewingStand = mock(Block.class);
        when(directBrewingStand.getType()).thenReturn(Material.BREWING_STAND);
        when(directBrewingStand.isBlockPowered()).thenReturn(true);
        when(directBrewingStand.isBlockIndirectlyPowered()).thenReturn(false);
        when(directBrewingStandLoc.getBlock()).thenReturn(directBrewingStand);

        Block indirectBrewingStand = mock(Block.class);
        when(indirectBrewingStand.getType()).thenReturn(Material.BREWING_STAND);
        when(indirectBrewingStand.isBlockPowered()).thenReturn(false);
        when(indirectBrewingStand.isBlockIndirectlyPowered()).thenReturn(true);
        when(indirectBrewingStandLoc.getBlock()).thenReturn(indirectBrewingStand);

        File file = new File(baseRecipePath + "flagBlockPowered/flagBlockPoweredBrew.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            BrewRecipe recipe = (BrewRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).location(unpoweredBrewingStandLoc).build();

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
}
