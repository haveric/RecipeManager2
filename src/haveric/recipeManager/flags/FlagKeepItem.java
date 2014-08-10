package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Tools;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.Tools.ParseBit;
import haveric.recipeManager.recipes.FuelRecipe;
import haveric.recipeManager.recipes.SmeltRecipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class FlagKeepItem extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.KEEPITEM;

        A = new String[] { "{flag} <ingredient>", "{flag} <ingredient> | damage <num>", "{flag} <ingredient> | replace <item>", };

        D = new String[] { "Keeps the specified ingredient material from being used when crafting.", "This flag can be used more than once to specify more ingredients.", "", "The <ingredient> argument can be a material:data combination of the ingredient, data value beeing optional, just like defining an ingredient.", "", "For the optional 'damage <num>' argument you can specify the amount of damage to add or remove from a damageable item.", "Damaging the item beyond its max durability will break it.", "This argument only works for damageable items and the <num> can be a positive number to damage the item or negative to repair it.", "", "For the optional 'replace <item>' argument you can specify an item that will replace the ingredient.", "The <item> on 'replace' argument can support material:data:amount and enchantments, just like recipe results.", "This argument only works for unstackable ingredients. The item specified as replacement can be stackable.", };

        E = new String[] { "{flag} iron_axe  // makes the iron_axe ingredient persistent", "{flag} potion | replace bottle // using any kind of potion would return an empty bottle", "{flag} diamond_pickaxe | damage 5  // keeps the diamond pickaxe but damages it by 5 points", "{flag} shears | damage -99999 // keeps shears and fully repairs it", };
    }

    // Flag code

    private Map<String, Object> keepItems = new HashMap<String, Object>();

    public FlagKeepItem() {
    }

    public FlagKeepItem(FlagKeepItem flag) {
        for (Entry<String, Object> e : flag.keepItems.entrySet()) {
            Object obj = e.getValue();

            keepItems.put(e.getKey(), (obj instanceof ItemStack ? ((ItemStack) obj).clone() : obj));
        }
    }

    @Override
    public FlagKeepItem clone() {
        return new FlagKeepItem(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public Map<String, Object> getKeepItems() {
        return keepItems;
    }

    public void setKeepItems(Map<String, Object> keepItems) {
        this.keepItems = keepItems;
    }

    public Object getItem(ItemStack item) {
        Object obj = keepItems.get(String.valueOf(item.getTypeId() + ":" + item.getDurability()));

        if (obj == null) {
            return keepItems.get(String.valueOf(item.getTypeId()));
        }

        return obj;
    }

    /**
     * @param ingredient
     * @param object
     *            can be Integer or ItemStack object
     */
    public void addItem(ItemStack item, Object object) {
        String key = item.getTypeId() + (item.getDurability() == Vanilla.DATA_WILDCARD ? "" : ":" + item.getDurability());

        keepItems.put(key, object);
    }

    @Override
    protected boolean onParse(String value) {
        String[] split = value.split("\\|", 2);

        ItemStack item = Tools.parseItem(split[0], Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

        if (item == null) {
            return false;
        }

        String key = item.getTypeId() + (item.getDurability() == Vanilla.DATA_WILDCARD ? "" : ":" + item.getDurability());

        if (keepItems.containsKey(key)) {
            ErrorReporter.warning("Flag " + getType() + " already has the '" + Tools.Item.print(item) + "' ingredient added.");
            return false;
        }

        if (split.length > 1) {
            value = split[1].trim();

            if (value.startsWith("damage")) {
                Integer damage = 0;

                if (item.getType().getMaxDurability() > 0) {
                    value = value.substring("damage".length()).trim();

                    try {
                        damage = Integer.valueOf(value);
                    } catch (NumberFormatException e) {
                        ErrorReporter.warning("Flag " + getType() + " has invalid damage number: " + value + ", ignored.");
                    }
                } else {
                    ErrorReporter.warning("Flag " + getType() + " can't set damage on non-damageable item: " + Tools.Item.print(item) + ", ignored.");
                }

                keepItems.put(key, damage);
            } else if (value.startsWith("replace")) {
                if (item.getType().getMaxStackSize() > 1) {
                    ErrorReporter.warning("Flag " + getType() + " can't replace stackable ingredient: " + Tools.Item.print(item));
                    return false;
                }

                value = value.substring("replace".length());

                ItemStack replace = Tools.parseItem(value, 0);

                if (replace == null || replace.getTypeId() == 0) {
                    return false;
                }

                keepItems.put(key, replace);
            } else {
                ErrorReporter.warning("Flag " + getType() + " has unknown argument: " + value);
                return false;
            }
        } else {
            keepItems.put(key, Integer.valueOf(0));
        }

        return true;
    }

    private void parse(ReturnTask task, Inventory inv, Args a, int index) {
        ItemStack item = inv.getItem(index);

        if (item == null || item.getTypeId() == 0) {
            return;
        }

        Object obj = getItem(item);

        if (obj != null) {
            ItemStack clone = null;

            if (obj instanceof Integer) {
                clone = item.clone();
                Integer dmg = (Integer) obj;

                if (dmg != 0 && clone.getType().getMaxDurability() > 0) {
                    if (dmg > 0) {
                        short data = (short) (dmg + clone.getDurability());

                        if (data > clone.getType().getMaxDurability()) {
                            if (a.hasLocation()) {
                                a.location().getWorld().playSound(a.location(), Sound.ITEM_BREAK, 1.0f, 0.0f);
                            }

                            if (a.hasPlayer()) {
                                if (!a.hasLocation()) {
                                    a.player().playSound(a.player().getLocation(), Sound.ITEM_BREAK, 1.0f, 0.0f);
                                }

                                Bukkit.getPluginManager().callEvent(new PlayerItemBreakEvent(a.player(), clone)); // TODO unsure if I should really call this...
                            }

                            clone = null;
                        } else {
                            clone.setDurability(data);
                        }
                    } else {
                        clone.setDurability((short) Math.max(dmg + clone.getDurability(), 0));
                    }
                }
            } else if (obj instanceof ItemStack) {
                clone = ((ItemStack) obj).clone();
            }

            if (clone != null) {
                task.setItem(index, clone);
            }
        }
    }

    @Override
    protected void onCrafted(final Args a) {
        if (!a.hasInventory()) {
            a.addCustomReason("Needs an inventory !");
            return;
        }

        if (a.inventory() instanceof CraftingInventory) {
            CraftingInventory inv = (CraftingInventory) a.inventory();
            ReturnTask task = new ReturnTask(inv);

            for (int i = 1; i < 10; i++) {
                parse(task, inv, a, i);
            }

            task.startIfRequired();
        } else if (a.inventory() instanceof FurnaceInventory) {
            FurnaceInventory inv = (FurnaceInventory) a.inventory();
            ReturnTask task = new ReturnTask(inv);

            if (a.recipe() instanceof SmeltRecipe) {
                parse(task, inv, a, 0);
                parse(task, inv, a, 1);
            } else if (a.recipe() instanceof FuelRecipe) {
                parse(task, inv, a, 1);
            } else {
                a.addCustomReason("Needs a recipe !");
                return;
            }

            task.startIfRequired();
        } else {
            a.addCustomReason("Needs a crafting or furnace inventory !");
        }
    }

    private class ReturnTask extends BukkitRunnable {
        private Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
        private boolean taskRequired = false;
        private Inventory inv;

        public ReturnTask(Inventory inv) {
            this.inv = inv;
        }

        public void setItem(int index, ItemStack replace) {
            items.put(index, replace);
            taskRequired = true;
        }

        public void startIfRequired() {
            if (taskRequired) {
                runTaskLater(RecipeManager.getPlugin(), 1);
            }
        }

        @Override
        public void run() {
            for (Entry<Integer, ItemStack> e : items.entrySet()) {
                inv.setItem(e.getKey(), e.getValue());
            }
        }
    }
}
