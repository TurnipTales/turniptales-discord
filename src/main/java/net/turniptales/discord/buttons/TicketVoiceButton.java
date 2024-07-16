package net.turniptales.discord.buttons;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.EnumSet;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static net.dv8tion.jda.api.Permission.VIEW_CHANNEL;
import static net.turniptales.discord.TurnipTalesDiscord.discordBotProperties;
import static net.turniptales.discord.common.services.UtilService.sendSelfDeletingMessage;

public class TicketVoiceButton extends ButtonBase {

    public TicketVoiceButton() {
        super("btn_ticket_voice");
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        Member member = event.getMember();

        Optional<GuildChannel> optionalGuildChannel = discordBotProperties.getTicketCategory().getChannels().stream()
                .filter(guildChannel -> guildChannel instanceof VoiceChannel)
                .filter(guildChannel -> guildChannel.getName().equalsIgnoreCase("ticket-" + member.getUser().getName()))
                .findFirst();

        if (optionalGuildChannel.isPresent()) {
            sendSelfDeletingMessage(event, "Für dieses Ticket gibt es bereits einen Voice-Channel!");
            return;
        }

        String userName = requireNonNull(member).getUser().getName();

        discordBotProperties.getTicketCategory().createVoiceChannel("ticket-" + userName)
                .addPermissionOverride(discordBotProperties.getGuild().getPublicRole(), null, EnumSet.of(VIEW_CHANNEL))
                .addPermissionOverride(member, EnumSet.of(VIEW_CHANNEL), null)
                .addPermissionOverride(discordBotProperties.getSeniorModeratorRole(), EnumSet.of(VIEW_CHANNEL), null)
                .addPermissionOverride(discordBotProperties.getModeratorRole(), EnumSet.of(VIEW_CHANNEL), null)
                .addPermissionOverride(discordBotProperties.getSupporterRole(), EnumSet.of(VIEW_CHANNEL), null)
                .queue(voiceChannel -> sendSelfDeletingMessage(event, "Voice-Channel erstellt!"));
    }
}
