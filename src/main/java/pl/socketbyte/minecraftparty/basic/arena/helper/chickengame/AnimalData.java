package pl.socketbyte.minecraftparty.basic.arena.helper.chickengame;

public class AnimalData {

    private final AnimalType type;

    private AnimalPointData plus;
    private AnimalPointData minus;

    private int health;
    private double chance;

    public AnimalData(AnimalType type) {
        this.type = type;
    }

    public AnimalType getType() {
        return type;
    }

    public AnimalPointData getPlus() {
        return plus;
    }

    public void setPlus(AnimalPointData plus) {
        this.plus = plus;
    }

    public AnimalPointData getMinus() {
        return minus;
    }

    public void setMinus(AnimalPointData minus) {
        this.minus = minus;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }
}
