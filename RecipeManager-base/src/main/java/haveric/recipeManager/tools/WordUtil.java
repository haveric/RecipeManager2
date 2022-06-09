package haveric.recipeManager.tools;

public class WordUtil {
    public static String capitalize(final String str) {
        if (str.isEmpty()) {
            return str;
        }

        String[] words = str.split(" ");
        StringBuilder replaced = new StringBuilder(str.length());

        for (String word : words) {
            if (replaced.length() > 0) {
                replaced.append(" ");
            }

            if (!word.isEmpty()) {
                replaced.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
        }

        return replaced.toString();
    }

    public static String capitalizeFully(final String str) {
        if (str.isEmpty()) {
            return str;
        }

        return capitalize(str.toLowerCase());
    }
}
