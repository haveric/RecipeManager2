package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import org.bukkit.Color;
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

        if (result == null || !(result.getItemMeta() instanceof LeatherArmorMeta)) {
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

            if (!(meta instanceof LeatherArmorMeta)) {
                a.addCustomReason("Needs leather armor!");
                return;
            }

            LeatherArmorMeta leather = (LeatherArmorMeta) meta;
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
}
