package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
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
        return new String[] {
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
            "  all            = Hides everything",
            "Arguments can be listed in any order.", };
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

    public FlagHide() { }

    public FlagHide(FlagHide flag) {
        attributes = flag.attributes;
        destroys = flag.destroys;
        enchants = flag.enchants;
        placedon = flag.placedon;
        potioneffects = flag.potioneffects;
        unbreakable = flag.unbreakable;
    }

    @Override
    public FlagHide clone() {
        return new FlagHide((FlagHide) super.clone());
    }

    @Override
    public boolean onParse(String value) {
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
            } else if (arg.startsWith("all")) {
                attributes = true;
                destroys = true;
                enchants = true;
                placedon = true;
                potioneffects = true;
                unbreakable = true;
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

        return toHash.hashCode();
    }
}
