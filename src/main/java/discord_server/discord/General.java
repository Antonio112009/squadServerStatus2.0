package discord_server.discord;

import config.ColorChoose;
import config.Config;
import config.ResultColor;
import config.SteamQuery;
import embedMesssage.EmbedMessage;
import entities.Guilds;
import entities.MessageServer;
import entities.ServerInfo;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import repository.GuildRepository;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class General {

    private GuildMessageReceivedEvent event;
    private int minutes = Integer.valueOf(Config.getProperty("time_expire"));

    public General(GuildMessageReceivedEvent event) {
        this.event = event;
    }

    public void cleanChannel(){
        Guilds guild = new GuildRepository().getGuildByDiscordId(event.getGuild().getIdLong());
        TextChannel channel;

        if((channel = event.getGuild().getTextChannelById(guild.getChannel_id())) != null){
            channel.getIterableHistory().takeAsync(500).thenAccept(channel::purgeMessages);

            for (MessageServer messageServer : guild.getMessageServers()) {
                channel.sendMessage(new EmbedMessage().messageEmptyServer()).queue(
                        (mess) ->{
                            guild.removeMessage(messageServer);
                            messageServer.setMessage_id(mess.getIdLong());
                            guild.addMessage(messageServer);
                            new GuildRepository().saveGuild(guild);

                            ServerInfo steamQuary;

                            if ((steamQuary = SteamQuery.getServerInfo(messageServer.getServer().getServer_ip(), messageServer.getServer().getServer_port())).getMap() != null) {

                                //TODO: Изменить дату
                                String players = steamQuary.getCurrentPlayers() + "/" + steamQuary.getMaxPlayers();

                                //Set color
                                double percent = ((double) steamQuary.getCurrentPlayers() / (double) steamQuary.getMaxPlayers()) * 100;
                                Color color = ColorChoose.getColor(percent);

                                mess.editMessage(new EmbedMessage().messageServerInfo(
                                        steamQuary.getServerName(),
                                        "online",
                                        messageServer.getServer().getBattlemetrics_id(),
                                        players,
                                        steamQuary.getMap(),
                                        "steam://connect/" + messageServer.getServer().getServer_ip() + ":" + messageServer.getServer().getServer_port(),
                                        color
                                )).queue();
                            } else {
                                mess.editMessage(new EmbedMessage().messageServerInfo(
                                        "Server is unavailable",
                                        "offline",
                                        messageServer.getServer().getBattlemetrics_id(),
                                        "none",
                                        "none",
                                        "https://joinsquad.com/wp-content/themes/squad/img/logo.png",
                                        "no link",
                                        ResultColor.EMPTYPLAYERS
                                )).queue();
                            }
                        }
                );
            }

            event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor("Success:", "Successfully refreshed data", ResultColor.SUCCESS)).queue();

        } else {
            event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor(
                    "Error:",
                    "No channel assigned to bot or channel doesn't exist\n\n" +
                            "To assign bot to channel use command `ss/addchannel`\n",
                    ResultColor.ERROR)
            ).queue(
                    (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
            );
        }
    }



    public void showGuide(){
        String text = "" +
                "\n" +
                "**If you need help - use `ss/helpSS` command**\n" +
                "\n" +
                ":star: Only following users could manage bot:\n" +
                ":white_small_square:  Users with `MANAGE SERVER` permission\n" +
                ":white_small_square: Users with roles that were assigned to the bot\n" +
                "\n" +
                ":star2: **How to start using bot?**\n\n" +
                ":one: - assign bot to channel using `ss/addchannel` command\n\n" +
                ":two: - assign server(s) to the bot using `ss/addserver` command\n\n" +
                ":three: - if assigned channel looks \"ugly\" - use `ss/clean` command to delete all messages and \"rewrite\" server(s) status\n\n" +
                "\n" +
                ":star2: **Where to find server ID(s)?**\n" +
                "Currently, bot is taking data from [battlemetrics](https://www.battlemetrics.com/servers/squad)\n\n" +
                ":one: Find in search server(s) that you want to add to your server\n\n" +
                ":two: *URL* of the found server would be like: [https://www.battlemetrics.com/servers/squad/3672740](https://www.battlemetrics.com/servers/squad/3672740)\n\n" +
                ":three: **Get only numbers from the end of the URL.**\nIn our example it would be [3672740](https://www.battlemetrics.com/servers/squad/3672740)\n\n" +
                ":four: Use `ss/addserver` command to add that server.\nExample: `ss/addserver 3672740`" +
                "";
        event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor("Complete Guide", text, ResultColor.DEFAULT)).queue(
                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
        );
    }

    public void showHelp() {
        String text = "" +
                ":fire: **All commands and ouputs self-destroy after " + Config.getProperty("time_expire") + " minutes** :fire:\n" +
                "\n" +
                ":star2: **General:**\n" +
                "`ss/help` - see list of commands\n" +
                "`ss/guide` - see guide of bot installation\n" +
                "`ss/about` - see info about bot\n" +
                "`ss/credits` - see contributed to the project (in dev)\n" +
                "\n" +
                "**Only people who have `MANAGE SERVER` permission and/or added to the bot roles could use bot commands!**\n" +
                "\n" +
                ":star2: **Channel manipulations:**\n" +
                "`ss/channel` - show assigned channel to the bot\n" +
                "`ss/addchannel #CHANNEL` - assign channel to the bot to post servers' status\n" +
                "`ss/editchannel #CHANNEL` - override already assigned channel to the bot\n" +
                "\n" +
                ":star2: **Servers manipulations:**\n" +
                "`ss/servers` - list all servers assigned to the bot\n" +
                "`ss/addserver server1 server2 .. serverN` - assign server/servers to the bot\n" +
                "`ss/editserver server1 to server2` - change old server1 to new server2\n" +
                "`ss/deleteserver server1 server2 .. serverN` - delete server/servers from the bot\n" +
                "`ss/clean` - To refresh data in assigned to bot channel\n" +
                "\n" +
                "Example: `?addserver 3272036` or `?addserver 3272036 2125740`\n" +
                "\n" +
                ":star2: **Role manipulations:**\n" +
                "`ss/access` - show list of roles who have access permission to the bot\n" +
                "`ss/addrole @ROLE1 @ROLE2 .. @ROLE` - add role/roles that can manage bot\n" +
                "`ss/deleterole @ROLE1 @ROLE2 .. @ROLE` - delete role/roles that can manage bot\n" +
                "\n" +
                "Example: `ss/addrole @admin` or `?addrole @admin @moderator`\n" +
                "\n" +
                "For additional help - contact **" + Objects.requireNonNull(event.getJDA().getUserById(Objects.requireNonNull(Config.getProperty("special_id")))).getAsTag() + "**\n";

        event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor("Help", text, ResultColor.DEFAULT)).queue(
                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
        );
    }


    public void showCredits(){

        StringBuilder text = new StringBuilder();

        String[] players = Config.getProperty("credit_players").split(",");

        text.append("I would like to say thank you to everyone who helped me or gave me advises:\n\n");

        for(String player : players){
            try {
                text.append("**").append(player).append("**\n");
            } catch (NullPointerException ignore){}
        }

        event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor("Credits:", text.toString(), ResultColor.DEFAULT)).queue(
                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
        );
    }


    public void showAbout(){
        String text = "" +
                "Hi! I am " + event.getJDA().getSelfUser().getName() + ", a bot build by [[LANCE]Tony Anglichanin](https://github.com/Antonio112009)!\n" +
                "I'm written in Java, using [JDA library](https://github.com/DV8FromTheWorld/JDA) (4.BETA.0_4) \n" +
                "\n" +
                "To add me to your server - [press this link](https://discordapp.com/oauth2/authorize?client_id=562952086438936586&scope=bot&permissions=8)\n" +
                "\n" +
                "For additional help - contact **" + event.getJDA().getUserById(Config.getProperty("special_id")).getAsTag() + "**\n" +
                "\n" +
                "number of servers: `" + event.getJDA().getGuilds().size() + "`\n";

        for (int i = 0; i < event.getJDA().getGuilds().size(); i++) {
            if(i == 13) break;

            text += (i+1) + ". **" + event.getJDA().getGuilds().get(i).getName() + "**\n";
        }

        event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor(
                "About:",
                text,
                ResultColor.DEFAULT
        )).queue();
    }

    public void showInviteLink() {
        event.getChannel().sendMessage(new EmbedMessage().messageTitlePictureTextColor(
                "Link to add bot to your server:",
                "https://cdnb.artstation.com/p/assets/images/images/000/591/901/large/violet-nightingale-medved2.jpg",
                "[Press this link to invite bot to your server](https://discordapp.com/oauth2/authorize?client_id=562952086438936586&scope=bot&permissions=8)\n\n" +
                        "Thank you for your interest in this bot.\n" +
                        "Tony Anglichanin#0007\n" +
                        ":heart: :heart: :heart: :heart:\n",
                ResultColor.DEFAULT)).queue();
    }
}
