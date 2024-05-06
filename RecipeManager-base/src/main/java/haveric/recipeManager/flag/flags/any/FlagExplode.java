package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.fuel.FuelRecipe1_13;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FlagExplode extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.EXPLODE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <arguments or false>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Makes the workbench/furnace/player explode when recipe is crafted.",
            "This flag can only be declared once per recipe and once per result.",
            "",
            "Replace <arguments> with the following arguments separated by | character:",
            "  power <0.0 to ...>      = (default 2.0) Set the explosion power, value multiplied by 2 is the range in blocks; TNT has 4.0",
            "  fire                    = (default not set) Explosion sets fires.",
            "  nobreak                 = (default not set) Makes explosion not break blocks.",
            "  nodamage [self]         = (default not set) Explosion doesn't damage players or only the crafter if 'self' is specified.",
            "  fail                    = (default not set) Explode if recipe failed as opposed to succeed.",
            "  fuel <start,end,random> = (default start) Causes the explosion to happen at different times. Can only be used on fuel recipes.",
            "All arguments are optional and you can specify these arguments in any order.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} // will explode when recipe succeeds with power 2 and breaks blocks",
            "{flag} nobreak | fire | power 6 // will explode  without block damage but sets fires",
            "{flag} fail | power 2 // will explode when recipe fails",
            "{flag} fuel end // On a fuel recipe, will cause the explosion to happen after the fuel runs out",
            "{flag} fuel random // On a fuel recipe, will cause the explosion to happen sometime randomly before the fuel runs out", };
    }


    private float power = 2.0f;
    private boolean fire = false;
    private boolean noBreak = false;
    private byte noDamage = 0;
    private boolean failure = false;
    private String fuel = "start";

    public FlagExplode() {
    }

    public FlagExplode(FlagExplode flag) {
        super(flag);
        power = flag.power;
        fire = flag.fire;
        noBreak = flag.noBreak;
        noDamage = flag.noDamage;
        failure = flag.failure;
        fuel = flag.fuel;
    }

    @Override
    public FlagExplode clone() {
        return new FlagExplode((FlagExplode) super.clone());
    }

    public float getPower() {
        return power;
    }

    public void setPower(float newPower) {
        power = newPower;
    }

    public boolean getFire() {
        return fire;
    }

    public void setFire(boolean newFire) {
        fire = newFire;
    }

    public boolean getFailure() {
        return failure;
    }

    public void setFailure(boolean newFailure) {
        failure = newFailure;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String newFuel) {
        fuel = newFuel;
    }

    public boolean getNoBreak() {
        return noBreak;
    }

    public void setNoBreak(boolean newNoBreak) {
        noBreak = newNoBreak;
    }

    public boolean isNoDamageEnabled() {
        return noDamage > 0;
    }

    public boolean isNoDamageSelf() {
        return noDamage == 2;
    }

    public void setNoDamage(boolean enable) {
        setNoDamage(enable, false);
    }

    public void setNoDamage(boolean enable, boolean self) {
        if (enable) {
            if (self) {
                noDamage = 2;
            } else {
                noDamage = 1;
            }
        } else {
            noDamage = 0;
        }
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        if (value == null) {
            return true; // accepts null value
        }

        String[] args = value.toLowerCase().split("\\|");

        for (String arg : args) {
            arg = arg.trim().toLowerCase();

            if (arg.equals("fire")) {
                fire = true;
            } else if (arg.equals("fail")) {
                failure = true;
            } else if (arg.equals("nobreak")) {
                noBreak = true;
            } else if (arg.startsWith("nodamage")) {
                value = arg.substring("nodamage".length()).trim();

                setNoDamage(true, value.equals("self"));
            } else if (arg.startsWith("power")) {
                value = arg.substring("power".length()).trim();

                try {
                    power = Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'power' argument with invalid number: " + value);
                }
            } else if (arg.startsWith("fuel")) {
                if (getFlaggable() instanceof FuelRecipe1_13) {
                    value = arg.substring("fuel".length()).trim().toLowerCase();

                    if (value.equals("start") || value.equals("end") || value.equals("random")) {
                        fuel = value;
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'fuel' argument with invalid argument: " + value + ". Defaulting to 'start'.");
                        fuel = "start";
                    }
                } else {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'fuel' argument on non fuel recipe.");
                }
            } else {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + arg);
            }
        }

        return true;
    }

    @Override
    public void onCrafted(final Args a) {
        if (fuel.equals("start")) {
            runBoomLater(a);
        }
    }

    @Override
    public void onFailed(final Args a) {
        if (fuel.equals("start")) {
            runBoomLater(a);
        }
    }

    @Override
    public void onFuelRandom(final Args a) {
        if (fuel.equals("random")) {
            runBoomLater(a);
        }
    }

    @Override
    public void onFuelEnd(final Args a) {
        if (fuel.equals("end")) {
            runBoomLater(a);
        }
    }

    private void runBoomLater(final Args a) {
        new BukkitRunnable() {
            @Override
            public void run() {
                boom(a);
            }
        }.runTaskLater(RecipeManager.getPlugin(), 1);
    }

    private void boom(Args a) {
        if (!a.hasLocation()) {
            a.addCustomReason("Need a location!");
            return;
        }

        if (failure && !a.hasResult()) {
            a.addCustomReason("Needs a result!");
            return;
        }

        boolean failed;
        if (failure) {
            failed = a.result().getType() == Material.AIR;
        } else {
            failed = false;
        }

        if (failure == failed) {
            Map<LivingEntity, Double> entities = new HashMap<>();
            Location loc = a.location();
            World world = loc.getWorld();
            double x = loc.getX() + 0.5;
            double y = loc.getY() + 0.5;
            double z = loc.getZ() + 0.5;

            if (isNoDamageEnabled()) {
                double distanceSquared = power * 2.0;
                distanceSquared *= distanceSquared;

                if (isNoDamageSelf()) {
                    if (a.hasPlayer()) {
                        Player p = a.player();
                        Location l = p.getLocation();

                        if (l.distanceSquared(loc) <= distanceSquared) {
                            entities.put(p, p.getLastDamage());
                            p.setNoDamageTicks(p.getMaximumNoDamageTicks());
                            p.setLastDamage(Integer.MAX_VALUE);
                        }
                    } else {
                        a.addCustomReason("Can't protect crafter, no player!");
                    }
                } else {
                    for (LivingEntity e : world.getLivingEntities()) {
                        if (e.getLocation().distanceSquared(loc) <= distanceSquared) {
                            entities.put(e, e.getLastDamage());
                            e.setNoDamageTicks(e.getMaximumNoDamageTicks());
                            e.setLastDamage(Integer.MAX_VALUE);
                        }
                    }
                }
            }

            world.createExplosion(x, y, z, power, fire, !noBreak);

            for (Entry<LivingEntity, Double> e : entities.entrySet()) {
                e.getKey().setNoDamageTicks(0);
                e.getKey().setLastDamage(e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "power: " + power;
        toHash += "fire: " + fire;
        toHash += "noBreak: " + noBreak;
        toHash += "noDamage: " + noDamage;
        toHash += "failure: " + failure;
        toHash += "fuel: " + fuel;

        return toHash.hashCode();
    }
}
