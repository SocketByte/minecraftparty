package pl.socketbyte.minecraftparty.basic.arena.helper.trampolinio;

import org.bukkit.Material;

public enum BaloonType {
    LOW(1, Material.WOOL, (byte)5, 2),
    MEDIUM(3, Material.WOOL, (byte)4, 4),
    HIGH(10, Material.WOOL, (byte)14, 4),
    BOOSTER(0, Material.WOOL, (byte)0, 0);

    private int points;
    private Material material;
    private byte data;
    private int divideLimit;

    BaloonType(int points, Material material, byte data, int divideLimit) {
        this.points = points;
        this.material = material;
        this.data = data;
        this.divideLimit = divideLimit;
    }

    public int getPoints() {
        return points;
    }

    public Material getMaterial() {
        return material;
    }

    public byte getData() {
        return data;
    }

    public int getDivideLimit() {
        return divideLimit;
    }
}
