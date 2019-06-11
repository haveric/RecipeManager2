package haveric.recipeManager.messages;

import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Messages {
    private static Messages instance;
    private Map<String, Set<String>> sent = new HashMap<>();

    private Map<String,String> messages = new HashMap<>();

    private Messages() {

    }

    public static Messages getInstance() {
        if (instance == null) {
            instance = new Messages();
        }

        return instance;
    }

    public void reload(CommandSender sender) {
        loadDefaultMessages(sender);
    }

    public void loadDefaultMessages(CommandSender sender) {
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + Files.FILE_MESSAGES);

        if (!file.exists()) {
            RecipeManager.getPlugin().saveResource(Files.FILE_MESSAGES, false);
            MessageSender.getInstance().sendAndLog(sender, RMCChatColor.GREEN + "Generated '" + Files.FILE_MESSAGES + "' file.");
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (!Files.LASTCHANGED_MESSAGES.equals(config.getString("lastchanged"))) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_MESSAGES + "' file is outdated, please delete it to allow it to be generated again.");
        }

        loadMessages(sender, file);
    }

    public void loadMessages(CommandSender sender, File file) {
        if (!file.exists()) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>Messages file " + file.getAbsolutePath() + " is missing.");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String arg : config.getKeys(true)) {
            if (arg.equals("lastchanged")) {
                continue;
            }

            putMessage(arg, config.getString(arg));
        }
    }

    private void putMessage(String path, String message) {
        messages.put(path, message);
    }

    public String get(String path) {
        String message;
        if (messages.containsKey(path)) {
            message = messages.get(path);
        } else {
            message = "";
            MessageSender.getInstance().info("Missing message: " + path);
        }

        if (message.equals("") || message.equals("false")) {
            message = null;
        }

        return message;
    }

    public String parse(String path) {
        return RMCUtil.parseColors(get(path), false);
    }

    public String parse(String path, Object... variables) {
        return RMCUtil.replaceVariables(parse(path), variables);
    }

    public String parseCustom(String path, String customMessage, Object... variables) {
        String msg = get(path);

        if (customMessage != null) { // has custom message
            // if flag message is set to "false" then don't show the message
            if (customMessage.equals("false")) {
                msg = null;
            } else {
                msg = customMessage;
            }
        } else if (msg != null && msg.equals("false")) {
            // message is "false", don't show the message
            msg = null;
        }

        String finalCustom;
        if (msg == null) {
            finalCustom = null;
        } else {
            finalCustom = RMCUtil.replaceVariables(msg, variables);
        }

        return finalCustom;
    }

    public void send(CommandSender sender, String path) {
        if (sender != null) {
            MessageSender.getInstance().send(sender, get(path));
        }
    }

    public void send(CommandSender sender, String path, Object... variables) {
        sendCustom(sender, path, null, variables);
    }

    public void sendCustom(CommandSender sender, String path, String customMessage, Object... variables) {
        if (sender != null) {
            String msg = get(path);

            if (customMessage != null) { // has custom message
                if (customMessage.equals("false")) { // if custom message is set to "false" then don't show the message
                    return;
                }

                msg = customMessage;
            } else if (msg == null) { // message is "false", don't show the message
                return;
            }

            msg = RMCUtil.replaceVariables(msg, variables);

            MessageSender.getInstance().send(sender, msg);
        }
    }

    public void sendOnce(CommandSender sender, String path) {
        sendOnceCustom(sender, path);
    }

    public void sendOnceCustom(CommandSender sender, String path, Object... variables) {
        if (sender != null) {
            Set<String> set = sent.get(sender.getName());

            if (set == null) {
                set = new HashSet<>();
                sent.put(sender.getName(), set);
            }

            if (!set.contains(path)) {
                set.add(path);
                send(sender, path, variables);
            }
        }
    }

    public void clearPlayer(String name) {
        sent.remove(name);
    }



}
