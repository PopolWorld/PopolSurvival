package me.nathanfallet.popolsurvival.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.RestrictedArea;

public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Check if to is different of from
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            // Player changed of block
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
