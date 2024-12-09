package haveric.recipeManager.flag.flags.any;

import com.google.common.base.Preconditions;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.HashMap;
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
        try {
            if (!Biome.class.isEnum()) {
                return new String[]{
                    "{flag} " + Biome.JUNGLE.getKey().getKey().toLowerCase() + ", " + Biome.BAMBOO_JUNGLE.getKey().getKey().toLowerCase() + ", " + Biome.SPARSE_JUNGLE.getKey().getKey().toLowerCase(),
                    "{flag} !" + Biome.DRIPSTONE_CAVES.getKey().getKey().toLowerCase() + ", !" + Biome.LUSH_CAVES.getKey().getKey().toLowerCase()
                };
            } else {
                return new String[]{
                    "{flag} " + Biome.JUNGLE.name().toLowerCase() + ", " + Biome.BAMBOO_JUNGLE.name().toLowerCase() + ", " + Biome.SPARSE_JUNGLE.name().toLowerCase(),
                    "{flag} !" + Biome.DRIPSTONE_CAVES.name().toLowerCase() + ", !" + Biome.LUSH_CAVES.name().toLowerCase()
                };
            }
        } catch (NoClassDefFoundError e) {
            // Skip for tests
            return new String[]{""};
        }
    }

    private Map<Biome, Boolean> biomes = new HashMap<>();
    private String failMessage;

    public FlagBiome() {
    }

    public FlagBiome(FlagBiome flag) {
        super(flag);
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

    public void setBiomes(Map<Biome, Boolean> newBiomes) {
        Preconditions.checkNotNull(newBiomes, "The 'biomes' argument must not be null!");

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
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
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
