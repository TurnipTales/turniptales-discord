package net.turniptales.discord.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.turniptales.discord.common.api.model.stats.Contract;
import net.turniptales.discord.common.api.model.stats.PunishmentData;
import net.turniptales.discord.common.api.model.stats.RoleplayData;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerStats {

    private UUID minecraftUuid;
    private String minecraftName;
    private String faction;
    private int level;
    private boolean online;
    private PunishmentData punishmentData;
    private RoleplayData roleplayData;
    private List<Integer> houses;
    private List<Contract> contracts;
    private String role;
    private int knownPlayerCount;
    private List<String> cars;
}
