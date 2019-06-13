package haveric.recipeManager.flag;

import haveric.recipeManager.*;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagEnchantedBook;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
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
        Enchantment.registerEnchantment(new TestEnchantmentSharpness(Enchantment.DAMAGE_ALL));
        Enchantment.registerEnchantment(new TestEnchantmentOxygen(Enchantment.OXYGEN));
        Enchantment.registerEnchantment(new TestEnchantmentDigSpeed(Enchantment.DIG_SPEED));
        when(settings.getEnchantment("damageall")).thenReturn(Enchantment.DAMAGE_ALL);
        when(settings.getEnchantment("sharpness")).thenReturn(Enchantment.DAMAGE_ALL);
        when(settings.getEnchantment("oxygen")).thenReturn(Enchantment.OXYGEN);
        when(settings.getEnchantment("digspeed")).thenReturn(Enchantment.DIG_SPEED);
    }

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagEnchantedBook/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();

            ItemResult result = recipe.getResult(a);

            FlagEnchantedBook flag = (FlagEnchantedBook) result.getFlag(FlagType.ENCHANTED_BOOK);
            flag.onPrepare(a);

            TestMetaEnchantedBook meta = (TestMetaEnchantedBook) result.getItemMeta();
            String name = recipe.getName();

            assertEquals(0, meta.getEnchants().size());

            switch (name) {
                case "one":
                    assertEquals(1, meta.getStoredEnchants().size());
                    assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DIG_SPEED));
                    assertEquals(1, meta.getStoredEnchants().get(Enchantment.DIG_SPEED).intValue());
                    break;
                case "max":
                    assertEquals(1, meta.getStoredEnchants().size());
                    assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DAMAGE_ALL));
                    assertEquals(5, meta.getStoredEnchants().get(Enchantment.DAMAGE_ALL).intValue());
                    break;
                case "127":
                    assertEquals(1, meta.getStoredEnchants().size());
                    assertTrue(meta.getStoredEnchants().containsKey(Enchantment.OXYGEN));
                    assertEquals(127, meta.getStoredEnchants().get(Enchantment.OXYGEN).intValue());
                    break;
                case "onlyremove":
                    assertEquals(0, meta.getStoredEnchants().size());
                    break;
                case "multiple":
                    assertEquals(3, meta.getStoredEnchants().size());
                    assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DIG_SPEED));
                    assertEquals(1, meta.getStoredEnchants().get(Enchantment.DIG_SPEED).intValue());
                    assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DAMAGE_ALL));
                    assertEquals(5, meta.getStoredEnchants().get(Enchantment.DAMAGE_ALL).intValue());
                    assertTrue(meta.getStoredEnchants().containsKey(Enchantment.OXYGEN));
                    assertEquals(127, meta.getStoredEnchants().get(Enchantment.OXYGEN).intValue());
                    break;
                case "remove":
                    assertEquals(2, meta.getStoredEnchants().size());
                    assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DIG_SPEED));
                    assertEquals(1, meta.getStoredEnchants().get(Enchantment.DIG_SPEED).intValue());
                    assertTrue(meta.getStoredEnchants().containsKey(Enchantment.OXYGEN));
                    assertEquals(127, meta.getStoredEnchants().get(Enchantment.OXYGEN).intValue());
                    break;
            }
        }
    }
}
