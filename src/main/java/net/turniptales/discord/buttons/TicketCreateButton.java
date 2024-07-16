package net.turniptales.discord.buttons;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.EnumSet;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static net.dv8tion.jda.api.Permission.VIEW_CHANNEL;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;
import static net.dv8tion.jda.api.interactions.components.buttons.Button.success;
import static net.dv8tion.jda.api.interactions.components.text.TextInputStyle.PARAGRAPH;
import static net.dv8tion.jda.api.interactions.modals.Modal.create;
import static net.turniptales.discord.TurnipTalesDiscord.discordBotProperties;

public class TicketCreateButton extends ButtonBase {

    public TicketCreateButton() {
        super("btn_ticket_create");
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        String memberId = member.getId();

        boolean hasTicketChannel = discordBotProperties.getTicketCategory().getChannels().stream()
                .map(guildChannel -> discordBotProperties.getGuild().getTextChannelById(guildChannel.getId()))
                .filter(Objects::nonNull)
                .map(StandardGuildMessageChannel::getTopic)
                .filter(Objects::nonNull)
                .anyMatch(s -> s.contains(memberId));

        if (hasTicketChannel) {
            event.reply("Du hast bereits einen Ticket Channel!").setEphemeral(true).queue();
            return;
        }

        event.replyModal(getTicketModal()).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equalsIgnoreCase("mdl_ticket")) {
            return;
        }

        Member member = event.getMember();
        assert member != null;
        String memberId = member.getId();
        String userName = member.getUser().getName();

        String minecraftName = requireNonNull(event.getValue("tip_minecraft_name")).getAsString();
        String log = requireNonNull(event.getValue("tip_topic")).getAsString();

        Role supporterRole = discordBotProperties.getSupporterRole();
        Role moderatorRole = discordBotProperties.getModeratorRole();
        discordBotProperties.getTicketCategory().createTextChannel("ticket-" + userName)
                .setTopic("Ticket von " + userName + " (" + memberId + ")")
                .addPermissionOverride(discordBotProperties.getGuild().getPublicRole(), null, EnumSet.of(VIEW_CHANNEL))
                .addPermissionOverride(member, EnumSet.of(VIEW_CHANNEL), null)
                .addPermissionOverride(discordBotProperties.getSeniorModeratorRole(), EnumSet.of(VIEW_CHANNEL), null)
                .addPermissionOverride(moderatorRole, EnumSet.of(VIEW_CHANNEL), null)
                .addPermissionOverride(supporterRole, EnumSet.of(VIEW_CHANNEL), null)
                .queue(textChannel -> textChannel
                        .sendMessage("Hey " + member.getAsMention() + "! Danke dass du ein Ticket erstellt hast. Die " + supporterRole.getAsMention() + " und " + moderatorRole.getAsMention() + " werden Dir schnellstmöglich deine Frage beantworten oder Dir helfen.\n"
                                + "Spieler:  " + minecraftName + "\n"
                                + "Anliegen: " + log)
                        .addActionRow(success("closeTicket", "Ticket schließen").withEmoji(fromUnicode("U+1F512")))
                        .queue(message -> event.reply("Du hast ein Ticket erstellt: " + message.getJumpUrl()).setEphemeral(true).queue()));
    }

    private Modal getTicketModal() {
        TextInput minecraftNameTextInput = TextInput.create("tip_minecraft_name", "Minecraft Name", TextInputStyle.SHORT)
                .setMinLength(3)
                .setMaxLength(16)
                .setRequired(true)
                .build();

        TextInput logInput = TextInput.create("tip_topic", "Anliegen", PARAGRAPH)
                .setRequired(true)
                .setPlaceholder("Hey, wie kann ich mich im Bauteam bewerben?")
                .build();

        return create("mdl_ticket", "Neues Ticket")
                .addComponents(ActionRow.of(minecraftNameTextInput), ActionRow.of(logInput))
                .build();
    }
}
