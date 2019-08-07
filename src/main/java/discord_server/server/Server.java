package discord_server.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import config.*;
import embedMesssage.EmbedMessage;
import entities.Guilds;
import entities.MessageServer;
import entities.ServerInfo;
import entities.Servers;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import repository.GuildRepository;
import repository.MessageServerRepository;
import repository.ServerRepository;
import runnable.Task;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Server {

    private GuildMessageReceivedEvent event;
    private GuildRepository guildRepository = new GuildRepository();
    private String context;
    private int minutes = Integer.valueOf(Config.getProperty("time_expire"));

    public Server(GuildMessageReceivedEvent event) {
        this.event = event;
        context = event.getMessage().getContentRaw().replaceAll("\\s{2,}", " ").trim();
    }


    /*
    Show servers
     */
    public void showServers() {
        Guilds guild = guildRepository.getGuildByDiscordId(event.getGuild().getIdLong());

        try {
            event.getGuild().getTextChannelById(guild.getChannel_id());
        } catch (NullPointerException e) {
            event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor(
                    "Error:",
                    "You haven't assigned bot to text channel",
                    ResultColor.ERROR
            )).queue(
                    (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
            );
            return;
        }

        List<MessageServer> servers = guild.getMessageServers();

        new Thread(
                () -> {
                    StringBuilder text = new StringBuilder("Servers connected to the bot:\n\n");

                    if (servers.size() == 0) {
                        event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor(
                                "Error:",
                                "No server assigned to the bot\n" +
                                        "\n" +
                                        "To assign new server use command `ss/addserver`",
                                ResultColor.ERROR
                        )).queue(
                                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
                        );
                    } else {
                        for (MessageServer messageServer : servers) {
                            Servers server = messageServer.getServer();

                            text.append("ServerId: **").append(server.getBattlemetrics_id()).append("**\n")
                                    .append("Name: **").append(server.getServer_name()).append("**\n\n");
                        }
                        event.getChannel().sendMessage(new EmbedMessage().messageText(text.toString())).queue(
                                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
                        );
                    }
                }
        ).start();
    }


    /*
    Add new Server to Guild
     */
    public void addNewServer() {
        Guilds guild = guildRepository.getGuildByDiscordId(event.getGuild().getIdLong());

        if(guild.getChannel_id() == 0){
            //TODO: modify
            event.getChannel().sendMessage("no channel assigned").queue(
                    (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
            );
            return;
        }

        try {
            event.getGuild().getTextChannelById(guild.getChannel_id());
        } catch (NullPointerException e) {
            event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor(
                    "Error:",
                    "You haven't assigned bot to text channel ",
                    ResultColor.ERROR
            )).queue(
                    (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
            );
            return;
        }

        String[] arrayOfServers = event.getMessage().getContentRaw().replaceAll("\\s{2,}", " ").trim().split(" ");

        if (arrayOfServers.length > 1) {
            StringBuilder embedText = new StringBuilder();
            new Thread(
                    () -> {
                        for (int i = 1; i < arrayOfServers.length; i++) {
                            Servers serverDB;

                            // Если к боту уже привязан сервер
                            if ((serverDB = new ServerRepository().getServerByGuildAndServerId(event.getGuild().getIdLong(), arrayOfServers[i])) != null) {
                                embedText.append("**").append(serverDB.getServer_name()).append("**\n").append("\uD83D\uDCBE Server [").append(serverDB.getBattlemetrics_id()).append("](https://battlemetrics.com/servers/squad/").append(serverDB.getBattlemetrics_id()).append(") - this server is already assigned to the bot\n\n");
                                continue;
                            }

                            //Если сервер уже есть в списках
                            if ((serverDB = new ServerRepository().getServerByServerId(arrayOfServers[i])) != null) {
                                sendMessageToChannel(event, serverDB, guild.getChannel_id(), guild);
                                embedText.append("**").append(serverDB.getServer_name()).append("**\n").append("\u2705 Server [").append(serverDB.getBattlemetrics_id()).append("](https://battlemetrics.com/servers/squad/").append(serverDB.getBattlemetrics_id()).append(") - this server successfully assigned to the bot\n\n");
                                continue;
                            }


                            //Если сервера нет в списках = новый
                            JsonElement serverJSON;
                            if ((serverJSON = BattleMetrics.getServerInfo(arrayOfServers[i])) != null) {
                                serverDB = new Servers();

                                if (!serverJSON.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("relationships").getAsJsonObject("game").getAsJsonObject("data").get("id").getAsString().equals("squad")) {
                                    embedText.append("\u274C Server [").append(arrayOfServers[i]).append("](https://battlemetrics.com/servers/squad/").append(arrayOfServers[i]).append(") - this server isn't a squad server\n\n");
                                    continue;
                                }

                                //getting data from JSON
                                JsonObject attrJSON = serverJSON.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("attributes");
                                serverDB.setBattlemetrics_id(arrayOfServers[i]);
                                serverDB.setServer_name(attrJSON.get("name").getAsString());
                                serverDB.setServer_ip(attrJSON.get("ip").getAsString());
                                serverDB.setServer_port(attrJSON.get("portQuery").getAsInt());
                                //TODO: добавить id сообщения
                                sendMessageToChannel(event, serverDB, guild.getChannel_id(), guild);
                                embedText.append("**").append(serverDB.getServer_name()).append("**\n").append("\u2705 Server [").append(serverDB.getBattlemetrics_id()).append("](https://battlemetrics.com/servers/squad/").append(serverDB.getBattlemetrics_id()).append(") - this server successfully assigned to the bot \n\n");
                            } else {
                                embedText.append("\u274C Server [").append(arrayOfServers[i]).append("](https://en.wikipedia.org/wiki/HTTP_404) - this server cannot be added as it has incorrect input\n\n");
                            }
                        }

                        event.getChannel().sendMessage(
                                new EmbedMessage().messageTitleTextColor("Results of adding servers to the bot:", embedText.toString(), ResultColor.DEFAULT)
                        ).queue(
                                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
                        );
                    }
            ).start();
        } else {
            event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor(
                    "Error:",
                    "You forgot to mention at least one server.",
                    ResultColor.ERROR)).queue(
                    (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
            );
        }
    }


    public void editServer() {

        //checkers
        try {
            if (!(context.split(" ").length == 4 && context.split(" ")[2].equals("to"))) {
                throw new Exception();
            }


            int checker = Integer.valueOf(context.split(" ")[1]);
            checker = Integer.valueOf(context.split(" ")[3]);

            Servers serverDB;
            Guilds guild = guildRepository.getGuildByDiscordId(event.getGuild().getIdLong());

            //Если сервер уже есть в списках
            if ((serverDB = new ServerRepository().getServerByServerId(context.split(" ")[3])) != null) {

                MessageServer messageServer = new MessageServerRepository().getMessageByServerId(event.getGuild().getIdLong(), context.split(" ")[1]);
                messageServer.getServer().removeMessage(messageServer);
                messageServer.setServer(serverDB);
                messageServer.getServer().addMessage(messageServer);
                new GuildRepository().saveGuild(messageServer.getGuild());

                event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor(
                        "Success:",
                        "\u2705 Successful server change",
                        ResultColor.SUCCESS
                )).queue(
                        (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
                );
                return;
            }

            //Если сервера нет в списках = новый
            JsonElement serverJSON;
            StringBuilder embedText = new StringBuilder();
            if ((serverJSON = BattleMetrics.getServerInfo(context.split(" ")[3])) != null) {
                serverDB = new Servers();

                if (!serverJSON.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("relationships").getAsJsonObject("game").getAsJsonObject("data").get("id").getAsString().equals("squad")) {
                    embedText.append("\u274C Server [").append(context.split(" ")[3]).append("](https://battlemetrics.com/servers/squad/").append(context.split(" ")[3]).append(") - this server isn't a squad server\n\n");
                } else {

                    //getting data from JSON
                    JsonObject attrJSON = serverJSON.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("attributes");
                    MessageServer messageServer = new MessageServerRepository().getMessageByServerId(event.getGuild().getIdLong(), context.split(" ")[1]);
                    messageServer.getServer().removeMessage(messageServer);
                    serverDB.setBattlemetrics_id(context.split(" ")[3]);
                    serverDB.setServer_name(attrJSON.get("name").getAsString());
                    serverDB.setServer_ip(attrJSON.get("ip").getAsString());
                    serverDB.setServer_port(attrJSON.get("portQuery").getAsInt());
                    messageServer.setServer(serverDB);
                    messageServer.getServer().addMessage(messageServer);
                    new GuildRepository().saveGuild(messageServer.getGuild());

                    embedText.append("**").append(serverDB.getServer_name()).append("**\n").append("\u2705 Server [").append(serverDB.getBattlemetrics_id()).append("](https://battlemetrics.com/servers/squad/").append(serverDB.getBattlemetrics_id()).append(") - this server successfully assigned to the bot \n\n");
                }
            } else {
                embedText.append("\u274C Server [").append(context.split(" ")[3]).append("](https://en.wikipedia.org/wiki/HTTP_404) - this server cannot be added as it has incorrect input\n\n");
            }

            event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor(
                    "Result:",
                    embedText.toString(),
                    ResultColor.SUCCESS
            )).queue(
                    (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
            );
        } catch (Exception ignore) {
            event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor(
                    "Error:",
                    "Incorrectly written command",
                    ResultColor.ERROR)).queue(
                    (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
            );
        }
    }


    //TODO: доделать
    /////// /////// /////// ///////
    /////// /////// /////// ///////
    /////// /////// /////// ///////
    /////// /////// /////// ///////
    public void deleteServer() {
        MessageServer messageServer;
        StringBuilder embedText = new StringBuilder();

        //TODO: если context.length = 1.
        if(context.split(" ").length ==1){
            event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor(
                    "Error:",
                    "You forgot to mention servers",
                    ResultColor.ERROR
            )).queue();
            return;
        }

        for (int i = 1; i < context.split(" ").length; i++) {
             messageServer = new MessageServerRepository().getMessageByServerId(event.getGuild().getIdLong(), context.split(" ")[i]);
             if(messageServer != null){
                 event.getGuild().getTextChannelById(messageServer.getGuild().getChannel_id()).deleteMessageById(messageServer.getMessage_id()).queue();
                 messageServer.getServer().removeMessage(messageServer);
                 messageServer.getGuild().removeMessage(messageServer);
                 new MessageServerRepository().deleteMessage(messageServer);


                 embedText.append("\u2705 Server [").append(context.split(" ")[i]).append("](https://battlemetrics.com/servers/squad/").append(context.split(" ")[i]).append(") - successfully deleted\n\n");

             } else {
                 embedText.append("\u274C Server [").append(context.split(" ")[i]).append("](https://en.wikipedia.org/wiki/HTTP_404)").append(" - server cannot be deleted as it isn't assinged to the bot.\n\n");
             }
        }

        event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor(
                "Result:",
                embedText.toString(),
                ResultColor.SUCCESS
        )).queue(
                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
        );

    }


    private void sendMessageToChannel(GuildMessageReceivedEvent event, Servers serverDB, long channelId, Guilds guild) {
        event.getGuild().getTextChannelById(channelId).sendMessage(
                new EmbedMessage().messageEmptyServer()
        ).queue(
                (message) -> {
                    ServerInfo steamQuary;

                    if ((steamQuary = SteamQuery.getServerInfo(serverDB.getServer_ip(), serverDB.getServer_port())).getMap() != null) {

                        String players = steamQuary.getCurrentPlayers() + "/" + steamQuary.getMaxPlayers();

                        //Set color
                        double percent = ((double) steamQuary.getCurrentPlayers() / (double) steamQuary.getMaxPlayers()) * 100;
                        Color color = ColorChoose.getColor(percent);

                        message.editMessage(new EmbedMessage().messageServerInfo(
                                steamQuary.getServerName(),
                                "online",
                                serverDB.getBattlemetrics_id(),
                                players,
                                steamQuary.getMap(),
                                "steam://connect/" + serverDB.getServer_ip() + ":" + serverDB.getServer_port(),
                                color
                        )).queue();
                        MessageServer messageServer = new MessageServer();
                        messageServer.setMessage_id(message.getIdLong());
                        messageServer.setServer(serverDB);
                        guild.addMessage(messageServer);
                        new GuildRepository().saveGuild(guild);
                    } else {
                        message.editMessage(new EmbedMessage().messageServerInfo(
                                "Server is unavailable",
                                "offline",
                                serverDB.getBattlemetrics_id(),
                                "none",
                                "none",
                                "https://joinsquad.com/wp-content/themes/squad/img/logo.png",
                                "no link",
                                ResultColor.EMPTYPLAYERS
                        )).queue();
                        MessageServer messageServer = new MessageServer();
                        messageServer.setMessage_id(message.getIdLong());
                        messageServer.setServer(serverDB);
                        guild.addMessage(messageServer);
                        new GuildRepository().saveGuild(guild);
                    }
                }
        );
    }
}


