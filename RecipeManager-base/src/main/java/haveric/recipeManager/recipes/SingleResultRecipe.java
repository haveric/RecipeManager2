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
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionString;
import haveric.recipeManager.flag.flags.any.FlagIngredientCondition;
import haveric.recipeManager.flag.flags.any.meta.FlagDisplayName;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SingleResultRecipe extends BaseRecipe {
    protected ItemResult result;

    protected SingleResultRecipe() {
    }

    public SingleResultRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof SingleResultRecipe r) {
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

        if (r.isAir() && hasFlags()) {
            sendFailed(a);
            a.sendEffects(a.player(), Messages.getInstance().get("flag.prefix.recipe"));
        }

        return r;
    }

    public void setResult(ItemResult newResult) {
        Preconditions.checkNotNull(newResult);

        result = newResult.setRecipe(this);
    }

    public void setResult(ItemStack newResult) {
        Preconditions.checkNotNull(newResult);

        result = new ItemResult(newResult).setRecipe(this);
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

        if (result.hasFlag(FlagType.DISPLAY_NAME)) {
            FlagDisplayName flag = (FlagDisplayName)result.getFlag(FlagType.DISPLAY_NAME);
            s.append(RMCChatColor.BLACK).append(RMCUtil.parseColors(flag.getPrintName(), false));
        } else {
            s.append(ToolsItem.print(getResult().getItemStack(), RMCChatColor.DARK_GREEN, null));
        }

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.ingredient")).append(RMCChatColor.BLACK);

        return s;
    }

    protected String getConditionResultName(ItemResult result) {
        String print = "";
        if (result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
            FlagIngredientCondition flag = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);
            List<ConditionsIngredient> conditionsList = flag.getIngredientConditions(result.getItemStack());

            if (!conditionsList.isEmpty()) {
                ConditionsIngredient condition = conditionsList.get(0);

                Map<String, Condition> conditions = condition.getConditions();
                if (conditions.containsKey("name")) {
                    ConditionString conditionString = (ConditionString) conditions.get("name");
                    print = RMCChatColor.BLACK + conditionString.getValuesString();
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
