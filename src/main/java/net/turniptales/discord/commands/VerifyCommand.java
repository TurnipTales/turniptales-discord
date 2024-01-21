package net.turniptales.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.turniptales.discord.common.api.Api;
import net.turniptales.discord.common.api.model.PlayerStats;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

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

        PlayerStats playerStats = new Api().getPlayerStatsByDiscordUserIdVerify(e.getUser().getIdLong(), codeOptionMapping.getAsString());
        String message;
        if (nonNull(playerStats)) {
            message = "Du hast deinen Discord Account mit dem Minecraft Account " + playerStats.getMinecraftName() + " verknüpft!\nDiese Nachricht zerstört sich gleich von selbst...";
        } else {
            message = "Dein Discord Account konnte nicht mit deinem Minecraft Account verknüpft werden.";
        }

        e.reply(message).setEphemeral(true).queue();
        e.getHook().deleteOriginal().queueAfter(5, SECONDS);
    }
}
