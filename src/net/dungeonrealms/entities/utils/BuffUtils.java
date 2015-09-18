package net.dungeonrealms.entities.utils;

import net.dungeonrealms.entities.types.EnderCrystal;
import net.dungeonrealms.enums.EnumEntityType;
import net.dungeonrealms.mastery.MetadataUtils;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Created by Kieran on 9/18/2015.
 */
public class BuffUtils {

    public static void spawnBuff(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EnderCrystal enderCrystal = new EnderCrystal(world, EnumEntityType.BUFF);
        enderCrystal.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 0 , 0);
        world.addEntity(enderCrystal, CreatureSpawnEvent.SpawnReason.CUSTOM);
        enderCrystal.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 0 , 0);
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 1F, 1F);
        MetadataUtils.registerBuffMetadata(enderCrystal, PotionEffectType.BLINDNESS, 10, 20 * 6);
    }
}
