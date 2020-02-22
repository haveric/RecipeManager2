package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class FlagSuspiciousStew extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.SUSPICIOUS_STEW;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <effect type> | [arguments]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[]{
            "Adds potion effects to a suspicious stew.",
            "This flag can be used more than once to add more effects.",
            "",
            "The <effect type> argument must be an effect type, names for them can be found in '" + Files.FILE_INFO_NAMES + "' file at 'POTION EFFECT TYPE'.",
            "",
            "Optionally you can add more arguments separated by | character in any order:",
            "  duration <float>    = (default 1.0) potion effect duration in seconds, only works on non-instant effect types.",
            "  amplifier <num>     = (default 0) potion effect amplifier.",
            "  ambient [false]     = (default true) makes the effect produce more, translucent, particles.",
            "  !ambient            = equivalent to 'ambient false'",
            "  particles [false]   = (defaults true) display particles.",
            "  !particles          = equivalent to 'particles false'",
            "  icon [false]        = (defaults true) show the effect icon.",
            "  !icon               = equivalent to 'icon false'",};
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} heal",
            "{flag} blindness | duration 60 | amplifier 5",
            "{flag} poison | chance 6.66% | ambient | amplifier 666 | duration 6.66", };
    }


    private List<PotionEffect> effects = new ArrayList<>();

    public FlagSuspiciousStew() {
    }

    public FlagSuspiciousStew(FlagSuspiciousStew flag) {
        effects.addAll(flag.effects);
    }

    @Override
    public FlagSuspiciousStew clone() {
        return new FlagSuspiciousStew((FlagSuspiciousStew) super.clone());
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<PotionEffect> newEffects) {
        if (newEffects == null) {
            remove();
        } else {
            effects.clear();
            effects.addAll(newEffects);
        }
    }

    public void addEffect(PotionEffect effect) {
        effects.add(effect);
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();

        if (result == null || !(result.getItemMeta() instanceof SuspiciousStewMeta)) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a suspicious stew!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        value = value.toLowerCase();

        PotionEffect effect = Tools.parsePotionEffect(value, getFlagType());
        if (effect != null) {
            addEffect(effect);
        }

        return true;
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();

            if (!(meta instanceof SuspiciousStewMeta)) {
                a.addCustomReason("Needs suspicious stew");
                return;
            }

            SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) meta;
            for (PotionEffect e : effects) {
                stewMeta.addCustomEffect(e, true);
            }

            a.result().setItemMeta(stewMeta);
        }
    }

    @Override
    public int hashCode() {
        StringBuilder toHash = new StringBuilder("" + super.hashCode());

        toHash.append("effects: ");
        for (PotionEffect effect : effects) {
            toHash.append(effect.hashCode());
        }

        return toHash.toString().hashCode();
    }
}
