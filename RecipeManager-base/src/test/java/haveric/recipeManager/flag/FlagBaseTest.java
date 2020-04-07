package haveric.recipeManager.flag;

import haveric.recipeManager.*;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.messages.TestMessageSender;
import haveric.recipeManager.recipes.RecipeTypeFactory;
import haveric.recipeManager.recipes.RecipeTypeLoader;
import org.bukkit.Bukkit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.UUID;

import static haveric.recipeManager.Files.FILE_MESSAGES;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({Settings.class, MessageSender.class, Bukkit.class, RecipeManager.class})
public class FlagBaseTest {
    protected Settings settings;
    protected TestUnsafeValues unsafeValues;
    protected File workDir;
    protected String originalResourcesPath;
    protected String baseResourcesPath;
    protected String baseDataPath;
    protected String baseRecipePath;
    protected UUID testUUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Before
    public void setupBase() {
        mockStatic(Bukkit.class);
        unsafeValues = new TestUnsafeValues();
        when(Bukkit.getUnsafe()).thenReturn(unsafeValues);

        mockStatic(RecipeManager.class);
        settings = new Settings(false);
        when(RecipeManager.getSettings()).thenReturn(settings);

        mockStatic(MessageSender.class);
        when(MessageSender.getInstance()).thenReturn(TestMessageSender.getInstance());


        TestItemFactory itemFactory = new TestItemFactory();
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);

        TestOfflinePlayer player = new TestOfflinePlayer();
        when(Bukkit.getOfflinePlayer(testUUID)).thenReturn(player);

        new RecipeTypeLoader();
        RecipeTypeFactory.getInstance().init();
        new FlagLoader(true);
        FlagFactory.getInstance().init();

        File baseSrcDir = new File("src");
        String baseSrcPath = baseSrcDir.getAbsolutePath().replace(".idea\\modules\\", "") + "/";
        String baseTestPath = baseSrcPath + "test/";

        workDir = new File(baseTestPath + "work/");
        workDir.delete();
        workDir.mkdirs();

        originalResourcesPath = baseSrcPath + "main/resources/";
        baseResourcesPath = baseTestPath + "resources/";
        baseDataPath = baseResourcesPath + "data/";
        settings.loadFileConfig(new File(baseDataPath), "config-no-multithreading.yml");

        baseRecipePath = baseResourcesPath + "recipes/";

        File messagesFile = new File(baseSrcPath + "/main/resources/" + FILE_MESSAGES);
        Messages.getInstance().loadMessages(null, messagesFile);

        Recipes recipes = new Recipes();

        when(RecipeManager.getRecipes()).thenReturn(recipes);
    }
}
