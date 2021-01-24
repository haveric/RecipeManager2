package haveric.recipeManager.flag.flags.result;

import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Supports;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagHide extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.HIDE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <arguments>", };
    }

    @Override
    protected String[] getDescription() {
        String[] description = new String[]{
            "Configures hide attributes for items",
            "",
            "Replace '<arguments>' with the following arguments separated by | character.",
            "Arguments can be:",
            "  attributes     = Hide attributes like Damage",
            "  destroys       = Hide what the item can break/destroy",
            "  enchants       = Hide enchants",
            "  placedon       = Hide where this item can be placed on",
            "  potioneffects  = Hide potion effects on this item",
            "  unbreakable    = Hide the unbreakable state",
        };

        if (Supports.itemFlagHideDye()) {
            description = ObjectArrays.concat(description, new String[] {
                "  dye            = Hides dyes from colored leather armor",
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[] {
            "  all            = Hides everything",
            "Arguments can be listed in any order.",
        }, String.class);

        return description;
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} attributes // Removes Damage on a sword",
            "{flag} placedon | destroys // Removes placed on line and break/destroy lines", };
    }


    private boolean attributes = false;
    private boolean destroys = false;
    private boolean enchants = false;
    private boolean placedon = false;
    private boolean potioneffects = false;
    private boolean unbreakable = false;
    private boolean dye = false;

    public FlagHide() { }

    public FlagHide(FlagHide flag) {
        super(flag);
        attributes = flag.attributes;
        destroys = flag.destroys;
        enchants = flag.enchants;
        placedon = flag.placedon;
        potioneffects = flag.potioneffects;
        unbreakable = flag.unbreakable;
        dye = flag.dye;
    }

    @Override
    public FlagHide clone() {
        return new FlagHide((FlagHide) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] args = value.toLowerCase().split("\\|");

        if (args.length < 1) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs at least one argument", "Read '" + Files.FILE_INFO_FLAGS + "' for more info.");
        }

        for (String arg : args) {
            arg = arg.trim().toLowerCase();

            if (arg.startsWith("attributes")) {
                attributes = true;
            } else if (arg.startsWith("destroys")) {
                destroys = true;
            } else if (arg.startsWith("enchants")) {
                enchants = true;
            } else if (arg.startsWith("placedon")) {
                placedon = true;
            } else if (arg.startsWith("potioneffects")) {
                potioneffects = true;
            } else if (arg.startsWith("unbreakable")) {
                unbreakable = true;
            } else if (arg.startsWith("dye")) {
                dye = true;
            } else if (arg.startsWith("all")) {
                attributes = true;
                destroys = true;
                enchants = true;
                placedon = true;
                potioneffects = true;
                unbreakable = true;
                dye = true;
            }
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
            if (meta != null) {
                if (attributes) {
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                }

                if (destroys) {
                    meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                }

                if (enchants) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                if (placedon) {
                    meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
                }

                if (potioneffects) {
                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                }

                if (unbreakable) {
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                }

                if (dye && Supports.itemFlagHideDye()) {
                    meta.addItemFlags(ItemFlag.HIDE_DYE);
                }

                a.result().setItemMeta(meta);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "attributes: " + attributes;
        toHash += "destroys: " + destroys;
        toHash += "enchants: " + enchants;
        toHash += "placedon: " + placedon;
        toHash += "potioneffects: " + potioneffects;
        toHash += "unbreakable: " + unbreakable;
        toHash += "dye: " + dye;

        return toHash.hashCode();
    }
}
