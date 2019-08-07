package runnable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import config.*;
import embedMesssage.EmbedMessage;
import entities.MessageServer;
import entities.ServerInfo;
import entities.Servers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import repository.ServerRepository;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Task {

    private JDA api;

    public Task(JDA api) {
        this.api = api;
    }

    void UpdateMessages() {
        List<Servers> serversList = new ServerRepository().getAllServers();

        String[] serversException;
        try{
            serversException = Config.getProperty("servers_exception").split(",");
        } catch (NullPointerException e){
            serversException = null;
        }


        for (Servers server : serversList) {
            ArrayList<String> finalServersException = new ArrayList< >(Arrays.asList(serversException != null ? serversException : new String[0]));
            new Thread(
                    () -> {
                        if (server.getMessageServers().size() != 0) {

                            //Get info about server
                            ServerInfo steamQuary;

                            //exceptions
                            if(finalServersException.contains(server.getBattlemetrics_id())){
                                JsonElement serverJSON = BattleMetrics.getServerInfo(server.getBattlemetrics_id());
                                JsonObject attrJSON = serverJSON.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("attributes");

                                String players = attrJSON.get("players").getAsString() + "/" + attrJSON.get("maxPlayers").getAsString();

                                double percent = (Double.valueOf(attrJSON.get("players").getAsString()) / Double.valueOf(attrJSON.get("maxPlayers").getAsString())) * 100;

                                Color color = ColorChoose.getColor(percent);

                                for (MessageServer messageServer : server.getMessageServers()) {
                                    try {

                                        api.getGuildById(messageServer.getGuild().getDiscord_id()).getTextChannelById(messageServer.getGuild().getChannel_id()).editMessageById(messageServer.getMessage_id(), new EmbedMessage()
                                                .messageServerInfo(
                                                        attrJSON.get("name").getAsString(),
                                                        "online",
                                                        server.getBattlemetrics_id(),
                                                        players,
                                                        attrJSON.getAsJsonObject("details").get("map").getAsString(),
                                                        "steam://connect/" + server.getServer_ip() + ":" + server.getServer_port(),
                                                        color
                                                )).queue();
                                    } catch (Exception ignored){

                                    }
                                }
                            } else if ((steamQuary = SteamQuery.getServerInfo(server.getServer_ip(), server.getServer_port())).getMap() != null) {
                                String players = steamQuary.getCurrentPlayers() + "/" + steamQuary.getMaxPlayers();

                                //Set color
                                double percent = ((double) steamQuary.getCurrentPlayers() / (double) steamQuary.getMaxPlayers()) * 100;
                                Color color = ColorChoose.getColor(percent);

                                for (MessageServer messageServer : server.getMessageServers()) {
                                    try {
                                        api.getGuildById(messageServer.getGuild().getDiscord_id()).getTextChannelById(messageServer.getGuild().getChannel_id()).editMessageById(messageServer.getMessage_id(), new EmbedMessage().messageServerInfo(
                                                steamQuary.getServerName(),
                                                "online",
                                                server.getBattlemetrics_id(),
                                                players,
                                                steamQuary.getMap(),
                                                "steam://connect/" + server.getServer_ip() + ":" + server.getServer_port(),
                                                color
                                        )).queue();
                                    } catch (Exception ignored){
                                    }
                                }
                            } else {

                                try {
                                    for (MessageServer messageServer : server.getMessageServers()) {
                                        api.getGuildById(messageServer.getGuild().getDiscord_id())
                                                .getTextChannelById(messageServer.getGuild().getChannel_id())
                                                .editMessageById(messageServer.getMessage_id(), new EmbedMessage().messageOfflineServer(
                                                        messageServer.getServer().getServer_name(),
                                                        messageServer.getServer().getBattlemetrics_id()
                                                        )
                                                ).queue();
                                    }
                                } catch (Exception ignored){
                                }
                            }
                        }
                    }
            ).start();
        }
    }

    void StatusUpdate() {
        if (api.getPresence().getActivity().getName().toLowerCase().startsWith("help")) {
            api.getPresence().setActivity(Activity.watching("Bot info - ss/about"));
            return;
        }

        if (api.getPresence().getActivity().getName().toLowerCase().startsWith("bot")) {
            api.getPresence().setActivity(Activity.watching("Invite bot - ss/link"));
            return;
        }

        if (api.getPresence().getActivity().getName().toLowerCase().startsWith("bot")) {
            api.getPresence().setActivity(Activity.watching("help - ss/help"));
        }
    }
}
