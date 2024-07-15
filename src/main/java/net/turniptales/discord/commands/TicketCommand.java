package net.turniptales.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.Color;

import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;
import static net.dv8tion.jda.api.interactions.components.buttons.Button.success;
import static net.turniptales.discord.TurnipTalesDiscord.discordBotProperties;

public class TicketCommand extends CommandBase {

    public TicketCommand() {
        super("ticket");
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(0x609fee))
                .setTitle("Ticket")
                .addField("🎫 **Hier kannst du ein Ticket erstellen um schnell Hilfe zu erhalten oder sonstige Fragen zu klären.**", "Bei der Erstellung eines Tickets wirst du nach deinem Minecraft Namen und Anliegen gefragt.", false);

        discordBotProperties.getTicketTextChannel()
                .sendMessageEmbeds(embedBuilder.build())
                .addActionRow(success("createTicketAddButton", "Neues Ticket").withEmoji(fromUnicode("U+1F3AB")))
                .queue();
        event.getHook().deleteOriginal().queue();
    }
}
