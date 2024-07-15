package net.turniptales.discord.events;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.EnumSet;
import java.util.Objects;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.dv8tion.jda.api.Permission.VIEW_CHANNEL;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;
import static net.dv8tion.jda.api.interactions.components.buttons.Button.danger;
import static net.dv8tion.jda.api.interactions.components.buttons.Button.secondary;
import static net.dv8tion.jda.api.interactions.components.buttons.Button.success;
import static net.dv8tion.jda.api.interactions.components.text.TextInputStyle.PARAGRAPH;
import static net.dv8tion.jda.api.interactions.modals.Modal.create;
import static net.turniptales.discord.TurnipTalesDiscord.discordBotProperties;
import static net.turniptales.discord.commands.GiveawayCommand.giveaways;
import static net.turniptales.discord.commands.SurveyCommand.pendingSurveys;

public class ButtonInteractionListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        String componentId = e.getComponentId();
        Category ticketCategory = discordBotProperties.getTicketCategory();

        switch (componentId) {
            case "createTicketAddButton" -> {
                Member member = e.getMember();
                assert member != null;
                String memberId = member.getId();

                boolean hasTicketChannel = ticketCategory.getChannels().stream()
                        .map(guildChannel -> discordBotProperties.getGuild().getTextChannelById(guildChannel.getId()))
                        .filter(Objects::nonNull)
                        .map(StandardGuildMessageChannel::getTopic)
                        .filter(Objects::nonNull)
                        .anyMatch(s -> s.contains(memberId));

                if (hasTicketChannel) {
                    e.reply("Du hast bereits einen Ticket Channel!").setEphemeral(true).queue();
                    return;
                }

                TextInput minecraftNameTextInput = TextInput.create("minecraft_name_input", "Minecraft Name", TextInputStyle.SHORT)
                        .setMinLength(3)
                        .setMaxLength(16)
                        .setRequired(true)
                        .build();

                TextInput logInput = TextInput.create("topic_input", "Anliegen", PARAGRAPH)
                        .setRequired(true)
                        .setPlaceholder("Hey, wie kann ich mich im Bauteam bewerben?")
                        .build();

                Modal ticketModal = create("ticket_modal", "Neues Ticket")
                        .addComponents(ActionRow.of(minecraftNameTextInput), ActionRow.of(logInput))
                        .build();

                e.replyModal(ticketModal).queue();
            }
            case "closeTicket" -> {
                TextChannel textChannel = e.getChannel().asTextChannel();
                if (Objects.equals(textChannel.getParentCategory(), ticketCategory) && textChannel.getName().startsWith("ticket-")) {
                    e.reply("Möchtest Du das Ticket wirklich schließen?")
                            .addActionRow(danger("closeTicketConfirm", "Bestätigen"), secondary("closeTicketAbort", "Abbrechen"))
                            .queue();
                }
            }
            case "closeTicketConfirm" -> {
                TextChannel textChannel = e.getChannel().asTextChannel();
                if (Objects.equals(textChannel.getParentCategory(), ticketCategory) && textChannel.getName().startsWith("ticket-")) {
                    textChannel.delete().queue();
                }
            }
            case "closeTicketAbort" -> e.getMessage().delete().queue();
            case "sendSurvey" -> {
                Member member = e.getMember();
                assert member != null;

                ofNullable(pendingSurveys.remove(member)).ifPresentOrElse(survey -> {
                    discordBotProperties.getSurveyTextChannel().sendMessage("@everyone").setEmbeds(survey.toEmbed(member))
                            .queue(message -> survey.getReactions().forEach(emoji -> message.addReaction(emoji).queue()));

                    e.reply("Umfrage veröffentlicht!").setEphemeral(true).queue();
                    e.getHook().deleteOriginal().queueAfter(5, SECONDS);
                }, () -> {
                    e.reply("Es gibt keine Umfrage die veröffentlicht werden kann!").setEphemeral(true).queue();
                    e.getHook().deleteOriginal().queueAfter(5, SECONDS);
                });
            }
            case "publishWinner" -> {
                giveaways.get(e.getUser()).publishWinner();
                e.reply("Gewinner veröffentlicht!").setEphemeral(true).queue();
                e.getHook().deleteOriginal().queueAfter(5, SECONDS);
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {
        String modalId = e.getModalId();

        switch (modalId) {
            case "ticket_modal" -> {
                Member member = e.getMember();
                assert member != null;
                String memberId = member.getId();
                String userName = member.getUser().getName();

                String minecraftName = e.getValue("minecraft_name_input").getAsString();
                String log = e.getValue("topic_input").getAsString();

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
                                .queue(message -> e.reply("Du hast ein Ticket erstellt: " + message.getJumpUrl()).setEphemeral(true).queue()));
            }
        }
    }
}
