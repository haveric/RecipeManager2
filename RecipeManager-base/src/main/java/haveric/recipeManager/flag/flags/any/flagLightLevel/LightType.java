package haveric.recipeManager.flag.flags.any.flagLightLevel;

public enum LightType {
    ANY,
    SUN,
    BLOCKS;

    public static LightType getLightType(char c) {
        switch (c) {
            case 's':
                return LightType.SUN;
            case 'b':
                return LightType.BLOCKS;
            default:
                return LightType.ANY;
        }
    }

    public String asString() {
        switch(this) {
            case SUN:
                return "sun light";
            case BLOCKS:
                return "block light";
            default:
                return "light";
        }
    }
}