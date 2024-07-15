package net.turniptales.discord.events;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static java.util.Objects.nonNull;
import static net.turniptales.discord.TurnipTalesDiscord.discordBotProperties;

public class MessageReactionListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        MessageChannelUnion channel = e.getChannel();
        User user = e.getUser();
        if (channel.equals(discordBotProperties.getSurveyTextChannel()) && nonNull(user) && !user.isBot()) {
            channel.retrieveMessageById(e.getMessageIdLong()).queue(message -> message.getReactions().forEach(messageReaction -> messageReaction.retrieveUsers().queue(users -> {
                EmojiUnion emoji = messageReaction.getEmoji();
                if (users.contains(user) && !emoji.equals(e.getEmoji())) {
                    message.removeReaction(emoji, user).queue();
                }
            })));
        }
    }
}
