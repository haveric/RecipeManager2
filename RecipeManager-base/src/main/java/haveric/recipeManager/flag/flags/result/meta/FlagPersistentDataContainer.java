package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FlagPersistentDataContainer extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.PERSISTENT_DATA_CONTAINER;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <arguments>",
        };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Set a persistent data container value",
            "",
            "Replace <arguments> with the following arguments separated by | character:",
            "  boolean <namespace:key> <value>      = Sets a boolean for namespace:key",
            "  byte <namespace:key> <value>         = Sets a byte for namespace:key",
            "  byte <namespace:key> <value, ...>    = Sets a byte array for namespace:key",
            "  string <namespace:key> <value>       = Sets a string for namespace:key",
            "  double <namespace:key> <value>       = Sets a double for namespace:key",
            "  float <namespace:key> <value>        = Sets a float for namespace:key",
            "  integer <namespace:key> <value>      = Sets an integer for namespace:key",
            "  integer <namespace:key> <value, ...> = Sets an integer array for namespace:key",
            "  long <namespace:key> <value>         = Sets a long for namespace:key",
            "  long <namespace:key> <value, ...>    = Sets a long array for namespace:key",
            "  short <namespace:key> <value>        = Sets a short for namespace:key",
        };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} boolean RecipeManager:deadly_swords true",
            "{flag} string RecipeManager:last_challenger haveric",
            "{flag} integer YourPlugin:points 7",
            "{flag} integer AnotherPlugin:fibonacci 0, 1, 1, 2, 3, 5, 8, 13",
        };
    }

    Map<NamespacedKey, Boolean> booleanPDCS = new HashMap<>();
    Map<NamespacedKey, Byte> bytePDCS = new HashMap<>();
    Map<NamespacedKey, byte[]> byteArrayPDCS = new HashMap<>();
    Map<NamespacedKey, String> stringPDCS = new HashMap<>();
    Map<NamespacedKey, Double> doublePDCS = new HashMap<>();
    Map<NamespacedKey, Float> floatPDCS = new HashMap<>();
    Map<NamespacedKey, Integer> integerPDCS = new HashMap<>();
    Map<NamespacedKey, int[]> integerArrayPDCS = new HashMap<>();
    Map<NamespacedKey, Long> longPDCS = new HashMap<>();
    Map<NamespacedKey, long[]> longArrayPDCS = new HashMap<>();
    Map<NamespacedKey, Short> shortPDCS = new HashMap<>();
    // PersistentDataType.TAG_CONTAINER
    // PersistentDataType.LIST

    public FlagPersistentDataContainer() {

    }

    public FlagPersistentDataContainer(FlagPersistentDataContainer flag) {
        super(flag);
        booleanPDCS.putAll(flag.booleanPDCS);
        bytePDCS.putAll(flag.bytePDCS);
        byteArrayPDCS.putAll(flag.byteArrayPDCS);
        stringPDCS.putAll(flag.stringPDCS);
        doublePDCS.putAll(flag.doublePDCS);
        floatPDCS.putAll(flag.floatPDCS);
        integerPDCS.putAll(flag.integerPDCS);
        integerArrayPDCS.putAll(flag.integerArrayPDCS);
        longPDCS.putAll(flag.longPDCS);
        longArrayPDCS.putAll(flag.longArrayPDCS);
        shortPDCS.putAll(flag.shortPDCS);
    }

    @Override
    public FlagPersistentDataContainer clone() {
        return new FlagPersistentDataContainer((FlagPersistentDataContainer) super.clone());
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] args = value.split("\\|");

        if (args.length < 1) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs at least one argument", "Read '" + Files.FILE_INFO_FLAGS + "' for more info.");
        }

        for (String arg : args) {
            arg = arg.trim();
            String argLower = arg.toLowerCase();
            String[] values = arg.split(" ", 2);

            if (values.length < 2) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid argument: " + arg, "Argument needs a value.");
                return false;
            }

            NamespacedKey key = NamespacedKey.fromString(values[1].trim());
            String keyValue = values[2].trim();

            if (argLower.startsWith("bool")) {
                booleanPDCS.put(key, Boolean.parseBoolean(keyValue));
            } else if (argLower.startsWith("byte")) {
                String[] bytesString = keyValue.split(",");
                if (bytesString.length > 1) {
                    byte[] byteArray = new byte[bytesString.length];
                    for (var i = 0; i < bytesString.length; i++) {
                        byteArray[i] = Byte.parseByte(bytesString[i]);
                    }

                    byteArrayPDCS.put(key, byteArray);
                } else {
                    bytePDCS.put(key, Byte.parseByte(keyValue));
                }
            } else if (argLower.startsWith("string")) {
                stringPDCS.put(key, keyValue);
            } else if (argLower.startsWith("double")) {
                doublePDCS.put(key, Double.parseDouble(keyValue));
            } else if (argLower.startsWith("float")) {
                floatPDCS.put(key, Float.parseFloat(keyValue));
            } else if (argLower.startsWith("integer")) {
                String[] integersString = keyValue.split(",");
                if (integersString.length > 1) {
                    int[] intArray = new int[integersString.length];
                    for (var i = 0; i < integersString.length; i++) {
                        intArray[i] = Integer.parseInt(integersString[i]);
                    }

                    integerArrayPDCS.put(key, intArray);
                } else {
                    integerPDCS.put(key, Integer.parseInt(keyValue));
                }
            } else if (argLower.startsWith("long")) {
                String[] longsString = keyValue.split(",");
                if (longsString.length > 1) {
                    long[] longArray = new long[longsString.length];
                    for (var i = 0; i < longsString.length; i++) {
                        longArray[i] = Long.parseLong(longsString[i]);
                    }

                    longArrayPDCS.put(key, longArray);
                } else {
                    longPDCS.put(key, Long.parseLong(keyValue));
                }
            } else if (argLower.startsWith("short")) {
                shortPDCS.put(key, Short.parseShort(keyValue));
            }
            // PersistentDataType.TAG_CONTAINER
            // PersistentDataType.LIST
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
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                for (Map.Entry<NamespacedKey, Boolean> entry : booleanPDCS.entrySet()) {
                    pdc.set(entry.getKey(), PersistentDataType.BOOLEAN, entry.getValue());
                }
                for (Map.Entry<NamespacedKey, Byte> entry : bytePDCS.entrySet()) {
                    pdc.set(entry.getKey(), PersistentDataType.BYTE, entry.getValue());
                }
                for (Map.Entry<NamespacedKey, byte[]> entry : byteArrayPDCS.entrySet()) {
                    pdc.set(entry.getKey(), PersistentDataType.BYTE_ARRAY, entry.getValue());
                }
                for (Map.Entry<NamespacedKey, String> entry : stringPDCS.entrySet()) {
                    pdc.set(entry.getKey(), PersistentDataType.STRING, entry.getValue());
                }
                for (Map.Entry<NamespacedKey, Double> entry : doublePDCS.entrySet()) {
                    pdc.set(entry.getKey(), PersistentDataType.DOUBLE, entry.getValue());
                }
                for (Map.Entry<NamespacedKey, Float> entry : floatPDCS.entrySet()) {
                    pdc.set(entry.getKey(), PersistentDataType.FLOAT, entry.getValue());
                }
                for (Map.Entry<NamespacedKey, Integer> entry : integerPDCS.entrySet()) {
                    pdc.set(entry.getKey(), PersistentDataType.INTEGER, entry.getValue());
                }
                for (Map.Entry<NamespacedKey, int[]> entry : integerArrayPDCS.entrySet()) {
                    pdc.set(entry.getKey(), PersistentDataType.INTEGER_ARRAY, entry.getValue());
                }
                for (Map.Entry<NamespacedKey, Long> entry : longPDCS.entrySet()) {
                    pdc.set(entry.getKey(), PersistentDataType.LONG, entry.getValue());
                }
                for (Map.Entry<NamespacedKey, long[]> entry : longArrayPDCS.entrySet()) {
                    pdc.set(entry.getKey(), PersistentDataType.LONG_ARRAY, entry.getValue());
                }
                for (Map.Entry<NamespacedKey, Short> entry : shortPDCS.entrySet()) {
                    pdc.set(entry.getKey(), PersistentDataType.SHORT, entry.getValue());
                }
                // PersistentDataType.TAG_CONTAINER
                // PersistentDataType.LIST
            }
        }
    }

    @Override
    public int hashCode() {
        StringBuilder toHash = new StringBuilder("" + super.hashCode());

        toHash.append("booleanPDCS: ");
        for (Map.Entry<NamespacedKey, Boolean> entry : booleanPDCS.entrySet()) {
            toHash.append(entry.getKey().hashCode()).append("-").append(entry.getValue().toString());
        }

        toHash.append("bytePDCS: ");
        for (Map.Entry<NamespacedKey, Byte> entry : bytePDCS.entrySet()) {
            toHash.append(entry.getKey().hashCode()).append("-").append(entry.getValue().toString());
        }

        toHash.append("byteArrayPDCS: ");
        for (Map.Entry<NamespacedKey, byte[]> entry : byteArrayPDCS.entrySet()) {
            toHash.append(entry.getKey().hashCode()).append("-").append(Arrays.toString(entry.getValue()));
        }

        toHash.append("stringPDCS: ");
        for (Map.Entry<NamespacedKey, String> entry : stringPDCS.entrySet()) {
            toHash.append(entry.getKey().hashCode()).append("-").append(entry.getValue());
        }

        toHash.append("doublePDCS: ");
        for (Map.Entry<NamespacedKey, Double> entry : doublePDCS.entrySet()) {
            toHash.append(entry.getKey().hashCode()).append("-").append(entry.getValue());
        }

        toHash.append("floatPDCS: ");
        for (Map.Entry<NamespacedKey, Float> entry : floatPDCS.entrySet()) {
            toHash.append(entry.getKey().hashCode()).append("-").append(entry.getValue());
        }

        toHash.append("integerPDCS: ");
        for (Map.Entry<NamespacedKey, Integer> entry : integerPDCS.entrySet()) {
            toHash.append(entry.getKey().hashCode()).append("-").append(entry.getValue());
        }

        toHash.append("integerArrayPDCS: ");
        for (Map.Entry<NamespacedKey, int[]> entry : integerArrayPDCS.entrySet()) {
            toHash.append(entry.getKey().hashCode()).append("-").append(Arrays.toString(entry.getValue()));
        }

        toHash.append("longPDCS: ");
        for (Map.Entry<NamespacedKey, Long> entry : longPDCS.entrySet()) {
            toHash.append(entry.getKey().hashCode()).append("-").append(entry.getValue());
        }

        toHash.append("longArrayPDCS: ");
        for (Map.Entry<NamespacedKey, long[]> entry : longArrayPDCS.entrySet()) {
            toHash.append(entry.getKey().hashCode()).append("-").append(Arrays.toString(entry.getValue()));
        }

        toHash.append("shortPDCS: ");
        for (Map.Entry<NamespacedKey, Short> entry : shortPDCS.entrySet()) {
            toHash.append(entry.getKey().hashCode()).append("-").append(entry.getValue());
        }

        return toHash.toString().hashCode();
    }

    // TODO: Override parseItemMeta

    // TODO: Add condition support for FlagPersistentDataContainer
}
