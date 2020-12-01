package me.nathanfallet.popolsurvival.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.nathanfallet.popolserver.api.APIJob;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;
import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.PopolJob.JobsLoaderHandler;

public class PopolJobMenu {

    // Menu title (constant)
    private static final String title = "Jobs";

    // Open menu for a player
    public static void openMenu(Player player) {
        // Create menu
        Inventory menu = Bukkit.createInventory(null, 27, title);

        // Fill menu with jobs
        for (JobType type : JobType.values()) {
            // Create item
            ItemStack item = new ItemStack(type.icon);
            ItemMeta meta = item.getItemMeta();

            // Set server name
            meta.setDisplayName(ChatColor.GOLD + type.name);

            // Create description
            List<String> lore = new ArrayList<>();
            lore.add("");

            // TODO: Some job description?

            // Set description
            meta.setLore(lore);

            // Set meta back and add to menu
            item.setItemMeta(meta);
            menu.addItem(item);
        }

        // Open menu
        player.openInventory(menu);
    }

    // Handle click in menu
    public static void handleClick(InventoryClickEvent event) {
        // Check inventory title and entity
        if (event.getView().getTitle().equals(title) && event.getWhoClicked() instanceof Player) {
            // Cancel event
            event.setCancelled(true);

            // Get player
            final Player player = (Player) event.getWhoClicked();

            // Count which job is selected
            int i = 0;
            for (final JobType type : JobType.values()) {
                // Check if this slot is this job
                if (i == event.getSlot()) {
                    // Disable any active job
                    PopolJob active = PopolSurvival.getInstance().getActiveJob(player.getUniqueId());
                    if (active != null) {
                        active.put(new APIJob(null, active.getJobType().toString().toLowerCase(),
                                player.getUniqueId().toString(), null, false), new CompletionHandler<APIJob>() {
                                    @Override
                                    public void completionHandler(APIJob job, APIResponseStatus status) {
                                        // Nothing to do here
                                    }
                                });
                    }

                    // Check if this player has an entry for this job
                    PopolJob entry = PopolSurvival.getInstance().getJob(player.getUniqueId(), type);
                    if (entry != null) {
                        // Mark this entry as active
                        entry.put(new APIJob(null, type.toString().toLowerCase(), player.getUniqueId().toString(), null,
                                true), new CompletionHandler<APIJob>() {
                                    @Override
                                    public void completionHandler(APIJob job, APIResponseStatus status) {
                                        // Check response
                                        if (status == APIResponseStatus.ok) {
                                            // Player is now in this job
                                            player.sendMessage(ChatColor.GREEN + "Vous êtes maintenant "
                                                    + ChatColor.YELLOW + type.name + ChatColor.GREEN + " !");
                                        } else {
                                            // Error
                                            player.sendMessage(ChatColor.RED + "Erreur inconnue !");
                                        }
                                    }
                                });
                    } else {
                        // Create a new entry
                        PopolSurvival.getInstance().createJob(type, player.getUniqueId(), new JobsLoaderHandler() {
                            @Override
                            public void jobsLoaded(List<PopolJob> jobs) {
                                // Check response
                                if (jobs != null) {
                                    // Player is now in this job
                                    player.sendMessage(ChatColor.GREEN + "Vous êtes maintenant " + ChatColor.YELLOW
                                            + type.name + ChatColor.GREEN + " !");
                                } else {
                                    // Error
                                    player.sendMessage(ChatColor.RED + "Erreur inconnue !");
                                }
                            }
                        });
                    }

                    // Close menu
                    player.closeInventory();

                    // Stop here
                    return;
                }

                // Next one
                i++;
            }
        }

    }

}
