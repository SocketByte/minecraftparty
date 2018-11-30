package pl.socketbyte.minecraftparty.basic.arena.misc;

import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.ImmutableBlock;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WoolCell {

    private DyeColor color;
    private List<Location> locations = new ArrayList<>();

    public WoolCell() {
    }

    public void paintDefault() {
        for (Location location : locations) {
            location.getBlock().setType(Material.WOOL);
        }
    }

    public void paint(EditSession session, DyeColor color) {
        this.color = color;
        for (Location location : locations) {
            try {
                session.setBlock(new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                        new BaseBlock(Material.WOOL.getId(), color.getData()));
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        }
    }

    public void remove(EditSession session) {
        this.color = null;
        for (Location location : locations) {
            try {
                session.setBlock(new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                        new BaseBlock(Material.AIR.getId(), 0));
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        }
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void setLocation(Location location) {
        this.locations.add(location);
    }

    public DyeColor getColor() {
        return color;
    }

}
