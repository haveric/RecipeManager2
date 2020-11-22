package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import haveric.recipeManager.tools.Version;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FlagEnchantedBook extends Flag {

    @Override
    public String getFlagType() {
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
            "You must specify an enchantment name or id, see " + Files.getNameIndexHashLink("enchantment"),
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


    private Map<Enchantment, Integer> enchants = new HashMap<>();
    private List<Enchantment> enchantsToRemove = new ArrayList<>();

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

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
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
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof EnchantmentStorageMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        if (Version.has1_13BasicSupport()) {
            FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();

            if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), EnchantmentStorageMeta.class)) {
                validFlaggable = true;
            }
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs an enchantable book!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.split(" ");
        value = split[0].trim();
        Enchantment enchant = Tools.parseEnchant(value);

        if (enchant == null) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid enchantment: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for enchantment names.");
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
                    return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid enchantment level number!");
                }
            }
        }

        addEnchant(enchant, level);

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        onCrafted(a);
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) a.result().getItemMeta();
            if (meta != null) {
                for (Entry<Enchantment, Integer> e : enchants.entrySet()) {
                    meta.addStoredEnchant(e.getKey(), e.getValue(), true);
                }

                for (Enchantment e : enchantsToRemove) {
                    meta.removeStoredEnchant(e);
                }

                a.result().setItemMeta(meta);
            }
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
