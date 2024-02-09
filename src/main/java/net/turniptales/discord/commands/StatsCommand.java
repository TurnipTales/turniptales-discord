package net.turniptales.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.turniptales.discord.common.api.Api;
import net.turniptales.discord.common.api.model.PlayerStats;
import net.turniptales.discord.common.api.model.stats.PunishmentData;
import net.turniptales.discord.common.api.model.stats.RoleplayData;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import static java.awt.Color.CYAN;
import static java.lang.String.valueOf;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.turniptales.discord.Config.BOT;
import static net.turniptales.discord.common.api.model.stats.RoleplayData.Gender.MAN;

public class StatsCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!e.getName().equals("stats")) {
            return;
        }

        OptionMapping playerOptionMapping = e.getOption("player");

        Api api = new Api();
        Member member = e.getMember();
        if (nonNull(playerOptionMapping)) {
            try {
                PlayerStats playerStats = api.getPlayerStatsByDiscordUserId(playerOptionMapping.getAsUser().getIdLong());
                MessageEmbed publicPlayerStats = isTicketChannel(e.getChannel()) ? getPrivatePlayerStats(playerStats, member) : getPublicPlayerStats(playerStats, member);
                e.replyEmbeds(publicPlayerStats).queue();
            } catch (Exception ex) {
                e.reply("Der angegebene Spieler hat seinen Minecraft Account noch nicht verknüpft.").setEphemeral(true).queue();
                e.getHook().deleteOriginal().queueAfter(5, SECONDS);
            }
        } else {
            try {
                PlayerStats playerStats = api.getPlayerStatsByDiscordUserId(e.getUser().getIdLong());
                MessageEmbed privatePlayerStats = getPrivatePlayerStats(playerStats, member);
                e.replyEmbeds(privatePlayerStats).setEphemeral(true).queue();
            } catch (Exception ex) {
                e.reply("Du hast deinen Minecraft Account noch nicht verknüpft.").setEphemeral(true).queue();
                e.getHook().deleteOriginal().queueAfter(5, SECONDS);
            }
        }
    }

    private boolean isTicketChannel(Channel channel) {
        return channel instanceof TextChannel textChannel
                && nonNull(textChannel.getTopic())
                && textChannel.getTopic().contains("Ticket von ");
    }

    private MessageEmbed getPublicPlayerStats(PlayerStats playerStats, Member member) {
        RoleplayData roleplayData = playerStats.getRoleplayData();

        return getDefaultEmbedBuilder(playerStats, member)
                .addField("Level", valueOf(playerStats.getLevel()), true)
                .addField("Fraktion:", playerStats.getFaction(), true)
                .addField("Kennt Spieler", valueOf(playerStats.getKnownPlayerCount()), true)

                .addField("Steckbrief", getCharacteristics(roleplayData), false)
                .addBlankField(false)
                .addField("Häuser:", getHouses(playerStats.getHouses()), true)
                .addField("Autos:", getCars(playerStats.getCars()), true)
                .build();
    }

    private MessageEmbed getPrivatePlayerStats(PlayerStats playerStats, Member member) {
        RoleplayData roleplayData = playerStats.getRoleplayData();

        return getDefaultEmbedBuilder(playerStats, member)
                .addField("Level", valueOf(playerStats.getLevel()), true)
                .addField("Fraktion:", playerStats.getFaction(), true)
                .addField("Kennt Spieler", valueOf(playerStats.getKnownPlayerCount()), true)

                .addField("Steckbrief", getCharacteristics(roleplayData), false)
                .addField("Finanzen", getBalances(roleplayData), true)
                .addField("Bestrafung", getPunishments(playerStats.getPunishmentData()), true)
                .addBlankField(false)
                .addField("Häuser:", getHouses(playerStats.getHouses()), true)
                .addField("Autos:", getCars(playerStats.getCars()), true)
                .build();
    }

    private EmbedBuilder getDefaultEmbedBuilder(PlayerStats playerStats, Member member) {
        assert BOT != null;
        return new EmbedBuilder()
                .setColor(CYAN)
                .setThumbnail("https://minotar.net/helm/" + playerStats.getMinecraftUuid() + "/300.png")
                .setAuthor("TurnipTales", "https://turniptales.net/", BOT.getEffectiveAvatarUrl())
                .setTitle("Statistiken von " + playerStats.getMinecraftName())
                .setDescription("**Role**: " + playerStats.getRole())
                .setFooter(member.getEffectiveName(), member.getEffectiveAvatarUrl())
                .setTimestamp(new Date().toInstant());
    }

    private String getCharacteristics(RoleplayData roleplayData) {
        return """
                **Name**: %s
                **Alter**: %s
                **Geschlecht**: %s
                """
                .formatted(roleplayData.getFirstname() + " " + roleplayData.getLastname(),
                        "20",
                        roleplayData.getGender().equals(MAN) ? ":male_sign:" : ":female_sign:");
    }

    private String getBalances(RoleplayData roleplayData) {
        return """
                **Bank**: %s$
                **Bargeld**: %s$
                **PayPal**: %s$
                """
                .formatted(roleplayData.getBankBalance(), roleplayData.getCashBalance(), roleplayData.getPayPalBalance());
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
                .formatted(punishmentData.isWeaponBlockActive(), punishmentData.isFactionBlockActive(), punishmentData.isSkillBlockActive(), punishmentData.isAdBlockActive(), punishmentData.isCarBlockActive(), punishmentData.getCheckpointsLeft());
    }

    private String getHouses(List<Integer> houses) {
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
