package haveric.recipeManager.recipes;

import com.google.common.base.Preconditions;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RecipeTypeFactory {
    private static RecipeTypeFactory instance = null;
    private Map<String, BaseRecipe> recipes = new HashMap<>();
    private Map<String, BaseRecipeEvents> recipeEvents = new HashMap<>();
    private Map<String, BaseRecipeParser> recipeParsers = new HashMap<>();

    private boolean initialized;

    public static RecipeTypeFactory getInstance() {
        if (instance == null) {
            instance = new RecipeTypeFactory();
        }

        return instance;
    }

    private RecipeTypeFactory() {
        initialized = false;
    }

    protected void initializeRecipeType(String name, BaseRecipe recipe, BaseRecipeParser parser, BaseRecipeEvents events) {
        name = name.toLowerCase().trim();

        if (!initialized) {
            if (recipes.containsKey(name)) {
                ErrorReporter.getInstance().error("Recipe Type already exists: " + name);
            } else {
                if (!recipes.containsKey(name)) {
                    recipes.put(name, recipe);

                    if (!recipeParsers.containsKey(name)) {
                        recipeParsers.put(name, parser);
                    }

                    if (events != null) {
                        String eventsClassName = events.getClass().getName();

                        // Make sure the events class is unique before we add it, allowing multiple recipe types to share events
                        if (!recipeEvents.containsKey(eventsClassName)) {
                            recipeEvents.put(eventsClassName, events);
                        }
                    }
                }
            }
        }
    }

    public void init() {
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void cleanEvents() {
        for (BaseRecipeEvents event : recipeEvents.values()) {
            event.clean();
        }
    }

    public void reloadEvents() {
        for (BaseRecipeEvents event : recipeEvents.values()) {
            HandlerList.unregisterAll(event);
            Bukkit.getPluginManager().registerEvents(event, RecipeManager.getPlugin());
        }
    }

    public Map<String, BaseRecipe> getRecipeTypes() {
        return recipes;
    }

    public BaseRecipe getRecipeType(String name) {
        Preconditions.checkNotNull(name);

        return recipes.get(name.toLowerCase().trim());
    }

    public Collection<BaseRecipeEvents> getRecipeEvents() {
        return recipeEvents.values();
    }

    public BaseRecipeParser getRecipeParser(String name) {
        return recipeParsers.get(name);
    }
}
