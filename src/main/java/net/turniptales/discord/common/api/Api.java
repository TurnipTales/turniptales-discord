package net.turniptales.discord.common.api;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import net.turniptales.discord.common.api.model.PlayerStats;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.HttpMethod.GET;

@Log4j2
@Data
public class Api {

    /**
     * {@link PlayerStats}
     */
    public PlayerStats getPlayerStatsByDiscordUserId(long userId) {
        return parseGetRequest("/" + userId, PlayerStats.class);
    }

    public PlayerStats getPlayerStatsByDiscordUserIdVerify(long userId, String discordCode) {
        return parseGetRequest("/" + userId + "?discordCode=" + discordCode, PlayerStats.class);
    }

    /**
     * Request
     */
    private <T> T parseGetRequest(String url, Class<T> model) {
        Gson gson = new Gson();
        return gson.fromJson(sendGetRequest(url), model);
    }

    private String sendGetRequest(String url) {
        return getClient(url).method(GET)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private WebClient getClient(String url) {
        return WebClient.builder()
                .baseUrl("https://rettichlp.de:7100/turniptalesapi/v1/discord" + url)
                .defaultHeader("Authorization", "Basic dHVybmlwdGFsZXMtYXBpdXNlcjoqYnp0OThjV3EvLCckOT41SEN7NlEtTycralZFZmprMQ==")
                .build();
    }
}
