package haveric.recipeManager.tools;

public class ParseBit {
    public static final byte NO_ERRORS = 1 << 0;
    public static final byte NO_WARNINGS = 1 << 1;
    public static final byte NO_PRINT = NO_ERRORS | NO_WARNINGS;

    public static final byte NO_DATA = 1 << 2;
    public static final byte NO_AMOUNT = 1 << 3;

    public static final byte NO_ENCHANTMENTS = 1 << 5;
    public static final byte NO_NAME = 1 << 6;
    public static final short NO_LORE = 1 << 7;
    public static final short NO_COLOR = 1 << 8;
    public static final short NO_META = NO_ENCHANTMENTS | NO_NAME | NO_LORE | NO_COLOR;
}