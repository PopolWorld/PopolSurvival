package me.nathanfallet.popolsurvival.utils;

import java.util.List;
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
            // Get level from calculator
            level = getLevelFromExperience(getCached().experience);
        }
    }

    // Calculate level from experience
    public static long getLevelFromExperience(long experience) {
        if (experience > 1395) {
            return (long) (Math.ceil(Math.sqrt(72 * experience - 54215) + 325) / 18);
        }
        if (experience > 315) {
            return (long) Math.ceil(Math.sqrt(40 * experience - 7839) / 10 + 8.1);
        }
        if (experience > 0) {
            return (long) Math.ceil(Math.sqrt(experience + 9) - 3);
        }
        return 0;
    }

    // Interface for job loading
    public interface JobsLoaderHandler {

        void jobsLoaded(List<PopolJob> jobs);

    }

}
