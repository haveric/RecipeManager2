package haveric.recipeManager.commands;

import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class ExtractCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "recipes" + File.separator + "disabled" + File.separator + "extracted recipes (" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + ").txt");

        if (file.exists()) {
            Messages.getInstance().send(sender, "cmd.extract.wait");
            return true;
        }

        boolean skipSpecial = true;

        if (args.length > 0) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("special")) {
                    skipSpecial = false;
                } else {
                    Messages.getInstance().send(sender, "cmd.extract.unknownarg", "{arg}", arg);
                }
            }
        }

        Messages.getInstance().send(sender, "cmd.extract.converting");

        List<String> parsedCraftRecipes = new ArrayList<String>();
        List<String> parsedCombineRecipes = new ArrayList<String>();
        List<String> parsedSmeltRecipes = new ArrayList<String>();

        Iterator<Recipe> recipes = Bukkit.getServer().recipeIterator();

        Recipe r;
        int recipesNum = 0;

        while (recipes.hasNext()) {
            try {
                r = recipes.next();

                if (r == null || RecipeManager.getRecipes().isCustomRecipe(r)) {
                    continue;
                }

                if (skipSpecial) {
                    if (Vanilla.isSpecialRecipe(r)) {
                        continue;
                    }
                }

                if (r instanceof ShapedRecipe) {
                    ShapedRecipe recipe = (ShapedRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.CRAFT.getDirective()).append(Files.NL);
                    Map<Character, ItemStack> items = recipe.getIngredientMap();
                    String[] shape = recipe.getShape();
                    char[] cols;
                    ItemStack item;

                    for (String element : shape) {
                        cols = element.toCharArray();

                        int colsLength = cols.length;
                        for (int c = 0; c < colsLength; c++) {
                            item = items.get(cols[c]);

                            recipeString.append(parseIngredient(item));

                            if ((c + 1) < colsLength) {
                                recipeString.append(" + ");
                            }
                        }

                        recipeString.append(Files.NL);
                    }

                    parseResult(recipe.getResult(), recipeString);

                    parsedCraftRecipes.add(recipeString.toString());
                } else if (r instanceof ShapelessRecipe) {
                    ShapelessRecipe recipe = (ShapelessRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.COMBINE.getDirective()).append(Files.NL);
                    List<ItemStack> ingredients = recipe.getIngredientList();

                    int size = ingredients.size();
                    for (int i = 0; i < size; i++) {
                        recipeString.append(parseIngredient(ingredients.get(i)));

                        if ((i + 1) < size) {
                            recipeString.append(" + ");
                        }
                    }

                    recipeString.append(Files.NL);
                    parseResult(recipe.getResult(), recipeString);

                    parsedCombineRecipes.add(recipeString.toString());
                } else if (r instanceof FurnaceRecipe) {
                    FurnaceRecipe recipe = (FurnaceRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.SMELT.getDirective()).append(Files.NL);

                    recipeString.append(parseIngredient(recipe.getInput()));
                    recipeString.append(Files.NL);
                    parseResult(recipe.getResult(), recipeString);

                    parsedSmeltRecipes.add(recipeString.toString());
                }

                recipesNum++;
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        if (recipesNum == 0) {
            Messages.getInstance().send(sender, "cmd.extract.norecipes");
        } else {
            try {
                file.getParentFile().mkdirs();

                BufferedWriter stream = new BufferedWriter(new FileWriter(file, false));

                stream.write("// You can uncomment one of the following lines to apply a flag to the entire file:" + Files.NL);
                stream.write("//@remove   // remove these recipes from the server." + Files.NL);
                stream.write("//@restrict // prevents recipes from being used with a notification, you can also set a custom message." + Files.NL);
                stream.write("//@override // overwrites recipe to allow result change and adding flags to it." + Files.NL);

                stream.write("//---------------------------------------------------" + Files.NL + "// Craft recipes" + Files.NL + Files.NL);

                for (String str : parsedCraftRecipes) {
                    stream.write(str);
                }

                stream.write("//---------------------------------------------------" + Files.NL + "// Combine recipes" + Files.NL + Files.NL);

                for (String str : parsedCombineRecipes) {
                    stream.write(str);
                }

                stream.write("//---------------------------------------------------" + Files.NL + "// Smelt recipes" + Files.NL + Files.NL);

                for (String str : parsedSmeltRecipes) {
                    stream.write(str);
                }

                stream.close();

                Messages.getInstance().send(sender, "cmd.extract.done", "{file}", file.getPath().replace(RecipeManager.getPlugin().getDataFolder().toString(), ""));
            } catch (Throwable e) {
                MessageSender.getInstance().error(sender, e, "Error writing '" + file.getName() + "'");
            }
        }

        return true;
    }

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
