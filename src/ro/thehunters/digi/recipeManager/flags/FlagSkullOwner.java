package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.SkullMeta;

import ro.thehunters.digi.recipeManager.ErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagSkullOwner extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.SKULLOWNER;

        A = new String[] { "{flag} <name>", };

        D = new String[] { "Sets the human skull's owner to apply the skin.", "If you set it to '{player}' then it will use crafter's name.", };

        E = new String[] { "{flag} Notch", "{flag} {player}", };
    }

    // Flag code

    private String owner;

    public FlagSkullOwner() {
    }

    public FlagSkullOwner(FlagSkullOwner flag) {
        owner = flag.owner;
    }

    @Override
    public FlagSkullOwner clone() {
        return new FlagSkullOwner(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    protected boolean onValidate() {
        ItemResult result = getResult();

        if (result == null || result.getItemMeta() instanceof SkullMeta == false || result.getDurability() != 3) {
            return ErrorReporter.error("Flag " + getType() + " needs a SKULL_ITEM with data value 3 to work!");
        }

        return true;
    }

    @Override
    protected boolean onParse(String value) {
        setOwner(value);
        return true;
    }

    @Override
    protected void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        SkullMeta meta = (SkullMeta) a.result().getItemMeta();

        if (getOwner().equalsIgnoreCase("{player}")) {
            if (!a.hasPlayerName()) {
                a.addCustomReason("Needs player name!");
                return;
            }

            meta.setOwner(a.playerName());
        } else {
            meta.setOwner(getOwner());
        }

        a.result().setItemMeta(meta);
    }
}
