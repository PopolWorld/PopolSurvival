package me.nathanfallet.popolsurvival.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.nathanfallet.popolsurvival.utils.PopolJobMenu;

public class InventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check for job menu
        PopolJobMenu.handleClick(event);
    }
    
}
