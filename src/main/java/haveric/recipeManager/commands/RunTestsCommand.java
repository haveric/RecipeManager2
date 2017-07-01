package haveric.recipeManager.commands;

import com.google.common.collect.Iterators;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManagerCommon.RMCChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RunTestsCommand implements CommandExecutor {

    private int total;
    private int passed;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        passed = 0;
        total = 0;

        MessageSender.getInstance().send(sender, "Running Tests...");

        //testRemoveRestoreSpecialRecipes(sender);
        // TODO: Figure out more tests that can be done in-game

        MessageSender.getInstance().send(sender, getFinalChatColor() + "" + passed + " out of " + total + " tests passed.");

        // Reload to (hopefully) undo any changes.
        RecipeManager.getPlugin().reload(sender, false, false);

        return true;
    }

    private boolean testRemoveRestoreSpecialRecipes(CommandSender sender) {
        Bukkit.resetRecipes();
        int initialRecipes = Iterators.size(Bukkit.recipeIterator());

        Vanilla.removeAllButSpecialRecipes();
        Vanilla.restoreAllButSpecialRecipes();
        int restoredRecipes = Iterators.size(Bukkit.recipeIterator());

        return passFail(sender, initialRecipes, restoredRecipes);
    }

    private boolean passFail(CommandSender sender, Object expected, Object actual) {
        total++;
        if (expected.equals(actual)) {
            MessageSender.getInstance().send(sender, RMCChatColor.GREEN + "Test " + total + " PASSED");
            passed++;
            return true;
        } else {
            MessageSender.getInstance().send(sender, RMCChatColor.RED + "Test " + total + " FAILED. " + RMCChatColor.RESET + "Expected: " + expected + ". Actual: " + actual);
            return false;
        }
    }

    private RMCChatColor getFinalChatColor() {
        RMCChatColor finalChatColor;
        if (passed == 0) {
            finalChatColor = RMCChatColor.RED;
        } else if (passed < total) {
            finalChatColor = RMCChatColor.YELLOW;
        } else {
            finalChatColor = RMCChatColor.GREEN;
        }

        return finalChatColor;
    }
}