package me.nathanfallet.popolsurvival;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.utils.PopolPlayer;
import me.nathanfallet.popolserver.utils.ScoreboardLinesGenerator;
import me.nathanfallet.popolsurvival.commands.TeamCommand;

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

        // Register commands
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
