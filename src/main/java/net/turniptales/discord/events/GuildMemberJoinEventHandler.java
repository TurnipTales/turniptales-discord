package net.turniptales.discord.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.turniptales.discord.Config;

import static java.awt.Color.GREEN;

public class GuildMemberJoinEventHandler extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        Guild guild = e.getGuild();
        TextChannel systemChannel = guild.getSystemChannel();
        if (systemChannel == null) {
            return;
        }

        User user = e.getUser();
        int memberCount = guild.getMemberCount();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(GREEN)
                .setTitle(user.getAsTag())
                .setThumbnail(user.getAvatarUrl())
                .setDescription("Ist **" + guild.getName() + "** beigetreten!\nHier sind nun **" + memberCount + "** Spieler.");

        systemChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        systemChannel.getManager().setTopic("Aktuelle Spieler: " + memberCount).queue();

        assert Config.ROLE_0 != null;
        guild.addRoleToMember(user, Config.ROLE_0).queue();
    }
}
