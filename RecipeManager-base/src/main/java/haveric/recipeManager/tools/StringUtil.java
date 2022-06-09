package haveric.recipeManager.tools;

import java.util.Iterator;
import java.util.List;

public class StringUtil {
    public static String join(final List<String> list, final String separator) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return "";
        }

        Iterator<String> iter = list.iterator();
        String first = iter.next();
        if (!iter.hasNext()) {
            return first;
        }

        final StringBuilder returned = new StringBuilder(256);
        if (first != null) {
            returned.append(first);
        }

        // two or more list items
        while (iter.hasNext()) {
            if (separator != null) {
                returned.append(separator);
            }
            String str = iter.next();
            if (str != null) {
                returned.append(str);
            }
        }

        return returned.toString();
    }
}
