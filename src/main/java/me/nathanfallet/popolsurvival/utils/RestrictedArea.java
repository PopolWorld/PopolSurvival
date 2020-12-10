package me.nathanfallet.popolsurvival.utils;

import org.bukkit.entity.Player;

public interface RestrictedArea {

    boolean isAllowed(Player player);
    
}
