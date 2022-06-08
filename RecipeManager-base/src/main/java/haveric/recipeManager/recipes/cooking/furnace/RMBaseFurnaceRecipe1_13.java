package haveric.recipeManager.recipes.cooking.furnace;

import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.cooking.RMBaseCookingRecipe;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

public class RMBaseFurnaceRecipe1_13 extends RMBaseCookingRecipe {
    private ItemResult fuel;

    public RMBaseFurnaceRecipe1_13() {
        minTime = Vanilla.FURNACE_RECIPE_TIME;
    }

    public RMBaseFurnaceRecipe1_13(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof RMBaseFurnaceRecipe1_13) {
            RMBaseFurnaceRecipe1_13 r = (RMBaseFurnaceRecipe1_13) recipe;

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

    public RMBaseFurnaceRecipe1_13(Flags flags) {
        super(flags);
    }

    // Legacy constructor for 1.13 / 1.12
    public RMBaseFurnaceRecipe1_13(FurnaceRecipe recipe) {
        setIngredientChoice(recipe.getInputChoice());
        setResult(recipe.getResult());
    }

    // Constructor for 1.14 +
    public RMBaseFurnaceRecipe1_13(CookingRecipe<?> recipe) {
        super(recipe);
    }

    public ItemResult getFuel() {
        return fuel;
    }

    public void setFuel(ItemStack newFuel) {
        Validate.notNull(newFuel);

        if (newFuel instanceof ItemResult) {
            fuel = ((ItemResult) newFuel).setRecipe(this);
        } else {
            fuel = new ItemResult(newFuel).setRecipe(this);
        }
    }

    public String getFuelIndex() {
        String fuelIndex = "" + fuel.getType();

        if (fuel.getDurability() != RMCVanilla.DATA_WILDCARD) {
            fuelIndex += ":" + fuel.getDurability();
        }

        return fuelIndex;
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
            s.append('\n').append(ToolsItem.print(fuel, RMCChatColor.RESET, RMCChatColor.BLACK));
        }

        return s.toString();
    }
}
