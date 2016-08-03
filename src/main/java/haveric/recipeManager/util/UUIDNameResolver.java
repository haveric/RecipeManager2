package haveric.recipeManager.util;

import com.google.gson.GsonBuilder;
import haveric.recipeManager.RecipeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by psygate on 04.08.2016.
 * <p>
 * Provides a simple name/uuid cache. This could be a little more beautiful and performant, but it's still faster
 * than querying over the internet.
 */
public class UUIDNameResolver implements Listener {
    private static UUIDNameResolver instance;
    private File cacheFolder = new File(RecipeManager.getPlugin().getDataFolder(), "recipe_manager_cache");
    private GsonBuilder gsonBuilder = new GsonBuilder();

    private UUIDNameResolver() {
        if (!cacheFolder.exists() && !cacheFolder.mkdirs()) {
            throw new IllegalStateException("Cannot create cache folder. " + cacheFolder);
        } else if (!cacheFolder.isDirectory()) {
            throw new IllegalStateException("Cannot read cache folder. " + cacheFolder);
        }
    }

    public static UUIDNameResolver getInstance() {
        if (instance == null) {
            instance = new UUIDNameResolver();
        }

        return instance;
    }

    public boolean hasCachedUUID(String playername) {
        return Files.exists(cacheFolder.toPath().resolve(playername + ".cacheuuid"));
    }

    public UUID getUUID(String playername) {
        File userFile = new File(cacheFolder, playername);
        try (FileInputStream in = new FileInputStream(userFile)) {
            FileInputStream fis = new FileInputStream(userFile);
            byte[] data = new byte[(int) userFile.length()];
            if (fis.read(data) != data.length) {
                throw new IllegalStateException("Failed to read uuid file " + userFile + " all at once.");
            }
            fis.close();

            String str = new String(data, "UTF-8");

            return UUID.fromString(str);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent ev) {
        File userFile = new File(cacheFolder, ev.getPlayer().getName() + ".cacheduuid");

        if (!userFile.exists()) {
            try (FileOutputStream out = new FileOutputStream(userFile)) {
                out.write(ev.getPlayer().getUniqueId().toString().getBytes(Charset.forName("UTf-8")));
            } catch (FileNotFoundException e) {
                RecipeManager.getPlugin().getLogger().log(Level.WARNING, "Failed to persist info for "
                        + ev.getPlayer().getName()
                        + ", " + ev.getPlayer().getUniqueId());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
