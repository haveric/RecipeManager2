package haveric.recipeManager.messages;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import haveric.recipeManager.Settings;
import haveric.recipeManager.tools.Tools;

public class SoundNotifier {


    public static void sendDenySound(Player player, Location location) {
        Sound soundBlockNoteBass = Tools.getSound("BLOCK_NOTE_BASS");
        sendSound(player, location, soundBlockNoteBass, 0.8f, 4, Settings.getInstance().getSoundsFailedClick());
    }

    public static void sendFailSound(Player player, Location location) {
        Sound soundBlockNotePling = Tools.getSound("BLOCK_NOTE_PLING");
        sendSound(player, location, soundBlockNotePling, 0.8f, 4, Settings.getInstance().getSoundsFailed());
    }

    public static void sendRepairSound(Player player, Location location) {
        Sound soundBlockAnvilUse = Tools.getSound("BLOCK_ANVIL_USE");
        sendSound(player, location, soundBlockAnvilUse, 0.8f, 4, Settings.getInstance().getSoundsRepair());
    }

    private static void sendSound(Player player, Location location, Sound sound, float volume, float pitch, boolean condition) {
        if (player != null && condition) {
            if (location == null) {
                location = player.getLocation();
            }
            player.playSound(location, sound, volume, pitch);
        }
    }
}
