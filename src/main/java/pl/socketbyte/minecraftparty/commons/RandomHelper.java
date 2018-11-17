package pl.socketbyte.minecraftparty.commons;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomHelper {

    private RandomHelper() {
    }

    public static List<Location> createRandomLocations(World world, double min, double max, double height, int size) {
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            locations.add(createRandomLocation(world, min, max, height));
        }
        return locations;
    }

    public static List<Location> createLocations(Location location, int size) {
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            locations.add(location);
        }
        return locations;
    }

    public static Location createRandomLocation(World world, double min, double max, double height) {
        return new Location(world, randomDouble(min, max), height, randomDouble(min, max));
    }

    public static int randomInteger(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
}
