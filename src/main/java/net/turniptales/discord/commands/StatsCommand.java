package net.turniptales.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.turniptales.discord.common.api.model.ConnectionDataValue;
import net.turniptales.discord.common.api.model.PunishmentData;
import org.springframework.http.ResponseEntity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import static java.awt.Color.CYAN;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.util.Locale.US;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.turniptales.discord.TurnipTalesDiscord.api;
import static net.turniptales.discord.TurnipTalesDiscord.discordBot;

public class StatsCommand extends CommandBase {

    public static final DecimalFormatSymbols US_SYMBOLS = DecimalFormatSymbols.getInstance(US);
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.##", US_SYMBOLS);

    public StatsCommand(String name) {
        super(name);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        OptionMapping playerOptionMapping = event.getOption("player");

        Member member = event.getMember();
        String userId = event.getUser().getId();

        boolean notSelf = nonNull(playerOptionMapping);
        String targetUserId = notSelf ? playerOptionMapping.getAsString() : userId;
        ResponseEntity<ConnectionDataValue> responseEntity = api.getData(targetUserId);

        boolean isConnected = responseEntity.getStatusCode().is2xxSuccessful();
        if (!isConnected) {
            event.reply((notSelf ? "Der Spieler hat seinen" : "Du hast deinen") + " Discord Account noch nicht mit dem Minecraft Account verknüpft.\n-# 🚮 <t:" + (currentTimeMillis() / 1000 + 10) + ":R>").setEphemeral(true).queue();
            event.getHook().deleteOriginal().queueAfter(10, SECONDS);
            return;
        }

        ConnectionDataValue targetConnectionDataValue = responseEntity.getBody();

        if (notSelf) {
            MessageEmbed publicPlayerStats = isTicketChannel(event.getChannel()) ? getPrivatePlayerStats(targetConnectionDataValue, member) : getPublicPlayerStats(targetConnectionDataValue, member);
            event.replyEmbeds(publicPlayerStats).queue();
        } else {
            MessageEmbed privatePlayerStats = getPrivatePlayerStats(targetConnectionDataValue, member);
            event.replyEmbeds(privatePlayerStats).setEphemeral(true).queue();
        }
    }

    private boolean isTicketChannel(Channel channel) {
        return channel instanceof TextChannel textChannel
                && nonNull(textChannel.getTopic())
                && textChannel.getTopic().contains("Ticket von ");
    }

    private MessageEmbed getPublicPlayerStats(ConnectionDataValue connectionDataValue, Member member) {
        return getDefaultEmbedBuilder(connectionDataValue, member)
                .addField("Level", valueOf(connectionDataValue.getLevel()), true)
                .addField("Fraktion:", connectionDataValue.getGroup(), true)
                .addField("Kennt Spieler", valueOf(connectionDataValue.getKnownPlayerCount()), true)

                .addField("Steckbrief", getCharacteristics(connectionDataValue), false)
                .addBlankField(false)
                .addField("Häuser:", getHouses(connectionDataValue.getHouses()), true)
                .addField("Autos:", getCars(connectionDataValue.getCars()), true)
                .build();
    }

    private MessageEmbed getPrivatePlayerStats(ConnectionDataValue connectionDataValue, Member member) {
        return getDefaultEmbedBuilder(connectionDataValue, member)
                .addField("Level", valueOf(connectionDataValue.getLevel()), true)
                .addField("Fraktion:", connectionDataValue.getGroup(), true)
                .addField("Kennt Spieler", valueOf(connectionDataValue.getKnownPlayerCount()), true)

                .addField("Steckbrief", getCharacteristics(connectionDataValue), false)
                .addField("Finanzen", getBalances(connectionDataValue), true)
                .addField("Bestrafung", getPunishments(connectionDataValue.getPunishmentData()), true)
                .addBlankField(false)
                .addField("Häuser:", getHouses(connectionDataValue.getHouses()), true)
                .addField("Autos:", getCars(connectionDataValue.getCars()), true)
                .build();
    }

    private EmbedBuilder getDefaultEmbedBuilder(ConnectionDataValue connectionDataValue, Member member) {
        return new EmbedBuilder()
                .setColor(CYAN)
                .setThumbnail("https://minotar.net/helm/" + connectionDataValue.getMinecraftUuid() + "/300.png")
                .setAuthor("TurnipTales", "https://turniptales.net/", discordBot.getSelfUser().getEffectiveAvatarUrl())
                .setTitle("Statistiken von " + connectionDataValue.getMinecraftName())
                .setDescription("**Role**: " + connectionDataValue.getRole())
                .setFooter(member.getEffectiveName(), member.getEffectiveAvatarUrl())
                .setTimestamp(new Date().toInstant());
    }

    private String getCharacteristics(ConnectionDataValue connectionDataValue) {
        return """
                **Name**: %s
                **Alter**: %s
                **Geschlecht**: %s
                """
                .formatted(connectionDataValue.getRoleplayName(),
                        connectionDataValue.getAge(),
                        connectionDataValue.getGender().equals("MAN") ? ":male_sign:" : ":female_sign:");
    }

    private String getBalances(ConnectionDataValue connectionDataValue) {
        return """
                **Bank**: %s$
                **Bargeld**: %s$
                **PayPal**: %s$
                """
                .formatted(DECIMAL_FORMAT.format(connectionDataValue.getBankBalance()), DECIMAL_FORMAT.format(connectionDataValue.getCashBalance()), DECIMAL_FORMAT.format(connectionDataValue.getPalPayBalance()));
    }

    private String getPunishments(PunishmentData punishmentData) {
        return """
                **Waffensperre**: %s
                **Fraktionssperre**: %s
                **Skill-Sperre**: %s
                **Ad-Sperre**: %s
                **Auto-Sperre**: %s
                **Checkpoints**: %s
                """
                .formatted(!punishmentData.noWeaponBlockActive(), !punishmentData.noGroupBlockActive(), !punishmentData.noSkillBlockActive(), !punishmentData.noAdBlockActive(), !punishmentData.noCarBlockActive(), punishmentData.getCheckpointsLeft());
    }

    private String getHouses(List<Long> houses) {
        StringJoiner houseJoiner = new StringJoiner("\n- ", "- ", "");
        houses.stream()
                .map(Object::toString)
                .forEach(houseJoiner::add);
        return !houses.isEmpty() ? houseJoiner.toString() : "keine Häuser im Besitz oder zur Miete";
    }

    private String getCars(List<String> cars) {
        StringJoiner carJoiner = new StringJoiner("\n- ", "- ", "");
        cars.forEach(carJoiner::add);
        return !cars.isEmpty() ? carJoiner.toString() : "keine Autos im Besitz";
    }
}
