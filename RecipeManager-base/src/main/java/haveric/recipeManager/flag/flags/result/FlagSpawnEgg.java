package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

public class FlagSpawnEgg extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.SPAWN_EGG;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <entity type>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Sets the entity type that will be spawned from the spawn egg.",
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

    public FlagSpawnEgg() {
    }

    public FlagSpawnEgg(FlagSpawnEgg flag) {
        entityType = flag.entityType;
    }

    @Override
    public FlagSpawnEgg clone() {
        return new FlagSpawnEgg((FlagSpawnEgg) super.clone());
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

        if (result == null || !(result.getItemMeta() instanceof SpawnEggMeta)) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a spawn egg item!");
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
        onCrafted(a);
    }

    @Override
    public void onCrafted(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        ItemMeta meta = a.result().getItemMeta();

        if (!(meta instanceof SpawnEggMeta)) {
            a.addCustomReason("Needs Spawn Egg");
            return;
        }

        SpawnEggMeta spawnEggMeta = (SpawnEggMeta) meta;
        spawnEggMeta.setSpawnedType(entityType);

        a.result().setItemMeta(spawnEggMeta);
    }
}
