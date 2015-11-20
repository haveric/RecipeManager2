package haveric.recipeManager.flags;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;

public class FlagHide extends Flag {

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


    boolean attributes = false;
    boolean destroys = false;
    boolean enchants = false;
    boolean placedon = false;
    boolean potioneffects = false;
    boolean unbreakable = false;

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
        super.clone();
        return new FlagHide(this);
    }

    @Override
    protected boolean onParse(String value) {
        String[] args = value.toLowerCase().split("\\|");

        if (args.length < 1) {
            return ErrorReporter.error("Flag " + getType() + " needs at least one argument", "Read '" + Files.FILE_INFO_FLAGS + "' for more info.");
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i].trim().toLowerCase();

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
    protected void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Need result!");
            return;
        }

        try {
            ItemMeta meta = a.result().getItemMeta();

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
        } catch (NoClassDefFoundError e) {
            // No 1.8 support
        }
    }
}
