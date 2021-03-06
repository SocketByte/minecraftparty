package pl.socketbyte.minecraftparty.commons;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RandomHelper {

    private RandomHelper() {
    }

    public static boolean chance(double chance) {
        double normalized = chance / 100.0;

        return Math.random() < normalized;
    }

    public static <T> T pick(Map<T, Double> map) {
        double p = Math.random() * 100.0;
        double cumulativeProbability = 0.0;
        for (Map.Entry<T, Double> entry : map.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (p <= cumulativeProbability && entry.getValue() != 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static Location randomizeLocation(Location location, int radius) {
        Location old = location.clone();
        int randomX = RandomHelper.randomInteger(-radius, radius);
        int randomZ = RandomHelper.randomInteger(-radius, radius);

        location.add(randomX, 0, randomZ);
        if (location.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
            while (location.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
                location.subtract(0, 1, 0);
            }
        }
        else if (location.clone().add(0, 1, 0).getBlock().getType() != Material.AIR
                || location.getBlock().getType() != Material.AIR) {
            while (location.clone().add(0, 1, 0).getBlock().getType() != Material.AIR
                    && location.getBlock().getType() != Material.AIR) {
                location.add(0, 1, 0);
            }
            location.add(0, 1, 0);
        }
        if (location.clone().add(0, 1, 0).getBlock().getType() != Material.AIR && location.getBlock().getType() != Material.AIR)
            return randomizeLocation(old, radius);
        return location;
    }

    public static List<Location> createRandomizedLocations(Location base, int radius, int size) {
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            locations.add(randomizeLocation(base, radius));
        }
        return locations;
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
