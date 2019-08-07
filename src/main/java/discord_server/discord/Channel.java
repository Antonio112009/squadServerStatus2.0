package discord_server.discord;

import config.ResultColor;
import embedMesssage.EmbedMessage;
import entities.Guilds;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import repository.GuildRepository;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Channel {

    private GuildMessageReceivedEvent event;
    private EmbedMessage embed = new EmbedMessage();
    private GuildRepository guildRepository = new GuildRepository();

    public Channel(GuildMessageReceivedEvent event) {
        this.event = event;
    }


    /*
    Display chanel that was assigned.
     */
    public void showChannel() {

        /*
        Get discord
         */
        Guilds guild = guildRepository.getGuildByDiscordId(event.getGuild().getIdLong());

        String text;
        Color final_color;
        TextChannel channel;

        if((channel = event.getGuild().getTextChannelById(guild.getChannel_id())) != null){
            text = "Bot is assigned to channel **" + channel.getName() + "**\n" +
                    "\n" +
                    "To change assigned channel to another one use command `ss/editchannel`";
            final_color = ResultColor.SUCCESS;
        } else {
            text = "Bot is not assigned to any channel!\n" +
                    "\n" +
                    "To assign bot to channel use command `ss/addchannel`";
            final_color = ResultColor.ERROR;
        }

        event.getChannel().sendMessage(embed.messageTitleTextColor("Result:", text, final_color)).queue(
                (m) -> m.delete().queueAfter(30, TimeUnit.SECONDS)
        );
    }



    /*
    Adding new Channel
     */
    public void addChannel(){
        Guilds guilds = guildRepository.getGuildByDiscordId(event.getGuild().getIdLong());

        /*
        Channel checker
         */
        if(guilds.getChannel_id() != 0){
            TextChannel channel;
            if((channel = event.getGuild().getTextChannelById(guilds.getChannel_id())) != null){
                event.getChannel().sendMessage(
                        embed.messageTitleTextColor(
                                "Error:",
                                "You have already assigned bot to channel **" + channel.getName() + "** (" + channel.getAsMention() + ")\n" +
                                        "\n" +
                                        "Assign bot to new channel using `ss/editchannel`",
                        ResultColor.ERROR
                        )
                ).queue(
                        (m) -> m.delete().queueAfter(30, TimeUnit.SECONDS)
                );
                return;
            }
            else {
                event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor("Error:", "You deleted channel that was assigned to the bot.\n" +
                        "\n" +
                        "Assign bot to new channel using `ss/editchannel`", ResultColor.ERROR)).queue(
                        (m) -> m.delete().queueAfter(30, TimeUnit.SECONDS)
                );
                return;
            }
        }


        /*
        If bot passed checkers.
         */
        addingChannel(guilds);
    }

    private void addingChannel(Guilds guilds) {
        if(event.getMessage().getMentionedChannels().size() == 1){
            TextChannel channel = event.getMessage().getMentionedChannels().get(0);
            guilds.setChannel_id(channel.getIdLong());
            guildRepository.saveGuild(guilds);
            event.getChannel().sendMessage(
                    embed.messageTitleTextColor(
                            "Result:",
                            "You successfully assigned bot to channel: **" + channel.getName() + "** (" + channel.getAsMention() + ")",
                            ResultColor.SUCCESS
                    )
            ).queue(
                    (m) -> m.delete().queueAfter(30, TimeUnit.SECONDS)
            );
        } else {
            event.getChannel().sendMessage(embed.messageTitleTextColor("ERROR:", "No channel mentioned or mentioned more than one.", ResultColor.ERROR)).queue(
                    (m) -> m.delete().queueAfter(30, TimeUnit.SECONDS)
            );
        }
    }


    /*
    Editing channel
     */
    public void editChannel(){
        Guilds guilds = guildRepository.getGuildByDiscordId(event.getGuild().getIdLong());

        addingChannel(guilds);
    }


}
