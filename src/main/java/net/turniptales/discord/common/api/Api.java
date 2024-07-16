package net.turniptales.discord.common.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import net.turniptales.discord.common.api.model.ConnectionDataValue;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

import static com.google.gson.JsonParser.parseString;
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
    public ResponseEntity<ConnectionDataValue> getData(String accountUserId) {
        String url = format("/data?discordUserId=%s", accountUserId);

        ResponseEntity<String> responseEntity = sendRequest(url, GET);

        return responseEntity.getStatusCode().is2xxSuccessful()
                ? ResponseEntity.ok(getGson().fromJson(responseEntity.getBody(), ConnectionDataValue.class))
                : ResponseEntity.status(responseEntity.getStatusCode()).body(null);
    }

    public ResponseEntity<String> connect(String accountUserId, String code) {
        String url = format("/connect?type=DISCORD&accountUserId=%s&code=%s", accountUserId, code);
        return sendRequest(url, POST);
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

    private ResponseEntity<String> sendRequest(String url, HttpMethod method) {
        return getClient(url).method(method)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    String responseBodyAsString = ex.getResponseBodyAsString();
                    String info = parseString(responseBodyAsString).getAsJsonObject().get("info").getAsString();
                    log.error("Request failed with code {}: {}", ex.getStatusCode(), info);
                    return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(responseBodyAsString));
                })
                .block();
    }

    private WebClient getClient(String url) {
        return WebClient.builder()
                .baseUrl("https://turniptales.net:7200/turniptalesapi/v1/connection" + url)
                .defaultHeader("Authorization", "Basic dHVybmlwdGFsZXMtYXBpdXNlcjoqYnp0OThjV3EvLCckOT41SEN7NlEtTycralZFZmprMQ==")
                .build();
    }
}
