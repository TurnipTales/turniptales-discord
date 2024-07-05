package net.turniptales.discord.commands;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.dv8tion.jda.api.Permission.ADMINISTRATOR;
import static net.dv8tion.jda.api.interactions.components.buttons.Button.success;

@Log4j2
public class GiveawayCommand extends ListenerAdapter {

    public static final Map<User, Giveaway> giveaways = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!e.getName().equals("giveaway") || !Objects.requireNonNull(e.getMember()).hasPermission(ADMINISTRATOR)) {
            return;
        }

        OptionMapping messageOptionMapping = e.getOption("message");

        if (nonNull(messageOptionMapping)) {
            e.deferReply().setEphemeral(true).queue();

            Message message = e.getChannel().retrieveMessageById(messageOptionMapping.getAsString()).complete();
            List<MessageReaction> reactions = message.getReactions();
            List<User> usersWhoReacted = getUsersWhoReacted(reactions);

            List<User> uniqueUsers = usersWhoReacted.stream()
                    .distinct()
                    .toList();

            User winner = uniqueUsers.stream()
                    .filter(user -> !user.isBot())
                    .toList().get(new Random().nextInt(uniqueUsers.size()));

            Giveaway giveaway = new Giveaway(message, uniqueUsers, winner);
            giveaways.put(e.getUser(), giveaway);

            e.getHook()
                    .sendMessage("Reaktionen: " + usersWhoReacted.size() + " (" + reactions.size() + ")\nNutzer: " + uniqueUsers.size() + "\n\nGewinner: " + winner.getAsMention())
                    .addActionRow(success("publishWinner", "Gewinner veröffentlichen"))
                    .queue();
        } else {
            e.reply("Es muss eine Nachrichten ID angegeben werden.").setEphemeral(true).queue();
            e.getHook().deleteOriginal().queueAfter(5, SECONDS);
        }
    }

    public List<User> getUsersWhoReacted(List<MessageReaction> reactions) {
        List<CompletableFuture<List<User>>> messageReactionUsersFuture = new ArrayList<>();

        reactions.forEach(reaction -> {
            CompletableFuture<List<User>> reactionUsersFuture = new CompletableFuture<>();
            reaction.retrieveUsers().queue(reactionUsersFuture::complete);
            messageReactionUsersFuture.add(reactionUsersFuture);
        });

        return messageReactionUsersFuture.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        log.error(e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .toList();
    }

    public static class Giveaway {

        private final Message message;
        private final List<User> users;
        private final User winner;

        public Giveaway(Message message, List<User> users, User winner) {
            this.message = message;
            this.users = users;
            this.winner = winner;
        }

        public void publishWinner() {
            this.message.reply("@here\n:tada: **Ein Gewinner wurde gezogen!** :tada:\n\nAus `" + this.users.size() + "` Teilnehmern wurde " + this.winner.getAsMention() + " ausgelost. Herzlichen Glückwunsch!").queue();
        }
    }
}
