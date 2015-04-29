package haveric.recipeManager.commands;

import haveric.recipeManager.Messages;
import haveric.recipeManager.RecipeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandMapping;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;

public class BaseCommand implements CommandCallable{

    @Override
    public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
        Messages.send(source, Texts.of(TextColors.YELLOW, "------ ", TextColors.WHITE, "Recipe Manager", TextColors.GRAY, " by haveric ", TextColors.YELLOW, "------"));
        
        CommandService service = RecipeManager.getGame().getCommandDispatcher();
        PluginContainer pluginContainer = RecipeManager.getPluginContainer();
        
        Set<CommandMapping> commands = service.getOwnedBy(pluginContainer);
        ArrayList<CommandMapping> commandsList = new ArrayList<CommandMapping>();
        commandsList.addAll(commands);
        Collections.sort(commandsList, new CommandSorter());
        
        for (CommandMapping command : commandsList) {
            CommandCallable callable = command.getCallable();

            Text usage = callable.getUsage(source);
            String primaryUsage = Texts.toPlain(usage).replace("<command>", command.getPrimaryAlias());
            
            Text description = callable.getShortDescription(source).get();
            
            if (callable.testPermission(source)) {
                Messages.send(source, Texts.of(TextColors.GOLD, primaryUsage, TextColors.RESET, " ", description));
            }
        }

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
        return Optional.of((Text) Texts.of("plugin info and available commands"));
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
