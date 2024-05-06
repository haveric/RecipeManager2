package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.cooking.furnace.RMBaseFurnaceRecipe;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import haveric.recipeManager.recipes.item.ItemRecipe;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FlagKeepItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.KEEP_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <ingredient>",
            "{flag} <ingredient> | damage <num>",
            "{flag} <ingredient> | replace <item>",
            "{flag} <ingredient> | replace item:<name>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Keeps the specified ingredient material from being used when crafting.",
            "This flag can be used more than once to specify more ingredients.",
            "",
            "The <ingredient> argument can be a material:data combination of the ingredient, data value being optional, just like defining an ingredient.",
            "",
            "For the optional 'damage <num>' argument you can specify the amount of damage to add or remove from a damageable item.",
            "Damaging the item beyond its max durability will break it.",
            "This argument only works for damageable items and the <num> can be a positive number to damage the item or negative to repair it.",
            "",
            "For the optional 'replace <item>' argument you can specify an item that will replace the ingredient.",
            "  The <item> on 'replace' argument can support material:data:amount and enchantments, just like recipe results.",
            "  You can use a predefined item from an item recipe:",
            "    Format = item:<name>",
            "    <name> = The name of an item recipe defined before this flag.",
            "  This argument only works for unstackable ingredients. The item specified as replacement can be stackable.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} iron_axe  // makes the iron_axe ingredient persistent",
            "{flag} potion | replace bottle // using any kind of potion would return an empty bottle",
            "{flag} potion | replace item:test sword // using any kind of potion would return the item from the 'test sword' recipe, assuming it's defined.",
            "{flag} diamond_pickaxe | damage 5  // keeps the diamond pickaxe but damages it by 5 points",
            "{flag} shears | damage -99999 // keeps shears and fully repairs it", };
    }


    private Map<String, Object> keepItems = new HashMap<>();

    public FlagKeepItem() {
    }

    public FlagKeepItem(FlagKeepItem flag) {
        super(flag);
        for (Entry<String, Object> e : flag.keepItems.entrySet()) {
            Object obj = e.getValue();

            if (obj instanceof ItemStack) {
                keepItems.put(e.getKey(), ((ItemStack) obj).clone());
            } else {
                keepItems.put(e.getKey(), obj);
            }
        }
    }

    @Override
    public FlagKeepItem clone() {
        return new FlagKeepItem((FlagKeepItem) super.clone());
    }

    public Map<String, Object> getKeepItems() {
        return keepItems;
    }

    public void setKeepItems(Map<String, Object> newKeepItems) {
        keepItems = newKeepItems;
    }

    public Object getItem(ItemStack item) {
        Object obj = keepItems.get(item.getType() + ":" + item.getDurability());

        if (obj == null) {
            return keepItems.get(String.valueOf(item.getType().toString()));
        }

        return obj;
    }

    /**
     * @param item
     * @param object
     *            can be Integer or ItemStack object
     */
    public void addItem(ItemStack item, Object object) {
        String key = Tools.convertItemToStringId(item);
        keepItems.put(key, object);
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.split("\\|", 2);
        ItemStack item = Tools.parseItem(split[0], RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

        if (item == null) {
            return false;
        }

        String key = Tools.convertItemToStringId(item);
        if (keepItems.containsKey(key)) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " already has the '" + ToolsItem.print(item) + "' ingredient added.");
            return false;
        }

        if (split.length > 1) {
            value = split[1].trim();

            if (value.startsWith("damage")) {
                int damage = 0;

                Short maxDurability = RecipeManager.getSettings().getCustomData(item.getType());
                if (maxDurability > 0) {
                    value = value.substring("damage".length()).trim();

                    try {
                        damage = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid damage number: " + value + ", ignored.");
                    }
                } else {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " can't set damage on non-damageable item: " + ToolsItem.print(item) + ", ignored.");
                }

                keepItems.put(key, damage);
            } else if (value.startsWith("replace")) {
                value = value.substring("replace".length()).trim();

                if (value.startsWith("item:")) {
                    value = value.substring("item:".length());

                    ItemRecipe recipe = ItemRecipe.getRecipe(value);
                    if (recipe == null) {
                        return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid item reference: " + value + "!");
                    } else {
                        keepItems.put(key, recipe);
                    }
                } else {
                    ItemStack replace = Tools.parseItem(value, 0);
                    if (replace == null || replace.getType() == Material.AIR) {
                        return false;
                    }

                    keepItems.put(key, replace);
                }
            } else {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + value);
                return false;
            }
        } else {
            keepItems.put(key, 0);
        }

        return true;
    }

    private void parse(Inventory inv, Args a, final int index) {
        ItemStack item = inv.getItem(index);
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        Object obj = getItem(item);
        if (obj != null) {
            ItemStack clone = null;

            if (obj instanceof Integer) {
                clone = item.clone();
                int dmg = (Integer) obj;

                short maxDurability = RecipeManager.getSettings().getCustomData(item.getType());
                if (dmg != 0 && maxDurability > 0) {
                    if (dmg > 0) {
                        short data = (short) (dmg + clone.getDurability());

                        if (data > maxDurability) {
                            Sound soundItemBreak = Tools.getSound("ENTITY_ITEM_BREAK");
                            if (a.hasLocation()) {
                                a.location().getWorld().playSound(a.location(), soundItemBreak, 1.0f, 0.0f);
                            }

                            if (a.hasPlayer()) {
                                if (!a.hasLocation()) {
                                    a.player().playSound(a.player().getLocation(), soundItemBreak, 1.0f, 0.0f);
                                }

                                inv.setItem(index, new ItemStack(Material.AIR));
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
            } else if (obj instanceof ItemRecipe) {
                ItemRecipe itemRecipe = (ItemRecipe) obj;
                ItemResult result = itemRecipe.getResult();
                Args itemArgs = ArgBuilder.create(a).recipe(itemRecipe).result(result).build();
                itemArgs.setFirstRun(true);

                if (result.getFlags().sendCrafted(itemArgs, true)) {
                    clone = itemArgs.result();
                }
            }

            if (clone == null) {
                inv.setItem(index, new ItemStack(Material.AIR));
            } else {
                ItemStack invItem = inv.getItem(index);
                if (invItem != null && invItem.getType() != clone.getType()) {
                    if (a.hasPlayer()) {
                        Player player = a.player();
                        if (Tools.playerCanAddItem(player, clone)) {
                            player.getInventory().addItem(clone);
                        } else {
                            player.getWorld().dropItem(player.getLocation(), clone);
                        }
                    } else if (a.hasLocation()) {
                        Location loc = a.location();
                        World world = loc.getWorld();
                        if (world != null) {
                            world.dropItemNaturally(loc, clone);
                        }
                    }
                } else {
                    if (inv instanceof AnvilInventory || inv instanceof CraftingInventory || inv instanceof CartographyInventory || inv instanceof GrindstoneInventory) {
                        clone.setAmount(clone.getAmount() + 1);
                    } else if (inv instanceof BrewerInventory) {
                        if (a.hasExtra()) {
                            @SuppressWarnings("unchecked")
                            List<Boolean> potionBools = (List<Boolean>) a.extra();
                            if (potionBools.size() < 4) {
                                potionBools.add(true);
                            }
                        } else {
                            List<Boolean> potionBools = new ArrayList<>();
                            potionBools.add(true);
                            a.setExtra(potionBools);
                        }
                    }

                    inv.setItem(index, clone);
                }
            }
        }
    }

    @Override
    public void onCrafted(final Args a) {
        if (!a.hasInventory()) {
            a.addCustomReason("Needs an inventory!");
            return;
        }

        if (a.inventory() instanceof CraftingInventory) {
            CraftingInventory inv = (CraftingInventory) a.inventory();

            for (int i = 1; i < inv.getSize(); i++) {
                parse(inv, a, i);
            }
        } else if (a.inventory() instanceof BrewerInventory) {
            parse(a.inventory(), a, 3);
        } else if (a.inventory() instanceof FurnaceInventory) {
            FurnaceInventory inv = (FurnaceInventory) a.inventory();

            if (a.recipe() instanceof RMBaseFurnaceRecipe) {
                parse(inv, a, 0);
                parse(inv, a, 1);
            } else if (a.recipe() instanceof FuelRecipe) {
                parse(inv, a, 1);
            } else {
                a.addCustomReason("Needs a recipe!");
            }
        } else if (a.inventory() instanceof AnvilInventory || a.inventory() instanceof GrindstoneInventory || a.inventory() instanceof CartographyInventory) {
            parse(a.inventory(), a, 0);
            parse(a.inventory(), a, 1);
        } else {
            a.addCustomReason("Needs a crafting or furnace inventory!");
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "keepItems: ";
        for (Map.Entry<String, Object> entry : keepItems.entrySet()) {
            toHash += entry.getKey() + entry.getValue().hashCode();
        }

        return toHash.hashCode();
    }
}
