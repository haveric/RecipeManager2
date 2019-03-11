package haveric.recipeManager.messages;

import org.bukkit.command.CommandSender;

public abstract class AbstractMessageSender {

    public abstract void info(String message);

    public abstract void log(String message);

    public abstract void send(CommandSender sender, String message);

    public abstract void sendAndLog(CommandSender sender, String message);

    public abstract void error(CommandSender sender, Throwable thrown, String message);

    public abstract void debug(String message);
}
