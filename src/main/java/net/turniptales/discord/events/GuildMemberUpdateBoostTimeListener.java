package net.turniptales.discord.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.util.Date;

import static java.util.Objects.nonNull;
import static net.turniptales.discord.Config.BOT;
import static net.turniptales.discord.Config.COMMUNITY_TEXT_CHANNEL;

public class GuildMemberUpdateBoostTimeListener extends ListenerAdapter {

    @Override
    public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent e) {
        Guild guild = e.getGuild();
        TextChannel communityTextChannel = COMMUNITY_TEXT_CHANNEL;
        if (nonNull(communityTextChannel) && nonNull(BOT)) {
            Member member = e.getMember();

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(new Color(85, 255, 255))
                    .setAuthor("TurnipTales", "https://turniptales.net/", BOT.getEffectiveAvatarUrl())
                    .setTitle("Danke für den Boost!")
                    .setDescription(member.getAsMention() + " hat den Server geboostet.")
                    .setFooter("Aktuelle Boosts: **" + guild.getBoostCount() + "** (" + guild.getBoostTier() + ")", member.getEffectiveAvatarUrl())
                    .setTimestamp(new Date().toInstant());

            communityTextChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }
}
