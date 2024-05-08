package haveric.recipeManager.flag.conditions.condition;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ConditionInteger extends Condition {
    private boolean matchesName;
    private boolean noCondition;
    private final String flagType;
    private final boolean noMeta;
    List<ConditionIntegerRange> values;
    List<ConditionIntegerRange> negativeValues;

    public ConditionInteger(String name, String flagType, String arg, boolean noMeta) {
        super(name);
        this.noMeta = noMeta;
        this.flagType = flagType;

        parseArg(arg.toLowerCase(), flagType);
    }

    public boolean skipCondition() {
        return !matchesName || (!noMeta && noCondition);
    }

    public boolean shouldHaveNoMeta() {
        return noMeta || noCondition;
    }

    private void parseArg(String argLower, String flagType) {
        values = new ArrayList<>();
        negativeValues = new ArrayList<>();
        noCondition = false;
        matchesName = false;

        if (argLower.startsWith("!" + name) || argLower.startsWith("no" + name)) {
            noCondition = true;
            matchesName = true;
        } else if (argLower.startsWith(name)) {
            matchesName = true;
            String argTrimmed = argLower.substring(name.length()).trim();
            String[] args = argTrimmed.split(",");
            for (String arg : args) {
                boolean negative = false;
                String valTrimmed = arg.trim();
                if (valTrimmed.startsWith("!")) {
                    valTrimmed = valTrimmed.substring(1);
                    negative = true;
                }

                String[] minMax = valTrimmed.split("-");

                if (minMax.length > 1) {
                    int min;
                    int max;

                    try {
                        min = Integer.parseInt(minMax[0]);
                        max = Integer.parseInt(minMax[1]);
                        if (min > max) {
                            ErrorReporter.getInstance().warning("Flag " + flagType + " has '" + name + "' argument with invalid number range: " + min + " to " + max);
                            break;
                        } else {
                            if (negative) {
                                negativeValues.add(new ConditionIntegerRange(min, max));
                            } else {
                                values.add(new ConditionIntegerRange(min, max));
                            }
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + flagType + " has '" + name + "' argument with invalid number: " + valTrimmed);
                    }
                } else {
                    try {
                        int value = Integer.parseInt(valTrimmed);
                        if (negative) {
                            negativeValues.add(new ConditionIntegerRange(value));
                        } else {
                            values.add(new ConditionIntegerRange(value));
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + flagType + " has '" + name + "' argument with invalid number: " + valTrimmed);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasValue() {
        return (!values.isEmpty() || !negativeValues.isEmpty()) && !noCondition;
    }

    public boolean contains(int value) {
        return containsValue(value) && !containsNegativeValue(value);
    }

    public boolean containsValue(int value) {
        for (ConditionIntegerRange range : values) {
            if (range.isInRange(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsNegativeValue(int value) {
        for (ConditionIntegerRange range : negativeValues) {
            if (range.isInRange(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void copy(Condition condition) {
        if (condition instanceof ConditionInteger) {
            noCondition = ((ConditionInteger) condition).noCondition;
            values = ((ConditionInteger) condition).values;
            negativeValues = ((ConditionInteger) condition).negativeValues;
        }
    }

    @Override
    public void addReasons(Args a, ItemStack item, ItemMeta meta, String failMessage) {
        if (hasValue()) {
            StringBuilder data = new StringBuilder();
            boolean firstValue = true;
            for (ConditionIntegerRange range : values) {
                if (!firstValue) {
                    data.append(",");
                }
                data.append(range.getHashString());

                firstValue = false;
            }

            for (ConditionIntegerRange range : negativeValues) {
                if (!firstValue) {
                    data.append(",");
                }
                data.append(range.getHashString());

                firstValue = false;
            }

            a.addReason("flag.ingredientconditions.no" + name, failMessage, "{item}", ToolsItem.print(item), "{data}", data.toString());
        } else {
            a.addReason("flag.ingredientconditions.empty" + name, failMessage, "{item}", ToolsItem.print(item));
        }
    }

    @Override
    public String getHashString() {
        String toHash = "conditionInteger:";
        toHash += "name: " + name;
        toHash += "noCondition: " + noCondition;

        for (ConditionIntegerRange value : values) {
            toHash += "value: " + value.getHashString();
        }

        for (ConditionIntegerRange value : negativeValues) {
            toHash += "negativeValue: " + value.getHashString();
        }

        return toHash;
    }
}
