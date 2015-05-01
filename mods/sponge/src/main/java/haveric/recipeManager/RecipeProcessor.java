package haveric.recipeManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;

public class RecipeProcessor implements Runnable{

    private CommandSource sender;
    private RecipeManager plugin;
    
    private int loaded;
    
    private volatile RecipeRegistrator registrator = null;
    private final List<String> fileList = new ArrayList<String>();
    
    private static String DIR_PLUGIN;
    private static String DIR_RECIPES;
    private static String FILE_ERRORLOG;
    private static final String[] COMMENTS = { "//", "#" };
    
    private Optional<Task> task;
    
    public RecipeProcessor(RecipeManager recipeManager) {
        DIR_PLUGIN = plugin.getSettings().getDefaultFolderPath() + File.separator;
        DIR_RECIPES = DIR_PLUGIN + "recipes" + File.separator;
        FILE_ERRORLOG = DIR_RECIPES + "errors.log";
        
        plugin = recipeManager;
    }
    
    public void reload(CommandSource source) {
        sender = source;
        
        boolean multithreading = false; // TODO: Settings.getInstance().getMultithreading()
        
        if (task.isPresent()) {
            task.get().cancel();
        }
        
        ErrorReporter.startCatching();
        
        if (multithreading) {
            task = plugin.getGame().getAsyncScheduler().runTask(plugin, this);
        } else {
            run();
        }
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        
        Messages.send(sender, "Loading all recipes...");
        try {
            File dir = new File(DIR_RECIPES);
            
            if (!dir.exists() && !dir.mkdirs()) {
                Messages.sendAndLog(sender, Texts.of(TextColors.RED, "Error: couldn't create directories: " + dir.getPath()));
            }
            
            // Scan for files
            analyzeDirectory(dir);
            
            if (fileList.isEmpty()) {
                Messages.sendAndLog(sender, Texts.of(TextColors.YELLOW, "No recipe files exist in the recipes folder."));
            } else {
                registrator = new RecipeRegistrator();
                
                long lastDisplay = System.currentTimeMillis();
                long time;
                int numFiles = fileList.size();
                int parsedFiles = 0;
                loaded = 0;
                
                // Start reading files...
                for (String name : fileList) {
                    try {
                        parseFile(DIR_RECIPES, name);
                        parsedFiles++;
                        time = System.currentTimeMillis();
                        
                        // display progress each second
                        if (time > lastDisplay + 500) {
                            Messages.sendAndLog(sender, "Recipes processed " + ((parsedFiles * 100) / numFiles) + "%...");
                            lastDisplay = time;
                        }
                    } catch (Throwable e) {
                        Messages.error(sender, e, "Error while reading recipe files!");
                    }
                }
                
                int errors = ErrorReporter.getCaughtAmount();
                
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
                    
                    Messages.sendAndLog(sender, Texts.of(TextColors.YELLOW, "Parsed " + loaded + " recipes from " + fileList.size() + " files in " + (System.currentTimeMillis() - start) / 1000.0 + " seconds, " + errors + senderMessage));
                    
                    ErrorReporter.print(FILE_ERRORLOG);
                } else {
                    Messages.sendAndLog(sender, "Parsed " + loaded + " recipes from " + fileList.size() + " files without errors, elapsed time " + (System.currentTimeMillis() - start) / 1000.0 + " seconds.");
                    
                    File log = new File(FILE_ERRORLOG);
                    
                    if (log.exists()) {
                        log.delete();
                    }
                }
                
                ErrorReporter.stopCatching();
            }
        } catch (Throwable e) {
            Messages.error(sender, e, "Code error while processing recipes");
        } finally {
            task = null;
            
            if (registrator == null) {
                return;
            }
            
            boolean multithreading = false; // TODO: Settings.getInstance().getMultithreading()
            
            if (multithreading) {
                plugin.getGame().getAsyncScheduler().runTask(RecipeManager.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        registrator.registerRecipesToServer(sender, start);
                    }
                });
            } else {
                registrator.registerRecipesToServer(sender, start);
            }
        }
    }
    
    private void analyzeDirectory(File dir) {
        File[] listOfFiles = dir.listFiles();
        
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    if (!file.getName().equalsIgnoreCase("disabled")) {
                        analyzeDirectory(file);
                    }
                } else {
                    int i = file.getName().lastIndexOf(".");
                    String ext;
                    if (i > 0) {
                        ext = file.getName().substring(i).toLowerCase();
                    } else {
                        ext = file.getName();
                    }
                    
                    if (!Files.FILE_RECIPE_EXTENSIONS.contains(ext)) {
                        continue;
                    }
                    
                    String fileName = file.getPath().replace(DIR_RECIPES, ""); // get the relative path+filename
                    fileList.add(fileName); // add to the processing file list
                }
            }
        }
    }
    
    private void parseFile(String root, String fileName) throws Throwable {
        
    }
}
