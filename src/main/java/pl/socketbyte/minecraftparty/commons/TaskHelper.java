package pl.socketbyte.minecraftparty.commons;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import pl.socketbyte.minecraftparty.MinecraftParty;

import java.util.concurrent.TimeUnit;

public class TaskHelper {

    private TaskHelper() {
    }

    private static final BukkitScheduler scheduler = Bukkit.getScheduler();

    public static BukkitTask async(Runnable runnable) {
        return scheduler.runTaskAsynchronously(MinecraftParty.getInstance(), runnable);
    }

    public static BukkitTask sync(Runnable runnable) {
        return scheduler.runTask(MinecraftParty.getInstance(), runnable);
    }

    public static BukkitTask delay(Runnable runnable, long delay, TimeUnit unit) {
        return scheduler.runTaskLater(MinecraftParty.getInstance(), runnable, toTicks(delay, unit));
    }

    public static BukkitTask schedule(Runnable runnable, long period, TimeUnit unit) {
        return scheduler.runTaskTimer(MinecraftParty.getInstance(), runnable, period, toTicks(period, unit));
    }

    private static long toTicks(long time, TimeUnit unit) {
        return unit.toSeconds(time) * 20;
    }
}
