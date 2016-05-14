package haveric.recipeManager.messages;

import org.bukkit.command.CommandSender;

public class TestMessageSender extends AbstractMessageSender {

    private static AbstractMessageSender instance;

    private TestMessageSender() {

    }

    public static AbstractMessageSender getInstance() {
        if(instance == null) {
            instance = new TestMessageSender();
        }
        return instance;
    }

    public void info(String message){
        testOutput("info", message);
    }

    public void log(String message){
        testOutput("log", message);
    }

    public void send(CommandSender sender, String message){
        testOutput("send", message);
    }

    public void sendAndLog(CommandSender sender, String message){
        testOutput("sendAndLog", message);
    }

    public void error(CommandSender sender, Throwable thrown, String message){
        testOutput("error", message);
        thrown.printStackTrace();
    }

    public void debug(String message){
        testOutput("debug", message);
    }

    private void testOutput(String type, String message) {
        System.out.println(type + ": " + message);
    }
}
