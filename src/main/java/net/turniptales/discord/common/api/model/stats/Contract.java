package net.turniptales.discord.common.api.model.stats;

import lombok.Data;

import java.util.UUID;

@Data
public class Contract {

    private long id;
    private long startDate;
    private long endDate;
    private UUID owner;
    private UUID partner;
    private long payDayBalance; // is paid every pay day by partner
    private long oneTimeBalance; // is paid at endDate by partner

    public boolean isPayedEveryPayDay(UUID minecraftUuid) {
        return this.partner.equals(minecraftUuid) && this.payDayBalance > 0;
    }
}
