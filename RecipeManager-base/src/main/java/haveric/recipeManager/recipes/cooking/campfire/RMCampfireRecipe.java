package haveric.recipeManager.recipes.cooking.campfire;

import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.cooking.RMBaseCookingRecipe;
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;

public class RMCampfireRecipe extends RMBaseCookingRecipe {
    public RMCampfireRecipe() {
        minTime = Vanilla.CAMPFIRE_RECIPE_TIME;
    }

    public RMCampfireRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof RMCampfireRecipe) {
            RMCampfireRecipe r = (RMCampfireRecipe) recipe;

            hash = r.hash;
        }
    }

    public RMCampfireRecipe(Flags flags) {
        super(flags);

        minTime = Vanilla.CAMPFIRE_RECIPE_TIME;
    }

    public RMCampfireRecipe(CampfireRecipe recipe) {
        super(recipe);
    }

    @Override
    public boolean hasCustomTime() {
        return minTime != Vanilla.CAMPFIRE_RECIPE_TIME;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof CampfireRecipe && hash == obj.hashCode();
    }

    @Override
    public CampfireRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredientChoice() || !hasResult()) {
            return null;
        }

        Args a = ArgBuilder.create().result(getResult()).build();
        getFlags().sendPrepare(a, true);
        getResult().getFlags().sendPrepare(a, true);

        CampfireRecipe bukkitRecipe = new CampfireRecipe(getNamespacedKey(), a.result(), ingredientChoice, experience, getCookTicks());
        if (hasGroup()) {
            bukkitRecipe.setGroup(getGroup());
        }

        if (Supports.categories() && hasCategory()) {
            bukkitRecipe.setCategory(CookingBookCategory.valueOf(getCategory()));
        }

        return bukkitRecipe;
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.CAMPFIRE;
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult(getType().getDirective());

        String print = getConditionResultName(result);

        if (print.isEmpty()) {
            print = ToolsRecipeChoice.printRecipeChoice(ingredientChoice, RMCChatColor.RESET, RMCChatColor.BLACK);
        }

        s.append('\n').append(print);

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.cooktime")).append(RMCChatColor.BLACK);
        s.append('\n');

        if (hasCustomTime()) {
            if (maxTime > minTime) {
                s.append(Messages.getInstance().parse("recipebook.smelt.time.random", "{min}", RMCUtil.printNumber(minTime), "{max}", RMCUtil.printNumber(maxTime)));
            } else {
                if (minTime <= 0) {
                    s.append(Messages.getInstance().parse("recipebook.smelt.time.instant"));
                } else {
                    s.append(Messages.getInstance().parse("recipebook.smelt.time.fixed", "{time}", RMCUtil.printNumber(minTime)));
                }
            }
        } else {
            s.append(Messages.getInstance().parse("recipebook.smelt.time.normal", "{time}", RMCUtil.printNumber(minTime)));
        }

        return s.toString();
    }
}
