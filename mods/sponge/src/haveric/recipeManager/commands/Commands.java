package haveric.recipeManager.commands;

import haveric.recipeManager.RecipeManager;

import org.spongepowered.api.service.command.CommandService;

public class Commands {

    private RecipeManager plugin;

    public Commands(RecipeManager recipeManager) {
        plugin = recipeManager;

        CommandService service = plugin.getGame().getCommandDispatcher();

        service.register(plugin, new BaseCommand(), "rm", "recipemanager", "rmhelp");

        service.register(plugin, new BooksCommand(), "rmbooks", "recipebooks");
        service.register(plugin, new CheckCommand(), "rmcheck", "checkrecipes");
        service.register(plugin, new ExtractCommand(), "rmextract", "rmimport", "importrecipes", "extractrecipes");
        service.register(plugin, new ExtractRecipeCommand(), "rmextractrecipe", "extractrecipe");
        service.register(plugin, new FindItemCommand(), "rmfinditem", "finditem");
        service.register(plugin, new GetBookCommand(), "rmgetbook", "getrecipebook");
        service.register(plugin, new RecipeCommand(), "rmrecipes", "recipe", "recipes");
        service.register(plugin, new ReloadBooksCommand(), "rmreloadbooks", "reloadbooks");
        service.register(plugin, new ReloadCommand(), "rmreload");
        service.register(plugin, new UpdateCommand(), "rmupdate", "rmcheckupdates");
    }

}
