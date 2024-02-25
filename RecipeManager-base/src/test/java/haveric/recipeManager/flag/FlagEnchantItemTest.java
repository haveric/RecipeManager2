package haveric.recipeManager.flag;

public class FlagEnchantItemTest extends FlagBaseTest {

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
//        try {
//            //Enchantment.registerEnchantment(new TestEnchantmentInfinity(Enchantment.ARROW_INFINITE));
//        } catch (IllegalArgumentException ignored) { }
//
//        settings.addEnchantName("sharpness", Enchantment.DAMAGE_ALL);
//        settings.addEnchantName("digspeed", Enchantment.DIG_SPEED);
//        settings.addEnchantName("arrowinfinite", Enchantment.ARROW_INFINITE);
//    }
//
//    @Test
//    public void onRecipeParse() {
//        File file = new File(baseRecipePath + "flagEnchantItem/");
//        reloadRecipeProcessor(true, file);
//
//        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();
//
//        assertEquals(5, queued.size());
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
//                FlagEnchantItem flag = (FlagEnchantItem) result.getFlag(FlagType.ENCHANT_ITEM);
//                flag.onPrepare(a);
//
//                Material resultType = result.getType();
//                ItemMeta meta = result.getItemMeta();
//                if (resultType == Material.DIRT) {
//                    assertEquals(1, meta.getEnchants().size());
//                    assertTrue(meta.getEnchants().containsKey(Enchantment.OXYGEN));
//                    assertEquals(1, meta.getEnchants().get(Enchantment.OXYGEN).intValue());
//                } else if (resultType == Material.STONE_SWORD) {
//                    assertEquals(1, meta.getEnchants().size());
//                    assertTrue(meta.getEnchants().containsKey(Enchantment.DIG_SPEED));
//                    assertEquals(3, meta.getEnchants().get(Enchantment.DIG_SPEED).intValue());
//                } else if (resultType == Material.GOLDEN_SWORD) {
//                    assertEquals(1, meta.getEnchants().size());
//                    assertTrue(meta.getEnchants().containsKey(Enchantment.ARROW_INFINITE));
//                    assertEquals(127, meta.getEnchants().get(Enchantment.ARROW_INFINITE).intValue());
//                } else if (resultType == Material.IRON_SWORD) {
//                    assertEquals(0, meta.getEnchants().size());
//                } else if (resultType == Material.DIAMOND_SWORD) {
//                    assertEquals(2, meta.getEnchants().size());
//                    assertTrue(meta.getEnchants().containsKey(Enchantment.DIG_SPEED));
//                    assertTrue(meta.getEnchants().containsKey(Enchantment.DAMAGE_ALL));
//                    assertEquals(1, meta.getEnchants().get(Enchantment.DIG_SPEED).intValue());
//                    assertEquals(1, meta.getEnchants().get(Enchantment.DAMAGE_ALL).intValue());
//                }
//            }
//        }
//    }
}
