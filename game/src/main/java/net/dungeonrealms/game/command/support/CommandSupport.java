package net.dungeonrealms.game.command.support;

import net.dungeonrealms.common.game.command.BaseCommand;
import net.dungeonrealms.common.game.database.player.Rank;

import net.dungeonrealms.game.player.inventory.menus.guis.support.MainSupportGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Brad on 09/06/2016.
 */
public class CommandSupport extends BaseCommand {

    public CommandSupport(String command, String usage, String description) {
        super(command, usage, description);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if (!Rank.isSupport(player)) return false;
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Invalid usage: /support <name>");
            return false;
        }

        new MainSupportGUI(player,args[0]).open(player,null);
        return true;
    }

}
