package me.nathanfallet.popolsurvival.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.RestrictedArea;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if target is a block
        if (event.getClickedBlock() != null) {
            // Check for protection
            RestrictedArea area = PopolSurvival.getInstance().getRestrictedArea(event.getClickedBlock().getLocation());
            if (area != null && !area.isAllowed(event.getPlayer())) {
                event.setCancelled(true);
                return;
            }
        }
    }

}
