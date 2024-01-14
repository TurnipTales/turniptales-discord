package net.turniptales.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.turniptales.discord.common.api.API;
import net.turniptales.discord.common.api.model.PlayerStats;

import static java.lang.String.valueOf;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class StatsCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!e.getName().equals("stats")) {
            return;
        }

        OptionMapping playerOptionMapping = e.getOption("player");
        if (!nonNull(playerOptionMapping)) {
            e.reply("Gib einen Spielernamen an!").setEphemeral(true).queue();
            return;
        }

        PlayerStats playerStats = API.getPlayerStats(playerOptionMapping.getAsString());
        MessageEmbed messageEmbed = createPlayerStatsMessageEmbed(playerStats, requireNonNull(e.getMember()));
        e.replyEmbeds(messageEmbed).queue();
    }

    private MessageEmbed createPlayerStatsMessageEmbed(PlayerStats playerStats, Member requestor) {
        return new EmbedBuilder()
                .setTitle("Statistiken von " + playerStats.getMinecraftName())
                .setDescription(playerStats.getMinecraftUuid().toString())
                .addField("Online", playerStats.isOnline() ? "Ja" : "Nein", true)
                .addField("Level", valueOf(playerStats.getLevel()), true)
                .setFooter("Angefordert von " + requestor.getUser().getAsMention(), requestor.getAvatarUrl())
                .build();
    }
}
