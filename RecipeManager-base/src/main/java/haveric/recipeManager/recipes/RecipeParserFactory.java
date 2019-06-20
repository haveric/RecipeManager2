package haveric.recipeManager.recipes;


import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.brew.BrewRecipeParser;
import haveric.recipeManager.recipes.campfire.RMCampfireRecipeParser;
import haveric.recipeManager.recipes.combine.CombineRecipeParser;
import haveric.recipeManager.recipes.craft.CraftRecipeParser;
import haveric.recipeManager.recipes.fuel.FuelRecipeParser;
import haveric.recipeManager.recipes.furnace.RMBaseFurnaceRecipeParser;
import haveric.recipeManager.recipes.stonecutting.RMStonecuttingRecipeParser;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;

public class RecipeParserFactory {

    public BaseRecipeParser getParser(String directive, RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator) {
        BaseRecipeParser parser = null;
        if (directive.equals(RMCRecipeType.CRAFT.getDirective())) {
            parser = new CraftRecipeParser(reader, recipeName, fileFlags, recipeRegistrator);
        } else if (directive.equals(RMCRecipeType.COMBINE.getDirective())) {
            parser = new CombineRecipeParser(reader, recipeName, fileFlags, recipeRegistrator);
        } else if (directive.equals(RMCRecipeType.SMELT.getDirective())) {
            parser = new RMBaseFurnaceRecipeParser(reader, recipeName, fileFlags, recipeRegistrator, RMCRecipeType.SMELT);
        } else if (directive.equals(RMCRecipeType.BLASTING.getDirective())) {
            parser = new RMBaseFurnaceRecipeParser(reader, recipeName, fileFlags, recipeRegistrator, RMCRecipeType.BLASTING);
        } else if (directive.equals(RMCRecipeType.SMOKING.getDirective())) {
            parser = new RMBaseFurnaceRecipeParser(reader, recipeName, fileFlags, recipeRegistrator, RMCRecipeType.SMOKING);
        } else if (directive.equals(RMCRecipeType.CAMPFIRE.getDirective())) {
            parser = new RMCampfireRecipeParser(reader, recipeName, fileFlags, recipeRegistrator);
        } else if (directive.equals(RMCRecipeType.STONECUTTING.getDirective())) {
            parser = new RMStonecuttingRecipeParser(reader, recipeName, fileFlags, recipeRegistrator);
        } else if (directive.equals(RMCRecipeType.FUEL.getDirective())) {
            parser = new FuelRecipeParser(reader, recipeName, fileFlags, recipeRegistrator);
        } else if (directive.equals(RMCRecipeType.BREW.getDirective())) {
            parser = new BrewRecipeParser(reader, recipeName, fileFlags, recipeRegistrator);
        } else if (directive.equals(RMCRecipeType.SPECIAL.getDirective())) {
            parser = new RemoveResultsParser(reader, recipeName, fileFlags, recipeRegistrator);
        } else {
            ErrorReporter.getInstance().warning("Unexpected directive: '" + reader.getLine() + "'", "This might be caused by previous errors.");
            reader.nextLine();
        }
        return parser;
    }
}
