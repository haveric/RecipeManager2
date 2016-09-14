package haveric.recipeManager.flags;

import haveric.recipeManager.*;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.enchantments.Enchantment;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FlagEnchantedBookTest extends FlagBaseTest {

    @Before
    public void setup() {
        Enchantment.registerEnchantment(new TestEnchantmentSharpness(16));
        Enchantment.registerEnchantment(new TestEnchantmentOxygen(5));
        Enchantment.registerEnchantment(new TestEnchantmentDigSpeed(32));
        when(settings.getEnchantment("damageall")).thenReturn(Enchantment.DAMAGE_ALL);
        when(settings.getEnchantment("sharpness")).thenReturn(Enchantment.DAMAGE_ALL);
        when(settings.getEnchantment("oxygen")).thenReturn(Enchantment.OXYGEN);
        when(settings.getEnchantment("digspeed")).thenReturn(Enchantment.DIG_SPEED);
    }

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagEnchantedBook/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).build();
            a.setPlayerUUID(testUUID);

            ItemResult result = recipe.getResult(a);

            FlagEnchantedBook flag = (FlagEnchantedBook) result.getFlag(FlagType.ENCHANTED_BOOK);
            flag.onPrepare(a);

            TestMetaEnchantedBook meta = (TestMetaEnchantedBook) result.getItemMeta();
            String name = recipe.getName();

            assertEquals(0, meta.getEnchants().size());

            if (name.equals("one")) {
                assertEquals(1, meta.getStoredEnchants().size());
                assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DIG_SPEED));
                assertEquals(1, meta.getStoredEnchants().get(Enchantment.DIG_SPEED).intValue());
            } else if (name.equals("max")) {
                assertEquals(1, meta.getStoredEnchants().size());
                assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DAMAGE_ALL));
                assertEquals(5, meta.getStoredEnchants().get(Enchantment.DAMAGE_ALL).intValue());
            } else if (name.equals("127")) {
                assertEquals(1, meta.getStoredEnchants().size());
                assertTrue(meta.getStoredEnchants().containsKey(Enchantment.OXYGEN));
                assertEquals(127, meta.getStoredEnchants().get(Enchantment.OXYGEN).intValue());
            } else if (name.equals("onlyremove")) {
                assertEquals(0, meta.getStoredEnchants().size());
            } else if (name.equals("multiple")) {
                assertEquals(3, meta.getStoredEnchants().size());
                assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DIG_SPEED));
                assertEquals(1, meta.getStoredEnchants().get(Enchantment.DIG_SPEED).intValue());
                assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DAMAGE_ALL));
                assertEquals(5, meta.getStoredEnchants().get(Enchantment.DAMAGE_ALL).intValue());
                assertTrue(meta.getStoredEnchants().containsKey(Enchantment.OXYGEN));
                assertEquals(127, meta.getStoredEnchants().get(Enchantment.OXYGEN).intValue());
            } else if (name.equals("remove")) {
                assertEquals(2, meta.getStoredEnchants().size());
                assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DIG_SPEED));
                assertEquals(1, meta.getStoredEnchants().get(Enchantment.DIG_SPEED).intValue());
                assertTrue(meta.getStoredEnchants().containsKey(Enchantment.OXYGEN));
                assertEquals(127, meta.getStoredEnchants().get(Enchantment.OXYGEN).intValue());
            }
        }
    }
}
