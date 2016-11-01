package net.dungeonrealms.vgame.world.dungeon.type;

import net.dungeonrealms.DungeonRealms;
import net.dungeonrealms.common.game.util.AsyncUtils;
import net.dungeonrealms.old.game.achievements.Achievements;
import net.dungeonrealms.old.game.mastery.MetadataUtils;
import net.dungeonrealms.old.game.mastery.Utils;
import net.dungeonrealms.old.game.party.Party;
import net.dungeonrealms.old.game.world.entity.EnumEntityType;
import net.dungeonrealms.old.game.world.entity.type.monster.boss.type.Burick;
import net.dungeonrealms.old.game.world.entity.util.EntityStats;
import net.dungeonrealms.old.game.world.teleportation.Teleportation;
import net.dungeonrealms.vgame.Game;
import net.dungeonrealms.vgame.world.dungeon.EnumDungeonEndReason;
import net.dungeonrealms.vgame.world.dungeon.EnumDungeon;
import net.dungeonrealms.vgame.world.dungeon.IDungeon;
import net.minecraft.server.v1_9_R2.Entity;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * Copyright © 2016 Matthew E Development - All Rights Reserved
 * You may NOT use, distribute and modify this code.
 * <p>
 * Created by Matthew E on 11/1/2016 at 2:35 PM.
 */
public class VarengladeDungeon implements IDungeon
{

    private Party party;
    private String name;
    private EnumDungeon dungeonEnum;
    private World world;
    private File worldZip;

    public VarengladeDungeon(Party party) {
        this.dungeonEnum = EnumDungeon.VARENGLADE;
        this.name = dungeonEnum.getName();
        this.party = party;
        this.worldZip = new File(Game.getGame().getDataFolder() + File.separator + "dungeons" + File.separator  + name + ".zip");
        setupInstance();
    }

    private void setupInstance() {
        String worldName = worldZip.getName().split(".zip")[0];
        AsyncUtils.pool.submit(() -> {
            try {
                unZip(new ZipFile(worldZip), worldName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        AsyncUtils.pool.submit(() -> {
            if (new File(worldName + "/" + "uid.dat").exists()) {
                // Delete that shit.
                new File(worldName + "/" + "uid.dat").delete();
            }
            try {
                FileUtils.forceDelete(new File(worldName + "/players"));
                FileUtils.copyDirectory(new File("plugins/WorldGuard/worlds/" + worldName), new File("plugins/WorldGuard/worlds/varenglade"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Utils.log.info("Completed setup of Dungeon: " + worldName);
        });
        Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonRealms.getInstance(), () -> {
            WorldCreator worldCreator = new WorldCreator(worldName);
            worldCreator.generateStructures(false);
            World world = Bukkit.getServer().createWorld(worldCreator);
            world.setStorm(false);
            world.setAutoSave(false);
            world.setKeepSpawnInMemory(false);
            world.setPVP(false);
            world.setGameRuleValue("randomTickSpeed", "0");
            Bukkit.getWorlds().add(world);
            this.world = world;
        }, 60L);
    }

    @Override
    public Party getParty() {
        return party;
    }

    @Override
    public void startDungeon() {

    }

    @Override
    public void endDungeon(EnumDungeonEndReason dungeonEndReason) {
        switch (dungeonEndReason) {
            case COMPLETE:
                getParty().getMembers().forEach(player -> {
                    Achievements.getInstance().giveAchievement(player.getUniqueId(), Achievements.EnumAchievements.VARENGLADE);
                });
                break;
            case LOSE:
                break;
        }
    }

    @Override
    public void teleportOut() {
        getParty().getMembers().forEach(player -> player.teleport(Teleportation.Cyrennica));
    }

    @Override
    public void spawnBoss(Location location) {
        Entity burick = new Burick(((CraftWorld) location.getWorld()).getHandle(), location);
        MetadataUtils.registerEntityMetadata(burick, EnumEntityType.HOSTILE_MOB, 1, 100);
        EntityStats.setBossRandomStats(burick, 100, 3);
        burick.setLocation(location.getX(), location.getY(), location.getZ(), 1, 1);
        ((CraftWorld) location.getWorld()).getHandle().addEntity(burick, CreatureSpawnEvent.SpawnReason.CUSTOM);
        burick.setLocation(location.getX(), location.getY(), location.getZ(), 1, 1);
        location.getWorld().playSound(location, Sound.ENTITY_ENDERDRAGON_HURT, 4F, 0.5F);
        return;
    }

    @Override
    public EnumDungeon getDungeonEnum() {
        return dungeonEnum;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void enterDungeon(Player player) {
        player.sendMessage(ChatColor.RED.toString() + "Burick The Fanatic" + ChatColor.RESET + ": How dare you enter my domain!");
        player.teleport(getDungeonWorld().getSpawnLocation());
    }

    @Override
    public World getDungeonWorld() {
        return world;
    }
}