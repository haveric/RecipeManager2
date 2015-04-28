package haveric.recipeManager.tools;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Tools {

    public static boolean saveTextToFile(String text, String filePath) {
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        return saveTextToFile(text, file);
    }

    public static boolean saveTextToFile(String text, File file) {
        boolean success = false;

        try {
            BufferedWriter stream = new BufferedWriter(new FileWriter(file, false));
            stream.write(text);
            stream.close();

            success = true;
        } catch (IOException e) {
            //e.printStackTrace();
        }

        return success;
    }
}
