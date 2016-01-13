package haveric.recipeManager.flags;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bukkit.GameMode;
import org.junit.Test;

public class FlagGameModeTest {

    @Test
    public void getGameModes() {
        FlagGameMode flag = new FlagGameMode();

        flag.addGameMode(GameMode.ADVENTURE);
        flag.getGameModes().clear();
        assertFalse(flag.getGameModes().isEmpty());
    }

    @Test
    public void onParse() {
        FlagGameMode flag = new FlagGameMode();

        flag.clearGameModes();
        flag.onParse("a");
        assertTrue(flag.getGameModes().contains(GameMode.ADVENTURE));

        flag.clearGameModes();
        flag.onParse("adventure");
        assertTrue(flag.getGameModes().contains(GameMode.ADVENTURE));

        flag.clearGameModes();
        flag.onParse("c");
        assertTrue(flag.getGameModes().contains(GameMode.CREATIVE));

        flag.clearGameModes();
        flag.onParse("creative");
        assertTrue(flag.getGameModes().contains(GameMode.CREATIVE));

        flag.clearGameModes();
        flag.onParse("s");
        assertTrue(flag.getGameModes().contains(GameMode.SURVIVAL));

        flag.clearGameModes();
        flag.onParse("survival");
        assertTrue(flag.getGameModes().contains(GameMode.SURVIVAL));

        flag.clearGameModes();
        flag.onParse("a,s");
        assertTrue(flag.getGameModes().contains(GameMode.ADVENTURE));
        assertTrue(flag.getGameModes().contains(GameMode.SURVIVAL));
        assertFalse(flag.getGameModes().contains(GameMode.CREATIVE));

        flag.clearGameModes();
        flag.onParse("false");
        assertTrue(flag.getGameModes().isEmpty());

        flag.clearGameModes();
        try {
            flag.onParse("duck");
        } catch (NullPointerException e) {
            assertTrue(flag.getGameModes().isEmpty());
        }

    }

}
