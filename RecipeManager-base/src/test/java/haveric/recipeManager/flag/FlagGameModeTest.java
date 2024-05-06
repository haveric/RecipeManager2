package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.any.FlagGameMode;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlagGameModeTest extends FlagBaseTest{

    private FlagGameMode flag;

    @BeforeEach
    public void setup() {
        flag = new FlagGameMode();
    }

    @Test
    public void testClone() {
        flag.addGameMode(GameMode.ADVENTURE);
        flag.addGameMode(GameMode.SURVIVAL);
        flag.setFailMessage("Recipe has failed");

        FlagGameMode clone = flag.clone();
        assertTrue(clone.getGameModes().contains(GameMode.ADVENTURE));
        assertTrue(clone.getGameModes().contains(GameMode.SURVIVAL));
        assertEquals(clone.getGameModes().size(), 2);

        assertEquals(clone.getFailMessage(), "Recipe has failed");
    }

    @Test
    public void getGameModes() {
        flag.addGameMode(GameMode.ADVENTURE);
        flag.getGameModes().clear();
        assertFalse(flag.getGameModes().isEmpty());
    }

    //@Test TODO: Rewrite test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagGameMode/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(12, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            FlagGameMode flag = (FlagGameMode) result.getFlag(FlagType.GAMEMODE);
            if (resultType == Material.IRON_SWORD) {
                assertTrue(flag.getGameModes().contains(GameMode.ADVENTURE));
                assertFalse(flag.getGameModes().contains(GameMode.CREATIVE));
                assertFalse(flag.getGameModes().contains(GameMode.SURVIVAL));
            } else if (resultType == Material.DIRT) {
                assertFalse(flag.getGameModes().contains(GameMode.ADVENTURE));
                assertTrue(flag.getGameModes().contains(GameMode.CREATIVE));
                assertFalse(flag.getGameModes().contains(GameMode.SURVIVAL));
            } else if (resultType == Material.COBBLESTONE) {
                assertFalse(flag.getGameModes().contains(GameMode.ADVENTURE));
                assertFalse(flag.getGameModes().contains(GameMode.CREATIVE));
                assertTrue(flag.getGameModes().contains(GameMode.SURVIVAL));
            } else if (resultType == Material.OAK_LOG) {
                assertTrue(flag.getGameModes().contains(GameMode.ADVENTURE));
                assertFalse(flag.getGameModes().contains(GameMode.CREATIVE));
                assertTrue(flag.getGameModes().contains(GameMode.SURVIVAL));
            } else if (resultType == Material.SHORT_GRASS) {
                assertTrue(flag.getGameModes().contains(GameMode.ADVENTURE));
                assertTrue(flag.getGameModes().contains(GameMode.CREATIVE));
                assertTrue(flag.getGameModes().contains(GameMode.SURVIVAL));
            } else if (resultType == Material.OAK_WOOD) {
                assertNull(flag);
            } else if (resultType == Material.TROPICAL_FISH) {
                assertTrue(flag.getGameModes().contains(GameMode.ADVENTURE));
                assertEquals(flag.getGameModes().size(), 1);
                assertEquals(flag.getFailMessage(), "fail");
            } else if (resultType == Material.COOKED_SALMON) {
                assertNull(flag);
            } else if (resultType == Material.BRICK) {
                assertFalse(flag.getGameModes().contains(GameMode.ADVENTURE));
                assertTrue(flag.getGameModes().contains(GameMode.CREATIVE));
                assertFalse(flag.getGameModes().contains(GameMode.SURVIVAL));
            }
        }
    }

    @Test
    public void onCrafted() {
        Player mockPlayer = mock(Player.class);
        when(mockPlayer.getGameMode()).thenReturn(GameMode.SURVIVAL);
        Args a = ArgBuilder.create().player(mockPlayer).build().processArgs();

        // Test valid game mode
        flag.addGameMode(GameMode.SURVIVAL);
        flag.onCrafted(a);
        assertFalse(a.hasReasons());

        // Test invalid game mode
        flag.clearGameModes();
        flag.addGameMode(GameMode.CREATIVE);
        flag.onCrafted(a);
        assertTrue(a.hasReasons());

        // Test Args with no player
        a = ArgBuilder.create().build();
        flag.onCrafted(a);
        assertTrue(a.hasReasons());
    }
}
