package net.turniptales.discord.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerStats {

    private UUID minecraftUuid;
    private String minecraftName;
    private int level;
    private boolean online;
}
