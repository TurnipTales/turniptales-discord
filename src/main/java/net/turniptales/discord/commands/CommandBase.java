package net.turniptales.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class CommandBase extends ListenerAdapter {

    private final String name;

    public CommandBase(String name) {
        this.name = name;
    }

    public abstract void onCommand(SlashCommandInteractionEvent event);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(this.name)) {
            onCommand(event);
        }
    }
}
