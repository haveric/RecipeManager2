package haveric.recipeManager.tools;

import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagDescriptor;
import haveric.recipeManager.flag.FlagFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class ToolsFlag {
    public static void parseItemMeta(ItemStack item, StringBuilder recipeString) {
        for (FlagDescriptor flagDescriptor : FlagFactory.getInstance().getFlags().values()) {
            Flag flag = flagDescriptor.getFlag();
            flag.parseItemMeta(item, item.getItemMeta(), recipeString);
        }

        recipeString.append(Files.NL);
    }

    public static void parsePotionEffectForItemMeta(StringBuilder recipeString, PotionEffect effect) {
        parsePotionEffect(recipeString, effect, " | ");
    }

    public static void parsePotionEffectForCondition(StringBuilder ingredientCondition, PotionEffect effect) {
        parsePotionEffect(ingredientCondition, effect, ", ");
    }

    private static void parsePotionEffect(StringBuilder builder, PotionEffect effect, String separator) {
        builder.append(effect.getType());

        int duration = effect.getDuration();
        if (duration != 20) {
            float durationInSeconds = (float) (duration / 20);
            builder.append(separator).append("duration ").append(durationInSeconds);
        }
        int amplifier = effect.getAmplifier();
        if (amplifier != 0) {
            builder.append(separator).append("amplifier ").append(amplifier);
        }
        if (!effect.isAmbient()) {
            builder.append(separator).append("!ambient");
        }
        if (!effect.hasParticles()) {
            builder.append(separator).append("!particles");
        }
        if (!effect.hasIcon()) {
            builder.append(separator).append("!icon");
        }
    }
}
