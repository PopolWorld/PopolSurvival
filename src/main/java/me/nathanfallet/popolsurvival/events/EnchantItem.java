package me.nathanfallet.popolsurvival.events;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.JobType;
import me.nathanfallet.popolsurvival.utils.PopolJob;

public class EnchantItem implements Listener {

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        // Get player job
        PopolJob job = PopolSurvival.getInstance().getActiveJob(event.getEnchanter().getUniqueId());

        // Calculate coefs
        int coef = (int) Math.floor(((double) event.getExpLevelCost()) / 10.0);
        int modulo = (int) Math.floor(5 / ((double) coef));

        // Check job and coef
        if (job != null && job.getJobType().equals(JobType.ENCHANTER) && new Random().nextInt() % modulo == 0) {
            // Give one experience
            job.getPendingTransaction().add(coef);
        }
    }
    
}
