package net.turniptales.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.turniptales.discord.common.api.model.ConnectionDataValue;
import org.springframework.http.ResponseEntity;

import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.turniptales.discord.TurnipTalesDiscord.api;

public class VerifyCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!e.getName().equals("verify")) {
            return;
        }

        OptionMapping codeOptionMapping = e.getOption("code");
        if (isNull(codeOptionMapping)) {
            e.reply("Gib einen Verifizierungscode an!").setEphemeral(true).queue();
            return;
        }

        Api api = new Api();

        long accountUserId = e.getUser().getIdLong();
        ResponseEntity<Void> response = api.connect(accountUserId, codeOptionMapping.getAsString());
        boolean success = response.getStatusCode().is2xxSuccessful();

        String message;
        if (success) {
            ConnectionDataValue connectionDataValue = api.getData(accountUserId);
            message = "Du hast deinen Discord Account mit dem Minecraft Account " + connectionDataValue.getMinecraftName() + " verknüpft!";
        } else {
            message = "Dein Discord Account konnte nicht mit deinem Minecraft Account verknüpft werden.";
        }

        e.reply(message).setEphemeral(true).queue();
        e.getHook().deleteOriginal().queueAfter(5, SECONDS);
    }
}
