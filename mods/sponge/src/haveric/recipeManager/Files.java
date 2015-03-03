package haveric.recipeManager;

import haveric.recipeManager.tools.Tools;

import java.io.File;


public class Files {
    public static final String NL = System.getProperty("line.separator");

    public static final String FILE_USED_VERSION = "used.version";

    private static String DIR_PLUGIN;

    private RecipeManager plugin;

    public Files(RecipeManager recipeManager) {
        plugin = recipeManager;
        DIR_PLUGIN = plugin.getSettings().getDefaultFolderPath() + File.separator;

        reload();
    }

    public void reload() {
        createDirectories();
    }

    private void createDirectories() {
        File file = new File(DIR_PLUGIN + "recipes" + File.separator + "disabled");
        file.mkdirs();

        file = new File(file.getPath() + File.separator + "Recipe files in here are ignored!");

        if (!file.exists()) {
            Tools.saveTextToFile("In the disabled folder you can place recipe files you don't want to load, instead of deleting them.", file);
        }
    }
}
