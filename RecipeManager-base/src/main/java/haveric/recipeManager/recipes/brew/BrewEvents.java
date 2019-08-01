package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.brew.data.BrewingStandData;
import haveric.recipeManager.recipes.brew.data.BrewingStands;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BrewEvents implements Listener {
    public BrewEvents() { }

    public void clean() {
        HandlerList.unregisterAll(this);
    }

    public static void reload() {
        HandlerList.unregisterAll(RecipeManager.getBrewEvents());
        Bukkit.getPluginManager().registerEvents(RecipeManager.getBrewEvents(), RecipeManager.getPlugin());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void brewingStandPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.BREWING_STAND) {
            BrewingStands.add(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void brewingStandBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.BREWING_STAND) {
            BrewingStands.remove(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void brewingStandPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.BREWING_STAND) {
                if (!RecipeManager.getPlugin().canCraft(event.getPlayer())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void brewingStandInventoryClick(InventoryClickEvent event) {
        HumanEntity ent = event.getWhoClicked();

        if (ent instanceof Player) {
            Inventory brewingInventory = event.getInventory();
            InventoryHolder holder = brewingInventory.getHolder();

            if (brewingInventory instanceof BrewerInventory && holder instanceof BrewingStand) {
                if (event.getRawSlot() < brewingInventory.getSize()) {
                    BrewingStandData data = BrewingStands.get(((BrewingStand) holder).getLocation());
                    data.setFuelerUUID(ent.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void brewEvent(BrewEvent event) {
        BrewerInventory inventory = event.getContents();

        ItemStack ingredient = inventory.getIngredient();
        BrewRecipe recipe = RecipeManager.getRecipes().getBrewRecipe(ingredient);

        if (recipe != null) {
            Block block = event.getBlock();
            Location location = block.getLocation();
            BrewingStandData data = BrewingStands.get(location);
            Args a = Args.create().inventory(inventory).location(location).player(data.getFuelerUUID()).recipe(recipe).build();
            ItemResult result = recipe.getResult(a);

            if (result != null && recipe.sendCrafted(a)) {
                if (recipe.checkFlags(a) && result.checkFlags(a)) {
                    @SuppressWarnings("unchecked")
                    List<Boolean> potionBools = (List<Boolean>) a.extra();

                    ItemStack bukkitResult = result.toItemStack();

                    boolean cancel = false;
                    if (potionBools.get(0)) {
                        inventory.setItem(0, bukkitResult.clone());
                        cancel = true;
                    }

                    if (potionBools.get(1)) {
                        inventory.setItem(1, bukkitResult.clone());
                        cancel = true;
                    }

                    if (potionBools.get(2)) {
                        inventory.setItem(2, bukkitResult.clone());
                        cancel = true;
                    }

                    if (cancel) {
                        event.setCancelled(true);
                        ItemStack originalIngredient = inventory.getItem(3);
                        originalIngredient.setAmount(originalIngredient.getAmount() - 1);

                        inventory.setItem(3, originalIngredient);
                    }
                }
            }
        }
    }
}
