package me.nathanfallet.popolsurvival.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.popolserver.api.APIChunk;
import me.nathanfallet.popolserver.utils.PopolMoney.BalanceCheckHandler;
import me.nathanfallet.popolserver.utils.PopolMoney.BalanceUpdatedHandler;
import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.PopolChunk;
import me.nathanfallet.popolsurvival.utils.PopolChunkMap;
import me.nathanfallet.popolsurvival.utils.PopolRegion;
import me.nathanfallet.popolsurvival.utils.PopolRegion.ChunkLoaderHandler;
import me.nathanfallet.popolsurvival.utils.PopolRegion.ChunkUnloaderHandler;
import me.nathanfallet.popolsurvival.utils.PopolTeam;
import me.nathanfallet.popolsurvival.utils.PopolTeamMoney;

public class ChunkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        // Check args
        if (args.length != 0) {
            // Claim command
            if (args[0].equalsIgnoreCase("claim") && sender instanceof Player) {
                // Check args
                if (args.length == 2) {
                    // Get team with this name
                    final PopolTeam team = PopolSurvival.getInstance().getTeam(args[1]);
                    if (team != null) {
                        // Check if player is in this team
                        Player player = (Player) sender;
                        if (team.hasPlayer(player.getUniqueId())) {
                            // Check world
                            if (player.getLocation().getWorld().getName().equals("world")) {
                                // Check if chunk is already claimed
                                // Start by getting the region
                                final long x = player.getLocation().getChunk().getX();
                                final long z = player.getLocation().getChunk().getZ();
                                PopolRegion originalRegion = PopolSurvival.getInstance().getRegion(x >> 5, z >> 5);

                                // If the region doesn't exist, create it
                                if (originalRegion == null) {
                                    // Create region and add it
                                    originalRegion = new PopolRegion(x >> 5, z >> 5, new ArrayList<APIChunk>());
                                    PopolSurvival.getInstance().getRegions().add(originalRegion);
                                }

                                // Check if chunk is already claimed
                                final PopolRegion region = originalRegion;
                                PopolChunk chunk = region.getChunk(x, z);
                                if (chunk == null) {
                                    // Check team account
                                    PopolTeamMoney.checkBalance(team, new BalanceCheckHandler() {
                                        @Override
                                        public void balanceChecked(Long money) {
                                            // Check if team has enough to buy this chunk
                                            if (money >= PopolChunk.price) {
                                                // Update balance
                                                sender.sendMessage(ChatColor.YELLOW + "Claim du chunk en cours...");
                                                PopolTeamMoney.updateBalance(team, money - PopolChunk.price,
                                                        new BalanceUpdatedHandler() {
                                                            @Override
                                                            public void balanceUpdated(final Long money) {
                                                                // Claim chunk
                                                                region.claimChunk(x, z, team, new ChunkLoaderHandler() {
                                                                    @Override
                                                                    public void chunkLoaded(APIChunk chunk) {
                                                                        // Send new money
                                                                        sender.sendMessage(ChatColor.GREEN
                                                                                + "Le chunk appartient désormais à votre team !");
                                                                        sender.sendMessage(ChatColor.GREEN
                                                                                + "Nouveau solde de votre team : "
                                                                                + money + "₽");
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
                                } else {
                                    // Error
                                    sender.sendMessage(ChatColor.RED + "Erreur : ce chunk est déjà claim !");
                                }
                            } else {
                                // Error
                                sender.sendMessage(ChatColor.RED + "Erreur : ce monde n'est pas disponible au claim !");
                            }
                        } else {
                            // Error
                            sender.sendMessage(ChatColor.RED + "Erreur : vous n'êtes pas membre de cette team !");
                        }
                    } else {
                        // Error
                        sender.sendMessage(ChatColor.RED + "Erreur : cette team n'existe pas !");
                    }
                } else {
                    // Send help
                    sender.sendMessage(ChatColor.RED + "/chunk claim <team>");
                }
            }

            // Unclaim command
            else if (args[0].equalsIgnoreCase("unclaim") && sender instanceof Player) {
                // Get player
                Player player = (Player) sender;

                // Check world
                if (player.getLocation().getWorld().getName().equals("world")) {
                    // Get region from coordinates
                    long x = player.getLocation().getChunk().getX();
                    long z = player.getLocation().getChunk().getZ();
                    PopolRegion region = PopolSurvival.getInstance().getRegion(x >> 5, z >> 5);
                    if (region != null) {
                        // Get chunk in region
                        PopolChunk chunk = region.getChunk(x, z);
                        if (chunk != null) {
                            // Check team for this chunk
                            PopolTeam team = PopolSurvival.getInstance().getTeam(chunk.getCached().teamId);
                            if (team != null && team.hasPlayer(player.getUniqueId())) {
                                // Unclaim chunk
                                region.unclaimChunk(x, z, new ChunkUnloaderHandler() {
                                    @Override
                                    public void chunkUnloaded() {
                                        // Chunk unclaimed
                                        sender.sendMessage(ChatColor.GREEN + "Le chunk a bien été libéré !");
                                    }
                                });
                            } else {
                                // Error
                                sender.sendMessage(ChatColor.RED
                                        + "Erreur : vous n'êtes pas membre de la team qui possède ce chunk !");
                            }
                        } else {
                            // Error
                            sender.sendMessage(ChatColor.RED + "Erreur : ce chunk n'est pas encore claim !");
                        }
                    } else {
                        // Error
                        sender.sendMessage(ChatColor.RED + "Erreur : ce chunk n'est pas encore claim !");
                    }
                } else {
                    // Error
                    sender.sendMessage(ChatColor.RED + "Erreur : ce monde n'est pas disponible au claim !");
                }
            }

            // Map command
            else if (args[0].equalsIgnoreCase("map") && sender instanceof Player) {
                // Get player
                Player player = (Player) sender;

                // Check world
                if (player.getLocation().getWorld().getName().equals("world")) {
                    // Open menu
                    PopolChunkMap.openMenu(player);
                } else {
                    // Error
                    sender.sendMessage(ChatColor.RED + "Erreur : ce monde n'est pas disponible au claim !");
                }
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
        sender.sendMessage(ChatColor.YELLOW + "----- " + ChatColor.GOLD + "Aide du /chunk " + ChatColor.YELLOW
                + "-----\n" + ChatColor.GOLD + "/chunk claim <team> " + ChatColor.YELLOW
                + ": Claim un chunk pour une team.\n" + ChatColor.GOLD + "/chunk unclaim " + ChatColor.YELLOW
                + ": Libère un chunk.\n" + ChatColor.GOLD + "/chunk map " + ChatColor.YELLOW
                + ": Affiche la carte des chunks alentours.");
    }

}
