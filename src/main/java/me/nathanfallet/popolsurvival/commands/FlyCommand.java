package me.nathanfallet.popolsurvival.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check player
        if (sender instanceof Player) {
            // Cast to player
            Player player = (Player) sender;

            // Check permission
            if (player.hasPermission("popolsurvival.fly")) {
                // Toggle fly
                player.setFallDistance(0F);
                player.setAllowFlight(!player.getAllowFlight());
                if (!player.getAllowFlight()) {
                    player.setFlying(false);
                }

                // Send message
                player.sendMessage(ChatColor.GREEN + "Fly " + (player.getAllowFlight() ? "" : "des") + "activé !");
            } else {
                // Not allowed
                player.sendMessage(
                        ChatColor.RED + "Vous n'avez pas le niveau suffisant pour utiliser cette commande !");
            }
        }
        return true;
    }

}
