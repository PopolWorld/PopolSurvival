package me.nathanfallet.popolsurvival;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.utils.PopolPlayer;
import me.nathanfallet.popolserver.utils.ScoreboardLinesGenerator;
import me.nathanfallet.popolsurvival.commands.ChunkCommand;
import me.nathanfallet.popolsurvival.commands.FlyCommand;
import me.nathanfallet.popolsurvival.commands.TeamCommand;
import me.nathanfallet.popolsurvival.events.BlockBreak;
import me.nathanfallet.popolsurvival.events.BlockPlace;
import me.nathanfallet.popolsurvival.events.PlayerDeath;
import me.nathanfallet.popolsurvival.events.PlayerInteract;
import me.nathanfallet.popolsurvival.events.PlayerJoin;
import me.nathanfallet.popolsurvival.events.PlayerMove;

public class PopolSurvival extends JavaPlugin {

    // Static instance
    private static PopolSurvival instance;

    // Retrieve instance
    public static PopolSurvival getInstance() {
        return instance;
    }

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
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("team").setExecutor(new TeamCommand());

        // Add scoreboard lines
        PopolServer.getInstance().getScoreboardGenerators().add(new ScoreboardLinesGenerator() {
            @Override
            public List<String> generateLines(Player arg0, PopolPlayer arg1) {
                // Create lines
                List<String> lines = new ArrayList<>();
                lines.add(ChatColor.RED + "");
                lines.add(ChatColor.RED + "" + ChatColor.BOLD + "Team :");

                // Fetch team for player
                // TODO
                lines.add(ChatColor.WHITE + "Bientôt...");

                // Return them
                return lines;
            }
        });
    }

    @Override
    public void onDisable() {

    }

}
