package haveric.recipeManager;

import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManagerCommon.RMCChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorReporter {

    private static ErrorReporter instance;

    public static ErrorReporter getInstance() {
        if(instance == null){
            instance = new ErrorReporter();
        }
        return instance;
    }

    private HashMap<String, List<String>> fileErrors;
    private String currentFile;
    private int currentLine;
    private boolean ignore = false;



    /**
     * Starts catching reported errors and stores them in a list for later printing.<br>
     * This also resets file to null and line to 0
     */
    public void startCatching() {
        stopCatching();
        fileErrors = new HashMap<>();
    }

    /**
     * Stops catching the errors and ditches any caught errors so far!<br>
     * Calling this requires calling {@link #startCatching()} again to queue errors.
     */
    public void stopCatching() {
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
    public boolean isCatching() {
        return fileErrors != null;
    }

    /**
     * Gets the amount of queued errors.
     *
     * @return 0 if no errors, -1 if not catching at all
     */
    public int getCatchedAmount() {
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
     */
    public void print(String logFile) {
        if (!isCatching() || fileErrors.isEmpty()) {
            stopCatching();
            return;
        }

        int errors = fileErrors.size();
        String lastError;
        int lastErrorNum;
        int similarErrors;

        StringBuilder buffer;
        StringBuilder text = new StringBuilder(errors * 128);
        MessageSender.getInstance().info(text.toString());

        for (Map.Entry<String, List<String>> entry : fileErrors.entrySet()) {
            buffer = new StringBuilder();
            buffer.append(RMCChatColor.BOLD).append(RMCChatColor.AQUA).append("File: ").append(entry.getKey()).append(Files.NL);

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
                        buffer.append(RMCChatColor.RED).append("... and ").append(similarErrors).append(" more similar errors.").append(Files.NL);
                    }

                    lastErrorNum = 0;
                    similarErrors = 0;
                }

                buffer.append(RMCChatColor.WHITE).append(error).append(Files.NL);
                lastError = error.substring(10);
            }

            if (similarErrors > 0) {
                buffer.append(RMCChatColor.RED).append("... and ").append(similarErrors).append(" more similar errors.").append(Files.NL);
            }

            buffer.append(Files.NL);
            text.append(buffer);
            MessageSender.getInstance().info(buffer.toString());
        }

        text.append(Files.NL).append(Files.NL);

        if (logFile != null && Tools.saveTextToFile(RMCChatColor.stripColor(text.toString()), logFile)) {
            MessageSender.getInstance().info(RMCChatColor.YELLOW + "Error messages saved in '" + logFile + "'.");
        }

        stopCatching();
    }

    /**
     * Set the current file path/name - printed in queued errors.<br>
     * This also resets line to 0.
     *
     */
    public void setFile(String file) {
        currentFile = file;
        currentLine = 0;
    }

    /**
     * @return the current file the parser is at.
     */
    public String getFile() {
        return currentFile;
    }

    /**
     * Set the current line - printed in queued errors.<br>
     * This will be reset to 0 after calling
     *
     */
    public void setLine(int line) {
        currentLine = line;
    }

    /**
     * @return the current line the parser is at.
     */
    public int getLine() {
        return currentLine;
    }

    /**
     * This can be used to temporarily ignore any errors that are stored.<br>
     * <b>NOTE: Only works when catching errors, use with care.</b>
     *
     */
    public void setIgnoreErrors(boolean set) {
        if (isCatching()) {
            ignore = set;
        }
    }

    public void warning(String warning) {
        warning(warning, null);
    }

    public void warning(String warning, String tip) {
        entry(RMCChatColor.YELLOW.toString() + RMCChatColor.UNDERLINE + "Warning", warning, tip);
    }

    /**
     * Queue error or print it directly if queue was not started.
     *
     * @return always returns false, useful for quick returns
     */
    public boolean error(String error) {
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
    public boolean error(String error, String tip) {
        entry(RMCChatColor.RED.toString() + RMCChatColor.UNDERLINE + "Fatal", error, tip);
        return false;
    }

    private void entry(String type, String message, String tip) {
        if (!isCatching()) {
            String infoMessage = type + ":" + RMCChatColor.RESET + " " + message;

            if (tip != null) {
                infoMessage += RMCChatColor.DARK_GREEN + " TIP: " + RMCChatColor.GRAY + tip;
            }
            MessageSender.getInstance().info(infoMessage);
        } else if (!ignore) {
            List<String> errors = fileErrors.get(currentFile);

            if (errors == null) {
                errors = new ArrayList<>();
            }

            String errorMessage = "line " + String.format("%-5d", currentLine) + type + ": " + RMCChatColor.RESET + message;

            if (tip != null) {
                errorMessage += Files.NL + RMCChatColor.DARK_GREEN + "          TIP: " + RMCChatColor.GRAY + tip;
            }
            errors.add(errorMessage);

            fileErrors.put(currentFile, errors);
        }
    }
}
