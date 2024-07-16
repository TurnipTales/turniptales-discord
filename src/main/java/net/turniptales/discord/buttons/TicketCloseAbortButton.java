package net.turniptales.discord.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class TicketCloseAbortButton extends ButtonBase {

    public TicketCloseAbortButton() {
        super("btn_ticket_close_abort");
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        String messageId = event.getMessage().getId();
        event.getChannel().deleteMessageById(messageId).queue();
    }
}
