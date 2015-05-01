package haveric.recipeManager;

import java.util.Collection;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.source.ConsoleSource;

public class Messages {

    private static String MOD_URL = "https://github.com/haveric/RecipeManager2/issues";
    
    public static void send(CommandSource sender, String message) {
        send(sender, Texts.of(message));
    }
    
    public static void send(CommandSource sender, Text text) {
        if (sender == null) {
            sender = RecipeManager.getPlugin().getGame().getServer().getConsole();
        }
        
        if (sender instanceof ConsoleSource) {
            text = Texts.of("[RecipeManager] ", text);
        }
    
        sender.sendMessage(text);
    }
    
    public static void sendAndLog(CommandSource sender, String message) {
        sendAndLog(sender, Texts.of(message));
    }
    
    public static void sendAndLog(CommandSource sender, Text text) {
        if (sender instanceof Player) {
            send(sender, text);
        }
        
        send(null, text);
    }
    
    public static void error(CommandSource sender, Throwable thrown, String message) {
        error(sender, thrown, Texts.of(message));
    }
    
    public static void error(CommandSource sender, Throwable thrown, Text text) {
        String reportMessage = "If you're using the latest version you should report this error at: " + MOD_URL;
        
        try {
            if (text == null) {
                text = Texts.of(TextColors.RED, thrown.getMessage());
            } else {
                text = Texts.of(TextColors.RED, text, " (" + thrown.getMessage() + ")");
            }
            
            sendAndLog(sender, text);
            notifyDebuggers(text);
            
            thrown.printStackTrace();
            
            text = Texts.of(TextColors.LIGHT_PURPLE, reportMessage);
            send(null, text);
            notifyDebuggers(text);
        } catch (Throwable e) {
            System.out.print("Error while printing error!");
            System.out.print("Initial error:");
            thrown.printStackTrace();
            
            System.out.print("Error printing error:");
            e.printStackTrace();
            
            System.out.print(reportMessage);
        }
    }
    
    protected static void notifyDebuggers(Text text) {
        text = Texts.of(TextColors.DARK_RED, "(RecipeManager debug) ", TextColors.RESET, text);
        
        Collection<Player> onlinePlayers = RecipeManager.getPlugin().getGame().getServer().getOnlinePlayers();
        
        for (Player player : onlinePlayers) {
            if (player.hasPermission("recipemanager.debugger")) {
                send(player, text);
            }
        }
    }
}
