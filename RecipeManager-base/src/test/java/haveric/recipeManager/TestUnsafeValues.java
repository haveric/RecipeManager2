package haveric.recipeManager;

import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.UnsafeValues;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;

import static org.bukkit.Material.LEGACY_PREFIX;

public class TestUnsafeValues implements UnsafeValues {
    @Override
    public Material toLegacy(Material material) {
        Material legacyMaterial = material;
        if (!material.name().startsWith(LEGACY_PREFIX)) {
            legacyMaterial = Material.matchMaterial(LEGACY_PREFIX + material.name());
        }

        return legacyMaterial;
    }

    @Override
    public Material fromLegacy(Material material) {
        Material nonLegacyMaterial = material;
        if (material.name().startsWith(LEGACY_PREFIX)) {
            nonLegacyMaterial = Material.matchMaterial(material.name().replace(LEGACY_PREFIX, ""));
        }

        return nonLegacyMaterial;
    }

    @Override
    public Material fromLegacy(MaterialData materialData) {
        return fromLegacy(materialData.getItemType());
    }

    @Override
    public Material fromLegacy(MaterialData material, boolean itemPriority) {
        return fromLegacy(material);
    }

    @Override
    public BlockData fromLegacy(Material material, byte data) {
        return null;
    }

    public Material getMaterial(String material, int version) {
        return null;
    }

    @Override
    public int getDataVersion() {
        return 0;
    }

    @Override
    public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        return null;
    }

    @Override
    public void checkSupported(PluginDescriptionFile pdf) {

    }

    @Override
    public byte[] processClass(PluginDescriptionFile pdf, String path, byte[] clazz) {
        return new byte[0];
    }

    @Override
    public Advancement loadAdvancement(NamespacedKey key, String advancement) {
        return null;
    }

    @Override
    public boolean removeAdvancement(NamespacedKey key) {
        return false;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(Material material, EquipmentSlot slot) {
        return null;
    }
}
