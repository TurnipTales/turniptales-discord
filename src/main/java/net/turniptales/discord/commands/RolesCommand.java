package net.turniptales.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static net.turniptales.discord.TurnipTalesDiscord.discordBotProperties;

public class RolesCommand extends CommandBase {

    public RolesCommand(String name) {
        super(name);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        event.reply("""
                Die Standard Rolle für jedes Mitglied ist `%s`.

                Wenn man längere Zeit auf diesem Discord ist, wird einem automatisch eine Rolle zugewiesen. Je nachdem, wie lang man schon auf diesem Discord ist, erhält man eine höhere Rolle:

                `%s` ➜ direkt nach Serverbeitritt
                `%s` ➜ nach einer Woche
                `%s` ➜ nach zwei Wochen
                `%s` ➜ nach einem Monat
                `%s` ➜ nach drei Monaten
                `%s` ➜ nach sechs Monaten
                `%s` ➜ nach einem Jahr
                `%s` ➜ nach zwei Jahren
                `%s` ➜ nach drei Jahren

                So sieht man auch gut, wer schon lang dabei ist und sich schon gut auskennen könnte. Die Rollen werden durch den TurnipTales Bot automatisch vergeben und können dadurch nicht erstattet werden, wenn man den Server zwischenzeitlich verlässt.
                """.formatted(
                discordBotProperties.getPlayerRole().getName(),
                discordBotProperties.getRole0().getName(),
                discordBotProperties.getRole1Week().getName(),
                discordBotProperties.getRole2Week().getName(),
                discordBotProperties.getRole1Month().getName(),
                discordBotProperties.getRole3Month().getName(),
                discordBotProperties.getRole6Month().getName(),
                discordBotProperties.getRole1Year().getName(),
                discordBotProperties.getRole2Year().getName(),
                discordBotProperties.getRole3Year().getName())).queue();
    }
}
