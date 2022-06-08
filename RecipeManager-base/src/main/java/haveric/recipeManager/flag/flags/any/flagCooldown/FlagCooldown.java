package haveric.recipeManager.flag.flags.any.flagCooldown;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipe;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlagCooldown extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.COOLDOWN;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <number>[suffix] | [arguments]",
            "{flag} false", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Sets a cooldown time for crafting a recipe or result.",
            "Once a recipe/result is used, the crafter can not craft it again for the specified amount of time.",
            "If set on a result, the result will be unavailable to the crafter for the cooldown time but the rest of results and the recipe will work as before.",
            "NOTE: cooldowns will be saved on server shutdown to maintain longer cooldowns.",
            "",
            "The <number> argument must be a number, by default it's seconds.",
            "The [suffix] argument defines what the <number> value is scaled in, values for suffix can be:",
            "  s  = for seconds (default)",
            "  m  = for minutes",
            "  h  = for hours",
            "  d  = for days",
            "You can also use float values like '0.5m' to get 30 seconds.",
            "",
            "Optionally you can add some arguments separated by | character, those being:",
            "  global            = make the cooldown global instead of per-player.",
            "  msg <text>        = overwrites the information message; false to hide; supports colors; use {time} variable to display the new cooldown time.",
            "  failmsg <text>    = overwrites the failure message; false to hide; supports colors; use {time} variable to display the remaining time.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 30",
            "{flag} 30s // exactly the same as the previous flag",
            "{flag} 1.75m | failmsg <red>Usable in: {time} // 1 minute and 45 seconds or 1 minute and 75% of a minute.",
            "{flag} .5h | global | failmsg <red>Someone used this recently, wait: {time} | msg <yellow>Cooldown time: {time} // half an hour", };
    }


    private final UUID globalUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private Map<UUID, MutableLong> cooldownTime;
    public static Map<String, Integer> cooldownFlagCounter;
    private String cooldownFlagId = "";

    private int cooldown;
    private boolean global = false;
    private String failMessage;
    private String craftMessage;

    public FlagCooldown() {
        cooldownTime = new HashMap<>();
        cooldownFlagCounter = new HashMap<>();
    }

    public FlagCooldown(FlagCooldown flag) {
        super(flag);

        cooldown = flag.cooldown;
        global = flag.global;
        failMessage = flag.failMessage;
        craftMessage = flag.craftMessage;

        if (flag.cooldownTime == null) {
            cooldownTime = new HashMap<>();
        } else {
            cooldownTime = new HashMap<>(flag.cooldownTime);
        }

        cooldownFlagCounter = new HashMap<>();
        cooldownFlagId = flag.cooldownFlagId;
    }

    @Override
    public FlagCooldown clone() {
        return new FlagCooldown((FlagCooldown) super.clone());
    }

    /**
     * @return cooldown time in seconds
     */
    public int getCooldownTime() {
        return cooldown;
    }

    /**
     * @param seconds
     *            Set the cooldown time in seconds
     */
    public void setCooldownTime(int seconds) {
        cooldown = seconds;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean newGlobal) {
        global = newGlobal;
    }

    /**
     * Gets the cooldown time in seconds for specified player or for global if null is specified and global is enabled.
     *
     * @param playerUUID
     *            if global is enabled this value is ignored, can be null.
     * @return -1 if there is a problem otherwise 0 or more specifies seconds left
     */
    public long getTimeLeftFor(UUID playerUUID) {
        if (global) {
            playerUUID = globalUUID;
        } else if (playerUUID == null) {
            return -1;
        }

        MutableLong get = cooldownTime.get(playerUUID);
        long time = (int) (System.currentTimeMillis() / 1000);

        if (get == null || time >= get.intValue()) {
            return 0;
        }

        return get.intValue() - time;
    }

    /**
     * Gets the cooldown time as formatted string for specified player or for global if null is specified and global is enabled.
     *
     * @param playerUUID
     *            if global is enabled this value is ignored, can be null.
     * @return '#h #m #s' format of remaining time.
     */
    public String getTimeLeftStringFor(UUID playerUUID) {
        return timeToString(getTimeLeftFor(playerUUID));
    }

    private String timeToString(long time) {
        String timeString = "";
        if (time < 1) {
            timeString = "0s";
        } else {
            long seconds = time % 60;
            long minutes = time % 3600 / 60;
            long hours = time % 84600 / 3600;
            long days = time / 84600;

            if (days > 0) {
                timeString += days + "d ";
            }

            if (hours > 0) {
                timeString += hours + "h ";
            }

            if (minutes > 0) {
                timeString += minutes + "m ";
            }

            if (seconds > 0) {
                timeString += seconds + "s";
            }

            timeString = timeString.trim();
        }

        return timeString;
    }

    /**
     * Checks countdown time for player or globally if null is supplied and global is enabled.
     *
     * @param playerUUID
     *            if global is enabled this value is ignored, can be null.
     * @return true if has a cooldown or can't be used
     */
    public boolean hasCooldown(UUID playerUUID) {
        if (global) {
            playerUUID = globalUUID;
        } else if (playerUUID == null) {
            return true;
        }

        MutableLong get = cooldownTime.get(playerUUID);
        if (get == null) {
            return false;
        }

        return (System.currentTimeMillis() / 1000) < get.intValue();
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String message) {
        failMessage = message;
    }

    public String getCraftMessage() {
        return craftMessage;
    }

    public void setCraftMessage(String message) {
        craftMessage = message;
    }

    @Override
    public void onRegistered() {
        BaseRecipe recipe = getRecipeDeep();
        if (recipe != null) {
            String cooldownId = recipe.getName().replaceAll(":", "");

            int flagCounter = cooldownFlagCounter.getOrDefault(cooldownId, 0);
            flagCounter += 1;
            cooldownFlagCounter.put(cooldownId, flagCounter);
            cooldownId += "-" + flagCounter;

            cooldownFlagId = cooldownId;

            cooldownTime.clear();
            CooldownData cooldownData = Cooldowns.get(cooldownFlagId);
            if (cooldownData.hasCooldowns()) {
                cooldownTime.putAll(cooldownData.getCooldowns());
            }
        }
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.split("\\|");

        value = split[0].trim();
        float multiplier = 0;
        float time;

        switch (value.charAt(value.length() - 1)) {
            case 'm':
                multiplier = 60.0f;
                break;
            case 'h':
                multiplier = 3600.0f;
                break;
            case 'd':
                multiplier = 86400.0f;
                break;
            case 's':
                multiplier = 1;
                break;
            default:
                break;
        }

        if (multiplier > 0) {
            value = value.substring(0, value.length() - 1).trim();
        }

        if (value.length() > String.valueOf(Float.MAX_VALUE).length()) {
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has cooldown value that is too long: " + value, "Value for float numbers can be between " + RMCUtil.printNumber(Float.MIN_VALUE) + " and " + RMCUtil.printNumber(Float.MAX_VALUE) + ".");
        }

        try {
            time = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid number: " + value);
        }

        if (multiplier > 0) {
            cooldown = Math.round(multiplier * time);
        } else {
            cooldown = Math.round(time);
        }

        if (time <= 0.0f) {
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag must have cooldown value more than 0!");
        }

        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                value = split[i].trim();

                if (value.equalsIgnoreCase("global")) {
                    global = true;
                } else if (value.toLowerCase().startsWith("msg")) {
                    craftMessage = RMCUtil.trimExactQuotes(value.substring("msg".length()));
                } else if (value.toLowerCase().startsWith("failmsg")) {
                    failMessage = RMCUtil.trimExactQuotes(value.substring("failmsg".length()));
                }
            }
        }

        return true;
    }

    @Override
    public void onCheck(Args a) {
        if (hasCooldown(a.playerUUID())) {
            String message;
            if (global) {
                message = "flag.cooldown.fail.global";
            } else {
                message = "flag.cooldown.fail.perplayer";
            }
            a.addReason(message, failMessage, "{time}", getTimeLeftStringFor(a.playerUUID()));
        }
    }

    @Override
    public void onCrafted(Args a) {
        if (!global && !a.hasPlayerUUID()) {
            return;
        }

        UUID playerUUID;
        if (global) {
            playerUUID = globalUUID;
        } else {
            playerUUID = a.playerUUID();
        }

        MutableLong get = cooldownTime.get(playerUUID);
        long diff = (System.currentTimeMillis() / 1000) + cooldown;

        if (get == null) {
            get = new MutableLong(diff);
            cooldownTime.put(playerUUID, get);
        } else {
            get.setValue(diff);
        }

        CooldownData cooldownData = Cooldowns.get(cooldownFlagId);
        cooldownData.setCooldown(playerUUID, diff);

        String message;
        if (global) {
            message = "flag.cooldown.set.global";
        } else {
            message = "flag.cooldown.set.perplayer";
        }
        a.addEffect(message, craftMessage, "{time}", timeToString(cooldown));
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "cooldown: " + cooldown;
        toHash += "global: " + global;
        toHash += "failMessage: " + failMessage;
        toHash += "craftMessage: " + craftMessage;

        return toHash.hashCode();
    }
}
