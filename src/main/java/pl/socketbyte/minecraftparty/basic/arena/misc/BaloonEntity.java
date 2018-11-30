package pl.socketbyte.minecraftparty.basic.arena.misc;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bat;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaloonEntity extends EntityBat {

    public BaloonEntity(World world) {
        super(world);
    }

    @Override
    public void move(double d0, double d1, double d2) {

    }


    public static ArmorStand spawn(Location loc, int id, int data, String title){
        return null;
    }
}
