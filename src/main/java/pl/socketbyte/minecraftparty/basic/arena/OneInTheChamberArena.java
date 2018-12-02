package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.RandomHelper;

import java.util.List;

public class OneInTheChamberArena extends Arena {

    private boolean freezed = true;

    public OneInTheChamberArena(Game game) {
        super(game);
    }

    @Override
    public List<Location> getPlayerStartPositions() {
        return RandomHelper.createRandomizedLocations(getArenaInfo().getDefaultLocation(),
                getArenaInfo().getData().getInt("radius"), getGame().getPlaying().size());
    }

    @Override
    public void onCountdown() {

    }

    private int _tempTime = 0;
    @Override
    public void onTick(long timeLeft) {
        _tempTime++;
        if (_tempTime >= 20) {
            _tempTime = 0;
            for (Player player : getGame().getPlaying()) {
                if (player.getInventory().getItem(8) != null) {
                    int amount = player.getInventory().getItem(8).getAmount();
                    player.getInventory().setItem(8, new ItemStack(Material.ARROW, amount + 1));
                }
                else {
                    player.getInventory().setItem(8, new ItemStack(Material.ARROW, 1));
                }
            }
        }
    }

    @Override
    public void onFreeze() {
        for (Player player : getGame().getPlaying()) {
            player.getInventory().clear();
        }
    }

    @Override
    public void onInit() {
        getBoard().setType(ArenaBoardType.SCORES);
        setPvp(false);
        setInfiniteHealth(true);
    }

    public Location getSpawnLocation() {
        return RandomHelper.randomizeLocation(getArenaInfo().getDefaultLocation(),
                getArenaInfo().getData().getInt("radius"));
    }

    @Override
    public void onStart() {
        freezed = false;
        for (Player player : getGame().getPlaying()) {
            player.getInventory().clear();
            player.getInventory().setItem(0, new ItemStack(Material.BOW));
            player.getInventory().setItem(8, new ItemStack(Material.ARROW));
            player.getInventory().setHeldItemSlot(0);
        }
    }

    @EventHandler
    public void onPlayerShoot(EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE)
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (!getGame().isPlaying(player))
            return;

        if (!getGame().isArena(this))
            return;

        Projectile damager = ((Projectile)event.getDamager());
        ProjectileSource shooterSource = ((Projectile)event.getDamager()).getShooter();
        if (!(shooterSource instanceof Player))
            return;

        Player shooter = (Player) shooterSource;

        event.setCancelled(true);

        player.teleport(getSpawnLocation());
        player.getInventory().clear();
        player.getInventory().setItem(0, new ItemStack(Material.BOW));
        player.getInventory().setItem(8, new ItemStack(Material.ARROW));
        player.getInventory().setHeldItemSlot(0);

        if (shooter.getInventory().getItem(8) != null) {
            int amount = shooter.getInventory().getItem(8).getAmount();
            shooter.getInventory().setItem(8, new ItemStack(Material.ARROW, amount + 1));
        }
        else {
            shooter.getInventory().setItem(8, new ItemStack(Material.ARROW, 1));
        }

        addInternalScore(shooter,1);
        shooter.getWorld().playSound(shooter.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
        damager.setBounce(false);
        damager.remove();

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        if (freezed) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        Player player = event.getPlayer();

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!getGame().isPlaying((Player) event.getWhoClicked()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrop(PlayerDropItemEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!getGame().isPlaying((Player) event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @Override
    public void onEnd() {

    }
}
