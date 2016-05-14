package haveric.recipeManager.flags;

/**
 * Flag bits to configure special behavior
 */
public class FlagBit {
    public static final byte NONE = 0;

    /**
     * Flag only works in recipes.
     */
    public static final byte RECIPE = 1 << 0;

    /**
     * Flag only works on results.
     */
    public static final byte RESULT = 1 << 1;

    /**
     * No value is allowed for this flag.
     */
    public static final byte NO_VALUE = 1 << 2;

    /**
     * Disables flag from being stored - used on flags that directly affect result's metadata.
     */
    public static final byte NO_FOR = 1 << 3;

    /**
     * Disables "false" or "remove" values from removing the flag.
     */
    public static final byte NO_FALSE = 1 << 4;

    /**
     * Disables shift+click on the recipe if there is at least one flag with this bit.
     */
    public static final byte NO_SHIFT = 1 << 5;

    /**
     * Disables generating a skip permission for this flag
     */
    public static final byte NO_SKIP_PERMISSION = 1 << 6;
}
