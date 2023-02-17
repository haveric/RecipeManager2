package haveric.recipeManager.recipes.cooking.campfire;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.BaseRecipeEvents;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.cooking.campfire.data.RMCampfireData;
import haveric.recipeManager.recipes.cooking.campfire.data.RMCampfires;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Campfire;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class RMCampfireEvents extends BaseRecipeEvents {
    public RMCampfireEvents() { }

    @EventHandler(priority = EventPriority.LOW)
    public void rmCampfirePlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();

            if (item != null && item.getType() != Material.AIR) {
                Block block = event.getClickedBlock();

                if (block != null && (block.getType() == Material.CAMPFIRE || (Version.has1_16Support() && block.getType() == Material.SOUL_CAMPFIRE))) {
                    Campfire campfire = (Campfire) block.getState();

                    int slot = -1;
                    for (int i = 0; i <= 3; i++) {
                        ItemStack currentIngredient = campfire.getItem(i);

                        if (currentIngredient == null) {
                            slot = i;
                            break;
                        }
                    }

                    if (slot != -1) {
                        RMCampfireData data = RMCampfires.get(campfire.getLocation());
                        Player player = event.getPlayer();

                        data.setItemId(slot, player.getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void rmCampfireCookEvent(BlockCookEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();
        if (blockType == Material.CAMPFIRE || (Version.has1_16Support() && blockType == Material.SOUL_CAMPFIRE)) {
            ItemStack ingredient = event.getSource();

            BaseRecipe baseRecipe = RecipeManager.getRecipes().getRecipe(RMCRecipeType.CAMPFIRE, ingredient);
            if (baseRecipe instanceof RMCampfireRecipe) {
                RMCampfireRecipe recipe = (RMCampfireRecipe) baseRecipe;
                Campfire campfire = (Campfire) block.getState();

                int slot = -1;
                for (int i = 0; i <= 3; i++) {
                    ItemStack currentIngredient = campfire.getItem(i);

                    if (currentIngredient != null && campfire.getCookTime(i) == recipe.getCookTicks()) {
                        slot = i;
                        break;
                    }
                }

                if (slot != -1) {
                    RMCampfireData data = RMCampfires.get(campfire.getLocation());
                    UUID playerUUID = data.getItemUUID(slot);
                    data.setItemId(slot, null);
                    if (data.allSlotsEmpty()) {
                        RMCampfires.remove(campfire.getLocation());
                    }

                    Args a = Args.create().player(playerUUID).recipe(recipe).build();
                    if (!recipe.checkFlags(a)) {
                        event.setCancelled(true);
                        return;
                    }

                    ItemResult result = recipe.getResult(a);

                    a = Args.create().player(playerUUID).recipe(recipe).result(result).build();

                    if (!result.checkFlags(a)) {
                        event.setCancelled(true);
                        return;
                    }

                    a.clear();

                    boolean recipeCraftSuccess = recipe.sendCrafted(a);
                    if (recipeCraftSuccess) {
                        a.sendEffects(a.player(), Messages.getInstance().get("flag.prefix.recipe"));
                    }

                    a.clear();

                    boolean resultCraftSuccess = result.sendCrafted(a);
                    if (resultCraftSuccess) {
                        a.sendEffects(a.player(), Messages.getInstance().parse("flag.prefix.result", "{item}", ToolsItem.print(result)));
                    }

                    if (recipeCraftSuccess && resultCraftSuccess) {
                        event.setResult(a.result());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void rmCampfireBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();

        if (blockType == Material.CAMPFIRE || (Version.has1_16Support() && blockType == Material.SOUL_CAMPFIRE)) {
            RMCampfires.remove(block.getLocation());
        }
    }
}
