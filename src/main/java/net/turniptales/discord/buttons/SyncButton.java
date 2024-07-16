package net.turniptales.discord.buttons;

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

public class SyncButton extends ButtonBase {

    private final Map<User, Long> lastSync = new HashMap<>();

    public SyncButton() {
        super("btn_sync");
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        User user = event.getUser();
        if (currentTimeMillis() - this.lastSync.getOrDefault(user, 0L) <= SECONDS.toMillis(30)) {
            event.reply("Du hast bereits vor weniger als 30 Sekunden deine Berechtigungen synchronisiert!").setEphemeral(true).queue();
            return;
        }

        this.lastSync.put(user, currentTimeMillis());

        ResponseEntity<ConnectionDataValue> responseEntity = api.getData(user.getId());

        boolean connected = responseEntity.getStatusCode().is2xxSuccessful();

        if (connected) {
            synchronise(responseEntity.getBody());
            event.reply("Du hast deine Rechte synchronisiert.\n-# 🚮 <t:" + (currentTimeMillis() / 1000 + 10) + ":R>").setEphemeral(true).queue();
            event.getHook().deleteOriginal().queueAfter(10, SECONDS);
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
        String accountUserId = event.getUser().getId();

        ResponseEntity<String> response = api.connect(accountUserId, discordCode);
        boolean success = response.getStatusCode().is2xxSuccessful();

        String message;
        if (success) {
            ConnectionDataValue connectionDataValue = api.getData(accountUserId).getBody();
            synchronise(connectionDataValue);
            message = "Du hast deinen Discord Account mit dem Minecraft Account " + connectionDataValue.getMinecraftName() + " verknüpft!";
        } else {
            message = "Dein Discord Account konnte nicht mit deinem Minecraft Account verknüpft werden.";
        }

        event.reply(message + "\n-# 🚮 <t:" + (currentTimeMillis() / 1000 + 10) + ":R>").setEphemeral(true).queue();
        event.getHook().deleteOriginal().queueAfter(10, SECONDS);
    }

    private void synchronise(ConnectionDataValue connectionDataValue) {
        System.out.println(connectionDataValue.getMinecraftName());
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
