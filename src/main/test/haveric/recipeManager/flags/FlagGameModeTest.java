package haveric.recipeManager.flags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class FlagGameModeTest {

    private FlagGameMode flag;

    @Before
    public void setup() {
        flag = new FlagGameMode();
    }
    @Test
    public void testClone() {
        flag.addGameMode(GameMode.ADVENTURE);
        flag.addGameMode(GameMode.SURVIVAL);
        flag.setFailMessage("Recipe has failed");

        FlagGameMode clone = flag.clone();
        assertTrue(clone.getGameModes().contains(GameMode.ADVENTURE));
        assertTrue(clone.getGameModes().contains(GameMode.SURVIVAL));
        assertEquals(clone.getGameModes().size(), 2);

        assertEquals(clone.getFailMessage(), "Recipe has failed");
    }

    @Test
    public void getGameModes() {
        flag.addGameMode(GameMode.ADVENTURE);
        flag.getGameModes().clear();
        assertFalse(flag.getGameModes().isEmpty());
    }

    @Test
    public void onParse() {
        // Test individual game modes
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

        // Test multiple gamemodes
        flag.clearGameModes();
        flag.onParse("a,s");
        assertTrue(flag.getGameModes().contains(GameMode.ADVENTURE));
        assertTrue(flag.getGameModes().contains(GameMode.SURVIVAL));
        assertFalse(flag.getGameModes().contains(GameMode.CREATIVE));

        // Test disable using false
        flag.clearGameModes();
        flag.onParse("false");
        assertTrue(flag.getGameModes().isEmpty());

        // Test fail message
        flag.clearGameModes();
        flag.onParse("a | fail");
        assertTrue(flag.getGameModes().contains(GameMode.ADVENTURE));
        assertEquals(flag.getGameModes().size(), 1);
        assertEquals(flag.getFailMessage(), "fail");

        // Test invalid input TODO: Improve this
        flag.clearGameModes();
        try {
            flag.onParse("duck");
        } catch (NullPointerException e) {
            assertTrue(flag.getGameModes().isEmpty());
        }
    }

    @Test
    public void onCrafted() {
        Player mockPlayer = mock(Player.class);
        when(mockPlayer.getGameMode()).thenReturn(GameMode.SURVIVAL);
        Args a = ArgBuilder.create().player(mockPlayer).build().processArgs();

        // Test valid game mode
        flag.addGameMode(GameMode.SURVIVAL);
        flag.onCrafted(a);
        assertFalse(a.hasReasons());

        // Test invalid game mode
        flag.clearGameModes();
        flag.addGameMode(GameMode.CREATIVE);
        flag.onCrafted(a);
        assertTrue(a.hasReasons());

        // Test Args with no player
        a = ArgBuilder.create().build();
        flag.onCrafted(a);
        assertTrue(a.hasReasons());
    }
}
