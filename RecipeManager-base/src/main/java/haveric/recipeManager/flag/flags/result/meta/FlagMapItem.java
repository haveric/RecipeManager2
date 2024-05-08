package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.Arrays;

public class FlagMapItem extends Flag {
    @Override
    public String getFlagType() {
        return FlagType.MAP_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [arguments]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[]{
            "Create a custom map item",
            "",
            "Replace <arguments> with the following arguments separated by | character:",
            "  scaling [false]            = Sets if the map is scaling.",
            "",
            "  locationname <name>        = Sets the location name.",
            "  color <red> <green> <blue> = Sets the map color. Colors must be 3 numbers ranged from 0 to 255, the red, green and blue channels.",
            "",
            "  world <worldName>          = Sets the world this map is associated with.",
            "  centerx <x>                = Sets the center x position of the map. Must be an integer.",
            "  centerz <z>                = Sets the center z position of the map. Must be an integer.",
            "  scale <scale>              = Sets the scale of the map.",
            "    <scale> values: " + RMCUtil.collectionToString(Arrays.asList(MapView.Scale.values())).toLowerCase(),
            "  locked [false]             = Sets the locked status of the map. Locked maps can not be explored further.",
            "  trackingposition [false]   = Sets whether a position cursor should be shown when the map is near its center.",
            "  unlimitedtracking [false]  = Whether the map will show a smaller position cursor (true), or no position cursor (false) when cursor is outside of map's range.",
        };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} scaling",
            "{flag} scaling | color 255 0 0",
            "{flag} locked | trackingposition | unlimitedtracking", };
    }

    private boolean isScaling = false;
    private String locationName;
    private Color mapColor;
    private World world;
    private Integer centerX;
    private Integer centerZ;
    private String scale;
    private boolean isLocked = false;
    private boolean isTrackingPosition = false;
    private boolean isUnlimitedTracking = false;

    public FlagMapItem() { }

    public FlagMapItem(FlagMapItem flag) {
        super(flag);

        isScaling = flag.isScaling;
        locationName = flag.locationName;
        mapColor = flag.mapColor;
        world = flag.world;
        centerX = flag.centerX;
        centerZ = flag.centerZ;
        scale = flag.scale;
        isLocked = flag.isLocked;
        isTrackingPosition = flag.isTrackingPosition;
        isUnlimitedTracking = flag.isUnlimitedTracking;
    }

    @Override
    public FlagMapItem clone() {
        return new FlagMapItem((FlagMapItem) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    public boolean isScaling() {
        return isScaling;
    }

    public void setScaling(boolean scaling) {
        isScaling = scaling;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public boolean hasLocationName() {
        return locationName != null;
    }

    public Color getMapColor() {
        return mapColor;
    }

    public void setMapColor(Color mapColor) {
        this.mapColor = mapColor;
    }

    public boolean hasMapColor() {
        return mapColor != null;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public boolean hasWorld() {
        return world != null;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public boolean hasCenterX() {
        return centerX != null;
    }

    public int getCenterZ() {
        return centerZ;
    }

    public void setCenterZ(int centerZ) {
        this.centerZ = centerZ;
    }

    public boolean hasCenterZ() {
        return centerZ != null;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public boolean hasScale() {
        return scale != null;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isTrackingPosition() {
        return isTrackingPosition;
    }

    public void setTrackingPosition(boolean trackingPosition) {
        isTrackingPosition = trackingPosition;
    }

    public boolean isUnlimitedTracking() {
        return isUnlimitedTracking;
    }

    public void setUnlimitedTracking(boolean unlimitedTracking) {
        isUnlimitedTracking = unlimitedTracking;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof MapMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), MapMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a FILLED_MAP item!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] args = value.toUpperCase().split("\\|");

        for (String s : args) {
            String arg = s.trim();
            String argLower = arg.toLowerCase();

            if (argLower.startsWith("scaling")) {
                argLower = argLower.substring("scaling".length()).trim();

                if (argLower.isEmpty() || argLower.equals("true")) {
                    isScaling = true;
                } else if (argLower.equals("false")) {
                    isScaling = false;
                }
            } else if (argLower.startsWith("locationname")) {
                locationName = arg.substring("locationname".length()).trim();
            } else if (argLower.startsWith("color")) {
                arg = arg.substring("color".length()).trim();
                Color color = Tools.parseColor(arg);
                if (color == null) {
                    ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid color numbers!", "Use 3 numbers ranging from 0 to 255, e.g. 255 128 0 for orange.");
                } else {
                    mapColor = color;
                }
            } else if (argLower.startsWith("world")) {
                arg = arg.substring("world".length()).trim();

                world = Bukkit.getWorld(arg);
            } else if (argLower.startsWith("centerx")) {
                arg = arg.substring("centerx".length()).trim();

                try {
                    centerX = Integer.parseInt(arg);
                } catch(NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'centerx' argument with invalid number: " + s);
                }
            } else if (argLower.startsWith("centerz")) {
                arg = arg.substring("centerz".length()).trim();

                try {
                    centerZ = Integer.parseInt(arg);
                } catch(NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'centerz' argument with invalid number: " + s);
                }
            } else if (argLower.startsWith("scale")) {
                arg = arg.substring("scale".length()).trim();

                try {
                    MapView.Scale mapScale = MapView.Scale.valueOf(arg.toUpperCase());

                    scale = mapScale.name();
                } catch (IllegalArgumentException e) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'scale' argument with invalid value: " + s);
                }
            } else if (argLower.startsWith("locked")) {
                argLower = argLower.substring("locked".length()).trim();

                if (argLower.isEmpty() || argLower.equals("true")) {
                    isLocked = true;
                } else if (argLower.equals("false")) {
                    isLocked = false;
                }
            } else if (argLower.startsWith("trackingposition")) {
                argLower = argLower.substring("trackingposition".length()).trim();

                if (argLower.isEmpty() || argLower.equals("true")) {
                    isTrackingPosition = true;
                } else if (argLower.equals("false")) {
                    isTrackingPosition = false;
                }
            } else if (argLower.startsWith("unlimitedtracking")) {
                argLower = argLower.substring("unlimitedtracking".length()).trim();

                if (argLower.isEmpty() || argLower.equals("true")) {
                    isUnlimitedTracking = true;
                } else if (argLower.equals("false")) {
                    isUnlimitedTracking = false;
                }
            } else {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + arg);
            }
        }

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

            if (!(meta instanceof MapMeta mapMeta)) {
                a.addCustomReason("Needs a FILLED_MAP item!");
                return;
            }

            mapMeta.setScaling(isScaling);

            if (hasLocationName()) {
                mapMeta.setLocationName(locationName);
            }

            if (hasMapColor()) {
                mapMeta.setColor(mapColor);
            }

            if (mapMeta.hasMapView()) {
                MapView mapView = mapMeta.getMapView();
                if (mapView != null) {
                    if (hasWorld()) {
                        mapView.setWorld(world);
                    }

                    if (hasCenterX()) {
                        mapView.setCenterX(centerX);
                    }

                    if (hasCenterZ()) {
                        mapView.setCenterZ(centerZ);
                    }

                    if (hasScale()) {
                        MapView.Scale mapScale = MapView.Scale.valueOf(scale);
                        mapView.setScale(mapScale);
                    }

                    mapView.setLocked(isLocked);
                    mapView.setTrackingPosition(isTrackingPosition);
                    mapView.setUnlimitedTracking(isUnlimitedTracking);
                }

                mapMeta.setMapView(mapView);
            }

            a.result().setItemMeta(mapMeta);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "isScaling: " + isScaling;
        toHash += "locationName: " + locationName;
        toHash += "mapColor: " + mapColor;
        toHash += "world: " + world;
        toHash += "centerX: " + centerX;
        toHash += "centerZ: " + centerZ;
        toHash += "scale: " + scale;
        toHash += "isLocked: " + isLocked;
        toHash += "isTrackingPosition: " + isTrackingPosition;
        toHash += "isUnlimitedTracking: " + isUnlimitedTracking;

        return toHash.hashCode();
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        if (meta instanceof MapMeta mapMeta) {
            recipeString.append(Files.NL).append("@map scaling");
            if (!mapMeta.isScaling()) {
                recipeString.append(" false");
            }

            if (mapMeta.hasLocationName()) {
                String locationName = mapMeta.getLocationName();
                recipeString.append(" | locationname ").append(locationName);
            }

            if (mapMeta.hasColor()) {
                Color mapColor = mapMeta.getColor();
                if (mapColor != null) {
                    recipeString.append(" | color ").append(mapColor.getRed()).append(mapColor.getGreen()).append(mapColor.getBlue());
                }
            }

            MapView mapView = mapMeta.getMapView();
            if (mapView != null) {
                World world = mapView.getWorld();
                if (world != null) {
                    recipeString.append(" | world ").append(world.getName());
                }

                int centerX = mapView.getCenterX();
                recipeString.append(" | centerx ").append(centerX);

                int centerZ = mapView.getCenterZ();
                recipeString.append(" | centerz ").append(centerZ);

                MapView.Scale scale = mapView.getScale();
                recipeString.append(" | scale ").append(scale.name());


                if (mapView.isLocked()) {
                    recipeString.append(" | locked");
                }

                if (mapView.isTrackingPosition()) {
                    recipeString.append(" | trackingposition");
                }

                if (mapView.isUnlimitedTracking()) {
                    recipeString.append(" | unlimitedtracking");
                }

                /* TODO: Custom renderer, warn user?
                if (mapView.isVirtual()) {

                }
                */
            }
        }
    }

    // TODO: Add condition support for FlagMapItem
}
