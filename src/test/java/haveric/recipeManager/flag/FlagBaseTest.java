package haveric.recipeManager.flag;

import haveric.recipeManager.*;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.messages.TestMessageSender;
import org.bukkit.Bukkit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.UUID;

import static haveric.recipeManager.Files.FILE_MESSAGES;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({Settings.class, MessageSender.class, Bukkit.class, RecipeManager.class})
public class FlagBaseTest {

    private Recipes recipes;
    protected Settings settings;
    protected File workDir;
    protected UUID testUUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Before
    public void setupBase() {
        mockStatic(Settings.class);
        settings = mock(Settings.class);
        when(Settings.getInstance()).thenReturn(settings);
        when(settings.getMultithreading()).thenReturn(false);

        mockStatic(MessageSender.class);
        when(MessageSender.getInstance()).thenReturn(TestMessageSender.getInstance());

        mockStatic(Bukkit.class);
        TestItemFactory itemFactory = new TestItemFactory();
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);

        TestOfflinePlayer player = new TestOfflinePlayer();
        when(Bukkit.getOfflinePlayer(testUUID)).thenReturn(player);

        new FlagLoader();
        FlagFactory.getInstance().init();

        workDir = new File("src/test/work/");
        workDir.delete();
        workDir.mkdirs();

        File messagesFile = new File("src/main/resources/" + FILE_MESSAGES);
        Messages.getInstance().loadMessages(null, messagesFile);

        recipes = new Recipes();

        mockStatic(RecipeManager.class);
        when(RecipeManager.getRecipes()).thenReturn(recipes);
    }
}
