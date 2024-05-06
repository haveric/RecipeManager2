package haveric.recipeManager.tools;

import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagDescriptor;
import haveric.recipeManager.flag.FlagFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class ToolsFlag {
    public static void parseItemMeta(ItemStack item, StringBuilder recipeString) {
        ItemMeta meta = item.getItemMeta();
        for (FlagDescriptor flagDescriptor : FlagFactory.getInstance().getFlags().values()) {
            Flag flag = flagDescriptor.getFlag();
            flag.parseItemMeta(item, meta, recipeString);
        }

        recipeString.append(Files.NL);
    }

    public static void parsePotionEffectForItemMeta(StringBuilder recipeString, PotionEffect effect) {
        recipeString.append(effect.getType());

        int duration = effect.getDuration();
        if (duration != 20) {
            float durationInSeconds = (float) (duration / 20);
            recipeString.append(" | duration ").append(durationInSeconds);
        }
        int amplifier = effect.getAmplifier();
        if (amplifier != 0) {
            recipeString.append(" | amplifier ").append(amplifier);
        }
        if (!effect.isAmbient()) {
            recipeString.append(" | !ambient");
        }
        if (!effect.hasParticles()) {
            recipeString.append(" | !particles");
        }
        if (!effect.hasIcon()) {
            recipeString.append(" | !icon");
        }
    }
}
