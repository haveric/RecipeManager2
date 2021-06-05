package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.flags.any.flagCooldown.FlagCooldown;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class FlagCooldownTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagCooldown/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(5, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                FlagCooldown flag = (FlagCooldown) result.getFlag(FlagType.COOLDOWN);

                Material resultType = result.getType();

                int cooldownTime = flag.getCooldownTime();
                String failMessage = flag.getFailMessage();
                String message = flag.getCraftMessage();
                if (resultType == Material.DIRT) {
                    assertEquals(30, cooldownTime);
                    assertNull(failMessage);
                    assertNull(message);
                    assertFalse(flag.isGlobal());
                } else if (resultType == Material.STONE_SWORD) {
                    assertEquals(30, cooldownTime);
                    assertNull(failMessage);
                    assertNull(message);
                    assertFalse(flag.isGlobal());
                } else if (resultType == Material.IRON_SWORD) {
                    assertEquals(105, cooldownTime);
                    assertEquals("<red>Usable in: {time}", failMessage);
                    assertNull(message);
                    assertFalse(flag.isGlobal());
                } else if (resultType == Material.GOLDEN_SWORD) {
                    assertEquals(1800, cooldownTime);
                    assertEquals("<red>Someone used this recently, wait: {time}", failMessage);
                    assertEquals("<yellow>Cooldown time: {time}", message);
                    assertTrue(flag.isGlobal());
                } else if (resultType == Material.DIAMOND_SWORD) {
                    assertEquals(600, cooldownTime);
                    assertNull(failMessage);
                    assertNull(message);
                    assertFalse(flag.isGlobal());
                }
            }
        }
    }
}