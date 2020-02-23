package haveric.recipeManager.recipes;

import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.flags.any.FlagItemName;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.util.RMCUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;

public class MultiResultRecipe extends BaseRecipe {
    private List<ItemResult> results = new ArrayList<>();

    protected MultiResultRecipe() {
    }

    public MultiResultRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof MultiResultRecipe) {
            MultiResultRecipe r = (MultiResultRecipe) recipe;

            results = new ArrayList<>(r.getResults().size());

            for (ItemResult i : r.getResults()) {
                results.add(i.clone());
            }
        }
    }

    public MultiResultRecipe(Flags flags) {
        super(flags);
    }

    public boolean hasResults() {
        return !results.isEmpty();
    }

    /**
     * @return results list, never null.
     */
    public List<ItemResult> getResults() {
        return results;
    }

    /**
     * @param newResults
     *            the results list or null if you want to clear results
     */
    public void setResults(List<ItemResult> newResults) {
        if (newResults == null) {
            results.clear();
            return;
        }

        results = newResults;

        for (ItemResult r : results) {
            r.setRecipe(this);
        }
    }

    /**
     * Removes all other results and add the specified result.
     *
     * @param result
     */
    public void setResult(ItemStack result) {
        results.clear();
        addResult(result);
    }

    /**
     * Adds the specified result to the list.
     *
     * @param result
     *            result item, must not be null.
     */
    public void addResult(ItemStack result) {
        Validate.notNull(result, "The 'result' argument must not be null!");

        if (result instanceof ItemResult) {
            results.add(((ItemResult) result).setRecipe(this));
        } else {
            results.add(new ItemResult(result).setRecipe(this));
        }
    }

    public String getResultsString() {
        StringBuilder s = new StringBuilder();

        int resultNum = getResults().size();

        if (resultNum > 0) {
            ItemStack result = getFirstResult();

            if (result == null) {
                s.append("nothing");
            } else {
                if (result.getAmount() > 1) {
                    s.append('x').append(result.getAmount()).append(' ');
                }

                s.append(result.getType().toString().toLowerCase());

                if (!Version.has1_13BasicSupport() || result instanceof Damageable) {
                    if (result.getDurability() != 0) {
                        s.append(':').append(result.getDurability());
                    }
                }

                if (resultNum > 1) {
                    s.append(" +").append(resultNum - 1).append(" more");
                }
            }
        } else {
            s.append("no result");
        }

        return s.toString();
    }

    /**
     * @return true if recipe has more than 1 result or has failure chance (2 results, one being air), otherwise false.
     */
    public boolean isMultiResult() {
        return results.size() > 1;
    }

    /**
     * @return failure chance or 0 if it can not fail.
     */
    public float getFailChance() {
        for (ItemResult r : results) {
            if (r.getType() == Material.AIR) {
                return r.getChance();
            }
        }

        return 0;
    }

    /**
     * @return the first valid result as a clone or null.
     */
    public ItemResult getFirstResult() {
        for (ItemResult r : results) {
            if (r.getType() != Material.AIR) {
                return r.clone();
            }
        }

        return null; // no valid results defined
    }

    public boolean hasValidResult() {
        boolean valid = false;

        for (ItemResult r : results) {
            if (r.getType() != Material.AIR) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    @Override
    public List<String> printBookIndices() {
        List<String> print = new ArrayList<>();

        if (hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
            for (ItemResult result : getResults()) {
                print.add(getResultPrintName(result));
            }
        } else {
            print.add(getResultPrintName(getFirstResult()));
        }

        return print;
    }

    @Override
    public List<String> printBookRecipes() {
        List<String> recipes = new ArrayList<>();

        if (hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
            for (ItemResult result : getResults()) {
                recipes.add(printBookResult(result));
            }
        } else {
            recipes.add(printBookResult(getFirstResult()));
        }

        return recipes;
    }

    protected StringBuilder getHeaderResult(String type, ItemResult result) {
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
            s.append(ToolsItem.print(getFirstResult(), RMCChatColor.DARK_GREEN, null));
        }

        if (isMultiResult() && !hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
            s.append('\n').append(Messages.getInstance().parse("recipebook.moreresults", "{amount}", (getResults().size() - 1)));
        }

        s.append("\n\n");

        return s;
    }
}
