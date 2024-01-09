package net.turniptales.discord.api.schema;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Verification {

    private UUID minecraftUuid;
    private String minecraftName;
}
