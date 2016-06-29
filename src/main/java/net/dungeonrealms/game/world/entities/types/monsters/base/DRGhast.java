package net.dungeonrealms.game.world.entities.types.monsters.base;

import lombok.Getter;
import net.dungeonrealms.API;
import net.dungeonrealms.DungeonRealms;
import net.dungeonrealms.game.miscellaneous.SkullTextures;
import net.dungeonrealms.game.world.anticheat.AntiCheat;
import net.dungeonrealms.game.world.entities.types.monsters.DRMonster;
import net.dungeonrealms.game.world.entities.types.monsters.EnumMonster;
import net.dungeonrealms.game.world.items.Item;
import net.dungeonrealms.game.world.items.itemgenerator.ItemGenerator;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kieran Quigley (Proxying) on 09-Jun-16.
 */
public class DRGhast extends EntityGhast implements DRMonster {

    EnumMonster monster;
    int tier;
    @Getter
    ItemStack weapon = getTierWeapon(tier);
    @Getter
    protected Map<String, Integer[]> attributes = new HashMap<>();

    public DRGhast(World world) {
        super(world);
    }

    public DRGhast(World world, EnumMonster mon, int tier) {
        super(world);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(24d);
        setArmor(tier);
        monster = mon;
        String customName = mon.getPrefix() + " " + mon.name + " " + mon.getSuffix() + " ";
        this.setCustomName(customName);
        this.getBukkitEntity().setMetadata("customname", new FixedMetadataValue(DungeonRealms.getInstance(), customName));
        LivingEntity livingEntity = (LivingEntity) this.getBukkitEntity();
        this.noDamageTicks = 0;
        this.maxNoDamageTicks = 0;
    }

    public void setArmor(int tier) {
        ItemStack[] armor = API.getTierArmor(tier);
        // weapon, boots, legs, chest, helmet/head
        LivingEntity livingEntity = (LivingEntity) this.getBukkitEntity();
        boolean armorMissing = false;
        if (random.nextInt(10) <= 5) {
            ItemStack armor0 = AntiCheat.getInstance().applyAntiDupe(armor[0]);
            livingEntity.getEquipment().setBoots(armor0);
            this.setEquipment(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(armor0));
        } else {
            armorMissing = true;
        }
        if (random.nextInt(10) <= 5 || armorMissing) {
            ItemStack armor1 = AntiCheat.getInstance().applyAntiDupe(armor[1]);
            livingEntity.getEquipment().setLeggings(armor1);
            this.setEquipment(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(armor1));
            armorMissing = false;
        } else {
            armorMissing = true;
        }
        if (random.nextInt(10) <= 5 || armorMissing) {
            ItemStack armor2 = AntiCheat.getInstance().applyAntiDupe(armor[2]);
            livingEntity.getEquipment().setChestplate(armor2);
            this.setEquipment(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(armor2));
        }
    }

    private ItemStack getTierWeapon(int tier) {
        ItemStack item = new ItemGenerator().setTier(Item.ItemTier.getByTier(tier)).setType(Item.ItemType.STAFF)
                .setRarity(API.getItemRarity(false)).generateItem().getItem();
        AntiCheat.getInstance().applyAntiDupe(item);
        return item;
    }

    @Override
    public void onMonsterAttack(Player p) {
    }

    @Override
    public void onMonsterDeath(Player killer) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonRealms.getInstance(), ()-> {
            this.checkItemDrop(this.getBukkitEntity().getMetadata("tier").get(0).asInt(), monster, this.getBukkitEntity(), killer);
        });
    }

    @Override
    public EnumMonster getEnum() {
        return monster;
    }

    @Override
    public void enderTeleportTo(double d0, double d1, double d2) {
        //Test for EnderPearl TP Cancel.
    }
}
