package ro.thehunters.digi.recipeManager.flags;

import java.util.Arrays;

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.ErrorReporter;
import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagFireworkChargeItem extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.FIREWORKCHARGEITEM;

        A = new String[] { "{flag} <effect arguments>", };

        D = new String[] { "Configures firework charge's effect.", "Using this flag more than once will overwrite previous changes since the item only supports one effect.", "", "Replace '<effect arguments>' with the following arguments separated by | character.", "Effects can be:", "  color <red> <green> <blue>, ...           = (Required) Sets the primary explosion color(s), you can define more colors separated by comma.", "  fadecolor <red> <green> <blue>, ...       = (Optional) Color(s) of the explosion fading, you can define more colors separated by comma.", "  type <explode type>                       = (Optional) Shape/size of explosion, can be: " + Tools.collectionToString(Arrays.asList(FireworkEffect.Type.values())).toLowerCase() + "  (see '" + Files.FILE_INFO_NAMES + "' file)", "  trail                                     = (Optional) Adds a trail to the explosion", "  flicker                                   = (Optional) Adds a flicker to explosion", "Effects can be listed in any order.", "Colors must be 3 numbers ranging from 0 to 255, basic RGB format.", "", "Specific item: firework_charge.", };

        E = new String[] { "{flag} trail | color 255 0 0, 0 255 0 | type ball_large", "{flag} type creeper | color 0 255 0 | fadecolor 255 0 0, 0 255 0 | flicker", };
    }

    // Flag code

    private FireworkEffect effect;

    public FlagFireworkChargeItem() {
    }

    public FlagFireworkChargeItem(FlagFireworkChargeItem flag) {
        effect = flag.effect;
    }

    @Override
    public FlagFireworkChargeItem clone() {
        return new FlagFireworkChargeItem(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public FireworkEffect getEffect() {
        return effect;
    }

    public void setEffect(FireworkEffect effect) {
        this.effect = effect;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();

        if (result == null || result.getItemMeta() instanceof FireworkEffectMeta == false) {
            ErrorReporter.error("Flag " + getType() + " needs a FIREWORK_CHARGE item!");
            return false;
        }

        return true;
    }

    @Override
    public boolean onParse(String value) {
        FireworkEffect effect = Tools.parseFireworkEffect(value, getType());

        if (effect == null) {
            return false;
        }

        setEffect(effect);

        return true;
    }

    @Override
    protected void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Need result!");
            return;
        }

        ItemMeta meta = a.result().getItemMeta();

        if (meta instanceof FireworkEffectMeta == false) {
            a.addCustomReason("Needs FireworkEffectMeta supported item!");
            return;
        }

        FireworkEffectMeta effectMeta = (FireworkEffectMeta) meta;

        effectMeta.setEffect(getEffect());

        a.result().setItemMeta(effectMeta);
    }
}
