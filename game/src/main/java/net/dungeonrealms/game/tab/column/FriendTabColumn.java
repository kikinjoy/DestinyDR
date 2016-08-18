package net.dungeonrealms.game.tab.column;

import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import net.dungeonrealms.common.Tuple;
import net.dungeonrealms.common.game.database.DatabaseAPI;
import net.dungeonrealms.common.network.ShardInfo;
import net.dungeonrealms.common.network.bungeecord.BungeeServerTracker;
import net.dungeonrealms.common.network.ping.PingResponse;
import net.dungeonrealms.game.handler.FriendHandler;
import net.dungeonrealms.game.tab.Column;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Class written by APOLLOSOFTWARE.IO on 8/5/2016
 */
public class FriendTabColumn extends Column {

    @Override
    public Column register() {
        for (int i = 0; i < 18; i++) {
            int cursor = i;
            variablesToRegister.add(new Variable("friends." + cursor) {
                @Override
                public String getReplacement(Player player) {
                    if (!DatabaseAPI.getInstance().PLAYERS.containsKey(player.getUniqueId())) return "";

                    List<String> friends = FriendHandler.getInstance().getFriendsList(player.getUniqueId());

                    if (friends.size() == 0) {
                        switch (cursor) {
                            case 0:
                                return ChatColor.GRAY.toString() + "Type " + ChatColor.GREEN + "/add" + ChatColor.GRAY + " to add someone";
                            case 1:
                                return ChatColor.GRAY.toString() + "To your buddy list";

                        }
                        return "";
                    }

                    List<String> onlineFriends = new ArrayList<>();

                    // MAKE SURE FRIENDS ARE ONLINE //
                    friends.forEach(uuid -> {
                        Optional<Tuple<PingResponse.PlayerInfo, ShardInfo>> curInfo = BungeeServerTracker.grabPlayerInfo(UUID.fromString(uuid));

                        if (curInfo.isPresent())
                            onlineFriends.add(ChatColor.GOLD + curInfo.get().b().getShardID() + " " + ChatColor.GREEN + curInfo.get().a().getName());
                    });

                    if (onlineFriends.isEmpty()) if (cursor == 0)
                        return ChatColor.RED + "No friends on this shard!";
                    else return "";
                    try {
                        if (onlineFriends.get(cursor) == null)
                            return "";
                    } catch (Exception ignored) {
                        return "";
                    }

                    return ChatColor.GREEN + onlineFriends.get(cursor);
                }
            });
        }
        return this;
    }
}