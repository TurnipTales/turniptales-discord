package net.turniptales.discord.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.util.Date;

import static java.awt.Color.CYAN;
import static java.util.Objects.nonNull;
import static net.turniptales.discord.TurnipTalesDiscord.discordBot;
import static net.turniptales.discord.TurnipTalesDiscord.discordBotProperties;

public class GuildAccessListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        Guild guild = e.getGuild();
        TextChannel communityTextChannel = discordBotProperties.getCommunityTextChannel();
        TextChannel missionControlTextChannel = guild.getSystemChannel();
        if (nonNull(communityTextChannel) && nonNull(missionControlTextChannel)) {
            Member member = e.getMember();

            EmbedBuilder embedBuildercommunityTextChannel = new EmbedBuilder()
                    .setColor(CYAN)
                    .setAuthor("TurnipTales", "https://turniptales.net/", discordBot.getSelfUser().getEffectiveAvatarUrl())
                    .setTitle("Willkommen auf TurnipTales!")
                    .setDescription(member.getAsMention() + " hat den Server betreten.")
                    .setFooter("Aktuelle Spieler: " + guild.getMemberCount(), member.getEffectiveAvatarUrl())
                    .setTimestamp(new Date().toInstant());

            EmbedBuilder embedBuildermissionControlTextChannel = new EmbedBuilder()
                    .setColor(CYAN)
                    .setAuthor("TurnipTales", "https://turniptales.net/", discordBot.getSelfUser().getEffectiveAvatarUrl())
                    .setDescription(member.getAsMention() + " hat den Server betreten.")
                    .setFooter("Aktuelle Spieler: " + guild.getMemberCount(), member.getEffectiveAvatarUrl())
                    .setTimestamp(new Date().toInstant());

            communityTextChannel.sendMessageEmbeds(embedBuildercommunityTextChannel.build()).queue();
            missionControlTextChannel.sendMessageEmbeds(embedBuildermissionControlTextChannel.build()).queue();

            guild.addRoleToMember(e.getUser(), discordBotProperties.getPlayerRole()).queue();
            guild.addRoleToMember(e.getUser(), discordBotProperties.getRole0()).queue();
        }
    }

    @Override
    public void onGuildBan(GuildBanEvent e) {
        Guild guild = e.getGuild();
        TextChannel communityTextChannel = discordBotProperties.getCommunityTextChannel();
        TextChannel missionControlTextChannel = guild.getSystemChannel();
        if (nonNull(communityTextChannel) && nonNull(missionControlTextChannel)) {
            User user = e.getUser();
            Color color = new Color(255, 85, 85);
            EmbedBuilder embedBuildercommunityTextChannel = new EmbedBuilder()
                    .setColor(color)
                    .setAuthor("TurnipTales", "https://turniptales.net/", discordBot.getSelfUser().getEffectiveAvatarUrl())
                    .setTitle("Auf wiedersehen!")
                    .setDescription(user.getAsMention() + " nimmt sich eine Auszeit.")
                    .setFooter("Aktuelle Spieler: " + guild.getMemberCount(), user.getEffectiveAvatarUrl())
                    .setTimestamp(new Date().toInstant());

            EmbedBuilder embedBuildermissionControlTextChannel = new EmbedBuilder()
                    .setColor(color)
                    .setAuthor("TurnipTales", "https://turniptales.net/", discordBot.getSelfUser().getEffectiveAvatarUrl())
                    .setDescription(user.getAsMention() + " wurde gebannt.")
                    .setFooter("Aktuelle Spieler: " + guild.getMemberCount(), user.getEffectiveAvatarUrl())
                    .setTimestamp(new Date().toInstant());

            communityTextChannel.sendMessageEmbeds(embedBuildercommunityTextChannel.build()).queue();
            missionControlTextChannel.sendMessageEmbeds(embedBuildermissionControlTextChannel.build()).queue();
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
        Guild guild = e.getGuild();
        User user = e.getUser();
        TextChannel missionControlTextChannel = guild.getSystemChannel();
        if (nonNull(missionControlTextChannel)) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(new Color(85, 85, 85))
                    .setAuthor("TurnipTales", "https://turniptales.net/", discordBot.getSelfUser().getEffectiveAvatarUrl())
                    .setDescription(user.getAsMention() + " hat den Server verlassen.")
                    .setFooter("Aktuelle Spieler: " + guild.getMemberCount(), user.getEffectiveAvatarUrl())
                    .setTimestamp(new Date().toInstant());

            missionControlTextChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }
}
