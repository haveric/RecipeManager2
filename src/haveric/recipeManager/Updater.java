package haveric.recipeManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Updater {
 // The project's unique ID
    private static int projectID;

    // An optional API key to use, will be null if not submitted
    private static String apiKey;

    // Keys for extracting file information from JSON response
    private static final String API_NAME_VALUE = "name";
    private static final String API_LINK_VALUE = "downloadUrl";

    // Static information for querying the API
    private static final String API_QUERY = "/servermods/files?projectIds=";
    private static final String API_HOST = "https://api.curseforge.com";

    // Only used to link the user to manually download files
    private static final String URL_PLUGIN = "http://dev.bukkit.org/bukkit-mods/recipemanager/";
    private static final String URL_FILES = URL_PLUGIN + "files";

    private static String newVersion;
    private static String newLink;
    private static int taskId = -1;

    private Updater() { } // Private constructor for utility class

    /**
     * Check for updates using your Curse account (with key)
     *
     * @param projectID The BukkitDev Project ID, found in the "Facts" panel on the right-side of your project page.
     * @param apiKey Your ServerMods API key, found at https://dev.bukkit.org/home/servermods-apikey/
     */
    public static void init(int newProjectID, String newApiKey) {
        newVersion = null;
        newLink = null;
        projectID = newProjectID;
        apiKey = newApiKey;
        stop();

        query(null); // Do one initial check

        int time = RecipeManager.getSettings().UPDATE_CHECK_FREQUENCY;

        if (time > 0) {
            time *= 60 * 60 * 20;
            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(RecipeManager.getPlugin(), new Runnable() {
                @Override public void run() {
                    query(null);
                }

            }, time, time);
        }
    }

    public static void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public static String getNewVersion() {
        return newVersion;
    }

    public static String getNewLink() {
        return newLink;
    }

    /**
     * Query the API to find the latest approved file's details.
     */
    public static void query(CommandSender sender) {
        if (RecipeManager.settings.UPDATE_CHECK_ENABLED) {
            URL url = null;

            try {
                // Create the URL to query using the project's ID
                url = new URL(API_HOST + API_QUERY + projectID);

                // Open a connection and query the project
                URLConnection conn = url.openConnection();

                if (apiKey != null) {
                    // Add the API key to the request if present
                    conn.addRequestProperty("X-API-Key", apiKey);
                }

                // Add the user-agent to identify the program
                conn.addRequestProperty("User-Agent", "RecipeManager");

                // Read the response of the query
                // The response will be in a JSON format, so only reading one line is necessary.
                final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = reader.readLine();

                // Parse the array of files from the query's response
                JSONArray array = (JSONArray) JSONValue.parse(response);

                if (array.size() > 0) {
                    // Get the newest file's details
                    JSONObject latest = (JSONObject) array.get(array.size() - 1);

                    // Get the version's title
                    String versionName = (String) latest.get(API_NAME_VALUE);
                    newVersion = versionName.substring(1).trim();

                    // Get the version's link
                    newLink = (String) latest.get(API_LINK_VALUE);
                }

                if (newVersion == null) {
                    if (sender != null) { // send this message only if it's a requested update check
                        Messages.sendAndLog(sender, "<red>Unable to check for updates, please check manually by visiting:<yellow> " + URL_FILES);
                    } else {
                        return; // block the disable message
                    }
                } else {
                    String currentVersion = RecipeManager.getPlugin().getDescription().getVersion().trim();

                    if (currentVersion.equalsIgnoreCase(newVersion)) {
                        if (sender != null) { // send this message only if it's a requested update check
                            Messages.sendAndLog(sender, "<gray>Using the latest version: " + newVersion);
                        } else {
                            return; // block the disable message
                        }
                    } else {
                        Messages.sendAndLog(sender, "New version: <green>" + newVersion + "<reset> ! You're using <yellow>" + currentVersion);
                        Messages.sendAndLog(sender, "Grab it at: <green>" + newLink);
                    }
                }

                if (sender == null) {
                    Messages.sendAndLog(sender, "<gray>You can disable this check from config.yml.");
                }
            } catch (MalformedURLException e) {
                Messages.error(null, e, "Error while checking for updates");
                Messages.info("You can disable the update checker in config.yml, but please report the error.");
            } catch (IOException e) {
                // There was an error reading the query
                Messages.error(null, e, "Error while checking for updates");
                Messages.info("You can disable the update checker in config.yml, but please report the error.");
            }
        }
    }
}