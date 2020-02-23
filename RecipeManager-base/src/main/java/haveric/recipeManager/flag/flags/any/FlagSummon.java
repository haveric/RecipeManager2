package haveric.recipeManager.flag.flags.any;

import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsEntity;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FlagSummon extends Flag {

    private static String argFormat = "  %-26s = %s";

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
                "The <type> argument can be a living entity type, you can find all entity types in '" + Files.FILE_INFO_NAMES + "' file.",
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
            description = ObjectArrays.concat(description, new String[]{
                String.format(argFormat, "cat <type>", "ocelot type, available values: " + RMCUtil.collectionToString(Arrays.asList(Ocelot.Type.values())).toLowerCase()),
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[] {
            String.format(argFormat, "chance <0.01-100>%", "chance of the creature to spawn, this value is for individual creatures."),
            String.format(argFormat, "chest <item> [drop%]", "equip an item on the creature's chest with optional drop chance."),
            String.format(argFormat, "color <dye>", "sets the color of animal, only works for sheep and pet wolf/cats; values can be found in '" + Files.FILE_INFO_NAMES + "' file at 'DYE COLORS' section."),
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

        description = ObjectArrays.concat(description, new String[]{
            String.format(argFormat, "pet [nosit]", "makes creature owned by crafter, only works for tameable creatures, optionally specify 'nosit' to not spawn creature in sit stance."),
            String.format(argFormat, "pickup [true/false]", "change if creature can pick-up dropped items."),
            String.format(argFormat, "playerirongolem", "marks iron golem as player-made."),
            String.format(argFormat, "potion <type> [time] [amp]", "adds potion effect on the spawned creature; for <type> see '" + Files.FILE_INFO_NAMES + "' at 'POTION EFFECT TYPE'; [time] can be a decimal of duration in seconds; [amp] can be an integer that defines amplifier; this argument can be used more than once to add more effects."),
            String.format(argFormat, "poweredcreeper", "makes creeper a powered one, only works for creepers."),
            String.format(argFormat, "rabbit <type>", "set the rabbit type, values: " + RMCUtil.collectionToString(Arrays.asList(Rabbit.Type.values())).toLowerCase()),
            String.format(argFormat, "saddle [mount]", "adds saddle on creature (forces animal to be adult), only works for pig and horse, optionally you can specify 'mount' to make crafter mount creature."),
            String.format(argFormat, "shearedsheep", "sets the sheep as sheared, only works for sheep."),
        }, String.class);

        if (!Version.has1_12Support()) {
            description = ObjectArrays.concat(description, new String[] {
                // Skeleton variants (STRAY, WITHER_SKELETON) are now their own entity types
                String.format(argFormat, "skeleton <type>", "set the skeleton type, values: " + RMCUtil.collectionToString(Arrays.asList(SkeletonType.values())).toLowerCase()) }, String.class);
        }

        description = ObjectArrays.concat(description, new String[] {
                String.format(argFormat, "spread <range>", "spawns creature(s) spread within block range instead of on top of workbench or furnace. (WARNING: can be CPU intensive)"),
                String.format(argFormat, "target", "creature targets crafter, that means monsters attack and animals follow and the rest do nothing"),
                String.format(argFormat, "villager <type>", "set the villager profession, values: " + RMCUtil.collectionToString(Arrays.asList(Villager.Profession.values())).toLowerCase()),
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


    public class Customization implements Cloneable {
        private EntityType type;
        private boolean noAi = false;
        private boolean noEffect = false;
        private boolean noRemove = false;
        private boolean invulnerable = false;
        private boolean mountNext = false;
        private float chance = 100.0f;
        private int num = 1;
        private int spread = 0;
        private float onFire = 0;
        private Boolean pickup = null;
        private boolean target = false;
        private boolean hit = false;
        private boolean angry = false;
        private boolean pet = false;
        private boolean noSit = false;
        private Ocelot.Type ocelot = null;
        private Cat.Type cat = null;
        private boolean saddle = false;
        private boolean mount = false;
        private DyeColor color = null;
        private boolean shearedSheep = false;
        private SkeletonType skeleton = null;
        private boolean zombieVillager = false;
        private Villager.Profession villager = null;
        private boolean poweredCreeper = false;
        private boolean playerIronGolem = false;
        private int pigAnger = 0;
        private String name = null;
        private boolean noHideName = false;
        private int hp = 0;
        private int maxHp = 0;
        private boolean adult = false;
        private boolean baby = false;
        private boolean ageLock = false;
        private boolean noBreed = false;
        private ItemStack[] equip = new ItemStack[6];
        private float[] drop = new float[6];
        private List<PotionEffect> potions = new ArrayList<>();
        private Horse.Variant horse = null;
        private Horse.Color horseColor = null;
        private Horse.Style horseStyle = null;
        private Parrot.Variant parrot = null;
        private boolean hasChest = false;
        private Float jumpStrength = null;
        private Rabbit.Type rabbit = null;
        private boolean elder = false;
        private Panda.Gene pandaHiddenGene = null;
        private Panda.Gene pandaMainGene = null;
        private Fox.Type fox = null;
        private boolean foxCrouching = false;
        private UUID foxFirstTrustedPlayerUUID = null;
        private UUID foxSecondTrustedPlayerUUID = null;
        private boolean foxFirstTrustedPlayer = false;
        private boolean foxSecondTrustedPlayer = false;
        private boolean foxSleeping = false;

        public Customization(EntityType newType) {
            type = newType;
        }

        public Customization(Customization c) {
            type = c.type;
            noAi = c.noAi;
            noEffect = c.noEffect;
            noRemove = c.noRemove;
            invulnerable = c.invulnerable;
            chance = c.chance;
            num = c.num;
            spread = c.spread;
            onFire = c.onFire;
            pickup = c.pickup;
            target = c.target;
            hit = c.hit;
            angry = c.angry;
            pet = c.pet;
            noSit = c.noSit;
            ocelot= c.ocelot;
            cat = c.cat;
            saddle = c.saddle;
            mount = c.mount;
            color = c.color;
            shearedSheep = c.shearedSheep;
            villager = c.villager;
            poweredCreeper = c.poweredCreeper;
            playerIronGolem = c.playerIronGolem;
            pigAnger = c.pigAnger;
            name = c.name;
            noHideName = c.noHideName;
            hp = c.hp;
            maxHp = c.maxHp;
            adult = c.adult;
            baby = c.baby;
            ageLock = c.ageLock;
            noBreed = c.noBreed;
            System.arraycopy(c.equip, 0, equip, 0, c.equip.length);
            System.arraycopy(c.drop, 0, drop, 0, c.drop.length);
            potions.addAll(c.potions);
            mountNext = c.mountNext;
            horseColor = c.horseColor;
            horseStyle = c.horseStyle;
            hasChest = c.hasChest;
            jumpStrength = c.jumpStrength;
            rabbit = c.rabbit;

            if (!Version.has1_12Support()) {
                horse = c.horse;
                elder = c.elder;
                skeleton = c.skeleton;
                zombieVillager = c.zombieVillager;
            }

            if (Version.has1_12Support()) {
                parrot = c.parrot;
            }

            if (Version.has1_14Support()) {
                pandaHiddenGene = c.pandaHiddenGene;
                pandaMainGene = c.pandaMainGene;
                fox = c.fox;
                foxCrouching = c.foxCrouching;
                foxFirstTrustedPlayerUUID = c.foxFirstTrustedPlayerUUID;
                foxSecondTrustedPlayerUUID = c.foxSecondTrustedPlayerUUID;
                foxFirstTrustedPlayer = c.foxFirstTrustedPlayer;
                foxSecondTrustedPlayer = c.foxSecondTrustedPlayer;
                foxSleeping = c.foxSleeping;
            }
        }

        @Override
        public Customization clone() {
            return new Customization(this);
        }

        public List<LivingEntity> spawn(Location location, Player player) {
            List<LivingEntity> entities = new ArrayList<>(num);
            World world = location.getWorld();

            int minX = location.getBlockX() - spread / 2;
            int minY = location.getBlockY() - spread / 2;
            int minZ = location.getBlockZ() - spread / 2;

            int maxY = minY + spread;

            for (int i = 0; i < num; i++) {
                if (spread > 0) {
                    int tries = spread * 10;
                    boolean found = false;

                    while (tries-- > 0) {
                        int x = minX + RecipeManager.random.nextInt(spread);
                        int z = minZ + RecipeManager.random.nextInt(spread);
                        int y;

                        for (y = maxY; y >= minY; y--) {
                            Block block = world.getBlockAt(x, y, z);
                            Material material = block.getType();
                            if (!material.isSolid()) {
                                found = true;
                                break;
                            }
                        }

                        if (found) {
                            location.setX(x);
                            location.setY(y);
                            location.setZ(z);
                            break;
                        }
                    }

                    if (!found) {
                        MessageSender.getInstance().debug("Couldn't find suitable location after " + (spread * 10) + " tries, using center.");
                    }

                    location.add(0.5, 0, 0.5);
                }

                LivingEntity ent = (LivingEntity) world.spawnEntity(location, type);
                entities.add(ent);

                if (!noEffect) {
                    world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 20);
                }

                if (name != null) {
                    ent.setCustomName(name);
                    ent.setCustomNameVisible(noHideName);
                }

                if (onFire > 0.0f) {
                    ent.setFireTicks((int) Math.ceil(onFire * 20.0));
                }

                if (pickup != null) {
                    ent.setCanPickupItems(pickup);
                }

                if (pet && ent instanceof Tameable) {
                    Tameable npc = (Tameable) ent;
                    npc.setOwner(player);
                    npc.setTamed(true);
                }

                if (Version.has1_12Support() && ent instanceof Sittable) {
                    Sittable npc = (Sittable) ent;

                    if (pet && noSit) {
                        npc.setSitting(false);
                    }
                }

                if (ent instanceof Wolf) {
                    Wolf npc = (Wolf) ent;

                    if (pet) {
                        if (color != null) {
                            npc.setCollarColor(color);
                        }
                    } else if (angry) {
                        npc.setAngry(true);
                    }
                }

                if (ent instanceof Ocelot) {
                    Ocelot npc = (Ocelot) ent;

                    if (ocelot != null) {
                        npc.setCatType(ocelot);
                    }
                }

                if (Version.has1_14Support() && ent instanceof Cat) {
                    Cat npc = (Cat) ent;

                    if (cat != null) {
                        npc.setCatType(cat);
                    }

                    if (pet && color != null) {
                        npc.setCollarColor(color);
                    }
                }

                if (hp > 0) {
                    ent.setHealth(hp);

                    if (maxHp > 0) {
                        ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHp);
                    }
                }

                if (ent instanceof Ageable) {
                    Ageable npc = (Ageable) ent;

                    if (baby) {
                        npc.setBaby();
                    }

                    if (adult) {
                        npc.setAdult();
                    }

                    if (ageLock) {
                        npc.setAgeLock(true);
                    }

                    if (noBreed) {
                        npc.setBreed(false);
                    }
                }

                if (saddle) {
                    if (ent instanceof Pig) {
                        Pig npc = (Pig) ent;

                        npc.setAdult();
                        npc.setSaddle(true);

                        if (mount) {
                            npc.addPassenger(player);
                        }
                    } else if (ent instanceof Horse) {
                        Horse npc = (Horse) ent;

                        npc.setAdult();
                        npc.getInventory().setSaddle(new ItemStack(Material.SADDLE));

                        if (mount) {
                            npc.addPassenger(player);
                        }
                    }
                }

                if (ent instanceof Zombie) {
                    Zombie npc = (Zombie) ent;

                    if (baby) {
                        npc.setBaby(true);
                    }

                    if (!Version.has1_12Support() && zombieVillager) {
                        npc.setVillager(true);
                    }
                }

                if (villager != null && ent instanceof Villager) {
                    Villager npc = (Villager) ent;
                    npc.setProfession(villager);
                }

                if (poweredCreeper && ent instanceof Creeper) {
                    Creeper npc = (Creeper) ent;
                    npc.setPowered(true);
                }

                if (playerIronGolem && ent instanceof IronGolem) {
                    IronGolem npc = (IronGolem) ent;
                    npc.setPlayerCreated(true);
                }

                if (shearedSheep && ent instanceof Sheep) {
                    Sheep npc = (Sheep) ent;
                    npc.setSheared(true);
                }

                if (color != null && ent instanceof Colorable) {
                    Colorable npc = (Colorable) ent;
                    npc.setColor(color);
                }

                if (!Version.has1_12Support() && skeleton != null && ent instanceof Skeleton) {
                    Skeleton npc = (Skeleton) ent;
                    npc.setSkeletonType(skeleton);
                }

                if (ent instanceof Horse) {
                    Horse npc = (Horse) ent;

                    if (!Version.has1_12Support() && horse != null) {
                        npc.setVariant(horse);
                    }

                    if (horseColor != null) {
                        npc.setColor(horseColor);
                    }

                    if (horseStyle != null) {
                        npc.setStyle(horseStyle);
                    }

                    if (hasChest) {
                        npc.setAdult();
                        npc.setTamed(true);

                        if (Version.has1_12Support()) {
                            if (npc instanceof ChestedHorse) {
                                ChestedHorse chestedHorse = (ChestedHorse) npc;
                                chestedHorse.setCarryingChest(true);
                            }
                        } else {
                            npc.setCarryingChest(true);
                        }
                    }

                    if (jumpStrength != null) {
                        npc.setJumpStrength(jumpStrength);
                    }
                }

                if (Version.has1_12Support() && ent instanceof Parrot) {
                    Parrot npc = (Parrot) ent;

                    if (parrot != null) {
                        npc.setVariant(parrot);
                    }
                }

                if (Version.has1_14Support() && ent instanceof Panda) {
                    Panda npc = (Panda) ent;

                    if (pandaMainGene != null) {
                        npc.setMainGene(pandaMainGene);
                    }
                    if (pandaHiddenGene != null) {
                        npc.setHiddenGene(pandaHiddenGene);
                    }
                }

                if (Version.has1_14Support() && ent instanceof Fox) {
                    Fox npc = (Fox) ent;

                    if (fox != null) {
                        npc.setFoxType(fox);
                    }

                    if (foxCrouching) {
                        npc.setCrouching(true);
                    }

                    if (foxFirstTrustedPlayerUUID != null) {
                        OfflinePlayer firstPlayer = Bukkit.getOfflinePlayer(foxFirstTrustedPlayerUUID);
                        npc.setFirstTrustedPlayer(firstPlayer);
                    } else if (foxFirstTrustedPlayer) {
                        npc.setFirstTrustedPlayer(player);
                    }

                    if (foxSecondTrustedPlayerUUID != null) {
                        OfflinePlayer secondPlayer = Bukkit.getOfflinePlayer(foxSecondTrustedPlayerUUID);
                        npc.setFirstTrustedPlayer(secondPlayer);
                    } else if (foxSecondTrustedPlayer) {
                        npc.setSecondTrustedPlayer(player);
                    }

                    if (foxSleeping) {
                        npc.setSleeping(true);
                    }
                }

                if (rabbit != null && ent instanceof Rabbit) {
                    Rabbit npc = (Rabbit) ent;
                    npc.setRabbitType(rabbit);
                }

                if (!Version.has1_12Support() && elder && ent instanceof Guardian) {
                    Guardian npc = (Guardian) ent;
                    npc.setElder(true);
                }

                if (target && ent instanceof Creature) {
                    Creature npc = (Creature) ent;
                    npc.setTarget(player);
                }

                if (pigAnger > 0 && ent instanceof PigZombie) {
                    PigZombie npc = (PigZombie) ent;
                    npc.setAnger(pigAnger);
                }

                if (hit) {
                    ent.damage(0, player);
                    ent.setVelocity(new Vector());
                }

                if (!potions.isEmpty()) {
                    for (PotionEffect effect : potions) {
                        ent.addPotionEffect(effect, true);
                    }
                }

                ent.setRemoveWhenFarAway(!noRemove);
                if (Version.has1_10Support()) {
                    ent.setInvulnerable(invulnerable);
                    ent.setAI(!noAi);
                }

                EntityEquipment eq = ent.getEquipment();
                if (eq != null) {
                    for (int j = 0; j < equip.length; j++) {
                        ItemStack item = equip[j];
                        if (item == null) {
                            continue;
                        }

                        switch (j) {
                            case 0:
                                eq.setHelmet(item);
                                eq.setHelmetDropChance(drop[j]);
                                break;

                            case 1:
                                eq.setChestplate(item);
                                eq.setChestplateDropChance(drop[j]);
                                break;

                            case 2:
                                eq.setLeggings(item);
                                eq.setLeggingsDropChance(drop[j]);
                                break;

                            case 3:
                                eq.setBoots(item);
                                eq.setBootsDropChance(drop[j]);
                                break;

                            case 4:
                                if (ent instanceof Enderman) {
                                    Enderman npc = (Enderman) ent;
                                    if (item.getData() != null) {
                                        npc.setCarriedMaterial(item.getData());
                                    }
                                } else {
                                    if (Version.has1_9Support()) {
                                        eq.setItemInMainHand(item);
                                        eq.setItemInMainHandDropChance(drop[j]);
                                    } else {
                                        eq.setItemInHand(item);
                                        eq.setItemInHandDropChance(drop[j]);
                                    }
                                }

                                break;
                            case 5:
                                eq.setItemInOffHand(item);
                                eq.setItemInOffHandDropChance(drop[j]);
                            default:
                                break;
                        }
                    }
                }
            }

            return entities;
        }

        public EntityType getType() {
            return type;
        }

        public void setType(EntityType newType) {
            type = newType;
        }

        public boolean isNoAi() {
            return noAi;
        }

        public void setNoAi(boolean newNoAi) {
            noAi = newNoAi;
        }

        public boolean isNoEffect() {
            return noEffect;
        }

        public void setNoEffect(boolean newNoEffect) {
            noEffect = newNoEffect;
        }

        public boolean isNoRemove() {
            return noRemove;
        }

        public void setNoRemove(boolean newNoRemove) {
            noRemove = newNoRemove;
        }

        public boolean isInvulnerable() {
            return invulnerable;
        }

        public void setInvulnerable(boolean newInvulnerable) {
            invulnerable = newInvulnerable;
        }

        public float getChance() {
            return chance;
        }

        public void setChance(float newChance) {
            if (newChance < 0.01f || newChance > 100.0f) {
                chance = Math.min(Math.max(newChance, 0.01f), 100.0f);

                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has chance value less than 0.01 or higher than 100.0, value trimmed.");
            } else {
                chance = newChance;
            }
        }

        public float getJumpStrength() {
            return jumpStrength;
        }

        public void setJumpStrength(float newJumpStrength) {
            if (newJumpStrength < 0.0f || newJumpStrength > 100.0f) {
                jumpStrength = Math.min(Math.max(newJumpStrength, 0.0f), 100.0f);

                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has jumpStrength value less than 0.0 or higher than 100.0, value trimmed.");
            } else {
                jumpStrength = newJumpStrength;
            }
        }

        public int getNum() {
            return num;
        }

        public void setNum(int newNum) {
            if (newNum < 1) {
                num = 1;

                ErrorReporter.getInstance().warning("The " + getFlagType() + " flag can't have 'num' argument less than 1, set to 1.");
            } else {
                num = newNum;
            }
        }

        public int getSpread() {
            return spread;
        }

        public void setSpread(int newSpread) {
            spread = newSpread;
        }

        public boolean isTarget() {
            return target;
        }

        public void setTarget(boolean newTarget) {
            target = newTarget;
        }

        public boolean isPet() {
            return pet;
        }

        public void setPet(boolean newPet) {
            pet = newPet;
        }

        public boolean isSaddle() {
            return saddle;
        }

        public void setSaddle(boolean newSaddle) {
            saddle = newSaddle;
        }

        public boolean isMount() {
            return mount;
        }

        public void setMount(boolean newMount) {
            mount = newMount;
        }

        public String getName() {
            return name;
        }

        public void setName(String newName) {
            name = RMCUtil.parseColors(newName, false);
        }

        public boolean isNoHideName() {
            return noHideName;
        }

        public void setNoHideName(boolean newNoHideName) {
            noHideName = newNoHideName;
        }

        public int getHp() {
            return hp;
        }

        public void setHp(int newHp) {
            hp = newHp;
        }

        public int getMaxHp() {
            return maxHp;
        }

        public void setMaxHp(int newMaxHp) {
            maxHp = newMaxHp;
        }

        public boolean isAdult() {
            return adult;
        }

        public void setAdult(boolean newAdult) {
            adult = newAdult;
        }

        public boolean isBaby() {
            return baby;
        }

        public void setBaby(boolean newBaby) {
            baby = newBaby;
        }

        public boolean isAgeLock() {
            return ageLock;
        }

        public void setAgeLock(boolean lock) {
            ageLock = lock;
        }

        public ItemStack[] getEquip() {
            return equip.clone();
        }

        public void setEquip(ItemStack[] newEquip) {
            equip = newEquip.clone();
        }

        public void setEquip(ItemStack item, int index) {
            if (index < equip.length) {
                equip[index] = item.clone();
            }
        }

        public float[] getDrop() {
            return drop.clone();
        }

        public void setDrop(float[] newDrop) {
            drop = newDrop.clone();
        }

        public void setDrop(float newDrop, int index) {
            if (index < drop.length) {
                drop[index] = newDrop;
            }
        }
        public List<PotionEffect> getPotionEffects() {
            return potions;
        }

        public void setPotionEFfects(List<PotionEffect> effects) {
            if (effects == null) {
                potions.clear();
            } else {
                potions = effects;
            }
        }

        public void addPotionEffect(PotionEffectType newType, float duration, int amplifier) {
            potions.add(new PotionEffect(newType, (int) Math.ceil(duration * 20), amplifier));
        }

        public float getOnFire() {
            return onFire;
        }

        public void setOnFire(float newOnFire) {
            onFire = newOnFire;
        }

        public Boolean getPickup() {
            return pickup;
        }

        public void setPickup(Boolean newPickup) {
            pickup = newPickup;
        }

        public boolean isNoSit() {
            return noSit;
        }

        public void setNoSit(boolean newNoSit) {
            noSit = newNoSit;
        }

        public boolean isAngry() {
            return angry;
        }

        public void setAngry(boolean newAngry) {
            angry = newAngry;
        }

        public Ocelot.Type getOcelot() {
            return ocelot;
        }

        public void setOcelot(Ocelot.Type newOcelot) {
            ocelot = newOcelot;
        }

        public Cat.Type getCat() {
            return cat;
        }

        public void setCat(Cat.Type newCat) {
            cat = newCat;
        }

        public DyeColor getColor() {
            return color;
        }

        public void setColor(DyeColor newColor) {
            color = newColor;
        }

        public boolean isShearedSheep() {
            return shearedSheep;
        }

        public void setShearedSheep(boolean newShearedSheep) {
            shearedSheep = newShearedSheep;
        }

        public SkeletonType getSkeleton() {
            return skeleton;
        }

        public void setSkeleton(SkeletonType newSkeleton) {
            skeleton = newSkeleton;
        }

        public boolean isZombieVillager() {
            return zombieVillager;
        }

        public void setZombieVillager(boolean newZombieVillager) {
            zombieVillager = newZombieVillager;
        }

        public Villager.Profession getVillager() {
            return villager;
        }

        public void setVillager(Villager.Profession newVillager) {
            villager = newVillager;
        }

        public boolean isPoweredCreeper() {
            return poweredCreeper;
        }

        public void setPoweredCreeper(boolean newPoweredCreeper) {
            poweredCreeper = newPoweredCreeper;
        }

        public boolean isPlayerIronGolem() {
            return playerIronGolem;
        }

        public void setPlayerIronGolem(boolean newPlayerIronGolem) {
            playerIronGolem = newPlayerIronGolem;
        }

        public int getPigAnger() {
            return pigAnger;
        }

        public void setPigAnger(int anger) {
            pigAnger = anger;
        }

        public boolean isMountNext() {
            return mountNext;
        }

        public void setMountNext(boolean newMountNext) {
            mountNext = newMountNext;
        }

        public boolean isHit() {
            return hit;
        }

        public void setHit(boolean newHit) {
            hit = newHit;
        }

        public boolean isNoBreed() {
            return noBreed;
        }

        public void setNoBreed(boolean newNoBreed) {
            noBreed = newNoBreed;
        }

        public void setHorseVariant(Horse.Variant newVariant) {
            horse = newVariant;
        }

        public Horse.Variant getHorseVariant() {
            return horse;
        }

        public void setHorseColor(Horse.Color newColor) {
            horseColor = newColor;
        }

        public Horse.Color getHorseColor() {
            return horseColor;
        }

        public void setHorseStyle(Horse.Style newStyle) {
            horseStyle = newStyle;
        }

        public Horse.Style getHorseStyle() {
            return horseStyle;
        }

        public void setParrotVariant(Parrot.Variant newVariant) {
            parrot = newVariant;
        }

        public Parrot.Variant getParrotVariant() {
            return parrot;
        }

        public void setPandaMainGene(Panda.Gene gene) {
            pandaMainGene = gene;
        }

        public Panda.Gene getPandaMainGene() {
            return pandaMainGene;
        }

        public void setPandaHiddenGene(Panda.Gene gene) {
            pandaHiddenGene = gene;
        }

        public Panda.Gene getPandaHiddenGene() {
            return pandaHiddenGene;
        }

        public void setFoxType(Fox.Type newType) {
            fox = newType;
        }

        public Fox.Type getFoxType() {
            return fox;
        }

        public void setFoxSleeping(boolean sleeping) {
            foxSleeping = sleeping;
        }

        public boolean isFoxSleeping() {
            return foxSleeping;
        }

        public void setFoxFirstTrustedPlayerUUID(UUID uuid) {
            foxFirstTrustedPlayerUUID = uuid;
        }

        public UUID getFoxFirstTrustedPlayerUUID() {
            return foxFirstTrustedPlayerUUID;
        }

        public void setFoxSecondTrustedPlayerUUID(UUID uuid) {
            foxSecondTrustedPlayerUUID = uuid;
        }

        public UUID getFoxSecondTrustedPlayerUUID() {
            return foxSecondTrustedPlayerUUID;
        }

        public void setFoxFirstTrustedPlayer(boolean usePlayer) {
            foxFirstTrustedPlayer = usePlayer;
        }

        public boolean getFoxFirstTrustedPlayer() {
            return foxFirstTrustedPlayer;
        }

        public void setFoxSecondTrustedPlayer(boolean usePlayer) {
            foxSecondTrustedPlayer = usePlayer;
        }

        public boolean getFoxSecondTrustedPlayer() {
            return foxSecondTrustedPlayer;
        }

        public void setFoxCrouching(boolean crouching) {
            foxCrouching = crouching;
        }

        public boolean getFoxCrouching() {
            return foxCrouching;
        }

        public void setHasChest(boolean newHasChest) {
            hasChest = newHasChest;
        }

        public void setRabbit(Rabbit.Type newRabbit) {
            rabbit = newRabbit;
        }

        public Rabbit.Type getRabbit() {
            return rabbit;
        }

        public void setElder(boolean newElder) {
            elder = newElder;
        }

        public boolean isElder() {
            return elder;
        }

        @Override
        public int hashCode() {
            String toHash = "" + super.hashCode();

            toHash += "type: " + type.toString();
            toHash += "noAi: " + noAi;
            toHash += "noEffect: " + noEffect;
            toHash += "noRemove: " + noRemove;
            toHash += "invulnerable: " + invulnerable;
            toHash += "mountNext: " + mountNext;
            toHash += "chance: " + chance;
            toHash += "num: " + num;
            toHash += "spread: " + spread;
            toHash += "onFire: " + onFire;
            toHash += "pickup: " + pickup.toString();
            toHash += "target: " + target;
            toHash += "hit: " + hit;
            toHash += "angry: " + angry;
            toHash += "pet: " + pet;
            toHash += "noSit: " + noSit;
            toHash += "ocelot: " + ocelot.toString();
            toHash += "cat: " + cat.toString();
            toHash += "saddle: " + saddle;
            toHash += "mount: " + mount;
            toHash += "color: " + color.toString();
            toHash += "shearedSheep: " + shearedSheep;
            toHash += "skeleton: " + skeleton.toString();
            toHash += "zombieVillager: " + zombieVillager;
            toHash += "villager: " + villager.toString();
            toHash += "poweredCreeper: " + poweredCreeper;
            toHash += "playerIronGolem: " + playerIronGolem;
            toHash += "pigAnger: " + pigAnger;
            toHash += "name: " + name;
            toHash += "noHideName: " + noHideName;
            toHash += "hp: " + hp;
            toHash += "maxHp: " + maxHp;
            toHash += "adult: " + adult;
            toHash += "baby: " + baby;
            toHash += "ageLock: " + ageLock;
            toHash += "noBreed: " + noBreed;

            for (ItemStack item : equip) {
                toHash += "equip: " + item.hashCode();
            }

            for (Float itemDrop : drop) {
                toHash += "drop: " + itemDrop;
            }

            for (PotionEffect effect : potions) {
                toHash += "potion: " + effect.hashCode();
            }

            toHash += "horse: " + horse.toString();
            toHash += "horseColor: " + horseColor.toString();
            toHash += "horseStyle: " + horseStyle.toString();
            toHash += "hasChest: " + hasChest;
            toHash += "jumpStrength: " + jumpStrength.toString();
            toHash += "rabbit: " + rabbit.toString();
            toHash += "elder: " + elder;

            toHash += "parrot: " + parrot;
            toHash += "pandaHiddenGene: " + pandaHiddenGene;
            toHash += "pandaMainGene: " + pandaMainGene;
            toHash += "fox: " + fox;
            toHash += "foxCrouching: " + foxCrouching;
            toHash += "foxFirstTrustedPlayerUUID: " + foxFirstTrustedPlayerUUID;
            toHash += "foxSecondTrustedPlayerUUID: " + foxSecondTrustedPlayerUUID;
            toHash += "foxFirstTrustedPlayer: " + foxFirstTrustedPlayer;
            toHash += "foxSecondTrustedPlayer: " + foxSecondTrustedPlayer;
            toHash += "foxSleeping: " + foxSleeping;

            return toHash.hashCode();
        }
    }

    private List<Customization> spawn = new ArrayList<>();

    public FlagSummon() { }

    public FlagSummon(FlagSummon flag) {
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
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        String[] split = value.split("\\|");

        value = split[0].trim();
        EntityType type = RMCUtil.parseEnum(value, EntityType.values());

        if (type == null || !type.isAlive()) {
            ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid creature: " + value, "Look in '" + Files.FILE_INFO_NAMES + "' at 'ENTITY TYPES' section for ALIVE entities.");
            return false;
        }

        Customization c = new Customization(type);

        if (split.length > 1) {
            for (int n = 1; n < split.length; n++) {
                String original = split[n].trim();
                value = original.toLowerCase();

                if (value.equals("noremove")) {
                    c.setNoRemove(true);
                } else if (value.equals("invulnerable")) {
                    c.setInvulnerable(true);
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
                    switch (type) {
                        case WOLF:
                        case PIG_ZOMBIE:
                            break;

                        default:
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'angry' on unsupported creature!");
                            continue;
                    }

                    c.setAngry(true);
                } else if (value.equals("shearedsheep")) {
                    if (type != EntityType.SHEEP) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'shearedsheep' on non-sheep creature!");
                        continue;
                    }

                    c.setShearedSheep(true);
                } else if (!Version.has1_12Support() && value.equals("zombievillager")) {
                    if (type != EntityType.ZOMBIE) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'zombievillager' on non-zombie creature!");
                        continue;
                    }

                    c.setZombieVillager(true);
                } else if (value.equals("poweredcreeper")) {
                    if (type != EntityType.CREEPER) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'poweredcreeper' on non-creeper creature!");
                        continue;
                    }

                    c.setPoweredCreeper(true);
                } else if (value.equals("playerirongolem")) {
                    if (type != EntityType.IRON_GOLEM) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'playerirongolem' on non-irongolem creature!");
                        continue;
                    }

                    c.setPlayerIronGolem(true);
                } else if (value.equals("hit")) {
                    c.setHit(true);
                } else if (value.equals("adult")) {
                    if (ToolsEntity.isAgeable(type)) {
                        c.setAdult(true);
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'adult' set on unsupported creature!");
                    }
                } else if (value.equals("baby")) {
                    if (ToolsEntity.isAgeable(type) || type == EntityType.ZOMBIE) {
                        c.setBaby(true);
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'baby' set on unsupported creature!");
                    }
                } else if (value.equals("agelock")) {
                    if (ToolsEntity.isAgeable(type)) {
                        c.setAgeLock(true);
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'agelock' set on unsupported creature!");
                    }
                } else if (value.equals("nobreed")) {
                    if (ToolsEntity.isAgeable(type)) {
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
                    if (ToolsEntity.isTameable(type)) {
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
                    if (type != EntityType.PIG && type != EntityType.HORSE ||
                            (Version.has1_12Support() && type != EntityType.SKELETON_HORSE && type != EntityType.ZOMBIE_HORSE && type != EntityType.MULE && type != EntityType.DONKEY)) {
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
                    switch (type) {
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
                    if (type != EntityType.VILLAGER) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'villager' argument on non-villager creature!");
                        continue;
                    }

                    value = value.substring("villager".length()).trim();

                    c.setVillager(RMCUtil.parseEnum(value, Villager.Profession.values()));

                    if (c.getVillager() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'villager' argument with invalid type: " + value);
                    }
                } else if (!Version.has1_12Support() && value.startsWith("skeleton")) {
                    if (type != EntityType.SKELETON) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'skeleton' argument on non-skeleton creature!");
                        continue;
                    }

                    value = value.substring("skeleton".length()).trim();

                    c.setSkeleton(RMCUtil.parseEnum(value, SkeletonType.values()));

                    if (c.getSkeleton() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'skeleton' argument with invalid type: " + value);
                    }
                } else if (Version.has1_14Support() && value.startsWith("cat")) {
                    if (type != EntityType.CAT) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'cat' argument on non-cat creature!");
                        continue;
                    }

                    value = value.substring("cat".length()).trim();

                    c.setCat(RMCUtil.parseEnum(value, Cat.Type.values()));

                    if (c.getCat() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'cat' argument with invalid type: " + value);
                    }
                } else if (!Version.has1_14Support() && value.startsWith("cat")) {
                    if (type != EntityType.OCELOT) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'cat' argument on non-ocelot creature!");
                        continue;
                    }

                    value = value.substring("cat".length()).trim();

                    c.setOcelot(RMCUtil.parseEnum(value, Ocelot.Type.values()));

                    if (c.getOcelot() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'cat' argument with invalid type: " + value);
                    }
                } else if (value.startsWith("rabbit")) {
                    if (type != EntityType.RABBIT) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'rabbit' argument on non-rabbit creature!");
                        continue;
                    }

                    value = value.substring("rabbit".length()).trim();

                    c.setRabbit(RMCUtil.parseEnum(value, Rabbit.Type.values()));

                    if (c.getRabbit() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'rabbit' argument with invalid type: " + value);
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
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'potion' argument with invalid type: " + value);
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
                    if (type != EntityType.HORSE) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'horse' argument on non-horse creature!");
                        continue;
                    }

                    value = value.substring("horse".length()).trim();

                    c.setHorseVariant(RMCUtil.parseEnum(value, Horse.Variant.values()));

                    if (c.getHorseVariant() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'horse' argument with invalid type: " + value);
                    }
                } else if (value.startsWith("horsecolor")) {
                    if (type != EntityType.HORSE) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'horsecolor' argument on non-horse creature!");
                        continue;
                    }

                    value = value.substring("horsecolor".length()).trim();

                    c.setHorseColor(RMCUtil.parseEnum(value, Horse.Color.values()));

                    if (c.getHorseColor() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'horsecolor' argument with invalid type: " + value);
                    }
                } else if (value.startsWith("horsestyle")) {
                    if (type != EntityType.HORSE) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'horsestyle' argument on non-horse creature!");
                        continue;
                    }

                    value = value.substring("horsestyle".length()).trim();

                    c.setHorseStyle(RMCUtil.parseEnum(value, Horse.Style.values()));

                    if (c.getHorseStyle() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'horsestyle' argument with invalid type: " + value);
                    }
                } else if (value.equals("haschest")) {
                    c.setHasChest(true);
                } else if (!Version.has1_12Support() && value.equals("elder")) {
                    if (type != EntityType.GUARDIAN) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'elder' on non-guardian creature!");
                        continue;
                    }

                    c.setPoweredCreeper(true);
                } else if (Version.has1_12Support() && value.startsWith("parrot")) {
                    if (type != EntityType.PARROT) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'parrot' argument on non-parrot creature!");
                        continue;
                    }

                    value = value.substring("parrot".length()).trim();

                    c.setParrotVariant(RMCUtil.parseEnum(value, Parrot.Variant.values()));

                    if (c.getParrotVariant() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'parrot' argument with invalid type: " + value);
                    }
                } else if (Version.has1_14Support() && value.startsWith("pandamaingene")) {
                    if (type != EntityType.PANDA) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'pandamaingene' argument on non-panda creature!");
                        continue;
                    }

                    value = value.substring("pandamaingene".length()).trim();

                    c.setPandaMainGene(RMCUtil.parseEnum(value, Panda.Gene.values()));

                    if (c.getPandaMainGene() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'pandamaingene' argument with invalid type: " + value);
                    }
                } else if (Version.has1_14Support() && value.startsWith("pandahiddengene")) {
                    if (type != EntityType.PANDA) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'pandahiddengene' argument on non-panda creature!");
                        continue;
                    }

                    value = value.substring("pandahiddengene".length()).trim();

                    c.setPandaHiddenGene(RMCUtil.parseEnum(value, Panda.Gene.values()));

                    if (c.getPandaHiddenGene() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'pandahiddengene' argument with invalid type: " + value);
                    }
                } else if (Version.has1_14Support() && value.startsWith("fox")) {
                    String[] foxSplit = value.split(" ");
                    if (type != EntityType.FOX) {
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
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'fox' argument with invalid type: " + value);
                        }
                    }
                } else {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + value);
                }
            }
        }

        if (type == EntityType.WOLF) {
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
            if (c.pet || c.target || (c.saddle && c.mount)) {
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
            if (c.chance < 100.0f && c.chance < (RecipeManager.random.nextFloat() * 100)) {
                continue;
            }

            List<LivingEntity> spawned = c.spawn(l, a.player());

            if (toMount != null) {
                for (int i = 0; i < Math.min(spawned.size(), toMount.size()); i++) {
                    spawned.get(i).addPassenger(toMount.get(i));
                }
            }

            if (c.mountNext) {
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
