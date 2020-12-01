package me.nathanfallet.popolsurvival.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.popolsurvival.utils.PopolJobMenu;

public class JobCommand implements CommandExecutor {

    // Process command
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check if sender is a player
        if (sender instanceof Player) {
            // Open menu
            PopolJobMenu.openMenu((Player) sender);
        }
        return true;
    }    

}
