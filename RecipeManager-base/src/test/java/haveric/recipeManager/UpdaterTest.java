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
}
