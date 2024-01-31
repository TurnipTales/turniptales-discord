package net.turniptales.discord.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.util.Date;

import static java.util.Objects.nonNull;
import static net.turniptales.discord.Config.BOT;
import static net.turniptales.discord.Config.COMMUNITY_TEXT_CHANNEL;

public class GuildMemberRemoveListener extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
        Guild guild = e.getGuild();
        User user = e.getUser();
        TextChannel missionControlTextChannel = guild.getSystemChannel();
        if (nonNull(missionControlTextChannel) && nonNull(BOT)) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(new Color(85, 85, 85))
                    .setAuthor("TurnipTales", "https://turniptales.net/", BOT.getEffectiveAvatarUrl())
                    .setDescription(user.getAsMention() + " hat den Server verlassen.")
                    .setFooter("Aktuelle Spieler: " + guild.getMemberCount(), user.getEffectiveAvatarUrl())
                    .setTimestamp(new Date().toInstant());

            missionControlTextChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    @Override
    public void onGuildBan(GuildBanEvent e) {
        Guild guild = e.getGuild();
        TextChannel communityTextChannel = COMMUNITY_TEXT_CHANNEL;
        TextChannel missionControlTextChannel = guild.getSystemChannel();
        if (nonNull(communityTextChannel) && nonNull(missionControlTextChannel) && nonNull(BOT)) {
            User user = e.getUser();
            Color color = new Color(255, 85, 85);
            EmbedBuilder embedBuildercommunityTextChannel = new EmbedBuilder()
                    .setColor(color)
                    .setAuthor("TurnipTales", "https://turniptales.net/", BOT.getEffectiveAvatarUrl())
                    .setTitle("Auf wiedersehen!")
                    .setDescription(user.getAsMention() + " nimmt sich eine Auszeit.")
                    .setFooter("Aktuelle Spieler: " + guild.getMemberCount(), user.getEffectiveAvatarUrl())
                    .setTimestamp(new Date().toInstant());

            EmbedBuilder embedBuildermissionControlTextChannel = new EmbedBuilder()
                    .setColor(color)
                    .setAuthor("TurnipTales", "https://turniptales.net/", BOT.getEffectiveAvatarUrl())
                    .setDescription(user.getAsMention() + " wurde gebannt.")
                    .setFooter("Aktuelle Spieler: " + guild.getMemberCount(), user.getEffectiveAvatarUrl())
                    .setTimestamp(new Date().toInstant());

            communityTextChannel.sendMessageEmbeds(embedBuildercommunityTextChannel.build()).queue();
            missionControlTextChannel.sendMessageEmbeds(embedBuildermissionControlTextChannel.build()).queue();
        }
    }
}
