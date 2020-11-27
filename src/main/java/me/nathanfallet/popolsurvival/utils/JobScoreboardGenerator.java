package me.nathanfallet.popolsurvival.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.nathanfallet.popolserver.utils.PopolPlayer;
import me.nathanfallet.popolserver.utils.ScoreboardGenerator;
import me.nathanfallet.popolsurvival.PopolSurvival;

public class JobScoreboardGenerator implements ScoreboardGenerator {

    @Override
    public List<String> generateLines(Player player, PopolPlayer pp) {
        // Create lines
        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.LIGHT_PURPLE + "");
        lines.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Job :");

        // Fetch job for player
        PopolJob job = PopolSurvival.getInstance().getActiveJob(player.getUniqueId());

        // Check if player has a job entry
        if (job != null) {
            // Information for this job
            lines.add(ChatColor.WHITE + job.getJobType().name + " (Lvl " + job.getLevel() + ")");
        } else {
            // No team
            lines.add(ChatColor.WHITE + "Aucun métier");
        }

        // Return them
        return lines;
    }

}
