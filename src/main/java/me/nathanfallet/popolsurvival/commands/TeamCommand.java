package me.nathanfallet.popolsurvival.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.PopolTeam;
import me.nathanfallet.popolsurvival.utils.PopolTeam.TeamLoaderHandler;

public class TeamCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        // Check args
        if (args.length != 0) {
            // Create command
            if (args[0].equalsIgnoreCase("create") && sender instanceof Player) {
                // Check args
                if (args.length == 2) {
                    // Try to create a team
                    sender.sendMessage(ChatColor.YELLOW + "Création de votre team...");
                    PopolSurvival.getInstance().createTeam(args[1],
                            PopolServer.getInstance().getPlayer(((Player) sender).getUniqueId()),
                            new TeamLoaderHandler() {
                                @Override
                                public void teamLoaded(PopolTeam team) {
                                    // Check if team was created
                                    if (team != null) {
                                        // Confirm
                                        sender.sendMessage(ChatColor.GREEN + "Bienvenue dans la team " + ChatColor.YELLOW + team.getCached().name + ChatColor.GREEN + " !");
                                    } else {
                                        // Error
                                        sender.sendMessage(ChatColor.RED + "Erreur : une team portant ce nom existe déjà !");
                                    }
                                }
                            });
                } else {
                    // Send help
                    sender.sendMessage(ChatColor.RED + "/team create <nom>");
                }
            }

            // Add command
            else if (args[0].equalsIgnoreCase("add") && sender instanceof Player) {
                // Check args
                if (args.length == 3) {
                    // Get team with this name

                    // Check permissions

                    // Add player

                } else {
                    // Send help
                    sender.sendMessage(ChatColor.RED + "/team add <team> <pseudo>");
                }
            }

            // Remove command
            else if (args[0].equalsIgnoreCase("remove") && sender instanceof Player) {
                // Check args
                if (args.length == 3) {
                    // Get team with this name

                    // Check permissions

                    // Remove player

                } else {
                    // Send help
                    sender.sendMessage(ChatColor.RED + "/team remove <team> <pseudo>");
                }
            }

            // Deposit command
            else if (args[0].equalsIgnoreCase("deposit") && sender instanceof Player) {

            }

            // Retrieve command
            else if (args[0].equalsIgnoreCase("retrieve") && sender instanceof Player) {

            }

            // Delete command
            else if (args[0].equalsIgnoreCase("delete") && sender instanceof Player) {

            }

            // Set role command
            else if (args[0].equalsIgnoreCase("setrole") && sender instanceof Player) {

            }

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
