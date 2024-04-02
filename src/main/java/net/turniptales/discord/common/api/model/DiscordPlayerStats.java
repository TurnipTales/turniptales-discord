package net.turniptales.discord.common.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DiscordPlayerStats {

    // data
    private UUID minecraftUuid;
    private String minecraftName;
    private String role;
    private int level;
    private String group;
    private int knownPlayerCount;
    // characteristics
    private String roleplayName;
    private int age;
    private String gender;
    // balances
    private double bankBalance;
    private double cashBalance;
    private double palPayBalance;
    private PunishmentData punishmentData;
    private List<Integer> houses;
    private List<String> cars;
}
