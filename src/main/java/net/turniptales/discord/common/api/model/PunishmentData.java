package net.turniptales.discord.common.api.model;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Objects.nonNull;
import static net.turniptales.discord.TurnipTalesDiscord.ZONE_ID;

@Data
@Builder
public class PunishmentData {

    @Nullable
    private ZonedDateTime weaponBlockUntilDateTime;
    @Nullable
    private ZonedDateTime groupBlockUntilDateTime;
    @Nullable
    private ZonedDateTime skillBlockUntilDateTime;
    @Nullable
    private ZonedDateTime adBlockUntilDateTime;
    @Nullable
    private ZonedDateTime carBlockUntilDateTime;
    private int checkpointsLeft;
    private List<String> log;

    public boolean noWeaponBlockActive() {
        return !nonNull(this.weaponBlockUntilDateTime) || this.weaponBlockUntilDateTime.isBefore(now(ZONE_ID));
    }

    public boolean noGroupBlockActive() {
        return !nonNull(this.groupBlockUntilDateTime) || this.groupBlockUntilDateTime.isBefore(now(ZONE_ID));
    }

    public boolean noSkillBlockActive() {
        return !nonNull(this.skillBlockUntilDateTime) || this.skillBlockUntilDateTime.isBefore(now(ZONE_ID));
    }

    public boolean noAdBlockActive() {
        return !nonNull(this.adBlockUntilDateTime) || this.adBlockUntilDateTime.isBefore(now(ZONE_ID));
    }

    public boolean noCarBlockActive() {
        return !nonNull(this.carBlockUntilDateTime) || this.carBlockUntilDateTime.isBefore(now(ZONE_ID));
    }
}
