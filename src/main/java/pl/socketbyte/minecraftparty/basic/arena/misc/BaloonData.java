package pl.socketbyte.minecraftparty.basic.arena.misc;

public class BaloonData {

    private final BaloonType type;

    private int points;
    private int minY;
    private int maxY;
    private double chance;
    private String title;

    public BaloonData(BaloonType type) {
        this.type = type;
        this.setPoints(type.getPoints());
    }

    public BaloonType getType() {
        return type;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
