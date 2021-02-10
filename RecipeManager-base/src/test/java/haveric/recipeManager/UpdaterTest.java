package haveric.recipeManager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdaterTest {

    @Test
    public void testVersionEquals() {
        assertEquals(0, Updater.isVersionNewerThan("2.10.0", "RecipeManager v2.10.0"));
        assertEquals(0, Updater.isVersionNewerThan("2.10.0 alpha", "RecipeManager v2.10.0 alpha"));
        assertEquals(0, Updater.isVersionNewerThan("2.10.0 beta", "v2.10.0 beta"));
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
