package haveric.recipeManager.commands.recipe;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.MultiChoiceResultRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.SingleResultRecipe;
import haveric.recipeManager.recipes.anvil.AnvilRecipe;
import haveric.recipeManager.recipes.brew.BrewRecipe;
import haveric.recipeManager.recipes.cartography.CartographyRecipe;
import haveric.recipeManager.recipes.combine.CombineRecipe;
import haveric.recipeManager.recipes.compost.CompostRecipe;
import haveric.recipeManager.recipes.cooking.campfire.RMCampfireRecipe;
import haveric.recipeManager.recipes.cooking.furnace.RMBaseFurnaceRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import haveric.recipeManager.recipes.grindstone.GrindstoneRecipe;
import haveric.recipeManager.recipes.smithing.RMSmithing1_19_4TransformRecipe;
import haveric.recipeManager.recipes.smithing.RMSmithingRecipe;
import haveric.recipeManager.recipes.stonecutting.RMStonecuttingRecipe;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.*;
import java.util.Map.Entry;

public class RecipeCommand implements TabExecutor {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            if (sender instanceof Player) {
                list.add("this");
            }

            String input = args[0];
            if (!input.contains(":")) {
                String inputMaterial = RMCUtil.parseAliasName(input);
                for (Material mat : Material.values()) {
                    String matName = RMCUtil.parseAliasName(mat.name());

                    if (matName.contains(inputMaterial)) {
                        list.add(mat.name().toLowerCase());
                    }
                }
            }
        }

        return list;
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            UUID playerUUID = null;
            if (sender instanceof Player player) {
                playerUUID = player.getUniqueId();
            }

            ItemStack item;

            if (args[0].equalsIgnoreCase("this")) {
                if (sender instanceof Player player) {
                    item = player.getInventory().getItemInMainHand();

                    if (item == null || item.getType() == Material.AIR) {
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

            boolean ingredient = true;
            boolean result = true;

            if (args.length > 1) {
                if (args[1].charAt(0) == 'i') {
                    result = false;
                } else if (args[1].charAt(0) == 'r') {
                    ingredient = false;
                }
            }

            List<String> list = new ArrayList<>();

            for (Entry<BaseRecipe, RMCRecipeInfo> e : RecipeManager.getRecipes().getRecipeList().entrySet()) {
                BaseRecipe recipe = e.getKey();

                if ((ingredient && hasItem(recipe, item, true)) || (result && hasItem(recipe, item, false))) {
                    list.addAll(recipe.printChat());
                }
            }

            if (list.isEmpty()) {
                Messages.getInstance().send(sender, "cmd.recipes.noresults", "{item}", ToolsItem.print(item));
            } else {
                Pages pages = new Pages(playerUUID, item, list);
                RecipePagination.put(playerUUID, pages);

                Messages.getInstance().send(sender, "cmd.recipes.header", "{item}", ToolsItem.print(pages.getItem()), "{num}", 1, "{total}", pages.getNumPages());
                MessageSender.getInstance().send(sender, pages.next());

                if (pages.hasNext()) {
                    Messages.getInstance().send(sender, "cmd.recipes.more", "{cmdnext}", "/rmnext", "{cmdprev}", "/rmprev");
                } else {
                    Messages.getInstance().send(sender, "cmd.recipes.end");
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

            Messages.getInstance().send(sender, "cmd.recipes.usage1", "{command}", label);
            Messages.getInstance().send(sender, "cmd.recipes.usage2", "{command}", label);
            Messages.getInstance().send(sender, "cmd.recipes.usage3", "{command}", label);
            Messages.getInstance().send(sender, "cmd.recipes.usage4", "{command}", label);
            Messages.getInstance().send(sender, "cmd.recipes.stats.mc", "{num}", mc);
            Messages.getInstance().send(sender, "cmd.recipes.stats.rm", "{num}", rm);
            Messages.getInstance().send(sender, "cmd.recipes.stats.other", "{num}", other);
        }

        return true;
    }

    private boolean hasItem(BaseRecipe recipe, ItemStack item, boolean ingredient) {
        if (ingredient) {
            if (recipe instanceof AnvilRecipe anvilRecipe) {
                return containsRecipeChoice(anvilRecipe.getPrimaryIngredientChoice(), item) || containsRecipeChoice(anvilRecipe.getSecondaryIngredientChoice(), item);
            } else if (recipe instanceof BrewRecipe brewRecipe) {
                return containsRecipeChoice(brewRecipe.getPrimaryIngredientChoice(), item) || containsRecipeChoice(brewRecipe.getPotionIngredientChoice(), item);
            } else if (recipe instanceof CartographyRecipe cartographyRecipe) {
                return containsRecipeChoice(cartographyRecipe.getPrimaryIngredientChoice(), item) || containsRecipeChoice(cartographyRecipe.getSecondaryIngredientChoice(), item);
            } else if (recipe instanceof CraftRecipe) {
                return containsRecipeChoiceMap(((CraftRecipe) recipe).getIngredients(), item);
            } else if (recipe instanceof CombineRecipe) {
                return containsRecipeChoiceCollection(((CombineRecipe) recipe).getIngredientChoiceList(), item);
            } else if (recipe instanceof CompostRecipe) {
                return containsRecipeChoice(((CompostRecipe) recipe).getIngredientChoice(), item);
            } else if (recipe instanceof FuelRecipe) {
                return containsRecipeChoice(((FuelRecipe) recipe).getIngredientChoice(), item.getType());
            } else if (recipe instanceof GrindstoneRecipe grindstoneRecipe) {
                return containsRecipeChoice(grindstoneRecipe.getPrimaryIngredientChoice(), item) || containsRecipeChoice(grindstoneRecipe.getSecondaryIngredientChoice(), item);
            } else if (recipe instanceof RMBaseFurnaceRecipe) {
                return containsRecipeChoice(((RMBaseFurnaceRecipe) recipe).getIngredientChoice(), item.getType());
            } else if (recipe instanceof RMCampfireRecipe) {
                return containsRecipeChoice(((RMCampfireRecipe) recipe).getIngredientChoice(), item.getType());
            } else if (recipe instanceof RMSmithing1_19_4TransformRecipe rmSmithing1_19_4TransformRecipe) {
                return containsRecipeChoice(rmSmithing1_19_4TransformRecipe.getTemplateIngredientChoice(), item) || containsRecipeChoice(rmSmithing1_19_4TransformRecipe.getPrimaryIngredientChoice(), item) || containsRecipeChoice(rmSmithing1_19_4TransformRecipe.getSecondaryIngredientChoice(), item);
            } else if (recipe instanceof RMSmithingRecipe rmSmithingRecipe) {
                return containsRecipeChoice(rmSmithingRecipe.getPrimaryIngredientChoice(), item) || containsRecipeChoice(rmSmithingRecipe.getSecondaryIngredientChoice(), item);
            } else if (recipe instanceof RMStonecuttingRecipe) {
                return containsRecipeChoice(((RMStonecuttingRecipe) recipe).getIngredientChoice(), item.getType());
            }
        } else {
            if (recipe instanceof MultiChoiceResultRecipe r) {
                return containsResult(r.getResults(), item, false);
            } else if (recipe instanceof SingleResultRecipe r) {
                return containsItem(Collections.singletonList(r.getResult().getItemStack()), item, false);
            }
        }

        return false;
    }

    private boolean containsRecipeChoiceMap(Map<Character, RecipeChoice> choiceMap, ItemStack item) {
        return containsRecipeChoiceCollection(choiceMap.values(), item);
    }

    private boolean containsRecipeChoiceCollection(Collection<RecipeChoice> choiceList, ItemStack item) {
        boolean contains = false;
        for (RecipeChoice choice : choiceList) {
            contains = containsRecipeChoice(choice, item);
            if (contains) {
                break;
            }
        }

        return contains;
    }

    private boolean containsRecipeChoice(RecipeChoice choice, ItemStack item) {
        if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return containsMaterial(materialChoice.getChoices(), item.getType());
        } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return containsItem(exactChoice.getChoices(), item, true);
        }

        return false;
    }

    private boolean containsRecipeChoice(RecipeChoice choice, Material material) {
        if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return containsMaterial(materialChoice.getChoices(), material);
        } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return containsMaterialFromItems(exactChoice.getChoices(), material);
        }

        return false;
    }

    private boolean containsMaterialFromItems(List<ItemStack> items, Material materialToMatch) {
        for (ItemStack item : items) {
            if (materialToMatch == item.getType()) {
                return true;
            }
        }

        return false;
    }

    private boolean containsMaterial(List<Material> materials, Material materialToMatch) {
        for (Material material : materials) {
            if (materialToMatch == material) {
                return true;
            }
        }

        return false;
    }

    private boolean containsResult(Collection<ItemResult> items, ItemStack item, boolean ingredient) {
        for (ItemResult result : items) {
            ItemStack i = result.getItemStack();
            if (i != null && i.getType() == item.getType() && (item.getDurability() == RMCVanilla.DATA_WILDCARD || i.getDurability() == item.getDurability()) && (ingredient || item.getAmount() == 1 || item.getAmount() == i.getAmount())) {
                return true;
            }
        }

        return false;
    }

    private boolean containsItem(Collection<ItemStack> items, ItemStack item, boolean ingredient) {
        for (ItemStack i : items) {
            if (i != null && i.getType() == item.getType() && (item.getDurability() == RMCVanilla.DATA_WILDCARD || i.getDurability() == item.getDurability()) && (ingredient || item.getAmount() == 1 || item.getAmount() == i.getAmount())) {
                return true;
            }
        }

        return false;
    }
}
