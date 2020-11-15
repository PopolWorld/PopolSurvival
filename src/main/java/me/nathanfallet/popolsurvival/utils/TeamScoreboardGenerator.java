package me.nathanfallet.popolsurvival.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.nathanfallet.popolserver.utils.PopolPlayer;
import me.nathanfallet.popolserver.utils.ScoreboardGenerator;

public class TeamScoreboardGenerator implements ScoreboardGenerator {

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

}
