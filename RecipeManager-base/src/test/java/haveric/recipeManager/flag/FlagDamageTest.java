package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.result.meta.FlagDamage;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.MultiChoiceResultRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mockStatic;

public class FlagDamageTest extends FlagBaseTest {
    //@Test TODO: Rewrite test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagDamage/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(15, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            MultiChoiceResultRecipe recipe = (MultiChoiceResultRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();
                Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).build();

                FlagDamage flag = (FlagDamage) result.getFlag(FlagType.DAMAGE);
                if (flag != null) {
                    flag.onPrepare(a);
                }

                ItemMeta meta = result.getItemMeta();
                assertInstanceOf(Damageable.class, meta);
                Damageable damageable = (Damageable) meta;

                Material resultType = result.getType();

                if (resultType == Material.STONE_SWORD) {
                    assertEquals(5, damageable.getDamage());
                } else if (resultType == Material.IRON_SWORD) {
                    assertEquals(10, damageable.getDamage());
                } else if (resultType == Material.GOLDEN_SWORD) {
                    assertEquals(20, damageable.getDamage());
                    assertEquals(1, result.getFlags().get().size());
                }
            }
        }
    }
}
