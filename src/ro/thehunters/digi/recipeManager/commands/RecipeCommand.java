package ro.thehunters.digi.recipeManager.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.Tools.ParseBit;
import ro.thehunters.digi.recipeManager.Vanilla;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.CombineRecipe;
import ro.thehunters.digi.recipeManager.recipes.CraftRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;
import ro.thehunters.digi.recipeManager.recipes.SingleResultRecipe;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;
import ro.thehunters.digi.recipeManager.recipes.WorkbenchRecipe;

public class RecipeCommand implements CommandExecutor {
    private static final Map<String, Pages> pagination = new HashMap<String, Pages>();

    public static void clean(String name) {
        pagination.remove(name);
    }

    public static void clean() {
        pagination.clear();
    }

    public class Pages {
        private String name;
        private ItemStack item;
        private int page = -1;
        private String[] pages;
        private BukkitTask task;

        public Pages(String name, ItemStack item, List<String> pages) {
            this.name = name;
            this.item = item;
            this.pages = pages.toArray(new String[0]);
        }

        private void doTask() {
            if (task != null) {
                task.cancel();
            }

            task = new BukkitRunnable() {
                @Override
                public void run() {
                    pagination.remove(name);
                }
            }.runTaskLater(RecipeManager.getPlugin(), 20 * 60);

        }

        public boolean hasNext() {
            return (pages.length > (page + 1));
        }

        public String next() {
            page++;

            if (page >= pages.length) {
                return null;
            }

            doTask();
            return pages[page];
        }

        public boolean hasPrev() {
            return (page > 0);
        }

        public String prev() {
            if (page <= 0) {
                return null;
            }

            page--;

            doTask();
            return pages[page];
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String name = (sender instanceof Player ? sender.getName() : null);

            boolean next = args[0].equalsIgnoreCase("next");

            if (next || args[0].equalsIgnoreCase("prev")) {
                Pages pages = pagination.get(name);

                if (pages != null) {
                    if (next ? pages.hasNext() : pages.hasPrev()) {
                        String page = (next ? pages.next() : pages.prev());
                        Messages.CMD_RECIPES_HEADER.print(sender, null, "{item}", Tools.Item.print(pages.item), "{num}", (pages.page + 1), "{total}", pages.pages.length);
                        Messages.send(sender, page);

                        if (pages.hasNext()) {
                            Messages.CMD_RECIPES_MORE.print(sender, null, "{cmdnext}", "/" + label + " next", "{cmdprev}", "/" + label + " prev");
                        } else {
                            Messages.CMD_RECIPES_END.print(sender);
                        }
                    } else {
                        if (next) {
                            Messages.CMD_RECIPES_NONEXT.print(sender, null, "{command}", "/" + label + " prev");
                        } else {
                            Messages.CMD_RECIPES_NOPREV.print(sender, null, "{command}", "/" + label + " next");
                        }
                    }
                } else {
                    Messages.CMD_RECIPES_NEEDQUERY.print(sender);
                }
            } else {
                ItemStack item;

                if (args[0].equalsIgnoreCase("this")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        item = player.getItemInHand();

                        if (item == null) {
                            Messages.CMD_RECIPES_NOHAND.print(sender);
                            return true;
                        }
                    } else {
                        sender.sendMessage("The 'this' argument can't be used from console.");
                        return true;
                    }
                } else {
                    item = Tools.parseItem(args[0], Vanilla.DATA_WILDCARD, ParseBit.NO_META | ParseBit.NO_PRINT);

                    if (item == null) {
                        Messages.CMD_RECIPES_INVALIDITEM.print(sender, null, "{arg}", args[0]);
                        return true;
                    }
                }

                boolean ingredient = (args.length > 1 && args[1].charAt(0) == 'i');

                List<String> list = new ArrayList<String>();

                for (Entry<BaseRecipe, RecipeInfo> e : RecipeManager.getRecipes().getRecipeList().entrySet()) {
                    BaseRecipe recipe = e.getKey();

                    if (hasItem(recipe, item, ingredient)) {
                        list.add(recipe.printChat());
                    }
                }

                if (!list.isEmpty()) {
                    Pages pages = new Pages(name, item, list);
                    pagination.put(name, pages);

                    Messages.CMD_RECIPES_HEADER.print(sender, null, "{item}", Tools.Item.print(pages.item), "{num}", 1, "{total}", pages.pages.length);
                    Messages.send(sender, pages.next());

                    if (pages.hasNext()) {
                        Messages.CMD_RECIPES_MORE.print(sender, null, "{cmdnext}", "/" + label + " next", "{cmdprev}", "/" + label + " prev");
                    } else {
                        Messages.CMD_RECIPES_END.print(sender);
                    }
                } else {
                    Messages.CMD_RECIPES_NORESULTS.print(sender, null, "{item}", Tools.Item.print(item));
                }
            }
        } else {
            int mc = 0;
            int rm = 0;
            int other = 0;

            for (RecipeInfo info : RecipeManager.getRecipes().getRecipeList().values()) {
                switch (info.getOwner()) {
                    case MINECRAFT: {
                        mc++;
                        break;
                    }

                    case RECIPEMANAGER: {
                        rm++;
                        break;
                    }

                    default: {
                        other++;
                    }
                }
            }

            Messages.CMD_RECIPES_USAGE.print(sender, null, "{command}", label);
            Messages.CMD_RECIPES_STATS_MC.print(sender, null, "{num}", mc);
            Messages.CMD_RECIPES_STATS_RM.print(sender, null, "{num}", rm);
            Messages.CMD_RECIPES_STATS_OTHER.print(sender, null, "{num}", other);
        }

        return true;
    }

    private boolean hasItem(BaseRecipe recipe, ItemStack item, boolean ingredient) {
        if (ingredient) {
            if (recipe instanceof CraftRecipe) {
                CraftRecipe r = (CraftRecipe) recipe;

                return containsItem(Arrays.asList(r.getIngredients()), item, true);
            } else if (recipe instanceof CombineRecipe) {
                CombineRecipe r = (CombineRecipe) recipe;

                return containsItem(r.getIngredients(), item, true);
            } else if (recipe instanceof SmeltRecipe) {
                SmeltRecipe r = (SmeltRecipe) recipe;

                return containsItem(Arrays.asList(r.getIngredient()), item, true);
            } else if (recipe instanceof FuelRecipe) {
                FuelRecipe r = (FuelRecipe) recipe;

                return containsItem(Arrays.asList(r.getIngredient()), item, true);
            }
        } else {
            if (recipe instanceof WorkbenchRecipe) {
                WorkbenchRecipe r = (WorkbenchRecipe) recipe;

                return containsItem(r.getResults(), item, false);
            } else if (recipe instanceof SingleResultRecipe) {
                SingleResultRecipe r = (SingleResultRecipe) recipe;

                return containsItem(Arrays.asList(r.getResult()), item, false);
            }
        }

        return false;
    }

    private boolean containsItem(Collection<? extends ItemStack> items, ItemStack item, boolean ingredient) {
        for (ItemStack i : items) {
            if (i != null && i.getTypeId() == item.getTypeId() && (item.getDurability() == Vanilla.DATA_WILDCARD || i.getDurability() == item.getDurability()) && (ingredient || item.getAmount() == 1 || item.getAmount() == i.getAmount())) {
                return true;
            }
        }

        return false;
    }
}
