package haveric.recipeManager.flag.flags.any.flagSummon;

import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsEntity;
import haveric.recipeManager.tools.Version;
import haveric.recipeManager.common.util.RMCUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
                "Summons a creature.",
                "Using this flag more than once will add more creatures.",
                "",
                "The &lt;type&gt; argument can be a living entity type, you can find all entity types here: " + Files.getNameIndexHashLink("entitytype"),
                "",
                "Optionally you can add some arguments separated by | character, those being:",
                String.format(argFormat, "adult", "forces creature to spawn as an adult, works with animals and villagers (works opposite of baby)."),
                String.format(argFormat, "agelock", "prevent the creature from maturing or getting ready for mating, works with animals and villagers."),
                String.format(argFormat, "angry", "makes creature angry, only works for wolves and pigzombies; you can't use 'pet' with this."),
                String.format(argFormat, "baby", "spawn creature as a baby, works with animals, villagers and zombies (works opposite of adult)."),
        };
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
            String.format(argFormat, "chance <0.01-100>%", "chance of the creature to spawn, this value is for individual creatures."),
            String.format(argFormat, "chest <item> [drop%]", "equip an item on the creature's chest with optional drop chance."),
            String.format(argFormat, "color &lt;dye>&gt;", "sets the color of animal, only works for sheep and pet wolf/cats. Values: " + Files.getNameIndexHashLink("dyecolor")),
        }, String.class);

        if (!Version.has1_12Support()) {
            description = ObjectArrays.concat(description, new String[] {
                // ELDER_GUARDIAN is its own entity type now
                String.format(argFormat, "elder", "sets a guardian as an elder") }, String.class);
        }

        description = ObjectArrays.concat(description, new String[]{
            String.format(argFormat, "feet <item> [drop%]", "equip an item on the creature's feet with optional drop chance."),
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
            String.format(argFormat, "hand <item> [drop%]", "equip an item on the creature's main hand with optional drop chance; for enderman it only uses material and data from the item."),
            String.format(argFormat, "head <item> [drop%]", "equip an item on the creature's head with optional drop chance."),
            String.format(argFormat, "hit", "crafter will fake-attack the creature to provoke it into attacking or scare it away."),
            String.format(argFormat, "haschest", "adds a chest to creature (Only works on horses, forces horse to be an adult and tamed)."),
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
            String.format(argFormat, "hp <health> [max]", "set creature's health and optionally max health."),
            String.format(argFormat, "offhand <item> [drop%]", "equip an item on the creature's offhand with optional drop chance."),
            String.format(argFormat, "invulnerable", "makes the creature invulnerable."),
        }, String.class);

        if (Version.has1_16Support()) {
            description = ObjectArrays.concat(description, new String[]{
                String.format(argFormat, "invisible", "makes the creature invisible."),
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[]{
            String.format(argFormat, "jumpstrength <0.0-2.0>", "sets the creature's jump strength (Only works for horses). 0 = no jump"),
            String.format(argFormat, "legs <item> [drop%]", "equip an item on the creature's legs with optional drop chance."),
            String.format(argFormat, "mountnext", "this creature will mount the next creature definition that triggers after it."),
            String.format(argFormat, "noai", "disable the ai on creature."),
            String.format(argFormat, "noeffect", "no spawning particle effects on creature."),
            String.format(argFormat, "noremove", "prevents creature from being removed if nobody is near it."),
            String.format(argFormat, "name <text>", "sets the creature's name, supports colors (<red>, &3, etc)."),
            String.format(argFormat, "nobreed", "prevent the creature being able to breed, works for animals and villagers."),
            String.format(argFormat, "nohidename", "don't hide name plate when not aiming at creature."),
            String.format(argFormat, "num <number>", "spawn more cloned creatures."),
            String.format(argFormat, "onfire <time>", "spawn creature on fire for <time> amount of seconds, value can be float."),
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
                    String.format(argFormat, "persistent", "Makes the creature persistent"),
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[]{
            String.format(argFormat, "pet [nosit]", "makes creature owned by crafter, only works for tameable creatures, optionally specify 'nosit' to not spawn creature in sit stance."),
            String.format(argFormat, "pickup [true/false]", "change if creature can pick-up dropped items."),
            String.format(argFormat, "playerirongolem", "marks iron golem as player-made."),
            String.format(argFormat, "potion <type> [time] [amp]", "adds potion effect on the spawned creature; this argument can be used more than once to add more effects."),
            String.format(argFormatExtra, "", "type values: " + Files.getNameIndexHashLink("potioneffect")),
            String.format(argFormatExtra, "", "[time] can be a decimal of duration in seconds"),
            String.format(argFormatExtra, "", "[amp] can be an integer that defines amplifier;"),
            String.format(argFormat, "poweredcreeper", "makes creeper a powered one, only works for creepers."),
            String.format(argFormat, "rabbit <type>", "set the rabbit type, values: " + RMCUtil.collectionToString(Arrays.asList(Rabbit.Type.values())).toLowerCase()),
            String.format(argFormat, "saddle [mount]", "adds saddle on creature (forces animal to be adult), only works for pig and horse, optionally you can specify 'mount' to make crafter mount creature."),
            String.format(argFormat, "shearedsheep", "sets the sheep as sheared, only works for sheep."),
        }, String.class);

        if (!Version.has1_12Support()) {
            //noinspection deprecation
            description = ObjectArrays.concat(description, new String[] {
                // Skeleton variants (STRAY, WITHER_SKELETON) are now their own entity types
                String.format(argFormat, "skeleton <type>", "set the skeleton type, values: " + RMCUtil.collectionToString(Arrays.asList(SkeletonType.values())).toLowerCase()) }, String.class);
        }

        description = ObjectArrays.concat(description, new String[] {
                String.format(argFormat, "spread <range>", "spawns creature(s) spread within block range instead of on top of workbench or furnace. (WARNING: can be CPU intensive)"),
                String.format(argFormat, "target", "creature targets crafter, that means monsters attack and animals follow and the rest do nothing"),
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
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid creature: " + value, "Look in '" + Files.FILE_INFO_NAMES + "' at 'ENTITY TYPES' section for ALIVE entities.");
        }

        Customization c = new Customization(getFlagType(), entityType);

        if (split.length > 1) {
            for (int n = 1; n < split.length; n++) {
                String original = split[n].trim();
                value = original.toLowerCase();

                if (value.equals("noremove")) {
                    c.setNoRemove(true);
                } else if (value.equals("invulnerable")) {
                    c.setInvulnerable(true);
                } else if (value.equals("invisible")) {
                    c.setInvisible(true);
                } else if (value.equals("persistent")) {
                    c.setPersistent(true);
                } else if (value.equals("noai")) {
                    c.setNoAi(true);
                } else if (value.equals("noeffect")) {
                    c.setNoEffect(true);
                } else if (value.equals("target")) {
                    c.setTarget(true);
                } else if (value.equals("nohidename")) {
                    c.setNoHideName(true);
                } else if (value.equals("mountnext")) {
                    c.setMountNext(true);
                } else if (value.equals("angry")) {
                    boolean error = false;
                    if (Version.has1_16Support()) {
                        switch (entityType) {
                            case WOLF:
                            case ZOMBIFIED_PIGLIN:
                                break;

                            default:
                                error = true;
                        }
                    } else {
                        if (entityType != EntityType.WOLF && entityType != EntityType.valueOf("PIG_ZOMBIE")) {
                            error = true;
                        }
                    }

                    if (error) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'angry' on unsupported creature!");
                        continue;
                    }

                    c.setAngry(true);
                } else if (value.equals("shearedsheep")) {
                    if (entityType != EntityType.SHEEP) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'shearedsheep' on non-sheep creature!");
                        continue;
                    }

                    c.setShearedSheep(true);
                } else if (!Version.has1_12Support() && value.equals("zombievillager")) {
                    if (entityType != EntityType.ZOMBIE) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'zombievillager' on non-zombie creature!");
                        continue;
                    }

                    c.setZombieVillager(true);
                } else if (value.equals("poweredcreeper")) {
                    if (entityType != EntityType.CREEPER) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'poweredcreeper' on non-creeper creature!");
                        continue;
                    }

                    c.setPoweredCreeper(true);
                } else if (value.equals("playerirongolem")) {
                    if (entityType != EntityType.IRON_GOLEM) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'playerirongolem' on non-irongolem creature!");
                        continue;
                    }

                    c.setPlayerIronGolem(true);
                } else if (value.equals("hit")) {
                    c.setHit(true);
                } else if (value.equals("adult")) {
                    if (ToolsEntity.isAgeable(entityType)) {
                        c.setAdult(true);
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'adult' set on unsupported creature!");
                    }
                } else if (value.equals("baby")) {
                    if (ToolsEntity.isAgeable(entityType) || entityType == EntityType.ZOMBIE) {
                        c.setBaby(true);
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'baby' set on unsupported creature!");
                    }
                } else if (value.equals("agelock")) {
                    if (ToolsEntity.isAgeable(entityType)) {
                        c.setAgeLock(true);
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'agelock' set on unsupported creature!");
                    }
                } else if (value.equals("nobreed")) {
                    if (ToolsEntity.isAgeable(entityType)) {
                        c.setNoBreed(true);
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'nobreed' set on unsupported creature!");
                    }
                } else if (value.startsWith("pickup")) {
                    value = value.substring("pickup".length()).trim();

                    if (value.isEmpty()) {
                        c.setPickup(true);
                    } else {
                        c.setPickup(value.equals("true"));
                    }
                } else if (value.startsWith("pet")) {
                    if (ToolsEntity.isTameable(entityType)) {
                        c.setPet(true);

                        if (value.length() > "pet".length()) {
                            value = value.substring("pet".length()).trim();

                            if (value.equals("nosit")) {
                                c.setNoSit(true);
                            } else {
                                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'pet' argument with unknown value: " + value);
                            }
                        }
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'pet' on untameable creature!");
                    }
                } else if (value.startsWith("saddle")) {
                    if (entityType != EntityType.PIG && entityType != EntityType.HORSE ||
                            (Version.has1_12Support() && entityType != EntityType.SKELETON_HORSE && entityType != EntityType.ZOMBIE_HORSE && entityType != EntityType.MULE && entityType != EntityType.DONKEY)) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'saddle' on non-pig and non-horse creature!");
                        continue;
                    }

                    c.setSaddle(true);

                    if (value.length() > "saddle".length()) {
                        value = value.substring("saddle".length()).trim();

                        if (value.equals("mount")) {
                            c.setMount(true);
                        } else {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'saddle' argument with unknown value: " + value);
                        }
                    }
                } else if (value.startsWith("chance")) {
                    value = value.substring("chance".length()).trim();

                    if (value.charAt(value.length() - 1) == '%') {
                        value = value.substring(0, value.length() - 1);
                    }

                    try {
                        c.setChance(Float.parseFloat(value));
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'chance' argument with invalid number: " + value);
                    }
                } else if (value.startsWith("jumpstrength")) {
                    value = value.substring("jumpstrength".length()).trim();

                    try {
                        c.setJumpStrength(Float.parseFloat(value));
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'jumpstrength' argument with invalid number: " + value);
                    }
                } else if (value.startsWith("num")) {
                    value = value.substring("num".length()).trim();

                    try {
                        c.setNum(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'num' argument with invalid value number: " + value);
                    }
                } else if (value.startsWith("spread")) {
                    value = value.substring("spread".length()).trim();

                    try {
                        c.setSpread(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'spread' argument with invalid value number: " + value);
                    }
                } else if (value.startsWith("onfire")) {
                    value = value.substring("onfire".length()).trim();

                    try {
                        c.setOnFire(Float.parseFloat(value) * 20.0f);
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'onfire' argument with invalid value number: " + value);
                    }
                } else if (value.startsWith("color")) {
                    switch (entityType) {
                        case SHEEP:
                        case WOLF:
                            break;

                        case CAT:
                            break;

                        default:
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'color' on unsupported creature!");
                            continue;
                    }

                    value = value.substring("color".length()).trim();

                    c.setColor(RMCUtil.parseEnum(value, DyeColor.values()));

                    if (c.getColor() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'color' argument with invalid dye color: " + value);
                    }
                } else if (value.startsWith("villager")) {
                    if (entityType != EntityType.VILLAGER) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'villager' argument on non-villager creature!");
                        continue;
                    }

                    value = value.substring("villager".length()).trim();

                    c.setVillager(RMCUtil.parseEnum(value, Villager.Profession.values()));

                    if (c.getVillager() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'villager' argument with invalid entityType: " + value);
                    }
                } else if (!Version.has1_12Support() && value.startsWith("skeleton")) {
                    if (entityType != EntityType.SKELETON) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'skeleton' argument on non-skeleton creature!");
                        continue;
                    }

                    value = value.substring("skeleton".length()).trim();

                    //noinspection deprecation
                    c.setSkeleton(RMCUtil.parseEnum(value, SkeletonType.values()));

                    if (c.getSkeleton() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'skeleton' argument with invalid entityType: " + value);
                    }
                } else if (Version.has1_14Support() && value.startsWith("cat")) {
                    if (entityType != EntityType.CAT) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'cat' argument on non-cat creature!");
                        continue;
                    }

                    value = value.substring("cat".length()).trim();

                    c.setCat(RMCUtil.parseEnum(value, Cat.Type.values()));

                    if (c.getCat() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'cat' argument with invalid entityType: " + value);
                    }
                } else if (!Version.has1_14Support() && value.startsWith("cat")) {
                    if (entityType != EntityType.OCELOT) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'cat' argument on non-ocelot creature!");
                        continue;
                    }

                    value = value.substring("cat".length()).trim();

                    //noinspection deprecation
                    c.setOcelot(RMCUtil.parseEnum(value, Ocelot.Type.values()));

                    if (c.getOcelot() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'cat' argument with invalid entityType: " + value);
                    }
                } else if (value.startsWith("rabbit")) {
                    if (entityType != EntityType.RABBIT) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'rabbit' argument on non-rabbit creature!");
                        continue;
                    }

                    value = value.substring("rabbit".length()).trim();

                    c.setRabbit(RMCUtil.parseEnum(value, Rabbit.Type.values()));

                    if (c.getRabbit() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'rabbit' argument with invalid entityType: " + value);
                    }
                } else if (value.startsWith("name")) {
                    value = original.substring("name".length()).trim();

                    c.setName(value);
                } else if (value.startsWith("hp")) {
                    value = value.substring("hp".length()).trim();

                    String[] args = value.split(" ");

                    value = args[0].trim();

                    try {
                        c.setHp(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'hp' argument with invalid number: " + value);
                        continue;
                    }

                    if (args.length > 1) {
                        value = args[1].trim();

                        try {
                            c.setMaxHp(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'hp' argument with invalid number for maxhp: " + value);
                        }
                    }
                } else if (value.startsWith("potion")) {
                    value = value.substring("potion".length()).trim();
                    String[] args = value.split(" ");
                    value = args[0].trim();

                    PotionEffectType effect = PotionEffectType.getByName(value); // Tools.parseEnum(value, PotionEffectType.values());

                    if (effect == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'potion' argument with invalid entityType: " + value);
                        continue;
                    }

                    float duration = 1;
                    int amplifier = 0;

                    if (args.length > 1) {
                        value = args[1].trim();

                        try {
                            duration = Float.parseFloat(value);
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'potion' argument with invalid number for duration: " + value);
                            continue;
                        }
                    }

                    if (args.length > 2) {
                        value = args[2].trim();

                        try {
                            amplifier = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'potion' argument with invalid number for amplifier: " + value);
                            continue;
                        }
                    }

                    c.addPotionEffect(effect, duration, amplifier);
                } else if (value.startsWith("hand") || value.startsWith("mainhand") || value.startsWith("offhand") || value.startsWith("hold") || value.startsWith("head") || value.startsWith("helmet") || value.startsWith("chest") || value.startsWith("leg") || value.startsWith("feet") || value.startsWith("boot")) {
                    int index = -1;

                    switch (value.charAt(0)) {
                        case 'h':
                            switch (value.charAt(1)) {
                                case 'e':
                                    index = 0;
                                    break;

                                case 'o':
                                case 'a':
                                    index = 4;
                                    break;
                                default:
                                    break;
                            }
                            break;

                        case 'c':
                            index = 1;
                            break;

                        case 'l':
                            index = 2;
                            break;

                        case 'b':
                        case 'f':
                            index = 3;
                            break;
                        case 'm':
                            index = 4;
                            break;
                        case 'o':
                            index = 5;
                            break;
                        default:
                            break;
                    }

                    if (index < 0) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + value);
                        continue;
                    }

                    int i = value.indexOf(' ');
                    String[] args = value.substring(i + 1).trim().split(" ");
                    value = args[0].trim();

                    ItemStack item = Tools.parseItem(value, 0);

                    if (item == null) {
                        continue;
                    }

                    c.setEquip(item, index);

                    if (args.length > 1) {
                        value = args[1].trim();

                        if (value.charAt(value.length() - 1) == '%') {
                            value = value.substring(0, value.length() - 1);
                        }

                        try {
                            c.setDrop( Math.min(Math.max(Float.parseFloat(value), 0), 100), index);
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'chance' argument with invalid number: " + value);
                        }
                    }
                } else if (!Version.has1_12Support() && value.startsWith("horse")) {
                    if (entityType != EntityType.HORSE) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'horse' argument on non-horse creature!");
                        continue;
                    }

                    value = value.substring("horse".length()).trim();

                    //noinspection deprecation
                    c.setHorseVariant(RMCUtil.parseEnum(value, Horse.Variant.values()));

                    if (c.getHorseVariant() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'horse' argument with invalid entityType: " + value);
                    }
                } else if (value.startsWith("horsecolor")) {
                    if (entityType != EntityType.HORSE) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'horsecolor' argument on non-horse creature!");
                        continue;
                    }

                    value = value.substring("horsecolor".length()).trim();

                    c.setHorseColor(RMCUtil.parseEnum(value, Horse.Color.values()));

                    if (c.getHorseColor() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'horsecolor' argument with invalid entityType: " + value);
                    }
                } else if (value.startsWith("horsestyle")) {
                    if (entityType != EntityType.HORSE) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'horsestyle' argument on non-horse creature!");
                        continue;
                    }

                    value = value.substring("horsestyle".length()).trim();

                    c.setHorseStyle(RMCUtil.parseEnum(value, Horse.Style.values()));

                    if (c.getHorseStyle() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'horsestyle' argument with invalid entityType: " + value);
                    }
                } else if (value.equals("haschest")) {
                    c.setHasChest(true);
                } else if (!Version.has1_12Support() && value.equals("elder")) {
                    if (entityType != EntityType.GUARDIAN) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'elder' on non-guardian creature!");
                        continue;
                    }

                    c.setPoweredCreeper(true);
                } else if (Version.has1_12Support() && value.startsWith("parrot")) {
                    if (entityType != EntityType.PARROT) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'parrot' argument on non-parrot creature!");
                        continue;
                    }

                    value = value.substring("parrot".length()).trim();

                    c.setParrotVariant(RMCUtil.parseEnum(value, Parrot.Variant.values()));

                    if (c.getParrotVariant() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'parrot' argument with invalid entityType: " + value);
                    }
                } else if (Version.has1_14Support() && value.startsWith("pandamaingene")) {
                    if (entityType != EntityType.PANDA) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'pandamaingene' argument on non-panda creature!");
                        continue;
                    }

                    value = value.substring("pandamaingene".length()).trim();

                    c.setPandaMainGene(RMCUtil.parseEnum(value, Panda.Gene.values()));

                    if (c.getPandaMainGene() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'pandamaingene' argument with invalid entityType: " + value);
                    }
                } else if (Version.has1_14Support() && value.startsWith("pandahiddengene")) {
                    if (entityType != EntityType.PANDA) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'pandahiddengene' argument on non-panda creature!");
                        continue;
                    }

                    value = value.substring("pandahiddengene".length()).trim();

                    c.setPandaHiddenGene(RMCUtil.parseEnum(value, Panda.Gene.values()));

                    if (c.getPandaHiddenGene() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'pandahiddengene' argument with invalid entityType: " + value);
                    }
                } else if (Version.has1_14Support() && value.startsWith("fox")) {
                    String[] foxSplit = value.split(" ");
                    if (entityType != EntityType.FOX) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has '" + foxSplit[0] + "' argument on non-fox creature!");
                    }

                    if (value.startsWith("foxcrouching")) {
                        c.setFoxCrouching(true);
                    } else if (value.startsWith("foxfirsttrustedplayer")) {
                        value = value.substring("foxfirsttrustedplayer".length()).trim();

                        if (value.equals("player")) {
                            c.setFoxFirstTrustedPlayer(true);
                        } else {
                            try {
                                UUID uuid = UUID.fromString(value);
                                c.setFoxFirstTrustedPlayerUUID(uuid);
                            } catch (IllegalArgumentException e) {
                                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'foxfirsttrustedplayer' with invalid uuid: " + value);
                            }
                        }
                    } else if (value.startsWith("foxsecondtrustedplayer")) {
                        value = value.substring("foxsecondtrustedplayer".length()).trim();

                        if (value.equals("player")) {
                            c.setFoxSecondTrustedPlayer(true);
                        } else {
                            try {
                                UUID uuid = UUID.fromString(value);
                                c.setFoxSecondTrustedPlayerUUID(uuid);
                            } catch (IllegalArgumentException e) {
                                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'foxsecondtrustedplayer' with invalid uuid: " + value);
                            }
                        }
                    } else if (value.startsWith("foxsleeping")) {
                        c.setFoxSleeping(true);
                    } else if (value.startsWith("fox")) {
                        value = value.substring("fox".length()).trim();

                        c.setFoxType(RMCUtil.parseEnum(value, Fox.Type.values()));

                        if (c.getFoxType() == null) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'fox' argument with invalid entityType: " + value);
                        }
                    }
                } else {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + value);
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
