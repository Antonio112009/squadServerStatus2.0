package listeners;

import config.Config;
import discord_server.Specail;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
public class Private extends ListenerAdapter {

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String contexts = event.getMessage().getContentRaw().replaceAll("\\s{2,}", " ").trim();

        String command = contexts.split(" ")[0].toLowerCase();

        if (event.getAuthor().getId().equals(Config.getProperty("special_id"))) {
            if(command.equals("ss/exit")){
                System.exit(1);

            }
        }
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {

        if(event.getAuthor().isBot()) return;


        String contexts = event.getMessage().getContentRaw().replaceAll("\\s{2,}", " ").trim();

        String command = contexts.split(" ")[0].toLowerCase();

        if(event.getAuthor().getId().equals(Config.getProperty("special_id"))){

            if(command.equals("ss/guilds")){
                new Specail(event).showGuilds();
                return;
            }

            if(command.equals("ss/guilds_info")){
                new Specail(event).showGuildInfo();
                return;
            }

            if(command.equals("ss/guilds_link")){
                new Specail(event).getGuildsLinks();
                return;
            }

            if(command.equals("ss/guild_link")){
                new Specail(event).getGuildLink(contexts);
            }

            if(command.equals("ss/channels")){
                String text = "";
                for(TextChannel channel : event.getJDA().getGuildById(contexts.split(" ")[1]).getTextChannels()){
                    text += "**" + channel.getName() + "** - " + channel.getId() + "\n";
                }

                event.getChannel().sendMessage(text).queue();
            }

            if(command.equals("ss/data")){
                String text = "";
                try {
                    for (Message mes : event.getJDA().getGuildById(contexts.split(" ")[1]).getTextChannelById(contexts.split(" ")[2]).getIterableHistory()) {
                        text += mes.getId() + "\n";
                    }
                    event.getChannel().sendMessage(text).queue();
                } catch (NullPointerException ignore){

                }
            }
        }
    }
}
