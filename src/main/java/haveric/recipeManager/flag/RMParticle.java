package haveric.recipeManager.flag;

import org.bukkit.Particle;

public class RMParticle {

    private Particle particle;
    private int delay = 0;

    public static double DEFAULT_OFFSET_X = .5;
    public static  double DEFAULT_OFFSET_Y = 1.0;
    public static  double DEFAULT_OFFSET_Z = .5;

    public static  double DEFAULT_RANDOM_OFFSET = .25;

    private double offsetX = DEFAULT_OFFSET_X;
    private double offsetY = DEFAULT_OFFSET_Y;
    private double offsetZ = DEFAULT_OFFSET_Z;

    private double randomOffsetX = DEFAULT_RANDOM_OFFSET;
    private double randomOffsetY = DEFAULT_RANDOM_OFFSET;
    private double randomOffsetZ = DEFAULT_RANDOM_OFFSET;

    private int count = 1;
    private Double extra = Double.NaN;

    public RMParticle(Particle newParticle) {
        particle = newParticle;
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getOffsetZ() {
        return offsetZ;
    }

    public void setOffsetZ(double offsetZ) {
        this.offsetZ = offsetZ;
    }

    public double getRandomOffsetX() {
        return randomOffsetX;
    }

    public void setRandomOffsetX(double randomOffsetX) {
        this.randomOffsetX = randomOffsetX;
    }

    public double getRandomOffsetY() {
        return randomOffsetY;
    }

    public void setRandomOffsetY(double randomOffsetY) {
        this.randomOffsetY = randomOffsetY;
    }

    public double getRandomOffsetZ() {
        return randomOffsetZ;
    }

    public void setRandomOffsetZ(double randomOffsetZ) {
        this.randomOffsetZ = randomOffsetZ;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getExtra() {
        return extra;
    }

    public void setExtra(double extra) {
        this.extra = extra;
    }
}
