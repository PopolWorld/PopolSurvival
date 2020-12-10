package me.nathanfallet.popolsurvival.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.RestrictedArea;

public class BlockPlace implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Check for protection
        RestrictedArea area = PopolSurvival.getInstance().getRestrictedArea(event.getBlock().getLocation());
        if (area != null && !area.isAllowed(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
    }

}
