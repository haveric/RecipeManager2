package haveric.recipeManager.commands;

import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.RMCVanilla;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

public class CreateRecipeCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = ((Player) sender);
            ItemStack holdingStack;
            if (Version.has1_12Support()) {
                holdingStack = player.getInventory().getItemInMainHand();
            } else {
                holdingStack = player.getItemInHand();
            }
            PlayerInventory inventory = player.getInventory();

            if (holdingStack == null || holdingStack.getType() == Material.AIR) {
                MessageSender.getInstance().send(sender, "No item to extract a recipe from.");
            } else {
                File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "recipes" + File.separator + "disabled" + File.separator + "extracted recipe (" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + ").txt");

                if (file.exists()) {
                    Messages.getInstance().send(sender, "cmd.extract.wait");
                    return true;
                }

                StringBuilder craftString = new StringBuilder(RMCRecipeType.CRAFT.getDirective()).append(Files.NL);
                StringBuilder combineString = new StringBuilder(RMCRecipeType.COMBINE.getDirective()).append(Files.NL);
                StringBuilder conditionString = new StringBuilder();

                parseResult(holdingStack, conditionString);

                ItemStack[] ingredients = new ItemStack[9];
                ingredients[0] = inventory.getItem(9);
                ingredients[1] = inventory.getItem(10);
                ingredients[2] = inventory.getItem(11);

                ingredients[3] = inventory.getItem(18);
                ingredients[4] = inventory.getItem(19);
                ingredients[5] = inventory.getItem(20);

                ingredients[6] = inventory.getItem(27);
                ingredients[7] = inventory.getItem(28);
                ingredients[8] = inventory.getItem(29);

                int numNulls = 0;
                for (int i = 0; i <= 8; i++) {
                    if (ingredients[i] == null) {
                        numNulls ++;
                    }
                }

                if (numNulls == 9) {
                    MessageSender.getInstance().send(player, RMCChatColor.RED + "No ingredients found in the left 3x3 inventory slots.");
                    return false;
                }

                Tools.trimItemMatrix(ingredients);

                int width = 0;
                int height = 0;

                for (int h = 0; h < 3; h++) {
                    for (int w = 0; w < 3; w++) {
                        ItemStack item = ingredients[(h * 3) + w];

                        if (item != null) {
                            width = Math.max(width, w);
                            height = Math.max(height, h);
                        }
                    }
                }

                width++;
                height++;

                parseIngredient(ingredients[0], craftString, conditionString);
                if (width > 1) {
                    craftString.append(" + ");
                    parseIngredient(ingredients[1], craftString, conditionString);
                }
                if (width > 2) {
                    craftString.append(" + ");
                    parseIngredient(ingredients[2], craftString, conditionString);
                }
                craftString.append(Files.NL);

                if (height > 1) {
                    parseIngredient(ingredients[3], craftString, conditionString);
                    if (width > 1) {
                        craftString.append(" + ");
                        parseIngredient(ingredients[4], craftString, conditionString);
                    }
                    if (width > 2) {
                        craftString.append(" + ");
                        parseIngredient(ingredients[5], craftString, conditionString);
                    }
                    craftString.append(Files.NL);
                }

                if (height > 2) {
                    parseIngredient(ingredients[6], craftString, conditionString);
                    if (width > 1) {
                        craftString.append(" + ");
                        parseIngredient(ingredients[7], craftString, conditionString);
                    }
                    if (width > 2) {
                        craftString.append(" + ");
                        parseIngredient(ingredients[8], craftString, conditionString);
                    }
                    craftString.append(Files.NL);
                }

                boolean first = true;
                for (ItemStack item : ingredients) {
                    if (item != null) {
                        if (first) {
                            first = false;
                        } else {
                            combineString.append(" + ");
                        }
                        parseIngredientName(item, combineString);
                    }
                }
                combineString.append(Files.NL);

                StringBuilder recipeString = new StringBuilder();
                recipeString.append(craftString).append(conditionString).append(Files.NL);
                recipeString.append(combineString).append(conditionString).append(Files.NL);

                file.getParentFile().mkdirs();

                try {
                    BufferedWriter stream = new BufferedWriter(new FileWriter(file, false));

                    stream.write(recipeString.toString());

                    stream.close();

                    Messages.getInstance().send(sender, "cmd.extractrecipe.done", "{file}", file.getPath().replace(RecipeManager.getPlugin().getDataFolder().toString(), ""));
                } catch (IOException e) {
                    MessageSender.getInstance().error(sender, e, "Error writing '" + file.getName() + "'");
                }
            }
        }

        return true;
    }

    private void parseIngredient(ItemStack item, StringBuilder recipeString, StringBuilder conditionString) {
        parseIngredientName(item, recipeString);

        if (item != null && item.getType() != Material.AIR) {
            String ingredientCondition = "";

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (meta.hasDisplayName()) {
                    ingredientCondition += " | name " + meta.getDisplayName();
                }

                if (meta.hasLore()) {
                    List<String> lores = meta.getLore();
                    for (String lore : lores) {
                        ingredientCondition += " | lore " + lore;
                    }
                }

                if (meta instanceof LeatherArmorMeta) {
                    LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
                    Color color = leatherMeta.getColor();

                    if (!color.equals(Bukkit.getItemFactory().getDefaultLeatherColor())) {
                        ingredientCondition += " | color " + color.getRed() + "," + color.getGreen() + "," + color.getBlue();
                    }
                }
            }

            if (item.getAmount() != 1) {
                ingredientCondition += " | amount " + item.getAmount();
            }

            if (item.getEnchantments().size() > 0) {
                for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                    ingredientCondition += " | enchant " + entry.getKey().getName() + " " + entry.getValue();
                }
            }

            if (ingredientCondition.length() > 0) {
                conditionString.append(FlagType.INGREDIENT_CONDITION).append(' ').append(item.getType().toString().toLowerCase()).append(ingredientCondition);
                conditionString.append(Files.NL);
            }
        }
    }

    private void parseIngredientName(ItemStack item, StringBuilder recipeString) {
        String name;

        if (item == null || item.getType() == Material.AIR) {
            name = "air";
        } else {
            name = item.getType().toString().toLowerCase();

            if (item.getDurability() == -1 || item.getDurability() == RMCVanilla.DATA_WILDCARD) {
                name += ":*";
            } else if (item.getDurability() != 0) {
                name += ":" + item.getDurability();
            }
        }

        recipeString.append(name);
    }

    private void parseResult(ItemStack result, StringBuilder recipeString) {
        recipeString.append("= ").append(result.getType().toString().toLowerCase());
        if (result.getDurability() != 0 || result.getAmount() > 1) {
            recipeString.append(':').append(result.getDurability());
        }
        if (result.getAmount() > 1) {
            recipeString.append(':').append(result.getAmount());
        }

        if (result.getEnchantments().size() > 0) {
            for (Entry<Enchantment, Integer> entry : result.getEnchantments().entrySet()) {
                recipeString.append(Files.NL).append("@enchant ").append(entry.getKey().getName()).append(' ').append(entry.getValue());
            }
        }

        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                recipeString.append(Files.NL).append("@name ").append(meta.getDisplayName());
            }

            if (meta.hasLore()) {
                List<String> lores = meta.getLore();
                for (String lore : lores) {
                    recipeString.append(Files.NL).append("@lore ").append(lore);
                }
            }

            if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
                Color color = leatherMeta.getColor();

                if (!color.equals(Bukkit.getItemFactory().getDefaultLeatherColor())) {
                    recipeString.append(Files.NL).append("@leathercolor ").append(color.getRed()).append(' ').append(color.getGreen()).append(' ').append(color.getBlue());
                }
            }
        }

        recipeString.append(Files.NL);
    }
}
