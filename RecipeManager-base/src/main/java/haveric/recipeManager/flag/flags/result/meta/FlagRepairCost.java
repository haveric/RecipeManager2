package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionInteger;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class FlagRepairCost extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.REPAIR_COST;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <text or false> | prepareLore [message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Changes result's repair cost when repaired in an anvil.",
            "",
            "prepareLore [message] sets a lore message that will display when preparing the recipe.",
            "  Setting message to true will use the default message: flag.repaircost.preparelore",
            "  For the prepare lore you can use the following arguments:",
            "    {cost} = the repair cost",
            "  Allows quotes to prevent spaces being trimmed.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 1 // Sets the default repair cost to 1",
            "{flag} 25 // Sets the default repair cost to 25", };
    }

    private Integer cost = null;
    boolean useLoreMessage = false;
    String loreMessage = null;


    public FlagRepairCost() {
    }

    public FlagRepairCost(FlagRepairCost flag) {
        super(flag);
        cost = flag.cost;
        useLoreMessage = flag.useLoreMessage;
        loreMessage = flag.loreMessage;
    }

    @Override
    public FlagRepairCost clone() {
        return new FlagRepairCost((FlagRepairCost) super.clone());
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int newCost) {
        cost = newCost;
    }

    public boolean isUseLoreMessage() {
        return useLoreMessage;
    }

    public void setUseLoreMessage(boolean useLoreMessage) {
        this.useLoreMessage = useLoreMessage;
    }

    public String getLoreMessage() {
        return loreMessage;
    }

    public void setLoreMessage(String loreMessage) {
        this.loreMessage = loreMessage;
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return useLoreMessage;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof Repairable)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), Repairable.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a repairable result!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        // Match on single pipes '|', but not double '||'
        String[] args = value.split("(?<!\\|)\\|(?!\\|)");

        try {
            cost = Integer.parseInt(args[0].trim());
        } catch (NumberFormatException e) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid number for cost: " + value);
            return false;
        }

        if (args.length > 1) {
            String prepareLore = args[1].trim();
            if (prepareLore.toLowerCase().startsWith("preparelore")) {
                useLoreMessage = true;
                prepareLore = prepareLore.substring("preparelore".length()).trim();

                if (!prepareLore.equalsIgnoreCase("true")) {
                    // Replace double pipes with single pipe: || -> |
                    prepareLore = prepareLore.replaceAll("\\|\\|", "|");
                    prepareLore = RMCUtil.trimExactQuotes(prepareLore);

                    loreMessage = prepareLore;
                }
            } else {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid argument: " + args[1] + ".");
            }
        }

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();
            if (!(meta instanceof Repairable repairable) || cost == null) {
                return;
            }

            if (useLoreMessage) {
                addResultLore(a, Messages.getInstance().parseCustom("flag.repaircost.preparelore", loreMessage, "{cost}", cost));
            }

            repairable.setRepairCost(cost);

            a.result().setItemMeta(meta);
        }
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();
            if (!(meta instanceof Repairable repairable) || cost == null) {
                return;
            }

            repairable.setRepairCost(cost);

            a.result().setItemMeta(meta);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "cost: " + cost;
        toHash += "useLoreMessage: " + useLoreMessage;
        toHash += "loreMessage: " + loreMessage;

        return toHash.hashCode();
    }

    @Override
    public Condition parseCondition(String arg, boolean noMeta) {
        ConditionInteger returnCondition = new ConditionInteger(getConditionName(), getFlagType(), arg, noMeta);
        if (returnCondition.skipCondition()) {
            return null;
        }

        returnCondition.setCheckCallback((item, meta, condition) -> {
            ConditionInteger callbackCondition = (ConditionInteger) condition;
            boolean isRepariableMeta = meta instanceof Repairable;
            if (callbackCondition.shouldHaveNoMeta()) {
                return !isRepariableMeta || !((Repairable) meta).hasRepairCost();
            }

            if (isRepariableMeta &&  ((Repairable) meta).hasRepairCost()) {
                return !callbackCondition.hasValue() || callbackCondition.contains(((Repairable) meta).getRepairCost());
            }

            return false;
        });

        return returnCondition;
    }

    @Override
    public String getConditionName() {
        return "repaircost";
    }

    @Override
    public String[] getConditionDescription() {
        return new String[] {
            "  repaircost <number> = Ingredient must have a repair cost",
            "    <number> supports ranges: <min>-<max>",
            "    <number> supports multiple values that are comma separated: <number1>, <number2>, <number3>",
            "    <number> supports negative matching by preceding a number (or range) with an exclamation mark `!`: !<min>-<max>, !<number>",
            "    <number> any combination of the above can be combined together: <min>-<max>, <number1>, !<number2>, !<min>-<max>",
            "      Matching for <number> must match ANY of the non-negative values and NONE of the negative values",
            "  norepaircost or !repaircost = Ingredient must not have repair cost",
        };
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        parse(meta, recipeString, Files.NL + "@repaircost ");
    }

    @Override
    public void parseIngredientForConditions(ItemStack item, ItemMeta meta, StringBuilder ingredientCondition) {
        parse(meta, ingredientCondition, " | repaircost ");
    }

    private void parse(ItemMeta meta, StringBuilder builder, String prefix) {
        if (meta instanceof Repairable repairableMeta) {
            if (repairableMeta.hasRepairCost()) {
                builder.append(prefix).append(repairableMeta.getRepairCost());
            }
        }
    }
}
