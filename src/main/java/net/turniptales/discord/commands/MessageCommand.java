package net.turniptales.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.Color;

import static java.util.Objects.requireNonNull;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;
import static net.dv8tion.jda.api.interactions.components.buttons.Button.success;

public class MessageCommand extends CommandBase {

    public MessageCommand(String name) {
        super(name);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        TextChannel textChannel = event.getChannel().asTextChannel();
        switch (requireNonNull(event.getSubcommandName())) {
            case "sync" -> {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setColor(new Color(0x609fee))
                        .setTitle("Verifikation und Synchronisation")
                        .addField("🎡 **Du kannst Deinen Minecraft Account mit Deinem Discord Account verbinden! Dadurch hast Du unter anderem folgende Vorteile:**", "- Statistiken einsehen\n- Rechte synchronisieren\n- schnellerer und besserer Support", false);

                textChannel.sendMessageEmbeds(embedBuilder.build())
                        .addActionRow(success("btn_sync", "Rechte synchronisieren").withEmoji(fromUnicode("U+1F504")))
                        .queue();
            }
            case "ticket" -> {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setColor(new Color(0x609fee))
                        .setTitle("Ticket")
                        .addField("🎫 **Hier kannst du ein Ticket erstellen um schnell Hilfe zu erhalten oder sonstige Fragen zu klären.**", "Bei der Erstellung eines Tickets wirst du nach deinem Minecraft Namen und Anliegen gefragt.", false);

                textChannel
                        .sendMessageEmbeds(embedBuilder.build())
                        .addActionRow(success("btn_ticket_create", "Neues Ticket").withEmoji(fromUnicode("U+1F3AB")))
                        .queue();
            }
        }

        event.getHook().deleteOriginal().queue();
    }
}
