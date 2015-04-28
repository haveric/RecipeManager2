package haveric.recipeManager.commands;

import haveric.recipeManager.Messages;

import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;

public class BaseCommand implements CommandCallable{

    @Override
    public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
        source.sendMessage(Texts.of(TextColors.YELLOW, "------ ", TextColors.WHITE, "Recipe Manager", TextColors.GRAY, " by haveric ", TextColors.YELLOW, "------"));
        return Optional.of(CommandResult.success());
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return null;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        Optional.of((Text) Texts.of(""));
        return null;
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.of((Text) Texts.of(""));
    }

    @Override
    public Text getUsage(CommandSource source) {
        // TODO Auto-generated method stub
        return null;
    }

}
