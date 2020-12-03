package me.nathanfallet.popolsurvival.events;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.JobType;
import me.nathanfallet.popolsurvival.utils.PopolJob;

public class BlockBreak implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Check for protection

        // Check for a job
        PopolJob job = PopolSurvival.getInstance().getActiveJob(event.getPlayer().getUniqueId());
        if (job != null) {
            // Calculate eanerd experience
            long exp = 0;
            Material material = event.getBlock().getType();

            // Check for miner
            if (job.getJobType().equals(JobType.MINER)) {
                // Check for stones
                if (material.equals(Material.STONE) || material.equals(Material.ANDESITE)
                        || material.equals(Material.GRANITE) || material.equals(Material.DIORITE)) {
                    exp = new Random().nextInt() % 20 == 0 ? 1 : 0;
                }

                // Check for ores
                else if (material.equals(Material.COAL_ORE) || material.equals(Material.IRON_ORE)
                        || material.equals(Material.GOLD_ORE)) {
                    exp = 4;
                } else if (material.equals(Material.LAPIS_ORE) || material.equals(Material.REDSTONE_ORE)) {
                    exp = 6;
                } else if (material.equals(Material.DIAMOND_ORE) || material.equals(Material.EMERALD_ORE)) {
                    exp = 10;
                }
            }

            // Check for nether miner
            if (job.getJobType().equals(JobType.NETHER_MINER)) {
                // Check for stones
                if (material.equals(Material.NETHERRACK) || material.equals(Material.BASALT)
                        || material.equals(Material.BLACKSTONE) || material.equals(Material.DIORITE)) {
                    exp = new Random().nextInt() % 20 == 0 ? 1 : 0;
                }

                // Check for ores
                else if (material.equals(Material.NETHER_QUARTZ_ORE) || material.equals(Material.NETHER_GOLD_ORE)) {
                    exp = 4;
                } else if (material.equals(Material.ANCIENT_DEBRIS)) {
                    exp = 15;
                }
            }

            // Check for woodcutter
            else if (job.getJobType().equals(JobType.WOODCUTTER)) {
                // Check for logs
                if (material.equals(Material.OAK_LOG) || material.equals(Material.BIRCH_LOG)
                        || material.equals(Material.SPRUCE_LOG) || material.equals(Material.JUNGLE_LOG)
                        || material.equals(Material.ACACIA_LOG) || material.equals(Material.DARK_OAK_LOG)) {
                    exp = 6;
                }

                // Check for leaves
                else if (material.equals(Material.OAK_LEAVES) || material.equals(Material.BIRCH_LEAVES)
                        || material.equals(Material.SPRUCE_LEAVES) || material.equals(Material.JUNGLE_LEAVES)
                        || material.equals(Material.ACACIA_LEAVES) || material.equals(Material.DARK_OAK_LEAVES)) {
                    exp = 3;
                }
            }

            // Check for nether woodcutter
            else if (job.getJobType().equals(JobType.NETHER_WOODCUTTER)) {
                // Check for logs
                if (material.equals(Material.CRIMSON_STEM) || material.equals(Material.WARPED_STEM)) {
                    exp = 6;
                }

                // Check for leaves
                else if (material.equals(Material.NETHER_WART_BLOCK) || material.equals(Material.WARPED_WART_BLOCK)) {
                    exp = 3;
                }
            }

            // If not null, add experience to player
            if (exp > 0) {
                job.getPendingTransaction().add(exp);
            }
        }
    }

}
