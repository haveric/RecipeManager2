package haveric.recipeManager.recipes;

import com.google.common.base.Preconditions;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.flag.flags.any.FlagIngredientCondition;
import haveric.recipeManager.flag.flags.any.FlagItemName;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class SingleResultRecipe extends BaseRecipe {
    protected ItemResult result;

    protected SingleResultRecipe() {
    }

    public SingleResultRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof SingleResultRecipe) {
            SingleResultRecipe r = (SingleResultRecipe) recipe;

            result = r.getResult();
        }
    }

    public SingleResultRecipe(Flags flags) {
        super(flags);
    }

    /**
     * @return result as clone
     */
    public ItemResult getResult() {
        ItemResult itemResult = null;

        if (result != null) {
            itemResult = result.clone();
        }

        return itemResult;
    }

    /**
     * @param a
     * @return result as clone or null if failed by chance or failed by flag check
     */
    public ItemResult getResult(Args a) {
        if (result == null) {
            return null;
        }

        float rand = RecipeManager.random.nextFloat() * 100f;

        ItemResult r;
        if (result.getChance() >= rand) {
            r = result.clone();
        } else {
            r = new ItemResult(Material.AIR, 1, 0, (100 - result.getChance()));
        }

        a.setResult(r);

        if (r.getType() == Material.AIR && hasFlags()) {
            sendFailed(a);
            a.sendEffects(a.player(), Messages.getInstance().get("flag.prefix.recipe"));
        }

        return r;
    }

    public void setResult(ItemStack newResult) {
        Preconditions.checkNotNull(newResult);

        if (newResult instanceof ItemResult) {
            result = ((ItemResult) newResult).setRecipe(this);
        } else {
            result = new ItemResult(newResult).setRecipe(this);
        }
    }

    public boolean hasResult() {
        return result != null;
    }

    public String getResultString() {
        StringBuilder s = new StringBuilder();

        if (result != null) {
            if (result.getAmount() > 1) {
                s.append('x').append(result.getAmount()).append(' ');
            }

            s.append(result.getType().toString().toLowerCase());

            if (result.getDurability() != 0) {
                s.append(':').append(result.getDurability());
            }
        }

        return s.toString();
    }

    @Override
    public List<String> printBookIndices() {
        List<String> print = new ArrayList<>();

        if (hasCustomName()) {
            print.add(RMCChatColor.ITALIC + getName());
        } else {
            print.add(getResultPrintName(getResult()));
        }

        return print;
    }

    @Override
    public List<String> printBookRecipes() {
        List<String> recipes = new ArrayList<>();

        recipes.add(printBookResult(getResult()));

        return recipes;
    }

    protected StringBuilder getHeaderResult(String type) {
        StringBuilder s = new StringBuilder(256);

        s.append(Messages.getInstance().parse("recipebook.header." + type));

        if (hasCustomName()) {
            s.append('\n').append(RMCChatColor.BLACK).append(RMCChatColor.ITALIC).append(getName());
        }

        s.append('\n').append(RMCChatColor.GRAY).append('=');

        if (result.hasFlag(FlagType.ITEM_NAME)) {
            FlagItemName flag = (FlagItemName)result.getFlag(FlagType.ITEM_NAME);
            s.append(RMCChatColor.BLACK).append(RMCUtil.parseColors(flag.getPrintName(), false));
        } else {
            s.append(ToolsItem.print(getResult(), RMCChatColor.DARK_GREEN, null));
        }

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.ingredient")).append(RMCChatColor.BLACK);

        return s;
    }

    protected String getConditionResultName(ItemResult result) {
        String print = "";
        if (result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
            FlagIngredientCondition flag = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);
            List<ConditionsIngredient> conditions = flag.getIngredientConditions(result);

            if (conditions.size() > 0) {
                ConditionsIngredient condition = conditions.get(0);

                if (condition.hasName()) {
                    print = RMCChatColor.BLACK + condition.getName();
                } else if (condition.hasLore()) {
                    print = RMCChatColor.BLACK + "" + RMCChatColor.ITALIC + condition.getLores().get(0);
                }
            }
        }

        return print;
    }

    public boolean requiresRecipeManagerModification() {
        boolean requiresModification = false;

        if (result.getChance() < 100) {
            requiresModification = true;
        }

        if (!requiresModification) {
            for (Flag flag : getFlags().get()) {
                if (flag.requiresRecipeManagerModification()) {
                    requiresModification = true;
                    break;
                }
            }
        }

        if (!requiresModification) {
            for (Flag flag : result.getFlags().get()) {
                if (flag.requiresRecipeManagerModification()) {
                    requiresModification = true;
                    break;
                }
            }
        }

        return requiresModification;
    }
}
