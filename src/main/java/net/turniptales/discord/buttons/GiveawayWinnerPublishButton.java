package net.turniptales.discord.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static net.turniptales.discord.commands.GiveawayCommand.giveaways;
import static net.turniptales.discord.common.services.UtilService.sendSelfDeletingMessage;

public class GiveawayWinnerPublishButton extends ButtonBase {

    public GiveawayWinnerPublishButton() {
        super("btn_giveaway_winner_publish_button");
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        giveaways.get(event.getUser()).publishWinner();
        sendSelfDeletingMessage(event, "Gewinner veröffentlicht!");
    }
}
