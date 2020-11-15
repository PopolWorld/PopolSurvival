package me.nathanfallet.popolsurvival;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APITeam;
import me.nathanfallet.popolsurvival.commands.ChunkCommand;
import me.nathanfallet.popolsurvival.commands.FeedCommand;
import me.nathanfallet.popolsurvival.commands.FlyCommand;
import me.nathanfallet.popolsurvival.commands.TeamCommand;
import me.nathanfallet.popolsurvival.events.BlockBreak;
import me.nathanfallet.popolsurvival.events.BlockPlace;
import me.nathanfallet.popolsurvival.events.PlayerDeath;
import me.nathanfallet.popolsurvival.events.PlayerInteract;
import me.nathanfallet.popolsurvival.events.PlayerJoin;
import me.nathanfallet.popolsurvival.events.PlayerMove;
import me.nathanfallet.popolsurvival.utils.TeamLeaderboardGenerator;
import me.nathanfallet.popolsurvival.utils.TeamScoreboardGenerator;

public class PopolSurvival extends JavaPlugin {

    // Static instance
    private static PopolSurvival instance;

    // Retrieve instance
    public static PopolSurvival getInstance() {
        return instance;
    }

    // Properties
    private List<APITeam> teams;

    @Override
    public void onEnable() {
        // Store instance
        instance = this;

        // Register events
        Bukkit.getPluginManager().registerEvents(new BlockBreak(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlace(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteract(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMove(), this);

        // Register commands
        getCommand("chunk").setExecutor(new ChunkCommand());
        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("team").setExecutor(new TeamCommand());

        // Add scoreboard lines
        PopolServer.getInstance().getScoreboardGenerators().add(new TeamScoreboardGenerator());

        // Add leaderboards
        PopolServer.getInstance().getLeaderboardGenerators().put("teams", new TeamLeaderboardGenerator());
    }

    @Override
    public void onDisable() {
        // Clear teams
        teams = null;
    }

    // Retrieve teams
    public List<APITeam> getTeams() {
        // Init teams if needed
        if (teams == null) {
            teams = new ArrayList<>();
        }

        // Return teams
        return teams;
    }

}
