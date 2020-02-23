package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.common.util.RMCUtil;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class FlagFireworkStarItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.FIREWORK_STAR_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <effect arguments>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Configures firework charge's effect.",
            "Using this flag more than once will overwrite previous changes since the item only supports one effect.",
            "",
            "Replace '<effect arguments>' with the following arguments separated by | character.",
            "Effects can be:",
            "  color <red> <green> <blue>, ...           = (Required) Sets the primary explosion color(s), you can define more colors separated by comma.",
            "  fadecolor <red> <green> <blue>, ...       = (Optional) Color(s) of the explosion fading, you can define more colors separated by comma.",
            "  type <explode type>                       = (Optional) Shape/size of explosion, can be: " + RMCUtil.collectionToString(Arrays.asList(FireworkEffect.Type.values())).toLowerCase() + "  (see '" + Files.FILE_INFO_NAMES + "' file)",
            "  trail                                     = (Optional) Adds a trail to the explosion",
            "  flicker                                   = (Optional) Adds a flicker to explosion",
            "Effects can be listed in any order.",
            "Colors must be 3 numbers ranging from 0 to 255, basic RGB format.",
            "",
            "Specific item: firework_star.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} trail | color 255 0 0, 0 255 0 | type ball_large",
            "{flag} type creeper | color 0 255 0 | fadecolor 255 0 0, 0 255 0 | flicker", };
    }


    private FireworkEffect effect;

    public FlagFireworkStarItem() {
    }

    public FlagFireworkStarItem(FlagFireworkStarItem flag) {
        effect = flag.effect;
    }

    @Override
    public FlagFireworkStarItem clone() {
        return new FlagFireworkStarItem((FlagFireworkStarItem) super.clone());
    }

    public FireworkEffect getEffect() {
        return effect;
    }

    public void setEffect(FireworkEffect newEffect) {
        effect = newEffect;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();

        if (result == null || !(result.getItemMeta() instanceof FireworkEffectMeta)) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a FIREWORK_STAR item!");
            return false;
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        FireworkEffect newEffect = Tools.parseFireworkEffect(value, getFlagType());

        if (newEffect == null) {
            return false;
        }

        setEffect(newEffect);

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

            if (!(meta instanceof FireworkEffectMeta)) {
                a.addCustomReason("Needs FireworkEffectMeta supported item!");
                return;
            }

            FireworkEffectMeta effectMeta = (FireworkEffectMeta) meta;

            effectMeta.setEffect(getEffect());

            a.result().setItemMeta(effectMeta);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "effect: " + effect.hashCode();

        return toHash.hashCode();
    }
}
