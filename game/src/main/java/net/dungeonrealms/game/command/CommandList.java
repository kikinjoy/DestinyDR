package net.dungeonrealms.game.command;

import net.dungeonrealms.common.game.command.BaseCommand;
import net.dungeonrealms.common.game.database.player.Rank;
import net.dungeonrealms.database.PlayerWrapper;
import net.dungeonrealms.game.player.json.JSONMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Nick on 9/11/2015.
 */
public class CommandList extends BaseCommand {

    public CommandList(String command, String usage, String description) {
        super(command, usage, description);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String string, String[] args) {
        if (commandSender instanceof Player && !Rank.isPMOD((Player) commandSender))
            return false;

        // We want to force only showing PMODs online for users with the PMOD rank.
        boolean isModerator = (commandSender instanceof Player && !Rank.isTrialGM((Player) commandSender));
        if (isModerator)
            args = new String[] {"-m"};

        if (args.length > 0 && (args[0].equals("-h") || args[0].equals("-help"))) {
            commandSender.sendMessage(new String[] {
                    ChatColor.YELLOW + ChatColor.BOLD.toString() + "Displays a list of online users: /list [-s|-m|-h]",
                    ChatColor.YELLOW + "-s: Display only staff members.",
                    ChatColor.YELLOW + "-m: Display only player moderators.",
                    ChatColor.YELLOW + "-h: Display this help information."
            });
            return true;
        }

        StringBuilder players = new StringBuilder();

        final boolean staffOnly = args.length > 0 && args[0].equals("-s");
        final boolean pmodsOnly = args.length > 0 && args[0].equals("-m");

        String searchName = "Players";
        if (staffOnly) {
            searchName = "Staff Members";
        } else if (pmodsOnly) {
            searchName = "Player Moderators";
        }

        int onlinePlayers = 0; // Use an int that increments so we can refine the search.
        final JSONMessage message = new JSONMessage("", ChatColor.GREEN);
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Searching for staff but player isn't staff, skip...
            if (staffOnly && !Rank.isPMOD(player)) continue;
            // Searching for mods only but player isn't a mod, skip...
            if (pmodsOnly && !Rank.isPMOD(player)) continue;

            onlinePlayers++;

            // Format player name + rank properly.
            String playerName = PlayerWrapper.getWrapper(player).getChatName();
//            playerName = playerName.substring(0, playerName.length() - 4);

            String messageString = ChatColor.GRAY + "[" + playerName + ChatColor.GRAY + "]" + (onlinePlayers % 3 == 0 ? "\n" : " ");
            if (!isModerator) {
                message.addRunCommand(messageString, ChatColor.GRAY, "/tp " + player.getDisplayName(), "Click to teleport!");
            } else {
                message.addText(messageString, ChatColor.GRAY);
            }

            players.append(playerName);
        }

        if (players.length() > 0) {
            commandSender.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + searchName + " Online: " + ChatColor.LIGHT_PURPLE + onlinePlayers + ChatColor.GRAY + "/" + ChatColor.LIGHT_PURPLE + Bukkit.getMaxPlayers());
            // If the user is a player we want to send the JSONMessage to them so that they can use the added teleport functionality.
            if ((commandSender instanceof Player) && Rank.isTrialGM((Player) commandSender)) {
                message.sendToPlayer((Player) commandSender);
            } else {
                commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + players.toString() + ChatColor.GRAY + "]");
            }
        } else {
            commandSender.sendMessage(ChatColor.RED + ChatColor.ITALIC.toString() + "No " + searchName.toLowerCase() + " online.");
        }
        return true;
    }
}