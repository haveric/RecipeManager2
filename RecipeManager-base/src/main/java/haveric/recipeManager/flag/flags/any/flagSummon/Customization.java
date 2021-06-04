package haveric.recipeManager.flag.flags.any.flagSummon;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.messages.MessageSender;
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

    private EntityType type;
    private boolean noAi = false;
    private boolean noEffect = false;
    private boolean noRemove = false;
    private boolean invulnerable = false;
    private boolean invisible = false;
    private boolean persistent = false;
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
    @SuppressWarnings("deprecation")
    private Ocelot.Type ocelot = null;
    private Cat.Type cat = null;
    private boolean saddle = false;
    private boolean mount = false;
    private DyeColor color = null;
    private boolean shearedSheep = false;
    @SuppressWarnings("deprecation")
    private Skeleton.SkeletonType skeleton = null;
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
    @SuppressWarnings("deprecation")
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

    public Customization(String newFlagType, EntityType newType) {
        flagType = newFlagType;
        type = newType;
    }

    public Customization(Customization c) {
        flagType = c.flagType;

        type = c.type;
        noAi = c.noAi;
        noEffect = c.noEffect;
        noRemove = c.noRemove;
        invulnerable = c.invulnerable;
        invisible = c.invisible;
        persistent = c.persistent;
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
        ocelot = c.ocelot;
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
            if (Version.has1_10Support()) {
                ent.setInvulnerable(invulnerable);
                ent.setAI(!noAi);
            }

            if (Version.has1_16Support()) {
                ent.setInvisible(invisible);
            }

            if (Version.has1_13Support()) {
                ent.setPersistent(persistent);
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

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean newInvisible) {
        invisible = newInvisible;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean newPersistent) {
        persistent = newPersistent;
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

    public float getJumpStrength() {
        return jumpStrength;
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

    @SuppressWarnings("deprecation")
    public Ocelot.Type getOcelot() {
        return ocelot;
    }

    public void setOcelot(@SuppressWarnings("deprecation") Ocelot.Type newOcelot) {
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

    @SuppressWarnings("deprecation")
    public Skeleton.SkeletonType getSkeleton() {
        return skeleton;
    }

    public void setSkeleton(@SuppressWarnings("deprecation") Skeleton.SkeletonType newSkeleton) {
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

    public void setHorseVariant(@SuppressWarnings("deprecation") Horse.Variant newVariant) {
        horse = newVariant;
    }

    @SuppressWarnings("deprecation")
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
        toHash += "invisible: " + invisible;
        toHash += "persistent: " + persistent;
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
