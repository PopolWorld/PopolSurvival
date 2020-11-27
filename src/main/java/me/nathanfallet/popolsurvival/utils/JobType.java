package me.nathanfallet.popolsurvival.utils;

import org.bukkit.Material;

public enum JobType {

    /**
     * Enum values
     */

    WOODCUTTER("Bûcheron", Material.OAK_LOG),
    MINER("Mineur", Material.STONE);

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
