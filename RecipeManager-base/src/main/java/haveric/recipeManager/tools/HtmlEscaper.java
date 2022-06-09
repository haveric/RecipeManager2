package haveric.recipeManager.tools;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

/**
 * Custom version of HtmlEscapers
 */
public class HtmlEscaper {
    public static Escaper htmlEscaper() {
        return HTML_ESCAPER;
    }

    private static final Escaper HTML_ESCAPER =
        Escapers.builder()
            .addEscape('"', "&quot;")
            // Note: "&apos;" is not defined in HTML 4.01.
            //.addEscape('\'', "&#39;") // CUSTOM: Ignore single quotes
            .addEscape('&', "&amp;")
            .addEscape('<', "&lt;")
            .addEscape('>', "&gt;")
            .build();

    private HtmlEscaper() {}
}
