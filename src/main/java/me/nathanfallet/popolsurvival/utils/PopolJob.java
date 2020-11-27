package me.nathanfallet.popolsurvival.utils;

import java.util.UUID;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APIJob;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;

public class PopolJob {

    // Properties
    private UUID playerUUID;
    private JobType job;

    // Cached current job
    private APIJob cached;

    // Cached level value
    private Long level;

    // Constructor
    public PopolJob(UUID playerUUID, JobType job, APIJob cached) {
        // Set properties
        this.playerUUID = playerUUID;
        this.job = job;

        // Set cached data
        this.cached = cached;

        // Calculate level
        this.recalculateLevel();
    }

    // Retrieve player uuid
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    // Retrieve job
    public JobType getJobType() {
        return job;
    }

    // Retrieve cache
    public APIJob getCached() {
        return cached;
    }

    // Retrieve level
    public Long getLevel() {
        return level;
    }

    // Get update from API
    public void get(final CompletionHandler<APIJob> completionHandler) {
        // Fetch job data
        PopolServer.getInstance().getConnector().getJob(playerUUID.toString(), job.toString().toLowerCase(),
                new CompletionHandler<APIJob>() {
                    @Override
                    public void completionHandler(APIJob object, APIResponseStatus status) {
                        // Update cache
                        cached = object;
                        recalculateLevel();

                        // Call completion handler
                        completionHandler.completionHandler(object, status);
                    }
                });
    }

    // Send update to API
    public void put(APIJob job, final CompletionHandler<APIJob> completionHandler) {
        // Update job data
        PopolServer.getInstance().getConnector().putJob(job, new CompletionHandler<APIJob>() {
            @Override
            public void completionHandler(APIJob object, APIResponseStatus status) {
                // Update cache
                cached = object;
                recalculateLevel();

                // Call completion handler
                completionHandler.completionHandler(object, status);
            }
        });
    }

    // Recalculate level from experience
    public void recalculateLevel() {
        // Reset level
        level = 0L;

        // If cached experience is availble
        if (getCached() != null && getCached().experience != null) {
            // TODO: Find a way to convert exp to level

        }
    }

}
