package haveric.recipeManager.recipes.anvil.data;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.anvil.BaseAnvilRecipe;
import haveric.recipeManager.tools.Version;

public class BaseAnvilParser extends BaseRecipeParser {
    public BaseAnvilParser() {
        super();
    }

    protected void parseArgs(BaseAnvilRecipe recipe, String[] split) {
        if (split.length > 1) {
            String repairString = split[1].trim();

            // Skip if empty
            if (!repairString.isEmpty()) {
                try {
                    int repairCost = Integer.parseInt(repairString);

                    recipe.setRepairCost(repairCost);

                    if (!Version.has1_11Support()) {
                        ErrorReporter.getInstance().warning("Repair Cost is only supported in 1.11 or newer.");
                    }
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().error("Recipe has invalid repair cost: " + split[1] + ". Defaulting to 0.");
                }
            }
        }

        if (split.length > 2) {
            String renameText = split[2].trim().toLowerCase();

            // Skip if empty
            if (!renameText.isEmpty()) {
                if (renameText.equals("allowrename") || renameText.equals("true")) {
                    recipe.setRenamingAllowed(true);
                } else if (renameText.equals("false")) {
                    recipe.setRenamingAllowed(false);
                } else {
                    ErrorReporter.getInstance().warning("Invalid rename attribute: " + renameText + ". Defaulting to false. Accepted values: allowrename, true, false.");
                }
            }
        }

        if (split.length > 3) {
            try {
                double anvilDamageChance = Double.parseDouble(split[3].trim());

                if (anvilDamageChance < 0) {
                    ErrorReporter.getInstance().warning("Anvil damage chance cannot be below 0: " + split[3] + ". Allowed values from 0-300 (decimal values allowed). Defaulting to 0.");
                    anvilDamageChance = 0;
                } else if (anvilDamageChance > 300) {
                    ErrorReporter.getInstance().warning("Anvil damage chance cannot be above 300: " + split[3] + ". Allowed values from 0-300 (decimal values allowed). Defaulting to 300.");
                    anvilDamageChance = 300;
                }

                recipe.setAnvilDamageChance(anvilDamageChance);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().error("Invalid anvil damage chance: " + split[3] + ". Allowed values from 0-300 (decimal values allowed). Defaulting to 12.");
            }
        }
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        return false;
    }
}
