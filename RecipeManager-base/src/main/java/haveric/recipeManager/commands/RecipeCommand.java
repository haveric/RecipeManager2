package haveric.recipeManager.commands;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.SingleResultRecipe;
import haveric.recipeManager.recipes.WorkbenchRecipe;
import haveric.recipeManager.recipes.campfire.RMCampfireRecipe;
import haveric.recipeManager.recipes.combine.CombineRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import haveric.recipeManager.recipes.furnace.RMBaseFurnaceRecipe;
import haveric.recipeManager.recipes.stonecutting.RMStonecuttingRecipe;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.RMCVanilla;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import haveric.recipeManagerCommon.util.ParseBit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.Map.Entry;

public class RecipeCommand implements CommandExecutor {
    private static Map<String, Pages> pagination = new HashMap<>();

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

        public Pages(String newName, ItemStack newItem, List<String> newPages) {
            name = newName;
            item = newItem;
            pages = newPages.toArray(new String[newPages.size()]);
        }

        private void doTask() {
            if (task != null) {
                task.cancel();
            }

            task = new BukkitRunnable() {
                public void run() {
                    pagination.remove(name);
                }
            }.runTaskLater(RecipeManager.getPlugin(), 20 * 60);

        }

        public boolean hasNext() {
            return pages.length > (page + 1);
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
            return page > 0;
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

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String name = null;
            if (sender instanceof Player) {
                name = sender.getName();
            }

            boolean next = args[0].equalsIgnoreCase("next");

            if (next || args[0].equalsIgnoreCase("prev")) {
                Pages pages = pagination.get(name);

                if (pages == null) {
                    Messages.getInstance().send(sender, "cmd.recipes.needquery");
                } else {
                    if (next ? pages.hasNext() : pages.hasPrev()) {
                        String page = (next ? pages.next() : pages.prev());
                        Messages.getInstance().send(sender, "cmd.recipes.header", "{item}", ToolsItem.print(pages.item), "{num}", (pages.page + 1), "{total}", pages.pages.length);
                        MessageSender.getInstance().send(sender, page);

                        if (pages.hasNext()) {
                            Messages.getInstance().send(sender, "cmd.recipes.more", "{cmdnext}", "/" + label + " next", "{cmdprev}", "/" + label + " prev");
                        } else {
                            Messages.getInstance().send(sender, "cmd.recipes.end");
                        }
                    } else {
                        if (next) {
                            Messages.getInstance().send(sender, "cmd.recipes.nonext", "{command}", "/" + label + " prev");
                        } else {
                            Messages.getInstance().send(sender, "cmd.recipes.noprev", "{command}", "/" + label + " next");
                        }
                    }
                }
            } else {
                ItemStack item;

                if (args[0].equalsIgnoreCase("this")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (Version.has1_12Support()) {
                            item = player.getInventory().getItemInMainHand();
                        } else {
                            item = player.getItemInHand();
                        }

                        if (item == null) {
                            Messages.getInstance().send(player, "cmd.recipes.nohand");
                            return true;
                        }
                    } else {
                        sender.sendMessage("The 'this' argument can't be used from console.");
                        return true;
                    }
                } else {
                    item = Tools.parseItem(args[0], RMCVanilla.DATA_WILDCARD, ParseBit.NO_META | ParseBit.NO_PRINT);

                    if (item == null) {
                        Messages.getInstance().send(sender, "cmd.recipes.invaliditem", "{arg}", args[0]);
                        return true;
                    }
                }

                boolean ingredient = (args.length > 1 && args[1].charAt(0) == 'i');

                List<String> list = new ArrayList<>();

                for (Entry<BaseRecipe, RMCRecipeInfo> e : RecipeManager.getRecipes().getRecipeList().entrySet()) {
                    BaseRecipe recipe = e.getKey();

                    if (hasItem(recipe, item, ingredient)) {
                        list.addAll(recipe.printChat());
                    }
                }

                if (list.isEmpty()) {
                    Messages.getInstance().send(sender, "cmd.recipes.noresults", "{item}", ToolsItem.print(item));
                } else {
                    Pages pages = new Pages(name, item, list);
                    pagination.put(name, pages);

                    Messages.getInstance().send(sender, "cmd.recipes.header", "{item}", ToolsItem.print(pages.item), "{num}", 1, "{total}", pages.pages.length);
                    MessageSender.getInstance().send(sender, pages.next());

                    if (pages.hasNext()) {
                        Messages.getInstance().send(sender, "cmd.recipes.more", "{cmdnext}", "/" + label + " next", "{cmdprev}", "/" + label + " prev");
                    } else {
                        Messages.getInstance().send(sender, "cmd.recipes.end");
                    }
                }
            }
        } else {
            int mc = 0;
            int rm = 0;
            int other = 0;

            for (RMCRecipeInfo info : RecipeManager.getRecipes().getRecipeList().values()) {
                switch (info.getOwner()) {
                    case MINECRAFT:
                        mc++;
                        break;
                    case RECIPEMANAGER:
                        rm++;
                        break;
                    default:
                        other++;
                }
            }

            Messages.getInstance().send(sender, "cmd.recipes.usage", "{command}", label);
            Messages.getInstance().send(sender, "cmd.recipes.stats.mc", "{num}", mc);
            Messages.getInstance().send(sender, "cmd.recipes.stats.rm", "{num}", rm);
            Messages.getInstance().send(sender, "cmd.recipes.stats.other", "{num}", other);
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
            } else if (recipe instanceof RMBaseFurnaceRecipe) {
                RMBaseFurnaceRecipe r = (RMBaseFurnaceRecipe) recipe;

                return containsItem(Collections.singletonList(r.getIngredient()), item, true);
            } else if (recipe instanceof RMCampfireRecipe) {
                RMCampfireRecipe r = (RMCampfireRecipe) recipe;

                return containsItem(Collections.singletonList(r.getIngredient()), item, true);
            } else if (recipe instanceof RMStonecuttingRecipe) {
                RMStonecuttingRecipe r = (RMStonecuttingRecipe) recipe;

                return containsItem(Collections.singletonList(r.getIngredient()), item, true);
            } else if (recipe instanceof FuelRecipe) {
                FuelRecipe r = (FuelRecipe) recipe;

                return containsItem(Collections.singletonList(r.getIngredient()), item, true);
            }
        } else {
            if (recipe instanceof WorkbenchRecipe) {
                WorkbenchRecipe r = (WorkbenchRecipe) recipe;

                return containsItem(r.getResults(), item, false);
            } else if (recipe instanceof SingleResultRecipe) {
                SingleResultRecipe r = (SingleResultRecipe) recipe;

                return containsItem(Collections.singletonList(r.getResult()), item, false);
            }
        }

        return false;
    }

    private boolean containsItem(Collection<? extends ItemStack> items, ItemStack item, boolean ingredient) {
        for (ItemStack i : items) {
            if (i != null && i.getType() == item.getType() && (item.getDurability() == RMCVanilla.DATA_WILDCARD || i.getDurability() == item.getDurability()) && (ingredient || item.getAmount() == 1 || item.getAmount() == i.getAmount())) {
                return true;
            }
        }

        return false;
    }
}
