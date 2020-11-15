package me.nathanfallet.popolsurvival.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check args
        if (args.length != 0) {
            // Create command
            if (args[0].equalsIgnoreCase("create") && sender instanceof Player) {

            }

            //

            //

            // Invalid sub command, send help
            else {
                sendHelp(sender);
            }
        }

        // No command specified, send help
        else {
            sendHelp(sender);
        }
        return true;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "----- " + ChatColor.GOLD + "Aide du /team " + ChatColor.YELLOW
                + "-----\n" + ChatColor.GOLD + "/team create <nom> " + ChatColor.YELLOW + ": Créer une team.\n"
                + ChatColor.GOLD + "/team add <team> <pseudo> " + ChatColor.YELLOW + ": Ajouter un membre à une team.\n"
                + ChatColor.GOLD + "/team remove <team> <pseudo> " + ChatColor.YELLOW
                + ": Enlever un membre d'une team.\n" + ChatColor.GOLD + "/team deposit <team> <valeur> "
                + ChatColor.YELLOW + ": Déposer de la money sur le compte d'une team.\n" + ChatColor.GOLD
                + "/team retrieve <team> <valeur> " + ChatColor.YELLOW
                + ": Récupérer de la money sur le compte d'une team.\n" + ChatColor.GOLD + "/team delete <team> "
                + ChatColor.YELLOW + ": Supprimer une team.\n" + ChatColor.GOLD
                + "/team setrole <team> <pseudo> <player/admin> " + ChatColor.YELLOW
                + ": Modifier le rôle d'un joueur d'une team.");
    }

}
