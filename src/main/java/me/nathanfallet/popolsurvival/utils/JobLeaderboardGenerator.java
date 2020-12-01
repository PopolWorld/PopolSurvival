package me.nathanfallet.popolsurvival.utils;

import java.util.ArrayList;
import java.util.List;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APIJob;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;
import me.nathanfallet.popolserver.utils.LeaderboardGenerator;

public class JobLeaderboardGenerator implements LeaderboardGenerator, CompletionHandler<APIJob[]> {

    // Cached leaderboard
    private List<String> cache;
    private long lastRefresh = 0L;

    @Override
    public List<String> getLines(int limit) {
        // If cache is null, create an empty list (avoid null pointer exceptions)
        if (cache == null) {
            cache = new ArrayList<>();
        }

        // Check for refresh (every 60 seconds)
        if (System.currentTimeMillis() - lastRefresh >= 60_000L) {
            // Update last refresh to now
            lastRefresh = System.currentTimeMillis();

            // Generate lines for this leaderboard
            PopolServer.getInstance().getConnector().getJobLeaderboard(limit, this);
        }

        // Return cached data
        return cache;
    }

    @Override
    public String getTitle() {
        // Title for this leaderboard
        return "Jobs";
    }

    @Override
    public void completionHandler(APIJob[] object, APIResponseStatus status) {
        // Check status
        if (status == APIResponseStatus.ok) {
            // Clear cache
            cache = new ArrayList<>();

            // Fill cache with new data
            for (APIJob job : object) {
                cache.add(job.player.name + " - " + JobType.valueOf(job.job.toUpperCase()).name + " (Lvl "
                        + PopolJob.getLevelFromExperience(job.experience) + ")");
            }
        }
    }

}
