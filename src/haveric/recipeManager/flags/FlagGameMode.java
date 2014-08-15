package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Messages;
import haveric.recipeManager.tools.Tools;

import java.util.EnumSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.GameMode;


public class FlagGameMode extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.GAMEMODE;

        A = new String[] { "{flag} <game mode>", "{flag} <game mode> | [message]", "{flag} false", };

        D = new String[] { "Requires the crafter to be in a specific game mode.", "Using this flag more than once will overwrite the previous ones.", "", "Values for <game mode> can be: c or creative, a or adventure, s or survival", "", "Optionally you can specify a failure message, should be short because it prints in the display result.", "Additionally you can use the following variables in the message:", "  {playergm}  = player's game mode (which is not allowed)", "  {gamemodes}  = list of required game modes", };

        E = new String[] { "{flag} creative // only creative", "{flag} s // only survival", "{flag} a,s // only adventure and survival", "{flag} false // disable flag, allow all gamemodes", };
    }

    // Flag code

    private Set<GameMode> gameModes = EnumSet.noneOf(GameMode.class);
    private String failMessage;

    public FlagGameMode() {
    }

    public FlagGameMode(FlagGameMode flag) {
        gameModes.addAll(flag.gameModes);
        failMessage = flag.failMessage;
    }

    @Override
    public FlagGameMode clone() {
        return new FlagGameMode(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public Set<GameMode> getGameModes() {
        return gameModes;
    }

    public void setGameModes(EnumSet<GameMode> set) {
        Validate.notNull(set, "The 'set' argument must not be null!");

        gameModes = set;
    }

    public void addGameMode(GameMode gameMode) {
        gameModes.add(gameMode);
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String newFailMessage) {
        failMessage = newFailMessage;
    }

    @Override
    protected boolean onParse(String value) {
        String[] split = value.split("\\|");

        if (split.length > 1) {
            setFailMessage(split[1].trim());
        }

        split = split[0].toLowerCase().split(",");

        for (String arg : split) {
            arg = arg.trim();

            switch (arg.charAt(0)) {
                case 'a':
                    addGameMode(GameMode.ADVENTURE);
                    break;

                case 'c':
                    addGameMode(GameMode.CREATIVE);
                    break;

                case 's':
                    addGameMode(GameMode.SURVIVAL);
                    break;

                default:
                    try {
                        addGameMode(GameMode.valueOf(arg));
                    } catch (IllegalArgumentException e) {
                        return ErrorReporter.error("Flag " + getType() + " has unknown game mode: " + value);
                    }
            }
        }

        return true;
    }

    @Override
    protected void onCrafted(Args a) {
        if (!a.hasPlayer()) {
            a.addCustomReason("Need a player!");
            return;
        }

        GameMode gm = a.player().getGameMode();

        if (!getGameModes().contains(gm)) {
            a.addReason(Messages.FLAG_GAMEMODE, failMessage, "{playergm}", gm.toString().toLowerCase(), "{gamemodes}", Tools.collectionToString(getGameModes()));
        }
    }

    /*
     * @Override public List<String> information() { List<String> list = new ArrayList<String>(1);
     *
     * list.add(Messages.FLAG_GAMEMODE.get("{gamemodes}", Tools.collectionToString(getGameModes())));
     *
     * return list; }
     */
}
