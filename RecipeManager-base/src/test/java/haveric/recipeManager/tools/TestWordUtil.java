package haveric.recipeManager.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestWordUtil {

    @Test
    public void capitalizeAlreadyCapitalized() {
        assertEquals("Foo Bar", WordUtil.capitalize("Foo Bar"));
    }

    @Test
    public void capitalizeFromLower() {
        assertEquals("Foo Bar", WordUtil.capitalize("foo bar"));
    }

    @Test
    public void capitalizeMixedCase() {
        assertEquals("Foo Bar", WordUtil.capitalize("foo Bar"));
    }

    @Test
    public void capitalizeWithEndSpace() {
        assertEquals("Foo Bar", WordUtil.capitalize("foo bar "));
    }

    @Test
    public void capitalizeWithSpacing() {
        assertEquals("Foo Bar", WordUtil.capitalize(" foo bar "));
    }

    @Test
    public void capitalizeFully() {
        assertEquals("Foo Bar", WordUtil.capitalizeFully("FOO BAR"));
    }
}
