package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Tools;
import org.bukkit.enchantments.Enchantment;

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
            "Enchantments are forced and there is no level cap!",
            "This flag may be used more times to add more enchantments to the item.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} OXYGEN // enchant with oxygen at level 1",
            "{flag} DIG_SPEED max // enchant with dig speed at max valid level",
            "{flag} ARROW_INFINITE 127 // enchant with arrow infinite forced at level 127",
            "{flag} SHARPNESS remove // removes a sharpness enchant", };
    }


    private Map<Enchantment, Integer> enchants = new HashMap<>();
    private List<Enchantment> enchantsToRemove = new ArrayList<>();

    public FlagEnchantItem() {
    }

    public FlagEnchantItem(FlagEnchantItem flag) {
        enchants.putAll(flag.enchants);
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
        return false;
    }

    @Override
    public void onRemove() {
        getResult().getEnchantments().clear();
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.split(" ");
        value = split[0].trim();

        Enchantment enchant = Tools.parseEnchant(value);

        if (enchant == null) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid enchantment: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for enchantment names.");
            return false;
        }

        int level = enchant.getStartLevel();

        if (split.length > 1) {
            value = split[1].toLowerCase().trim();

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
                        ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid enchantment level number: " + value);
                        return false;
                    }
                    break;
            }
        }

        enchants.put(enchant, level);

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        onCrafted(a);
    }

    @Override
    public void onCrafted(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        for (Entry<Enchantment, Integer> e : enchants.entrySet()) {
            a.result().addUnsafeEnchantment(e.getKey(), e.getValue());
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

        toHash += "EnchantsToRemove: ";
        for (Enchantment enchantment : enchantsToRemove) {
            toHash += enchantment.hashCode();
        }

        return toHash.hashCode();
    }
}
