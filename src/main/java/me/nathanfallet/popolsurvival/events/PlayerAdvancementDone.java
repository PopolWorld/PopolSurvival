package me.nathanfallet.popolsurvival.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.utils.PopolMoney;
import me.nathanfallet.popolserver.utils.PopolMoney.BalanceCheckHandler;
import me.nathanfallet.popolserver.utils.PopolMoney.BalanceUpdatedHandler;
import me.nathanfallet.popolserver.utils.PopolPlayer;

public class PlayerAdvancementDone implements Listener {

    public static final long reward = 50L;

    @EventHandler
    public void onPlayerAdvancementDone(final PlayerAdvancementDoneEvent event) {
        // Get player
        final PopolPlayer player = PopolServer.getInstance().getPlayer(event.getPlayer().getUniqueId());

        // Reward
        PopolMoney.checkBalance(player, new BalanceCheckHandler() {
            @Override
            public void balanceChecked(Long money) {
                // Add money to account
                PopolMoney.updateBalance(player, money + reward, new BalanceUpdatedHandler() {
                    @Override
                    public void balanceUpdated(Long money) {
                        // Reward given
                        event.getPlayer().sendMessage(
                                ChatColor.YELLOW + "Vous avez gagné " + reward + "₽ pour avoir accompli ce défi !");
                    }
                });
            }
        });
    }

}
