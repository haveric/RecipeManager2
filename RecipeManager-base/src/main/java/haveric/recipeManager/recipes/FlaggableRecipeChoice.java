package haveric.recipeManager.recipes;

import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.Flaggable;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.inventory.RecipeChoice;

public class FlaggableRecipeChoice implements Flaggable {
    private RecipeChoice choice;
    private Flags flags;
    private BaseRecipe recipe;

    public FlaggableRecipeChoice() {
    }

    public FlaggableRecipeChoice(FlaggableRecipeChoice flaggable) {

        if (flaggable.hasFlags()) {
            flags = flaggable.getFlags().clone(this);
        } else {
            flags = null;
        }

        choice = flaggable.choice;
        recipe = flaggable.recipe; // don't clone, needs to be a pointer
    }

    @Override
    public FlaggableRecipeChoice clone() {
        return new FlaggableRecipeChoice(this);
    }

    public RecipeChoice getChoice() {
        return choice;
    }

    public void setChoice(RecipeChoice choice) {
        this.choice = choice;
    }

    public BaseRecipe getRecipe() {
        return recipe;
    }

    public FlaggableRecipeChoice setRecipe(BaseRecipe newRecipe) {
        recipe = newRecipe;
        return this;
    }

    // From Flaggable interface

    public boolean hasFlag(String type) {
        boolean hasFlag = false;

        if (flags != null) {
            hasFlag = flags.hasFlag(type);
        }

        return hasFlag;
    }

    public boolean hasFlags() {
        return flags != null;
    }

    public Flag getFlag(String type) {
        return flags.getFlag(type);
    }

    public Flags getFlags() {
        if (flags == null) {
            flags = new Flags(this);
        }

        return flags;
    }

    public void clearFlags() {
        flags = null;
    }

    public void addFlag(Flag flag) {
        flags.addFlag(flag, FlagBit.RESULT);
    }

    public boolean checkFlags(Args a) {
        boolean checkFlags = true;

        if (flags != null) {
            checkFlags = flags.checkFlags(a);
        }

        return checkFlags;
    }

    public boolean sendCrafted(Args a) {
        boolean sendCrafted = true;

        if (flags != null) {
            sendCrafted = flags.sendCrafted(a);
        }

        return sendCrafted;
    }

    public boolean sendPrepare(Args a) {
        boolean sendPrepare = true;

        if (flags != null) {
            sendPrepare = flags.sendPrepare(a);
        }

        return sendPrepare;
    }

    @Override
    public boolean sendFuelRandom(Args a) {
        boolean sendRandom = true;

        if (flags != null) {
            sendRandom = flags.sendFuelRandom(a);
        }

        return sendRandom;
    }

    @Override
    public boolean sendFuelEnd(Args a) {
        boolean sendEnd = true;

        if (flags != null) {
            sendEnd = flags.sendFuelEnd(a);
        }

        return sendEnd;
    }

    @Override
    public int hashCode() {
        String toHash = String.valueOf(super.hashCode()); // Get ItemStack's hash

        if (hasFlags() && flags.hasFlags()) {
            toHash += flags.hashCode();
        }

        return toHash.hashCode();
    }
}
