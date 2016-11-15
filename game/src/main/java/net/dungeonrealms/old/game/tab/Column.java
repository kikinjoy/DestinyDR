package net.dungeonrealms.old.game.tab;

import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import lombok.Getter;
import net.dungeonrealms.common.old.network.ShardInfo;
import net.dungeonrealms.vgame.Game;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Class written by APOLLOSOFTWARE.IO on 8/4/2016
 */

public abstract class Column {

    @Getter
    protected List<Variable> variablesToRegister = new ArrayList<>();


    /**
     * Create all variables associated with this colman
     *
     * @return Column instance
     */
    public abstract Column register();


    protected static String getFormat(String displayName, ShardInfo shard) {
        if (Game.getGame().getGameShard().getShardInfo().equals(shard)) {
            // THIS WILL INDICATE THAT PLAYER IS IN CURRENT SHARD //
            return ChatColor.GREEN + " ⦿ " + ChatColor.GRAY + displayName;
        } else {
            return ChatColor.GOLD + "(" + shard.getShardID() + ") " + ChatColor.GRAY + displayName;
        }

    }

}
