package me.nathanfallet.popolsurvival.utils;

import java.util.UUID;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APIMessage;
import me.nathanfallet.popolserver.api.APIPlayer;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;
import me.nathanfallet.popolserver.api.APITeam;
import me.nathanfallet.popolsurvival.PopolSurvival;

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

    // Send delete to API
    public void delete(final CompletionHandler<APIMessage> completionHandler) {
        // Update team data
        PopolServer.getInstance().getConnector().deleteTeam(id, new CompletionHandler<APIMessage>() {
            @Override
            public void completionHandler(APIMessage object, APIResponseStatus status) {
                // Delete loaded object
                PopolSurvival.getInstance().unloadTeam(PopolTeam.this);

                // Call completion handler
                completionHandler.completionHandler(object, status);
            }
        });
    }

    // Post player to API
    public void postPlayer(UUID player, String role, final CompletionHandler<APITeam> completionHandler) {
        // Post data
        PopolServer.getInstance().getConnector().postTeamPlayer(id, player.toString(), role,
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
    public void deletePlayer(UUID player, final CompletionHandler<APITeam> completionHandler) {
        // Post data
        PopolServer.getInstance().getConnector().deleteTeamPlayer(id, player.toString(),
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

    // Put player to API
    public void putPlayer(UUID player, String role, final CompletionHandler<APITeam> completionHandler) {
        // Post data
        PopolServer.getInstance().getConnector().putTeamPlayer(id, player.toString(), role,
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
    public boolean hasPlayer(UUID player) {
        // Check that cache is loaded
        if (cached != null && cached.players != null) {
            // Iterate players
            for (APIPlayer p : cached.players) {
                // Check if id is the same
                if (p.uuid.equals(player.toString())) {
                    return true;
                }
            }
        }

        // Player is not in the team
        return false;
    }

    // Get role for a player
    public String getRole(UUID player) {
        // Check that cache is loaded
        if (cached != null && cached.players != null) {
            // Iterate players
            for (APIPlayer p : cached.players) {
                // Check if name is the same
                if (p.uuid.equals(player.toString())) {
                    return p.team_member.role;
                }
            }
        }

        // Player is not in the team
        return null;
    }

    // Interface for team loading
    public interface TeamLoaderHandler {

        void teamLoaded(PopolTeam team);

    }

}
