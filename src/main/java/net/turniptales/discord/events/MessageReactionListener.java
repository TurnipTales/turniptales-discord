package net.turniptales.discord.events;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static java.util.Objects.nonNull;
import static net.turniptales.discord.Config.SURVEY_TEXT_CHANNEL;

public class MessageReactionListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        MessageChannelUnion channel = e.getChannel();
        User user = e.getUser();
        if (channel.equals(SURVEY_TEXT_CHANNEL) && nonNull(user) && !user.isBot()) {
            channel.retrieveMessageById(e.getMessageIdLong()).queue(message -> message.getReactions()
                    .forEach(messageReaction -> messageReaction.removeReaction(user).queue()));
        }
    }
}
