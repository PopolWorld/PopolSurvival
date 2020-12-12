package me.nathanfallet.popolsurvival.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.popolsurvival.PopolSurvival;

public class SetRandomTPCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Check sender
		if (sender instanceof Player) {
			// Update random TP location
			Player p = (Player) sender;
			p.sendMessage(ChatColor.GREEN + "Le random TP a bien été définit !");
			PopolSurvival.getInstance().setRandomTP(p.getLocation());
		}
		return true;
	}

}
