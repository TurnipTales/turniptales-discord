package net.turniptales.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.turniptales.discord.common.api.model.Verification;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.turniptales.discord.common.api.API.getVerification;

public class VerifyCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!e.getName().equals("verify")) return;

        OptionMapping codeOptionMapping = e.getOption("code");
        if (!nonNull(codeOptionMapping)) {
            e.reply("Gib einen Verifizierungscode an!").setEphemeral(true).queue();
            return;
        }

        Verification verification = getVerification(codeOptionMapping.getAsString(), requireNonNull(e.getMember()).getIdLong());

        e.reply("Du hast deinen Discord Account mit dem Minecraft Account " + verification.getMinecraftName() + " verknüpft!\nDiese Nachricht zerstört sich gleich von selbst...").setEphemeral(true).queue();
        e.getHook().deleteOriginal().queueAfter(5, SECONDS);
    }
}
