package net.turniptales.discord.commands;

import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.awt.Color.CYAN;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.dv8tion.jda.api.Permission.ADMINISTRATOR;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;
import static net.dv8tion.jda.api.interactions.components.buttons.Button.success;
import static net.turniptales.discord.Config.BOT;

public class SurveyCommand extends ListenerAdapter {

    public static Map<Member, Survey> pendingSurveys = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!e.getName().equals("umfrage") || !Objects.requireNonNull(e.getMember()).hasPermission(ADMINISTRATOR)) {
            return;
        }

        OptionMapping questionOptionMapping = e.getOption("question");
        OptionMapping descriptionOptionMapping = e.getOption("description");
        OptionMapping answer1OptionMapping = e.getOption("answer1");
        OptionMapping answer2OptionMapping = e.getOption("answer2");
        OptionMapping answer3OptionMapping = e.getOption("answer3");
        OptionMapping answer4OptionMapping = e.getOption("answer4");
        OptionMapping answer5OptionMapping = e.getOption("answer5");

        List<String> answers = Stream.of(answer1OptionMapping, answer2OptionMapping, answer3OptionMapping, answer4OptionMapping, answer5OptionMapping)
                .filter(Objects::nonNull)
                .map(OptionMapping::getAsString)
                .toList();

        if (nonNull(questionOptionMapping) && nonNull(descriptionOptionMapping) && answers.size() > 1) {
            Survey survey = new Survey(questionOptionMapping.getAsString(), descriptionOptionMapping.getAsString(), answers);

            Member member = e.getMember();
            assert BOT != null;

            pendingSurveys.put(member, survey);

            e.replyEmbeds(survey.toEmbed(member))
                    .setEphemeral(true)
                    .addActionRow(success("sendSurvey", "Starten"))
                    .queue();
            e.getHook().deleteOriginal().queueAfter(30, SECONDS);
        } else {
            e.reply("Es müssen mindestens eine Frage und zwei Antworten angegeben werden.").setEphemeral(true).queue();
            e.getHook().deleteOriginal().queueAfter(5, SECONDS);
        }
    }

    @Data
    public static class Survey {

        private final String title;
        private final String description;
        private final List<String> answers;

        public MessageEmbed toEmbed(Member member) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(CYAN)
                    .setTitle(this.title)
                    .setAuthor("TurnipTales", "https://turniptales.net/", BOT.getEffectiveAvatarUrl())
                    .setDescription(this.description)
                    .setFooter("Umfrage erstellt von " + member.getEffectiveName(), member.getEffectiveAvatarUrl())
                    .setTimestamp(new Date().toInstant());

            for (int i = 0; i < answers.size(); i++) {
                embedBuilder.addField("Antwort " + (i + 1), this.answers.get(i), false);
            }

            return embedBuilder.build();
        }

        public List<Emoji> getReactions() {
            List<Emoji> emojis = List.of(
                    fromUnicode("U+0031 U+20E3"),
                    fromUnicode("U+0032 U+20E3"),
                    fromUnicode("U+0033 U+20E3"),
                    fromUnicode("U+0034 U+20E3"),
                    fromUnicode("U+0035 U+20E3")
            );

            return emojis.subList(0, this.answers.size());
        }
    }
}
