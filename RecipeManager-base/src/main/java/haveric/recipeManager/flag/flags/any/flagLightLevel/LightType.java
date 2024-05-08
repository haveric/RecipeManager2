package haveric.recipeManager.flag.flags.any.flagLightLevel;

public enum LightType {
    ANY,
    SUN,
    BLOCKS;

    public static LightType getLightType(char c) {
        return switch (c) {
            case 's' -> LightType.SUN;
            case 'b' -> LightType.BLOCKS;
            default -> LightType.ANY;
        };
    }

    public String asString() {
        return switch (this) {
            case SUN -> "sun light";
            case BLOCKS -> "block light";
            default -> "light";
        };
    }
}