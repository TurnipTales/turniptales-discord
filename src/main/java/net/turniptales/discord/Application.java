package net.turniptales.discord;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.turniptales.discord.commands.StatsCommand;
import net.turniptales.discord.commands.TicketCommand;
import net.turniptales.discord.commands.VerifyCommand;
import net.turniptales.discord.events.GuildBanEventHandler;
import net.turniptales.discord.events.GuildMemberJoinEventHandler;
import net.turniptales.discord.events.GuildMemberRemoveEventHandler;
import net.turniptales.discord.events.TicketEventHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static java.lang.System.currentTimeMillis;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT;
import static net.dv8tion.jda.api.utils.Compression.NONE;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.MEMBER_OVERRIDES;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.VOICE_STATE;

@Log4j2
@SpringBootApplication
public class Application implements WebMvcConfigurer {

    public static JDA TURNIPTALES_BOT;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        long discordBotStartTime = currentTimeMillis();
        log.info("Discord bot starting");
        startDiscordBot();
        log.info("Discord bot started in {}ms", currentTimeMillis() - discordBotStartTime);
    }

    private static void startDiscordBot() {
        TURNIPTALES_BOT = JDABuilder
                .createDefault("MTE5MDcxNDY0MzU1MTg5MTQ3OA.GdvTJE.EMI9oOxjzEe-unfAXWPtOKgb2qGtWkqonPq5OY")
                .disableCache(MEMBER_OVERRIDES, VOICE_STATE) // Disable parts of the cache
                .setBulkDeleteSplittingEnabled(false) // Enable the bulk delete event
                .setCompression(NONE) // Disable compression (not recommended)
                .enableIntents(MESSAGE_CONTENT)
                .enableIntents(GUILD_MEMBERS)
                .addEventListeners(new TicketCommand(), new VerifyCommand(), new StatsCommand())
                .addEventListeners(new GuildMemberJoinEventHandler())
                .addEventListeners(new GuildMemberRemoveEventHandler())
                .addEventListeners(new GuildBanEventHandler())
                .addEventListeners(new TicketEventHandler())
                .build();

        TURNIPTALES_BOT
                .upsertCommand("ticket", "Erstellt die Nachricht um Tickets zu erstellen")
                .queue();

        TURNIPTALES_BOT
                .upsertCommand("verify", "Verifiziert deinen Minecraft Account")
                .addOption(STRING, "code", "Verifizierungscode", true)
                .queue();

        TURNIPTALES_BOT
                .upsertCommand("stats", "Statistiken eines Nutzers")
                .addOption(STRING, "player", "Spielername", true)
                .queue();
    }
}
