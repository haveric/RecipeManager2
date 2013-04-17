package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.RecipeManager;

;

public class FlagSummon extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.SUMMON;
        
        A = new String[]
        {
            "{flag} <type> | [arguments]",
            "{flag} false",
        };
        
        D = new String[]
        {
            "NOTE: not finished, some arguments don't do anything yet, most of them work, some of them are broken.", // TODO finish
            "",
            "Summons a creature.",
            "Using this flag more than once will add more creatures.",
            "",
            "The <type> argument can be a living entity type, you can find all entity types in '" + Files.FILE_INFO_NAMES + "' file.",
            "",
            "Optionally you can add some arguments separated by | character, those being:",
            "  noeffect             = no spawning particle effects on creature.",
            "  noremove             = prevents creature from being removed if nobody is near it.",
            "  mountnext            = this creature will mount the next creature definition that triggers after it.",
            "  chance <0.01-100>%   = chance of the creature to spawn, this value is for individual creatures.",
            "  num <number>         = spawn more cloned creatures.",
            "  spread <range>       = spawns creature(s) spread within block range instead of on top of workbench or furnace. (WARNING: can be CPU intensive)",
            "  view <yaw> [pitch]   = sets the creature's yaw view axis and optionally the pitch view axis too, values must be angles from -180 to 180.",
            "  target               = creature targets crafter, that means monsters attack and animals follow and the rest do nothing.",
            "  onfire <time>        = spawn creature on fire for <time> amount of seconds, value can be float.",
            "  nodamage <time>      = spawn creature as invulnerable for <time> amount of seconds, value can be float.",
            "  damagedelay <time>   = make creature have invulnerability of <time> amount of seconds after being damaged.",
            "  pickup [true/false]  = change if creature can pick-up dropped items.",
            "  pet [nosit]          = makes creature owned by crafter, only works for wolf and ocelot, optionally specify 'nosit' to not spawn creature in sit stance.",
            "  collar <dye>         = sets wolf collar color, only works for wolves and only if 'pet' argument is set, values can be found in '" + Files.FILE_INFO_NAMES + "' file at 'DYE COLORS' section.",
            "  angrywolf            = makes wolf angry, only works for wolves and it's incompatible with 'pet' and 'collar' arguments.",
            "  cat <type>           = ocelot type, available values: wild, black, red, siamese.",
            "  saddle [mount]       = adds saddle on creature, only works for pig, optionally you can specify 'mount' to make crafter mount creature.",
            "  color <dye>          = sets character color, only works for sheep, values can be found in '" + Files.FILE_INFO_NAMES + "' file at 'DYE COLORS' section.",
            "  shearedsheep         = sets the sheep as sheared, only works for sheep.",
            "  zombievillager       = makes zombie a zombie villager, only works on zombies.",
            "  villager <type>      = set the villager profession, values: famer, librarian, priest, blacksmith, butcher.",
            "  skeleton <type>      = set the skeleton type, values: normal, wither", // TODO maybe list SkeletonType directly ?
            "  poweredcreeper       = makes creeper a powered one, only works for creepers.",
            "  name <text>          = sets the creature's name, supports colors (<red>, &3, etc).",
            "  nohidename           = don't hide name plate when aiming away from creature.",
            "  hp <health> [max]    = set creature's health and optionally max health",
            "  baby [always]        = spawn creature as a baby, optionally specify 'always' to prevent creature from growing up (doesn't work for zombies)",
            "  head <item> [drop%]  = equip an item on the creature's head with optional drop chance.",
            "  chest <item> [drop%] = equip an item on the creature's chest with optional drop chance.",
            "  legs <item> [drop%]  = equip an item on the creature's legs with optional drop chance.",
            "  feet <item> [drop%]  = equip an item on the creature's feet with optional drop chance.",
            "  hand <item> [drop%]  = equip an item on the creature's hand with optional drop chance.",
            "",
            "These arguments can be used in any order and they're all optional.",
        };
        
        E = new String[]
        {
            "{flag}", // TODO
        };
    }
    
    // Flag code
    
    public class Customization implements Cloneable
    {
        private EntityType type;
        private boolean noEffect = false;
        private boolean noRemove = false;
        private float chance = 100.0f;
        private int num = 1;
        private int spread = 0;
        private Float yaw = null;
        private Float pitch = null;
        private float onFire = 0;
        private float noDamage = 0;
        private float damageDelay = 0;
        private Boolean pickup = null;
        private boolean target = false;
        private boolean pet = false;
        private boolean noSit = false;
        private DyeColor collar = null;
        private boolean angryWolf = false;
        private Ocelot.Type cat = null;
        private boolean saddle = false;
        private boolean mount = false;
        private DyeColor color = null;
        private boolean shearedSheep = false;
        private SkeletonType skeleton = null;
        private boolean zombieVillager = false;
        private Villager.Profession villager = null;
        private boolean poweredCreeper = false;
        private MaterialData enderHand = null;
        private boolean playerIronGolem = false;
        private int pigZombieAnger = 0;
        private String name = null;
        private boolean noHideName = false;
        private int hp = 0;
        private int maxHp = 0;
        private boolean baby = false;
        private boolean alwaysBaby = false;
        private ItemStack[] equip = new ItemStack[5];
        private float[] drop = new float[5];
        private List<PotionEffect> effects = new ArrayList<PotionEffect>();
        private boolean mountNext = false;
        
        public Customization(EntityType type)
        {
            this.type = type;
        }
        
        public Customization(Customization clone)
        {
            // TODO clone
            
            type = clone.type;
            noEffect = clone.noEffect;
            noRemove = clone.noRemove;
            chance = clone.chance;
            num = clone.num;
            spread = clone.spread;
            target = clone.target;
            pet = clone.pet;
            name = clone.name;
            noHideName = clone.noHideName;
            hp = clone.hp;
            maxHp = clone.maxHp;
            baby = clone.baby;
            alwaysBaby = clone.alwaysBaby;
            equip = clone.equip.clone();
            drop = clone.drop.clone();
            effects.addAll(clone.effects);
        }
        
        @Override
        public Customization clone()
        {
            return new Customization(this);
        }
        
        public List<LivingEntity> spawn(Location location, Player player)
        {
            List<LivingEntity> entities = new ArrayList<LivingEntity>(this.num);
            World world = location.getWorld();
            
            for(int num = 0; num < this.num; num++)
            {
                if(spread > 0)
                {
                    int minX = location.getBlockX() - spread / 2;
                    int minY = location.getBlockY() - spread / 2;
                    int minZ = location.getBlockZ() - spread / 2;
                    int maxX = location.getBlockX() + spread / 2;
                    int maxY = location.getBlockY() + spread / 2;
                    int maxZ = location.getBlockZ() + spread / 2;
                    
                    int tries = spread * 10;
                    boolean found = false;
                    
                    while(tries-- > 0)
                    {
                        int x = minX + RecipeManager.random.nextInt(maxX - minX);
                        int z = minZ + RecipeManager.random.nextInt(maxZ - minZ);
                        int y = 0;
                        
                        for(y = maxY; y >= minY; y--)
                        {
                            if(!Material.getMaterial(world.getBlockTypeIdAt(x, y, z)).isSolid())
                            {
                                found = true;
                                break;
                            }
                        }
                        
                        if(found)
                        {
                            location.setX(x);
                            location.setY(y);
                            location.setZ(z);
                            break;
                        }
                    }
                    
                    if(!found)
                    {
                        Messages.debug("Can't find suitable location !");
                    }
                    
                    location.add(0.5, 0, 0.5);
                }
                
                if(yaw != null)
                {
                    location.setYaw(yaw);
                }
                
                if(pitch != null)
                {
                    location.setPitch(pitch);
                }
                
                LivingEntity ent = (LivingEntity)world.spawnEntity(location, type);
                
                if(!noEffect)
                {
                    world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 20);
                }
                
                if(name != null)
                {
                    ent.setCustomName(name);
                    ent.setCustomNameVisible(noHideName);
                }
                
                if(onFire > 0.0f)
                {
                    ent.setFireTicks((int)Math.ceil(onFire * 20.0));
                }
                
                if(noDamage > 0.0f)
                {
                    ent.setNoDamageTicks((int)Math.ceil(noDamage * 20.0));
                }
                
                if(damageDelay > 0.0f)
                {
                    ent.setMaximumNoDamageTicks((int)Math.ceil(damageDelay * 20.0));
                }
                
                if(pickup != null)
                {
                    ent.setCanPickupItems(pickup);
                }
                
                if(pet && ent instanceof Tameable)
                {
                    Tameable npc = (Tameable)ent;
                    npc.setOwner(player);
                    npc.setTamed(true);
                }
                
                if(ent instanceof Wolf)
                {
                    Wolf npc = (Wolf)ent;
                    
                    if(pet)
                    {
                        if(noSit)
                        {
                            npc.setSitting(false);
                        }
                        
                        if(collar != null)
                        {
                            npc.setCollarColor(collar);
                        }
                    }
                    else if(angryWolf)
                    {
                        npc.setAngry(true);
                    }
                }
                
                if(ent instanceof Ocelot)
                {
                    Ocelot npc = (Ocelot)ent;
                    
                    if(pet && noSit)
                    {
                        npc.setSitting(false);
                    }
                    
                    if(cat != null)
                    {
                        npc.setCatType(cat);
                    }
                }
                
                if(hp > 0)
                {
                    ent.setHealth(hp);
                    
                    if(maxHp > 0)
                    {
                        ent.setMaxHealth(maxHp);
                    }
                }
                
                if(baby && ent instanceof Ageable)
                {
                    Ageable npc = (Ageable)ent;
                    npc.setBaby();
                    
                    if(alwaysBaby)
                    {
                        npc.setAgeLock(true);
                    }
                }
                
                if(saddle && ent instanceof Pig)
                {
                    Pig npc = (Pig)ent;
                    npc.setSaddle(true);
                    
                    if(mount)
                    {
                        npc.setPassenger(player);
                    }
                }
                
                if(ent instanceof Zombie)
                {
                    Zombie npc = (Zombie)ent;
                    
                    if(baby)
                    {
                        npc.setBaby(true);
                    }
                    
                    if(zombieVillager)
                    {
                        npc.setVillager(true);
                    }
                }
                
                if(villager != null && ent instanceof Villager)
                {
                    Villager npc = (Villager)ent;
                    npc.setProfession(villager);
                }
                
                if(poweredCreeper && ent instanceof Creeper)
                {
                    Creeper npc = (Creeper)ent;
                    npc.setPowered(true);
                }
                
                if(enderHand != null && ent instanceof Enderman)
                {
                    Enderman npc = (Enderman)ent;
                    npc.setCarriedMaterial(enderHand);
                    // TODO test entity equipament in-hand item against this
                }
                
                if(playerIronGolem && ent instanceof IronGolem)
                {
                    IronGolem npc = (IronGolem)ent;
                    npc.setPlayerCreated(true);
                    // TODO what exacly does this do ?
                }
                
                if(shearedSheep && ent instanceof Sheep)
                {
                    Sheep npc = (Sheep)ent;
                    npc.setSheared(true);
                }
                
                if(color != null && ent instanceof Colorable)
                {
                    Colorable npc = (Colorable)ent;
                    npc.setColor(color);
                }
                
                if(skeleton != null && ent instanceof Skeleton)
                {
                    Skeleton npc = (Skeleton)ent;
                    npc.setSkeletonType(skeleton);
                }
                
                if(target && ent instanceof Creature)
                {
                    Creature npc = (Creature)ent;
                    npc.setTarget(player);
                }
                
                if(pigZombieAnger > 0 && ent instanceof PigZombie)
                {
                    PigZombie npc = (PigZombie)ent;
                    npc.setAnger(pigZombieAnger);
                }
                
                ent.setRemoveWhenFarAway(!noRemove);
                
                EntityEquipment eq = ent.getEquipment();
                
                for(int i = 0; i < equip.length; i++)
                {
                    ItemStack item = equip[i];
                    
                    if(item == null)
                    {
                        continue;
                    }
                    
                    switch(i)
                    {
                        case 0:
                            eq.setHelmet(item);
                            eq.setHelmetDropChance(drop[i]);
                            break;
                        
                        case 1:
                            eq.setChestplate(item);
                            eq.setChestplateDropChance(drop[i]);
                            break;
                        
                        case 2:
                            eq.setLeggings(item);
                            eq.setLeggingsDropChance(drop[i]);
                            break;
                        
                        case 3:
                            eq.setBoots(item);
                            eq.setBootsDropChance(drop[i]);
                            break;
                        
                        case 4:
                            eq.setItemInHand(item);
                            eq.setItemInHandDropChance(drop[i]);
                            break;
                    }
                }
                
                entities.add(ent);
            }
            
            return entities;
        }
        
        public EntityType getType()
        {
            return type;
        }
        
        public void setType(EntityType type)
        {
            this.type = type;
        }
        
        public boolean isNoEffect()
        {
            return noEffect;
        }
        
        public void setNoEffect(boolean noEffect)
        {
            this.noEffect = noEffect;
        }
        
        public boolean isNoRemove()
        {
            return noRemove;
        }
        
        public void setNoRemove(boolean noRemove)
        {
            this.noRemove = noRemove;
        }
        
        public float getChance()
        {
            return chance;
        }
        
        public void setChance(float chance)
        {
            if(chance < 0.01f || chance > 100.0f)
            {
                this.chance = Math.min(Math.max(chance, 0.01f), 100.0f);
                
                RecipeErrorReporter.warning("Flag " + getType() + " has chance value less than 0.01 or higher than 100.0, value trimmed.");
            }
            else
            {
                this.chance = chance;
            }
        }
        
        public int getNum()
        {
            return num;
        }
        
        public void setNum(int num)
        {
            if(num < 1)
            {
                this.num = 1;
                
                RecipeErrorReporter.warning("The " + getType() + " flag can't have 'num' argument less than 1, set to 1.");
            }
            else
            {
                this.num = num;
            }
        }
        
        public int getSpread()
        {
            return spread;
        }
        
        public void setSpread(int spread)
        {
            this.spread = spread;
        }
        
        public boolean isTarget()
        {
            return target;
        }
        
        public void setTarget(boolean target)
        {
            this.target = target;
        }
        
        public boolean isPet()
        {
            return pet;
        }
        
        public void setPet(boolean pet)
        {
            this.pet = pet;
        }
        
        public boolean isSaddle()
        {
            return saddle;
        }
        
        public void setSaddle(boolean saddle)
        {
            this.saddle = saddle;
        }
        
        public boolean isMount()
        {
            return mount;
        }
        
        public void setMount(boolean mount)
        {
            this.mount = mount;
        }
        
        public String getName()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public boolean isNoHideName()
        {
            return noHideName;
        }
        
        public void setNoHideName(boolean noHideName)
        {
            this.noHideName = noHideName;
        }
        
        public int getHp()
        {
            return hp;
        }
        
        public void setHp(int hp)
        {
            this.hp = hp;
        }
        
        public int getMaxHp()
        {
            return maxHp;
        }
        
        public void setMaxHp(int maxHp)
        {
            this.maxHp = maxHp;
        }
        
        public boolean isBaby()
        {
            return baby;
        }
        
        public void setBaby(boolean baby)
        {
            this.baby = baby;
        }
        
        public boolean isAlwaysBaby()
        {
            return alwaysBaby;
        }
        
        public void setAlwaysBaby(boolean alwaysBaby)
        {
            this.alwaysBaby = alwaysBaby;
        }
        
        public ItemStack[] getEquip()
        {
            return equip;
        }
        
        public void setEquip(ItemStack[] equip)
        {
            this.equip = equip;
        }
        
        public float[] getDrop()
        {
            return drop;
        }
        
        public void setDrop(float[] drop)
        {
            this.drop = drop;
        }
        
        public List<PotionEffect> getEffects()
        {
            return effects;
        }
        
        public void setEffects(List<PotionEffect> effects)
        {
            this.effects = effects;
        }
        
        public Float getYaw()
        {
            return yaw;
        }
        
        public void setYaw(Float yaw)
        {
            this.yaw = yaw;
        }
        
        public Float getPitch()
        {
            return pitch;
        }
        
        public void setPitch(Float pitch)
        {
            this.pitch = pitch;
        }
        
        public float getOnFire()
        {
            return onFire;
        }
        
        public void setOnFire(float onFire)
        {
            this.onFire = onFire;
        }
        
        public float getNoDamage()
        {
            return noDamage;
        }
        
        public void setNoDamage(float noDamage)
        {
            this.noDamage = noDamage;
        }
        
        public float getDamageDelay()
        {
            return damageDelay;
        }
        
        public void setDamageDelay(float damageDelay)
        {
            this.damageDelay = damageDelay;
        }
        
        public Boolean getPickup()
        {
            return pickup;
        }
        
        public void setPickup(Boolean pickup)
        {
            this.pickup = pickup;
        }
        
        public boolean isNoSit()
        {
            return noSit;
        }
        
        public void setNoSit(boolean noSit)
        {
            this.noSit = noSit;
        }
        
        public DyeColor getCollar()
        {
            return collar;
        }
        
        public void setCollar(DyeColor collar)
        {
            this.collar = collar;
        }
        
        public boolean isAngryWolf()
        {
            return angryWolf;
        }
        
        public void setAngryWolf(boolean angryWolf)
        {
            this.angryWolf = angryWolf;
        }
        
        public Ocelot.Type getCat()
        {
            return cat;
        }
        
        public void setCat(Ocelot.Type cat)
        {
            this.cat = cat;
        }
        
        public DyeColor getColor()
        {
            return color;
        }
        
        public void setColor(DyeColor color)
        {
            this.color = color;
        }
        
        public boolean isShearedSheep()
        {
            return shearedSheep;
        }
        
        public void setShearedSheep(boolean shearedSheep)
        {
            this.shearedSheep = shearedSheep;
        }
        
        public SkeletonType getSkeleton()
        {
            return skeleton;
        }
        
        public void setSkeleton(SkeletonType skeleton)
        {
            this.skeleton = skeleton;
        }
        
        public boolean isZombieVillager()
        {
            return zombieVillager;
        }
        
        public void setZombieVillager(boolean zombieVillager)
        {
            this.zombieVillager = zombieVillager;
        }
        
        public Villager.Profession getVillager()
        {
            return villager;
        }
        
        public void setVillager(Villager.Profession villager)
        {
            this.villager = villager;
        }
        
        public boolean isPoweredCreeper()
        {
            return poweredCreeper;
        }
        
        public void setPoweredCreeper(boolean poweredCreeper)
        {
            this.poweredCreeper = poweredCreeper;
        }
        
        public MaterialData getEnderHand()
        {
            return enderHand;
        }
        
        public void setEnderHand(MaterialData enderHand)
        {
            this.enderHand = enderHand;
        }
        
        public boolean isPlayerIronGolem()
        {
            return playerIronGolem;
        }
        
        public void setPlayerIronGolem(boolean playerIronGolem)
        {
            this.playerIronGolem = playerIronGolem;
        }
        
        public int getPigZombieAnger()
        {
            return pigZombieAnger;
        }
        
        public void setPigZombieAnger(int pigZombieAnger)
        {
            this.pigZombieAnger = pigZombieAnger;
        }
        
        public boolean isMountNext()
        {
            return mountNext;
        }
        
        public void setMountNext(boolean mountNext)
        {
            this.mountNext = mountNext;
        }
    }
    
    List<Customization> spawn = new ArrayList<Customization>();
    
    public FlagSummon()
    {
    }
    
    public FlagSummon(FlagSummon flag)
    {
        for(Customization c : flag.spawn)
        {
            spawn.add(c.clone());
        }
    }
    
    @Override
    public FlagSummon clone()
    {
        return new FlagSummon(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public List<Customization> getSpawnList()
    {
        return spawn;
    }
    
    public void setSpawnList(List<Customization> list)
    {
        if(list == null)
        {
            this.remove();
        }
        else
        {
            this.spawn = list;
        }
    }
    
    public void addSpawn(Customization spawn)
    {
        Validate.notNull(spawn, "'spawn' can not be null!");
        
        this.spawn.add(spawn);
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.split("\\|");
        
        value = split[0].trim();
        EntityType type = EntityType.fromName(value);
        
        if(type == null || !type.isAlive())
        {
            RecipeErrorReporter.error("The " + getType() + " flag has invalid creature: " + value, "Look in '" + Files.FILE_INFO_NAMES + "' at 'ENTITY TYPES' section for ALIVE entities.");
            return false;
        }
        
        Customization customize = new Customization(type);
        
        if(split.length > 1)
        {
            for(int i = 1; i < split.length; i++)
            {
                String original = split[i].trim();
                value = original.toLowerCase();
                
                if(value.equals("noremove"))
                {
                    customize.setNoRemove(true);
                }
                else if(value.equals("noeffect"))
                {
                    customize.setNoEffect(true);
                }
                else if(value.equals("target"))
                {
                    customize.setTarget(true);
                }
                else if(value.equals("nohidename"))
                {
                    customize.setNoHideName(true);
                }
                else if(value.equals("mountnext"))
                {
                    customize.setMountNext(true);
                }
                else if(value.equals("angrywolf"))
                {
                    customize.setAngryWolf(true);
                }
                else if(value.equals("shearedsheep"))
                {
                    customize.setShearedSheep(true);
                }
                else if(value.equals("zombievillager"))
                {
                    customize.setZombieVillager(true);
                }
                else if(value.equals("poweredcreeper"))
                {
                    customize.setPoweredCreeper(true);
                }
                else if(value.startsWith("pet"))
                {
                    customize.setPet(true);
                    
                    if(value.length() > "pet".length())
                    {
                        value = value.substring("pet".length()).trim();
                        
                        if(value.equals("nosit"))
                        {
                            customize.setNoSit(true);
                        }
                        else
                        {
                            RecipeErrorReporter.warning("Flag " + getType() + " has 'pet' argument with unknown value: " + value);
                        }
                    }
                }
                else if(value.startsWith("baby"))
                {
                    customize.setBaby(true);
                    
                    if(value.length() > "baby".length())
                    {
                        value = value.substring("baby".length()).trim();
                        
                        if(value.equals("always"))
                        {
                            customize.setAlwaysBaby(true);
                        }
                        else
                        {
                            RecipeErrorReporter.warning("Flag " + getType() + " has 'baby' argument with unknown value: " + value);
                        }
                    }
                }
                else if(value.startsWith("chance"))
                {
                    value = value.substring("chance".length()).trim();
                    
                    if(value.charAt(value.length() - 1) == '%')
                    {
                        value = value.substring(0, value.length() - 1);
                    }
                    
                    try
                    {
                        customize.setChance(Float.valueOf(value));
                    }
                    catch(NumberFormatException e)
                    {
                        RecipeErrorReporter.warning("Flag " + getType() + " has 'chance' argument with invalid number: " + value);
                        continue;
                    }
                }
                else if(value.startsWith("num"))
                {
                    value = value.substring("num".length()).trim();
                    
                    try
                    {
                        customize.setNum(Integer.valueOf(value));
                    }
                    catch(NumberFormatException e)
                    {
                        RecipeErrorReporter.warning("Flag " + getType() + " has 'num' argument with invalid value number: " + value);
                    }
                }
                else if(value.startsWith("spread"))
                {
                    value = value.substring("spread".length()).trim();
                    
                    try
                    {
                        customize.setSpread(Integer.valueOf(value));
                    }
                    catch(NumberFormatException e)
                    {
                        RecipeErrorReporter.warning("Flag " + getType() + " has 'spread' argument with invalid value number: " + value);
                    }
                }
                else if(value.startsWith("view"))
                {
                    value = value.substring("view".length()).trim();
                    String[] args = value.split(" ");
                    
                    value = args[0].trim();
                    
                    try
                    {
                        customize.setYaw(Float.valueOf(value));
                    }
                    catch(NumberFormatException e)
                    {
                        RecipeErrorReporter.warning("Flag " + getType() + " has 'view' argument with invalid value number for yaw: " + value);
                    }
                    
                    if(args.length > 1)
                    {
                        value = args[1].trim();
                        
                        try
                        {
                            customize.setPitch(Float.valueOf(value));
                        }
                        catch(NumberFormatException e)
                        {
                            RecipeErrorReporter.warning("Flag " + getType() + " has 'view' argument with invalid value number for pitch: " + value);
                        }
                    }
                }
                // TODO
                else
                {
                    RecipeErrorReporter.warning("Flag " + getType() + " has unknown argument: " + value);
                }
            }
        }
        
        addSpawn(customize);
        
        return true;
    }
    
    @Override
    protected void onCrafted(Args a)
    {
        if(!a.hasLocation())
        {
            a.addCustomReason("Needs location!");
            return;
        }
        
        for(Customization c : spawn)
        {
            if(c.pet || c.target || (c.saddle && c.mount))
            {
                if(!a.hasPlayer())
                {
                    a.addCustomReason("Needs player!");
                    return;
                }
                
                break;
            }
        }
        
        Location l = a.location();
        
        if(l.getX() == l.getBlockX())
        {
            l.add(0.5, 1.5, 0.5);
        }
        
        Customization mount = null;
        
        for(Customization c : spawn)
        {
            if(c.chance < 100.0f && c.chance < (RecipeManager.random.nextFloat() * 100))
            {
                continue;
            }
            
            if(mount != null)
            {
                List<LivingEntity> mounted = mount.spawn(l, a.player());
                List<LivingEntity> ents = c.spawn(l, a.player());
                
                for(int i = 0; i < Math.min(mounted.size(), ents.size()); i++)
                {
                    ents.get(i).setPassenger(mounted.get(i));
                }
                
                // TODO test ^
                
                mount = null;
            }
            else if(c.mountNext)
            {
                mount = c;
            }
            else
            {
                c.spawn(l, a.player());
            }
        }
        
        if(mount != null)
        {
            mount.spawn(l, a.player());
        }
    }
}
