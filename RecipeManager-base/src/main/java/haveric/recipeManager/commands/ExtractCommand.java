package haveric.recipeManager.commands;

import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.compost.CompostRecipe;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.ToolsFlag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.inventory.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static haveric.recipeManager.Vanilla.isSpecialRecipe;
import static org.bukkit.Tag.REGISTRY_BLOCKS;
import static org.bukkit.Tag.REGISTRY_ITEMS;

public class ExtractCommand implements TabExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "extracted" + File.separator + "extracted recipes (" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + ").txt");

        if (file.exists()) {
            Messages.getInstance().send(sender, "cmd.extract.wait");
            return true;
        }

        boolean skipSpecial = true;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("special")) {
                skipSpecial = false;
            } else {
                Messages.getInstance().send(sender, "cmd.extract.unknownarg", "{arg}", arg);
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
        List<String> parsedSmithingRecipes = new ArrayList<>();

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
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.CRAFT.getDirective());

                    appendNamespacedKey(recipeString, recipe.getKey());

                    if (!recipe.getGroup().isEmpty()) {
                        recipeString.append("group ").append(recipe.getGroup()).append(Files.NL);
                    }

                    if (Supports.categories()) {
                        recipeString.append("category ").append(recipe.getCategory().name()).append(Files.NL);
                    }

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

                    parseResult(recipe.getResult(), recipeString);

                    parsedCraftRecipes.add(recipeString.toString());
                } else if (r instanceof ShapelessRecipe) {
                    ShapelessRecipe recipe = (ShapelessRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.COMBINE.getDirective());

                    appendNamespacedKey(recipeString, recipe.getKey());

                    if (!recipe.getGroup().isEmpty()) {
                        recipeString.append("group ").append(recipe.getGroup()).append(Files.NL);
                    }

                    if (Supports.categories()) {
                        recipeString.append("category ").append(recipe.getCategory().name()).append(Files.NL);
                    }

                    List<RecipeChoice> ingredientChoices = recipe.getChoiceList();

                    int size = ingredientChoices.size();
                    for (int i = 0; i < size; i++) {
                        parseChoice(ingredientChoices.get(i), recipeString);

                        if (i + 1 < size) {
                            recipeString.append(" + ");
                        }
                    }

                    recipeString.append(Files.NL);
                    parseResult(recipe.getResult(), recipeString);

                    parsedCombineRecipes.add(recipeString.toString());
                } else if (r instanceof FurnaceRecipe) {
                    FurnaceRecipe recipe = (FurnaceRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.SMELT.getDirective());

                    appendNamespacedKey(recipeString, recipe.getKey());

                    if (!recipe.getGroup().isEmpty()) {
                        recipeString.append("group ").append(recipe.getGroup()).append(Files.NL);
                    }

                    if (Supports.categories()) {
                        recipeString.append("category ").append(recipe.getCategory().name()).append(Files.NL);
                    }

                    recipeString.append("xp ").append(recipe.getExperience()).append(Files.NL);

                    parseChoice(recipe.getInputChoice(), recipeString);

                    recipeString.append(Files.NL);
                    parseResult(recipe.getResult(), recipeString);

                    parsedSmeltRecipes.add(recipeString.toString());
                } else if (r instanceof BlastingRecipe) {
                    BlastingRecipe recipe = (BlastingRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.BLASTING.getDirective());

                    appendNamespacedKey(recipeString, recipe.getKey());

                    if (!recipe.getGroup().isEmpty()) {
                        recipeString.append("group ").append(recipe.getGroup()).append(Files.NL);
                    }

                    if (Supports.categories()) {
                        recipeString.append("category ").append(recipe.getCategory().name()).append(Files.NL);
                    }

                    recipeString.append("xp ").append(recipe.getExperience()).append(Files.NL);

                    parseChoice(recipe.getInputChoice(), recipeString);

                    recipeString.append(Files.NL);
                    parseResult(recipe.getResult(), recipeString);

                    parsedBlastingRecipes.add(recipeString.toString());
                } else if (r instanceof SmokingRecipe) {
                    SmokingRecipe recipe = (SmokingRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.SMOKING.getDirective());

                    appendNamespacedKey(recipeString, recipe.getKey());

                    if (!recipe.getGroup().isEmpty()) {
                        recipeString.append("group ").append(recipe.getGroup()).append(Files.NL);
                    }

                    if (Supports.categories()) {
                        recipeString.append("category ").append(recipe.getCategory().name()).append(Files.NL);
                    }

                    recipeString.append("xp ").append(recipe.getExperience()).append(Files.NL);

                    parseChoice(recipe.getInputChoice(), recipeString);

                    recipeString.append(Files.NL);
                    parseResult(recipe.getResult(), recipeString);

                    parsedSmokingRecipes.add(recipeString.toString());
                } else if (r instanceof CampfireRecipe) {
                    CampfireRecipe recipe = (CampfireRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.CAMPFIRE.getDirective());

                    appendNamespacedKey(recipeString, recipe.getKey());

                    if (!recipe.getGroup().isEmpty()) {
                        recipeString.append("group ").append(recipe.getGroup()).append(Files.NL);
                    }

                    if (Supports.categories()) {
                        recipeString.append("category ").append(recipe.getCategory().name()).append(Files.NL);
                    }

                    recipeString.append("xp ").append(recipe.getExperience()).append(Files.NL);

                    parseChoice(recipe.getInputChoice(), recipeString);

                    recipeString.append(Files.NL);
                    parseResult(recipe.getResult(), recipeString);

                    parsedCampfireRecipes.add(recipeString.toString());
                } else if (r instanceof StonecuttingRecipe) {
                    StonecuttingRecipe recipe = (StonecuttingRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.STONECUTTING.getDirective());

                    appendNamespacedKey(recipeString, recipe.getKey());

                    if (!recipe.getGroup().isEmpty()) {
                        recipeString.append("group ").append(recipe.getGroup()).append(Files.NL);
                    }

                    parseChoice(recipe.getInputChoice(), recipeString);

                    recipeString.append(Files.NL);
                    parseResult(recipe.getResult(), recipeString);

                    parsedStonecuttingRecipes.add(recipeString.toString());
                } else if (r instanceof SmithingRecipe) {
                    SmithingRecipe recipe = (SmithingRecipe) r;
                    StringBuilder recipeString = new StringBuilder(RMCRecipeType.SMITHING.getDirective());
                    appendNamespacedKey(recipeString, recipe.getKey());

                    if (Supports.experimental1_20()) {
                        if (r instanceof SmithingTransformRecipe) {
                            SmithingTransformRecipe transformRecipe = (SmithingTransformRecipe) r;

                            parseChoice(transformRecipe.getTemplate(), recipeString);
                            recipeString.append(" + ");
                        }
                    }

                    parseChoice(recipe.getBase(), recipeString);
                    recipeString.append(" + ");
                    parseChoice(recipe.getAddition(), recipeString);

                    recipeString.append(Files.NL);
                    parseResult(recipe.getResult(), recipeString);

                    parsedSmithingRecipes.add(recipeString.toString());
                }

                recipesNum++;
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            } catch (NoSuchElementException e) {
                // Vanilla datapack is disabled
            }
        }

        Map<BaseRecipe, RMCRecipeInfo> initialRecipes = Vanilla.getInitialRecipes();

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : initialRecipes.entrySet()) {
            BaseRecipe recipe = entry.getKey();

            if (recipe instanceof FuelRecipe) {
                FuelRecipe fuelRecipe = (FuelRecipe) recipe;
                StringBuilder recipeString = new StringBuilder(RMCRecipeType.FUEL.getDirective()).append(Files.NL);

                parseChoice(fuelRecipe.getIngredientChoice(), recipeString);
                recipeString.append(" % ").append(fuelRecipe.getMinTime());

                recipeString.append(Files.NL).append(Files.NL);

                parsedFuelRecipes.add(recipeString.toString());
            } else if (recipe instanceof CompostRecipe) {
                CompostRecipe compostRecipe = (CompostRecipe) recipe;
                StringBuilder recipeString = new StringBuilder(RMCRecipeType.COMPOST.getDirective()).append(Files.NL);

                parseChoice(compostRecipe.getIngredientChoice(), recipeString);

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
                writeRecipes(stream, parsedBlastingRecipes, "Blasting");
                writeRecipes(stream, parsedSmokingRecipes, "Smoking");
                writeRecipes(stream, parsedCampfireRecipes, "Campfire");
                writeRecipes(stream, parsedStonecuttingRecipes, "Stonecutting");
                writeRecipes(stream, parsedCompostRecipes, "Compost");
                writeRecipes(stream, parsedSmithingRecipes, "Smithing");
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
        if (tag == null || tag.getValues().isEmpty()) {
            tag = getChoiceTagMatch(materials, REGISTRY_ITEMS);
        }

        if (tag == null || tag.getValues().isEmpty()) {
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

        ToolsFlag.parseItemMeta(result, recipeString);

        recipeString.append(Files.NL);
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

    private void appendNamespacedKey(StringBuilder recipeString, NamespacedKey namespacedKey) {
        recipeString.append(" # ").append(namespacedKey.getNamespace()).append(":").append(namespacedKey.getKey()).append(Files.NL);
    }
}
