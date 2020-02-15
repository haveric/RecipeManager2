package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.GameMode;

import java.util.EnumSet;
import java.util.Set;

public class FlagGameMode extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.GAMEMODE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <game mode>",
            "{flag} <game mode> | [message]",
            "{flag} false", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Requires the crafter to be in a specific game mode.",
            "Using this flag more than once will overwrite the previous ones.",
            "",
            "Values for <game mode> can be: c or creative, a or adventure, s or survival",
            "",
            "Optionally you can specify a failure message, should be short because it prints in the display result.",
            "Additionally you can use the following variables in the message:",
            "  {playergm}  = player's game mode (which is not allowed)",
            "  {gamemodes}  = list of required game modes", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} creative // only creative",
            "{flag} s // only survival",
            "{flag} a,s // only adventure and survival",
            "{flag} false // disable flag, allow all gamemodes", };
    }


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
        return new FlagGameMode((FlagGameMode) super.clone());
    }

    public Set<GameMode> getGameModes() {
        Set<GameMode> clone = EnumSet.noneOf(GameMode.class);
        clone.addAll(gameModes);
        return clone;
    }

    public void clearGameModes() {
        gameModes.clear();
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
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        String[] split = value.split("\\|");

        if (split.length > 1) {
            setFailMessage(RMCUtil.trimExactQuotes(split[1]));
        }

        split = split[0].toLowerCase().split(",");

        // Clear gamemodes before parsing to prevent multiple declarations
        clearGameModes();

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
                        return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has unknown game mode: " + value);
                    }
            }
        }

        return true;
    }

    @Override
    public void onCrafted(Args a) {
        if (!a.hasPlayer()) {
            a.addCustomReason("Need a player!");
            return;
        }

        GameMode gm = a.player().getGameMode();

        if (!gameModes.contains(gm)) {
            a.addReason("flag.gamemode", failMessage, "{playergm}", gm.toString().toLowerCase(), "{gamemodes}", RMCUtil.collectionToString(gameModes));
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        for (GameMode gameMode : gameModes) {
            toHash += gameMode.toString();
        }

        toHash += failMessage;

        return toHash.hashCode();
    }
}
