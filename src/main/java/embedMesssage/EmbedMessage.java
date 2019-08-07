package embedMesssage;

import config.ResultColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

import java.awt.*;
import java.time.Instant;

public class EmbedMessage {
    private EmbedBuilder embed = new EmbedBuilder();
    private Color defaultColor = new Color(249, 29, 84);

    //Send discord_server.discord info
    public EmbedBuilder onJoinDiscordServer(GuildJoinEvent event){
        embed.setColor(new Color(63, 255, 0));
        embed.setThumbnail(event.getGuild().getIconUrl());
        embed.setTitle("Бот добавлен на новый Discord сервер:");
        embed.addField("Имя сервера:",
                event.getGuild().getName(),false);
        embed.addField("Кол-во юзеров:",
                String.valueOf(event.getGuild().getMembers().size()), false);
        return embed;
    }

    //Send Greeting message
    public MessageEmbed GreetingMessage(GuildJoinEvent event){
        embed.setColor(defaultColor);
        embed.setAuthor(event.getJDA().getSelfUser().getName(), null,event.getJDA().getSelfUser().getAvatarUrl());
        embed.setTitle("Thank you for adding me!");
        embed.setDescription("To get started you can view the bot guide via `?guide`");
        embed.addField("Bot guide", "`ss/guide`", true);
        embed.addField("List of commands", "`ss/help`", true);
        embed.addField("See info about me and credits:", "`ss/aboutSS`\n`ss/credit`",true);
        return embed.build();
    }


    //Send Text, Title, Color:
    public MessageEmbed messageTitleTextColor(String title, String text, Color color){
        embed.setColor(color);
        embed.setTitle(title);
        embed.setDescription(text);
        return embed.build();
    }

    //Send Text, Color default
    public MessageEmbed messageText(String text) {
        embed.setColor(ResultColor.DEFAULT);
        embed.setDescription(text);
        return embed.build();
    }

    //Send Text, Color
    public MessageEmbed messageTextColor(String text,Color color) {
        embed.setColor(color);
        embed.setDescription(text);
        return embed.build();
    }

    //Send Text, Color
    public MessageEmbed messageTitlePictureTextColor(String title ,String URL, String text,Color color) {
        embed.setTitle(title);
        embed.setThumbnail(URL);
        embed.setColor(color);
        embed.setDescription(text);
        return embed.build();
    }


    //Send Server Info
    public MessageEmbed messageServerInfo(String name, String status, String server_id, String players, String map, String joinServer, Color color){
        return messageServerInfo(name, status, server_id, players, map, "http://squadmaps.com/full/" + map.replaceAll(" ","_").replaceAll("'","") + ".jpg", joinServer, color);
    }


    public MessageEmbed messageServerInfo(String name, String status, String server_id, String players, String map, String picture, String joinServer, Color color){
        embed.setColor(color);
        embed.setThumbnail(picture);
        embed.setAuthor(name);
        embed.setDescription("Server status: **" + status + "**\n" +
                "Server id: **" + server_id + "**");
        embed.addField("Players", players, true);
        embed.addField("Map and mod",map, true);
        embed.setTimestamp(Instant.now());
        embed.addField("Join server: ",joinServer, false);
        return embed.build();
    }


    //Send Empty Server
    public MessageEmbed messageEmptyServer(){
        embed.setColor(new Color(255,0,0));
        embed.setTitle("Server is unavailable");
        embed.setDescription("Status: doesn't exist");
        embed.setThumbnail("https://joinsquad.com/wp-content/themes/squad/img/logo.png");
        embed.setTimestamp(Instant.now());
        return embed.build();
    }


    //Send offline Server
    public MessageEmbed messageOfflineServer(String serverName, String BMid){
        embed.setColor(new Color(0,0,0));
        embed.setTitle(serverName);
        embed.setDescription("Server removed or dead\n" +
                "Server id: **" + BMid + "**");
        embed.setThumbnail("https://joinsquad.com/wp-content/themes/squad/img/logo.png");
        embed.setTimestamp(Instant.now());
        return embed.build();
    }
}
