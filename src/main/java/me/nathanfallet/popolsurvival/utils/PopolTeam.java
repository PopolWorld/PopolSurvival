package me.nathanfallet.popolsurvival.utils;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APIPlayer;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;
import me.nathanfallet.popolserver.api.APITeam;
import me.nathanfallet.popolserver.utils.PopolPlayer;

public class PopolTeam {

    // Properties
    private Long id;

    // Cached current team
    private APITeam cached;

    // Constructor
    public PopolTeam(Long id, APITeam cached) {
        // Set id
        this.id = id;

        // Set cached data
        this.cached = cached;
    }

    // Retrieve id
    public Long getId() {
        return id;
    }

    // Retrieve cache
    public APITeam getCached() {
        return cached;
    }

    // Get update from API
    public void get(final CompletionHandler<APITeam> completionHandler) {
        // Fetch team data
        PopolServer.getInstance().getConnector().getTeam(id, new CompletionHandler<APITeam>() {
            @Override
            public void completionHandler(APITeam object, APIResponseStatus status) {
                // Update cache
                cached = object;

                // Call completion handler
                completionHandler.completionHandler(object, status);
            }
        });
    }

    // Send update to API
    public void put(APITeam team, final CompletionHandler<APITeam> completionHandler) {
        // Update team data
        PopolServer.getInstance().getConnector().putTeam(team, new CompletionHandler<APITeam>() {
            @Override
            public void completionHandler(APITeam object, APIResponseStatus status) {
                // Update cache
                cached = object;

                // Call completion handler
                completionHandler.completionHandler(object, status);
            }
        });
    }

    // Post player to API
    public void postPlayer(PopolPlayer player, String role, final CompletionHandler<APITeam> completionHandler) {
        // Post data
        PopolServer.getInstance().getConnector().postTeamPlayer(id, player.getUUID().toString(), role,
                new CompletionHandler<APITeam>() {
                    @Override
                    public void completionHandler(APITeam object, APIResponseStatus status) {
                        // Update cache
                        cached = object;

                        // Call completion handler
                        completionHandler.completionHandler(object, status);
                    }
                });
    }

    // Delete player from API
    public void deletePlayer(PopolPlayer player, final CompletionHandler<APITeam> completionHandler) {
        // Post data
        PopolServer.getInstance().getConnector().deleteTeamPlayer(id, player.getUUID().toString(),
                new CompletionHandler<APITeam>() {
                    @Override
                    public void completionHandler(APITeam object, APIResponseStatus status) {
                        // Update cache
                        cached = object;

                        // Call completion handler
                        completionHandler.completionHandler(object, status);
                    }
                });
    }

    // Check if a player is in the team
    public boolean hasPlayer(PopolPlayer player) {
        // Check that cache is loaded
        if (cached != null && cached.players != null) {
            // Iterate players
            for (APIPlayer p : cached.players) {
                // Check if id is the same
                if (p.uuid.equals(player.getUUID().toString())) {
                    return true;
                }
            }
        }

        // Player is not in the team
        return false;
    }

    // Interface for team loading
    public interface TeamLoaderHandler {

        void teamLoaded(PopolTeam team);

    }

}
