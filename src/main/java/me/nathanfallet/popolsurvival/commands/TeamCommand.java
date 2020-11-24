package me.nathanfallet.popolsurvival.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APIMessage;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;
import me.nathanfallet.popolserver.api.APITeam;
import me.nathanfallet.popolserver.utils.PopolMoney;
import me.nathanfallet.popolserver.utils.PopolMoney.BalanceCheckHandler;
import me.nathanfallet.popolserver.utils.PopolMoney.BalanceUpdatedHandler;
import me.nathanfallet.popolserver.utils.PopolPlayer;
import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.PopolTeam;
import me.nathanfallet.popolsurvival.utils.PopolTeam.TeamLoaderHandler;
import me.nathanfallet.popolsurvival.utils.PopolTeamMoney;

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
                                        sender.sendMessage(ChatColor.GREEN + "Bienvenue dans la team "
                                                + ChatColor.YELLOW + team.getCached().name + ChatColor.GREEN + " !");
                                    } else {
                                        // Error
                                        sender.sendMessage(
                                                ChatColor.RED + "Erreur : une team portant ce nom existe déjà !");
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
                    PopolTeam team = PopolSurvival.getInstance().getTeam(args[1]);
                    if (team != null) {
                        // Check permissions
                        String role = team.getRole(((Player) sender).getUniqueId());
                        if (role.equals("owner") || role.equals("admin")) {
                            // Get player
                            final Player player = Bukkit.getPlayer(args[2]);
                            if (player != null && player.isOnline()) {
                                // Check if it is not already in team
                                if (!team.hasPlayer(player.getUniqueId())) {
                                    // Add it
                                    sender.sendMessage(ChatColor.YELLOW + "Ajout du joueur dans la team...");
                                    team.postPlayer(player.getUniqueId(), "player", new CompletionHandler<APITeam>() {
                                        @Override
                                        public void completionHandler(APITeam team, APIResponseStatus status) {
                                            // Check status
                                            if (status == APIResponseStatus.ok) {
                                                // Player was added to team
                                                sender.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN
                                                        + " est maintenant dans la team " + ChatColor.YELLOW + team.name
                                                        + ChatColor.GREEN + " !");
                                                player.sendMessage(ChatColor.YELLOW + sender.getName() + ChatColor.GREEN
                                                        + " vous a ajouté dans la team " + ChatColor.YELLOW + team.name
                                                        + ChatColor.GREEN + " !");
                                            } else {
                                                // Error
                                                sender.sendMessage(ChatColor.RED + "Erreur inconnue !");
                                            }
                                        }
                                    });
                                } else {
                                    // Error
                                    sender.sendMessage(ChatColor.RED + "Erreur : ce joueur est déjà dans cette team !");
                                }
                            } else {
                                // Error
                                sender.sendMessage(
                                        ChatColor.RED + "Erreur : ce joueur n'existe pas ou n'est pas connecté !");
                            }
                        } else {
                            // Error
                            sender.sendMessage(
                                    ChatColor.RED + "Erreur : vous ne pouvez pas ajouter de joueur dans cette team !");
                        }
                    } else {
                        // Error
                        sender.sendMessage(ChatColor.RED + "Erreur : cette team n'existe pas !");
                    }
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
                    PopolTeam team = PopolSurvival.getInstance().getTeam(args[1]);
                    if (team != null) {
                        // Check permissions
                        String role = team.getRole(((Player) sender).getUniqueId());
                        if (role.equals("owner") || role.equals("admin")) {
                            // Get player
                            final Player player = Bukkit.getPlayer(args[2]);
                            if (player != null && player.isOnline()) {
                                // Check if it is in team
                                if (team.hasPlayer(player.getUniqueId())) {
                                    // Check target is not team's owner
                                    if (!team.getRole(player.getUniqueId()).equals("owner")) {
                                        // Remove it
                                        sender.sendMessage(ChatColor.YELLOW + "Suppression du joueur de la team...");
                                        team.deletePlayer(player.getUniqueId(), new CompletionHandler<APITeam>() {
                                            @Override
                                            public void completionHandler(APITeam team, APIResponseStatus status) {
                                                // Check status
                                                if (status == APIResponseStatus.ok) {
                                                    // Player was removed from team
                                                    sender.sendMessage(ChatColor.YELLOW + player.getName()
                                                            + ChatColor.GREEN + " n'est maintenant plus dans la team "
                                                            + ChatColor.YELLOW + team.name + ChatColor.GREEN + " !");
                                                    player.sendMessage(ChatColor.YELLOW + sender.getName()
                                                            + ChatColor.GREEN + " vous a expulsé de la team "
                                                            + ChatColor.YELLOW + team.name + ChatColor.GREEN + " !");
                                                } else {
                                                    // Error
                                                    sender.sendMessage(ChatColor.RED + "Erreur inconnue !");
                                                }
                                            }
                                        });
                                    } else {
                                        // Error
                                        sender.sendMessage(ChatColor.RED
                                                + "Erreur : ce joueur ne peut pas être supprimé de la team !");
                                    }
                                } else {
                                    // Error
                                    sender.sendMessage(
                                            ChatColor.RED + "Erreur : ce joueur n'est pas dans cette team !");
                                }
                            } else {
                                // Error
                                sender.sendMessage(
                                        ChatColor.RED + "Erreur : ce joueur n'existe pas ou n'est pas connecté !");
                            }
                        } else {
                            // Error
                            sender.sendMessage(
                                    ChatColor.RED + "Erreur : vous ne pouvez pas enlever de joueur dans cette team !");
                        }
                    } else {
                        // Error
                        sender.sendMessage(ChatColor.RED + "Erreur : cette team n'existe pas !");
                    }
                } else {
                    // Send help
                    sender.sendMessage(ChatColor.RED + "/team remove <team> <pseudo>");
                }
            }

            // Deposit command
            else if (args[0].equalsIgnoreCase("deposit") && sender instanceof Player) {
                // Check args
                if (args.length == 3) {
                    // Get team with this name
                    final PopolTeam team = PopolSurvival.getInstance().getTeam(args[1]);
                    if (team != null) {
                        // Check member
                        final PopolPlayer player = PopolServer.getInstance().getPlayer(((Player) sender).getUniqueId());
                        if (team.hasPlayer(player.getUUID())) {
                            // Check number
                            try {
                                final Long value = Long.parseLong(args[2]);

                                // Check player balance
                                sender.sendMessage(ChatColor.YELLOW + "Vérification de votre compte...");
                                PopolMoney.checkBalance(player, new BalanceCheckHandler() {
                                    @Override
                                    public void balanceChecked(Long money) {
                                        // Check value
                                        if (money >= value) {
                                            // Transfert money
                                            sender.sendMessage(ChatColor.YELLOW + "Transfert de " + value
                                                    + "₽ vers le compte de la team...");
                                            PopolMoney.updateBalance(player, money - value,
                                                    new BalanceUpdatedHandler() {
                                                        @Override
                                                        public void balanceUpdated(final Long money) {
                                                            // Get team balance
                                                            PopolTeamMoney.checkBalance(team,
                                                                    new BalanceCheckHandler() {
                                                                        @Override
                                                                        public void balanceChecked(Long teamMoney) {
                                                                            // Update balance
                                                                            PopolTeamMoney.updateBalance(team,
                                                                                    teamMoney + value,
                                                                                    new BalanceUpdatedHandler() {
                                                                                        @Override
                                                                                        public void balanceUpdated(
                                                                                                Long teamMoney) {
                                                                                            // Send new balances
                                                                                            sender.sendMessage(
                                                                                                    ChatColor.GREEN
                                                                                                            + "Nouveau solde de votre compte : "
                                                                                                            + money
                                                                                                            + "₽");
                                                                                            sender.sendMessage(
                                                                                                    ChatColor.GREEN
                                                                                                            + "Nouveau solde de votre team : "
                                                                                                            + teamMoney
                                                                                                            + "₽");
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        } else {
                                            // Error
                                            sender.sendMessage(ChatColor.RED
                                                    + "Erreur : vous n'avez pas assez de PopolMoney sur votre compte !");
                                        }

                                    }
                                });
                            } catch (NumberFormatException exception) {
                                // Error
                                sender.sendMessage(ChatColor.RED + "Erreur : quantité invalide !");
                            }
                        } else {
                            // Error
                            sender.sendMessage(ChatColor.RED + "Erreur : vous ne faites pas partie de cette team !");
                        }
                    } else {
                        // Error
                        sender.sendMessage(ChatColor.RED + "Erreur : cette team n'existe pas !");
                    }
                } else {
                    // Send help
                    sender.sendMessage(ChatColor.RED + "/team deposit <team> <valeur>");
                }
            }

            // Retrieve command
            else if (args[0].equalsIgnoreCase("retrieve") && sender instanceof Player) {
                // Check args
                if (args.length == 3) {
                    // Get team with this name
                    final PopolTeam team = PopolSurvival.getInstance().getTeam(args[1]);
                    if (team != null) {
                        // Check member
                        final PopolPlayer player = PopolServer.getInstance().getPlayer(((Player) sender).getUniqueId());
                        if (team.hasPlayer(player.getUUID())) {
                            // Check number
                            try {
                                final Long value = Long.parseLong(args[2]);

                                // Check team balance
                                sender.sendMessage(ChatColor.YELLOW + "Vérification du compte de cette team...");
                                PopolTeamMoney.checkBalance(team, new BalanceCheckHandler() {
                                    @Override
                                    public void balanceChecked(Long money) {
                                        // Check value
                                        if (money >= value) {
                                            // Transfert money
                                            sender.sendMessage(ChatColor.YELLOW + "Transfert de " + value
                                                    + "₽ vers votre compte...");
                                            PopolTeamMoney.updateBalance(team, money - value,
                                                    new BalanceUpdatedHandler() {
                                                        @Override
                                                        public void balanceUpdated(final Long money) {
                                                            // Get player balance
                                                            PopolMoney.checkBalance(player, new BalanceCheckHandler() {
                                                                @Override
                                                                public void balanceChecked(Long playerMoney) {
                                                                    // Update balance
                                                                    PopolMoney.updateBalance(player,
                                                                            playerMoney + value,
                                                                            new BalanceUpdatedHandler() {
                                                                                @Override
                                                                                public void balanceUpdated(
                                                                                        Long playerMoney) {
                                                                                    // Send new balances
                                                                                    sender.sendMessage(ChatColor.GREEN
                                                                                            + "Nouveau solde de votre compte : "
                                                                                            + playerMoney + "₽");
                                                                                    sender.sendMessage(ChatColor.GREEN
                                                                                            + "Nouveau solde de votre team : "
                                                                                            + money + "₽");
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                        }
                                                    });
                                        } else {
                                            // Error
                                            sender.sendMessage(ChatColor.RED
                                                    + "Erreur : il n'y a pas assez de PopolMoney sur le compte de cette team !");
                                        }

                                    }
                                });
                            } catch (NumberFormatException exception) {
                                // Error
                                sender.sendMessage(ChatColor.RED + "Erreur : quantité invalide !");
                            }
                        } else {
                            // Error
                            sender.sendMessage(ChatColor.RED + "Erreur : vous ne faites pas partie de cette team !");
                        }
                    } else {
                        // Error
                        sender.sendMessage(ChatColor.RED + "Erreur : cette team n'existe pas !");
                    }
                } else {
                    // Send help
                    sender.sendMessage(ChatColor.RED + "/team retrieve <team> <valeur>");
                }
            }

            // Leave command
            else if (args[0].equalsIgnoreCase("leave") && sender instanceof Player) {
                // Check args
                if (args.length == 2) {
                    // Get team with this name
                    PopolTeam team = PopolSurvival.getInstance().getTeam(args[1]);
                    if (team != null) {
                        // Check permissions
                        UUID player = ((Player) sender).getUniqueId();
                        if (!team.getRole(player).equals("owner")) {
                            // Remove it
                            sender.sendMessage(ChatColor.YELLOW + "Actualisation de la team...");
                            team.deletePlayer(player, new CompletionHandler<APITeam>() {
                                @Override
                                public void completionHandler(APITeam team, APIResponseStatus status) {
                                    // Check status
                                    if (status == APIResponseStatus.ok) {
                                        // Player was removed from team
                                        sender.sendMessage(
                                                ChatColor.GREEN + "Vous ne faites maintenant plus partie de la team "
                                                        + ChatColor.YELLOW + team.name + ChatColor.GREEN + " !");
                                    } else {
                                        // Error
                                        sender.sendMessage(ChatColor.RED + "Erreur inconnue !");
                                    }
                                }
                            });
                        } else {
                            // Error
                            sender.sendMessage(ChatColor.RED + "Erreur : vous ne pouvez pas quitter cette team !");
                        }
                    } else {
                        // Error
                        sender.sendMessage(ChatColor.RED + "Erreur : cette team n'existe pas !");
                    }
                } else {
                    // Send help
                    sender.sendMessage(ChatColor.RED + "/team leave <team>");
                }
            }

            // Delete command
            else if (args[0].equalsIgnoreCase("delete") && sender instanceof Player) {
                // Check args
                if (args.length == 2) {
                    // Get team with this name
                    final PopolTeam team = PopolSurvival.getInstance().getTeam(args[1]);
                    if (team != null) {
                        // Check permissions
                        UUID player = ((Player) sender).getUniqueId();
                        if (team.getRole(player).equals("owner")) {
                            // Delete it
                            sender.sendMessage(ChatColor.YELLOW + "Suppression de la team...");
                            team.delete(new CompletionHandler<APIMessage>() {
                                @Override
                                public void completionHandler(APIMessage message, APIResponseStatus status) {
                                    // Check status
                                    if (status == APIResponseStatus.ok) {
                                        // Team was deleted
                                        sender.sendMessage(ChatColor.GREEN + "La team " + ChatColor.YELLOW
                                                + team.getCached().name + ChatColor.GREEN + " a bien été supprimée !");
                                    } else {
                                        // Error
                                        sender.sendMessage(ChatColor.RED + "Erreur inconnue !");
                                    }
                                }
                            });
                        } else {
                            // Error
                            sender.sendMessage(ChatColor.RED + "Erreur : vous ne pouvez pas supprimer cette team !");
                        }
                    } else {
                        // Error
                        sender.sendMessage(ChatColor.RED + "Erreur : cette team n'existe pas !");
                    }
                } else {
                    // Send help
                    sender.sendMessage(ChatColor.RED + "/team leave <team>");
                }
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
                + ": Récupérer de la money sur le compte d'une team.\n" + ChatColor.GOLD + "/team leave <team> "
                + ChatColor.YELLOW + ": Quitter une team.\n" + ChatColor.GOLD + "/team delete <team> "
                + ChatColor.YELLOW + ": Supprimer une team.\n" + ChatColor.GOLD
                + "/team setrole <team> <pseudo> <player/admin> " + ChatColor.YELLOW
                + ": Modifier le rôle d'un joueur d'une team.");
    }

}
