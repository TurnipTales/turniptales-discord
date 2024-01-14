package net.turniptales.discord.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.turniptales.discord.Config;

import static java.awt.Color.ORANGE;

public class GuildMemberRemoveEventHandler extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
        Guild guild = e.getGuild();
        TextChannel systemChannel = Config.SYSTEM_TEXT_CHANNEL;
        if (systemChannel == null) {
            return;
        }

        User user = e.getUser();
        int memberCount = guild.getMemberCount();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(ORANGE)
                .setTitle(user.getAsTag())
                .setThumbnail(user.getAvatarUrl())
                .setDescription("Hat **" + guild.getName() + "** verlassen!\nHier sind nun **" + memberCount + "** Spieler.");

        systemChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        systemChannel.getManager().setTopic("Aktuelle Spieler: " + memberCount).queue();
    }
}
