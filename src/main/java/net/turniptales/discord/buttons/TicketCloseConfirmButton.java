package net.turniptales.discord.buttons;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.Objects;

import static net.turniptales.discord.TurnipTalesDiscord.discordBotProperties;

public class TicketCloseConfirmButton extends ButtonBase {

    public TicketCloseConfirmButton() {
        super("btn_ticket_close_confirm");
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();
        if (Objects.equals(textChannel.getParentCategory(), discordBotProperties.getTicketCategory()) && textChannel.getName().startsWith("ticket-")) {
            textChannel.delete().queue();
        }
    }
}
