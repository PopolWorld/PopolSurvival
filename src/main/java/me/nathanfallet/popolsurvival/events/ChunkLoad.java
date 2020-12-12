package me.nathanfallet.popolsurvival.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import me.nathanfallet.popolsurvival.PopolSurvival;

public class ChunkLoad implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        // If world is overworld
        if (event.getWorld().getName().equals("world")) {
            // Load region if necessary
            PopolSurvival.getInstance().loadRegion((long) event.getChunk().getX() >> 5,
                    (long) event.getChunk().getZ() >> 5, null);
        }
    }

}
