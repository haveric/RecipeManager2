package haveric.recipeManager.flag;

/**
 * Flag bits to configure special behavior
 */
public class FlagBit {
    public static final int NONE = 0;

    /**
     * Flag only works in recipes.
     */
    public static final int RECIPE = 1;

    /**
     * Flag only works on results.
     */
    public static final int RESULT = 1 << 1;

    /**
     * No value is required for this flag.
     */
    public static final int NO_VALUE_REQUIRED = 1 << 2;

    /**
     * Disables flag from being stored - used on flags that directly affect result's metadata.
     */
    public static final int NO_FOR = 1 << 3;

    /**
     * Disables "false" or "remove" values from removing the flag.
     */
    public static final int NO_FALSE = 1 << 4;

    /**
     * Allows flag to only work once per shift click, instead of for each result.
     */
    public static final int ONCE_PER_SHIFT = 1 << 5;

    /**
     * Disables generating a skip permission for this flag
     */
    public static final int NO_SKIP_PERMISSION = 1 << 6;
}
