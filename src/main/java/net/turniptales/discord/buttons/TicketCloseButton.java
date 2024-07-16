package net.turniptales.discord.buttons;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.Objects;

import static net.dv8tion.jda.api.interactions.components.buttons.Button.danger;
import static net.dv8tion.jda.api.interactions.components.buttons.Button.secondary;
import static net.turniptales.discord.TurnipTalesDiscord.discordBotProperties;

public class TicketCloseButton extends ButtonBase {

    public TicketCloseButton() {
        super("btn_ticket_close");
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();
        if (Objects.equals(textChannel.getParentCategory(), discordBotProperties.getTicketCategory()) && textChannel.getName().startsWith("ticket-")) {
            event.reply("Möchtest Du das Ticket wirklich schließen?")
                    .addActionRow(danger("btn_ticket_close_confirm", "Bestätigen"), secondary("btn_ticket_close_abort", "Abbrechen"))
                    .queue();
        }
    }
}
