package haveric.recipeManager.flags;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FlagIngredientConditionTest extends FlagBaseTest {

    private FlagIngredientCondition flag;

    @Before
    public void setup() {
        flag = new FlagIngredientCondition();
    }
/*
    @Test
    public void checkIngredient() {
        Args a = ArgBuilder.create().build();

        flag.onParse("dirt | data 0-5");
        ItemStack mockItem = mock(ItemStack.class);

        Tools tools = new Tools();
        when(Tools.parseItem(anyString(), Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META)).thenReturn(mockItem);
    }
*/
    @Test
    public void onCheck() {
        Args a;
        /*
        Inventory mockCraftingInventory = mock(CraftingInventory.class);
        ItemStack mockItem = mock(ItemStack.class);

        when(mockCraftingInventory.getItem(1)).thenReturn(mockItem);
        Conditions mockConditions = mock(Conditions.class);

        flag.setIngredientConditions(mockItem, mockConditions);
        when(mockConditions.checkIngredient(any(ItemStack.class), any(Args.class))).thenReturn(true);

        a = ArgBuilder.create().inventory(mockCraftingInventory).build();
        flag.onCheck(a);
        assertFalse(a.hasReasons());

        Inventory mockFurnaceInventory = mock(FurnaceInventory.class);
        // TODO: Add items to inventory
        a = ArgBuilder.create().inventory(mockFurnaceInventory).build();
        flag.onCheck(a);


        Inventory mockBrewerInventory = mock(BrewerInventory.class);
        // TODO: Add items to inventory
        a = ArgBuilder.create().inventory(mockBrewerInventory).build();
        flag.onCheck(a);


        Inventory mockUnknownInventory = mock(Inventory.class);
        a = ArgBuilder.create().inventory(mockUnknownInventory).build();
        flag.onCheck(a);
        assertTrue(a.hasReasons());
        */
        // Test Args with no inventory
        a = ArgBuilder.create().build();
        flag.onCheck(a);
        assertTrue(a.hasReasons());
    }

}
