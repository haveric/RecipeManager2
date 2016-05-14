package haveric.recipeManager.flags;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.Settings;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.TestMessageSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Settings.class, MessageSender.class, RecipeManager.class})
public class FlagItemNameTest {
    private FlagItemName flag;
    private Recipes recipes;

    @Before
    public void setup() {
        flag = new FlagItemName();
        recipes = new Recipes();

        mockStatic(Settings.class);
        Settings mockSettings = mock(Settings.class);
        when(Settings.getInstance()).thenReturn(mockSettings);
        when(mockSettings.getMultithreading()).thenReturn(false);

        mockStatic(MessageSender.class);
        when(MessageSender.getInstance()).thenReturn(TestMessageSender.getInstance());

        mockStatic(RecipeManager.class);
        when(RecipeManager.getRecipes()).thenReturn(recipes);

        new FlagLoader();
        FlagFactory.getInstance().init();
    }

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagItemName/flagItemName.txt");
        RecipeProcessor.reload(null, true, file.getPath());
    }
}
