package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.any.FlagCommand;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class FlagCommandTest extends FlagBaseTest {

    World world;
    Location location;

    @Before
    public void setupLocation() {
        mockStatic(World.class);
        mockStatic(Location.class);

        world = mock(World.class);
        when(world.getName()).thenReturn("TestWorld");

        location = mock(Location.class);
        when(location.getBlockX()).thenReturn(1);
        when(location.getBlockY()).thenReturn(2);
        when(location.getBlockZ()).thenReturn(5);

        when(location.getWorld()).thenReturn(world);
    }

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagCommand/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            Args a = ArgBuilder.create().recipe(recipe).result(result).location(location).player(testUUID).build();

            FlagCommand flag = (FlagCommand) result.getFlag(FlagType.COMMAND);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertEquals(1, flag.getCommands().size());
                assertEquals("/say I crafted {result}!", flag.getCommands().get(0));
            } else if (resultType == Material.STONE_SWORD) {
                assertEquals(1, flag.getCommands().size());
                assertEquals("kick {player}", flag.getCommands().get(0));
            } else if (resultType == Material.IRON_SWORD) {
                assertEquals(2, flag.getCommands().size());
                assertEquals("/say I crafted {result}!", flag.getCommands().get(0));
                assertEquals("kick {player}", flag.getCommands().get(1));
            } else if (resultType == Material.GOLDEN_SWORD) {
                assertEquals(1, flag.getCommands().size());
                assertEquals("/say {player} crafted Gold Sword at {x},{y},{z}", flag.getCommands().get(0));
                String parsed = a.parseVariables(flag.getCommands().get(0));
                assertEquals("/say TestPlayer crafted Gold Sword at 1,2,5", parsed);
            } else if (resultType == Material.DIAMOND_SWORD) {
                assertEquals(1, flag.getCommands().size());
                assertEquals("/say {player} crafted Sword at {x-0},{y+0},{z -0} on {world}", flag.getCommands().get(0));
                String parsed = a.parseVariables(flag.getCommands().get(0));
                assertEquals("/say TestPlayer crafted Sword at 1,2,5 on TestWorld", parsed);
            } else if (resultType == Material.STICK) {
                assertEquals(1, flag.getCommands().size());
                assertEquals("/say {player} crafted Sword at {x   +   12},{y - 3},{z+2}", flag.getCommands().get(0));
                String parsed = a.parseVariables(flag.getCommands().get(0));
                assertEquals("/say TestPlayer crafted Sword at 13,-1,7", parsed);
            }
        }
    }
}