package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class FlagCommand extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.COMMAND;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <text or false>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Executes the command when recipe is successful.",
            "This flag can be used more than once to add more commands to the list.",
            "",
            "Commands are executed server-side, if you add / prefix it will execute the command on the crafter.",
            "",
            "You can also use these variables:",
            "  {player}         = crafter's name or '(nobody)' if not available",
            "  {playerdisplay}  = crafter's display name or '(nobody)' if not available",
            "  {result}         = the result item name or '(nothing)' if recipe failed.",
            "  {recipename}     = recipe's custom or autogenerated name or '(unknown)' if not available",
            "  {recipetype}     = recipe type or '(unknown)' if not available",
            "  {inventorytype}  = inventory type or '(unknown)' if not available",
            "  {world}          = world name of event location or '(unknown)' if not available",
            "  {x}              = event location's X coord or '(?)' if not available",
            "  {y}              = event location's Y coord or '(?)' if not available",
            "  {z}              = event location's Z coord or '(?)' if not available",
            "    Relative positions are supported: {x-1},{y+7},{z+12}",
            "  {rand #1-#2}     = output a random integer between #1 and #2. Example: {rand 5-10} will output an integer from 5-10",
            "  {rand #1-#2, #3} = output a random number between #1 and #2, with decimal places of #3. Example: {rand 1.5-2.5, 2} will output a number from 1.50 to 2.50",
            "  {rand n}         = reuse a random output, where n is the nth {rand} in a recipe used excluding this format", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} /say I crafted {result}!",
            "{flag} kick {player}", };
    }


    private List<String> commands = new ArrayList<>();

    public FlagCommand() {
    }

    public FlagCommand(FlagCommand flag) {
        super(flag);
        commands.addAll(flag.commands);
    }

    @Override
    public FlagCommand clone() {
        return new FlagCommand((FlagCommand) super.clone());
    }

    /**
     * @return the command list
     */
    public List<String> getCommands() {
        return commands;
    }

    /**
     * Set the command list.<br>
     * You can use null to remove the entire flag.
     *
     * @param newCommands
     */
    public void setCommands(List<String> newCommands) {
        if (newCommands == null) {
            remove();
        } else {
            commands = newCommands;
        }
    }

    /**
     * Adds a command to the list.<br>
     * You can use null, "false" or "remove" to remove the entire flag.
     *
     * @param command
     */
    public void addCommand(String command) {
        if (command == null || command.equalsIgnoreCase("false") || command.equalsIgnoreCase("remove")) {
            remove();
        } else {
            commands.add(command);
        }
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        addCommand(value);

        return true;
    }

    @Override
    public void onCrafted(Args a) {
        for (String command : commands) {
            if (command.charAt(0) == '/') {
                if (!a.hasPlayer()) {
                    a.addCustomReason("Need player!");
                    return;
                }

                break;
            }
        }

        for (String command : commands) {
            command = a.parseVariables(command);

            if (command.charAt(0) == '/') {
                a.player().chat(command);
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        boolean first = true;
        for (String command : commands) {
            if (first) {
                first = false;
            } else {
                toHash += ",";
            }
            toHash += command;
        }

        return toHash.hashCode();
    }
}
