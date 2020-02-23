package haveric.recipeManager.flag.flags.any;

import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class FlagPotionEffect extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.POTION_EFFECT;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <effect type> | [arguments]",
            "{flag} clear", };
    }

    @Override
    protected String[] getDescription() {
        String[] description = new String[]{
            "Adds potion effects to crafter.",
            "This flag can be used more than once to add more effects.",
            "",
            "Using 'clear' will remove all potion effects from player before adding any defined ones.",
            "",
            "The <effect type> argument must be an effect type, names for them can be found in '" + Files.FILE_INFO_NAMES + "' file at 'POTION EFFECT TYPE'.",
            "",
            "Optionally you can add more arguments separated by | character in any order:",
            "  duration <float>    = (default 1.0) potion effect duration in seconds, only works on non-instant effect types.",
            "  amplifier <num>     = (default 0) potion effect amplifier.",
            "  ambient [false]     = (default true) makes the effect produce more, translucent, particles.",
            "  !ambient            = equivalent to 'ambient false'",
            "  particles [false]   = (defaults true) display particles.",
            "  !particles          = equivalent to 'particles false'",};

        if (Version.has1_13BasicSupport()) {
            description = ObjectArrays.concat(description, new String[]{
            "  icon [false]        = (defaults true) show the effect icon.",
            "  !icon               = equivalent to 'icon false'",
            }, String.class);
        }

        return description;
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} clear // remove all player's potion effects beforehand",
            "{flag} heal",
            "{flag} blindness | duration 60 | amplifier 5",
            "{flag} poison | chance 6.66% | ambient | amplifier 666 | duration 6.66", };
    }


    private List<PotionEffect> effects = new ArrayList<>();
    private boolean clear = false;

    public FlagPotionEffect() {
    }

    public FlagPotionEffect(FlagPotionEffect flag) {
        effects.addAll(flag.effects);
        clear = flag.clear;
    }

    @Override
    public FlagPotionEffect clone() {
        return new FlagPotionEffect((FlagPotionEffect) super.clone());
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

    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean newClear) {
        clear = newClear;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        value = value.toLowerCase();

        if (value.equals("clear")) {
            clear = true;
            return true;
        }

        PotionEffect effect = Tools.parsePotionEffect(value, getFlagType());
        if (effect != null) {
            addEffect(effect);
        }

        return true;
    }

    @Override
    public void onCrafted(Args a) {
        if (!a.hasPlayer()) {
            a.addCustomReason("Need player!");
            return;
        }

        if (clear) {
            for (PotionEffect e : a.player().getActivePotionEffects()) {
                a.player().removePotionEffect(e.getType());
            }
        }

        for (PotionEffect e : effects) {
            e.apply(a.player());
        }
    }

    @Override
    public int hashCode() {
        StringBuilder toHash = new StringBuilder("" + super.hashCode());

        toHash.append("effects: ");
        for (PotionEffect effect : effects) {
            toHash.append(effect.hashCode());
        }

        toHash.append("clear: ").append(clear);

        return toHash.toString().hashCode();
    }
}
