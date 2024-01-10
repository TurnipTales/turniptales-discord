package net.turniptales.discord.common.api;

import net.turniptales.discord.common.api.model.PlayerStats;
import net.turniptales.discord.common.api.model.Verification;

import static java.util.UUID.fromString;

public class API {

    public static Verification getVerification(String verificationCode, long memberId) {
        // rettichlp.de:8090/turniptales/v1/verification/check?code=<verificationCode>&memberId=<memberId>
        return new Verification(fromString("25855f4d-3874-4a7f-a6ad-e9e4f3042e19"), "RettichLP");
    }

    public static PlayerStats getPlayerStats(String playerName) {
        return new PlayerStats(fromString("25855f4d-3874-4a7f-a6ad-e9e4f3042e19"), "RettichLP", 20, true);
    }
}
