package me.nathanfallet.popolsurvival.events;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.RestrictedArea;

public class EntityChangeBlock implements Listener {

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        // Check if block is in a protected area
        RestrictedArea area = PopolSurvival.getInstance().getRestrictedArea(event.getBlock().getLocation());
        if (area != null) {
            // If this is an act from an enderman
            if (event.getEntityType().equals(EntityType.ENDERMAN)) {
                // Cancel
                event.setCancelled(true);
            }
        }
    }
    
}
