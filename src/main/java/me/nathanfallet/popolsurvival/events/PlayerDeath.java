package me.nathanfallet.popolsurvival.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Check if player has keep inventory
        if (event.getEntity().hasPermission("popolsurvival.keepinventory")) {
            // Set keep inventory and clear drops
            event.setKeepInventory(true);
            event.getDrops().clear();
        }
    }
    
}
