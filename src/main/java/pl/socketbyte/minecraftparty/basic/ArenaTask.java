package pl.socketbyte.minecraftparty.basic;

import org.bukkit.scheduler.BukkitTask;
import pl.socketbyte.minecraftparty.commons.TaskHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class ArenaTask {

    private final Arena arena;
    private final List<BukkitTask> tasks =  new CopyOnWriteArrayList<>();

    public ArenaTask(Arena arena) {
        this.arena = arena;
    }

    public void schedule(Runnable runnable, long period, TimeUnit unit) {
        tasks.add(TaskHelper.schedule(runnable, period, unit));
    }

    public void schedule(Runnable runnable, long ticks) {
        tasks.add(TaskHelper.schedule(runnable, ticks));
    }

    public void delay(Runnable runnable, long delay, TimeUnit unit) {
        tasks.add(TaskHelper.delay(runnable, delay, unit));
    }

    public void dispose() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
        tasks.clear();
    }

}
