package me.nathanfallet.popolsurvival.utils;

import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;
import me.nathanfallet.popolserver.api.APITeam;
import me.nathanfallet.popolserver.utils.PopolMoney.BalanceCheckHandler;
import me.nathanfallet.popolserver.utils.PopolMoney.BalanceUpdatedHandler;

public class PopolTeamMoney {

    // Check team balance
    public static void checkBalance(PopolTeam team, final BalanceCheckHandler handler) {
        // Fetch API
        team.get(new CompletionHandler<APITeam>() {
            @Override
            public void completionHandler(APITeam object, APIResponseStatus status) {
                // Check response and status
                if (object != null && status == APIResponseStatus.ok) {
                    // Return money
                    handler.balanceChecked(object.money);
                } else {
                    // Error, return null
                    handler.balanceChecked(null);
                }
            }
        });
    }

    // Update team balance
    public static void updateBalance(PopolTeam team, Long newBalance, final BalanceUpdatedHandler handler) {
        // Put API
        team.put(new APITeam(team.getId(), null, newBalance), new CompletionHandler<APITeam>() {
            @Override
            public void completionHandler(APITeam object, APIResponseStatus status) {
                // Check response and status
                if (object != null && status == APIResponseStatus.ok) {
                    // Return new balance
                    handler.balanceUpdated(object.money);
                } else {
                    // Error, return null
                    handler.balanceUpdated(null);
                }
            }
        });
    }

}
