package haveric.recipeManager.flag.flags;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.UUID;

public class FlagItemAttribute extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.ITEM_ATTRIBUTE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
                "{flag} <attribute> [modifier]<num>",
                "{flag} <attribute> [modifier]<num> | slot <slot>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
                "",
                "",
                "", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
                "{flag} ",
                "{flag} ",
                "{flag} ", };
    }


    private Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

    public FlagItemAttribute() {
    }

    public FlagItemAttribute(FlagItemAttribute flag) {
        attributes = flag.attributes;
    }

    @Override
    public FlagItemAttribute clone() {
        return new FlagItemAttribute((FlagItemAttribute) super.clone());
    }

    public Multimap<Attribute, AttributeModifier> getAttributes() {
        return attributes;
    }

    public void setAttributes(Multimap<Attribute, AttributeModifier> newAttributes) {
        attributes = newAttributes;
    }

    @Override
    public boolean onParse(String value) {
        String[] split = value.split("\\|");

        String attributeFullString = split[0].trim();

        String[] attributeSplit = attributeFullString.split(" ", 2);
        String attributeString = attributeSplit[0].toUpperCase();

        Attribute attribute;
        try {
            attribute = Attribute.valueOf(attributeString);
        } catch (IllegalArgumentException e) {
            try {
                attribute = Attribute.valueOf("GENERIC_" + attributeString);
            } catch (IllegalArgumentException e2) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid attribute: " + attributeSplit[0]);
                return false;
            }
        }

        if (attributeSplit.length < 2) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " needs an attribute modifier for: " + split[0]);
            return false;
        }

        AttributeModifier.Operation operation;
        String modifierString = attributeSplit[1].trim().replaceAll(" ", "").toLowerCase();
        String modifierNum;
        if (modifierString.startsWith("x")) {
            operation = AttributeModifier.Operation.ADD_SCALAR;
            modifierNum = modifierString.substring(1);
        } else {
            operation = AttributeModifier.Operation.ADD_NUMBER;
            modifierNum = modifierString;
        }

        double modifier;
        try {
            modifier = Double.parseDouble(modifierNum);
        } catch (NumberFormatException e) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid attribute modifier: " + modifierNum + " in attribute: " + split[0]);
            return false;
        }

        EquipmentSlot slot = EquipmentSlot.HAND;

        if (split.length > 1) {
            String slotString = split[1].replace("slot", "").trim();
            String slotStringUpper = slotString.toUpperCase();
            if (slotStringUpper.equals("ALL")) {
                slot = null;
            } else {
                if (slotStringUpper.equals("OFFHAND")) {
                    slotStringUpper = "OFF_HAND";
                }

                try {
                    slot = EquipmentSlot.valueOf(slotStringUpper);
                } catch (IllegalArgumentException e) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid slot: " + slotString + ". Defaulting to HAND.");
                }
            }
        }

        UUID uuid = UUID.randomUUID();
        attributes.put(attribute, new AttributeModifier(uuid, attribute.name() + "-" + uuid.toString(), modifier, operation, slot));

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        ItemMeta meta = a.result().getItemMeta();

        for (Map.Entry<Attribute, AttributeModifier> entry : attributes.entries()) {
            meta.addAttributeModifier(entry.getKey(), entry.getValue());
        }

        a.result().setItemMeta(meta);
    }
}
