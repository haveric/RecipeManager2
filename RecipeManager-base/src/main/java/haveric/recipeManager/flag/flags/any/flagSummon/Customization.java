package haveric.recipeManager.flag.flags.any.flagSummon;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsEntity;
import haveric.recipeManager.tools.Version;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Customization implements Cloneable {
    private String flagType;
    private EntityType entityType;

    private boolean adult = false;
    private boolean ageLock = false;
    private boolean angry = false;
    private Integer arrowCooldown = null;
    private int arrowsInBody = 0;
    private boolean baby = false;
    private int beeAnger = 0;
    private int beeCannotEnterHiveTicks = 0;
    private boolean beeHasNectar = false;
    private boolean beeHasStung = false;
    private Cat.Type cat = null;
    private float chance = 100.0f;
    private DyeColor color = null;
    private float[] drop = new float[6];
    private boolean elder = false;
    private ItemStack[] equip = new ItemStack[6];
    private Fox.Type fox = null;
    private boolean foxCrouching = false;
    private boolean foxFirstTrustedPlayer = false;
    private UUID foxFirstTrustedPlayerUUID = null;
    private boolean foxSecondTrustedPlayer = false;
    private UUID foxSecondTrustedPlayerUUID = null;
    private boolean foxSleeping = false;
    private boolean hasChest = false;
    private boolean hit = false;
    @SuppressWarnings("deprecation")
    private Horse.Variant horse = null;
    private Horse.Color horseColor = null;
    private Horse.Style horseStyle = null;
    private int hp = 0;
    private boolean invisible = false;
    private boolean invulnerable = false;
    private Float jumpStrength = null;
    private int maxHp = 0;
    private boolean mount = false;
    private boolean mountNext = false;
    private String name = null;
    private boolean noAi = false;
    private boolean noBreed = false;
    private boolean noCollision = false;
    private boolean noEffect = false;
    private boolean noHideName = false;
    private boolean noRemove = false;
    private boolean noSit = false;
    private int num = 1;
    @SuppressWarnings("deprecation")
    private Ocelot.Type ocelot = null;
    private float onFire = 0;
    private Panda.Gene pandaHiddenGene = null;
    private Panda.Gene pandaMainGene = null;
    private Parrot.Variant parrot = null;
    private boolean persistent = false;
    private boolean pet = false;
    private Boolean pickup = null;
    private int pigAnger = 0;
    private boolean playerIronGolem = false;
    private List<PotionEffect> potions = new ArrayList<>();
    private boolean poweredCreeper = false;
    private Rabbit.Type rabbit = null;
    private boolean saddle = false;
    private boolean shearedSheep = false;
    @SuppressWarnings("deprecation")
    private Skeleton.SkeletonType skeleton = null;
    private int spread = 0;
    private boolean target = false;
    private Villager.Profession villager = null;
    private boolean zombieVillager = false;

    public Customization(String newFlagType, EntityType newType) {
        flagType = newFlagType;
        entityType = newType;
    }

    public Customization(Customization c) {
        flagType = c.flagType;
        entityType = c.entityType;

        adult = c.adult;
        ageLock = c.ageLock;
        angry = c.angry;
        baby = c.baby;
        cat = c.cat;
        chance = c.chance;
        color = c.color;
        System.arraycopy(c.drop, 0, drop, 0, c.drop.length);
        System.arraycopy(c.equip, 0, equip, 0, c.equip.length);
        hasChest = c.hasChest;
        hit = c.hit;
        horseColor = c.horseColor;
        horseStyle = c.horseStyle;
        hp = c.hp;
        jumpStrength = c.jumpStrength;
        maxHp = c.maxHp;
        mount = c.mount;
        mountNext = c.mountNext;
        name = c.name;
        noBreed = c.noBreed;
        noEffect = c.noEffect;
        noHideName = c.noHideName;
        noRemove = c.noRemove;
        noSit = c.noSit;
        num = c.num;
        ocelot = c.ocelot;
        onFire = c.onFire;
        pet = c.pet;
        pickup = c.pickup;
        pigAnger = c.pigAnger;
        playerIronGolem = c.playerIronGolem;
        potions.addAll(c.potions);
        poweredCreeper = c.poweredCreeper;
        rabbit = c.rabbit;
        saddle = c.saddle;
        shearedSheep = c.shearedSheep;
        spread = c.spread;
        target = c.target;
        villager = c.villager;

        if (Version.has1_9Support()) {
            noCollision = c.noCollision;
        }

        if (Version.has1_10Support()) {
            invulnerable = c.invulnerable;
            noAi = c.noAi;
        }

        if (!Version.has1_12Support()) {
            elder = c.elder;
            horse = c.horse;
            skeleton = c.skeleton;
            zombieVillager = c.zombieVillager;
        }

        if (Version.has1_12Support()) {
            parrot = c.parrot;
        }

        if (Version.has1_13Support()) {
            persistent = c.persistent;
        }

        if (Version.has1_14Support()) {
            pandaHiddenGene = c.pandaHiddenGene;
            pandaMainGene = c.pandaMainGene;
            fox = c.fox;
            foxCrouching = c.foxCrouching;
            foxFirstTrustedPlayer = c.foxFirstTrustedPlayer;
            foxFirstTrustedPlayerUUID = c.foxFirstTrustedPlayerUUID;
            foxSecondTrustedPlayer = c.foxSecondTrustedPlayer;
            foxSecondTrustedPlayerUUID = c.foxSecondTrustedPlayerUUID;
            foxSleeping = c.foxSleeping;
        }

        if (Version.has1_15Support()) {
            beeAnger = c.beeAnger;
            beeCannotEnterHiveTicks = c.beeCannotEnterHiveTicks;
            beeHasNectar = c.beeHasNectar;
            beeHasStung = c.beeHasStung;
        }

        if (Version.has1_16Support()) {
            arrowCooldown = c.arrowCooldown;
            arrowsInBody = c.arrowsInBody;
            invisible = c.invisible;
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

            LivingEntity ent = (LivingEntity) world.spawnEntity(location, entityType);
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
                    //noinspection deprecation
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
                //noinspection deprecation
                npc.setSkeletonType(skeleton);
            }

            if (ent instanceof Horse) {
                Horse npc = (Horse) ent;

                if (!Version.has1_12Support() && horse != null) {
                    //noinspection deprecation
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
                        //noinspection deprecation
                        npc.setCarryingChest(true);
                    }
                }

                if (jumpStrength != null) {
                    npc.setJumpStrength(jumpStrength);
                }
            }

            if (Version.has1_12Support()) {
                if (ent instanceof Parrot) {
                    Parrot npc = (Parrot) ent;

                    if (parrot != null) {
                        npc.setVariant(parrot);
                    }
                }
            }

            if (Version.has1_14Support()) {
                if (ent instanceof Fox) {
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

                if (ent instanceof Panda) {
                    Panda npc = (Panda) ent;

                    if (pandaMainGene != null) {
                        npc.setMainGene(pandaMainGene);
                    }
                    if (pandaHiddenGene != null) {
                        npc.setHiddenGene(pandaHiddenGene);
                    }
                }
            }

            if (rabbit != null && ent instanceof Rabbit) {
                Rabbit npc = (Rabbit) ent;
                npc.setRabbitType(rabbit);
            }

            if (!Version.has1_12Support() && elder && ent instanceof Guardian) {
                Guardian npc = (Guardian) ent;
                //noinspection deprecation
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

            if (Version.has1_9Support()) {
                if (noCollision) {
                    ent.setCollidable(false);
                }
            }

            if (Version.has1_10Support()) {
                ent.setInvulnerable(invulnerable);
                ent.setAI(!noAi);
            }

            if (Version.has1_16Support()) {
                if (arrowCooldown != null) {
                    ent.setArrowCooldown(arrowCooldown);
                }
                ent.setArrowsInBody(arrowsInBody);
                ent.setInvisible(invisible);
            }

            if (Version.has1_13Support()) {
                ent.setPersistent(persistent);
            }

            if (Version.has1_15Support()) {
                if (ent instanceof Bee) {
                    Bee bee = (Bee) ent;
                    bee.setAnger(beeAnger);
                    bee.setCannotEnterHiveTicks(beeCannotEnterHiveTicks);
                    bee.setHasNectar(beeHasNectar);
                    bee.setHasStung(beeHasStung);
                }
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
                                    //noinspection deprecation
                                    eq.setItemInHand(item);
                                    //noinspection deprecation
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

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType newType) {
        entityType = newType;
    }

    public boolean isNoRemove() {
        return noRemove;
    }

    public void setNoRemove(boolean newNoRemove) {
        noRemove = newNoRemove;
    }

    public float getChance() {
        return chance;
    }

    public void setChance(float newChance) {
        if (newChance < 0.01f || newChance > 100.0f) {
            chance = Math.min(Math.max(newChance, 0.01f), 100.0f);

            ErrorReporter.getInstance().warning("Flag " + flagType + " has chance value less than 0.01 or higher than 100.0, value trimmed.");
        } else {
            chance = newChance;
        }
    }

    public void setJumpStrength(float newJumpStrength) {
        if (newJumpStrength < 0.0f || newJumpStrength > 100.0f) {
            jumpStrength = Math.min(Math.max(newJumpStrength, 0.0f), 100.0f);

            ErrorReporter.getInstance().warning("Flag " + flagType + " has jumpStrength value less than 0.0 or higher than 100.0, value trimmed.");
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

            ErrorReporter.getInstance().warning("The " + flagType + " flag can't have 'num' argument less than 1, set to 1.");
        } else {
            num = newNum;
        }
    }

    public boolean isTarget() {
        return target;
    }

    public boolean isPet() {
        return pet;
    }

    public boolean isSaddle() {
        return saddle;
    }

    public boolean isMount() {
        return mount;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = RMCUtil.parseColors(newName, false);
    }

    public void setEquip(ItemStack item, int index) {
        if (index < equip.length) {
            equip[index] = item.clone();
        }
    }

    public void setDrop(float newDrop, int index) {
        if (index < drop.length) {
            drop[index] = newDrop;
        }
    }

    public void addPotionEffect(PotionEffectType newType, float duration, int amplifier) {
        potions.add(new PotionEffect(newType, (int) Math.ceil(duration * 20), amplifier));
    }

    public boolean isAngry() {
        return angry;
    }

    public void setAngry(boolean newAngry) {
        angry = newAngry;
    }

    public DyeColor getColor() {
        return color;
    }

    public void setColor(DyeColor newColor) {
        color = newColor;
    }

    public boolean isMountNext() {
        return mountNext;
    }

    public boolean parseArgument(String original) {
        String lower = original.toLowerCase();

        if (lower.startsWith("hand") || lower.startsWith("mainhand") || lower.startsWith("offhand") || lower.startsWith("hold") || lower.startsWith("head") || lower.startsWith("helmet") || lower.startsWith("chest") || lower.startsWith("leg") || lower.startsWith("feet") || lower.startsWith("boot")) {
            int index = -1;

            switch (lower.charAt(0)) {
                case 'h':
                    switch (lower.charAt(1)) {
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
                ErrorReporter.getInstance().warning("Flag " + flagType + " has unknown argument: " + lower);
                return false;
            }

            int i = lower.indexOf(' ');
            String[] args = lower.substring(i + 1).trim().split(" ");
            lower = args[0].trim();

            ItemStack item = Tools.parseItem(lower, 0);

            if (item == null) {
                return false;
            }

            setEquip(item, index);

            if (args.length > 1) {
                lower = args[1].trim();

                if (lower.charAt(lower.length() - 1) == '%') {
                    lower = lower.substring(0, lower.length() - 1);
                }

                try {
                    setDrop( Math.min(Math.max(Float.parseFloat(lower), 0), 100), index);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'chance' argument with invalid number: " + lower);
                }
            }
        } else if (lower.equals("adult")) {
            if (ToolsEntity.isAgeable(entityType)) {
                adult = true;
            } else {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'adult' set on unsupported entity!");
            }
        } else if (lower.equals("agelock")) {
            if (ToolsEntity.isAgeable(entityType)) {
                ageLock = true;
            } else {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'agelock' set on unsupported entity!");
            }
        } else if (lower.equals("angry")) {
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
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'angry' on unsupported entity!");
                return false;
            }

            angry = true;
        } else if (lower.equals("baby")) {
            if (ToolsEntity.isAgeable(entityType) || entityType == EntityType.ZOMBIE) {
                baby = true;
            } else {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'baby' set on unsupported entity!");
            }
        } else if (lower.startsWith("cat")) {
            lower = lower.substring("cat".length()).trim();

            if (Version.has1_14Support()) {
                if (entityType != EntityType.CAT) {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'cat' argument on non-cat entity!");
                    return false;
                }

                cat = RMCUtil.parseEnum(lower, Cat.Type.values());

                if (cat == null) {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'cat' argument with invalid entityType: " + lower);
                }
            } else {
                if (entityType != EntityType.OCELOT) {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'cat' argument on non-ocelot entity!");
                    return false;
                }

                //noinspection deprecation
                ocelot = RMCUtil.parseEnum(lower, Ocelot.Type.values());

                if (ocelot == null) {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'cat' argument with invalid entityType: " + lower);
                }
            }
        } else if (lower.startsWith("chance")) {
            lower = lower.substring("chance".length()).trim();

            if (lower.charAt(lower.length() - 1) == '%') {
                lower = lower.substring(0, lower.length() - 1);
            }

            try {
                setChance(Float.parseFloat(lower));
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'chance' argument with invalid number: " + lower);
            }
        } else if (lower.startsWith("color")) {
            switch (entityType) {
                case SHEEP:
                case WOLF:
                    break;

                case CAT:
                    break;

                default:
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'color' on unsupported entity!");
                    return false;
            }

            lower = lower.substring("color".length()).trim();

            color = RMCUtil.parseEnum(lower, DyeColor.values());

            if (color == null) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'color' argument with invalid dye color: " + lower);
            }
        } else if (lower.equals("haschest")) {
            hasChest = true;
        } else if (lower.equals("hit")) {
            hit = true;
        } else if (lower.startsWith("horsecolor")) {
            if (entityType != EntityType.HORSE) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'horsecolor' argument on non-horse entity!");
                return false;
            }

            lower = lower.substring("horsecolor".length()).trim();

            horseColor = RMCUtil.parseEnum(lower, Horse.Color.values());

            if (horseColor == null) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'horsecolor' argument with invalid entityType: " + lower);
            }
        } else if (lower.startsWith("horsestyle")) {
            if (entityType != EntityType.HORSE) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'horsestyle' argument on non-horse entity!");
                return false;
            }

            lower = lower.substring("horsestyle".length()).trim();

            horseStyle = RMCUtil.parseEnum(lower, Horse.Style.values());

            if (horseStyle == null) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'horsestyle' argument with invalid entityType: " + lower);
            }
        } else if (lower.startsWith("hp")) {
            lower = lower.substring("hp".length()).trim();

            String[] args = lower.split(" ");

            lower = args[0].trim();

            try {
                hp = Integer.parseInt(lower);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'hp' argument with invalid number: " + lower);
                return false;
            }

            if (args.length > 1) {
                lower = args[1].trim();

                try {
                    maxHp = Integer.parseInt(lower);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'hp' argument with invalid number for maxhp: " + lower);
                }
            }
        } else if (lower.startsWith("jumpstrength")) {
            lower = lower.substring("jumpstrength".length()).trim();

            try {
                setJumpStrength(Float.parseFloat(lower));
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'jumpstrength' argument with invalid number: " + lower);
            }
        } else if (lower.equals("mountnext")) {
            mountNext = true;
        } else if (lower.startsWith("name")) {
            original = original.substring("name".length()).trim();

            setName(original);
        } else if (lower.equals("nobreed")) {
            if (ToolsEntity.isAgeable(entityType)) {
                noBreed = true;
            } else {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'nobreed' set on unsupported entity!");
            }
        } else if (lower.equals("noeffect")) {
            noEffect = true;
        } else if (lower.equals("nohidename")) {
            noHideName = true;
        } else if (lower.equals("noremove")) {
            noRemove = true;
        } else if (lower.startsWith("num")) {
            lower = lower.substring("num".length()).trim();

            try {
                setNum(Integer.parseInt(lower));
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'num' argument with invalid value number: " + lower);
            }
        } else if (lower.startsWith("onfire")) {
            lower = lower.substring("onfire".length()).trim();

            try {
                onFire = Float.parseFloat(lower) * 20.0f;
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'onfire' argument with invalid value number: " + lower);
            }
        } else if (lower.startsWith("pet")) {
            if (ToolsEntity.isTameable(entityType)) {
                pet = true;

                if (lower.length() > "pet".length()) {
                    lower = lower.substring("pet".length()).trim();

                    if (lower.equals("nosit")) {
                        noSit = true;
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + flagType + " has 'pet' argument with unknown value: " + lower);
                    }
                }
            } else {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'pet' on untameable entity!");
            }
        } else if (lower.startsWith("pickup")) {
            lower = lower.substring("pickup".length()).trim();

            if (lower.isEmpty()) {
                pickup = true;
            } else {
                pickup = lower.equals("true");
            }
        } else if (lower.equals("playerirongolem")) {
            if (entityType != EntityType.IRON_GOLEM) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'playerirongolem' on non-irongolem entity!");
                return false;
            }

            playerIronGolem = true;
        } else if (lower.startsWith("potion")) {
            lower = lower.substring("potion".length()).trim();
            String[] args = lower.split(" ");
            lower = args[0].trim();

            PotionEffectType effect = PotionEffectType.getByName(lower); // Tools.parseEnum(lower, PotionEffectType.values());

            if (effect == null) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'potion' argument with invalid entityType: " + lower);
                return false;
            }

            float duration = 1;
            int amplifier = 0;

            if (args.length > 1) {
                lower = args[1].trim();

                try {
                    duration = Float.parseFloat(lower);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'potion' argument with invalid number for duration: " + lower);
                    return false;
                }
            }

            if (args.length > 2) {
                lower = args[2].trim();

                try {
                    amplifier = Integer.parseInt(lower);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'potion' argument with invalid number for amplifier: " + lower);
                    return false;
                }
            }

            addPotionEffect(effect, duration, amplifier);
        } else if (lower.equals("poweredcreeper")) {
            if (entityType != EntityType.CREEPER) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'poweredcreeper' on non-creeper entity!");
                return false;
            }

            poweredCreeper = true;
        } else if (lower.startsWith("rabbit")) {
            if (entityType != EntityType.RABBIT) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'rabbit' argument on non-rabbit entity!");
                return false;
            }

            lower = lower.substring("rabbit".length()).trim();

            rabbit = RMCUtil.parseEnum(lower, Rabbit.Type.values());

            if (rabbit == null) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'rabbit' argument with invalid entityType: " + lower);
            }
        } else if (lower.startsWith("saddle")) {
            if (entityType != EntityType.PIG && entityType != EntityType.HORSE ||
                    (Version.has1_12Support() && entityType != EntityType.SKELETON_HORSE && entityType != EntityType.ZOMBIE_HORSE && entityType != EntityType.MULE && entityType != EntityType.DONKEY)) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'saddle' on non-pig and non-horse entity!");
                return false;
            }

            saddle = true;

            if (lower.length() > "saddle".length()) {
                lower = lower.substring("saddle".length()).trim();

                if (lower.equals("mount")) {
                    mount = true;
                } else {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'saddle' argument with unknown value: " + lower);
                }
            }
        } else if (lower.equals("shearedsheep")) {
            if (entityType != EntityType.SHEEP) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'shearedsheep' on non-sheep entity!");
                return false;
            }

            shearedSheep = true;
        } else if (lower.startsWith("spread")) {
            lower = lower.substring("spread".length()).trim();

            try {
                spread = Integer.parseInt(lower);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'spread' argument with invalid value number: " + lower);
            }
        } else if (lower.equals("target")) {
            target = true;
        } else if (lower.startsWith("villager")) {
            if (entityType != EntityType.VILLAGER) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'villager' argument on non-villager entity!");
                return false;
            }

            lower = lower.substring("villager".length()).trim();

            villager = RMCUtil.parseEnum(lower, Villager.Profession.values());

            if (villager == null) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'villager' argument with invalid entityType: " + lower);
            }
        } else if (Version.has1_9Support() && lower.equals("nocollision")) {
            noCollision = true;
        } else if (Version.has1_10Support() && lower.equals("invulnerable")) {
            invulnerable = true;
        } else if (Version.has1_10Support() && lower.equals("noai")) {
            noAi = true;
        } else if (!Version.has1_12Support() && lower.equals("elder")) {
            if (entityType != EntityType.GUARDIAN) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'elder' on non-guardian entity!");
                return false;
            }

            elder = true;
        } else if (!Version.has1_12Support() && lower.startsWith("horse")) {
            if (entityType != EntityType.HORSE) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'horse' argument on non-horse entity!");
                return false;
            }

            lower = lower.substring("horse".length()).trim();

            //noinspection deprecation
            horse = RMCUtil.parseEnum(lower, Horse.Variant.values());

            if (horse == null) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'horse' argument with invalid entityType: " + lower);
            }
        } else if (!Version.has1_12Support() && lower.startsWith("skeleton")) {
            if (entityType != EntityType.SKELETON) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'skeleton' argument on non-skeleton entity!");
                return false;
            }

            lower = lower.substring("skeleton".length()).trim();

            //noinspection deprecation
            skeleton = RMCUtil.parseEnum(lower, Skeleton.SkeletonType.values());

            if (skeleton == null) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'skeleton' argument with invalid entityType: " + lower);
            }
        } else if (!Version.has1_12Support() && lower.equals("zombievillager")) {
            if (entityType != EntityType.ZOMBIE) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'zombievillager' on non-zombie entity!");
                return false;
            }

            zombieVillager = true;
        } else if (Version.has1_12Support() && lower.startsWith("parrot")) {
            if (entityType != EntityType.PARROT) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'parrot' argument on non-parrot entity!");
                return false;
            }

            lower = lower.substring("parrot".length()).trim();

            parrot = RMCUtil.parseEnum(lower, Parrot.Variant.values());

            if (parrot == null) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'parrot' argument with invalid entityType: " + lower);
            }
        } else if (Version.has1_13Support() && lower.equals("persistent")) {
            persistent = true;
        } else if (Version.has1_14Support() && lower.startsWith("pandahiddengene")) {
            if (entityType != EntityType.PANDA) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'pandahiddengene' argument on non-panda entity!");
                return false;
            }

            lower = lower.substring("pandahiddengene".length()).trim();

            pandaHiddenGene = RMCUtil.parseEnum(lower, Panda.Gene.values());

            if (pandaHiddenGene == null) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'pandahiddengene' argument with invalid entityType: " + lower);
            }
        } else if (Version.has1_14Support() && lower.startsWith("pandamaingene")) {
            if (entityType != EntityType.PANDA) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'pandamaingene' argument on non-panda entity!");
                return false;
            }

            lower = lower.substring("pandamaingene".length()).trim();

            pandaMainGene = RMCUtil.parseEnum(lower, Panda.Gene.values());

            if (pandaMainGene == null) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'pandamaingene' argument with invalid entityType: " + lower);
            }
        } else if (Version.has1_14Support() && lower.startsWith("fox")) {
            String[] foxSplit = lower.split(" ");
            if (entityType != EntityType.FOX) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has '" + foxSplit[0] + "' argument on non-fox entity!");
            }

            if (lower.startsWith("foxcrouching")) {
                foxCrouching = true;
            } else if (lower.startsWith("foxfirsttrustedplayer")) {
                lower = lower.substring("foxfirsttrustedplayer".length()).trim();

                if (lower.equals("player")) {
                    foxFirstTrustedPlayer = true;
                } else {
                    try {
                        foxFirstTrustedPlayerUUID = UUID.fromString(lower);
                    } catch (IllegalArgumentException e) {
                        ErrorReporter.getInstance().warning("Flag " + flagType + " has 'foxfirsttrustedplayer' with invalid uuid: " + lower);
                    }
                }
            } else if (lower.startsWith("foxsecondtrustedplayer")) {
                lower = lower.substring("foxsecondtrustedplayer".length()).trim();

                if (lower.equals("player")) {
                    foxSecondTrustedPlayer = true;
                } else {
                    try {
                        foxSecondTrustedPlayerUUID = UUID.fromString(lower);
                    } catch (IllegalArgumentException e) {
                        ErrorReporter.getInstance().warning("Flag " + flagType + " has 'foxsecondtrustedplayer' with invalid uuid: " + lower);
                    }
                }
            } else if (lower.startsWith("foxsleeping")) {
                foxSleeping = true;
            } else if (lower.startsWith("fox")) {
                lower = lower.substring("fox".length()).trim();

                fox = RMCUtil.parseEnum(lower, Fox.Type.values());

                if (fox == null) {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'fox' argument with invalid entityType: " + lower);
                }
            }
        } else if (Version.has1_15Support() && lower.startsWith("bee")) {
            if (lower.startsWith("beeanger")) {
                lower = lower.substring("beeanger".length()).trim();

                try {
                    beeAnger = Integer.parseInt(lower);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'beeanger' argument with invalid value number: " + lower);
                }
            } else if (lower.startsWith("beecannotenterhiveticks")) {
                lower = lower.substring("beecannotenterhiveticks".length()).trim();

                try {
                    beeCannotEnterHiveTicks = Integer.parseInt(lower);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has 'beecannotenterhiveticks' argument with invalid value number: " + lower);
                }
            } else if (lower.equals("beehasnectar")) {
                beeHasNectar = true;
            } else if (lower.equals("beehasstung")) {
                beeHasStung = true;
            }
        } else if (Version.has1_16Support() && lower.startsWith("arrowcooldown")) {
            lower = lower.substring("arrowcooldown".length()).trim();

            try {
                arrowCooldown = Integer.parseInt(lower);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'arrowcooldown' argument with invalid value number: " + lower);
            }
        } else if (Version.has1_16Support() && lower.startsWith("arrowsinbody")) {
            lower = lower.substring("arrowsinbody".length()).trim();

            try {
                arrowsInBody = Integer.parseInt(lower);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has 'arrowsinbody' argument with invalid value number: " + lower);
            }
        } else if (Version.has1_16Support() && lower.equals("invisible")) {
            invisible = true;
        } else {
            ErrorReporter.getInstance().warning("Flag " + flagType + " has unknown argument: " + lower);
        }

        return true;
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();
        toHash += "flagType: " + flagType;
        toHash += "entityType: " + entityType.toString();

        toHash += "adult: " + adult;
        toHash += "ageLock: " + ageLock;
        toHash += "angry: " + angry;
        toHash += "baby: " + baby;
        toHash += "cat: " + cat.toString();
        toHash += "chance: " + chance;
        toHash += "color: " + color.toString();

        for (Float itemDrop : drop) {
            toHash += "drop: " + itemDrop;
        }

        for (ItemStack item : equip) {
            toHash += "equip: " + item.hashCode();
        }

        toHash += "hasChest: " + hasChest;
        toHash += "horseColor: " + horseColor.toString();
        toHash += "horseStyle: " + horseStyle.toString();
        toHash += "hit: " + hit;
        toHash += "hp: " + hp;
        toHash += "jumpStrength: " + jumpStrength.toString();
        toHash += "maxHp: " + maxHp;
        toHash += "mount: " + mount;
        toHash += "mountNext: " + mountNext;
        toHash += "name: " + name;
        toHash += "noBreed: " + noBreed;
        toHash += "noEffect: " + noEffect;
        toHash += "noHideName: " + noHideName;
        toHash += "noRemove: " + noRemove;
        toHash += "noSit: " + noSit;
        toHash += "num: " + num;
        toHash += "ocelot: " + ocelot.toString();
        toHash += "onFire: " + onFire;
        toHash += "pet: " + pet;
        toHash += "pickup: " + pickup.toString();
        toHash += "pigAnger: " + pigAnger;
        toHash += "playerIronGolem: " + playerIronGolem;

        for (PotionEffect effect : potions) {
            toHash += "potion: " + effect.hashCode();
        }

        toHash += "poweredCreeper: " + poweredCreeper;
        toHash += "rabbit: " + rabbit.toString();
        toHash += "saddle: " + saddle;
        toHash += "shearedSheep: " + shearedSheep;
        toHash += "spread: " + spread;
        toHash += "target: " + target;
        toHash += "villager: " + villager.toString();

        if (Version.has1_9Support()) {
            toHash += "noCollision: " + noCollision;
        }

        if (Version.has1_10Support()) {
            toHash += "invulnerable: " + invulnerable;
            toHash += "noAi: " + noAi;
        }

        if (!Version.has1_12Support()) {
            toHash += "elder: " + elder;
            toHash += "horse: " + horse.toString();
            toHash += "skeleton: " + skeleton.toString();
            toHash += "zombieVillager: " + zombieVillager;
        }

        if (Version.has1_12Support()) {
            toHash += "parrot: " + parrot;
        }

        if (Version.has1_13Support()) {
            toHash += "persistent: " + persistent;
        }

        if (Version.has1_14Support()) {
            toHash += "pandaHiddenGene: " + pandaHiddenGene;
            toHash += "pandaMainGene: " + pandaMainGene;
            toHash += "fox: " + fox;
            toHash += "foxCrouching: " + foxCrouching;
            toHash += "foxFirstTrustedPlayer: " + foxFirstTrustedPlayer;
            toHash += "foxFirstTrustedPlayerUUID: " + foxFirstTrustedPlayerUUID;
            toHash += "foxSecondTrustedPlayer: " + foxSecondTrustedPlayer;
            toHash += "foxSecondTrustedPlayerUUID: " + foxSecondTrustedPlayerUUID;
            toHash += "foxSleeping: " + foxSleeping;
        }

        if (Version.has1_15Support()) {
            toHash += "beeAnger: " + beeAnger;
            toHash += "beeCannotEnterHiveTicks: " + beeCannotEnterHiveTicks;
            toHash += "beeHasNectar: " + beeHasNectar;
            toHash += "beeHasStung: " + beeHasStung;
        }

        if (Version.has1_16Support()) {
            toHash += "arrowCooldown: " + arrowCooldown;
            toHash += "arrowsInBody: " + arrowsInBody;
            toHash += "invisible: " + invisible;
        }

        return toHash.hashCode();
    }
}
