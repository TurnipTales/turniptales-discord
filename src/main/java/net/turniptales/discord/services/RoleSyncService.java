package net.turniptales.discord.services;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.turniptales.discord.Config;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.HOURS;

@Log4j2
@Component
public class RoleSyncService {

    public RoleSyncService() {
        long sixHoursInMillis = HOURS.toMillis(6);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long startTime = currentTimeMillis();
                log.info("Discord role synchronising: started");

                Guild guild = Config.GUILD;

                assert guild != null;
                guild.loadMembers().get().forEach(member -> getRolesUserShouldHave(member.getTimeJoined()).stream()
                        .filter(role -> !member.getRoles().contains(role))
                        .forEach(role -> {
                            guild.addRoleToMember(member, role).queue();
                            log.info("Discord role synchronising: Add role {} to member {}", role.getName(), member.getEffectiveName());
                        }));

                log.info("Discord role synchronising: finished in {}ms", currentTimeMillis() - startTime);
            }
        }, (sixHoursInMillis - currentTimeMillis() % sixHoursInMillis) + HOURS.toMillis(2), sixHoursInMillis); // 4 10 16 22
        log.info("Discord role synchronising: scheduled");
    }

    private Collection<Role> getRolesUserShouldHave(OffsetDateTime timeJoined) {
        assert Config.ROLE_0 != null;
        Collection<Role> roles = new ArrayList<>(List.of(Config.ROLE_0));

        long joinTimeInMillis = timeJoined.toInstant().toEpochMilli();
        long currentTimeInMillis = currentTimeMillis();

        if (joinTimeInMillis + TimeUnit.DAYS.toMillis(7) <= currentTimeInMillis) { // 1 Woche
            roles.add(Config.ROLE_1_WEEK);
        }

        if (joinTimeInMillis + TimeUnit.DAYS.toMillis(14) <= currentTimeInMillis) { // 2 Wochen
            roles.add(Config.ROLE_2_WEEK);
        }

        if (joinTimeInMillis + TimeUnit.DAYS.toMillis(30) <= currentTimeInMillis) { // 1 Monat
            roles.add(Config.ROLE_1_MONTH);
        }

        if (joinTimeInMillis + TimeUnit.DAYS.toMillis(90) <= currentTimeInMillis) { // 3 Monate
            roles.add(Config.ROLE_3_MONTH);
        }

        if (joinTimeInMillis + TimeUnit.DAYS.toMillis(180) <= currentTimeInMillis) { // 6 Monate
            roles.add(Config.ROLE_6_MONTH);
        }

        if (joinTimeInMillis + TimeUnit.DAYS.toMillis(365) <= currentTimeInMillis) { // 1 Jahr
            roles.add(Config.ROLE_1_YEAR);
        }

        if (joinTimeInMillis + TimeUnit.DAYS.toMillis(730) <= currentTimeInMillis) { // 2 Jahre
            roles.add(Config.ROLE_2_YEAR);
        }

        if (joinTimeInMillis + TimeUnit.DAYS.toMillis(1460) <= currentTimeInMillis) { // 3 Jahre
            roles.add(Config.ROLE_3_YEAR);
        }

        return roles;
    }
}
