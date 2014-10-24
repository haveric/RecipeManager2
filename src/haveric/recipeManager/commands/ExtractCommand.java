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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;


public class ExtractCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "recipes" + File.separator + "disabled" + File.separator + "extracted recipes (" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + ").txt");

        if (file.exists()) {
            Messages.CMD_EXTRACT_WAIT.print(sender);
            return true;
        }

        boolean skipSpecial = true;

        if (args.length > 0) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("special")) {
                    skipSpecial = false;
                } else {
                    Messages.CMD_EXTRACT_UNKNOWNARG.print(sender, null, "{arg}", arg);
                }
            }
        }

        Messages.CMD_EXTRACT_CONVERTING.print(sender);

        List<String> parsedCraftRecipes = new ArrayList<String>();
        List<String> parsedCombineRecipes = new ArrayList<String>();
        List<String> parsedSmeltRecipes = new ArrayList<String>();

        Iterator<Recipe> recipes = Bukkit.getServer().recipeIterator();
        ItemStack result;
        Recipe r;
        int recipesNum = 0;

        while (recipes.hasNext()) {
            r = recipes.next();

            if (r == null || RecipeManager.getRecipes().isCustomRecipe(r)) {
                continue;
            }

            if (skipSpecial) {
                result = r.getResult();

                if (result.equals(Vanilla.RECIPE_LEATHERDYE) || result.equals(Vanilla.RECIPE_FIREWORKS) || result.equals(Vanilla.RECIPE_MAPCLONE) || result.equals(Vanilla.RECIPE_MAPEXTEND)) {
                    continue;
                }
            }

            if (r instanceof ShapedRecipe) {
                ShapedRecipe recipe = (ShapedRecipe) r;
                StringBuilder recipeString = new StringBuilder(RecipeType.CRAFT.getDirective()).append(Files.NL);
                Map<Character, ItemStack> items = recipe.getIngredientMap();
                String[] shape = recipe.getShape();
                char[] cols;
                ItemStack item;

                for (String element : shape) {
                    cols = element.toCharArray();

                    for (int c = 0; c < cols.length; c++) {
                        item = items.get(cols[c]);

                        recipeString.append(parseIngredient(item));

                        if ((c + 1) < cols.length) {
                            recipeString.append(" + ");
                        }
                    }

                    recipeString.append(Files.NL);
                }

                parseResult(recipe.getResult(), recipeString);

                parsedCraftRecipes.add(recipeString.toString());
            } else if (r instanceof ShapelessRecipe) {
                ShapelessRecipe recipe = (ShapelessRecipe) r;
                StringBuilder recipeString = new StringBuilder(RecipeType.COMBINE.getDirective()).append(Files.NL);
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
                StringBuilder recipeString = new StringBuilder(RecipeType.SMELT.getDirective()).append(Files.NL);

                recipeString.append(parseIngredient(recipe.getInput()));
                recipeString.append(Files.NL);
                parseResult(recipe.getResult(), recipeString);

                parsedSmeltRecipes.add(recipeString.toString());
            }

            recipesNum++;
        }

        if (recipesNum == 0) {
            Messages.CMD_EXTRACT_NORECIPES.print(sender);
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

                Messages.CMD_EXTRACT_DONE.print(sender, null, "{file}", file.getPath().replace(RecipeManager.getPlugin().getDataFolder().toString(), ""));
            } catch (Throwable e) {
                Messages.error(sender, e, "Error writing '" + file.getName() + "'");
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

        int enchantments = result.getEnchantments().size();

        if (enchantments > 0) {
            for (Entry<Enchantment, Integer> entry : result.getEnchantments().entrySet()) {
                recipeString.append(Files.NL).append("  @").append(FlagType.ENCHANTITEM.getName()).append(' ').append(entry.getKey().toString()).append(' ').append(entry.getValue());
            }
        }

        recipeString.append(Files.NL).append(Files.NL);
    }
}
