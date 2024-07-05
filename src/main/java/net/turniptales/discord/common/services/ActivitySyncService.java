package net.turniptales.discord.common.services;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MINUTES;
import static net.dv8tion.jda.api.entities.Activity.playing;
import static net.turniptales.discord.TurnipTalesDiscord.turniptalesBot;

@Log4j2
@Component
public class ActivitySyncService {

    public ActivitySyncService() {
        long tenMinutesInMillis = MINUTES.toMillis(10);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long startTime = currentTimeMillis();
                log.info("Activity synchronising: started");

                Activity activity = playing("auf TurnipTales.net");
                turniptalesBot.getPresence().setActivity(activity);

                log.info("Activity synchronising: finished in {}ms", currentTimeMillis() - startTime);
            }
        }, tenMinutesInMillis - currentTimeMillis() % tenMinutesInMillis, tenMinutesInMillis);
        log.info("Activity synchronising: scheduled");
    }
}
