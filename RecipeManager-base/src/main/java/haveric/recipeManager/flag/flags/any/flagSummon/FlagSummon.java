package haveric.recipeManager.flag.flags.any.flagSummon;

import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.entity.Skeleton.SkeletonType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlagSummon extends Flag {

    private static String argFormat = "  %-26s = %s";
    private static String argFormatExtra = "  %-30s %s";

    @Override
    public String getFlagType() {
        return FlagType.SUMMON;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <type> | [arguments]", };
    }

    @Override
    protected String[] getDescription() {
        String[] description = new String[]{
                "Summons a living entity.",
                "Using this flag more than once will add more entities.",
                "",
                "The &lt;type&gt; argument can be a living entity type, you can find all entity types here: " + Files.getNameIndexHashLink("entitytype"),
                "",
                "Optionally you can add some arguments separated by | character, those being:",
                String.format(argFormat, "adult", "forces entity to spawn as an adult, works with animals and villagers (works opposite of baby)."),
                String.format(argFormat, "agelock", "prevent the entity from maturing or getting ready for mating, works with animals and villagers."),
                String.format(argFormat, "angry", "makes entity angry, only works for wolves and pigzombies; you can't use 'pet' with this."),
        };

        if (Version.has1_16Support()) {
            description = ObjectArrays.concat(description, new String[]{
                String.format(argFormat, "arrowcooldown <ticks>", "sets the ticks until the next arrow leaves the entity's body."),
                String.format(argFormat, "arrowsinbody <amount>", "sets the number of arrows in the entity's body."),
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[]{
            String.format(argFormat, "baby", "spawn entity as a baby, works with animals, villagers and zombies (works opposite of adult)."),
        }, String.class);

        if (Version.has1_15Support()) {
            description = ObjectArrays.concat(description, new String[]{
                String.format(argFormat, "beeanger <ticks>", "sets the anger level to the number of ticks the bee will remain angry for."),
                String.format(argFormat, "beecannotenterhiveticks <ticks>", "sets the ticks the bee cannot enter a hive for."),
                String.format(argFormat, "beehasnectar", "sets the bee to have nectar."),
                String.format(argFormat, "beehasstung", "sets if the bee has stung."),
            }, String.class);
        }

        if (Version.has1_14Support()) {
            description = ObjectArrays.concat(description, new String[]{
                String.format(argFormat, "cat <type>", "cat type, available values: " + RMCUtil.collectionToString(Arrays.asList(Cat.Type.values())).toLowerCase()),
            }, String.class);
        } else {
            //noinspection deprecation
            description = ObjectArrays.concat(description, new String[]{
                String.format(argFormat, "cat <type>", "ocelot type, available values: " + RMCUtil.collectionToString(Arrays.asList(Ocelot.Type.values())).toLowerCase()),
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[] {
            String.format(argFormat, "chance <0.01-100>%", "chance of the entity to spawn, this value is for individual entities."),
            String.format(argFormat, "chest <item> [drop%]", "equip an item on the entity's chest with optional drop chance."),
            String.format(argFormat, "color &lt;dye>&gt;", "sets the color of animal, only works for sheep and pet wolf/cats. Values: " + Files.getNameIndexHashLink("dyecolor")),
        }, String.class);

        if (!Version.has1_12Support()) {
            description = ObjectArrays.concat(description, new String[] {
                // ELDER_GUARDIAN is its own entity type now
                String.format(argFormat, "elder", "sets a guardian as an elder") }, String.class);
        }

        description = ObjectArrays.concat(description, new String[]{
            String.format(argFormat, "feet <item> [drop%]", "equip an item on the entity's feet with optional drop chance."),
        }, String.class);

        if (Version.has1_14Support()) {
            description = ObjectArrays.concat(description, new String[]{
                    String.format(argFormat, "fox <type>", "set the fox type, values: " + RMCUtil.collectionToString(Arrays.asList(Fox.Type.values())).toLowerCase()),
                    String.format(argFormat, "foxcrouching", "set the fox to be crouching"),
                    String.format(argFormat, "foxfirsttrustedplayer <uuid or player>", "set the fox's first trusted player. If set to 'player', the crafter will be used."),
                    String.format(argFormat, "foxsecondtrustedplayer <uuid or player>", "set the fox's second trusted player. If set to 'player', the crafter will be used."),
                    String.format(argFormat, "foxsleeping", "set the fox to be sleeping"),
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[] {
            String.format(argFormat, "hand <item> [drop%]", "equip an item on the entity's main hand with optional drop chance; for enderman it only uses material and data from the item."),
            String.format(argFormat, "head <item> [drop%]", "equip an item on the entity's head with optional drop chance."),
            String.format(argFormat, "hit", "crafter will fake-attack the entity to provoke it into attacking or scare it away."),
            String.format(argFormat, "haschest", "adds a chest to entity (Only works on horses, forces horse to be an adult and tamed)."),
        }, String.class);

        if (!Version.has1_12Support()) {
            //noinspection deprecation
            description = ObjectArrays.concat(description, new String[] {
                // Horse variants (DONKEY, MULE, ZOMBIE_HORSE, SKELETON_HORSE) are now their own entity types
                String.format(argFormat, "horse <type>", "set the horse type, values: " + RMCUtil.collectionToString(Arrays.asList(Horse.Variant.values())).toLowerCase()) }, String.class);
        }

        description = ObjectArrays.concat(description, new String[]{
            String.format(argFormat, "horsecolor <type>", "set the horse color, values: " + RMCUtil.collectionToString(Arrays.asList(Horse.Color.values())).toLowerCase()),
            String.format(argFormat, "horsestyle <type>", "set the horse style, values: " + RMCUtil.collectionToString(Arrays.asList(Horse.Style.values())).toLowerCase()),
            String.format(argFormat, "hp <health> [max]", "set entity's health and optionally max health."),
            String.format(argFormat, "offhand <item> [drop%]", "equip an item on the entity's offhand with optional drop chance."),
            String.format(argFormat, "invulnerable", "makes the entity invulnerable."),
        }, String.class);

        if (Version.has1_16Support()) {
            description = ObjectArrays.concat(description, new String[]{
                String.format(argFormat, "invisible", "makes the entity invisible."),
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[]{
            String.format(argFormat, "jumpstrength <0.0-2.0>", "sets the entity's jump strength (Only works for horses). 0 = no jump"),
            String.format(argFormat, "legs <item> [drop%]", "equip an item on the entity's legs with optional drop chance."),
            String.format(argFormat, "mountnext", "this entity will mount the next entity definition that triggers after it."),
            String.format(argFormat, "name <text>", "sets the entity's name, supports colors (<red>, &3, etc)."),
            String.format(argFormat, "noai", "disable the ai on entity."),
            String.format(argFormat, "nobreed", "prevent the entity being able to breed, works for animals and villagers."),
        }, String.class);

        if (Version.has1_9Support()) {
            description = ObjectArrays.concat(description, new String[]{
                String.format(argFormat, "nocollision", "disables collision with other entities."),
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[]{
            String.format(argFormat, "noeffect", "no spawning particle effects on entity."),
            String.format(argFormat, "nohidename", "don't hide name plate when not aiming at entity."),
            String.format(argFormat, "noremove", "prevents entity from being removed if nobody is near it."),
            String.format(argFormat, "num <number>", "spawn more cloned entities."),
            String.format(argFormat, "onfire <time>", "spawn entity on fire for <time> amount of seconds, value can be float."),
        }, String.class);

        if (Version.has1_14Support()) {
            description = ObjectArrays.concat(description, new String[]{
                    String.format(argFormat, "pandahiddengene <type>", "set the panda's hidden gene, values: " + RMCUtil.collectionToString(Arrays.asList(Panda.Gene.values())).toLowerCase()),
                    String.format(argFormat, "pandamaingene <type>", "set the panda's main gene, values: " + RMCUtil.collectionToString(Arrays.asList(Panda.Gene.values())).toLowerCase()),
            }, String.class);
        }

        if (Version.has1_12Support()) {
            description = ObjectArrays.concat(description, new String[]{
                    String.format(argFormat, "parrot <type>", "set the parrot type, values: " + RMCUtil.collectionToString(Arrays.asList(Parrot.Variant.values())).toLowerCase()),
            }, String.class);
        }

        if (Version.has1_13Support()) {
            description = ObjectArrays.concat(description, new String[]{
                    String.format(argFormat, "persistent", "Makes the entity persistent"),
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[]{
            String.format(argFormat, "pet [nosit]", "makes entity owned by crafter, only works for tameable entities, optionally specify 'nosit' to not spawn entity in sit stance."),
            String.format(argFormat, "pickup [true/false]", "change if entity can pick-up dropped items."),
            String.format(argFormat, "playerirongolem", "marks iron golem as player-made."),
            String.format(argFormat, "potion <type> [time] [amp]", "adds potion effect on the spawned entity; this argument can be used more than once to add more effects."),
            String.format(argFormatExtra, "", "type values: " + Files.getNameIndexHashLink("potioneffect")),
            String.format(argFormatExtra, "", "[time] can be a decimal of duration in seconds"),
            String.format(argFormatExtra, "", "[amp] can be an integer that defines amplifier;"),
            String.format(argFormat, "poweredcreeper", "makes creeper a powered one, only works for creepers."),
            String.format(argFormat, "rabbit <type>", "set the rabbit type, values: " + RMCUtil.collectionToString(Arrays.asList(Rabbit.Type.values())).toLowerCase()),
            String.format(argFormat, "saddle [mount]", "adds saddle on entity (forces animal to be adult), only works for pig and horse, optionally you can specify 'mount' to make crafter mount entity."),
            String.format(argFormat, "shearedsheep", "sets the sheep as sheared, only works for sheep."),
        }, String.class);

        if (!Version.has1_12Support()) {
            //noinspection deprecation
            description = ObjectArrays.concat(description, new String[] {
                // Skeleton variants (STRAY, WITHER_SKELETON) are now their own entity types
                String.format(argFormat, "skeleton <type>", "set the skeleton type, values: " + RMCUtil.collectionToString(Arrays.asList(SkeletonType.values())).toLowerCase()) }, String.class);
        }

        description = ObjectArrays.concat(description, new String[] {
                String.format(argFormat, "spread <range>", "spawns entities spread within block range instead of on top of workbench or furnace. (WARNING: can be CPU intensive)"),
                String.format(argFormat, "target", "entity targets crafter, that means monsters attack and animals follow and the rest do nothing"),
                String.format(argFormat, "villager <type>", "set the villager profession"),
                String.format(argFormatExtra, "", "Values: " + RMCUtil.collectionToString(Arrays.asList(Villager.Profession.values())).toLowerCase()),
        }, String.class);

        if (!Version.has1_12Support()) {
            description = ObjectArrays.concat(description, new String[]{
                String.format(argFormat, "zombievillager", "makes zombie a zombie villager, only works on zombies.") }, String.class);
        }

        description = ObjectArrays.concat(description, new String[] {
            "",
            "These arguments can be used in any order and they're all optional.", }, String.class);

        return description;
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} cow",
            "{flag} skeleton | hand bow // skeletons spawn without weapons, you need to give it one",
            "{flag} zombie | baby | chest chainmail_chestplate 25% | legs chainmail_leggings 25% | hand iron_sword 50% // baby zombie warrior",
            "{flag} sheep | color pink | name <light_purple>Pony",
            "{flag} ocelot | cat redcat | pet | potion speed 30 5",
            "// chicken on a villager and villager on a cow:",
            "{flag} chicken | mountnext",
            "{flag} villager | mountnext",
            "{flag} cow", };
    }


    private List<Customization> spawn = new ArrayList<>();

    public FlagSummon() { }

    public FlagSummon(FlagSummon flag) {
        super(flag);
        for (Customization c : flag.spawn) {
            spawn.add(c.clone());
        }
    }

    @Override
    public FlagSummon clone() {
        return new FlagSummon((FlagSummon) super.clone());
    }

    public List<Customization> getSpawnList() {
        return spawn;
    }

    public void setSpawnList(List<Customization> list) {
        if (list == null) {
            remove();
        } else {
            spawn = list;
        }
    }

    public void addSpawn(Customization newSpawn) {
        Validate.notNull(newSpawn, "'spawn' can not be null!");

        spawn.add(newSpawn);
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.split("\\|");

        value = split[0].trim();
        EntityType entityType = RMCUtil.parseEnum(value, EntityType.values());

        if (entityType == null || !entityType.isAlive()) {
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid entity: " + value, "Look in '" + Files.FILE_INFO_NAMES + "' at 'ENTITY TYPES' section for ALIVE entities.");
        }

        Customization c = new Customization(getFlagType(), entityType);

        if (split.length > 1) {
            for (int n = 1; n < split.length; n++) {
                String original = split[n].trim();

                if (!c.parseArgument(original)) {
                    continue;
                }
            }
        }

        if (entityType == EntityType.WOLF) {
            if (c.isPet()) {
                if (c.isAngry()) {
                    c.setAngry(false);
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'angry' with 'pet' on wolf! Argument 'angry' ignored.");
                }
            } else {
                if (c.getColor() != null) {
                    c.setColor(null);
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'color' argument without wolf being a pet, ignored.");
                }
            }
        }

        addSpawn(c);

        return true;
    }

    @Override
    public void onCrafted(Args a) {
        if (!a.hasLocation()) {
            a.addCustomReason("Needs location!");
            return;
        }

        for (Customization c : spawn) {
            if (c.isPet() || c.isTarget() || (c.isSaddle() && c.isMount())) {
                if (!a.hasPlayer()) {
                    a.addCustomReason("Needs player!");
                    return;
                }

                break;
            }
        }

        Location l = a.location();

        if (l.getX() == l.getBlockX()) {
            l.add(0.5, 1.5, 0.5);
        }

        List<LivingEntity> toMount = null;

        for (Customization c : spawn) {
            if (c.getChance() < 100.0f && c.getChance() < (RecipeManager.random.nextFloat() * 100)) {
                continue;
            }

            List<LivingEntity> spawned = c.spawn(l, a.player());

            if (toMount != null) {
                for (int i = 0; i < Math.min(spawned.size(), toMount.size()); i++) {
                    spawned.get(i).addPassenger(toMount.get(i));
                }
            }

            if (c.isMountNext()) {
                toMount = spawned;
            } else {
                toMount = null;
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        for (Customization customization : spawn) {
            toHash += customization.hashCode();
        }

        return toHash.hashCode();
    }
}
