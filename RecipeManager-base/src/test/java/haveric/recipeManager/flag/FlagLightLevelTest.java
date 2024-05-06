package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.any.flagLightLevel.FlagLightLevel;
import haveric.recipeManager.flag.flags.any.flagLightLevel.LightLevelOptions;
import haveric.recipeManager.flag.flags.any.flagLightLevel.LightType;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlagLightLevelTest extends FlagBaseTest {
    private Location light1Loc;
    private Location light11SkyLoc;
    private Location light12SkyLoc;
    private Location light13SkyLoc;
    private Location light14SkyLoc;
    private Location light15SkyLoc;
    private Location light13BlocksLoc;
    private Location light14BlocksLoc;
    private Location light15BlocksLoc;

    @BeforeEach
    public void setup() {
        mockStatic(Location.class);
        light1Loc = mock(Location.class);
        light11SkyLoc = mock(Location.class);
        light12SkyLoc = mock(Location.class);
        light13SkyLoc = mock(Location.class);
        light14SkyLoc = mock(Location.class);
        light15SkyLoc = mock(Location.class);
        light13BlocksLoc = mock(Location.class);
        light14BlocksLoc = mock(Location.class);
        light15BlocksLoc = mock(Location.class);

        mockStatic(Block.class);
        Block light1 = mock(Block.class);
        when(light1.getLightFromBlocks()).thenReturn((byte) 1);
        when(light1.getLightFromSky()).thenReturn((byte) 1);
        when(light1Loc.getBlock()).thenReturn(light1);

        Block light11Sky = mock(Block.class);
        when(light11Sky.getLightFromBlocks()).thenReturn((byte) 1);
        when(light11Sky.getLightFromSky()).thenReturn((byte) 11);
        when(light11SkyLoc.getBlock()).thenReturn(light11Sky);

        Block light12Sky = mock(Block.class);
        when(light12Sky.getLightFromBlocks()).thenReturn((byte) 1);
        when(light12Sky.getLightFromSky()).thenReturn((byte) 12);
        when(light12SkyLoc.getBlock()).thenReturn(light12Sky);

        Block light13Sky = mock(Block.class);
        when(light13Sky.getLightFromBlocks()).thenReturn((byte) 1);
        when(light13Sky.getLightFromSky()).thenReturn((byte) 13);
        when(light13SkyLoc.getBlock()).thenReturn(light13Sky);

        Block light14Sky = mock(Block.class);
        when(light14Sky.getLightFromBlocks()).thenReturn((byte) 1);
        when(light14Sky.getLightFromSky()).thenReturn((byte) 14);
        when(light14SkyLoc.getBlock()).thenReturn(light14Sky);

        Block light15Sky = mock(Block.class);
        when(light15Sky.getLightFromBlocks()).thenReturn((byte) 1);
        when(light15Sky.getLightFromSky()).thenReturn((byte) 15);
        when(light15SkyLoc.getBlock()).thenReturn(light15Sky);

        Block light13Blocks = mock(Block.class);
        when(light13Blocks.getLightFromBlocks()).thenReturn((byte) 13);
        when(light13Blocks.getLightFromSky()).thenReturn((byte) 1);
        when(light13BlocksLoc.getBlock()).thenReturn(light13Blocks);

        Block light14Blocks = mock(Block.class);
        when(light14Blocks.getLightFromBlocks()).thenReturn((byte) 14);
        when(light14Blocks.getLightFromSky()).thenReturn((byte) 1);
        when(light14BlocksLoc.getBlock()).thenReturn(light14Blocks);

        Block light15Blocks = mock(Block.class);
        when(light15Blocks.getLightFromBlocks()).thenReturn((byte) 15);
        when(light15Blocks.getLightFromSky()).thenReturn((byte) 1);
        when(light15BlocksLoc.getBlock()).thenReturn(light15Blocks);
    }

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagLightLevel/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(4, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).location(light1Loc).build();

                FlagLightLevel flag = (FlagLightLevel) result.getFlag(FlagType.LIGHT_LEVEL);
                flag.onCheck(a);

                List<LightLevelOptions> lightLevelOptions = flag.getLightLevelOptions();
                Material resultType = result.getType();
                if (resultType == Material.DIRT) {
                    assertTrue(a.hasReasons());
                    assertEquals(1, lightLevelOptions.size());
                    assertEquals(14, lightLevelOptions.get(0).getMinLight());
                    assertEquals(-1, lightLevelOptions.get(0).getMaxLight());
                    assertEquals(LightType.SUN, lightLevelOptions.get(0).getLightType());
                    assertNull(flag.getFailMessage());
                } else if (resultType == Material.STONE_SWORD) {
                    assertFalse(a.hasReasons());
                    assertEquals(1, lightLevelOptions.size());
                    assertEquals(0, lightLevelOptions.get(0).getMinLight());
                    assertEquals(4, lightLevelOptions.get(0).getMaxLight());
                    assertEquals(LightType.BLOCKS, lightLevelOptions.get(0).getLightType());
                    assertEquals("<red>Kill the lights!", flag.getFailMessage());
                } else if (resultType == Material.IRON_SWORD) {
                    assertFalse(a.hasReasons());
                    assertEquals(2, lightLevelOptions.size());
                    assertEquals(14, lightLevelOptions.get(0).getMinLight());
                    assertEquals(-1, lightLevelOptions.get(0).getMaxLight());
                    assertEquals(LightType.SUN, lightLevelOptions.get(0).getLightType());

                    assertEquals(0, lightLevelOptions.get(1).getMinLight());
                    assertEquals(4, lightLevelOptions.get(1).getMaxLight());
                    assertEquals(LightType.BLOCKS, lightLevelOptions.get(1).getLightType());
                    assertEquals("<red>Test", flag.getFailMessage());
                } else if (resultType == Material.DIAMOND_SWORD) {
                    assertTrue(a.hasReasons());
                    assertEquals(12, lightLevelOptions.get(0).getMinLight());
                    assertEquals(14, lightLevelOptions.get(0).getMaxLight());
                    assertEquals(LightType.ANY, lightLevelOptions.get(0).getLightType());
                    assertNull(flag.getFailMessage());
                }

                a.clear();
                a.setLocation(light11SkyLoc);
                flag.onCheck(a);

                if (resultType == Material.DIAMOND_SWORD) {
                    assertTrue(a.hasReasons());
                }

                a.clear();
                a.setLocation(light12SkyLoc);
                flag.onCheck(a);

                if (resultType == Material.DIAMOND_SWORD) {
                    assertFalse(a.hasReasons());
                }

                a.clear();
                a.setLocation(light13SkyLoc);
                flag.onCheck(a);

                if (resultType == Material.DIRT) {
                    assertTrue(a.hasReasons());
                } else if (resultType == Material.DIAMOND_SWORD) {
                    assertFalse(a.hasReasons());
                }

                a.clear();
                a.setLocation(light14SkyLoc);
                flag.onCheck(a);

                if (resultType == Material.DIRT) {
                    assertFalse(a.hasReasons());
                } else if (resultType == Material.DIAMOND_SWORD) {
                    assertFalse(a.hasReasons());
                }

                a.clear();
                a.setLocation(light15SkyLoc);
                flag.onCheck(a);

                if (resultType == Material.DIRT) {
                    assertFalse(a.hasReasons());
                } else if (resultType == Material.DIAMOND_SWORD) {
                    assertTrue(a.hasReasons());
                }

                a.clear();
                a.setLocation(light13BlocksLoc);
                flag.onCheck(a);

                if (resultType == Material.DIRT) {
                    assertTrue(a.hasReasons());
                }

                a.clear();
                a.setLocation(light14BlocksLoc);
                flag.onCheck(a);

                if (resultType == Material.DIRT) {
                    assertTrue(a.hasReasons());
                }

                a.clear();
                a.setLocation(light15BlocksLoc);
                flag.onCheck(a);

                if (resultType == Material.DIRT) {
                    assertTrue(a.hasReasons());
                }
            }
        }
    }
}