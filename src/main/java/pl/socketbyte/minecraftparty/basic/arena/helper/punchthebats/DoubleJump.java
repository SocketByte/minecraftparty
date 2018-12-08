package pl.socketbyte.minecraftparty.basic.arena.helper.punchthebats;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DoubleJump implements Listener {

    private static List<UUID> players = new ArrayList<>();
    private static final List<UUID> allowed = new ArrayList<>();

    public static void enable(List<Player> players) {
        for (Player player : players) {
            player.setAllowFlight(true);
            player.setFlying(false);
            allowed.add(player.getUniqueId());
        }
    }

    public static void disable(List<Player> players) {
        for (Player player : players) {
            disable(player);
        }
    }

    public static void disable(Player player) {
        player.setAllowFlight(false);
        player.setFlying(false);
        DoubleJump.players.remove(player.getUniqueId());
        DoubleJump.allowed.remove(player.getUniqueId());
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        if (!allowed.contains(p.getUniqueId()))
            return;

        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR
                || p.isFlying() || players.contains(p.getUniqueId())) {
            return;
        }
        players.add(p.getUniqueId());

        e.setCancelled(true);

        p.setAllowFlight(false);
        p.setFlying(false);

        p.setVelocity(e.getPlayer().getLocation().getDirection().multiply(1.1).setY(0.9));
        p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1.0f, -5.0f);

        p.setFallDistance(100);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player p = (Player) e.getEntity();
            if (!allowed.contains(p.getUniqueId()))
                return;
            if (players.contains(p.getUniqueId())) {
                e.setCancelled(true);
                players.remove(p.getUniqueId());
                p.setAllowFlight(true);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        players.remove(e.getPlayer().getUniqueId());
        allowed.remove(e.getPlayer().getUniqueId());
    }

}
