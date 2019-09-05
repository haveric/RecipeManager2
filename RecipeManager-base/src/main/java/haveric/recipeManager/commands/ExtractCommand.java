package haveric.recipeManager.commands;

import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.compost.CompostRecipe;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.RMCVanilla;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import static haveric.recipeManager.Vanilla.isSpecialRecipe;
import static org.bukkit.Tag.REGISTRY_BLOCKS;
import static org.bukkit.Tag.REGISTRY_ITEMS;

public class ExtractCommand implements TabExecutor {
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

        List<String> parsedCraftRecipes = new ArrayList<>();
        List<String> parsedCombineRecipes = new ArrayList<>();
        List<String> parsedSmeltRecipes = new ArrayList<>();
        List<String> parsedBlastingRecipes = new ArrayList<>();
        List<String> parsedSmokingRecipes = new ArrayList<>();
        List<String> parsedCampfireRecipes = new ArrayList<>();
        List<String> parsedStonecuttingRecipes = new ArrayList<>();

        List<String> parsedFuelRecipes = new ArrayList<>();
        List<String> parsedCompostRecipes = new ArrayList<>();

        Iterator<Recipe> recipes = Bukkit.recipeIterator();

        Recipe r;
        int recipesNum = 0;

        while (recipes.hasNext()) {
            try {
                r = recipes.next();

                if (r == null || RecipeManager.getRecipes().isCustomRecipe(r)) {
                    continue;
                }

                if (skipSpecial) {
                    if (isSpecialRecipe(r)) {
                        continue;
                    }
                }

                if (r instanceof ShapedRecipe) {
                    ShapedRecipe recipe = (ShapedRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.CRAFT.getDirective()).append(Files.NL);

                    if (Version.has1_13Support()) {
                        Map<Character, RecipeChoice> choices = recipe.getChoiceMap();
                        char[] cols;
                        RecipeChoice choice;

                        for (String element : recipe.getShape()) {
                            cols = element.toCharArray();

                            int colsLength = cols.length;
                            for (int c = 0; c < colsLength; c++) {
                                choice = choices.get(cols[c]);

                                parseChoice(choice, recipeString);

                                if (c + 1 < colsLength) {
                                    recipeString.append(" + ");
                                }
                            }

                            recipeString.append(Files.NL);
                        }
                    } else {
                        Map<Character, ItemStack> items = recipe.getIngredientMap();
                        char[] cols;
                        ItemStack item;

                        for (String element : recipe.getShape()) {
                            cols = element.toCharArray();

                            int colsLength = cols.length;
                            for (int c = 0; c < colsLength; c++) {
                                item = items.get(cols[c]);

                                recipeString.append(parseIngredient(item));

                                if (c + 1 < colsLength) {
                                    recipeString.append(" + ");
                                }
                            }

                            recipeString.append(Files.NL);
                        }
                    }

                    parseResult(recipe.getResult(), recipeString);

                    parsedCraftRecipes.add(recipeString.toString());
                } else if (r instanceof ShapelessRecipe) {
                    ShapelessRecipe recipe = (ShapelessRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.COMBINE.getDirective()).append(Files.NL);

                    if (Version.has1_13Support()) {
                        List<RecipeChoice> ingredientChoices = recipe.getChoiceList();

                        int size = ingredientChoices.size();
                        for (int i = 0; i < size; i++) {
                            parseChoice(ingredientChoices.get(i), recipeString);

                            if (i + 1 < size) {
                                recipeString.append(" + ");
                            }
                        }
                    } else {
                        List<ItemStack> ingredients = recipe.getIngredientList();

                        int size = ingredients.size();
                        for (int i = 0; i < size; i++) {
                            recipeString.append(parseIngredient(ingredients.get(i)));

                            if (i + 1 < size) {
                                recipeString.append(" + ");
                            }
                        }
                    }

                    recipeString.append(Files.NL);
                    parseResult(recipe.getResult(), recipeString);

                    parsedCombineRecipes.add(recipeString.toString());
                } else if (r instanceof FurnaceRecipe) {
                    FurnaceRecipe recipe = (FurnaceRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.SMELT.getDirective()).append(Files.NL);

                    if (Version.has1_13Support()) {
                        parseChoice(recipe.getInputChoice(), recipeString);
                    } else {
                        recipeString.append(parseIngredient(recipe.getInput()));
                    }

                    recipeString.append(Files.NL);
                    parseResult(recipe.getResult(), recipeString);

                    parsedSmeltRecipes.add(recipeString.toString());
                } else if (Version.has1_14Support()){
                    if (r instanceof BlastingRecipe) {
                        BlastingRecipe recipe = (BlastingRecipe) r;
                        StringBuilder recipeString = new StringBuilder(RMCRecipeType.BLASTING.getDirective()).append(Files.NL);

                        if (Version.has1_13Support()) {
                            parseChoice(recipe.getInputChoice(), recipeString);
                        } else {
                            recipeString.append(parseIngredient(recipe.getInput()));
                        }

                        recipeString.append(Files.NL);
                        parseResult(recipe.getResult(), recipeString);

                        parsedBlastingRecipes.add(recipeString.toString());
                    } else if (r instanceof SmokingRecipe) {
                        SmokingRecipe recipe = (SmokingRecipe) r;
                        StringBuilder recipeString = new StringBuilder(RMCRecipeType.SMOKING.getDirective()).append(Files.NL);

                        if (Version.has1_13Support()) {
                            parseChoice(recipe.getInputChoice(), recipeString);
                        } else {
                            recipeString.append(parseIngredient(recipe.getInput()));
                        }

                        recipeString.append(Files.NL);
                        parseResult(recipe.getResult(), recipeString);

                        parsedSmokingRecipes.add(recipeString.toString());
                    } else if (r instanceof CampfireRecipe) {
                        CampfireRecipe recipe = (CampfireRecipe) r;
                        StringBuilder recipeString = new StringBuilder(RMCRecipeType.CAMPFIRE.getDirective()).append(Files.NL);

                        if (Version.has1_13Support()) {
                            parseChoice(recipe.getInputChoice(), recipeString);
                        } else {
                            recipeString.append(parseIngredient(recipe.getInput()));
                        }

                        recipeString.append(Files.NL);
                        parseResult(recipe.getResult(), recipeString);

                        parsedCampfireRecipes.add(recipeString.toString());
                    } else if (r instanceof StonecuttingRecipe) {
                        StonecuttingRecipe recipe = (StonecuttingRecipe) r;
                        StringBuilder recipeString = new StringBuilder(RMCRecipeType.STONECUTTING.getDirective()).append(Files.NL);

                        if (Version.has1_13Support()) {
                            parseChoice(recipe.getInputChoice(), recipeString);
                        } else {
                            recipeString.append(parseIngredient(recipe.getInput()));
                        }

                        recipeString.append(Files.NL);
                        parseResult(recipe.getResult(), recipeString);

                        parsedStonecuttingRecipes.add(recipeString.toString());
                    }
                }

                recipesNum++;
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        Map<BaseRecipe, RMCRecipeInfo> initialRecipes = Vanilla.getInitialRecipes();

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : initialRecipes.entrySet()) {
            BaseRecipe recipe = entry.getKey();

            if (recipe instanceof FuelRecipe) {
                FuelRecipe fuelRecipe = (FuelRecipe) recipe;
                StringBuilder recipeString = new StringBuilder(RMCRecipeType.FUEL.getDirective()).append(Files.NL);

                recipeString.append(parseIngredient(fuelRecipe.getIngredient()));
                recipeString.append(" % ").append(fuelRecipe.getMinTime());

                recipeString.append(Files.NL).append(Files.NL);

                parsedFuelRecipes.add(recipeString.toString());
            } else if (recipe instanceof CompostRecipe) {
                CompostRecipe compostRecipe = (CompostRecipe) recipe;
                StringBuilder recipeString = new StringBuilder(RMCRecipeType.COMPOST.getDirective()).append(Files.NL);

                parseMaterialList(compostRecipe.getIngredients(), recipeString);
                recipeString.append(" % ").append(compostRecipe.getLevelSuccessChance()).append(" % ").append(compostRecipe.getLevels());

                recipeString.append(Files.NL);
                parseResult(compostRecipe.getFirstResult(), recipeString);

                parsedCompostRecipes.add(recipeString.toString());
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

                writeRecipes(stream, parsedCraftRecipes, "Craft");
                writeRecipes(stream, parsedCombineRecipes, "Combine");
                writeRecipes(stream, parsedSmeltRecipes, "Smelt");

                if (Version.has1_14Support()) {
                    writeRecipes(stream, parsedBlastingRecipes, "Blasting");
                    writeRecipes(stream, parsedSmokingRecipes, "Smoking");
                    writeRecipes(stream, parsedCampfireRecipes, "Campfire");
                    writeRecipes(stream, parsedStonecuttingRecipes, "Stonecutting");
                    writeRecipes(stream, parsedCompostRecipes, "Compost");
                }

                writeRecipes(stream, parsedFuelRecipes, "Fuel");

                stream.close();

                Messages.getInstance().send(sender, "cmd.extract.done", "{file}", file.getPath().replace(RecipeManager.getPlugin().getDataFolder().toString(), ""));
            } catch (Throwable e) {
                MessageSender.getInstance().error(sender, e, "Error writing '" + file.getName() + "'");
            }
        }

        return true;
    }

