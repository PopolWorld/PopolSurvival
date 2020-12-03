package me.nathanfallet.popolsurvival;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APIJob;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;
import me.nathanfallet.popolserver.api.APITeam;
import me.nathanfallet.popolserver.api.APITeamCreation;
import me.nathanfallet.popolsurvival.commands.ChunkCommand;
import me.nathanfallet.popolsurvival.commands.FeedCommand;
import me.nathanfallet.popolsurvival.commands.FlyCommand;
import me.nathanfallet.popolsurvival.commands.JobCommand;
import me.nathanfallet.popolsurvival.commands.TeamCommand;
import me.nathanfallet.popolsurvival.events.BlockBreak;
import me.nathanfallet.popolsurvival.events.BlockPlace;
import me.nathanfallet.popolsurvival.events.EntityDeath;
import me.nathanfallet.popolsurvival.events.InventoryClick;
import me.nathanfallet.popolsurvival.events.PlayerDeath;
import me.nathanfallet.popolsurvival.events.PlayerInteract;
import me.nathanfallet.popolsurvival.events.PlayerJoin;
import me.nathanfallet.popolsurvival.events.PlayerMove;
import me.nathanfallet.popolsurvival.events.PopolPlayerLoaded;
import me.nathanfallet.popolsurvival.utils.JobLeaderboardGenerator;
import me.nathanfallet.popolsurvival.utils.JobScoreboardGenerator;
import me.nathanfallet.popolsurvival.utils.JobType;
import me.nathanfallet.popolsurvival.utils.PopolJob;
import me.nathanfallet.popolsurvival.utils.PopolJob.JobsLoaderHandler;
import me.nathanfallet.popolsurvival.utils.PopolTeam;
import me.nathanfallet.popolsurvival.utils.PopolTeam.TeamLoaderHandler;
import me.nathanfallet.popolsurvival.utils.TeamLeaderboardGenerator;
import me.nathanfallet.popolsurvival.utils.TeamScoreboardGenerator;

public class PopolSurvival extends JavaPlugin {

    /**
     * Static instance
     */

    // Static instance
    private static PopolSurvival instance;

    // Retrieve instance
    public static PopolSurvival getInstance() {
        return instance;
    }

    /**
     * Properties
     */

    // Properties
    private List<PopolTeam> teams;
    private List<PopolJob> jobs;

    /**
     * Plugin enable/disable
     */

