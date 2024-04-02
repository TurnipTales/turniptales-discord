package net.turniptales.discord.common.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import net.turniptales.discord.common.api.model.DiscordPlayerStats;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpMethod.GET;

@Log4j2
@Data
public class Api {

    /**
     * {@link DiscordPlayerStats}
     */
    public DiscordPlayerStats getPlayerStatsByDiscordUserId(long userId, @Nullable String discordCode) {
        String code = ofNullable(discordCode)
                .map(c -> "?discordCode=" + c)
                .orElse("");

        return parseGetRequest("/" + userId + code, DiscordPlayerStats.class);
    }

    /**
     * Request
     */
    private <T> T parseGetRequest(String url, Class<T> model) {
        Gson gson = getGson();
        return gson.fromJson(sendGetRequest(url), model);
    }

    private Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, (JsonDeserializer<ZonedDateTime>) (json, type, jsonDeserializationContext) -> parse(json.getAsJsonPrimitive().getAsString()))
                .registerTypeAdapter(ZonedDateTime.class, (JsonSerializer<ZonedDateTime>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.format(ISO_ZONED_DATE_TIME)))
                .create();
    }

    private String sendGetRequest(String url) {
        return getClient(url).method(GET)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private WebClient getClient(String url) {
        return WebClient.builder()
                .baseUrl("https://turniptales.net:7200/turniptalesapi/v1/verification/discord" + url)
                .defaultHeader("Authorization", "Basic dHVybmlwdGFsZXMtYXBpdXNlcjoqYnp0OThjV3EvLCckOT41SEN7NlEtTycralZFZmprMQ==")
                .build();
    }
}
