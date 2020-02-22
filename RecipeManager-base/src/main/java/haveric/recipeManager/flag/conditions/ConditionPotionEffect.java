package haveric.recipeManager.flag.conditions;

public class ConditionPotionEffect {
    private int durationMinLevel = -1;
    private int durationMaxLevel = -1;
    private int amplifyMinLevel = -1;
    private int amplifyMaxLevel = -1;
    private Boolean ambient;
    private Boolean particles;
    private Boolean icon;

    public ConditionPotionEffect() {

    }

    public int getDurationMinLevel() {
        return durationMinLevel;
    }

    public void setDurationMinLevel(int durationMinLevel) {
        this.durationMinLevel = durationMinLevel;
    }

    public int getDurationMaxLevel() {
        return durationMaxLevel;
    }

    public void setDurationMaxLevel(int durationMaxLevel) {
        this.durationMaxLevel = durationMaxLevel;
    }

    public boolean hasDuration() {
        return durationMinLevel > -1 && durationMaxLevel > -1;
    }

    public int getAmplifyMinLevel() {
        return amplifyMinLevel;
    }

    public void setAmplifyMinLevel(int amplifyMinLevel) {
        this.amplifyMinLevel = amplifyMinLevel;
    }

    public int getAmplifyMaxLevel() {
        return amplifyMaxLevel;
    }

    public void setAmplifyMaxLevel(int amplifyMaxLevel) {
        this.amplifyMaxLevel = amplifyMaxLevel;
    }

    public boolean hasAmplify() {
        return amplifyMinLevel > -1 && amplifyMaxLevel > -1;
    }

    public Boolean getAmbient() {
        return ambient;
    }

    public void setAmbient(Boolean ambient) {
        this.ambient = ambient;
    }

    public boolean hasAmbient() {
        return ambient != null;
    }

    public Boolean getParticles() {
        return particles;
    }

    public void setParticles(Boolean particles) {
        this.particles = particles;
    }

    public boolean hasParticles() {
        return particles != null;
    }

    public Boolean getIcon() {
        return icon;
    }

    public void setIcon(Boolean icon) {
        this.icon = icon;
    }

    public boolean hasIcon() {
        return icon != null;
    }

    @Override
    public int hashCode() {
        String toHash = "ConditionPotionEffect:";

        toHash += "durationMinLevel: " + durationMinLevel;
        toHash += "durationMaxLevel: " + durationMaxLevel;
        toHash += "amplifyMinLevel: " + amplifyMinLevel;
        toHash += "amplifyMaxLevel: " + amplifyMaxLevel;

        if (hasAmbient()) {
            toHash += "ambient: " + ambient.toString();
        }

        if (hasParticles()) {
            toHash += "particles: " + particles.toString();
        }

        if (hasIcon()) {
            toHash += "icon: " + icon.toString();
        }

        return toHash.hashCode();
    }
}
