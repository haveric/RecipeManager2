package haveric.recipeManager.flag.flags.result;

import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagMonsterSpawner extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.MONSTER_SPAWNER;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <entity type> | [arguments]", };
    }

    @Override
    protected String[] getDescription() {
        String[] description = new String[] {
            "Sets the entity type that will be spawned from the spawner.",
            "",
            "The &lt;entity type&gt; argument must be an entity type name, see " + Files.getNameIndexHashLink("entitytype"),
            "",
            "Optionally you can add more arguments separated by | character in any order:",
        };

        if (Version.has1_12Support()) {
            description = ObjectArrays.concat(description, new String[]{
                "  delay             = (default 20) initial delay in ticks, -1 will default to a random value between min delay and max delay",
                "  mindelay          = (default 200) Sets the min spawn delay (in ticks)",
                "  maxdelay          = (default 800) Sets the max spawn delay (in ticks)",
                "  maxnearbyentities = (default 6) Sets the max number of similar entities that are allowed to be within spawning range.",
                "  playerrange       = (default 16) Sets the maximum distance (squared) a player can be in order for this spawner to be active. (0 is always active if players are online)",
                "  spawnrange        = (default 4) Sets the radius around which the spawner will attempt to spawn mobs in.",
                "  spawncount        = (default 4) Sets how many mobs attempt to spawn.",
            }, String.class);
        } else {
            description = ObjectArrays.concat(description, new String[]{
                "  delay = delay in ticks, ",
            }, String.class);
        }

        return description;
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} creeper",
            "{flag} horse", };
    }

    private EntityType entityType;
    private int delay = 20;
    private int minDelay = 200;
    private int maxDelay = 800;
    private int maxNearbyEntities = 6;
    private int playerRange = 16;
    private int spawnRange = 4;
    private int spawnCount = 4;

    public FlagMonsterSpawner() {
    }

    public FlagMonsterSpawner(FlagMonsterSpawner flag) {
        entityType = flag.entityType;
        delay = flag.delay;
        minDelay = flag.minDelay;
        maxDelay = flag.maxDelay;
        maxNearbyEntities = flag.maxNearbyEntities;
        playerRange = flag.playerRange;
        spawnRange = flag.spawnRange;
        spawnCount = flag.spawnCount;
    }

    @Override
    public FlagMonsterSpawner clone() {
        return new FlagMonsterSpawner((FlagMonsterSpawner) super.clone());
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType newEntityType) {
        Validate.notNull(newEntityType, "The entity type argument can not be null!");

        entityType = newEntityType;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();

        Material spawnerMaterial;
        if (Version.has1_13BasicSupport()) {
            spawnerMaterial = Material.SPAWNER;
        } else {
            spawnerMaterial = Material.getMaterial("MOB_SPAWNER");
        }

        if (result == null || !result.getType().equals(spawnerMaterial)) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a " + spawnerMaterial + " to work!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);

        String[] split = value.toLowerCase().split("\\|");

        String entityType = split[0].trim().toUpperCase();
        try {
            setEntityType(EntityType.valueOf(entityType));
        } catch (IllegalArgumentException e) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid entity type name: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for entity type list.");
            return false;
        }

        for (int i = 1; i < split.length; i++) {
            String s = split[i].trim().toLowerCase();

            if (s.startsWith("delay")) {
                s = s.substring("delay".length()).trim();
                try {
                    int parsed = Integer.parseInt(s);

                    if (parsed < -1 ) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid delay value: " + s + ". Expecting an integer >= -1.");
                    } else {
                        delay = parsed;
                    }
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid delay value: " + s + ". Expecting an integer >= -1.");
                }
            } else if (Version.has1_12Support()) {
                if (s.startsWith("mindelay")) {
                    s = s.substring("mindelay".length()).trim();
                    try {
                        int parsed = Integer.parseInt(s);

                        if (parsed < 0) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid mindelay value: " + s + ". Expecting an integer >= 0.");
                        } else {
                            minDelay = parsed;
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid mindelay value: " + s + ". Expecting an integer >= 0.");
                    }
                } else if (s.startsWith("maxdelay")) {
                    s = s.substring("maxdelay".length()).trim();
                    try {
                        int parsed = Integer.parseInt(s);

                        if (parsed < 0) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid maxdelay value: " + s + ". Expecting an integer >= 0.");
                        } else {
                            maxDelay = parsed;
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid maxdelay value: " + s + ". Expecting an integer >= 0.");
                    }
                } else if (s.startsWith("maxnearbyentities")) {
                    s = s.substring("maxnearbyentities".length()).trim();
                    try {
                        int parsed = Integer.parseInt(s);

                        if (parsed < 0) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid maxnearbyentities value: " + s + ". Expecting an integer >= 0.");
                        } else {
                            maxNearbyEntities = parsed;
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid maxnearbyentities value: " + s + ". Expecting an integer >= 0.");
                    }
                } else if (s.startsWith("playerrange")) {
                    s = s.substring("playerrange".length()).trim();
                    try {
                        int parsed = Integer.parseInt(s);

                        if (parsed < 0) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid playerrange value: " + s + ". Expecting an integer >= 0.");
                        } else {
                            playerRange = parsed;
                        }

                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid playerrange value: " + s + ". Expecting an integer >= 0.");
                    }
                } else if (s.startsWith("spawnrange")) {
                    s = s.substring("spawnrange".length()).trim();
                    try {
                        int parsed = Integer.parseInt(s);

                        if (parsed < 0) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid spawnrange value: " + s + ". Expecting an integer >= 0.");
                        } else {
                            spawnRange = parsed;
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid spawnrange value: " + s + ". Expecting an integer >= 0.");
                    }
                } else if (s.startsWith("spawncount")) {
                    s = s.substring("spawncount".length()).trim();
                    try {
                        int parsed = Integer.parseInt(s);

                        if (parsed < 0) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid spawncount value: " + s + ". Expecting an integer >= 0.");
                        } else {
                            spawnCount = parsed;
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid spawncount value: " + s + ". Expecting an integer >= 0.");
                    }
                } else {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + s);
                }
            }
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
            if (!(meta instanceof BlockStateMeta)) {
                return;
            }

            BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
            BlockState blockState = blockStateMeta.getBlockState();

            CreatureSpawner spawner = (CreatureSpawner) blockState;
            spawner.setSpawnedType(entityType);

            spawner.setDelay(delay);

            if (Version.has1_12Support()) {
                spawner.setMinSpawnDelay(minDelay);
                spawner.setMaxSpawnDelay(maxDelay);
                spawner.setMaxNearbyEntities(maxNearbyEntities);
                spawner.setRequiredPlayerRange(playerRange);
                spawner.setSpawnRange(spawnRange);
                spawner.setSpawnCount(spawnCount);
            }

            spawner.update();

            blockStateMeta.setBlockState(spawner);
            a.result().setItemMeta(blockStateMeta);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "entityType: " + entityType.toString();

        return toHash.hashCode();
    }
}
