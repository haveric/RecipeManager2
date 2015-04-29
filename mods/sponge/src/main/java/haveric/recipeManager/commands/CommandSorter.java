package haveric.recipeManager.commands;

import java.util.Comparator;

import org.spongepowered.api.util.command.CommandMapping;

public class CommandSorter implements Comparator<CommandMapping>{

    @Override
    public int compare(CommandMapping o1, CommandMapping o2) {
        return o1.getPrimaryAlias().compareTo(o2.getPrimaryAlias());
    }

}
