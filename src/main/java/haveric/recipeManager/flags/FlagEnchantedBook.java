package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FlagEnchantedBook extends Flag {

    @Override
    protected String getFlagType() {
        return FlagType.ENCHANTED_BOOK;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <enchant> [level or max]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Adds stored enchantments in a enchanted book item.",
            "This flag may be used more times to add more enchantments to the item.",
            "",
            "You must specify an enchantment name or id, you can find all of them in '" + Files.FILE_INFO_NAMES + "' file.",
            "Optionally you can set the level of enchantment",
            "  Default is the enchantment's start level",
            "  You can use 'max' to set it to enchantment's max level.",
            "  You can use 'remove' to remove the enchantment (from a cloned ingredient)",
            "",
            "Enchantments are forced and there is no level cap!",
            "",
            "Specific item: enchanted_book", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} efficiency // dig_speed alias",
            "{flag} damage_all max",
            "{flag} arrow_fire 127",
            "{flag} sharpness remove", };
    }


    private Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
    private List<Enchantment> enchantsToRemove = new ArrayList<Enchantment>();

    public FlagEnchantedBook() {
    }

    public FlagEnchantedBook(FlagEnchantedBook flag) {
        enchants.putAll(flag.enchants);
        enchantsToRemove.addAll(flag.enchantsToRemove);
    }

    @Override
    public Flag clone() {
        return new FlagEnchantedBook((FlagEnchantedBook) super.clone());
    }

    public Map<Enchantment, Integer> getEnchants() {
        return enchants;
    }

    public List<Enchantment> getEnchantsToRemove() {
        return enchantsToRemove;
    }

    public void setEnchants(Map<Enchantment, Integer> newEnchants) {
        if (newEnchants == null) {
            enchants.clear();
        } else {
            enchants = newEnchants;
        }
    }

    public void addEnchant(Enchantment enchant, int level) {
        enchants.put(enchant, level);
    }

    @Override
    protected boolean onValidate() {
        ItemResult result = getResult();

        if (result == null || !(result.getItemMeta() instanceof EnchantmentStorageMeta)) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs an enchantable book!");
            return false;
        }

        return true;
    }

    @Override
    protected boolean onParse(String value) {
        String[] split = value.split(" ");
        value = split[0].trim();
        Enchantment enchant = Tools.parseEnchant(value);

        if (enchant == null) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid enchantment: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for enchantment names.");
            return false;
        }

        int level = enchant.getStartLevel();

        if (split.length > 1) {
            value = split[1].trim();

            if (value.equalsIgnoreCase("max")) {
                level = enchant.getMaxLevel();
            } else if (value.equalsIgnoreCase("remove")) {
                enchantsToRemove.add(enchant);
            } else {
                try {
                    level = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid enchantment level number!");
                    return false;
                }
            }
        }

        addEnchant(enchant, level);

        return true;
    }

    @Override
    protected void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) a.result().getItemMeta();

        for (Entry<Enchantment, Integer> e : enchants.entrySet()) {
            meta.addStoredEnchant(e.getKey(), e.getValue(), true);
        }

        for (Enchantment e : enchantsToRemove) {
            meta.removeStoredEnchant(e);
        }

        a.result().setItemMeta(meta);
    }
}
