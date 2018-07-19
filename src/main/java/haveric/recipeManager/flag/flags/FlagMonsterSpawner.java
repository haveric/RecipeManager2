package haveric.recipeManager.flag.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BlockStateMeta;

public class FlagMonsterSpawner extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.MONSTER_SPAWNER;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <entity type>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Sets the entity type that will be spawned from the spawner.",
            "",
            "The <entity type> argument must be an entity type name, you can find them in '" + Files.FILE_INFO_NAMES + "' file at 'ENTITY TYPES' section.",
        };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} creeper",
            "{flag} horse", };
    }

    private EntityType entityType;

    public FlagMonsterSpawner() {
    }

    public FlagMonsterSpawner(FlagMonsterSpawner flag) {
        entityType = flag.entityType;
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
        if (Version.has1_13Support()) {
            spawnerMaterial = Material.SPAWNER;
        } else {
            spawnerMaterial = Material.getMaterial("MOB_SPAWNER");
        }

        if (result == null || !result.getType().equals(spawnerMaterial)) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a MOB_SPAWNER to work!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value) {
        value = value.trim().toUpperCase();

        try {
            setEntityType(EntityType.valueOf(value));
        } catch (IllegalArgumentException e) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid entity type name: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for entity type list.");
            return false;
        }

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        Player player = a.player();
        Location playerLocation = player.getLocation();
        Location loc = new Location(player.getWorld(), playerLocation.getBlockX(), 0, playerLocation.getBlockZ());
        Block block = loc.getBlock();
        BlockState originalState = block.getState();

        Material spawnerMaterial;
        if (Version.has1_13Support()) {
            spawnerMaterial = Material.SPAWNER;
        } else {
            spawnerMaterial = Material.getMaterial("MOB_SPAWNER");
        }
        block.setType(spawnerMaterial);

        CreatureSpawner s = (CreatureSpawner) loc.getBlock().getState();
        s.setSpawnedType(entityType);
        s.update();


        BlockStateMeta meta = (BlockStateMeta) a.result().getItemMeta();
        meta.setBlockState(s);
        a.result().setItemMeta(meta);


        block.setType(originalState.getType());
        block.setData(originalState.getRawData());
        originalState.update();
    }
}
