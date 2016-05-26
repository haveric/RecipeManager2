package haveric.recipeManager.flags;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.Settings;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.TestMessageSender;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({Settings.class, MessageSender.class, RecipeManager.class})
public class FlagBaseTest {

    private Recipes recipes;
    private boolean loaded = false;
    protected File workDir;
    protected File errorFile;

    @Before
    public void setupBase() {
        if (!loaded) {
            setupOnce();

            loaded = true;
        }

        recipes = new Recipes();

        mockStatic(RecipeManager.class);
        when(RecipeManager.getRecipes()).thenReturn(recipes);
    }

    private void setupOnce() {
        mockStatic(Settings.class);
        Settings mockSettings = mock(Settings.class);
        when(Settings.getInstance()).thenReturn(mockSettings);
        when(mockSettings.getMultithreading()).thenReturn(false);

        mockStatic(MessageSender.class);
        when(MessageSender.getInstance()).thenReturn(TestMessageSender.getInstance());

        new FlagLoader();
        FlagFactory.getInstance().init();

        workDir = new File("src/test/work/");
        workDir.delete();
        workDir.mkdirs();
    }
}
