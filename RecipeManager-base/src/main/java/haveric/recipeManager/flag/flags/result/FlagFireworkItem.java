package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import haveric.recipeManager.tools.Version;
import org.apache.commons.lang3.Validate;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FlagFireworkItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.FIREWORK_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} effect <effect data>",
            "{flag} power <0-127>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Configures firework rocket's effects.",
            "Using this flag more than once will append changes to the item.",
            "",
            "The 'effect' setting adds an effect to the rocket.",
            "Replace '<effect arguments>' with the following arguments separated by | character.",
            "Effects can be:",
            "  color <red> <green> <blue>, ...           = (Required) Sets the primary explosion color(s), you can define more colors separated by comma.",
            "  fadecolor <red> <green> <blue>, ...       = (Optional) Color(s) of the explosion fading, you can define more colors separated by comma.",
            "  type &lt;explode type&gt;                       = (Optional) Shape/size of explosion, see: " + Files.getNameIndexHashLink("fireworkeffect"),
            "  trail                                     = (Optional) Adds a trail to the explosion",
            "  flicker                                   = (Optional) Adds a flicker to explosion",
            "Effects can be listed in any order.",
            "Colors must be 3 numbers ranging from 0 to 255, basic RGB format.",
            "",
            "The 'power <number 0-127>' value sets how long rocket will fly, each number is 0.5 seconds of flight, default 2, recommended max 4.",
            "",
            "Specific item: firework.", };
    }

    protected String[] getExamples() {
        return new String[] {
            "{flag} effect color 0 255 0",
            "{flag} effect trail | color 255 0 0 | type burst",
            "{flag} effect color 255 0 200, 0 255 0, 255 128 0 | trail | type ball_large | fadecolor 255 0 0, 0 0 255, 0 255 0",
            "{flag} power 1", };
    }


    private int power = 2;
    private List<FireworkEffect> effects = new ArrayList<>();

    public FlagFireworkItem() {
    }

    public FlagFireworkItem(FlagFireworkItem flag) {
        super(flag);
        effects.addAll(flag.effects);

        power = flag.power;
    }

    @Override
    public FlagFireworkItem clone() {
        return new FlagFireworkItem((FlagFireworkItem) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int newPower) {
        power = newPower;
    }

    public List<FireworkEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<FireworkEffect> newEffects) {
        Validate.notNull(newEffects, "The 'effects' argument must not be null!");

        effects = newEffects;
    }

    public void addEffect(FireworkEffect effect) {
        effects.add(effect);
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof FireworkMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        if (Version.has1_13BasicSupport()) {
            FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();

            if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), FireworkMeta.class)) {
                validFlaggable = true;
            }
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a FIREWORK item!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        value = value.toLowerCase();

        if (value.startsWith("effect")) {
            value = value.substring("effect".length()).trim();

            FireworkEffect effect = Tools.parseFireworkEffect(value, getFlagType());

            if (effect == null) {
                return false;
            }

            addEffect(effect);
        } else if (value.startsWith("power")) {
            value = value.substring("power".length()).trim();

            try {
                power = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // TODO: Handle exception
            }

            if (power < 0 || power > 127) {
                return ErrorReporter.getInstance().error("Flag " + getFlagType() + " invalid 'power' argument: '" + value + "', it must be a number from 0 to 127.");
            }
        } else {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + value);
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

            if (!(meta instanceof FireworkMeta)) {
                a.addCustomReason("Needs FireworkMeta supported item!");
                return;
            }

            FireworkMeta fireworkMeta = (FireworkMeta) meta;

            fireworkMeta.addEffects(effects);
            fireworkMeta.setPower(power);

            a.result().setItemMeta(fireworkMeta);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "power: " + power;

        toHash += "effects: ";
        for (FireworkEffect effects : effects) {
            toHash += effects.hashCode();
        }

        return toHash.hashCode();
    }
}
