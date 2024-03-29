package haveric.recipeManager.flag;

public class FlagEnchantedBookTest extends FlagBaseTest {

// TODO: Fix or replace now that registerEnchantment has been removed
//
//    @BeforeEach
//    public void setup() {
//        try {
//            //Enchantment.registerEnchantment(new TestEnchantmentSharpness(Enchantment.DAMAGE_ALL));
//        } catch (IllegalArgumentException ignored) { }
//
//        try {
//            //Enchantment.registerEnchantment(new TestEnchantmentOxygen(Enchantment.OXYGEN));
//        } catch (IllegalArgumentException ignored) { }
//
//        try {
//            //Enchantment.registerEnchantment(new TestEnchantmentDigSpeed(Enchantment.DIG_SPEED));
//        } catch (IllegalArgumentException ignored) { }
//
//        settings.addEnchantName("damageall", Enchantment.DAMAGE_ALL);
//        settings.addEnchantName("sharpness", Enchantment.DAMAGE_ALL);
//        settings.addEnchantName("digspeed", Enchantment.DIG_SPEED);
//    }
//
//    @Test
//    public void onRecipeParse() {
//        File file = new File(baseRecipePath + "flagEnchantedBook/");
//        reloadRecipeProcessor(true, file);
//
//        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();
//
//        assertEquals(6, queued.size());
//
//        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
//            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();
//
//            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
//                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
//
//                ItemResult result = recipe.getFirstResult();
//
//                Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).build();
//
//                FlagEnchantedBook flag = (FlagEnchantedBook) result.getFlag(FlagType.ENCHANTED_BOOK);
//                flag.onPrepare(a);
//
//                TestMetaEnchantedBook meta = (TestMetaEnchantedBook) result.getItemMeta();
//                String name = recipe.getName();
//
//                assertEquals(0, meta.getEnchants().size());
//
//                switch (name) {
//                    case "one":
//                        assertEquals(1, meta.getStoredEnchants().size());
//                        assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DIG_SPEED));
//                        assertEquals(1, meta.getStoredEnchants().get(Enchantment.DIG_SPEED).intValue());
//                        break;
//                    case "max":
//                        assertEquals(1, meta.getStoredEnchants().size());
//                        assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DAMAGE_ALL));
//                        assertEquals(5, meta.getStoredEnchants().get(Enchantment.DAMAGE_ALL).intValue());
//                        break;
//                    case "127":
//                        assertEquals(1, meta.getStoredEnchants().size());
//                        assertTrue(meta.getStoredEnchants().containsKey(Enchantment.OXYGEN));
//                        assertEquals(127, meta.getStoredEnchants().get(Enchantment.OXYGEN).intValue());
//                        break;
//                    case "onlyremove":
//                        assertEquals(0, meta.getStoredEnchants().size());
//                        break;
//                    case "multiple":
//                        assertEquals(3, meta.getStoredEnchants().size());
//                        assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DIG_SPEED));
//                        assertEquals(1, meta.getStoredEnchants().get(Enchantment.DIG_SPEED).intValue());
//                        assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DAMAGE_ALL));
//                        assertEquals(5, meta.getStoredEnchants().get(Enchantment.DAMAGE_ALL).intValue());
//                        assertTrue(meta.getStoredEnchants().containsKey(Enchantment.OXYGEN));
//                        assertEquals(127, meta.getStoredEnchants().get(Enchantment.OXYGEN).intValue());
//                        break;
//                    case "remove":
//                        assertEquals(2, meta.getStoredEnchants().size());
//                        assertTrue(meta.getStoredEnchants().containsKey(Enchantment.DIG_SPEED));
//                        assertEquals(1, meta.getStoredEnchants().get(Enchantment.DIG_SPEED).intValue());
//                        assertTrue(meta.getStoredEnchants().containsKey(Enchantment.OXYGEN));
//                        assertEquals(127, meta.getStoredEnchants().get(Enchantment.OXYGEN).intValue());
//                        break;
//                }
//            }
//        }
//    }
}
