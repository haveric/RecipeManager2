package haveric.recipeManager.recipes;

import haveric.recipeManager.Settings;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.flag.flags.any.FlagIngredientCondition;
import haveric.recipeManager.flag.flags.recipe.FlagDisplayResult;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WorkbenchRecipe extends MultiResultRecipe {
    protected WorkbenchRecipe() {
    }

    public WorkbenchRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public WorkbenchRecipe(Flags flags) {
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

        int displayAmount = 0;

        // Just to maintain previous functionality
        if (Version.has1_11Support()) {
            displayAmount = 1;
        }

        if (!checkFlags(a)) {
            List<String> reasons = new ArrayList<>();
            for (String reason : a.reasons()) {
                reasons.add(RMCUtil.parseColors(reason, false));
            }

            return ToolsItem.create(Settings.getInstance().getFailMaterial(), 0, displayAmount, Messages.getInstance().parse("craft.result.denied.title"), reasons);
        }

        List<ItemResult> displayResults = new ArrayList<>();
        float failChance = 0;
        int secretNum = 0;
        float secretChance = 0;
        int unavailableNum = 0;
        float unavailableChance = 0;
        int displayNum;

        for (ItemResult r : getResults()) {
            r = r.clone();
            a.clearReasons();
            a.setResult(r);

            if (r.checkFlags(a)) {
                if (r.hasFlag(FlagType.SECRET)) {
                    secretNum++;
                    secretChance += r.getChance();
                } else if (r.getType() == Material.AIR) {
                    failChance = r.getChance();
                } else {
                    displayResults.add(r);

                    a.sendEffects(a.player(), Messages.getInstance().parse("flag.prefix.result", "{item}", ToolsItem.print(r)));
                }
            } else {
                unavailableNum++;
                unavailableChance += r.getChance();
            }
        }

        displayNum = displayResults.size();
        boolean receive = (secretNum + displayNum) > 0;

        FlagDisplayResult flag;
        if (a.hasRecipe()) {
            flag = (FlagDisplayResult) a.recipe().getFlag(FlagType.DISPLAY_RESULT);
        } else {
            flag = null;
        }

        ItemResult displayResult = null;
        if (flag == null) {
            if (displayNum > 0) {
                displayResult = displayResults.get(0);
            } else {
                ItemStack air = new ItemStack(Material.AIR);
                return new ItemResult(air);
            }
        } else {
            if (!receive && flag.isSilentFail()) {
                return null;
            }

            ItemStack display = flag.getDisplayItem();

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

        if (flag != null || this.hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
            return displayResult;
        }

        if (unavailableNum == 0) {
            if (displayNum == 1 && secretNum == 0) {
                return displayResult;
            }/* else if (secretNum == 1 && displayNum == 0) { // TODO: Potential bug here
                return ToolsItem.create(Settings.getInstance().getSecretMaterial(), 0, displayAmount, Messages.getInstance().get("craft.result.receive.title.unknown"));
            }*/

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
            lore.add(Messages.getInstance().parse("craft.result.list.item", "{chance}", formatChance(r.getChance()), "{item}", ToolsItem.print(r), "{clone}", cloneMessage));
        }

        if (failChance > 0) {
            lore.add(Messages.getInstance().parse("craft.result.list.failure", "{chance}", formatChance(failChance)));
        }

        if (secretNum > 0) {
            lore.add(Messages.getInstance().parse("craft.result.list.secrets", "{chance}", formatChance(secretChance), "{num}", String.valueOf(secretNum)));
        }

        if (unavailableNum > 0) {
            lore.add(Messages.getInstance().parse("craft.result.list.unavailable", "{chance}", formatChance(unavailableChance), "{num}", String.valueOf(unavailableNum)));
        }

        Material displayMaterial;
        if (receive) {
            displayMaterial = Settings.getInstance().getMultipleResultsMaterial();
        } else {
            displayMaterial = Settings.getInstance().getFailMaterial();
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

    public void subtractIngredients(CraftingInventory inv, ItemResult result, boolean onlyExtra) {
        FlagIngredientCondition flagIC;
        if (hasFlag(FlagType.INGREDIENT_CONDITION)) {
            flagIC = (FlagIngredientCondition) getFlag(FlagType.INGREDIENT_CONDITION);
        } else {
            flagIC = null;
        }

        if (flagIC == null && result != null && result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
            flagIC = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);
        }

        for (int i = 1; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);

            if (item != null) {
                int amt = item.getAmount();
                int newAmt = amt;

                if (flagIC != null) {
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
