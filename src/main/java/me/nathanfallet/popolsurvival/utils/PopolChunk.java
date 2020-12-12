package me.nathanfallet.popolsurvival.utils;

import org.bukkit.entity.Player;

import me.nathanfallet.popolserver.api.APIChunk;
import me.nathanfallet.popolsurvival.PopolSurvival;

public class PopolChunk implements RestrictedArea {

    // Chunk price (constant)
    public static final Long price = 1000L;

    // Cached current chunk
    private APIChunk cached;

    // Constructor
    public PopolChunk(APIChunk cached) {
        this.cached = cached;
    }

    // Retrieve cache
    public APIChunk getCached() {
        return cached;
    }

    // Check is a player is allowed to interact with this region
    @Override
    public boolean isAllowed(Player player) {
        // Try to get team
        PopolTeam team = PopolSurvival.getInstance().getTeam(cached.teamId);

        // Check if team is loaded and if player is in this team
        return team != null && team.hasPlayer(player.getUniqueId());
    }

}
