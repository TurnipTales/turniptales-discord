package net.turniptales.discord.services;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Activity;
import net.turniptales.discord.Application;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;
import static net.dv8tion.jda.api.entities.Activity.playing;

@Log4j2
@Component
public class ActivitySyncService {

    public ActivitySyncService() {
        long tenMinutesInMillis = TimeUnit.MINUTES.toMillis(10);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long startTime = currentTimeMillis();
                log.info("Activity synchronising: started");

                Activity activity = playing("auf TurnipTales.net");
                Application.TURNIPTALES_BOT.getPresence().setActivity(activity);

                log.info("Activity synchronising: finished in {}ms", currentTimeMillis() - startTime);
            }
        }, tenMinutesInMillis - currentTimeMillis() % tenMinutesInMillis, tenMinutesInMillis);
        log.info("Activity synchronising: scheduled");
    }
}
