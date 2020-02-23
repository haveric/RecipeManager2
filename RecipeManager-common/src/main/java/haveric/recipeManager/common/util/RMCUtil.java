package haveric.recipeManager.common.util;

import haveric.recipeManager.common.RMCChatColor;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Set;

public class RMCUtil {
    public static <T> T parseEnum(String name, T[] values) {
        if (name != null && !name.isEmpty()) {
            name = RMCUtil.parseAliasName(name);

            for (T t : values) {
                if (t != null) {
                    String s = RMCUtil.parseAliasName(((Enum<?>) t).name());

                    if (s.equals(name)) {
                        return t;
                    }
                }
            }
        }

        return null;
    }

    public static String parseAliasName(String name) {
        return name.replaceAll("[\\s\\W_]+", "").trim().toLowerCase();
    }

    public static String removeExtensions(String value, Set<String> extensions) {
        int i = value.lastIndexOf('.');

        if (i > -1 && extensions.contains(value.substring(i).trim().toLowerCase())) {
            return value.substring(0, i);
        }

        return value;
    }

    public static String printNumber(Number number) {
        return NumberFormat.getNumberInstance().format(number);
    }

    public static String replaceVariables(String msg, Object... variables) {
        if (msg != null && variables != null) {
            int variablesLength = variables.length;
            if (variablesLength > 0) {
                if (variablesLength % 2 > 0) {
                    throw new IllegalArgumentException("Variables argument must have pairs of 2 arguments!");
                }

                for (int i = 0; i < variablesLength; i += 2) { // loop 2 by 2
                    if (variables[i] != null && variables[i + 1] != null) {
                        msg = msg.replace(variables[i].toString(), variables[i + 1].toString());
                    }
                }
            }
        }

        return msg;
    }

    public static String collectionToString(Collection<?> collection) {
        if (collection.isEmpty()) {
            return "";
        }

        StringBuilder s = new StringBuilder(collection.size() * 16);
        boolean first = true;

        for (Object o : collection) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }

            s.append(o.toString());
        }

        return s.toString();
    }

    public static String hideString(String string) {
        char[] data = new char[string.length() * 2];

        for (int i = 0; i < data.length; i += 2) {
            data[i] = RMCChatColor.COLOR_CHAR;
            if (i == 0) {
                data[i + 1] = string.charAt(0);
            } else {
                data[i + 1] = string.charAt(i / 2);
            }
        }

        return new String(data);
    }

    public static String unhideString(String string) {
        return string.replace(String.valueOf(RMCChatColor.COLOR_CHAR), "");
    }

    public static String parseColors(String message, boolean removeColors) {
        String parsedColors = null;

        if (message != null) {
            for (RMCChatColor color : RMCChatColor.values()) {
                String colorString = "";

                if (!removeColors) {
                    colorString = color.toString();
                }

                message = message.replaceAll("(?i)<" + color.name() + ">", colorString);
            }

            if (removeColors) {
                parsedColors = RMCChatColor.stripColor(message);
            } else {
                parsedColors = RMCChatColor.translateAlternateColorCodes('&', message);
            }
        }

        return parsedColors;
    }

    public static String trimExactQuotes(String input) {
        String output;

        output = input.trim();
        if (output.startsWith("\"") && output.endsWith("\"")) {
            output = output.substring(1, output.length() - 1);
        }

        return output;
    }
}
