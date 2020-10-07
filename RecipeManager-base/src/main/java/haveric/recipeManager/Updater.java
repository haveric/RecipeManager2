package haveric.recipeManager;

import haveric.recipeManager.messages.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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
    private static String urlFiles;
    private static RecipeManager plugin;
    private static String pluginName;

    private static String latestVersion;
    private static String latestLink;

    private static BukkitTask task = null;

    private Updater() { } // Private constructor for utility class

    /**
     * Check for updates using your Curse account (with key)
     *
     * @param newProjectID The BukkitDev Project ID, found in the "About This Project" panel on the right-side of your project page.
     * @param newApiKey Your ServerMods API key, found at https://dev.bukkit.org/home/servermods-apikey/
     */
    public static void init(RecipeManager newPlugin, int newProjectID, String newApiKey) {
        plugin = newPlugin;
        urlFiles = plugin.getDescription().getWebsite() + "files";
        pluginName = plugin.getDescription().getName();
        latestVersion = null;
        latestLink = null;
        projectID = newProjectID;
        apiKey = newApiKey;
        stop();

        updateOnce(null); // Do one initial check

        int time = RecipeManager.getSettings().getUpdateCheckFrequency();

        if (time > 0) {
            time *= 60 * 60 * 20;
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> query(null), time, time);
        }
    }

    public static void updateOnce(final CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(RecipeManager.getPlugin(), () -> query(sender));
    }

    public static void stop() {
        if (task != null) {
            task.cancel();
        }
    }

    public static String getCurrentVersion() {
        return plugin.getDescription().getVersion();
    }

    public static String getLatestVersion() {
        return latestVersion;
    }

    /**
     * @return compare<br>
     *  1: Version is newer than the check<br>
     *  0: Same version as check<br>
     * -1: Check is newer than version
     * -2: An error occurred
     */
    public static int isVersionNewerThan(String version, String check) {
        // If version starts with RecipeManager, remove it
        if (version.startsWith("RecipeManager")) {
            version = version.substring("RecipeManager".length()).trim();
        }

        if (check.startsWith("RecipeManager")) {
            check = check.substring("RecipeManager".length()).trim();
        }

        // Remove initial "v". Ex: v1.0.0 -> 1.0.0
        if (version.startsWith("v")) {
            version = version.substring(1).trim();
        }

        if (check.startsWith("v")) {
            check = check.substring(1).trim();
        }

        if (version.equals(check)) {
            return 0;
        }

        String[] versionSplit = version.split("[ -]");
        String[] checkSplit = check.split("[ -]");

        int compare = -2;
        String[] currentArray = versionSplit[0].split("\\.");
        String[] latestArray = checkSplit[0].split("\\.");

        int shortest = currentArray.length;
        int latestLength = latestArray.length;
        if (latestLength < shortest) {
            shortest = latestLength;
        }

        for (int i = 0; i < shortest; i++) {
            int c = Integer.parseInt(currentArray[i]);
            int l = Integer.parseInt(latestArray[i]);

            if (c > l) {
                compare = 1;
                break;
            } else if (l > c) {
                compare = -1;
                break;
            }
        }

        if (compare == -2) {
            boolean versionHasBeta = versionSplit.length > 1;
            boolean checkHasBeta = checkSplit.length > 1;
            if (versionHasBeta && checkHasBeta) {
                String versionBeta = versionSplit[1];
                versionBeta = versionBeta.replace("dev", "0.");
                versionBeta = versionBeta.replace("alpha", "1.");
                versionBeta = versionBeta.replace("beta", "2.");

                String checkBeta = checkSplit[1];
                checkBeta = checkBeta.replace("dev", "0.");
                checkBeta = checkBeta.replace("alpha", "1.");
                checkBeta = checkBeta.replace("beta", "2.");

                try {
                    double versionDouble = Double.parseDouble(versionBeta);
                    double checkDouble = Double.parseDouble(checkBeta);
                    if (versionDouble > checkDouble) {
                        compare = 1;
                    } else if (versionDouble < checkDouble) {
                        compare = -1;
                    }
                } catch (NumberFormatException e) {
                    // Versions aren't doubles, fail quietly
                }
            } else if (versionHasBeta) {
                // Only beta status on version means it's newer
                compare = 1;
            } else if (checkHasBeta) {
                // Only beta status on check means version is older
                compare = -1;
            }
        }

        return compare;
    }

    /**
     * @return compare<br>
     *  1: Version is older than the check<br>
     *  0: Same version as check<br>
     * -1: Check is older than version
     * -2: An error occurred
     */
    public static int isVersionOlderThan(String version, String check) {
        int isOlder;
        int isNewer = isVersionNewerThan(version, check);

        // Flip newer to older
        if (isNewer == 1) {
            isOlder = -1;
        } else if (isNewer == -1) {
            isOlder = 1;
        } else {
            isOlder = isNewer;
        }

        return isOlder;
    }

    public static String getLatestLink() {
        return latestLink;
    }

    /**
     * Query the API to find the latest approved file's details.
     */
    public static void query(CommandSender sender) {
        if (RecipeManager.getSettings().getUpdateCheckEnabled()) {
            try {
                // Create the URL to query using the project's ID
                URL url = new URL(API_HOST + API_QUERY + projectID);

                // Open a connection and query the project
                URLConnection conn = url.openConnection();

                if (apiKey != null) {
                    // Add the API key to the request if present
                    conn.addRequestProperty("X-API-Key", apiKey);
                }

                // Add the user-agent to identify the program
                conn.addRequestProperty("User-Agent", pluginName);

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
                    latestVersion = (String) latest.get(API_NAME_VALUE);

                    // Get the version's link
                    latestLink = (String) latest.get(API_LINK_VALUE);
                }

                if (latestVersion == null) {
                    if (sender != null) { // send this message only if it's a requested update check
                        MessageSender.getInstance().sendAndLog(sender, "<red>Unable to check for updates, please check manually by visiting:<yellow> " + urlFiles);
                    } else {
                        return; // block the disable message
                    }
                } else {
                    String currentVersion = getCurrentVersion();

                    if (latestVersion != null) {
                        int compare = isVersionNewerThan(currentVersion, latestVersion);

                        if (compare == 0) {
                            if (sender != null) { // send this message only if it's a requested update check
                                MessageSender.getInstance().sendAndLog(sender, "<gray>Using the latest version: " + latestVersion);
                            } else {
                                return; // block the disable message
                            }
                        } else if (compare == -1) {
                            MessageSender.getInstance().sendAndLog(sender, "New version: <green>" + latestVersion + "<reset>! You're using <yellow>" + currentVersion);
                            MessageSender.getInstance().sendAndLog(sender, "Grab it at: <green>" + latestLink);
                        } else if (compare == 1) {
                            if (sender != null || !RecipeManager.getSettings().getUpdateCheckLogNewOnly()) {
                                MessageSender.getInstance().sendAndLog(sender, "<gray>You are using a newer version: <green>" + currentVersion + "<reset>. Latest on BukkitDev: <yellow>" + latestVersion);
                            }
                        }
                    }
                }

                if (sender == null && !RecipeManager.getSettings().getUpdateCheckLogNewOnly()) {
                    MessageSender.getInstance().sendAndLog(null, "<gray>You can disable this check from config.yml.");
                }
            } catch (IOException e) {
                MessageSender.getInstance().error(null, e, "Error while checking for updates");
                MessageSender.getInstance().info("You can disable the update checker in config.yml, but please report the error.");
            }
        }
    }
}