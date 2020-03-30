package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Sound;

import haveric.recipeManager.Files;

public class FlagSound extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.SOUND;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <sound> | [arguments]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Plays a sound at crafting location.",
            "Using this flag more than once will overwrite the previous flag.",
            "",
            "The &lt;sound&gt; argument must be a sound name, see " + Files.getNameIndexHashLink("sound"),
            "",
            "Optionally you can specify some arguments separated by | character:",
            "  volume <0.0 to 100.0> = (default 1.0) sound volume, if exceeds 1.0 it extends range, each 1.0 extends range by about 10 blocks.",
            "  pitch <0.0 to 4.0>    = (default 0.0) sound pitch value.",
            "  player                = (default not set) if set it will only play the sound to the crafter.",
            "You can specify these arguments in any order and they're completely optional.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} level_up",
            "{flag} wolf_howl | volume 5 // can be heard loudly at 50 blocks away",
            "{flag} portal_travel | player | volume 0.65 | pitch 3.33", };
    }


    private Sound sound = null;
    private float volume = 1;
    private float pitch = 0;
    private boolean onlyPlayer = false;

    public FlagSound() {
    }

    public FlagSound(FlagSound flag) {
        sound = flag.sound;
        volume = flag.volume;
        pitch = flag.pitch;
        onlyPlayer = flag.onlyPlayer;
    }

    @Override
    public FlagSound clone() {
        return new FlagSound((FlagSound) super.clone());
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound newSound) {
        Validate.notNull(newSound, "The sound argument can not be null!");

        sound = newSound;
    }

    /**
     * @return volume from 0.0 to 1.0
     */
    public float getVolume() {
        return volume;
    }

    /**
     * @param newVolume
     *            from 0.0 to 1.0
     */
    public void setVolume(float newVolume) {
        if (newVolume < 0 || newVolume > 4) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid 'volume' number range, must be between 0.0 and 1.0, trimmed.");

            volume = Math.min(Math.max(newVolume, 0.0f), 4.0f);
        } else {
            volume = newVolume;
        }
    }

    /**
     * @return pitch from 0.0 to 4.0
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * @param newPitch
     *            from 0.0 to 4.0
     */
    public void setPitch(float newPitch) {
        if (newPitch < 0 || newPitch > 4) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid 'pitch' number range, must be between 0.0 and 4.0, trimmed.");

            pitch = Math.min(Math.max(newPitch, 0.0f), 4.0f);
        } else {
            pitch = newPitch;
        }
    }

    public boolean isOnlyPlayer() {
        return onlyPlayer;
    }

    public void setOnlyPlayer(boolean newOnlyPlayer) {
        onlyPlayer = newOnlyPlayer;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        String[] split = value.toLowerCase().split("\\|");

        value = split[0].trim().toUpperCase();

        try {
            setSound(Sound.valueOf(value));
        } catch (IllegalArgumentException e) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid sound name: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for sounds list.");
            return false;
        }

        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                value = split[i].trim();

                if (value.equals("player")) {
                    onlyPlayer = true;
                } else if (value.startsWith("volume")) {
                    value = value.substring("volume".length()).trim();

                    if (value.isEmpty()) {
                        ErrorReporter.getInstance().error("Flag " + getFlagType() + " has 'volume' argument with number!", "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                        return false;
                    }

                    try {
                        setVolume(Float.parseFloat(value));
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid 'volume' argument float number: " + value, "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                        return false;
                    }
                } else if (value.startsWith("pitch")) {
                    value = value.substring("pitch".length()).trim();

                    if (value.isEmpty()) {
                        ErrorReporter.getInstance().error("Flag " + getFlagType() + " has 'pitch' argument with number!", "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                        return false;
                    }

                    try {
                        setPitch(Float.parseFloat(value));
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid 'pitch' argument number: " + value, "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                        return false;
                    }
                } else {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + value, "Maybe it's spelled wrong, check it in " + Files.FILE_INFO_FLAGS + " file.");
                }
            }
        }

        return true;
    }

    @Override
    public void onCrafted(Args a) {
        if (onlyPlayer) {
            if (!a.hasPlayer()) {
                a.addCustomReason("Needs player!");
                return;
            }
            Location location;
            if (a.hasLocation()) {
                location = a.location();
            } else {
                location = a.player().getLocation();
            }
            a.player().playSound(location, sound, volume, pitch);
        } else {
            if (!a.hasLocation()) {
                a.addCustomReason("Needs location!");
                return;
            }

            a.location().getWorld().playSound(a.location(), sound, volume, pitch);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "sound: " + sound.toString();
        toHash += "volume: " + volume;
        toHash += "pitch: " + pitch;
        toHash += "onlyPlayer: " + onlyPlayer;

        return toHash.hashCode();
    }
}
