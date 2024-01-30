package net.turniptales.discord.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Date;

import static java.awt.Color.CYAN;
import static java.util.Objects.nonNull;
import static net.turniptales.discord.Config.BOT;
import static net.turniptales.discord.Config.ROLE_0;

public class GuildMemberJoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        Guild guild = e.getGuild();
        TextChannel systemChannel = guild.getSystemChannel();
        if (nonNull(systemChannel) && nonNull(BOT) && nonNull(ROLE_0)) {
            Member member = e.getMember();

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(CYAN)
                    .setAuthor("TurnipTales", "https://turniptales.net/", BOT.getEffectiveAvatarUrl())
                    .setTitle("Willkommen auf TurnipTales!")
                    .setDescription(member.getAsMention() + " hat den Server betreten.")
                    .setFooter("Aktuelle Spieler: " + guild.getMemberCount(), member.getEffectiveAvatarUrl())
                    .setTimestamp(new Date().toInstant());

            systemChannel.sendMessageEmbeds(embedBuilder.build()).queue();

            guild.addRoleToMember(e.getUser(), ROLE_0).queue();
        }
    }
}
