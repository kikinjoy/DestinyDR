package net.dungeonrealms.old.game.command;

import net.dungeonrealms.GameAPI;
import net.dungeonrealms.common.frontend.command.BaseCommand;
import net.dungeonrealms.common.old.game.database.DatabaseAPI;
import net.dungeonrealms.common.old.game.database.data.EnumData;
import net.dungeonrealms.common.old.game.database.data.EnumOperators;
import net.dungeonrealms.common.old.game.database.player.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Class written by APOLLOSOFTWARE.IO on 6/23/2016
 */

public class CommandRealmFix extends BaseCommand {

    public CommandRealmFix(String command, String usage, String description) {
        super(command, usage, description);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!Rank.isGM(player)) return false;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + usage);
            return true;
        }

        if (DatabaseAPI.getInstance().getUUIDFromName(args[0]).equals("")) {
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + args[0] + ChatColor.RED + " does not exist in our database.");
            return true;
        }

        UUID p_uuid = UUID.fromString(DatabaseAPI.getInstance().getUUIDFromName(args[0]));
        DatabaseAPI.getInstance().update(p_uuid, EnumOperators.$SET, EnumData.REALM_UPLOAD, false, false);
        DatabaseAPI.getInstance().update(p_uuid, EnumOperators.$SET, EnumData.REALM_UPGRADE, false, false);

        sender.sendMessage(ChatColor.GRAY.toString() + "Realm fixed");

        GameAPI.updatePlayerData(p_uuid);
        return true;
    }
}
