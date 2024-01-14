package net.turniptales.discord.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static java.awt.Color.RED;

public class GuildBanEventHandler extends ListenerAdapter {

    @Override
    public void onGuildBan(GuildBanEvent e) {
        Guild guild = e.getGuild();
        TextChannel systemChannel = guild.getSystemChannel();
        if (systemChannel == null) {
            return;
        }

        User user = e.getUser();
        int memberCount = guild.getMemberCount();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(RED)
                .setTitle(user.getAsTag())
                .setThumbnail(user.getAvatarUrl())
                .setDescription("Wurde von **" + guild.getName() + "** gebannt!");

        systemChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        systemChannel.getManager().setTopic("Aktuelle Spieler: " + memberCount).queue();
    }
}
