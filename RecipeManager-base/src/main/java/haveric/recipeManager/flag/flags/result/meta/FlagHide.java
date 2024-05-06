package haveric.recipeManager.flag.flags.result.meta;

import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.Version;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

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
            "  attributes         = Hide attributes like Damage",
            "  destroys           = Hide what the item can break/destroy",
            "  enchants           = Hide enchants",
            "  placedon           = Hide where this item can be placed on",
            "  additionaltooltip  = Hide potion effects, book and firework information, map tooltips, patterns of banners, and enchantments of enchanted books",
            "  unbreakable        = Hide the unbreakable state",
        };

        if (Supports.itemFlagHideDye()) {
            description = ObjectArrays.concat(description, new String[] {
                "  dye            = Hides dyes from colored leather armor",
            }, String.class);
        }

        if (Supports.itemFlagHideArmorTrim()) {
            description = ObjectArrays.concat(description, new String[] {
                "  armortrim      = Hides armor trim from leather armor",
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
    private boolean additionaltooltip = false;
    private boolean unbreakable = false;
    private boolean dye = false;
    private boolean armortrim = false;

    public FlagHide() { }

    public FlagHide(FlagHide flag) {
        super(flag);
        attributes = flag.attributes;
        destroys = flag.destroys;
        enchants = flag.enchants;
        placedon = flag.placedon;
        additionaltooltip = flag.additionaltooltip;
        unbreakable = flag.unbreakable;
        dye = flag.dye;
        armortrim = flag.armortrim;
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
                additionaltooltip = true;

                ErrorReporter.getInstance().warning("Flag " + getFlagType() + "'s argument of `potioneffects` was renamed to `additionaltooltip`. It will continue to work for now, but you should rename it as soon as you can.");
            } else if (arg.startsWith("additionaltooltip")) {
                additionaltooltip = true;
            } else if (arg.startsWith("unbreakable")) {
                unbreakable = true;
            } else if (arg.startsWith("dye")) {
                dye = true;
            } else if (arg.startsWith("armortrim")) {
                armortrim = true;
            } else if (arg.startsWith("all")) {
                attributes = true;
                destroys = true;
                enchants = true;
                placedon = true;
                additionaltooltip = true;
                unbreakable = true;
                dye = true;
                armortrim = true;
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

                if (additionaltooltip) {
                    if (Version.has1_20_5Support()) {
                        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                    } else {
                        meta.addItemFlags(ItemFlag.valueOf("HIDE_POTION_EFFECTS"));
                    }
                }

                if (unbreakable) {
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                }

                if (dye && Supports.itemFlagHideDye()) {
                    meta.addItemFlags(ItemFlag.HIDE_DYE);
                }

                if (armortrim && Supports.itemFlagHideArmorTrim()) {
                    meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
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
        toHash += "additionaltooltip: " + additionaltooltip;
        toHash += "unbreakable: " + unbreakable;
        toHash += "dye: " + dye;
        toHash += "armortrim: " + armortrim;

        return toHash.hashCode();
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        if (meta != null) {
            Set<ItemFlag> itemFlags = meta.getItemFlags();
            if (!itemFlags.isEmpty()) {
                recipeString.append(Files.NL).append("@hide ");

                boolean first = true;
                for (ItemFlag itemFlag : itemFlags) {
                    if (!first) {
                        recipeString.append(" | ");
                    }
                    recipeString.append(itemFlag.toString().toLowerCase());

                    first = false;
                }
            }
        }
    }
}
