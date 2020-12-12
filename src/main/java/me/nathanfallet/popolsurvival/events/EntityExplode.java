package me.nathanfallet.popolsurvival.events;

import java.util.Iterator;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.nathanfallet.popolsurvival.PopolSurvival;
import me.nathanfallet.popolsurvival.utils.RestrictedArea;

public class EntityExplode implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        // Iterate blocs
        Iterator<Block> iterator = event.blockList().iterator();
        while(iterator.hasNext()) {
            // Get block
            Block block = iterator.next();

            // Get area
            RestrictedArea area = PopolSurvival.getInstance().getRestrictedArea(block.getLocation());

            // If it corresponds to a resticted area
            if (area != null) {
                // Remove block
                iterator.remove();
            }
        }
    }
    
}
