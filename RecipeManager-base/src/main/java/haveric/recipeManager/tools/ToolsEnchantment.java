package haveric.recipeManager.tools;

import org.apache.commons.lang.WordUtils;
import org.bukkit.enchantments.Enchantment;

public class ToolsEnchantment {
    public static String getPrintableName(Enchantment enchantment) {
        String name = enchantment.getKey().getKey();
        name = name.replaceAll("_", " ");

        return WordUtils.capitalizeFully(name);
    }
}
