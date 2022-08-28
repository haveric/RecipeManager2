package haveric.recipeManager.flag.flags.any.flagLightLevel;

public class LightLevelOptions {
    private int minLight;
    private int maxLight;
    private LightType lightType;

    public LightLevelOptions(int minLight, int maxLight, LightType lightType) {
        this.minLight = minLight;
        this.maxLight = maxLight;
        this.lightType = lightType;
    }

    /**
     * @return min light level.
     */
    public int getMinLight() {
        return minLight;
    }

    /**
     * @return max light level, if disabled will be less than min light level.
     */
    public int getMaxLight() {
        return maxLight;
    }

    public LightType getLightType() {
        return lightType;
    }

    public String getLightString() {
        String lightString;

        if (maxLight > minLight) {
            lightString = minLight + "-" + maxLight;
        } else {
            lightString = minLight + "+";
        }
        return lightString;
    }

    public String getLightTypeString() {
        return lightType.asString();
    }

    public boolean isValidLightLevel(int lightLevel) {
        return lightLevel >= minLight && (maxLight <= minLight || lightLevel <= maxLight);
    }
}
