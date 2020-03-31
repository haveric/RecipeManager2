package haveric.recipeManager.flag.flags.any;

import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

public class FlagBiome extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.BIOME;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <types> | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Sets the biome required to allow crafting.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "For '<types>' you can list the biomes you want to allow or disallow.",
            "It needs at least one biome name and you can add more separated by , character.",
            "Also you can disallow biomes by prefixing them with ! character.",
            "Biomes: " + Files.getNameIndexHashLink("biomes"), };
    }

    @Override
    protected String[] getExamples() {
        String[] description = new String[]{
            "{flag} " + Biome.JUNGLE.name().toLowerCase() + ", " + Biome.JUNGLE_HILLS.name().toLowerCase(),
        };

        if (Version.has1_13BasicSupport()) {
            description = ObjectArrays.concat(description, new String[]{
                "{flag} !" + Biome.MUSHROOM_FIELDS.name().toLowerCase() + ", !" + Biome.MUSHROOM_FIELD_SHORE.name().toLowerCase(),
            }, String.class);
        } else if (Version.has1_9Support()) {
            description = ObjectArrays.concat(description, new String[]{
                "{flag} !mushroom_island, !mushroom_island_shore",
            }, String.class);
        } else {
            description = ObjectArrays.concat(description, new String[]{
                    "{flag} !mushroom_island, !mushroom_shore",
            }, String.class);
        }

        return description;
    }

    private Map<Biome, Boolean> biomes = new EnumMap<>(Biome.class);
    private String failMessage;

    public FlagBiome() {
    }

    public FlagBiome(FlagBiome flag) {
        biomes.putAll(flag.biomes);
        failMessage = flag.failMessage;
    }

    @Override
    public FlagBiome clone() {
        return new FlagBiome((FlagBiome) super.clone());
    }

    public Map<Biome, Boolean> getBiomes() {
        return biomes;
    }

    public void setBiomes(EnumMap<Biome, Boolean> newBiomes) {
        Validate.notNull(newBiomes, "The 'biomes' argument must not be null!");

        biomes = newBiomes;
    }

    public void addBiome(Biome biome, boolean allowed) {
        biomes.put(biome, allowed);
    }

    public String getBiomesString(boolean allowed) {
        StringBuilder s = new StringBuilder();

        for (Entry<Biome, Boolean> e : biomes.entrySet()) {
            if (allowed == e.getValue()) {
                if (s.length() > 0) {
                    s.append(", ");
                }

                s.append(e.getKey());
            }
        }

        return s.toString();
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String newFailMessage) {
        failMessage = newFailMessage;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        String[] split = value.split("\\|", 2);

        if (split.length > 1) {
            failMessage = RMCUtil.trimExactQuotes(split[1]);
        }

        split = split[0].trim().toLowerCase().split(",");

        biomes.clear();

        for (String arg : split) {
            arg = arg.trim();
            boolean not = arg.charAt(0) == '!';

            if (not) {
                arg = arg.substring(1).trim();
            }

            Biome biome = RMCUtil.parseEnum(arg, Biome.values());

            if (biome == null) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown biome: " + arg);
                continue;
            }

            addBiome(biome, !not);
        }

        return true;
    }

    @Override
    public void onCheck(Args a) {
        if (!a.hasLocation()) {
            a.addCustomReason("Needs location!");
            return;
        }

        Block b = a.location().getBlock();
        Boolean set = biomes.get(b.getBiome());

        if (set == null) {
            a.addReason("flag.biome.allowed", failMessage, "{biomes}", getBiomesString(true));
        } else if (!set) {
            a.addReason("flag.biome.unallowed", failMessage, "{biomes}", getBiomesString(false));
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        for (Map.Entry<Biome, Boolean> entry : biomes.entrySet()) {
            toHash += entry.getKey().toString() + "-" + entry.getValue().toString();
        }

        toHash += failMessage;

        return toHash.hashCode();
    }
}
