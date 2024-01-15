package net.turniptales.discord.common.services;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static net.turniptales.discord.Config.GUILD;
import static net.turniptales.discord.Config.ROLE_0;
import static net.turniptales.discord.Config.ROLE_1_MONTH;
import static net.turniptales.discord.Config.ROLE_1_WEEK;
import static net.turniptales.discord.Config.ROLE_1_YEAR;
import static net.turniptales.discord.Config.ROLE_2_WEEK;
import static net.turniptales.discord.Config.ROLE_2_YEAR;
import static net.turniptales.discord.Config.ROLE_3_MONTH;
import static net.turniptales.discord.Config.ROLE_3_YEAR;
import static net.turniptales.discord.Config.ROLE_6_MONTH;

@Log4j2
@Component
public class RoleSyncService {

    private List<Role> stayRoles;

    public RoleSyncService() {
        long sixHoursInMillis = HOURS.toMillis(6);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                assert ROLE_0 != null;
                assert ROLE_1_WEEK != null;
                assert ROLE_2_WEEK != null;
                assert ROLE_1_MONTH != null;
                assert ROLE_3_MONTH != null;
                assert ROLE_6_MONTH != null;
                assert ROLE_1_YEAR != null;
                assert ROLE_2_YEAR != null;
                assert ROLE_3_YEAR != null;
                RoleSyncService.this.stayRoles = List.of(ROLE_0, ROLE_1_WEEK, ROLE_2_WEEK, ROLE_1_MONTH, ROLE_3_MONTH, ROLE_6_MONTH, ROLE_1_YEAR, ROLE_2_YEAR, ROLE_3_YEAR);

                long startTime = currentTimeMillis();
                log.info("Discord role synchronising: started");

                Guild guild = GUILD;

                assert guild != null;
                guild.loadMembers().get().forEach(member -> {
                    Role highestRoleUserShouldHave = getHighestRoleUserShouldHave(member.getTimeJoined());
                    RoleSyncService.this.stayRoles.stream()
                            .filter(role -> !role.equals(highestRoleUserShouldHave))
                            .filter(role -> member.getRoles().contains(role))
                            .forEach(role -> {
                                guild.removeRoleFromMember(member, role).queue();
                                log.info("Discord role synchronising: Remove role {} from member {}", role.getName(), member.getEffectiveName());
                            });

                    if (!member.getRoles().contains(highestRoleUserShouldHave)) {
                        guild.addRoleToMember(member, highestRoleUserShouldHave).queue();
                        log.info("Discord role synchronising: Add role {} to member {}", highestRoleUserShouldHave.getName(), member.getEffectiveName());
                    }
                });

                log.info("Discord role synchronising: finished in {}ms", currentTimeMillis() - startTime);
            }
        }, (sixHoursInMillis - currentTimeMillis() % sixHoursInMillis) + HOURS.toMillis(2), sixHoursInMillis); // 4 10 16 22
        log.info("Discord role synchronising: scheduled");
    }

    private Role getHighestRoleUserShouldHave(OffsetDateTime timeJoined) {
        Role role = ROLE_0;

        long joinTimeInMillis = timeJoined.toInstant().toEpochMilli();
        long currentTimeInMillis = currentTimeMillis();

        if (joinTimeInMillis + DAYS.toMillis(7) <= currentTimeInMillis) { // 1 Woche
            role = ROLE_1_WEEK;
        }

        if (joinTimeInMillis + DAYS.toMillis(14) <= currentTimeInMillis) { // 2 Wochen
            role = ROLE_2_WEEK;
        }

        if (joinTimeInMillis + DAYS.toMillis(30) <= currentTimeInMillis) { // 1 Monat
            role = ROLE_1_MONTH;
        }

        if (joinTimeInMillis + DAYS.toMillis(90) <= currentTimeInMillis) { // 3 Monate
            role = ROLE_3_MONTH;
        }

        if (joinTimeInMillis + DAYS.toMillis(180) <= currentTimeInMillis) { // 6 Monate
            role = ROLE_6_MONTH;
        }

        if (joinTimeInMillis + DAYS.toMillis(365) <= currentTimeInMillis) { // 1 Jahr
            role = ROLE_1_YEAR;
        }

        if (joinTimeInMillis + DAYS.toMillis(730) <= currentTimeInMillis) { // 2 Jahre
            role = ROLE_2_YEAR;
        }

        if (joinTimeInMillis + DAYS.toMillis(1460) <= currentTimeInMillis) { // 3 Jahre
            role = ROLE_3_YEAR;
        }

        return role;
    }
}
