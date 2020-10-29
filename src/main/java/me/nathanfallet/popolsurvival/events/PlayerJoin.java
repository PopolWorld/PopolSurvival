package me.nathanfallet.popolsurvival.events;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Set gamemode
        event.getPlayer().setGameMode(GameMode.SURVIVAL);
    }

}
