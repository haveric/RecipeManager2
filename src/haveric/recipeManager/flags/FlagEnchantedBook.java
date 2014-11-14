package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;


public class FlagEnchantedBook extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE = FlagType.ENCHANTEDBOOK;
    protected static final String[] A = new String[] {
        "{flag} <enchant> [level or max]", };

    protected static final String[] D = new String[] {
        "Adds stored enchantments in a enchanted book item.",
        "This flag may be used more times to add more enchantments to the item.",
        "",
        "You must specify an enchantment name or id, you can find all of them in '" + Files.FILE_INFO_NAMES + "' file.",
        "Optionally you can set the level of enchantment, default is the enchantment's start level or you can use 'max' to set it to enchantment's max level.",
        "",
        "Enchantments are forced and there is no level cap!",
        "",
        "Specific item: enchanted_book", };

    protected static final String[] E = new String[] {
        "{flag} efficiency // dig_speed alias",
        "{flag} damage_all max",
        "{flag} arrow_fire 127", };


    // Flag code

    private Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();

    public FlagEnchantedBook() {
    }

    public FlagEnchantedBook(FlagEnchantedBook flag) {
        enchants.putAll(flag.enchants);
    }

    @Override
    public Flag clone() {
        super.clone();
        return new FlagEnchantedBook(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public Map<Enchantment, Integer> getEnchants() {
        return enchants;
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
            ErrorReporter.error("Flag " + getType() + " needs an enchantable book!");
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
            ErrorReporter.error("Flag " + getType() + " has invalid enchantment: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for enchantment names.");
            return false;
        }

        int level = enchant.getStartLevel();

        if (split.length > 1) {
            value = split[1].trim();

            if (!value.equalsIgnoreCase("max")) {
                try {
                    level = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    ErrorReporter.error("Flag " + getType() + " has invalid enchantment level number!");
                    return false;
                }
            } else {
                level = enchant.getMaxLevel();
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

        a.result().setItemMeta(meta);
    }
}
