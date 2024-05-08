package haveric.recipeManager.flag.conditions.condition;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ConditionString extends Condition {
    private boolean matchesName;
    private boolean noCondition;
    private final boolean allowExtraFormatting;
    List<String> values;
    List<String> negativeValues;
    private final String flagType;
    private final boolean noMeta;

    public ConditionString(String name, String flagType, String arg, boolean noMeta) {
        super(name);
        this.noMeta = noMeta;
        this.flagType = flagType;
        this.allowExtraFormatting = false;

        parseArg(arg);
    }

    public ConditionString(String name, String flagType, String arg, boolean noMeta, boolean allowExtraFormatting) {
        super(name);
        this.noMeta = noMeta;
        this.flagType = flagType;
        this.allowExtraFormatting = allowExtraFormatting;

        parseArg(arg);
    }

    @Override
    public boolean hasValue() {
        return (!values.isEmpty() || !negativeValues.isEmpty()) && !noCondition;
    }

    public List<String> getValues() {
        return values;
    }

    public List<String> getNegativeValues() {
        return negativeValues;
    }

    public boolean contains(String value) {
        return containsValue(value) && !containsNegativeValue(value);
    }

    public boolean containsRegex(String value) {
        return containsValueRegex(value) && !containsNegativeRegex(value);
    }

    private boolean containsValue(String valueToCheck) {
        for (String value : values) {
            if (valueToCheck.equalsIgnoreCase(value)) {
                return true;
            }
        }

        return false;
    }

    private boolean containsNegativeValue(String valueToCheck) {
        for (String value : negativeValues) {
            if (valueToCheck.equalsIgnoreCase(value)) {
                return true;
            }
        }

        return false;
    }

    private boolean containsValueRegex(String possibleRegex) {
        for (String value : values) {
            Boolean parsed = parseRegex(value, possibleRegex);
            if (parsed != null) {
                return parsed;
            }
        }
        return false;
    }

    private boolean containsNegativeRegex(String possibleRegex) {
        for (String value : negativeValues) {
            Boolean parsed = parseRegex(value, possibleRegex);
            if (parsed != null) {
                return parsed;
            }
        }
        return false;
    }

    private Boolean parseRegex(String value, String possibleRegex) {
        if (value.startsWith("regex:")) {
            try {
                Pattern pattern = Pattern.compile(value.substring("regex:".length()));
                if (pattern.matcher(possibleRegex).matches()) {
                    return true;
                }
            } catch (PatternSyntaxException e) {
                return ErrorReporter.getInstance().error("Flag " + flagType + " has invalid regex pattern '" + e.getPattern() + "', error: " + e.getMessage(), "Use 'https://www.regexpal.com/' (or something similar) to test your regex code before using it.");
            }
        } else if (value.equalsIgnoreCase(possibleRegex)) {
            return true;
        }

        return null;
    }

    public boolean skipCondition() {
        return !matchesName || (!noMeta && noCondition);
    }

    public boolean shouldHaveNoMeta() {
        return noMeta || noCondition;
    }

    private void parseArg(String originalArg) {
        String argLower = originalArg.toLowerCase();
        values = new ArrayList<>();
        negativeValues = new ArrayList<>();
        noCondition = false;
        matchesName = false;

        if (argLower.startsWith("!" + name) || argLower.startsWith("no" + name)) {
            noCondition = true;
            matchesName = true;
        } else if (argLower.startsWith(name)) {
            matchesName = true;
            String argTrimmed = originalArg.substring(name.length()).trim();
            String[] args = argTrimmed.split(",");
            for (String arg : args) {
                boolean negative = false;
                String valTrimmed = arg.trim();
                if (valTrimmed.startsWith("!")) {
                    valTrimmed = valTrimmed.substring(1);
                    negative = true;
                }

                if (allowExtraFormatting) {
                    // Trim any quotes and parse colors
                    valTrimmed = RMCUtil.parseColors(RMCUtil.trimExactQuotes(valTrimmed), false);
                }

                if (negative) {
                    negativeValues.add(valTrimmed);
                } else {
                    values.add(valTrimmed);
                }
            }
        }
    }

    @Override
    public void copy(Condition condition) {
        if (condition instanceof ConditionString) {
            values = ((ConditionString) condition).values;
            negativeValues = ((ConditionString) condition).negativeValues;
        }
    }

    @Override
    public void addReasons(Args a, ItemStack item, ItemMeta meta, String failMessage) {
        if (hasValue()) {
            a.addReason("flag.ingredientconditions.no" + name, failMessage, "{item}", ToolsItem.print(item), "{data}", getValuesString());
        } else {
            a.addReason("flag.ingredientconditions.empty" + name, failMessage, "{item}", ToolsItem.print(item));
        }
    }

    public String getValuesString() {
        StringBuilder data = new StringBuilder();
        boolean firstValue = true;
        for (String value : values) {
            if (!firstValue) {
                data.append(",");
            }
            data.append(value);

            firstValue = false;
        }

        for (String value : negativeValues) {
            if (!firstValue) {
                data.append(",");
            }
            data.append(value);

            firstValue = false;
        }

        return data.toString();
    }

    @Override
    public String getHashString() {
        String toHash = "conditionString:";
        toHash += "name: " + name;
        toHash += "noCondition: " + noCondition;

        for (String value : values) {
            toHash += "value: " + value;
        }

        for (String value : negativeValues) {
            toHash += "negativeValue: " + value;
        }

        return toHash;
    }
}
