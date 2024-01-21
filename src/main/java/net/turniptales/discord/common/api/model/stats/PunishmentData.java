package net.turniptales.discord.common.api.model.stats;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PunishmentData {

    private long id;
    private long weaponBlockUntil;
    private long factionBlockUntil;
    private long skillBlockUntil;
    private long adBlockUntil;
    private long carBlockUntil;
    private int checkpointsLeft;
    private List<String> log;

    public boolean isWeaponBlockActive() {
        return new Date(this.weaponBlockUntil).after(new Date());
    }

    public boolean isFactionBlockActive() {
        return new Date(this.factionBlockUntil).after(new Date());
    }

    public boolean isSkillBlockActive() {
        return new Date(this.skillBlockUntil).after(new Date());
    }

    public boolean isAdBlockActive() {
        return new Date(this.adBlockUntil).after(new Date());
    }

    public boolean isCarBlockActive() {
        return new Date(this.carBlockUntil).after(new Date());
    }
}
