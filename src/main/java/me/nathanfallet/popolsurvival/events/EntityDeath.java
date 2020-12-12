package me.nathanfallet.popolsurvival.events;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.JobType;
import me.nathanfallet.popolsurvival.utils.PopolJob;

public class EntityDeath implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // Get killer
        Player killer = event.getEntity().getKiller();

        // Check if player is defined
        if (killer != null) {
            // Get player job
            PopolJob job = PopolSurvival.getInstance().getActiveJob(killer.getUniqueId());

            // Calculate coefs
            int coef = (int) Math.floor(((double) event.getDroppedExp()) / 5.0);
            int modulo = (int) Math.floor(5 / ((double) coef));

            // Check job and coef
            if (job != null && job.getJobType().equals(JobType.HUNTER) && new Random().nextInt() % modulo == 0) {
                // Give one experience
                job.getPendingTransaction().add(coef);
            }
        }
    }

}
