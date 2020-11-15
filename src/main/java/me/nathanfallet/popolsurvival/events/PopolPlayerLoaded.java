package me.nathanfallet.popolsurvival.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.nathanfallet.popolserver.api.APITeam;
import me.nathanfallet.popolserver.events.PopolPlayerLoadedEvent;
import me.nathanfallet.popolsurvival.PopolSurvival;

public class PopolPlayerLoaded implements Listener {

    @EventHandler
    public void onPopolPlayerLoaded(PopolPlayerLoadedEvent event) {
        // Load teams for this player
        if (event.getPopolPlayer().getCached() != null && event.getPopolPlayer().getCached().teams != null) {
            // Iterate known teams
            for (APITeam team : event.getPopolPlayer().getCached().teams) {
                // Load team (if not loaded yet)
                PopolSurvival.getInstance().loadTeam(team.id, null);
            }
        }
    }

}
