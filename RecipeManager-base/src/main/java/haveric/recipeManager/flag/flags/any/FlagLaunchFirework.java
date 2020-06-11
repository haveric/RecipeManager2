package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FlagLaunchFirework extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.LAUNCH_FIREWORK;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} effect <effects>",
            "{flag} power <number 0-128>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Launches a firework from workbench/player/furnace when recipe or result item is crafted.",
            "This flag can be defined multiple times add effects and set power to the same rocket.",
            "",
            "The 'effect' setting adds an effect to the rocket.",
            "Replace <effects> with the effects separated by | character.",
            "Effects can be:",
            "  color <red> <green> <blue>, ...           = (Required at least 1 color) Sets the primary explosion color(s), you can define more colors separated by comma.",
            "  fadecolor <red> <green> <blue>, ...       = (Optional) Color(s) of the explosion fading, you can define more colors separated by comma.",
            "  type &lt;explode type&gt;                       = (Optional) Shape/size of explosion, see: " + Files.getNameIndexHashLink("fireworkeffect"),
            "  trail                                     = (Optional) Adds a trail to the explosion",
            "  flicker                                   = (Optional) Adds a flicker to explosion",
            "",
            "Effects can be listed in any order.",
            "Colors must be 3 numbers ranging from 0 to 255, basic RGB format.",
            "",
            "The 'power <number 0-128>' value sets how long rocket will fly, each number is 0.5 seconds of flight, values above 4 are NOT recommended because it heavily affects client performance, default 2.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} effect color 0 255 0",
            "{flag} effect trail | color 255 0 0 | type burst",
            "{flag} effect color 255 0 200, 0 255 0, 255 128 0 | trail | type ball_large | fadecolor 255 0 0, 0 0 255, 0 255 0",
            "{flag} power 2", };
    }

    private final int DEFAULT_POWER = 2;
    private FireworkMeta firework;
    private float chance = 100;

    public FlagLaunchFirework() {
    }

    public FlagLaunchFirework(FlagLaunchFirework flag) {
        firework = flag.firework.clone();
        chance = flag.chance;
    }

    @Override
    public FlagLaunchFirework clone() {
        return new FlagLaunchFirework((FlagLaunchFirework) super.clone());
    }

    public FireworkMeta getFirework() {
        return firework;
    }

    public void setFirework(FireworkMeta newFirework) {
        Validate.notNull(newFirework);

        firework = newFirework;
    }

    public float getChance() {
        return chance;
    }

    public void setChance(float newChance) {
        chance = newChance;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        Material fireworkMaterial;
        if (Version.has1_13BasicSupport()) {
            fireworkMaterial = Material.FIREWORK_ROCKET;
        } else {
            fireworkMaterial = Material.getMaterial("FIREWORK");
        }

        if (firework == null) {
            firework = (FireworkMeta) Bukkit.getItemFactory().getItemMeta(fireworkMaterial);
        }

        firework.setPower(DEFAULT_POWER);

        if (value.startsWith("effect")) {
            value = value.substring("effect".length()).trim();

            FireworkEffect effect = Tools.parseFireworkEffect(value, getFlagType());

            if (effect != null) {
                firework.addEffect(effect);
            }
        } else if (value.startsWith("power")) {
            value = value.substring("power".length()).trim();
            int power;

            try {
                power = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                power = -1;
            }

            if (power < 0 || power > 128) {
                ErrorReporter.getInstance().error("Flag " + getFlagType() + " invalid 'power' argument value: '" + value + "', it must be a number from 0 to 128");
                return false;
            }

            firework.setPower(power);
        } else if (value.startsWith("chance")) {
            value = value.substring("chance".length()).replace('%', ' ').trim();

            try {
                chance = Float.parseFloat(value);
            } catch (NumberFormatException e) {
                // TODO: Handle Exception
            }

            if (chance < 0 || chance > 100) {
                ErrorReporter.getInstance().error("Flag " + getFlagType() + " invalid 'chance' argument value: '" + value + "', it must be a number from 0 to 100");
                return false;
            }
        } else {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + value);
            return false;
        }

        return true;
    }

    @Override
    public void onCrafted(Args a) {
        Validate.notNull(firework);

        if (!a.hasLocation()) {
            return;
        }

        if (chance >= 100 || (RecipeManager.random.nextFloat() * 100) <= chance) {
            Firework ent = (Firework) a.location().getWorld().spawnEntity(a.location(), EntityType.FIREWORK);

            ent.setFireworkMeta(firework);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "power: " + firework.getPower();
        toHash += "effects: ";
        for (FireworkEffect effect : firework.getEffects()) {
            toHash += effect.hashCode();
        }

        toHash += "chance: " + chance;

        return toHash.hashCode();
    }
}
