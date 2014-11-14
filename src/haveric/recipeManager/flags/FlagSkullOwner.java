package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.recipes.ItemResult;

import org.bukkit.inventory.meta.SkullMeta;


public class FlagSkullOwner extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE = FlagType.SKULLOWNER;
    protected static final String[] A = new String[] {
        "{flag} <name>", };

    protected static final String[] D = new String[] {
        "Sets the human skull's owner to apply the skin.",
        "If you set it to '{player}' then it will use crafter's name.", };

    protected static final String[] E = new String[] {
        "{flag} Notch",
        "{flag} {player}", };

    // Flag code

    private String owner;

    public FlagSkullOwner() {
    }

    public FlagSkullOwner(FlagSkullOwner flag) {
        owner = flag.owner;
    }

    @Override
    public FlagSkullOwner clone() {
        super.clone();
        return new FlagSkullOwner(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String newOwner) {
        owner = newOwner;
    }

    @Override
    protected boolean onValidate() {
        ItemResult result = getResult();

        if (result == null || !(result.getItemMeta() instanceof SkullMeta) || result.getDurability() != 3) {
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
