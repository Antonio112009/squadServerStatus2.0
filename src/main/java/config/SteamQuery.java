package config;


import entities.ServerInfo;
import steamcondenser.steam.SteamPlayer;
import steamcondenser.steam.servers.GoldSrcServer;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class SteamQuery {

    public static ServerInfo getServerInfo(String ipaddress, int port){
        ServerInfo serverInfo = null;

        try {
            serverInfo = new ServerInfo();
            InetAddress serverIp = InetAddress.getByName(ipaddress);
            GoldSrcServer server = new GoldSrcServer(serverIp, port);
            server.initialize();
            int playerNumber = 0;
            for(Map.Entry<String, SteamPlayer> entry : server.getPlayers().entrySet()) {
                SteamPlayer value = entry.getValue();
                if(value.getName().equals(""))
                    continue;
                playerNumber++;
            }
            server.initialize();
            HashMap<String, Object> servInfo = server.getServerInfo();
            serverInfo.setCurrentPlayers(playerNumber);
            serverInfo.setServerName(servInfo.get("serverName").toString());
            serverInfo.setMaxPlayers(Integer.valueOf(servInfo.get("maxPlayers").toString()));
            serverInfo.setMap(servInfo.get("mapName").toString());
            return serverInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return serverInfo;
        }
    }
}
