package net.turniptales.discord.common.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ConnectionDataValue {

    // data
    private String citizenId;
    private UUID minecraftUuid;
    private String minecraftName;
    private String role;
    private String roleDisplayName;
    private List<String> teams;
    private int level;
    private int knownPlayerCount;
    // group
    private String group;
    private int factionRank;
    private boolean leaderRights;
    // characteristics
    private String roleplayName;
    private int age;
    private String gender;
    // balances
    private double bankBalance;
    private double cashBalance;
    private double palPayBalance;
    // other
    private PunishmentData punishmentData;
    private List<Long> houses;
    private List<String> cars;
}
