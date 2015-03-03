package haveric.recipeManager.tools;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Tools {

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
    /*
    public static boolean saveTextToFile(String text, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            BufferedWriter stream = new BufferedWriter(new FileWriter(file, false));
            stream.write(text);
            stream.close();
            return true;
        } catch (Throwable e) {
            //Messages.error(null, e, null);
        }

        return false;
    }
    */
}
