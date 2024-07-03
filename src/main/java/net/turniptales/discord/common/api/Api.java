package net.turniptales.discord.common.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import net.turniptales.discord.common.api.model.ConnectionDataValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.ZonedDateTime;

import static java.lang.String.format;
import static java.time.ZonedDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Log4j2
@Data
public class Api {

    /**
     * {@link ConnectionDataValue}
     */
    public ConnectionDataValue getData(long accountUserId) {
        String url = format("/data?discordUserId%s", accountUserId);
        return getGson().fromJson(sendGetRequest(url), ConnectionDataValue.class);
    }

    public ResponseEntity<Void> connect(long accountUserId, String code) {
        String url = format("/connect?type=DISCORD&accountUserId=%s&code=%s", accountUserId, code);
        return sendPostRequest(url);
    }

    /**
     * Request
     */
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

    private ResponseEntity<Void> sendPostRequest(String url) {
        return getClient(url).method(POST)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private WebClient getClient(String url) {
        return WebClient.builder()
                .baseUrl("https://turniptales.net:7200/turniptalesapi/v1/connection" + url)
                .defaultHeader("Authorization", "Basic dHVybmlwdGFsZXMtYXBpdXNlcjoqYnp0OThjV3EvLCckOT41SEN7NlEtTycralZFZmprMQ==")
                .build();
    }
}
