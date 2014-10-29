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

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                    // TODO: add new message
                    Messages.CMD_EXTRACT_WAIT.print(sender);
                    return true;
                }

                StringBuilder recipeString = new StringBuilder(RecipeType.CRAFT.getDirective()).append(Files.NL);
                recipeString.append(parseIngredient(holdingStack));
                recipeString.append(Files.NL);
                parseResult(holdingStack, recipeString);

                file.getParentFile().mkdirs();

                try {
                    BufferedWriter stream = new BufferedWriter(new FileWriter(file, false));

                    stream.write(recipeString.toString());

                    stream.close();

                    // TODO: add new message
                    Messages.CMD_EXTRACT_DONE.print(sender, null, "{file}", file.getPath().replace(RecipeManager.getPlugin().getDataFolder().toString(), ""));
                } catch (IOException e) {
                    Messages.error(sender, e, "Error writing '" + file.getName() + "'");
                }
            }
        }

        return false;
    }

    // TODO: Move to shared space to remove duplicate code
    private String parseIngredient(ItemStack item) {
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
        }

        return name;
    }

    // TODO: Move to shared space to remove duplicate code
    private void parseResult(ItemStack result, StringBuilder recipeString) {
        recipeString.append("= ").append(result.getType().toString().toLowerCase()).append(':').append(result.getDurability()).append(':').append(result.getAmount());

        int enchantments = result.getEnchantments().size();

        if (enchantments > 0) {
            for (Entry<Enchantment, Integer> entry : result.getEnchantments().entrySet()) {
                recipeString.append(Files.NL).append("  @").append(FlagType.ENCHANTITEM.getName()).append(' ').append(entry.getKey().toString()).append(' ').append(entry.getValue());
            }
        }

        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                recipeString.append(Files.NL).append("  @").append(FlagType.ITEMNAME.getName()).append(' ').append(meta.getDisplayName());
            }

            if (meta.hasLore()) {
                List<String> lores = meta.getLore();
                for (String lore : lores) {
                    recipeString.append(Files.NL).append("  @").append(FlagType.ITEMLORE.getName()).append(' ').append(lore);
                }
            }
        }

        recipeString.append(Files.NL).append(Files.NL);
    }

}
