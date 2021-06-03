package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsEnchantment;
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
            "You can set a random level using the {rand} format:",
            "  {rand #1-#2}     = output a random integer between #1 and #2. Example: {rand 2-3} will output an integer from 2-3",
            "  {rand n}         = reuse a random output, where n is the nth {rand} in a recipe used excluding this format",
            "    If using a saved random output that includes decimals, it will be rounded to the nearest integer.",
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
            "{flag} sharpness remove",
            "{flag} sharpness {rand 3-5} // Gives a random level of 3, 4, or 5", };
    }


    private Map<Enchantment, Integer> enchants = new HashMap<>();
    private Map<Enchantment, String> randomEnchants = new HashMap<>();
    private List<Enchantment> enchantsToRemove = new ArrayList<>();

    public FlagEnchantedBook() {
    }

    public FlagEnchantedBook(FlagEnchantedBook flag) {
        super(flag);
        enchants.putAll(flag.enchants);
        randomEnchants.putAll(flag.randomEnchants);
        enchantsToRemove.addAll(flag.enchantsToRemove);
    }

    @Override
    public Flag clone() {
        return new FlagEnchantedBook((FlagEnchantedBook) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return !randomEnchants.isEmpty();
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

    public Map<Enchantment, String> getRandomEnchants() {
        return randomEnchants;
    }

    public void setRandomEnchants(Map<Enchantment, String> newEnchants) {
        if (newEnchants == null) {
            randomEnchants.clear();
        } else {
            randomEnchants = newEnchants;
        }
    }

    public void addRandomEnchant(Enchantment enchant, String random) {
        randomEnchants.put(enchant, random);
    }

    public List<Enchantment> getEnchantsToRemove() {
        return enchantsToRemove;
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
        String[] split = value.split(" ", 2);
        value = split[0].trim();
        Enchantment enchant = Tools.parseEnchant(value);

        if (enchant == null) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid enchantment: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for enchantment names.");
        }

        boolean random = false;
        int level = enchant.getStartLevel();

        if (split.length > 1) {
            value = split[1].trim();
            String valueLower = value.toLowerCase();

            if (valueLower.equals("max")) {
                level = enchant.getMaxLevel();
            } else if (valueLower.equals("remove")) {
                enchantsToRemove.add(enchant);
            } else if (valueLower.startsWith("{rand")) {
                addRandomEnchant(enchant, valueLower);
                random = true;
            } else {
                try {
                    level = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid enchantment level number!");
                }
            }
        }

        if (!random) {
            addEnchant(enchant, level);
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
        if (canAddMeta(a)) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) a.result().getItemMeta();
            if (meta != null) {
                for (Entry<Enchantment, Integer> e : enchants.entrySet()) {
                    meta.addStoredEnchant(e.getKey(), e.getValue(), true);
                }

                if (prepare) {
                    List<String> lores = new ArrayList<>();
                    for (Entry<Enchantment, String> e : randomEnchants.entrySet()) {
                        meta.addStoredEnchant(e.getKey(), e.getKey().getStartLevel(), true);
                        lores.add(ToolsEnchantment.getPrintableName(e.getKey()) + ": " + a.parseRandomInt(e.getValue(), true));
                    }

                    addResultLores(a, lores);
                } else {
                    for (Entry<Enchantment, String> e : randomEnchants.entrySet()) {
                        double levelAsDouble = Double.parseDouble(a.parseRandomInt(e.getValue(), false));
                        int level = (int) Math.round(levelAsDouble);
                        meta.addStoredEnchant(e.getKey(), level, true);
                    }
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
}
