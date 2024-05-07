package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FlagEnchantItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.ENCHANT_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <enchantment> [level]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Enchants the result with the specified enchantment at specified level.",
            "You must specify an enchantment name, see " + Files.getNameIndexHashLink("enchantment"),
            "Optionally you can set the level of enchantment",
            "  Default is the enchantment's start level",
            "  You can use 'max' to set it to enchantment's max level.",
            "  You can use 'remove' to remove the enchantment (from a cloned ingredient)",
            "",
            "You can set a random level using the {rand} format:",
            "  {rand #1-#2}     = output a random integer between #1 and #2. Example: {rand 2-3} will output an integer from 2-3",
            "  {rand n}         = reuse a random output, where n is the nth {rand} in a recipe used excluding this format",
            "    If using a saved random output that includes decimals, it will be rounded to the nearest integer.",
            "",
            "Enchantments are forced and there is no level cap!",
            "This flag may be used more times to add more enchantments to the item.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} OXYGEN // enchant with oxygen at level 1",
            "{flag} DIG_SPEED max // enchant with dig speed at max valid level",
            "{flag} ARROW_INFINITE 127 // enchant with arrow infinite forced at level 127",
            "{flag} SHARPNESS remove // removes a sharpness enchant",
            "{flag} SHARPNESS {rand 3-5} // Gives a random level of 3, 4, or 5", };
    }


    private Map<Enchantment, Integer> enchants = new HashMap<>();
    private Map<Enchantment, String> randomEnchants = new HashMap<>();

    private List<Enchantment> enchantsToRemove = new ArrayList<>();

    public FlagEnchantItem() {
    }

    public FlagEnchantItem(FlagEnchantItem flag) {
        super(flag);
        enchants.putAll(flag.enchants);
        randomEnchants.putAll(flag.randomEnchants);
        enchantsToRemove.addAll(flag.enchantsToRemove);
    }

    public Map<Enchantment, Integer> getEnchants() {
        return enchants;
    }

    public List<Enchantment> getEnchantsToRemove() {
        return enchantsToRemove;
    }

    @Override
    public FlagEnchantItem clone() {
        return new FlagEnchantItem((FlagEnchantItem) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return !randomEnchants.isEmpty();
    }

    @Override
    public void onRemove() {
        getResult().getEnchantments().clear();
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.split(" ", 2);
        value = split[0].trim();

        Enchantment enchant = Tools.parseEnchant(value);

        if (enchant == null) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid enchantment: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for enchantment names.");
        }

        int level = enchant.getStartLevel();

        if (split.length > 1) {
            value = split[1].toLowerCase().trim();

            if (value.startsWith("{rand")) {
                randomEnchants.put(enchant, value);
            } else {
                switch (value) {
                    case "max":
                        level = enchant.getMaxLevel();
                        break;
                    case "remove":
                        enchantsToRemove.add(enchant);
                        break;
                    default:
                        try {
                            level = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid enchantment level number: " + value);
                        }
                        break;
                }

                enchants.put(enchant, level);
            }
        } else {
            enchants.put(enchant, level);
        }

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        craft(a, true);
    }

    @Override
    public void onCrafted(Args a) {
        craft(a, false);
    }

    private void craft(Args a, boolean prepare) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        for (Entry<Enchantment, Integer> e : enchants.entrySet()) {
            a.result().addUnsafeEnchantment(e.getKey(), e.getValue());
        }

        if (prepare) {
            List<String> lores = new ArrayList<>();
            for (Entry<Enchantment, String> e : randomEnchants.entrySet()) {
                a.result().addUnsafeEnchantment(e.getKey(), e.getKey().getStartLevel());
                lores.add(ToolsEnchantment.getPrintableName(e.getKey()) + ": " + a.parseRandomInt(e.getValue(), true));
            }

            addResultLores(a, lores);
        } else {
            for (Entry<Enchantment, String> e : randomEnchants.entrySet()) {
                double levelAsDouble = Double.parseDouble(a.parseRandomInt(e.getValue(), false));
                int level = (int) Math.round(levelAsDouble);
                a.result().addUnsafeEnchantment(e.getKey(), level);
            }
        }

        for (Enchantment e : enchantsToRemove) {
            a.result().removeEnchantment(e);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "Enchants: ";
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            toHash += entry.getKey().hashCode() + "-" + entry.getValue().toString();
        }

        toHash += "RandomEnchants: ";
        for (Map.Entry<Enchantment, String> entry : randomEnchants.entrySet()) {
            toHash += entry.getKey().hashCode() + "-" + entry.getValue();
        }

        toHash += "EnchantsToRemove: ";
        for (Enchantment enchantment : enchantsToRemove) {
            toHash += enchantment.hashCode();
        }

        return toHash.hashCode();
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        parse(item, recipeString, Files.NL + "@enchant ");
    }

    @Override
    public void parseIngredientForConditions(ItemStack item, ItemMeta meta, StringBuilder ingredientCondition) {
        parse(item, ingredientCondition, " | enchant ");
    }

    private void parse(ItemStack item, StringBuilder builder, String prefix) {
        if (!item.getEnchantments().isEmpty()) {
            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                builder.append(prefix).append(entry.getKey().getName()).append(' ').append(entry.getValue());
            }
        }
    }
}
