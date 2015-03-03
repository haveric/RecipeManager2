package haveric.recipeManager;

import haveric.recipeManager.tools.Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


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

        boolean overwrite = isNewVersion();
    }

    private void createDirectories() {
        File file = new File(DIR_PLUGIN + "recipes" + File.separator + "disabled");
        file.mkdirs();

        file = new File(file.getPath() + File.separator + "Recipe files in here are ignored!");

        if (!file.exists()) {
            Tools.saveTextToFile("In the disabled folder you can place recipe files you don't want to load, instead of deleting them.", file);
        }
    }

    private boolean isNewVersion() {
        boolean newVersion = true;

        File file = new File(DIR_PLUGIN + FILE_USED_VERSION);
        String currentVersion = plugin.getVersion();

        try {
            if (file.exists()) {
                BufferedReader b = new BufferedReader(new FileReader(file));
                String version = b.readLine();
                b.close();
                newVersion = (version == null || !version.equals(currentVersion));
            }

            if (newVersion || file.exists()) {
                BufferedWriter b = new BufferedWriter(new FileWriter(file, false));
                b.write(currentVersion);
                b.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
