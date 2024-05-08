package haveric.recipeManager.recipes.cooking.furnace;

import com.google.common.base.Preconditions;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.cooking.RMBaseCookingRecipe;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;

public class RMBaseFurnaceRecipe extends RMBaseCookingRecipe {
    private ItemResult fuel;

    public RMBaseFurnaceRecipe() {
        minTime = Vanilla.FURNACE_RECIPE_TIME;
    }

    public RMBaseFurnaceRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof RMBaseFurnaceRecipe r) {
            if (r.ingredientChoice != null) {
                ingredientChoice = r.ingredientChoice.clone();
            }

            if (r.fuel == null) {
                fuel = null;
            } else {
                fuel = r.fuel.clone();
            }

            hash = r.hash;
        }
    }

    public RMBaseFurnaceRecipe(Flags flags) {
        super(flags);
    }

    public RMBaseFurnaceRecipe(CookingRecipe<?> recipe) {
        super(recipe);
    }

    public ItemResult getFuel() {
        return fuel;
    }

    public void setFuel(ItemResult newFuel) {
        Preconditions.checkNotNull(newFuel);

        fuel = newFuel.setRecipe(this);
    }

    public void setFuel(ItemStack newFuel) {
        Preconditions.checkNotNull(newFuel);

        fuel = new ItemResult(newFuel).setRecipe(this);
    }

    public boolean hasFuel() {
        return fuel != null;
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

        if (hasFuel()) {
            s.append("\n\n");
            s.append(Messages.getInstance().parse("recipebook.header.requirefuel"));
            s.append('\n').append(ToolsItem.print(fuel.getItemStack(), RMCChatColor.RESET, RMCChatColor.BLACK));
        }

        return s.toString();
    }
}
