package me.nathanfallet.popolsurvival.events;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.JobType;
import me.nathanfallet.popolsurvival.utils.PopolJob;
import me.nathanfallet.popolsurvival.utils.RestrictedArea;

public class BlockBreak implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Check for protection
        RestrictedArea area = PopolSurvival.getInstance().getRestrictedArea(event.getBlock().getLocation());
        if (area != null && !area.isAllowed(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }

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
                    exp = new Random().nextInt() % 50 == 0 ? 1 : 0;
                }

                // Check for ores
                else if (material.equals(Material.COAL_ORE) || material.equals(Material.IRON_ORE)
                        || material.equals(Material.GOLD_ORE)) {
                    exp = 1;
                } else if (material.equals(Material.LAPIS_ORE) || material.equals(Material.REDSTONE_ORE)) {
                    exp = 2;
                } else if (material.equals(Material.DIAMOND_ORE) || material.equals(Material.EMERALD_ORE)) {
                    exp = 4;
                }
            }

            // Check for nether miner
            if (job.getJobType().equals(JobType.NETHER_MINER)) {
                // Check for stones
                if (material.equals(Material.NETHERRACK) || material.equals(Material.BASALT)
                        || material.equals(Material.BLACKSTONE) || material.equals(Material.DIORITE)) {
                    exp = new Random().nextInt() % 50 == 0 ? 1 : 0;
                }

                // Check for ores
                else if (material.equals(Material.NETHER_QUARTZ_ORE) || material.equals(Material.NETHER_GOLD_ORE)) {
                    exp = 1;
                } else if (material.equals(Material.ANCIENT_DEBRIS)) {
                    exp = 6;
                }
            }

            // Check for woodcutter
            else if (job.getJobType().equals(JobType.WOODCUTTER)) {
                // Check for logs
                if (material.equals(Material.OAK_LOG) || material.equals(Material.BIRCH_LOG)
                        || material.equals(Material.SPRUCE_LOG) || material.equals(Material.JUNGLE_LOG)
                        || material.equals(Material.ACACIA_LOG) || material.equals(Material.DARK_OAK_LOG)) {
                    exp = new Random().nextInt() % 5 == 0 ? 1 : 0;;
                }

                // Check for leaves
                else if (material.equals(Material.OAK_LEAVES) || material.equals(Material.BIRCH_LEAVES)
                        || material.equals(Material.SPRUCE_LEAVES) || material.equals(Material.JUNGLE_LEAVES)
                        || material.equals(Material.ACACIA_LEAVES) || material.equals(Material.DARK_OAK_LEAVES)) {
                    exp = new Random().nextInt() % 20 == 0 ? 1 : 0;;
                }
            }

            // Check for nether woodcutter
            else if (job.getJobType().equals(JobType.NETHER_WOODCUTTER)) {
                // Check for logs
                if (material.equals(Material.CRIMSON_STEM) || material.equals(Material.WARPED_STEM)) {
                    exp = new Random().nextInt() % 5 == 0 ? 1 : 0;;
                }

                // Check for leaves
                else if (material.equals(Material.NETHER_WART_BLOCK) || material.equals(Material.WARPED_WART_BLOCK)) {
                    exp = new Random().nextInt() % 20 == 0 ? 1 : 0;;
                }
            }

            // Check for farmer
            else if (job.getJobType().equals(JobType.FARMER)) {
                // Check for crops
                if (material.equals(Material.WHEAT) || material.equals(Material.CARROTS)
                        || material.equals(Material.POTATOES) || material.equals(Material.SWEET_BERRY_BUSH)
                        || material.equals(Material.BEETROOTS) || material.equals(Material.COCOA)) {
                    // Check age
                    if (event.getBlock().getBlockData() instanceof Ageable) {
                        Ageable ageable = (Ageable) event.getBlock().getBlockData();
                        if (ageable.getAge() == ageable.getMaximumAge()) {
                            exp = new Random().nextInt() % 5 == 0 ? 1 : 0;;
                        }
                    }
                }
            }

            // If not null, add experience to player
            if (exp > 0) {
                job.getPendingTransaction().add(exp);
            }
        }
    }

}
