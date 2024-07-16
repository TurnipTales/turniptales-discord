package net.turniptales.discord.buttons;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.turniptales.discord.common.api.model.ConnectionDataValue;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.dv8tion.jda.api.interactions.modals.Modal.create;
import static net.turniptales.discord.TurnipTalesDiscord.api;
import static net.turniptales.discord.TurnipTalesDiscord.discordBotProperties;
import static net.turniptales.discord.common.services.RoleSyncService.getHighestTimeRole;
import static net.turniptales.discord.common.services.RoleSyncService.getTimeRoles;
import static net.turniptales.discord.common.services.UtilService.sendSelfDeletingMessage;

public class SyncButton extends ButtonBase {

    private final Map<User, Long> lastSync = new HashMap<>();

    public SyncButton() {
        super("btn_sync");
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        User user = event.getUser();
        if (currentTimeMillis() - this.lastSync.getOrDefault(user, 0L) <= SECONDS.toMillis(30)) {
            sendSelfDeletingMessage(event, "Du hast bereits vor weniger als 30 Sekunden deine Berechtigungen synchronisiert!");
            return;
        }

        ResponseEntity<ConnectionDataValue> responseEntity = api.getData(user.getId());

        boolean connected = responseEntity.getStatusCode().is2xxSuccessful();

        if (connected) {
            ConnectionDataValue connectionDataValue = responseEntity.getBody();
            synchronise(event.getMember(), requireNonNull(connectionDataValue));
            sendSelfDeletingMessage(event, "Du hast deine Rechte mit dem Minecraft Account " + connectionDataValue.getMinecraftName() + " synchronisiert!");
            this.lastSync.put(user, currentTimeMillis());
        } else {
            event.replyModal(getVerificationModal()).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equalsIgnoreCase("mdl_verification")) {
            return;
        }

        String discordCode = requireNonNull(event.getValue("tip_discord_code")).getAsString();
        User user = event.getUser();
        String accountUserId = user.getId();

        ResponseEntity<String> response = api.connect(accountUserId, discordCode);
        boolean success = response == null;

        if (success) {
            ConnectionDataValue connectionDataValue = api.getData(accountUserId).getBody();
            synchronise(event.getMember(), requireNonNull(connectionDataValue));
            sendSelfDeletingMessage(event, "Du hast deine Rechte mit dem Minecraft Account " + connectionDataValue.getMinecraftName() + " synchronisiert!");
            this.lastSync.put(user, currentTimeMillis());
        } else {
            sendSelfDeletingMessage(event, "Dein Discord Account konnte nicht mit deinem Minecraft Account verknüpft werden.");
        }
    }

    public static void synchronise(Member member, ConnectionDataValue connectionDataValue) {
        Guild guild = discordBotProperties.getGuild();

        guild.removeRoleFromMember(member, discordBotProperties.getSeniorModeratorRole()).queue();
        guild.removeRoleFromMember(member, discordBotProperties.getModeratorRole()).queue();
        guild.removeRoleFromMember(member, discordBotProperties.getSupporterRole()).queue();
        guild.removeRoleFromMember(member, discordBotProperties.getTeamBuilderRole()).queue();
        guild.removeRoleFromMember(member, discordBotProperties.getTeamContentRole()).queue();
        guild.removeRoleFromMember(member, discordBotProperties.getTeamSocialMediaRole()).queue();
        getTimeRoles().forEach(role -> guild.removeRoleFromMember(member, role).queue());

        guild.addRoleToMember(member, discordBotProperties.getPlayerRole()).queue();

        switch (connectionDataValue.getRole()) { // ignore ADMINISTRATOR and HEAD_OF_DEVELOPMENT due to no permission
            case "SENIOR_MODERATOR" -> guild.addRoleToMember(member, discordBotProperties.getSeniorModeratorRole()).queue();
            case "MODERATOR" -> guild.addRoleToMember(member, discordBotProperties.getModeratorRole()).queue();
            case "SUPPORTER" -> guild.addRoleToMember(member, discordBotProperties.getSupporterRole()).queue();
        }

        connectionDataValue.getTeams().forEach(teamName -> {
            switch (teamName) {
                case "BUILDER" -> guild.addRoleToMember(member, discordBotProperties.getTeamBuilderRole()).queue();
                case "CONTENT" -> guild.addRoleToMember(member, discordBotProperties.getTeamContentRole()).queue();
                case "SOCIAL_MEDIA" -> guild.addRoleToMember(member, discordBotProperties.getTeamSocialMediaRole()).queue();
            }
        });

        guild.addRoleToMember(member, getHighestTimeRole(requireNonNull(member).getTimeJoined())).queue();
    }

    private Modal getVerificationModal() {
        TextInput discordCodeInput = TextInput.create("tip_discord_code", "Discord Code", TextInputStyle.SHORT)
                .setMinLength(8)
                .setMaxLength(8)
                .setRequired(true)
                .build();

        return create("mdl_verification", "Verifizierung")
                .addComponents(ActionRow.of(discordCodeInput))
                .build();
    }
}
