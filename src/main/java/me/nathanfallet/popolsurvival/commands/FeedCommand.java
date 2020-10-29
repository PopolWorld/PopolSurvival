package me.nathanfallet.popolsurvival.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check player
        if (sender instanceof Player) {
            // Cast to player
            Player player = (Player) sender;

            // Check permission
            if (player.hasPermission("popolsurvival.feed")) {
                // Feed
                player.setFoodLevel(20);
                player.setSaturation(10);
                player.setExhaustion(0F);

                // Send message
                player.sendMessage(ChatColor.GREEN + "Miam !");
            } else {
                // Not allowed
                player.sendMessage(
                        ChatColor.RED + "Vous n'avez pas le niveau suffisant pour utiliser cette commande !");
            }
        }
        return true;
    }

}
