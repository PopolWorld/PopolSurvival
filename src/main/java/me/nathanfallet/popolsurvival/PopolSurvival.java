package me.nathanfallet.popolsurvival;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APIChunk;
import me.nathanfallet.popolserver.api.APIJob;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;
import me.nathanfallet.popolserver.api.APITeam;
import me.nathanfallet.popolserver.api.APITeamCreation;
import me.nathanfallet.popolsurvival.commands.ChunkCommand;
import me.nathanfallet.popolsurvival.commands.FeedCommand;
import me.nathanfallet.popolsurvival.commands.FlyCommand;
import me.nathanfallet.popolsurvival.commands.JobCommand;
import me.nathanfallet.popolsurvival.commands.SetRandomTPCommand;
import me.nathanfallet.popolsurvival.commands.TeamCommand;
import me.nathanfallet.popolsurvival.events.BlockBreak;
import me.nathanfallet.popolsurvival.events.BlockPlace;
import me.nathanfallet.popolsurvival.events.ChunkLoad;
import me.nathanfallet.popolsurvival.events.EnchantItem;
import me.nathanfallet.popolsurvival.events.EntityChangeBlock;
import me.nathanfallet.popolsurvival.events.EntityDeath;
import me.nathanfallet.popolsurvival.events.EntityExplode;
import me.nathanfallet.popolsurvival.events.InventoryClick;
import me.nathanfallet.popolsurvival.events.PlayerDeath;
import me.nathanfallet.popolsurvival.events.PlayerFish;
import me.nathanfallet.popolsurvival.events.PlayerInteract;
import me.nathanfallet.popolsurvival.events.PlayerJoin;
import me.nathanfallet.popolsurvival.events.PlayerMove;
import me.nathanfallet.popolsurvival.events.PopolPlayerLoaded;
import me.nathanfallet.popolsurvival.utils.JobLeaderboardGenerator;
import me.nathanfallet.popolsurvival.utils.JobScoreboardGenerator;
import me.nathanfallet.popolsurvival.utils.JobType;
import me.nathanfallet.popolsurvival.utils.PopolChunk;
import me.nathanfallet.popolsurvival.utils.PopolJob;
import me.nathanfallet.popolsurvival.utils.PopolJob.JobsLoaderHandler;
import me.nathanfallet.popolsurvival.utils.PopolRegion;
import me.nathanfallet.popolsurvival.utils.PopolRegion.RegionLoaderHandler;
import me.nathanfallet.popolsurvival.utils.PopolTeam;
import me.nathanfallet.popolsurvival.utils.PopolTeam.TeamLoaderHandler;
import me.nathanfallet.popolsurvival.utils.RestrictedArea;
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
    private Location randomTP;
    private List<PopolTeam> teams;
    private List<PopolJob> jobs;
    private List<PopolRegion> regions;

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
        Bukkit.getPluginManager().registerEvents(new ChunkLoad(), this);
        Bukkit.getPluginManager().registerEvents(new EnchantItem(), this);
        Bukkit.getPluginManager().registerEvents(new EntityChangeBlock(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeath(), this);
        Bukkit.getPluginManager().registerEvents(new EntityExplode(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerFish(), this);
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
        getCommand("setrandomtp").setExecutor(new SetRandomTPCommand());

        // Add scoreboard lines
        PopolServer.getInstance().getScoreboardGenerators().add(new TeamScoreboardGenerator());
        PopolServer.getInstance().getScoreboardGenerators().add(new JobScoreboardGenerator());

        // Add leaderboards
        PopolServer.getInstance().getLeaderboardGenerators().put("teams", new TeamLeaderboardGenerator());
        PopolServer.getInstance().getLeaderboardGenerators().put("jobs", new JobLeaderboardGenerator());

        // Process experience transactions
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
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
     * Random TP location
     */

    // Get random TP location
    public Location getRandomTP() {
        // Check if spawn is loaded
        if (randomTP == null) {
            // Get file
            File f = new File(getDataFolder(), "random_tp.yml");

            // Return default randomTP location if it doesn't exist
            if (!f.exists()) {
                return null;
            }

            // Else, read from file
            FileConfiguration config = YamlConfiguration.loadConfiguration(f);
            randomTP = new Location(Bukkit.getWorld(config.getString("world")), config.getDouble("x"),
                    config.getDouble("y"), config.getDouble("z"));
                    randomTP.setYaw(config.getLong("yaw"));
                    randomTP.setPitch(config.getLong("pitch"));
        }

        // Return random TP location
        return randomTP;
    }

    // Set random TP location
    public void setRandomTP(Location randomTP) {
        // Update loaded spawn
        this.randomTP = randomTP;

        // Get file
        File f = new File(getDataFolder(), "random_tp.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        // Set location
        config.set("world", randomTP.getWorld().getName());
        config.set("x", randomTP.getX());
        config.set("y", randomTP.getY());
        config.set("z", randomTP.getZ());
        config.set("yaw", randomTP.getYaw());
        config.set("pitch", randomTP.getPitch());

        // Save
        try {
            config.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /**
     * Regions
     */

    // Retrieve regions
    public List<PopolRegion> getRegions() {
        // Init regions if needed
        if (regions == null) {
            regions = new ArrayList<>();
        }

        // Return regions
        return regions;
    }

    // Retrieve a region by coordinates
    public PopolRegion getRegion(Long x, Long z) {
        // Iterate regions
        for (PopolRegion region : getRegions()) {
            if (region.getX().equals(x) && region.getZ().equals(z)) {
                return region;
            }
        }

        // No region found
        return null;
    }

    // Load a region
    public void loadRegion(final Long x, final Long z, final RegionLoaderHandler handler) {
        // Check if region is not already loaded
        PopolRegion loaded = getRegion(x, z);
        if (loaded != null) {
            if (handler != null) {
                handler.regionLoaded(loaded);
            }
            return;
        }

        // Init region and add it to storage (avoid load it while its loading)
        final PopolRegion region = new PopolRegion(x, z, new ArrayList<APIChunk>());
        getRegions().add(region);

        // Fetch from API
        PopolServer.getInstance().getConnector().getChunks(x, z, new CompletionHandler<APIChunk[]>() {
            @Override
            public void completionHandler(APIChunk[] object, APIResponseStatus status) {
                // Check status
                if (status == APIResponseStatus.ok) {
                    // Add chunks to region
                    List<PopolChunk> chunks = new ArrayList<>();
                    for (APIChunk chunk : object) {
                        chunks.add(new PopolChunk(chunk));
                    }
                    region.getChunks().addAll(chunks);

                    // Call handler
                    if (handler != null) {
                        handler.regionLoaded(region);
                    }
                } else {
                    // Error
                    if (handler != null) {
                        handler.regionLoaded(null);
                    }
                }
            }
        });
    }

    // Unload a region
    public void unloadRegion(PopolRegion region) {
        // Remove object from storage
        getRegions().remove(region);
    }

    /**
     * Restricted areas
     */

    // Get a restricted area from a location
    public RestrictedArea getRestrictedArea(Location location) {
        // Check world
        switch (location.getWorld().getName()) {
            // Default world, check for chunks
            case "world":
                // Get region for coordinates
                long x = location.getChunk().getX();
                long z = location.getChunk().getZ();

                // Get region
                PopolRegion region = getRegion(x >> 5, z >> 5);
                if (region != null) {
                    // Get chunk (if exists, else return null)
                    return region.getChunk(x, z);
                }
        }

        // No area found
        return null;
    }

}
