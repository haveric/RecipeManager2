package haveric.recipeManager.flag;

import haveric.recipeManager.*;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.messages.TestMessageSender;
import haveric.recipeManager.recipes.RecipeTypeFactory;
import haveric.recipeManager.recipes.RecipeTypeLoader;
import haveric.recipeManager.settings.BaseSettings;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.UUID;

import static haveric.recipeManager.Files.FILE_MESSAGES;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class FlagBaseTest {
    protected BaseSettings settings;
    protected File workDir;
    protected String originalResourcesPath;
    protected String baseResourcesPath;
    protected String baseDataPath;
    protected String baseRecipePath;
    protected Recipes recipes;
    protected TestItemFactory itemFactory;
    protected TestOfflinePlayer player;
    protected UUID testUUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    protected void init() {
        settings = new TestSettings(false);
    }

    protected void loadSettings() {

    }

    @BeforeEach
    public void setupBase() {
        init();

        itemFactory = new TestItemFactory();
        try (MockedStatic<MessageSender> mockedMessageSender = mockStatic(MessageSender.class)) {
            mockedMessageSender.when(MessageSender::getInstance).thenReturn(TestMessageSender.getInstance());

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                new RecipeTypeLoader();
            }

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

            loadSettings();
            baseRecipePath = baseResourcesPath + "recipes/";

            File messagesFile = new File(originalResourcesPath + FILE_MESSAGES);
            Messages.getInstance().loadMessages(null, messagesFile);
        }

        recipes = new Recipes();
        RecipeManager.setRecipes(recipes);
        player = new TestOfflinePlayer();
    }

    public void reloadRecipeProcessor(boolean check, File file) {
        try (MockedStatic<RecipeManager> mockedRecipeManager = mockStatic(RecipeManager.class)) {
            mockedRecipeManager.when(RecipeManager::getSettings).thenReturn(settings);
            mockedRecipeManager.when(RecipeManager::getRecipes).thenReturn(recipes);

            try (MockedStatic<MessageSender> mockedMessageSender = mockStatic(MessageSender.class)) {
                mockedMessageSender.when(MessageSender::getInstance).thenReturn(TestMessageSender.getInstance());

                try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                    mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                    RecipeProcessor.reload(null, check, file.getPath(), workDir.getPath());
                }
            }
        }
    }
}
