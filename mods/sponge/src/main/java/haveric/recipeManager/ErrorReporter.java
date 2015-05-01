package haveric.recipeManager;

import haveric.recipeManager.tools.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class ErrorReporter {
    private static HashMap<String, List<Text>> fileErrors;
    private static String currentFile;
    private static int currentLine;
    private static boolean ignore = false;
    
    public static void startCatching() {
        stopCatching();
        fileErrors = new HashMap<String, List<Text>>();
    }
    
    public static void stopCatching() {
        fileErrors = null;
        currentFile = null;
        currentLine = 0;
        ignore = false;
    }
    
    public static boolean isCatching() {
        return fileErrors != null;
    }
    
    public static int getCaughtAmount() {
        int caught;
        
        if (isCatching()) {
            caught = fileErrors.size();
        } else {
            caught = -1;
        }
        
        return caught;
    }
    
    public static void print(String logFile) {
        if (!isCatching() || fileErrors.isEmpty()) {
            stopCatching();
            return;
        }

        String lastError;
        int lastErrorNum;
        int similarErrors;
        
        TextBuilder buffer;
        TextBuilder text = Texts.builder();
        
        for (Entry<String, List<Text>> entry : fileErrors.entrySet()) {
            buffer = Texts.builder();
            buffer.append(Texts.of(TextStyles.BOLD, TextColors.AQUA, "File: " + entry.getKey() + Files.NL));
            
            lastError = "";
            lastErrorNum = 0;
            similarErrors = 0;
            
            for (Text error : entry.getValue()) {
                String errorString = Texts.toPlain(error);
                if (errorString.startsWith(lastError, 10)) {
                    if (++lastErrorNum > 3) {
                        similarErrors++;
                        continue;
                    }
                } else {
                    if (similarErrors > 0) {
                        buffer.append(Texts.of(TextColors.RED, "... and " + similarErrors + " more similar errors." + Files.NL));
                    }
                    
                    lastErrorNum = 0;
                    similarErrors = 0;
                }
                
                buffer.append(Texts.of(TextColors.WHITE, error + Files.NL));
                lastError = errorString.substring(10);
            }
            
            if (similarErrors > 0) {
                buffer.append(Texts.of(TextColors.RED, "... and " + similarErrors + " more similar errors." + Files.NL));
            }
            
            buffer.append(Texts.of(Files.NL));
            text.append(Texts.of(buffer));
            Messages.send(null, buffer.build());
        }
        
        text.append(Texts.of(Files.NL + Files.NL));
        
        
        if (logFile != null && Tools.saveTextToFile(Texts.toPlain(text.build()), logFile)) {
            Messages.send(null, Texts.of(TextColors.YELLOW, "Error messages saved in" + logFile + '.'));
        }
        
        stopCatching();
    }
    
    public static void setFile(String file) {
        currentFile = file;
        currentLine = 0;
    }
    
    public static String getFile() {
        return currentFile;
    }
    
    public static void setLine(int line) {
        currentLine = line;
    }
    
    public static int getLine() {
        return currentLine;
    }
    
    protected static void setIgnoreErrors(boolean set) {
        if (isCatching()) {
            ignore = set;
        }
    }
    
    protected static boolean getIgnoreErrors() {
        return ignore;
    }
    
    public static void warning(String warning) {
        warning(warning, null);
    }
    
    public static void warning(String warning, String tip) {
        entry(Texts.of(TextColors.YELLOW, TextStyles.UNDERLINE, "Warning"), warning, tip);
    }
    
    public static boolean error(String error) {
        return error(error, null);
    }
    
    public static boolean error(String error, String tip) {
        entry(Texts.of(TextColors.RED, TextStyles.UNDERLINE, "Warning"), error, tip);
        return false;
    }
    
    private static void entry(Text type, String message, String tip) {
        if (!isCatching()) {
            type = Texts.of(type, ":", TextColors.RESET, " " + message);
            
            if (tip != null) {
                type = Texts.of(type, TextColors.DARK_GREEN, " TIP: ", TextColors.GRAY, tip);
            }
            
            Messages.send(null, type);
        } else if (!ignore) {
            List<Text> errors = fileErrors.get(currentFile);
            
            if (errors == null) {
                errors = new ArrayList<Text>();
            }
            
            type = Texts.of("line " + String.format("%-5d", currentLine), type, ": ", TextColors.RESET, message);
            
            if (tip != null) {
                type = Texts.of(type, Files.NL, TextColors.DARK_GREEN, "          TIP: ", TextColors.GRAY, tip);
            }
            errors.add(type);
            
            fileErrors.put(currentFile, errors);
        }
    }
}
