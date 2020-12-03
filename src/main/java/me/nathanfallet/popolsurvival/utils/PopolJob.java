package me.nathanfallet.popolsurvival.utils;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APIJob;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;
import me.nathanfallet.popolserver.utils.PopolMoney;
import me.nathanfallet.popolserver.utils.PopolMoney.BalanceCheckHandler;
import me.nathanfallet.popolserver.utils.PopolMoney.BalanceUpdatedHandler;
import me.nathanfallet.popolserver.utils.PopolPlayer;

public class PopolJob {

    // Properties
    private UUID playerUUID;
    private JobType job;

    // Cached current job
    private APIJob cached;

    // Cached level value
    private Long level;

    // Pending experience transaction
    private JobExperienceTransaction pendingTransaction;

    // Money per level constant
    public static final long moneyPerLevel = 50L;

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

    // Retrieve pending transaction
    public JobExperienceTransaction getPendingTransaction() {
        // If transaction doesn't exist, create it
        if (pendingTransaction == null) {
            pendingTransaction = new JobExperienceTransaction();
        }

        // Return it
        return pendingTransaction;
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

    // Send pending transaction if there is one, and clear it
    public void sendPendingTransactionAndClear() {
        // Check if a pending transaction is defined
        if (pendingTransaction != null && pendingTransaction.getAmount() > 0) {
            // Get current cached level and transaction amount
            final long previousCachedLevel = getLevel();
            long newExperience = (getCached() != null ? getCached().experience : 0) + pendingTransaction.getAmount();

            // Clear pending transaction
            pendingTransaction = null;

            // Send update
            put(new APIJob(null, job.toString().toLowerCase(), playerUUID.toString(), newExperience, null),
                    new CompletionHandler<APIJob>() {
                        @Override
                        public void completionHandler(APIJob object, APIResponseStatus status) {
                            // Get level changes
                            final long newLevels = getLevel() - previousCachedLevel;
                            if (newLevels > 0) {
                                // Earn money for each new level
                                final PopolPlayer player = PopolServer.getInstance().getPlayer(playerUUID);
                                PopolMoney.checkBalance(player, new BalanceCheckHandler() {
                                    @Override
                                    public void balanceChecked(Long money) {
                                        // Add money times new levels
                                        PopolMoney.updateBalance(player, money + moneyPerLevel * newLevels,
                                                new BalanceUpdatedHandler() {
                                                    @Override
                                                    public void balanceUpdated(Long money) {
                                                        // Notice player
                                                        Player player = Bukkit.getPlayer(playerUUID);
                                                        if (player != null && player.isOnline()) {
                                                            player.sendMessage(ChatColor.GREEN + "Vous venez de gagner "
                                                                    + newLevels + " niveau" + (newLevels > 1 ? "x" : "")
                                                                    + " dans votre métier de " + ChatColor.YELLOW
                                                                    + job.name + ChatColor.GREEN
                                                                    + ", avec une récompense de " + ChatColor.YELLOW
                                                                    + moneyPerLevel * newLevels + "₽ " + ChatColor.GREEN
                                                                    + " !");
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
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
