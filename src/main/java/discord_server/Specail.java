package discord_server;

import config.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;


public class Specail extends ListenerAdapter {

    private PrivateMessageReceivedEvent event;
    private int minutes = Integer.valueOf(Config.getProperty("time_expire"));

    public Specail(PrivateMessageReceivedEvent event) {
        this.event = event;
    }


    public void showGuilds(){
        StringBuilder text = new StringBuilder();
        for(Guild guild : event.getJDA().getGuilds()){
            text.append("**").append(guild.getName()).append("** - ").append(guild.getId()).append("\n");
        }

        event.getChannel().sendMessage(text.toString()).queue();
    }


    public void showGuildInfo(){
        String text = "";

        for(Guild guild : event.getJDA().getGuilds()){
            text += "**" + guild.getName() + "**\n" +
                    "number of people: `" + guild.getMembers().size() + "`";
            try {
                text += "owner: **" + guild.getOwner().getEffectiveName()+ "\n";
            } catch (Exception ignore){}
        }

        event.getChannel().sendMessage(text).queue(
                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
        );
    }

    public void getGuildsLinks() {
        StringBuilder text = new StringBuilder();
        for(Guild guild : event.getJDA().getGuilds()){
            try {
                text.append("link: ").append(guild.getDefaultChannel().createInvite().complete().getUrl()).append("\n\n");
            } catch (Exception e){
                text.append("\n\n");
            }
        }

        event.getChannel().sendMessage(text.toString()).queue(
                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
        );
    }

}
