package me.nathanfallet.popolsurvival.utils;

import org.bukkit.Material;

public enum JobType {

    /**
     * Enum values
     */

    MINER("Mineur", Material.STONE), WOODCUTTER("Bûcheron", Material.OAK_LOG),
    NETHER_MINER("Mineur du nether", Material.NETHERRACK),
    NETHER_WOODCUTTER("Bûcheron du nether", Material.CRIMSON_STEM), FARMER("Fermier", Material.WHEAT),
    HUNTER("Chasseur", Material.STONE_SWORD), FISHER("Pêcheur", Material.FISHING_ROD),
    ENCHANTER("Enchanteur", Material.ENCHANTING_TABLE);

    /**
     * Data structure
     */

    // Properties
    public final String name;
    public final Material icon;

    // Contructor
    JobType(String name, Material icon) {
        this.name = name;
        this.icon = icon;
    }

}
