package haveric.recipeManager.recipes;

import haveric.recipeManager.ErrorReporter;
import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.Map;

public class RecipeTypeFactory {
    private static RecipeTypeFactory instance = null;
    private Map<String, BaseRecipe> recipeTypes = new HashMap<>();

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

    protected void initializeRecipeType(String name, BaseRecipe recipe) {
        name = name.toLowerCase().trim();
        if (!initialized) {
            if (recipeTypes.containsKey(name)) {
                ErrorReporter.getInstance().error("Recipe Type already exists: " + name);
            } else {
                recipeTypes.put(name, recipe);
            }
        }
    }

    public void init() {
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Map<String, BaseRecipe> getRecipeTypes() {
        return recipeTypes;
    }

    public BaseRecipe getRecipeType(String name) {
        Validate.notNull(name);

        return recipeTypes.get(name.toLowerCase().trim());
    }
}
