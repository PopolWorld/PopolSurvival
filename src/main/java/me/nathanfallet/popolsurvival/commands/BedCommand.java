package me.nathanfallet.popolsurvival.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check player
        if (sender instanceof Player) {
            // Cast to player
            Player player = (Player) sender;

            // Teleport player to its bed location
            Location bedSpawn = player.getBedSpawnLocation();
            if (bedSpawn != null) {
                // Teleport
                player.sendMessage(ChatColor.GOLD + "Téléportation vers votre lit...");
                player.teleport(bedSpawn);
            } else {
                // No valid bed location
                player.sendMessage(
                        ChatColor.RED + "Erreur : vous n'avez dormi dans aucun lit ou celui ci a été détruit !");
            }
        }
        return true;
    }

}
