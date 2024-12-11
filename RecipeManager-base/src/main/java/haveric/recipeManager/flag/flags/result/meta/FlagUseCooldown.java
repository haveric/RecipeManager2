package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.UseCooldownComponent;

public class FlagUseCooldown extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.USE_COOLDOWN;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <namespacedKey> [seconds]"
        };
    }

    @Override
    protected String[] getDescription() {
        return new String[]{
            "Adds a cooldown to the use of an item, typically used in conjunction with " + FlagType.FOOD,
            "Using this flag more than once will overwrite the previous flag.",
            "",
            "  The <namespacedKey> argument sets the cooldown group to be used for similar items.",
            "    Expected format is `namespace:key` where namespace is the plugin name or `minecraft` as default",
            "",
            "  The optional [seconds] argument sets the time of the cooldown group in seconds (as a float).",
        };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} recipemanager:cooked_fish 3 // sets the `cooked_fish` group cooldown to 3 seconds.",
            "{flag} recipemanager:cooked_fish // sets the cooldown group to `cooked_fish`.",
            "{flag} fast 0.5 // sets the `minecraft:fast` cooldown group to 0.5 seconds.",
        };
    }

    private String namespacedKey = "";
    private float seconds = 0.0f;

    public FlagUseCooldown() {

    }

    public FlagUseCooldown(FlagUseCooldown flag) {
        super(flag);
        seconds = flag.seconds;
    }

    @Override
    public FlagUseCooldown clone() {
        return new FlagUseCooldown((FlagUseCooldown) super.clone());
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.toLowerCase().split(" ");

        namespacedKey = split[0].trim();

        if (split.length > 1) {
            value = split[1].trim();
            try {
                seconds = Float.parseFloat(value);
            } catch (NumberFormatException e) {
                return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid seconds value: " + value);
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
                UseCooldownComponent useCooldown = meta.getUseCooldown();

                useCooldown.setCooldownGroup(NamespacedKey.fromString(namespacedKey));

                if (seconds > 0.0f) {
                    useCooldown.setCooldownSeconds(seconds);
                }
            }

            a.result().setItemMeta(meta);
        }
    }

}
