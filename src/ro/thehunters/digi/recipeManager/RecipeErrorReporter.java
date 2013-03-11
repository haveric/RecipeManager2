package ro.thehunters.digi.recipeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

public class RecipeErrorReporter
{
    private static HashMap<String, List<String>> fileErrors;
    private static String                        currentFile;
    private static int                           currentLine;
    private static boolean                       ignore = false;
    
    /**
     * Starts catching reported errors and stores them in a list for later printing.<br>
     * This also resets file to null and line to 0
     */
    public static void startCatching()
    {
        stopCatching();
        fileErrors = new HashMap<String, List<String>>();
    }
    
    /**
     * Stops catching the errors and ditches any catched errors so far !<br>
     * Calling this requires calling {@link #startCatching()} again to queue errors.
     */
    public static void stopCatching()
    {
        fileErrors = null;
        currentFile = null;
        currentLine = 0;
        ignore = false;
    }
    
    /**
     * Check if class catched any errors.
     * 
     * @return false if no errors or no queue at all
     */
    public static boolean isCatching()
    {
        return (fileErrors != null && !fileErrors.isEmpty());
    }
    
    /**
     * Gets the amount of queued errors.
     * 
     * @return 0 if no errors or no queue at all
     */
    public static int getCatchedAmount()
    {
        return (fileErrors != null ? fileErrors.size() : 0);
    }
    
    /**
     * Print the queued errors (if any)
     * 
     * @param logFile
     */
    public static void print(String logFile)
    {
        if(fileErrors == null || fileErrors.isEmpty())
            return;
        
        int errors = fileErrors.size();
        String lastError;
        int lastErrorNum;
        int similarErrors;
        
        StringBuilder buffer;
        StringBuilder text = new StringBuilder(errors * 128).append(ChatColor.RED).append("There were ").append(errors).append(" errors while processing the files: ");
        Messages.info(text.toString());
        text.append(Files.NL).append(Files.NL);
        
        for(Entry<String, List<String>> entry : fileErrors.entrySet())
        {
            buffer = new StringBuilder();
            buffer.append(ChatColor.BOLD).append(ChatColor.BLUE).append("File: ").append(entry.getKey()).append(Files.NL);
            
            lastError = "";
            lastErrorNum = 0;
            similarErrors = 0;
            
            for(String error : entry.getValue())
            {
                if(error.startsWith(lastError, 10))
                {
                    if(++lastErrorNum > 3)
                    {
                        similarErrors++;
                        continue;
                    }
                }
                else
                {
                    if(similarErrors > 0)
                        buffer.append(ChatColor.RED).append("... and ").append(similarErrors).append(" more similar errors.").append(Files.NL);
                    
                    lastErrorNum = 0;
                    similarErrors = 0;
                }
                
                buffer.append(ChatColor.WHITE).append(error).append(Files.NL);
                lastError = error.substring(10);
            }
            
            if(similarErrors > 0)
                buffer.append(ChatColor.RED).append("... and ").append(similarErrors).append(" more similar errors.").append(Files.NL);
            
            buffer.append(Files.NL);
            text.append(buffer);
            Messages.info(buffer.toString());
        }
        
        text.append(Files.NL).append(Files.NL);
        
        if(logFile != null && Tools.saveTextToFile(ChatColor.stripColor(text.toString()), logFile))
        {
            Messages.info(ChatColor.YELLOW + "Apart from server.log, these errors have been saved in '" + logFile + "' as well.");
        }
        
        stopCatching();
    }
    
    /**
     * Set the current file path/name - printed in queued errors.<br>
     * This also resets line to 0.
     * 
     * @param line
     */
    public static void setFile(String file)
    {
        currentFile = file;
        currentLine = 0;
    }
    
    /**
     * Set the current line - printed in queued errors.<br>
     * This will be reset to 0 after calling {@link #setFile()}
     * 
     * @param line
     */
    public static void setLine(int line)
    {
        currentLine = line;
    }
    
    /**
     * This can be used to temporary ignore any errors that are stored.<br>
     * <b>NOTE: Only works when catching errors, use with care.</b>
     * 
     * @param set
     */
    protected static void setIgnoreErrors(boolean set)
    {
        if(isCatching())
            ignore = set;
    }
    
    protected static boolean getIgnoreErrors()
    {
        return ignore;
    }
    
    public static void warning(String warning)
    {
        warning(warning, null);
    }
    
    public static void warning(String warning, String tip)
    {
        entry(ChatColor.YELLOW + "Warning", warning, tip);
    }
    
    /**
     * Queue error or print it directly if queue was not started.
     * 
     * @param error
     * @return always returns false, useful for quick returns
     */
    public static boolean error(String error)
    {
        return error(error, null);
    }
    
    /**
     * Queue error or print it directly if queue was not started.
     * 
     * @param error
     *            the error message
     * @param tip
     *            optional tip, use null to avoid
     * 
     * @return always returns false, useful for quick returns
     */
    public static boolean error(String error, String tip)
    {
        entry(ChatColor.RED + "Fatal", error, tip);
        return false;
    }
    
    private static void entry(String type, String message, String tip)
    {
        if(fileErrors == null)
        {
            Messages.error(null, new Exception(), type + ": " + ChatColor.RESET + message + (tip != null ? ChatColor.DARK_GREEN + "; TIP: " + ChatColor.GRAY + tip : ""));
        }
        else if(!ignore)
        {
            List<String> errors = fileErrors.get(currentFile);
            
            if(errors == null)
                errors = new ArrayList<String>();
            
            errors.add("line " + String.format("%-5d", currentLine) + type + ": " + ChatColor.RESET + message + (tip != null ? Files.NL + ChatColor.DARK_GREEN + "          TIP: " + ChatColor.GRAY + tip : ""));
            
            fileErrors.put(currentFile, errors);
        }
    }
}
