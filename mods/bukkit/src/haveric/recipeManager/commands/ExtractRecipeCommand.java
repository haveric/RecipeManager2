package haveric.recipeManager.commands;

import haveric.recipeManager.Files;
import haveric.recipeManager.Messages;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.recipes.BaseRecipe.RecipeType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ExtractRecipeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            ItemStack holdingStack = ((Player) sender).getItemInHand();

            if (holdingStack == null || holdingStack.getType() == Material.AIR) {
                Messages.send(sender, "No item to extract a recipe from.");
            } else {
                File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "recipes" + File.separator + "disabled" + File.separator + "extracted item (" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + ").txt");

                if (file.exists()) {
                    Messages.CMD_EXTRACT_WAIT.print(sender);
                    return true;
                }

                StringBuilder recipeString = new StringBuilder(RecipeType.CRAFT.getDirective()).append(Files.NL);
                parseIngredient(holdingStack, recipeString);
                recipeString.append(Files.NL);
                parseResult(holdingStack, recipeString);

                file.getParentFile().mkdirs();

                try {
                    BufferedWriter stream = new BufferedWriter(new FileWriter(file, false));

                    stream.write(recipeString.toString());

                    stream.close();

                    Messages.CMD_EXTRACTRECIPE_DONE.print(sender, null, "{file}", file.getPath().replace(RecipeManager.getPlugin().getDataFolder().toString(), ""));
                } catch (IOException e) {
                    Messages.error(sender, e, "Error writing '" + file.getName() + "'");
                }
            }
        }

        return false;
    }

    private void parseIngredient(ItemStack item, StringBuilder recipeString) {
        String name;

        if (item == null || item.getType() == Material.AIR) {
            name = "air";
        } else {
            name = item.getType().toString().toLowerCase() + ":";

            if (item.getDurability() == -1 || item.getDurability() == Vanilla.DATA_WILDCARD) {
                name += "*";
            } else {
                name += item.getDurability();
            }

            if (item.getAmount() != 1) {
                name += ":" + item.getAmount();
            }

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

            if (item.getEnchantments().size() > 0) {
                for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                    ingredientCondition += " | enchant " + entry.getKey().getName() + " " + entry.getValue();
                }
            }

            if (ingredientCondition.length() > 0) {
                recipeString.append('@').append(FlagType.INGREDIENTCONDITION.getName()).append(' ').append(item.getType().toString().toLowerCase()).append(ingredientCondition);
                recipeString.append(Files.NL);
            }

        }

        recipeString.append(name);
    }

    private void parseResult(ItemStack result, StringBuilder recipeString) {
        recipeString.append("= ").append(result.getType().toString().toLowerCase()).append(':').append(result.getDurability()).append(':').append(result.getAmount());

        if (result.getEnchantments().size() > 0) {
            for (Entry<Enchantment, Integer> entry : result.getEnchantments().entrySet()) {
                recipeString.append(Files.NL).append("  @enchant ").append(entry.getKey().getName()).append(' ').append(entry.getValue());
            }
        }

        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                recipeString.append(Files.NL).append("  @name ").append(meta.getDisplayName());
            }

            if (meta.hasLore()) {
                List<String> lores = meta.getLore();
                for (String lore : lores) {
                    recipeString.append(Files.NL).append("  @lore ").append(lore);
                }
            }

            if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
                Color color = leatherMeta.getColor();

                if (!color.equals(Bukkit.getItemFactory().getDefaultLeatherColor())) {
                    recipeString.append(Files.NL).append("  @leathercolor ").append(color.getRed() + " " + color.getGreen() + " " + color.getBlue());
                }
            }
        }

        recipeString.append(Files.NL).append(Files.NL);
    }

}
