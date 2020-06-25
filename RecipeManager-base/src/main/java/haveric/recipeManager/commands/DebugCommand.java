package haveric.recipeManager.commands;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.recipes.brew.data.BrewingStandData;
import haveric.recipeManager.recipes.brew.data.BrewingStands;
import haveric.recipeManager.recipes.cooking.campfire.data.RMCampfireData;
import haveric.recipeManager.recipes.cooking.campfire.data.RMCampfires;
import haveric.recipeManager.recipes.compost.data.ComposterData;
import haveric.recipeManager.recipes.compost.data.Composters;
import haveric.recipeManager.recipes.cooking.furnace.data.FurnaceData;
import haveric.recipeManager.recipes.cooking.furnace.data.Furnaces;
import haveric.recipeManager.tools.Version;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class DebugCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MessageSender.getInstance().send(sender, "RM Version : " + RecipeManager.getPlugin().getDescription().getVersion());
        MessageSender.getInstance().send(sender, "Bukkit Version : " + Bukkit.getVersion());
        MessageSender.getInstance().send(sender, "Last Reload : " + RecipeManager.getLastReload());
        MessageSender.getInstance().send(sender, "Current Time: " + LocalDateTime.now());

        sendPermissionMessage(sender, "");

        if (Version.has1_12Support()) {
            MessageSender.getInstance().send(sender, "Gamerule 'doLimitedCrafting':");
            for (World world : Bukkit.getWorlds()) {
                String worldName = world.getName();
                Boolean gamerule = world.getGameRuleValue(GameRule.DO_LIMITED_CRAFTING);

                String gameruleMessage;
                if (gamerule == Boolean.TRUE) {
                    gameruleMessage = RMCChatColor.RED + "true";
                } else {
                    gameruleMessage = RMCChatColor.GREEN + "false";
                }
                MessageSender.getInstance().send(sender, "  " + worldName + ": " + gameruleMessage);
            }
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            Block block;
            if (Version.has1_14Support()) {
                block = player.getTargetBlockExact(20);
            } else {
                Set<Material> transparent = EnumSet.noneOf(Material.class);
                transparent.add(Material.AIR);
                block = player.getTargetBlock(transparent, 20);
            }

            if (block != null) {
                Location location = block.getLocation();
                Material type = block.getType();

                if (type == Material.BREWING_STAND) {
                    MessageSender.getInstance().send(sender, block.getType() + " Data: ");
                    BrewingStandData data = BrewingStands.get(location);
                    UUID fuelerUUID = data.getFuelerUUID();
                    MessageSender.getInstance().send(sender, "  Fueler UUID: " + fuelerUUID);

                    Player fueler = Bukkit.getPlayer(fuelerUUID);
                    if (fueler != null) {
                        MessageSender.getInstance().send(sender, "  Fueler: " + fueler.getName());
                        sendPermissionMessage(fueler, "  ");
                    }
                } else if ((Version.has1_14Support() && type == Material.CAMPFIRE) || (Version.has1_16Support() && type == Material.SOUL_CAMPFIRE)) {
                    MessageSender.getInstance().send(sender, block.getType() + " Data: ");
                    RMCampfireData data = RMCampfires.get(location);

                    for (int i = 0; i <= 3; i++) {
                        MessageSender.getInstance().send(sender, "  Slot " + i);
                        UUID uuid = data.getItemUUID(i);
                        MessageSender.getInstance().send(sender, "    UUID: " + uuid);
                        Player fueler = Bukkit.getPlayer(uuid);
                        if (fueler != null) {
                            MessageSender.getInstance().send(sender, "    Player: " + fueler.getName());
                            sendPermissionMessage(fueler, "    ");
                        }
                    }
                } else if (Version.has1_14Support() && type == Material.COMPOSTER) {
                    MessageSender.getInstance().send(sender, block.getType() + " Data: ");
                    ComposterData data = Composters.get(location);

                    UUID fuelerUUID = data.getPlayerUUID();
                    MessageSender.getInstance().send(sender, "  Fueler UUID: " + fuelerUUID);
                    Player fueler = Bukkit.getPlayer(fuelerUUID);
                    if (fueler != null) {
                        MessageSender.getInstance().send(sender, "  Player: " + fueler.getName());
                        sendPermissionMessage(fueler, "  ");
                    }
                    MessageSender.getInstance().send(sender, "  Recipe: " + data.getRecipe());
                    MessageSender.getInstance().send(sender, "  Level: " + data.getLevel());
                    MessageSender.getInstance().send(sender, "  Ingredients: " + data.getIngredients());
                } else if (type == Material.FURNACE || (!Version.has1_13BasicSupport() && type == Material.getMaterial("BURNING_FURNACE")) || Version.has1_14Support() && (type == Material.BLAST_FURNACE || type == Material.SMOKER)) {
                    MessageSender.getInstance().send(sender, block.getType() + " Data: ");
                    FurnaceData data = Furnaces.get(location);

                    MessageSender.getInstance().send(sender, "  Smelting: " + data.getSmelting());
                    MessageSender.getInstance().send(sender, "  Fuel: " + data.getFuel());

                    UUID fuelerUUID = data.getFuelerUUID();
                    MessageSender.getInstance().send(sender, "  Fueler UUID: " + fuelerUUID);

                    Player fueler = Bukkit.getPlayer(fuelerUUID);
                    if (fueler != null) {
                        MessageSender.getInstance().send(sender, "  Fueler: " + fueler.getName());
                        sendPermissionMessage(fueler, "  ");
                    }
                }
            }
        }

        return true;
    }

    private void sendPermissionMessage(CommandSender sender, String intro) {
        boolean hasPermission = sender.hasPermission("recipemanager.craft");

        String message;
        if (hasPermission) {
            message = RMCChatColor.GREEN + "true";
        } else {
            message = RMCChatColor.RED + "false";
        }

        MessageSender.getInstance().send(sender, intro + "Permission for 'recipemanager.craft': " + message);

    }
}
