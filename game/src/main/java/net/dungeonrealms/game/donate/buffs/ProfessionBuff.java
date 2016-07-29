package net.dungeonrealms.game.donate.buffs;

import net.dungeonrealms.DungeonRealms;
import net.dungeonrealms.common.game.database.DatabaseAPI;
import net.dungeonrealms.common.game.database.data.EnumOperators;
import net.dungeonrealms.game.donate.DonationEffects;
import net.dungeonrealms.game.mastery.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Created by Alan on 7/28/2016.
 */
public class ProfessionBuff extends Buff {

    // empty constructor is needed for generic instantiation during deserialization
    public ProfessionBuff() {}

    public ProfessionBuff(int duration, float bonusAmount, String activatingPlayer, String fromServer) {
        this.duration = duration;
        this.bonusAmount = bonusAmount;
        this.activatingPlayer = activatingPlayer;
        this.fromServer = fromServer;
    }

    @Override
    public void onActivateBuff() {
        int minutesUntilExpiry = duration / 60;
        Bukkit.getServer().broadcastMessage("");
        Bukkit.getServer().broadcastMessage(
                ChatColor.GOLD + "" + ChatColor.BOLD + ">> " + "(" + Utils.getFormattedShardName(fromServer) + ") " + ChatColor.RESET + activatingPlayer + ChatColor.GOLD
                        + " has just activated " + ChatColor.UNDERLINE + "+" + bonusAmount + "% Global Profession Rates" + ChatColor.GOLD
                        + " for 30 minutes by using 'Global Profession Buff' from the E-CASH store!");
        Bukkit.getServer().broadcastMessage("");
        DonationEffects.getInstance().setActiveProfessionBuff(this);
        DatabaseAPI.getInstance().updateShardCollection(DungeonRealms.getInstance().bungeeName, EnumOperators.$SET,
                "buffs.activeProfessionBuff", this.serialize(), true);
    }

    @Override
    public void deactivateBuff() {
        final DonationEffects de = DonationEffects.getInstance();
        final LootBuff nextBuff = de.getQueuedLootBuffs().poll();

        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + ">> " + ChatColor.GOLD + "The " + ChatColor.UNDERLINE
                + "+20% Global Profession Rates" + ChatColor.GOLD + " from " + activatingPlayer + ChatColor.GOLD + " has expired.");

        if (nextBuff != null) {
            DatabaseAPI.getInstance().updateShardCollection(DungeonRealms.getInstance().bungeeName, EnumOperators.$PULL,
                    "buffs.queuedProfessionBuffs", this.serialize(), true);
            nextBuff.activateBuff();
        } else
            de.getInstance().setActiveProfessionBuff(null);
    }

}