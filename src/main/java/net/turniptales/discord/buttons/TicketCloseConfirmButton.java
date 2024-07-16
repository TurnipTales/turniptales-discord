package net.turniptales.discord.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static net.turniptales.discord.TurnipTalesDiscord.discordBotProperties;

public class TicketCloseConfirmButton extends ButtonBase {

    public TicketCloseConfirmButton() {
        super("btn_ticket_close_confirm");
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        String userName = event.getChannel().getName().split("-")[1];
        discordBotProperties.getTicketCategory().getChannels().stream()
                .filter(guildChannel -> guildChannel.getName().startsWith("ticket-") && guildChannel.getName().contains(userName))
                .forEach(guildChannel -> guildChannel.delete().queue());
    }
}
