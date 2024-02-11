package net.turniptales.discord;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.turniptales.discord.commands.StatsCommand;
import net.turniptales.discord.commands.SurveyCommand;
import net.turniptales.discord.commands.TicketCommand;
import net.turniptales.discord.commands.VerifyCommand;
import net.turniptales.discord.events.ButtonInteractionListener;
import net.turniptales.discord.events.GuildMemberJoinListener;
import net.turniptales.discord.events.GuildMemberRemoveListener;
import net.turniptales.discord.events.GuildMemberUpdateBoostTimeListener;
import net.turniptales.discord.events.MessageReactionListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static java.lang.System.currentTimeMillis;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT;
import static net.dv8tion.jda.api.utils.Compression.NONE;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.MEMBER_OVERRIDES;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.VOICE_STATE;

@Log4j2
@SpringBootApplication
public class TurnipTalesDiscord implements WebMvcConfigurer {

    public static JDA TURNIPTALES_BOT;

    public static void main(String[] args) {
        SpringApplication.run(TurnipTalesDiscord.class, args);

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
                .addEventListeners(
                        new StatsCommand(),
                        new SurveyCommand(),
                        new TicketCommand(),
                        new VerifyCommand()
                )
                .addEventListeners(
                        new ButtonInteractionListener(),
                        new GuildMemberJoinListener(),
                        new GuildMemberRemoveListener(),
                        new GuildMemberUpdateBoostTimeListener(),
                        new MessageReactionListener()
                )
                .build();

        TURNIPTALES_BOT
                .upsertCommand("stats", "Deine Statistiken (nicht öffentlich) oder die eines Discord Nutzers (öffentlich)")
                .addOption(USER, "player", "Discord Nutzer dessen Statistiken angezeigt werden sollen (Discord Nutzer muss sich verknüpft haben)", false)
                .queue();

        TURNIPTALES_BOT
                .upsertCommand("umfrage", "Erstellt eine Umfrage")
                .addOption(STRING, "question", "Frage", true)
                .addOption(STRING, "description", "Beschreibung", true)
                .addOption(STRING, "answer1", "Antwort 1", true)
                .addOption(STRING, "answer2", "Antwort 2", true)
                .addOption(STRING, "answer3", "Antwort 3", false)
                .addOption(STRING, "answer4", "Antwort 4", false)
                .addOption(STRING, "answer5", "Antwort 5", false)
                .queue();

        TURNIPTALES_BOT
                .upsertCommand("ticket", "Erstellt die Nachricht um Tickets zu erstellen")
                .queue();

        TURNIPTALES_BOT
                .upsertCommand("verify", "Verifiziert deinen Minecraft Account")
                .addOption(STRING, "code", "Verifizierungscode", true)
                .queue();
    }
}
