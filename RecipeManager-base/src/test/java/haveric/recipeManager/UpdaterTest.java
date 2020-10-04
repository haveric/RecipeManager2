package haveric.recipeManager;

import org.junit.Test;

import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;

public class UpdaterTest {
    @Test
    public void testRegex() {
        Matcher matcher = Updater.getMatcher("RecipeManager v2.10.0");
        if (matcher.find()) {
            assertEquals("2.10.0", Updater.getVersion(matcher));
            assertEquals("", Updater.getBetaStatus(matcher));
        }

        matcher = Updater.getMatcher("RecipeManager 2.10.0 alpha");
        if (matcher.find()) {
            assertEquals("2.10.0", Updater.getVersion(matcher));
            assertEquals("alpha", Updater.getBetaStatus(matcher));
        }

        matcher = Updater.getMatcher("v2.10.0 beta");
        if (matcher.find()) {
            assertEquals("2.10.0", Updater.getVersion(matcher));
            assertEquals("beta", Updater.getBetaStatus(matcher));
        }

        matcher = Updater.getMatcher("2.10.0 dev");
        if (matcher.find()) {
            assertEquals("2.10.0", Updater.getVersion(matcher));
            assertEquals("dev", Updater.getBetaStatus(matcher));
        }

        matcher = Updater.getMatcher("2 foo");
        if (matcher.find()) {
            assertEquals("2", Updater.getVersion(matcher));
            assertEquals("", Updater.getBetaStatus(matcher));
        }

        matcher = Updater.getMatcher("2.0 alpha1");
        if (matcher.find()) {
            assertEquals("2.0", Updater.getVersion(matcher));
            assertEquals("alpha1", Updater.getBetaStatus(matcher));
        }

        matcher = Updater.getMatcher("2.0 alpha-1");
        if (matcher.find()) {
            assertEquals("2.0", Updater.getVersion(matcher));
            assertEquals("alpha1", Updater.getBetaStatus(matcher));
        }

        matcher = Updater.getMatcher("2.0 alpha - 1");
        if (matcher.find()) {
            assertEquals("2.0", Updater.getVersion(matcher));
            assertEquals("alpha1", Updater.getBetaStatus(matcher));
        }
    }

    @Test
    public void testVersionNewer() {
        // Newer
        assertEquals(1, Updater.isVersionNewerThan("1.0.0", "0.9.0"));

        // Equal
        assertEquals(0, Updater.isVersionNewerThan("1.5.0", "1.5.0"));

        // Newer than dev versions
        assertEquals(1, Updater.isVersionNewerThan("2.24.0", "2.23.1-dev3"));
        assertEquals(1, Updater.isVersionNewerThan("2.23.1-dev3", "2.23.1-dev2"));

        assertEquals(1, Updater.isVersionNewerThan("2.23.1-alpha", "2.23.1-dev3"));
        assertEquals(1, Updater.isVersionNewerThan("2.23.1-beta", "2.23.1-dev3"));
        assertEquals(1, Updater.isVersionNewerThan("2.23.1-beta", "2.23.1-alpha"));
        assertEquals(1, Updater.isVersionNewerThan("2.23.1-beta", "2.23.1"));

        assertEquals(1, Updater.isVersionNewerThan("v2.23.1-dev3", "v2.23.1"));
        assertEquals(1, Updater.isVersionNewerThan("v2.23.1 alpha", "v2.23.1"));

        assertEquals(0, Updater.isVersionOlderThan("2.24.0", "2.24.0"));
    }
}
