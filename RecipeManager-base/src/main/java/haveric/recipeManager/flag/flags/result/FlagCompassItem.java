package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagCompassItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.COMPASS_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
                "{flag} <worldName> <x> <y> <z> | [requiresLodestone]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
                "Sets the position that a compass points to.",
                "",
                "<worldName> is the name of the world you want to compass to work in.",
                "",
                "<x> <y> <z> are the x, y, and z coordinates you want to point to. <y> being the vertical only matters if you set requiresLodestone option to true",
                "",
                "The following variables may also be used",
                "  {world}          = world name of event location or '(unknown)' if not available",
                "  {x}              = event location's X coord",
                "  {y}              = event location's Y coord",
                "  {z}              = event location's Z coord",
                "    Relative positions are supported: {x-1},{y+7},{z+12}",
                "  {rand #1-#2}     = output a random integer between #1 and #2. Example: {rand 5-10} will output an integer from 5-10",
                "  {rand #1-#2, #3} = output a random number between #1 and #2, with decimal places of #3. Example: {rand 1.5-2.5, 2} will output a number from 1.50 to 2.50",
                "  {rand n}         = reuse a random output, where n is the nth {rand} used excluding this format",
                "",
                "[requiresLodestone] is optional and defaults to false: ",
                "  Requires a lodestone at the location set",
                "  values: true or false",
                "",
                "Specific items: compass.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
                "{flag} world 100 65 200 // Will track this location without a lodestone",
                "{flag} world 100 65 200 | true // Requires a lodestone to be at x=100, y=65, z=200 for the compass to track this location"};
    }

    String world;
    String x;
    String y;
    String z;
    boolean isLodestoneTracked = false;

    public FlagCompassItem() {
    }

    public FlagCompassItem(FlagCompassItem flag) {
        super(flag);
        isLodestoneTracked = flag.isLodestoneTracked;
        world = flag.world;
        x = flag.x;
        y = flag.y;
        z = flag.z;
    }

    @Override
    public FlagCompassItem clone() {
        return new FlagCompassItem((FlagCompassItem) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return world.startsWith("{") || x.startsWith("{") || y.startsWith("{") || z.startsWith("{");
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof CompassMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();

        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), CompassMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a compass!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);

        String[] split = value.split("\\|", 2);

        if (split.length > 1) {
            String requiresLodestoneString = split[1].trim();
            if ("true".equals(requiresLodestoneString)) {
                isLodestoneTracked = true;
            } else if ("false".equals(requiresLodestoneString)) {
                isLodestoneTracked = false;
            } else {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid requiresLodestone value! Defaulting to false", "Accepted values are true and false, defaulting false.");
            }
        }

        String[] locationString = split[0].split(" ");
        if (locationString.length != 4) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid location value!", "Location needs 4 arguments separated by spaces: world x y z, where world is the name of the world and x, y, and z are coordinates using doubles (0.0 or 0).");
        }

        world = locationString[0].trim();
        x = locationString[1].trim();
        y = locationString[2].trim();
        z = locationString[3].trim();

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        onCrafted(a);
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();

            if (!(meta instanceof CompassMeta)) {
                a.addCustomReason("Needs compass!");
                return;
            }

            CompassMeta compass = (CompassMeta) meta;
            compass.setLodestoneTracked(isLodestoneTracked);

            double actualX;
            double actualY;
            double actualZ;

            String xString = a.parseVariables(x);
            try {
                actualX = Double.parseDouble(xString);
            } catch (NumberFormatException e) {
                actualX = 0;
            }

            String yString = a.parseVariables(y);
            try {
                actualY = Double.parseDouble(yString);
            } catch (NumberFormatException e) {
                actualY = 0;
            }

            String zString = a.parseVariables(z);
            try {
                actualZ = Double.parseDouble(zString);
            } catch (NumberFormatException e) {
                actualZ = 0;
            }

            if (a.hasLocation()) {
                world = a.parseVariables(world);
            } else {
                world = Bukkit.getWorlds().get(0).getName();
            }
            compass.setLodestone(new Location(Bukkit.getWorld(world), actualX, actualY, actualZ));

            a.result().setItemMeta(compass);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "isLodestoneTracked: " + isLodestoneTracked;
        toHash += "world: " + world;
        toHash += "x: " + x;
        toHash += "y: " + y;
        toHash += "z: " + z;

        return toHash.hashCode();
    }
}
