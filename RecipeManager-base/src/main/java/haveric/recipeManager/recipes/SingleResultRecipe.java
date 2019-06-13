package haveric.recipeManager.recipes;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SingleResultRecipe extends BaseRecipe {
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
        Validate.notNull(newResult);

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
}
