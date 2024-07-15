package net.turniptales.discord.common.services;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.turniptales.discord.common.configuration.DiscordBotProperties;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

@Log4j2
@Component
public class RoleSyncService {

    private final DiscordBotProperties discordBotProperties;
    private List<Role> stayRoles;

    public RoleSyncService(DiscordBotProperties discordBotProperties) {
        long sixHoursInMillis = HOURS.toMillis(6);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                RoleSyncService.this.stayRoles = List.of(
                        discordBotProperties.getRole0(),
                        discordBotProperties.getRole1Week(),
                        discordBotProperties.getRole2Week(),
                        discordBotProperties.getRole1Month(),
                        discordBotProperties.getRole3Month(),
                        discordBotProperties.getRole6Month(),
                        discordBotProperties.getRole1Year(),
                        discordBotProperties.getRole2Year(),
                        discordBotProperties.getRole3Year()
                );

                long startTime = currentTimeMillis();
                log.info("Discord role synchronising: started");

                Guild guild = discordBotProperties.getGuild();
                guild.loadMembers().get().forEach(member -> {
                    Role highestRoleUserShouldHave = getHighestRoleUserShouldHave(member.getTimeJoined());
                    RoleSyncService.this.stayRoles.stream()
                            .filter(role -> !role.equals(highestRoleUserShouldHave))
                            .filter(role -> member.getRoles().contains(role))
                            .forEach(role -> {
                                guild.removeRoleFromMember(member, role).queue();
                                log.info("Discord role synchronising: Remove role {} from member {}", role.getName(), member.getEffectiveName());
                            });

                    Role playerRole = discordBotProperties.getPlayerRole();
                    if (!member.getRoles().contains(playerRole) || !member.getRoles().contains(highestRoleUserShouldHave)) {
                        guild.addRoleToMember(member, playerRole).queue();
                        guild.addRoleToMember(member, highestRoleUserShouldHave).queue();
                        log.info("Discord role synchronising: Add role {} to member {}", highestRoleUserShouldHave.getName(), member.getEffectiveName());
                    }
                });

                log.info("Discord role synchronising: finished in {}ms", currentTimeMillis() - startTime);
            }
        }, (sixHoursInMillis - currentTimeMillis() % sixHoursInMillis) + HOURS.toMillis(2), sixHoursInMillis); // 4 10 16 22
        log.info("Discord role synchronising: scheduled");
        this.discordBotProperties = discordBotProperties;
    }

    private Role getHighestRoleUserShouldHave(OffsetDateTime timeJoined) {
        Role role = discordBotProperties.getRole0();

        long joinTimeInMillis = timeJoined.toInstant().toEpochMilli();
        long currentTimeInMillis = currentTimeMillis();

        if (joinTimeInMillis + DAYS.toMillis(7) <= currentTimeInMillis) { // 1 Woche
            role = discordBotProperties.getRole1Week();
        }

        if (joinTimeInMillis + DAYS.toMillis(14) <= currentTimeInMillis) { // 2 Wochen
            role = discordBotProperties.getRole2Week();
        }

        if (joinTimeInMillis + DAYS.toMillis(30) <= currentTimeInMillis) { // 1 Monat
            role = discordBotProperties.getRole1Month();
        }

        if (joinTimeInMillis + DAYS.toMillis(90) <= currentTimeInMillis) { // 3 Monate
            role = discordBotProperties.getRole3Month();
        }

        if (joinTimeInMillis + DAYS.toMillis(180) <= currentTimeInMillis) { // 6 Monate
            role = discordBotProperties.getRole6Month();
        }

        if (joinTimeInMillis + DAYS.toMillis(365) <= currentTimeInMillis) { // 1 Jahr
            role = discordBotProperties.getRole1Year();
        }

        if (joinTimeInMillis + DAYS.toMillis(730) <= currentTimeInMillis) { // 2 Jahre
            role = discordBotProperties.getRole2Year();
        }

        if (joinTimeInMillis + DAYS.toMillis(1460) <= currentTimeInMillis) { // 3 Jahre
            role = discordBotProperties.getRole3Year();
        }

        return role;
    }
}
