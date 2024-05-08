package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class FlagLeatherColor extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.LEATHER_COLOR;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
                "{flag} <red> <green> <blue>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
                "Changes result's leather armor color, colors must be 3 numbers ranged from 0 to 255, the red, green and blue channels.",
                "",
                "Specific items: leather armor.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
                "{flag} 255 100 50", };
    }


    private Color color;

    public FlagLeatherColor() {
    }

    public FlagLeatherColor(FlagLeatherColor flag) {
        super(flag);
        color = flag.color;
    }

    @Override
    public FlagLeatherColor clone() {
        return new FlagLeatherColor((FlagLeatherColor) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof LeatherArmorMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), LeatherArmorMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a leather armor item!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        color = Tools.parseColor(value);

        if (color == null) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid color numbers!", "Use 3 numbers ranging from 0 to 255, e.g. 255 128 0 for orange.");
        }

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        onCrafted(a);
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();

            if (!(meta instanceof LeatherArmorMeta leather)) {
                a.addCustomReason("Needs leather armor!");
                return;
            }

            leather.setColor(color);

            a.result().setItemMeta(leather);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "color: " + color.hashCode();

        return toHash.hashCode();
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        parse(meta, recipeString, Files.NL + "@leathercolor ");
    }

    @Override
    public void parseIngredientForConditions(ItemStack item, ItemMeta meta, StringBuilder ingredientCondition) {
        parse(meta, ingredientCondition, " | color ");
    }

    private void parse(ItemMeta meta, StringBuilder builder, String prefix) {
        if (meta instanceof LeatherArmorMeta leatherMeta) {
            Color color = leatherMeta.getColor();

            if (!color.equals(Bukkit.getItemFactory().getDefaultLeatherColor())) {
                builder.append(prefix).append(color.getRed()).append(' ').append(color.getGreen()).append(' ').append(color.getBlue());
            }
        }
    }
}
