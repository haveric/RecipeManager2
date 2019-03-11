package haveric.recipeManager.recipes;


import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Settings;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RecipeFileReader {

    private final String UTF8_BOM = new String("\uFEFF".getBytes(StandardCharsets.UTF_8));

    private String line;
    private int lineNum;
    private boolean commentBlock;
    private String fileName;
    private BufferedReader reader;

    public RecipeFileReader(BufferedReader bufferedReader, String fileName) {
        lineNum = 0;
        this.reader = bufferedReader;
        commentBlock = false;
        this.fileName = fileName;
    }

    public boolean nextLine() {
        do {
            if (!readNextLine()) {
                return false;
            }

            line = parseComments();
        } while (line == null);

        return true;
    }

    public boolean searchRecipes() {
        return line != null && lineIsRecipe() || nextLine();

    }

    public boolean lineIsRecipe() {
        String lowered = line.toLowerCase();

        for (RMCRecipeType type : RMCRecipeType.values()) {
            String directive = type.getDirective();

            if (directive != null && (lowered.equals(directive) || lowered.startsWith(directive + " "))) {
                return true;
            }
        }

        return false;
    }

    public boolean lineIsFlag() {
        return line.charAt(0) == '@';
    }

    public boolean lineIsResult() {
        return line.charAt(0) == '=';
    }

    public String getFileName() {
        return fileName;
    }

    public boolean readNextLine() {
        lineNum++;
        ErrorReporter.getInstance().setLine(lineNum);

        try {
            line = reader.readLine();

            if (line != null) {
                if (lineNum == 1 && line.startsWith(UTF8_BOM)) {
                    line = line.replace(UTF8_BOM, "");
                }
                line = line.trim();
            }
            return line != null;
        } catch (Throwable e) {
            MessageSender.getInstance().error(null, e, null);
            return false;
        }
    }

    public String parseComments() {
        if (line == null || line.isEmpty()) {
            return null;
        }

        int index;

        // if we are in a comment block check for exit character
        if (commentBlock) {
            index = line.indexOf("*/");

            if (index >= 0) {
                commentBlock = false;

                String comment;
                if (index == 0) {
                    comment = null;
                } else {
                    comment = line.substring(0, index);
                }
                return comment;
            }

            return null;
        }

        index = line.indexOf("/*"); // check for comment block start

        if (index >= 0) {
            int end = line.indexOf("*/"); // check for comment block end chars on the same line

            if (end > 0) {
                return line.substring(0, index) + line.substring(end + 2);
            }

            commentBlock = true;
            String comment;
            if (index == 0) {
                comment = null;
            } else {
                comment = line.substring(0, index);
            }
            return comment;
        }

        // now check for in-line comments
        List<String> comments = Settings.getInstance().getRecipeCommentCharactersAsList();
        for (String comment : comments) {
            index = line.indexOf(comment);

            if (index == 0) {
                return null;
            }

            if (index > -1) {
                return line.substring(0, index); // partial comment, return filtered data
            }
        }

        return line;
    }

    public void parseFlags(Flags flags) {
        nextLine();

        while (line != null && line.length() > 0 && lineIsFlag()) {
            flags.parseFlag(line);
            nextLine();
        }
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }
}
