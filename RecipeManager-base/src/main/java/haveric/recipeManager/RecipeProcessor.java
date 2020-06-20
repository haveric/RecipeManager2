package haveric.recipeManager;

import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.recipes.RecipeFileParser;
import haveric.recipeManager.common.RMCChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes all recipe files and updates main Recipes class once done.
 */
public class RecipeProcessor implements Runnable {
    private final CommandSender sender;
    private final boolean check;

    // Storage
    private static volatile RecipeRegistrator registrator = null;
    private final List<String> fileList = new ArrayList<>();

    // Constants
    private static String DIR_RECIPES;
    private static String FILE_ERRORLOG;

    private static BukkitTask task;

    public static void reload(CommandSender sender, boolean check) {
        DIR_RECIPES = RecipeManager.getPlugin().getDataFolder() + File.separator + "recipes" + File.separator;
        FILE_ERRORLOG = DIR_RECIPES + "errors.log";
        new RecipeProcessor(sender, check);
    }

    /**
     * Used for Testing Only
     */
    public static void reload(CommandSender sender, boolean check, String newDirectory, String errorDirectory) {
        DIR_RECIPES = newDirectory;
        FILE_ERRORLOG = errorDirectory + File.separator + "errors.log";
        new RecipeProcessor(sender, check);
    }

    private RecipeProcessor(CommandSender newSender, boolean newCheck) {
        sender = newSender;
        check = newCheck;

        if (task != null) {
            task.cancel();
        }

        ErrorReporter.getInstance().startCatching();

        if (RecipeManager.getSettings().getMultithreading()) {
            task = Bukkit.getScheduler().runTaskAsynchronously(RecipeManager.getPlugin(), this);
        } else {
            run();
        }
    }

    public void run() {
        final long start = System.currentTimeMillis();

        try {
            String message;
            if (check) {
                message = "Checking";
            } else {
                message = "Loading";
            }
            MessageSender.getInstance().sendAndLog(sender, message + " all recipes...");

            File dir = new File(DIR_RECIPES);

            if (!dir.exists() && !dir.mkdirs()) {
                MessageSender.getInstance().sendAndLog(sender, RMCChatColor.RED + "Error: couldn't create directories: " + dir.getPath());
            }

            // Scan for files
            analyzeDirectory(dir);

            if (fileList.isEmpty()) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>No recipe files exist in the recipes folder.");
            } else {
                registrator = new RecipeRegistrator();

                long lastDisplay = System.currentTimeMillis();
                long time;
                int numFiles = fileList.size();
                int parsedFiles = 0;

                RecipeFileParser recipeParser = new RecipeFileParser(registrator);

                // Start reading files...
                for (String name : fileList) {
                    try {
                        recipeParser.parseFile(DIR_RECIPES, name);
                        parsedFiles++;
                        time = System.currentTimeMillis();

                        // display progress each second
                        if (time > lastDisplay + 500) {
                            MessageSender.getInstance().sendAndLog(sender, "Recipes processed " + (parsedFiles / numFiles * 100) + "%...");
                            lastDisplay = time;
                        }
                    } catch (Throwable e) {
                        MessageSender.getInstance().error(sender, e, "Error while reading recipe files!");
                    }
                }

                int errors = ErrorReporter.getInstance().getCatchedAmount();

                String parsed;
                if (check) {
                    parsed = "Checked";
                } else {
                    parsed = "Parsed";
                }

                int loaded = registrator.getNumQueuedRecipes();

                String recipesString;
                if (loaded > 1) {
                    recipesString = " recipes";
                } else {
                    recipesString = " recipe";
                }

                String filesString;
                if (fileList.size() > 1) {
                    filesString = " files";
                } else {
                    filesString = " file";
                }

                if (errors > 0) {
                    String senderMessage;
                    if (errors == 1) {
                        senderMessage = " error was found";
                    } else {
                        senderMessage = " errors were found";
                    }

                    if (sender == null) {
                        senderMessage += ", see below:";
                    } else {
                        senderMessage += ", see console.";
                    }

                    MessageSender.getInstance().sendAndLog(sender, RMCChatColor.YELLOW + parsed + " " + loaded + recipesString + " from " + fileList.size() + filesString + " in " + (System.currentTimeMillis() - start) / 1000.0 + " seconds, " + errors + senderMessage);

                    ErrorReporter.getInstance().print(FILE_ERRORLOG);
                } else {
                    MessageSender.getInstance().sendAndLog(sender, parsed + " " + loaded + recipesString + " from " + fileList.size() + filesString + " without errors, elapsed time " + (System.currentTimeMillis() - start) / 1000.0 + " seconds.");

                    File log = new File(FILE_ERRORLOG);

                    if (log.exists()) {
                        log.delete();
                    }
                }

                ErrorReporter.getInstance().stopCatching();
            }
        } catch (Throwable e) {
            MessageSender.getInstance().error(sender, e, "Code error while processing recipes");
        } finally {
            task = null;

            if (!check && registrator != null) {
                // Calling registerRecipesToServer() in main thread...
                if (RecipeManager.getSettings().getMultithreading()) {
                    new BukkitRunnable() {
                        public void run() {
                            registrator.registerRecipesToServer(sender, start);
                        }
                    }.runTask(RecipeManager.getPlugin());
                } else {
                    registrator.registerRecipesToServer(sender, start);
                }
            }
        }
    }

    private void analyzeDirectory(File dir) {
        if (dir.isFile()) {
            addFile(dir);
        } else {
            File[] listOfFiles = dir.listFiles();

            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isDirectory()) {
                        if (!file.getName().equalsIgnoreCase("disabled")) {
                            analyzeDirectory(file);
                        }
                    } else {
                        addFile(file);
                    }
                }
            }
        }
    }

    private void addFile(File file) {
        int i = file.getName().lastIndexOf('.');
        String ext;
        if (i > 0) {
            ext = file.getName().substring(i).toLowerCase();
        } else {
            ext = file.getName();
        }

        if (Files.FILE_RECIPE_EXTENSIONS.contains(ext)) {
            String fileName = file.getPath().replace(DIR_RECIPES, ""); // get the relative path+filename
            fileList.add(fileName); // add to the processing file list
        }
    }

    public static RecipeRegistrator getRegistrator() {
        return registrator;
    }
}
