package haveric.recipeManager.flag.flags;

import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.Location;
import org.bukkit.Particle;

public class FlagSpawnParticle extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.SPAWN_PARTICLE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <particle> | [arguments]", };
    }

    @Override
    protected String[] getDescription() {
    return new String[] {
            "Spawn a particle at crafting location",
            "This flag can be used more than once to spawn more particles.",
            "",
            "The <particle> argument must be a particlename, you can find them in '" + Files.FILE_INFO_NAMES + "' file at 'PARTICLE LIST' section.",
            "",
            "Optionally you can specify some arguments separated by | character:",
            /*
            "  volume <0.0 to 100.0> = (default 1.0) sound volume, if exceeds 1.0 it extends range, each 1.0 extends range by about 10 blocks.",
            "  pitch <0.0 to 4.0>    = (default 0.0) sound pitch value.",
            "  player                = (default not set) if set it will only play the sound to the crafter.",
            */
            "You can specify these arguments in any order and they're completely optional.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag}",
                /*
            "{flag} level_up",
            "{flag} wolf_howl | volume 5 // can be heard loudly at 50 blocks away",
            "{flag} portal_travel | player | volume 0.65 | pitch 3.33",*/ };
    }

    public FlagSpawnParticle() {}

    public FlagSpawnParticle(FlagSpawnParticle flag) {

    }

    @Override
    public FlagSpawnParticle clone() {
        return new FlagSpawnParticle((FlagSpawnParticle) super.clone());
    }

    @Override
    public boolean onParse(String value) {
        String[] split = value.toLowerCase().split("\\|");

        value = split[0].trim().toUpperCase();

        return true;
    }

    @Override
    public void onCrafted(Args a) {
        if (!a.hasLocation()) {
            a.addCustomReason("Needs location!");
            return;
        }
        Location location = a.location();
        double offsetX = 0;
        double offsetY = 0;
        double offsetZ = 0;

        double x = location.getX() + offsetX;
        double y = location.getY() + offsetY;
        double z = location.getZ() + offsetZ;

        double randomOffsetX = 0;
        double randomOffsetY = 0;
        double randomOffsetZ = 0;

        int count = 1;

        Particle particle = Particle.NOTE;

        double extra = 1;

        a.location().getWorld().spawnParticle(particle, x, y, z, count, randomOffsetX, randomOffsetY, randomOffsetZ, extra);
    }
}
