package haveric.recipeManager.commands;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;

public class ReloadCommand implements CommandExecutor {
    ConversationFactory reloadConversation;
    
    public ReloadCommand() {
        reloadConversation = new ConversationFactory(RecipeManager.getPlugin()).withLocalEcho(true)
                .withModality(false).withTimeout(60).addConversationAbandonedListener(abandonedEvent -> {
                    if (!abandonedEvent.gracefulExit()) {
                        abandonedEvent.getContext().getForWhom().sendRawMessage("Reload cancelled due to timeout.");
                    }
                }).withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return "Reloading in MC 1.12 - 1.14 is not guaranteed to function and may disrupt other plugins that also manage recipes.\n " +
                                "Are you sure you want to continue? Type \"yes\" to continue and anything else to cancel.";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        String cleanseInput = input.toLowerCase().trim();
                        if (cleanseInput.equals("yes")) {
                            return new MessagePrompt() {

                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return "As requested, performing reload.";
                                }

                                @Override
                                protected Prompt getNextPrompt(ConversationContext context) {
                                    Conversable whom = context.getForWhom();
                                    if (whom instanceof CommandSender) {
                                        RecipeManager.getPlugin().reload((CommandSender) whom, false, false);
                                    } else {
                                        RecipeManager.getPlugin().reload(Bukkit.getConsoleSender(), false, false);
                                    }
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            };
                        } else {
                            return new MessagePrompt() {
                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return "As requested, cancelling reload.";
                                }

                                @Override
                                protected Prompt getNextPrompt(ConversationContext context) {
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            };
                        }
                    }
                });
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Version.has1_15Support()) {
            if (sender instanceof Conversable) {
                reloadConversation.buildConversation((Conversable) sender).begin();
                return true;
            } else {
                MessageSender.getInstance().sendAndLog(sender, "WARNING: reload may cause instability or loss of recipe function in MC 1.12 or newer!");
            }
        }
        RecipeManager.getPlugin().reload(sender, false, false);

        return true;
    }
}
