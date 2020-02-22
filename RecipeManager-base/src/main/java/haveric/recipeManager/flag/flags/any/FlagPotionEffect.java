package haveric.recipeManager.flag.flags.any;

import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Version;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
            "  duration <float>    = (default 3.0) potion effect duration in seconds, only works on non-instant effect types.",
            "  amplifier <num>     = (default 0) potion effect amplifier.",
            "  chance <0.01-100>%  = (default 100%) chance that the effect will be applied, this chance is individual for this effect.",
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


    private Map<PotionEffect, Float> effects = new HashMap<>();
    private boolean clear = false;

    public FlagPotionEffect() {
    }

    public FlagPotionEffect(FlagPotionEffect flag) {
        effects.putAll(flag.effects);
        clear = flag.clear;
    }

    @Override
    public FlagPotionEffect clone() {
        return new FlagPotionEffect((FlagPotionEffect) super.clone());
    }

    public Map<PotionEffect, Float> getEffects() {
        return effects;
    }

    public void setEffects(Map<PotionEffect, Float> newEffects) {
        if (newEffects == null) {
            remove();
        } else {
            effects = newEffects;
        }
    }

    public void addEffect(PotionEffect effect) {
        addEffect(effect, 100);
    }

    public void addEffect(PotionEffect effect, float chance) {
        effects.put(effect, chance);
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
            setClear(true);
            return true;
        }

        String[] split = value.split("\\|");

        value = split[0].trim();
        PotionEffectType type = PotionEffectType.getByName(value);
        int amplifier = 0;
        float chance = 100.0f;
        float duration = 3.0f;
        boolean ambient = true;
        boolean particles = true;
        boolean icon = true;

        if (type == null) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid effect type: " + value);
            return false;
        }

        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                value = split[i].trim();

                if (value.startsWith("ambient")) {
                    value = value.substring("ambient".length()).trim();

                    if (value.equals("false")) {
                        ambient = false;
                    } else if (value.equals("true")) {
                        ambient = true;
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid value for ambient: " + value + ". Defaulting to true.");
                        ambient = true;
                    }
                } else if (value.startsWith("particles")) {
                    value = value.substring("particles".length()).trim();

                    if (value.equals("false")) {
                        particles = false;
                    } else if (value.equals("true")) {
                        particles = true;
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid value for particles: " + value + ". Defaulting to true.");
                        particles = true;
                    }
                } else if (value.startsWith("icon")) {
                    value = value.substring("icon".length()).trim();

                    if (value.equals("false")) {
                        icon = false;
                    } else if (value.equals("true")) {
                        icon = true;
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid value for icon: " + value + ". Defaulting to true.");
                        icon = true;
                    }
                } else if (value.equals("!ambient")) {
                    ambient = false;
                } else if (value.equals("!particles")) {
                    particles = false;
                } else if (value.equals("!icon")) {
                    icon = false;
                } else if (value.startsWith("chance")) {
                    value = value.substring("chance".length()).trim();

                    if (value.charAt(value.length() - 1) == '%') {
                        value = value.substring(0, value.length() - 1);
                    }

                    try {
                        chance = Float.parseFloat(value);
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid chance value number: " + value);
                        continue;
                    }

                    if (chance < 0.01f || chance > 100.0f) {
                        chance = Math.min(Math.max(chance, 0.01f), 100.0f);

                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has chance value less than 0.01 or higher than 100.0, value trimmed.");
                    }
                } else if (value.startsWith("amplifier")) {
                    value = value.substring("amplifier".length()).trim();

                    try {
                        amplifier = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid amplifier value number: " + value);
                    }
                } else if (value.startsWith("duration")) {
                    if (type.isInstant()) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has effect type '" + type.toString() + "' which is instant, it can't have duration, ignored.");
                        continue;
                    }

                    value = value.substring("duration".length()).trim();

                    try {
                        duration = Float.parseFloat(value);
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid duration value number: " + value);
                    }
                }
            }
        }

        PotionEffect effect;
        if (Version.has1_13BasicSupport()) {
            effect = new PotionEffect(type, (int) Math.ceil(duration * 20.0), amplifier, ambient, particles, icon);
        } else {
            effect = new PotionEffect(type, (int) Math.ceil(duration * 20.0), amplifier, ambient, particles);
        }

        addEffect(effect, chance);

        return true;
    }

    @Override
    public void onCrafted(Args a) {
        if (!a.hasPlayer()) {
            a.addCustomReason("Need player!");
            return;
        }

        if (isClear()) {
            for (PotionEffect e : a.player().getActivePotionEffects()) {
                a.player().removePotionEffect(e.getType());
            }
        }

        for (Entry<PotionEffect, Float> e : effects.entrySet()) {
            if (e.getValue() == 100 || e.getValue() >= (RecipeManager.random.nextFloat() * 100)) {
                e.getKey().apply(a.player());
            }
        }
    }

    @Override
    public int hashCode() {
        StringBuilder toHash = new StringBuilder("" + super.hashCode());

        toHash.append("effects: ");
        for (Map.Entry<PotionEffect, Float> entry : effects.entrySet()) {
            toHash.append(entry.getKey().hashCode()).append(entry.getValue().toString());
        }

        toHash.append("clear: ").append(clear);

        return toHash.toString().hashCode();
    }
}
