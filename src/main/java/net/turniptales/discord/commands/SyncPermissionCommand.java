package net.turniptales.discord.commands;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.turniptales.discord.common.api.model.ConnectionDataValue;
import org.springframework.http.ResponseEntity;

import static java.util.Objects.requireNonNull;
import static net.turniptales.discord.TurnipTalesDiscord.api;
import static net.turniptales.discord.buttons.SyncButton.synchronise;
import static net.turniptales.discord.common.services.UtilService.sendSelfDeletingMessage;

@Log4j2
public class SyncPermissionCommand extends CommandBase {

    public SyncPermissionCommand(String name) {
        super(name);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        Member member = requireNonNull(event.getOption("member")).getAsMember();
        ResponseEntity<ConnectionDataValue> connectionDataValueResponseEntity = api.getData(requireNonNull(member).getId());
        if (connectionDataValueResponseEntity.getStatusCode().is2xxSuccessful()) {
            ConnectionDataValue connectionDataValue = connectionDataValueResponseEntity.getBody();
            synchronise(member, requireNonNull(connectionDataValue));
            sendSelfDeletingMessage(event, "Die Rechte für " + member.getEffectiveName() + " (" + connectionDataValue.getMinecraftName() + ") wurden synchronisiert!");
        }
    }
}
