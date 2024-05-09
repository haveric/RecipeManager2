package haveric.recipeManager.recipes;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.flag.flags.any.FlagIngredientCondition;
import haveric.recipeManager.flag.flags.recipe.FlagDisplayResult;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class PreparableResultRecipe extends MultiChoiceResultRecipe {
    protected PreparableResultRecipe() {
    }

    public PreparableResultRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public PreparableResultRecipe(Flags flags) {
        super(flags);
    }

    /**
     * Generate a display result for showing off all results (if available).
     *
     * @param a
     * @return the result if it's only one or a special multi-result information item
     */
    public ItemResult getDisplayResult(Args a) {
        a.clear();

        int displayAmount = 1;
        if (!checkFlags(a)) {
            List<String> reasons = new ArrayList<>();
            for (String reason : a.reasons()) {
                reasons.add(RMCUtil.parseColors(reason, false));
            }

            return ToolsItem.create(RecipeManager.getSettings().getFailMaterial(), 0, displayAmount, Messages.getInstance().parse("craft.result.denied.title"), reasons);
        }

        boolean recipeHasSecret = false;
        if (hasFlag(FlagType.SECRET)) {
            recipeHasSecret = true;
        }

        List<ItemResult> displayResults = new ArrayList<>();
        float failChance = 0;
        int secretNum = 0;
        float secretChance = 0;
        int unavailableNum = 0;
        float unavailableChance = 0;
        int displayNum;
        List<String> unavailableReasons = new ArrayList<>();

        List<ItemResult> itemResults = getResults();
        for (ItemResult r : itemResults) {
            r = r.clone();
            a.clearReasons();
            a.setResult(r);

            if (r.checkFlags(a)) {
                if (recipeHasSecret || r.hasFlag(FlagType.SECRET)) {
                    secretNum++;
                    secretChance += r.getChance();
                } else if (r.isAir()) {
                    failChance = r.getChance();
                } else {
                    displayResults.add(r);

                    a.sendEffects(a.player(), Messages.getInstance().parse("flag.prefix.result", "{item}", ToolsItem.print(r.getItemStack())));
                }
            } else {
                unavailableNum++;
                unavailableChance += r.getChance();

                for (String reason : a.reasons()) {
                    unavailableReasons.add(RMCUtil.parseColors(reason, false));
                }
            }
        }

        displayNum = displayResults.size();
        boolean receive = (secretNum + displayNum) > 0;

        FlagDisplayResult displayResultFlag;
        if (a.hasRecipe()) {
            displayResultFlag = (FlagDisplayResult) a.recipe().getFlag(FlagType.DISPLAY_RESULT);
        } else {
            displayResultFlag = null;
        }

        ItemResult displayResult = null;
        if (displayResultFlag == null) {
            if (displayNum > 0) {
                displayResult = displayResults.get(0);
            } else if (secretNum > 0 && secretNum == itemResults.size()) {
                return ToolsItem.create(RecipeManager.getSettings().getSecretMaterial(), 0, displayAmount, Messages.getInstance().parse("craft.result.receive.title.unknown"));
            } else if (unavailableNum > 0) {
                for (String reason : unavailableReasons) {
                    a.addCustomReason(reason);
                }
                return ToolsItem.create(RecipeManager.getSettings().getFailMaterial(), 0, displayAmount, Messages.getInstance().parse("craft.result.denied.title"), unavailableReasons);
            } else {
                ItemStack air = new ItemStack(Material.AIR);
                return new ItemResult(air);
            }
        } else {
            if (!receive && displayResultFlag.isSilentFail()) {
                return null;
            }

            ItemStack display = displayResultFlag.getDisplayItem();

            if (display != null) {
                displayResult = new ItemResult(display);
            } else if (displayNum > 0) {
                displayResult = displayResults.get(0);
            }
        }

        if (displayResult == null) {
            ItemStack air = new ItemStack(Material.AIR);
            return new ItemResult(air);
        }

        if (displayResultFlag != null || this.hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
            return displayResult;
        }


        if (unavailableNum == 0) {
            if (displayNum == 1 && secretNum == 0) {
                return displayResult;
            } else if (secretNum > 0 && secretNum == itemResults.size()) {
                return ToolsItem.create(RecipeManager.getSettings().getSecretMaterial(), 0, displayAmount, Messages.getInstance().parse("craft.result.receive.title.unknown"));
            }

        }/* else if (displayNum == 1) {
            return ToolsItem.create(displayResult.getType(), 0, displayAmount, displayResult.getItemMeta().getDisplayName());
        }*/

        List<String> lore = new ArrayList<>();
        String title;
        if (receive) {
            title = Messages.getInstance().parse("craft.result.receive.title.random");
        } else {
            title = Messages.getInstance().parse("craft.result.noreceive.title");
            lore.add(Messages.getInstance().parse("craft.result.denied.info"));
        }

        for (ItemResult r : displayResults) {
            String cloneMessage = "";
            if (r.hasFlag(FlagType.CLONE_INGREDIENT)) {
                cloneMessage = Messages.getInstance().get("flag.clone.resultdisplay");
            }
            lore.add(Messages.getInstance().parse("craft.result.list.item", "{chance}", formatChance(r.getChance()), "{item}", ToolsItem.print(r.getItemStack()), "{clone}", cloneMessage));
        }

        if (failChance > 0) {
            lore.add(Messages.getInstance().parse("craft.result.list.failure", "{chance}", formatChance(failChance)));
        }

        if (secretNum > 0) {
            String message;
            if (secretNum == 1) {
                message = "craft.result.list.secret";
            } else {
                message = "craft.result.list.secrets";
            }
            lore.add(Messages.getInstance().parse(message, "{chance}", formatChance(secretChance), "{num}", String.valueOf(secretNum)));
        }

        if (unavailableNum > 0) {
            String message;
            if (unavailableNum == 1) {
                message = "craft.result.list.unavailable";
            } else {
                message = "craft.result.list.unavailables";
            }
            lore.add(Messages.getInstance().parse(message, "{chance}", formatChance(unavailableChance), "{num}", String.valueOf(unavailableNum)));
        }

        Material displayMaterial;
        if (receive) {
            displayMaterial = RecipeManager.getSettings().getMultipleResultsMaterial();
        } else {
            displayMaterial = RecipeManager.getSettings().getFailMaterial();
        }

        return ToolsItem.create(displayMaterial, 0, displayAmount, title, lore);
    }

    private String formatChance(float chance) {
        String formatString;

        if (chance == 100) {
            formatString = "100%";
        } else if (Math.round(chance) == chance) {
            formatString = String.format("%4.0f%%", chance);
        } else {
            formatString = String.format("%4.1f%%", chance);
        }

        return formatString;
    }

    public void subtractIngredients(Inventory inv, ItemResult result, boolean onlyExtra) {
        // Set defaults to non-crafting inventories where the index has results being last (Ex: anvils)
        int startIndex = 0;
        int endIndex = inv.getSize() - 1;
        if (inv instanceof CraftingInventory) {
            startIndex = 1;
            endIndex = inv.getSize();
        }

        for (int i = startIndex; i < endIndex; i++) {
            ItemStack item = inv.getItem(i);

            if (item != null) {
                int amt = item.getAmount();
                int newAmt = amt;

                if (hasFlag(FlagType.INGREDIENT_CONDITION)) {
                    FlagIngredientCondition flagIC = (FlagIngredientCondition) getFlag(FlagType.INGREDIENT_CONDITION);
                    List<ConditionsIngredient> condList = flagIC.getIngredientConditions(item);

                    for (ConditionsIngredient cond : condList) {
                        if (cond != null && cond.checkIngredient(item, ArgBuilder.create().build())) {
                            if (cond.getAmount() > 1) {
                                newAmt -= (cond.getAmount() - 1);
                            }
                        }
                    }
                }

                if (result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
                    FlagIngredientCondition flagIC = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);
                    List<ConditionsIngredient> condList = flagIC.getIngredientConditions(item);

                    for (ConditionsIngredient cond : condList) {
                        if (cond != null && cond.checkIngredient(item, ArgBuilder.create().build())) {
                            if (cond.getAmount() > 1) {
                                newAmt -= (cond.getAmount() - 1);
                            }
                        }
                    }
                }

                if (!onlyExtra) {
                    newAmt -= 1;
                }

                if (amt != newAmt) {
                    if (newAmt > 0) {
                        item.setAmount(newAmt);
                    } else {
                        inv.clear(i);
                    }
                }
            }
        }
    }
}