    @Override
    public void onEnable() {
        // Store instance
        instance = this;

        // Register events
        Bukkit.getPluginManager().registerEvents(new BlockBreak(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlace(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeath(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteract(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMove(), this);
        Bukkit.getPluginManager().registerEvents(new PopolPlayerLoaded(), this);

        // Register commands
        getCommand("chunk").setExecutor(new ChunkCommand());
        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("job").setExecutor(new JobCommand());
        getCommand("team").setExecutor(new TeamCommand());

        // Add scoreboard lines
        PopolServer.getInstance().getScoreboardGenerators().add(new TeamScoreboardGenerator());
        PopolServer.getInstance().getScoreboardGenerators().add(new JobScoreboardGenerator());

        // Add leaderboards
        PopolServer.getInstance().getLeaderboardGenerators().put("teams", new TeamLeaderboardGenerator());
        PopolServer.getInstance().getLeaderboardGenerators().put("jobs", new JobLeaderboardGenerator());

        // Process experience transactions
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
            @Override
            public void run() {
                // Iterate jobs
                for (PopolJob job : getJobs()) {
                    job.sendPendingTransactionAndClear();
                }
            }
        }, 300, 300);
    }

    @Override
    public void onDisable() {
        // Clear teams
        teams = null;

        // Clear jobs
        jobs = null;
    }

    /**
     * Teams
     */

    // Retrieve teams
    public List<PopolTeam> getTeams() {
        // Init teams if needed
        if (teams == null) {
            teams = new ArrayList<>();
        }

        // Return teams
        return teams;
    }

    // Retrieve a team from its id
    public PopolTeam getTeam(Long id) {
        // Iterate teams
        for (PopolTeam team : getTeams()) {
            if (team.getId().equals(id)) {
                return team;
            }
        }

        // No team found
        return null;
    }

    // Retrieve a team from its name
    public PopolTeam getTeam(String name) {
        // Iterate teams
        for (PopolTeam team : getTeams()) {
            if (team.getCached() != null && team.getCached().name.equalsIgnoreCase(name)) {
                return team;
            }
        }

        // No team found
        return null;
    }

    // Retrieve teams for a player
    public List<PopolTeam> getTeams(UUID player) {
        // Create a list
        List<PopolTeam> list = new ArrayList<>();

        // Iterate teams
        for (PopolTeam team : getTeams()) {
            // Check if player is in this team
            if (team.hasPlayer(player)) {
                list.add(team);
            }
        }

        // Return list
        return list;
    }

    // Load a team
    public void loadTeam(Long id, final TeamLoaderHandler handler) {
        // Check if team is not already loaded
        PopolTeam loaded = getTeam(id);
        if (loaded != null) {
            if (handler != null) {
                handler.teamLoaded(loaded);
            }
            return;
        }

        // Fetch from API
        PopolServer.getInstance().getConnector().getTeam(id, new CompletionHandler<APITeam>() {
            @Override
            public void completionHandler(APITeam object, APIResponseStatus status) {
                // Check status
                if (status == APIResponseStatus.ok) {
                    // Load it
                    PopolTeam team = new PopolTeam(object.id, object);

                    // Add it to storage
                    getTeams().add(team);

                    // Call handler
                    if (handler != null) {
                        handler.teamLoaded(team);
                    }
                } else {
                    // Error, team doesn't exist
                    if (handler != null) {
                        handler.teamLoaded(null);
                    }
                }
            }
        });
    }

    // Unload a team
    public void unloadTeam(PopolTeam team) {
        // Remove object from storage
        getTeams().remove(team);
    }

    // Create a team
    public void createTeam(String name, UUID owner, final TeamLoaderHandler handler) {
        // Create team to API
        PopolServer.getInstance().getConnector().postTeam(new APITeamCreation(name, owner.toString()),
                new CompletionHandler<APITeam>() {
                    @Override
                    public void completionHandler(APITeam object, APIResponseStatus status) {
                        // Check if team was created
                        if (status == APIResponseStatus.created) {
                            // Team was created, load it
                            PopolTeam team = new PopolTeam(object.id, object);

                            // Add it to storage
                            getTeams().add(team);

                            // Call handler
                            if (handler != null) {
                                handler.teamLoaded(team);
                            }
                        } else {
                            // Error, name is already taken
                            if (handler != null) {
                                handler.teamLoaded(null);
                            }
                        }
                    }
                });
    }

    /**
     * Jobs
     */

    // Retrieve jobs
    public List<PopolJob> getJobs() {
        // Init jobs if needed
        if (jobs == null) {
            jobs = new ArrayList<>();
        }

        // Return jobs
        return jobs;
    }

    // Retrieve jobs for a player
    public List<PopolJob> getJobs(UUID player) {
        // Create a list
        List<PopolJob> list = new ArrayList<>();

        // Iterate jobs
        for (PopolJob job : getJobs()) {
            // Check if this job is for this player
            if (job.getPlayerUUID().equals(player)) {
                list.add(job);
            }
        }

        // Return list
        return list;
    }

    // Retrieve active job for a player
    public PopolJob getJob(UUID player, JobType type) {
        // Iterate jobs
        for (PopolJob job : getJobs()) {
            // Check if this job is for this player and this type
            if (job.getPlayerUUID().equals(player) && job.getJobType().equals(type)) {
                // In that case return it
                return job;
            }
        }

        // No job found
        return null;
    }

    // Retrieve active job for a player
    public PopolJob getActiveJob(UUID player) {
        // Iterate jobs
        for (PopolJob job : getJobs()) {
            // Check if this job is for this player and is active
            if (job.getPlayerUUID().equals(player) && job.getCached() != null && job.getCached().active != null
                    && job.getCached().active.booleanValue()) {
                // In that case return it
                return job;
            }
        }

        // No job found
        return null;
    }

    // Load jobs for a player
    public void loadJobs(final UUID player, final JobsLoaderHandler handler) {
        // Check if jobs for this player are not already loaded
        final List<PopolJob> loaded = getJobs(player);
        if (!loaded.isEmpty()) {
            if (handler != null) {
                handler.jobsLoaded(loaded);
            }
            return;
        }

        // Fetch from API
        PopolServer.getInstance().getConnector().getJobs(player.toString(), new CompletionHandler<APIJob[]>() {
            @Override
            public void completionHandler(APIJob[] object, APIResponseStatus status) {
                // Check status
                if (status == APIResponseStatus.ok) {
                    // Load it
                    for (APIJob job : object) {
                        loaded.add(new PopolJob(player, JobType.valueOf(job.job.toUpperCase()), job));
                    }

                    // Add it to storage
                    getJobs().addAll(loaded);

                    // Call handler
                    if (handler != null) {
                        handler.jobsLoaded(loaded);
                    }
                } else {
                    // Error, player doesn't exist
                    if (handler != null) {
                        handler.jobsLoaded(null);
                    }
                }
            }
        });
    }

    // Unload a job
    public void unloadJob(PopolJob job) {
        // Remove object from storage
        getJobs().remove(job);
    }

    // Create a job
    public void createJob(final JobType jobType, final UUID player, final JobsLoaderHandler handler) {
        // Create team to API
        PopolServer.getInstance().getConnector().postJob(player.toString(), jobType.toString().toLowerCase(),
                new CompletionHandler<APIJob>() {
                    @Override
                    public void completionHandler(APIJob object, APIResponseStatus status) {
                        // Check if job was created
                        if (status == APIResponseStatus.created) {
                            // Job was created, load it
                            PopolJob job = new PopolJob(player, jobType, object);

                            // Mark any active job as not active anymore
                            PopolJob active = getActiveJob(player);
                            if (active != null) {
                                active.getCached().active = false;
                            }

                            // Add it to storage
                            getJobs().add(job);

                            // Call handler
                            if (handler != null) {
                                handler.jobsLoaded(Arrays.asList(job));
                            }
                        } else {
                            // Error, name is already taken
                            if (handler != null) {
                                handler.jobsLoaded(null);
                            }
                        }
                    }
                });
    }

}
