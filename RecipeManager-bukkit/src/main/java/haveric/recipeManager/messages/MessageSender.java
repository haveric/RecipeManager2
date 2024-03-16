package haveric.recipeManager.messages;

import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.util.RMCUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class MessageSender {

    private static MessageSender instance;
    private static boolean colorConsole = false;

    public MessageSender() {

    }

    public static MessageSender getInstance() {
        if (instance == null) {
            instance = new MessageSender();
        }
        return instance;
    }

    public static void init(boolean isColorConsole) {
        colorConsole = isColorConsole;
    }

    /**
     * Used by plugin to log messages, shouldn't be used by other plugins unless really needed to send a message tagged by RecipeManager
     *
     * @param message
     */
    public void info(String message) {
        send(null, message);
    }

    /**
     * Sends a message to a player or console.<br>
     * Message supports &lt;color&gt; codes.
     *
     * @param sender
     * @param message
     */
    public void send(CommandSender sender, String message) {
        if (sender == null) {
            sender = Bukkit.getConsoleSender();
        }

        if (sender instanceof ConsoleCommandSender) {
            message = "[RecipeManager] " + message;
        }

        sender.sendMessage(RMCUtil.parseColors(message, (sender instanceof ConsoleCommandSender && !colorConsole)));
    }

    public void log(String message) {
        Bukkit.getLogger().fine(RMCUtil.parseColors("[RecipeManager] " + message, true));
    }

    public void sendAndLog(CommandSender sender, String message) {
        if (sender instanceof Player) {
            send(sender, message);
        }

        info(message);
    }

    public void error(CommandSender sender, Throwable thrown, String message) {
        String reportMessage = "If you're using the latest version you should report this error at: https://dev.bukkit.org/projects/recipemanager/issues/create";
        try {
            if (message == null) {
                message = "<red>" + thrown.getMessage();
            } else {
                message = "<red>" + message + " (" + thrown.getMessage() + ")";
            }

            sendAndLog(sender, message);
            notifyDebuggers(message);

            thrown.printStackTrace();

            message = RMCChatColor.LIGHT_PURPLE + reportMessage;
            info(message);
            notifyDebuggers(message);
        } catch (Throwable e) {
            System.out.print("Error while printing error!");
            System.out.print("Initial error:");
            thrown.printStackTrace();

            System.out.print("Error printing error:");
            e.printStackTrace();

            System.out.print(reportMessage);
        }
    }


    /**
     * Notifies all online operators and people having "recipemanager.debugger" permission
     *
     * @param message
     */
    private void notifyDebuggers(String message) {
        message = RMCChatColor.DARK_RED + "(RecipeManager debug) " + RMCChatColor.RESET + message;

        Collection<?> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Object p : onlinePlayers) {
            if (p instanceof Player) {
                Player player = (Player) p;
                if (player.hasPermission("recipemanager.debugger")) {
                    send(player, message);
                }
            }
        }
    }

    public void debug(String message) {
        StackTraceElement[] e = new Exception().getStackTrace();
        int i = 1;
        Bukkit.getConsoleSender().sendMessage(RMCUtil.parseColors(RMCChatColor.GREEN + "[DEBUG]" + RMCChatColor.AQUA + RMCChatColor.UNDERLINE + e[i].getFileName() + ":" + e[i].getLineNumber() + RMCChatColor.RESET + " " + RMCChatColor.RED + e[i].getMethodName() + "() " + RMCChatColor.WHITE + RMCUtil.parseColors(message, false), false));
    }
}
