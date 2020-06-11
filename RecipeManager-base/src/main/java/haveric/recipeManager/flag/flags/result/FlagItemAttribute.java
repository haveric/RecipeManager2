package haveric.recipeManager.flag.flags.result;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.common.util.RMCUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
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
                "Adds an attribute, such as max health to the result",
                "",
                "<attribute> The attribute you want to modify.",
                "  Values: all, or see: " + Files.getNameIndexHashLink("itemattribute"),
                "    Values starting with 'GENERIC_' such as " + Attribute.GENERIC_MAX_HEALTH + " can be used without 'GENERIC_' for ease of use. max_health would be valid in this case.",
                "",
                "[modifier] can be x for multiplication otherwise it will use addition",
                "<num> is the amount you want the attribute modified by. If you're not using multiplication as a modifier, the value with be added/subtracted.",
                "  Note that with multiplication, 1 = 100%. Values are expected in decimal/double format: '1.5'.",
                "",
                "The <slot> argument is the slot you want the attribute to affect. Default is " + EquipmentSlot.HAND + ".",
                "  Values: all, " + RMCUtil.collectionToString(Arrays.asList(EquipmentSlot.values())).toLowerCase(),
                "    'all' will let the attribute affect all of the equipment slots",
                "    offhand can be used instead of off_hand as well.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
                "{flag} max_health 5 // Adds 2.5 hearts of health, defaults to HAND slot",
                "{flag} movement_speed -.1 | slot feet // Reduce speed by .1 when worn in the feet/boots slot",
                "{flag} armor x.2 | slot offhand // Adds +20% Armor", };
    }


    private Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

    public FlagItemAttribute() {
    }

    public FlagItemAttribute(FlagItemAttribute flag) {
        attributes.putAll(flag.attributes);
    }

    @Override
    public FlagItemAttribute clone() {
        return new FlagItemAttribute((FlagItemAttribute) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    public Multimap<Attribute, AttributeModifier> getAttributes() {
        return attributes;
    }

    public void setAttributes(Multimap<Attribute, AttributeModifier> newAttributes) {
        attributes.clear();
        attributes.putAll(newAttributes);
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
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
        onCrafted(a);
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();
            if (meta != null) {
                for (Map.Entry<Attribute, AttributeModifier> entry : attributes.entries()) {
                    meta.addAttributeModifier(entry.getKey(), entry.getValue());
                }

                a.result().setItemMeta(meta);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        for (Map.Entry<Attribute, AttributeModifier> entry : attributes.entries()) {
            toHash += entry.getKey().toString() + "-" + entry.getValue().hashCode();
        }

        return toHash.hashCode();
    }
}
