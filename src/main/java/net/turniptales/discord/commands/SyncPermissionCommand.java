package net.turniptales.discord.commands;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static java.util.Objects.requireNonNull;
import static net.turniptales.discord.common.services.UtilService.synchronise;

@Log4j2
public class SyncPermissionCommand extends CommandBase {

    public SyncPermissionCommand(String name) {
        super(name);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        Member member = requireNonNull(event.getOption("member")).getAsMember();
        synchronise(event, requireNonNull(member));
    }
}
