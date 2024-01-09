package net.turniptales.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.turniptales.discord.Config;

import java.awt.Color;
import java.util.Objects;

import static net.dv8tion.jda.api.Permission.ADMINISTRATOR;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;
import static net.dv8tion.jda.api.interactions.components.buttons.Button.success;

public class TicketCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!e.getName().equals("ticket") || !Objects.requireNonNull(e.getMember()).hasPermission(ADMINISTRATOR))
            return;

        e.deferReply(true).queue();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(0x609fee))
                .setTitle("Ticket")
                .addField("🎫 **Hier kannst du ein Ticket erstellen um schnell Hilfe zu erhalten oder sonstige Fragen zu klären.**", "Bei der Erstellung eines Tickets wirst du nach deinem Minecraft Namen und Anliegen gefragt.", false);

        assert Config.TICKET_TEXT_CHANNEL != null;
        Config.TICKET_TEXT_CHANNEL
                .sendMessageEmbeds(embedBuilder.build())
                .addActionRow(success("createTicketAddButton", "Neues Ticket").withEmoji(fromUnicode("U+1F3AB")))
                .queue();
        e.getHook().deleteOriginal().queue();
    }
}
