package entities;

import lombok.Data;

@Data
public class ServerInfo {

    private String serverName;

    private int maxPlayers;

    private int currentPlayers;

    private String map;
}
