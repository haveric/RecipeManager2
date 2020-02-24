package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ToolsInventory {
    public static void simulateHotbarSwap(Inventory inventoryOne, int slotOne, Inventory inventoryTwo, int slotTwo) {
        ItemStack itemOne = inventoryOne.getItem(slotOne);
        ItemStack itemTwo = inventoryTwo.getItem(slotTwo);

        boolean itemOneIsAir = itemOne == null || itemOne.getType() == Material.AIR;
        boolean itemTwoIsAir = itemTwo == null || itemTwo.getType() == Material.AIR;

        if (itemOneIsAir && !itemTwoIsAir) {
            ItemStack newItemOne = itemTwo.clone();

            inventoryOne.setItem(slotOne, newItemOne);
            inventoryTwo.setItem(slotTwo, null);
        } else if (!itemOneIsAir && itemTwoIsAir) {
            ItemStack newItemTwo = itemOne.clone();

            inventoryOne.setItem(slotOne, null);
            inventoryTwo.setItem(slotTwo, newItemTwo);
        } else if (!itemOneIsAir) {
            ItemStack newItemOne = itemTwo.clone();
            ItemStack newItemTwo = itemOne.clone();

            inventoryOne.setItem(slotOne, newItemOne);
            inventoryTwo.setItem(slotTwo, newItemTwo);
        }
    }

    /**
     * Due to some inventories limiting allowed items, we need to simulate all inventory behavior manually and cannot rely on the InventoryAction
     * @param player
     * @param inventory
     * @param slot
     */
    public static void simulateDefaultClick(Player player, Inventory inventory, int slot, ClickType clickType) {
        ItemStack cursor = player.getItemOnCursor();
        ItemStack clicked = inventory.getItem(slot);

        boolean cursorIsAir = cursor.getType() == Material.AIR;
        boolean clickedIsAir = clicked == null || clicked.getType() == Material.AIR;

        boolean itemsAreSame = ToolsItem.isSameItemHash(cursor, clicked);

        if ((clickType == ClickType.LEFT || clickType == ClickType.RIGHT) && !cursorIsAir && !clickedIsAir && !itemsAreSame) {
            // SWAP_WITH_CURSOR
            ItemStack newClicked = cursor.clone();
            ItemStack newCursor = clicked.clone();

            inventory.setItem(slot, newClicked);
            player.setItemOnCursor(newCursor);
        } else if (clickType == ClickType.LEFT) {
            if (clickedIsAir && !cursorIsAir) {
                // PLACE_ALL
                inventory.setItem(slot, cursor.clone());
                player.setItemOnCursor(null);
            } else if (!clickedIsAir && cursorIsAir) {
                // PICKUP_ALL
                player.setItemOnCursor(clicked.clone());
                inventory.setItem(slot, null);
            } else if (!cursorIsAir && itemsAreSame) {
                int clickedAmount = clicked.getAmount();
                int cursorAmount = cursor.getAmount();

                int stackSize = cursor.getType().getMaxStackSize();

                if (clickedAmount < stackSize) {
                    int combinedAmount = clickedAmount + cursorAmount;

                    if (combinedAmount <= stackSize) {
                        // PLACE_ALL
                        ItemStack newClicked = clicked.clone();
                        newClicked.setAmount(combinedAmount);
                        inventory.setItem(slot, newClicked);
                        player.setItemOnCursor(null);
                    } else {
                        // PLACE_SOME
                        int remaining = combinedAmount - stackSize;
                        ItemStack newClicked = clicked.clone();
                        newClicked.setAmount(stackSize);
                        inventory.setItem(slot, newClicked);

                        ItemStack newCursor = cursor.clone();
                        newCursor.setAmount(remaining);
                        player.setItemOnCursor(newCursor);
                    }
                }
            }
        } else if (clickType == ClickType.RIGHT) {
            if (cursorIsAir && !clickedIsAir) {
                // PICKUP_HALF
                ItemStack newClicked = clicked.clone();
                ItemStack newCursor = clicked.clone();

                int clickedAmount = clicked.getAmount();
                int newCursorAmount = (int) Math.ceil(clickedAmount / 2.0);
                int newClickedAmount = clickedAmount - newCursorAmount;

                newClicked.setAmount(newClickedAmount);
                newCursor.setAmount(newCursorAmount);

                inventory.setItem(slot, newClicked);
                player.setItemOnCursor(newCursor);
            } else if (!cursorIsAir && (clickedIsAir || itemsAreSame)) {
                // PLACE_ONE
                ItemStack newCursor = cursor.clone();
                ItemStack newClicked = cursor.clone();

                int cursorAmount = cursor.getAmount();

                int clickedAmount = 0;
                if (!clickedIsAir) {
                    clickedAmount = clicked.getAmount();
                }

                int newCursorAmount = cursorAmount - 1;
                int newClickedAmount = clickedAmount + 1;

                newClicked.setAmount(newClickedAmount);
                newCursor.setAmount(newCursorAmount);

                inventory.setItem(slot, newClicked);
                player.setItemOnCursor(newCursor);
            }
        }
    }
}
