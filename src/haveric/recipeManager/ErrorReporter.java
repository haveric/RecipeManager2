package haveric.recipeManager;

import haveric.recipeManager.tools.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

/**
 * This class is used by RecipeManager to display recipe errors.<br>
 * Errors can be caught to be displayed in a single chunk along with file name and lines.<br>
 * When errors are not caught they'll be directly displayed to console.
 */
public class ErrorReporter {
    private static HashMap<String, List<String>> fileErrors;
    private static String currentFile;
    private static int currentLine;
    private static boolean ignore = false;

    /**
     * Starts catching reported errors and stores them in a list for later printing.<br>
     * This also resets file to null and line to 0
     */
    public static void startCatching() {
        stopCatching();
        fileErrors = new HashMap<String, List<String>>();
    }

    /**
     * Stops catching the errors and ditches any caught errors so far!<br>
     * Calling this requires calling {@link #startCatching()} again to queue errors.
     */
    public static void stopCatching() {
        fileErrors = null;
        currentFile = null;
        currentLine = 0;
        ignore = false;
    }

    /**
     * Check if class caught any errors.
     *
     * @return true if catching, false otherwise
     */
    public static boolean isCatching() {
        return (fileErrors != null);
    }

    /**
     * Gets the amount of queued errors.
     *
     * @return 0 if no errors, -1 if not catching at all
     */
    public static int getCatchedAmount() {
        int caught;

        if (isCatching()) {
            caught = fileErrors.size();
        } else {
            caught = -1;
        }

        return caught;
    }

    /**
     * Print the queued errors (if any)
     *
     * @param logFile
     */
    public static void print(String logFile) {
        if (!isCatching() || fileErrors.isEmpty()) {
            stopCatching();
            return;
        }

        int errors = fileErrors.size();
        String lastError;
        int lastErrorNum;
        int similarErrors;

        StringBuilder buffer;
        StringBuilder text = new StringBuilder(errors * 128); // .append(ChatColor.RED).append("There were ").append(errors).append(" errors while processing the files: ");
        Messages.info(text.toString());
        // text.append(Files.NL).append(Files.NL);

        for (Entry<String, List<String>> entry : fileErrors.entrySet()) {
            buffer = new StringBuilder();
            buffer.append(ChatColor.BOLD).append(ChatColor.AQUA).append("File: ").append(entry.getKey()).append(Files.NL);

            lastError = "";
            lastErrorNum = 0;
            similarErrors = 0;

            for (String error : entry.getValue()) {
                if (error.startsWith(lastError, 10)) {
                    if (++lastErrorNum > 3) {
                        similarErrors++;
                        continue;
                    }
                } else {
                    if (similarErrors > 0) {
                        buffer.append(ChatColor.RED).append("... and ").append(similarErrors).append(" more similar errors.").append(Files.NL);
                    }

                    lastErrorNum = 0;
                    similarErrors = 0;
                }

                buffer.append(ChatColor.WHITE).append(error).append(Files.NL);
                lastError = error.substring(10);
            }

            if (similarErrors > 0) {
                buffer.append(ChatColor.RED).append("... and ").append(similarErrors).append(" more similar errors.").append(Files.NL);
            }

            buffer.append(Files.NL);
            text.append(buffer);
            Messages.info(buffer.toString());
        }

        text.append(Files.NL).append(Files.NL);

        if (logFile != null && Tools.saveTextToFile(ChatColor.stripColor(text.toString()), logFile)) {
            Messages.info(ChatColor.YELLOW + "Error messages saved in '" + logFile + "'.");
        }

        stopCatching();
    }

    /**
     * Set the current file path/name - printed in queued errors.<br>
     * This also resets line to 0.
     *
     * @param line
     */
    public static void setFile(String file) {
        currentFile = file;
        currentLine = 0;
    }

    /**
     * @return the current file the parser is at.
     */
    public static String getFile() {
        return currentFile;
    }

    /**
     * Set the current line - printed in queued errors.<br>
     * This will be reset to 0 after calling {@link #setFile()}
     *
     * @param line
     */
    public static void setLine(int line) {
        currentLine = line;
    }

    /**
     * @return the current line the parser is at.
     */
    public static int getLine() {
        return currentLine;
    }

    /**
     * This can be used to temporarily ignore any errors that are stored.<br>
     * <b>NOTE: Only works when catching errors, use with care.</b>
     *
     * @param set
     */
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
        entry(ChatColor.YELLOW.toString() + ChatColor.UNDERLINE + "Warning", warning, tip);
    }

    /**
     * Queue error or print it directly if queue was not started.
     *
     * @param error
     * @return always returns false, useful for quick returns
     */
    public static boolean error(String error) {
        return error(error, null);
    }

    /**
     * Queue error or print it directly if queue was not started.
     *
     * @param error
     *            the error message
     * @param tip
     *            optional tip, use null to avoid
     * @return always returns false, useful for quick returns
     */
    public static boolean error(String error, String tip) {
        entry(ChatColor.RED.toString() + ChatColor.UNDERLINE + "Fatal", error, tip);
        return false;
    }

    private static void entry(String type, String message, String tip) {
        if (!isCatching()) {
            String infoMessage = type + ":" + ChatColor.RESET + " " + message;

            if (tip != null) {
                infoMessage += ChatColor.DARK_GREEN + " TIP: " + ChatColor.GRAY + tip;
            }
            Messages.info(infoMessage);
        } else if (!ignore) {
            List<String> errors = fileErrors.get(currentFile);

            if (errors == null) {
                errors = new ArrayList<String>();
            }

            String errorMessage = "line " + String.format("%-5d", currentLine) + type + ": " + ChatColor.RESET + message;

            if (tip != null) {
                errorMessage += Files.NL + ChatColor.DARK_GREEN + "          TIP: " + ChatColor.GRAY + tip;
            }
            errors.add(errorMessage);

            fileErrors.put(currentFile, errors);
        }
    }
}
