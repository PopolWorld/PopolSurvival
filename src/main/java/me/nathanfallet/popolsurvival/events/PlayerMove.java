package me.nathanfallet.popolsurvival.events;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.RestrictedArea;

public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Check if to is different of from
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            // Player changed of block

            // Check for random tp location
            Location randomTP = PopolSurvival.getInstance().getRandomTP();
            if (randomTP != null && event.getTo().getWorld().getName().equals(randomTP.getWorld().getName())
                    && event.getTo().getBlockX() == randomTP.getBlockX()
                    && event.getTo().getBlockY() == randomTP.getBlockY()
                    && event.getTo().getBlockZ() == randomTP.getBlockZ()) {
                // Teleport player to a random world location
                event.getPlayer().sendMessage(ChatColor.GOLD + "Téléportation vers un endroit aléatoire...");
                Random random = new Random();
                int x = random.nextInt() % (1 << 16);
                int z = random.nextInt() % (1 << 16);
                event.getPlayer().teleport(randomTP.getWorld().getHighestBlockAt(x, z).getLocation());
                return;
            }

            // Get two regions and compare them
            RestrictedArea from = PopolSurvival.getInstance().getRestrictedArea(event.getFrom());
            RestrictedArea to = PopolSurvival.getInstance().getRestrictedArea(event.getTo());

            // Enter
            if (from == null && to != null) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "Vous entrez dans une zone protégée");
            }

            // Leave
            else if (from != null && to == null) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "Vous sortez de la zone protégée");
            }
        }
    }

}
