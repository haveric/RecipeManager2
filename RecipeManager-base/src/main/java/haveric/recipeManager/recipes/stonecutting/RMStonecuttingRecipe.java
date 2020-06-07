package haveric.recipeManager.recipes.stonecutting;

import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.SingleRecipeChoiceSingleResultRecipe;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.inventory.StonecuttingRecipe;

public class RMStonecuttingRecipe extends SingleRecipeChoiceSingleResultRecipe {
    private String group;

    public RMStonecuttingRecipe() {

    }

    public RMStonecuttingRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof RMStonecuttingRecipe) {
            RMStonecuttingRecipe r = (RMStonecuttingRecipe) recipe;

            group = r.group;
            hash = r.hash;
        }
    }

    public RMStonecuttingRecipe(Flags flags) {
        super(flags);
    }

    public RMStonecuttingRecipe(StonecuttingRecipe recipe) {
        setIngredientChoice(recipe.getInputChoice());
        setResult(recipe.getResult());

        group = recipe.getGroup();
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

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof StonecuttingRecipe && hash == obj.hashCode();
    }

    @Override
    public StonecuttingRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredientChoice() || !hasResult()) {
            return null;
        }

        Args a = ArgBuilder.create().result(getResult()).build();
        getFlags().sendPrepare(a, true);
        getResult().getFlags().sendPrepare(a, true);

        StonecuttingRecipe bukkitRecipe = new StonecuttingRecipe(getNamespacedKey(), a.result(), ingredientChoice);
        if (hasGroup()) {
            bukkitRecipe.setGroup(group);
        }

        return bukkitRecipe;
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.STONECUTTING;
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult(getType().getDirective());

        String print = getConditionResultName(result);

        if (print.isEmpty()) {
            print = ToolsItem.printRecipeChoice(ingredientChoice, RMCChatColor.RESET, RMCChatColor.BLACK);
        }

        s.append('\n').append(print);

        return s.toString();
    }
}
