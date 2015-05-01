package haveric.recipeManager.commands;

import haveric.recipeManager.RecipeManager;

import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;

public class ReloadCommand implements CommandCallable {

    @Override
    public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
        RecipeManager.getPlugin().reload();
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
        return Optional.of((Text) Texts.of("reload recipes/settings/books/etc."));
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.of((Text) Texts.of(""));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Texts.of("/<command>");
    }

}