    private void writeRecipes(BufferedWriter stream, List<String> recipes, String recipeType) throws IOException {
        stream.write("//---------------------------------------------------" + Files.NL + "// " + recipeType + " recipes" + Files.NL + Files.NL);

        for (String str : recipes) {
            stream.write(str);
        }
    }

    private String parseIngredient(ItemStack item) {
        String name;

        if (item == null || item.getType() == Material.AIR) {
            name = "air";
        } else {
            name = parseMaterial(item.getType()) + ":";

            if (item.getDurability() == -1 || item.getDurability() == RMCVanilla.DATA_WILDCARD) {
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

    private void parseChoice(RecipeChoice choice, StringBuilder recipeString) {
        if (choice instanceof RecipeChoice.MaterialChoice) {
            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
            parseMaterialList(materialChoice.getChoices(), recipeString);
        } else {
            recipeString.append("air");
        }
    }

    private void parseMaterialList(List<Material> materials, StringBuilder recipeString) {
        Tag<Material> tag = getChoiceTagMatch(materials, REGISTRY_BLOCKS);
        if (tag == null) {
            tag = getChoiceTagMatch(materials, REGISTRY_ITEMS);
        }

        if (tag == null) {
            int choicesSize = materials.size();

            for (int j = 0; j < choicesSize; j++) {
                recipeString.append(parseMaterial(materials.get(j)));

                if (j + 1 < choicesSize) {
                    recipeString.append(", ");
                }
            }
        } else {
            NamespacedKey key = tag.getKey();
            String namespace = key.getNamespace();

            recipeString.append("tag:");
            if (!namespace.equals(NamespacedKey.MINECRAFT)) {
                recipeString.append(namespace).append(":");
            }

            recipeString.append(key.getKey());
        }
    }

    private Tag<Material> getChoiceTagMatch(List<Material> choices, String tagType) {
        int choicesSize = choices.size();

        Iterable<Tag<Material>> blockTags = Bukkit.getTags(tagType, Material.class);
        for (Tag<Material> tag : blockTags) {
            Set<Material> materials = tag.getValues();

            if (choicesSize == materials.size()) {
                int numMatches = 0;
                for (Material material : choices) {
                    if (materials.contains(material)) {
                        numMatches++;
                    } else {
                        break;
                    }
                }

                if (numMatches == choicesSize) {
                    return tag;
                }
            }
        }

        return null;
    }

    private String parseMaterial(Material material) {
        return material.toString().toLowerCase();
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
                    recipeString.append(Files.NL).append("  @leathercolor ").append(color.getRed()).append(' ').append(color.getGreen()).append(' ').append(color.getBlue());
                }
            }
        }

        recipeString.append(Files.NL).append(Files.NL);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String currentInput = args[0].toLowerCase();
            if ("special".contains(currentInput)) {
                list.add("special");
            }
        }

        return list;
    }
}
