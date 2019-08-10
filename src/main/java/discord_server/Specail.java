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
        for(Guild guild : event.getJDA().getGuilds()){
            try {
                guild.getTextChannels().get(0).createInvite().queue(
                        (e) -> event.getChannel().sendMessage("link: " + e.getUrl() + "\n\n").queue(
                                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
                        )
                );
            } catch (Exception ignored){
            }
        }
    }

    public void getGuildLink(String context) {
        try {
            event.getJDA().getGuildById(context.split(" ")[1]).getTextChannels().get(0).createInvite().queue(
                    (e) -> event.getChannel().sendMessage(e.getUrl()).queue()
            );
        } catch (Exception e){
            event.getChannel().sendMessage("Invitation cannot be created").queue();
        }
    }
}
