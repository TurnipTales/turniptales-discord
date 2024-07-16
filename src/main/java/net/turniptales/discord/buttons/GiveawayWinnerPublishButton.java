package net.turniptales.discord.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.turniptales.discord.commands.GiveawayCommand.giveaways;

public class GiveawayWinnerPublishButton extends ButtonBase {

    public GiveawayWinnerPublishButton() {
        super("btn_giveaway_winner_publish_button");
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        giveaways.get(event.getUser()).publishWinner();
        event.reply("Gewinner veröffentlicht!\n-# 🚮 <t:" + (currentTimeMillis() / 1000 + 10) + ":R>").setEphemeral(true).queue();
        event.getHook().deleteOriginal().queueAfter(10, SECONDS);
    }
}
