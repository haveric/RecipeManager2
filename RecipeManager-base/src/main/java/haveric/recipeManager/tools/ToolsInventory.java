package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ToolsInventory {

    private static final int PLAYER_INVENTORY_SIZE = 36;

    public static void simulateDrag(Player player, Inventory topInventory, Inventory bottomInventory, Map<Integer, ItemStack> newItemsMap, ItemStack cursor) {

        int topInventorySize = topInventory.getSize();
        for (Map.Entry<Integer, ItemStack> entry : newItemsMap.entrySet()) {
            int slot = entry.getKey();
            ItemStack item = entry.getValue();

            if (slot < topInventorySize) {
                topInventory.setItem(slot, item);
            } else {
                int inventoryActualSlot = slot - topInventorySize + 9;
                if (inventoryActualSlot > PLAYER_INVENTORY_SIZE) {
                    inventoryActualSlot -= PLAYER_INVENTORY_SIZE;
                }

                bottomInventory.setItem(inventoryActualSlot, item);
            }

            player.setItemOnCursor(cursor);
        }
    }

    public static void simulateHotbarSwap(Inventory inventoryOne, int slotTop, Inventory inventoryTwo, int slotHotbar) {
        simulateHotbarSwap(inventoryOne, slotTop, inventoryTwo, slotHotbar, 64);
    }

    public static void simulateHotbarSwap(Inventory inventoryOne, int slotTop, Inventory inventoryTwo, int slotHotbar, int maxStackSize) {
        ItemStack itemTop = inventoryOne.getItem(slotTop);
        ItemStack itemHotbar = inventoryTwo.getItem(slotHotbar);

        boolean itemTopIsAir = itemTop == null || itemTop.getType() == Material.AIR;
        boolean itemHotbarIsAir = itemHotbar == null || itemHotbar.getType() == Material.AIR;

        if (itemTopIsAir && !itemHotbarIsAir) {
            ItemStack newItemTop = itemHotbar.clone();

            if (newItemTop.getAmount() >= maxStackSize) {
                inventoryOne.setItem(slotTop, newItemTop);
                inventoryTwo.setItem(slotHotbar, null);
            } else {
                ItemStack newItemHotbar = itemHotbar.clone();

                int actualMoved = Math.min(newItemTop.getAmount(), maxStackSize);
                newItemTop.setAmount(actualMoved);
                inventoryOne.setItem(slotTop, newItemTop);

                newItemHotbar.setAmount(newItemHotbar.getAmount() - actualMoved);
                inventoryTwo.setItem(slotHotbar, newItemHotbar);
            }
        } else if (!itemTopIsAir && itemHotbarIsAir) {
            ItemStack newItemTwo = itemTop.clone();

            inventoryOne.setItem(slotTop, null);
            inventoryTwo.setItem(slotHotbar, newItemTwo);
        } else if (!itemTopIsAir) {
            if (itemHotbar.getAmount() <= maxStackSize) {
                ItemStack newItemTop = itemHotbar.clone();
                ItemStack newItemHotbar = itemTop.clone();

                inventoryOne.setItem(slotTop, newItemTop);
                inventoryTwo.setItem(slotHotbar, newItemHotbar);
            }
        }
    }

    public static void simulateShiftClick(Inventory bottomInventory, Inventory topInventory, int clickedSlot, int maxStackSize, int fromSlot) {
        simulateShiftClick(bottomInventory, topInventory, clickedSlot, maxStackSize, fromSlot, fromSlot);
    }

    public static void simulateShiftClick(Inventory bottomInventory, Inventory topInventory, int clickedSlot, int maxStackSize, int fromSlot, int toSlot) {
        for (int i = fromSlot; i <= toSlot; i++) {
            ItemStack itemTop = topInventory.getItem(i);
            ItemStack itemClicked = bottomInventory.getItem(clickedSlot);

            boolean itemTopIsAir = itemTop == null || itemTop.getType() == Material.AIR;
            boolean itemClickedIsAir = itemClicked == null || itemClicked.getType() == Material.AIR;

            if (!itemClickedIsAir) {
                if (itemTopIsAir) {
                    ItemStack newItemTop = itemClicked.clone();

                    if (newItemTop.getAmount() <= maxStackSize) {
                        topInventory.setItem(i, newItemTop);
                        bottomInventory.setItem(clickedSlot, null);
                    } else {
                        ItemStack newItemClicked = itemClicked.clone();

                        int actualMoved = Math.min(newItemTop.getAmount(), maxStackSize);
                        newItemTop.setAmount(actualMoved);
                        topInventory.setItem(i, newItemTop);

                        newItemClicked.setAmount(newItemClicked.getAmount() - actualMoved);
                        bottomInventory.setItem(clickedSlot, newItemClicked);
                    }
                } else {
                    boolean itemsAreSame = ToolsItem.isSameItemHash(itemTop, itemClicked);

                    if (itemsAreSame) {
                        int clickedAmount = itemClicked.getAmount();
                        int topAmount = itemTop.getAmount();

                        int combinedAmount = clickedAmount + topAmount;

                        if (combinedAmount <= maxStackSize) {
                            // PLACE_ALL
                            ItemStack newTop = itemTop.clone();
                            newTop.setAmount(combinedAmount);
                            topInventory.setItem(i, newTop);
                            bottomInventory.setItem(clickedSlot, null);
                        } else {
                            // PLACE_SOME

                            int remaining = combinedAmount - maxStackSize;
                            ItemStack newTop = itemTop.clone();
                            newTop.setAmount(maxStackSize);
                            topInventory.setItem(i, newTop);

                            ItemStack newClicked = itemClicked.clone();
                            newClicked.setAmount(remaining);
                            bottomInventory.setItem(clickedSlot, newClicked);
                        }
                    }
                }
            }
        }
    }

    public static void simulateDefaultClick(Player player, Inventory inventory, int slot, ClickType clickType) {
        simulateDefaultClick(player, inventory, slot, clickType, 64);
    }
    /**
     * Due to some inventories limiting allowed items, we need to simulate all inventory behavior manually and cannot rely on the InventoryAction
     * @param player
     * @param inventory
     * @param slot
     */
    public static void simulateDefaultClick(Player player, Inventory inventory, int slot, ClickType clickType, int maxStackSize) {
        ItemStack cursor = player.getItemOnCursor();
        ItemStack clicked = inventory.getItem(slot);

        boolean cursorIsAir = cursor.getType() == Material.AIR;
        boolean clickedIsAir = clicked == null || clicked.getType() == Material.AIR;

        boolean itemsAreSame = ToolsItem.isSameItemHash(cursor, clicked);

        if ((clickType == ClickType.LEFT || clickType == ClickType.RIGHT) && !cursorIsAir && !clickedIsAir && !itemsAreSame) {
            // SWAP_WITH_CURSOR
            ItemStack newClicked = cursor.clone();
            ItemStack newCursor = clicked.clone();

            if (newClicked.getAmount() <= maxStackSize && newCursor.getAmount() <= maxStackSize) {
                inventory.setItem(slot, newClicked);
                player.setItemOnCursor(newCursor);
            }
        } else if (clickType == ClickType.LEFT) {
            if (clickedIsAir && !cursorIsAir) {
                // PLACE_ALL
                ItemStack toPlace = cursor.clone();
                if (toPlace.getAmount() <= maxStackSize) {
                    inventory.setItem(slot, toPlace);
                    player.setItemOnCursor(null);
                } else {
                    int stackSize = toPlace.getType().getMaxStackSize();
                    int minimumStackSize = Math.min(stackSize, maxStackSize);

                    int originalAmount = toPlace.getAmount();
                    int leftOnCursorAmount = originalAmount - minimumStackSize;
                    toPlace.setAmount(minimumStackSize);

                    ItemStack leftOnCursor = toPlace.clone();
                    leftOnCursor.setAmount(leftOnCursorAmount);

                    inventory.setItem(slot, toPlace);
                    player.setItemOnCursor(leftOnCursor);
                }
            } else if (!clickedIsAir && cursorIsAir) {
                // PICKUP_ALL
                player.setItemOnCursor(clicked.clone());
                inventory.setItem(slot, null);
            } else if (!cursorIsAir && itemsAreSame) {
                int clickedAmount = clicked.getAmount();
                int cursorAmount = cursor.getAmount();

                int stackSize = cursor.getType().getMaxStackSize();
                int minimumStackSize = Math.min(stackSize, maxStackSize);

                if (clickedAmount < stackSize) {
                    int combinedAmount = clickedAmount + cursorAmount;

                    if (combinedAmount <= minimumStackSize) {
                        // PLACE_ALL
                        ItemStack newClicked = clicked.clone();
                        newClicked.setAmount(combinedAmount);
                        inventory.setItem(slot, newClicked);
                        player.setItemOnCursor(null);
                    } else {
                        // PLACE_SOME

                        int remaining = combinedAmount - minimumStackSize;
                        ItemStack newClicked = clicked.clone();
                        newClicked.setAmount(minimumStackSize);
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

                int stackSize = cursor.getType().getMaxStackSize();
                int minimumStackSize = Math.min(stackSize, maxStackSize);
                if (newClickedAmount <= minimumStackSize) {
                    newClicked.setAmount(newClickedAmount);
                    newCursor.setAmount(newCursorAmount);

                    inventory.setItem(slot, newClicked);
                    player.setItemOnCursor(newCursor);
                }
            }
        }
    }
}
