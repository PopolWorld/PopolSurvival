package me.nathanfallet.popolsurvival.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.nathanfallet.popolsurvival.PopolSurvival;

public class PopolChunkMap {

    // Menu title (constant)
    private static final String title = "Carte des chunks";

    // Open menu for a player
    public static void openMenu(Player player) {
        // Create menu
        Inventory menu = Bukkit.createInventory(null, 45, title);

        // Get location of player chunk
        long x = player.getLocation().getChunk().getX();
        long z = player.getLocation().getChunk().getZ();

        // Fill menu with chunks
        for (int dz = 0; dz < 5; dz++) {
            for (int dx = 0; dx < 9; dx++) {
                // Calculate coordinates
                long newX = x + dx - 4;
                long newZ = z + dz - 2;

                // ItemStack
                ItemStack item = null;

                // If it is player chunk, change to its head
                if (newX == x && newZ == z) {
                    item = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) item.getItemMeta();
                    meta.setOwningPlayer(player);
                    item.setItemMeta(meta);
                }

                // Get region
                PopolRegion region = PopolSurvival.getInstance().getRegion(newX >> 5, newZ >> 5);
                if (region != null) {
                    // Get chunk
                    PopolChunk chunk = region.getChunk(newX, newZ);
                    if (chunk != null) {
                        // Chunk is claimed
                        // So create an item if not already created
                        if (item == null) {
                            item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                        }

                        // And add it a lore with some infos
                        ItemMeta meta = item.getItemMeta();
                        List<String> lore = new ArrayList<>();
                        lore.add("");
                        lore.add(ChatColor.GREEN + "Statut :");
                        lore.add(ChatColor.WHITE + "Chunk protégé");
                        lore.add("");
                        lore.add(ChatColor.RED + "Team :");

                        // Try to get team if loaded
                        PopolTeam team = PopolSurvival.getInstance().getTeam(chunk.getCached().teamId);
                        lore.add(ChatColor.WHITE + (team != null ? team.getCached().name : "Chargement..."));

                        // Set meta tot item
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }
                }

                // If no item was created, create one
                if (item == null) {
                    item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                }

                // Set chunk coordinates
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GOLD + "X: " + newX + ", Z:" + newZ);

                // If no lore is set
                if (!meta.hasLore()) {
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(ChatColor.GREEN + "Statut :");
                    lore.add(ChatColor.WHITE + "Libre");
                    meta.setLore(lore);
                }

                // Set meta
                item.setItemMeta(meta);

                // Add it to inventory
                menu.addItem(item);
            }
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
        }
    }

}
