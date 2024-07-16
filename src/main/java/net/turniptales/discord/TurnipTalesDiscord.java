package net.turniptales.discord;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.turniptales.discord.buttons.GiveawayWinnerPublishButton;
import net.turniptales.discord.buttons.SyncButton;
import net.turniptales.discord.buttons.TicketCloseAbortButton;
import net.turniptales.discord.buttons.TicketCloseButton;
import net.turniptales.discord.buttons.TicketCloseConfirmButton;
import net.turniptales.discord.buttons.TicketCreateButton;
import net.turniptales.discord.commands.GiveawayCommand;
import net.turniptales.discord.commands.RolesCommand;
import net.turniptales.discord.commands.StatsCommand;
import net.turniptales.discord.commands.TestCommand;
import net.turniptales.discord.commands.TicketCommand;
import net.turniptales.discord.commands.VerifyCommand;
import net.turniptales.discord.common.api.Api;
import net.turniptales.discord.common.configuration.DiscordBotProperties;
import net.turniptales.discord.events.GuildAccessListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.ZoneId;

import static java.lang.System.currentTimeMillis;
import static java.time.ZoneId.of;
import static net.dv8tion.jda.api.Permission.ADMINISTRATOR;
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

    public static final ZoneId ZONE_ID = of("Europe/Berlin");
    public static JDA discordBot;
    public static DiscordBotProperties discordBotProperties;
    public static Api api;

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(TurnipTalesDiscord.class, args);

        discordBotProperties = context.getBean(DiscordBotProperties.class);

        long discordBotStartTime = currentTimeMillis();
        log.info("Discord bot starting");
        startDiscordBot();
        log.info("Discord bot started in {}ms", currentTimeMillis() - discordBotStartTime);

        api = new Api();
    }

    private static void startDiscordBot() throws InterruptedException {
        discordBot = JDABuilder
                .createDefault(discordBotProperties.getToken())
                .disableCache(MEMBER_OVERRIDES, VOICE_STATE) // Disable parts of the cache
                .setBulkDeleteSplittingEnabled(false) // Enable the bulk delete event
                .setCompression(NONE) // Disable compression (not recommended)
                .enableIntents(MESSAGE_CONTENT)
                .enableIntents(GUILD_MEMBERS)
                .addEventListeners(
                        new GiveawayCommand(),
                        new RolesCommand(),
                        new StatsCommand(),
                        new TicketCommand(),
                        new VerifyCommand()
                )
                .addEventListeners(
                        new GuildAccessListener()
                )
                .addEventListeners(
                        new GiveawayWinnerPublishButton(),
                        new SyncButton(),
                        new TicketCloseAbortButton(),
                        new TicketCloseButton(),
                        new TicketCloseConfirmButton(),
                        new TicketCreateButton()
                )
                .build().awaitReady();

        discordBotProperties.getGuild().updateCommands().addCommands(
                Commands.slash("rollen", "Informationen zu den Rollen auf diesem Discord"),
                Commands.slash("stats", "Deine Statistiken (nicht öffentlich) oder die eines Discord Nutzers (öffentlich)")
                        .addOption(USER, "player", "Discord Nutzer dessen Statistiken angezeigt werden sollen (Discord Nutzer muss sich verknüpft haben)", false),
                Commands.slash("verify", "Verifiziert deinen Minecraft Account")
                        .addOption(STRING, "code", "Verifizierungscode", true),

                Commands.slash("ticket", "Erstellt die Nachricht um Tickets zu erstellen")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(ADMINISTRATOR)),
                Commands.slash("giveaway", "Lost einen Spieler anhand der Reaktionen einer Nachricht aus")
                        .addOption(STRING, "message", "Nachricht mit den Reaktionen", true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(ADMINISTRATOR))
        ).queue();
    }
}
