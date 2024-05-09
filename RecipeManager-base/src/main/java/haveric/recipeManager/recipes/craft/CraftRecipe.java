package haveric.recipeManager.recipes.craft;

import com.google.common.collect.ImmutableList;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.PreparableResultRecipe;
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import haveric.recipeManager.tools.Version;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CraftRecipe extends PreparableResultRecipe {
    private String[] choicePattern;
    private String group;
    private String category;

    private int width;
    private int height;

    public CraftRecipe() {
        init();
    }

    public CraftRecipe(ShapedRecipe recipe) {
        init();
        setBukkitRecipe(recipe);
        setChoicePattern(recipe.getShape());
        setIngredientsMap(recipe.getChoiceMap());
        setResult(recipe.getResult());
    }

    public CraftRecipe(BaseRecipe recipe) {
        super(recipe);
        init();

        if (recipe instanceof CraftRecipe r) {
            choicePattern = r.choicePattern;
            group = r.group;
            category = r.category;

            width = r.width;
            height = r.height;
        }
    }

    public CraftRecipe(Flags flags) {
        super(flags);
        init();
    }

    private void init() {
        setMaxIngredients(9);
        addValidChars(ImmutableList.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'));
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.CRAFT;
    }

    public void setChoicePattern(String[] pattern) {
        choicePattern = pattern;

        width = pattern[0].length();
        height = pattern.length;
    }

    public String[] getChoicePattern() {
        return choicePattern;
    }

    public boolean hasGroup() {
        return group != null;
    }

    public void setGroup(String newGroup) {
        group = newGroup;
    }

    public String getGroup() {
        return group;
    }

    public boolean hasCategory() {
        return category != null;
    }

    public void setCategory(String newCategory) {
        category = newCategory;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public void updateHash() {
        StringBuilder str = new StringBuilder("craft ");
        int shapeSize = choicePattern.length;
        for (int i = 0; i < shapeSize; i++) {
            str.append(choicePattern[i]);

            if (i + 1 < shapeSize) {
                str.append(",");
            }
        }

        for (Entry<Character, RecipeChoice> entry : getIngredients().entrySet()) {
            str.append(" ").append(entry.getKey()).append(":");

            str.append(ToolsRecipeChoice.getRecipeChoiceHash(entry.getValue()));
        }

        hash = str.toString().hashCode();
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("craft ").append(width).append('x').append(height);

        s.append(" (");

        if (choicePattern != null) {
            int shapeSize = choicePattern.length;
            for (int i = 0; i < shapeSize; i++) {
                s.append(choicePattern[i]);

                if (i + 1 < shapeSize) {
                    s.append(",");
                }
            }
        }

        for (Entry<Character, RecipeChoice> entry : getIngredients().entrySet()) {
            s.append(" ").append(entry.getKey()).append(":");

            s.append(ToolsRecipeChoice.getRecipeChoiceName(entry.getValue()));
        }

        s.append(") to ");
        s.append(getResultsString());

        if (removed) {
            s.append(" [removed recipe]");
        }

        name = s.toString();
        customName = false;
    }

    /**
     * @return Shape width, 1 to 3
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return Shape height, 1 to 3
     */
    public int getHeight() {
        return height;
    }

    @Override
    public ShapedRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredients() || !hasResults()) {
            return null;
        }

        ShapedRecipe bukkitRecipe;
        if (vanilla) {
            bukkitRecipe = new ShapedRecipe(getNamespacedKey(), getFirstResultItemStack());
        } else {
            ItemResult firstResult = getFirstResult();

            Args a = ArgBuilder.create().result(firstResult).build();
            getFlags().sendPrepare(a, true);
            firstResult.getFlags().sendPrepare(a, true);

            ItemStack result;
            if (requiresRecipeManagerModification()) {
                result = Tools.createItemRecipeId(a.result().getItemStack(), hashCode());
            } else {
                result = a.result().getItemStack();
            }

            bukkitRecipe = new ShapedRecipe(getNamespacedKey(), result);
        }

        bukkitRecipe.shape(choicePattern);
        if (hasGroup()) {
            bukkitRecipe.setGroup(group);
        }

        if (Supports.categories() && hasCategory()) {
            bukkitRecipe.setCategory(CraftingBookCategory.valueOf(category));
        }

        for (Entry<Character, RecipeChoice> entry : getIngredients().entrySet()) {
            if (Version.has1_21Support()) {
                if (entry.getValue() == null) {
                    char key = entry.getKey();

                    String[] replacementPattern = new String[choicePattern.length];
                    for (int i = 0; i < choicePattern.length; i++) {
                        replacementPattern[i] = choicePattern[i].replace(key, ' ');
                    }

                    bukkitRecipe.shape(replacementPattern);
                } else {
                    bukkitRecipe.setIngredient(entry.getKey(), entry.getValue());
                }
            } else {
                bukkitRecipe.setIngredient(entry.getKey(), entry.getValue());
            }
        }

        return bukkitRecipe;
    }

    @Override
    public boolean isValid() {
        return hasIngredients() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResults());
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("craft", result);

        s.append(Messages.getInstance().parse("recipebook.header.pattern")).append('\n');
        s.append(RMCChatColor.GRAY);

        for (String pattern : choicePattern) {
            for (char letter : pattern.toCharArray()) {
                s.append('[');

                RecipeChoice choice = getIngredients().get(letter);
                if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                    List<Material> materials = materialChoice.getChoices();

                    if (materials.size() == 1 && materials.contains(Material.AIR)) {
                        s.append(RMCChatColor.WHITE).append('_');
                    } else {
                        s.append(RMCChatColor.DARK_PURPLE).append(letter);
                    }
                } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
                    List<ItemStack> items = exactChoice.getChoices();

                    if (items.size() == 1 && items.get(0).getType() == Material.AIR) {
                        s.append(RMCChatColor.WHITE).append('_');
                    } else {
                        s.append(RMCChatColor.DARK_PURPLE).append(letter);
                    }
                } else {
                    s.append(RMCChatColor.WHITE).append('_');
                }

                s.append(RMCChatColor.GRAY).append(']');
            }

            s.append('\n');
        }

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        for (Map.Entry<Character, RecipeChoice> entry : getIngredients().entrySet()) {
            RecipeChoice choice = entry.getValue();

            // Skip empty choices which might be air
            if (choice instanceof RecipeChoice.MaterialChoice || choice instanceof RecipeChoice.ExactChoice) {
                boolean isAir = false;
                if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                    List<Material> choices = materialChoice.getChoices();
                    if (choices.size() == 1 && choices.get(0) == Material.AIR) {
                        isAir = true;
                    }
                }

                if (!isAir) {
                    s.append('\n').append(RMCChatColor.DARK_PURPLE).append(entry.getKey()).append(RMCChatColor.GRAY).append(": ");
                    s.append(ToolsRecipeChoice.printRecipeChoice(entry.getValue(), RMCChatColor.BLACK, RMCChatColor.BLACK));
                }
            }
        }

        return s.toString();
    }

    @Override
    public int getIngredientMatchQuality(List<ItemStack> ingredients) {
        boolean checkExact = true;
        if (hasFlag(FlagType.INGREDIENT_CONDITION)) {
            checkExact = false;
        } else {
            for (ItemResult result : getResults()) {
                if (result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
                    checkExact = false;
                    break;
                }
            }
        }

        int totalQuality = 0;
        for (ItemStack ingredient : ingredients) {
            if (ingredient.getType() != Material.AIR) {
                int quality = 0;
                for (String pattern : choicePattern) {
                    for (Character c : pattern.toCharArray()) {
                        RecipeChoice ingredientChoice = getIngredients().get(c);
                        int newQuality = ToolsRecipeChoice.getIngredientMatchQuality(ingredient, ingredientChoice, checkExact);
                        if (newQuality > quality) {
                            quality = newQuality;
                            totalQuality += quality;
                        }
                    }
                }

                if (quality == 0) {
                    totalQuality = 0;
                    break;
                }
            }
        }

        return totalQuality;
    }
}
