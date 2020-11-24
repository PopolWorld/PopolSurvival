package me.nathanfallet.popolsurvival.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.nathanfallet.popolserver.utils.PopolPlayer;
import me.nathanfallet.popolserver.utils.ScoreboardGenerator;
import me.nathanfallet.popolsurvival.PopolSurvival;

public class TeamScoreboardGenerator implements ScoreboardGenerator {

    @Override
    public List<String> generateLines(Player player, PopolPlayer pp) {
        // Create lines
        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.RED + "");
        lines.add(ChatColor.RED + "" + ChatColor.BOLD + "Team :");

        // Fetch teams for player
        List<PopolTeam> teams = PopolSurvival.getInstance().getTeams(pp.getUUID());

        // Check number of teams
        if (teams.isEmpty()) {
            // No team
            lines.add(ChatColor.WHITE + "Aucune team");
        } else if (teams.size() > 1) {
            // More than 1, just count
            lines.add(ChatColor.WHITE + "" + teams.size() + " teams");
        } else {
            // List teams
            for (PopolTeam team : teams) {
                lines.add(ChatColor.WHITE + (team.getCached() != null ? team.getCached().name : "Chargement..."));
            }
        }

        // Return them
        return lines;
    }

}
