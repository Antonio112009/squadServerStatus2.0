package discord_server.discord;

import config.Config;
import config.ResultColor;
import embedMesssage.EmbedMessage;
import entities.Authorities;
import entities.Guilds;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import repository.AuthorityRepository;
import repository.GuildRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Access {

    private GuildMessageReceivedEvent event;
    private int minutes = Integer.valueOf(Config.getProperty("time_expire"));

    public Access(GuildMessageReceivedEvent event) {
        this.event = event;
    }

    public void showAccess(){
        List<Authorities>  authorities = new AuthorityRepository().getAllAccessByGuildId(event.getGuild().getIdLong());
        StringBuilder embedText = new StringBuilder();
        embedText.append("All members with Permission `MANAGE SERVER`\n");
        if(authorities.size() > 0){
            for(Authorities authority : authorities){
                try {
                    embedText.append("Member with role - `").append(event.getGuild().getRoleById(authority.getDiscord_id()).getName()).append("`\n");
                } catch (Exception ignore){
                    ignore.printStackTrace();
                }
            }
        }

        embedText.append("\nTo add more roles - use command `ss/addrole`\n");
        embedText.append("To remove roles - use command `ss/removerole`\n");

        event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor("Who has access to the bot:", embedText.toString(), ResultColor.DEFAULT)).queue(
                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
        );
    }


    public void addAccess(){
        if(event.getMessage().getMentionedRoles().size() == 0){
            event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor("Error occurred", "You forgot to mention roles", ResultColor.ERROR)).queue(
                    (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
            );
            return;
        }

        StringBuilder embedText = new StringBuilder();
        Guilds guild = new GuildRepository().getGuildByDiscordId(event.getGuild().getIdLong());
        List<Authorities> authorities = guild.getAuthorities();

        for(Role role : event.getMessage().getMentionedRoles()){
            boolean exist = false;
            for(Authorities authority : authorities){
                if(authority.getDiscord_id() == role.getIdLong()) {
                    exist = true;
                    break;
                }
            }

            if(exist){
                embedText.append("\u26A0 Role **").append(role.getName()).append("** already added to the bot\n\n");
            } else {
                Authorities newAuth = new Authorities();
                newAuth.setDiscord_id(role.getIdLong());
                guild.addAuthority(newAuth);
                new GuildRepository().saveGuild(guild);
                embedText.append("\u2705  Role **").append(role.getName()).append("** successfully added to the bot\n\n");
            }
        }

        event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor("Result:",embedText.toString(), ResultColor.SUCCESS)).queue(
                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
        );
    }


    public void removeAccess(){

        if(event.getMessage().getMentionedRoles().size() == 0){
            event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor("Error occurred", "You forgot to mention roles", ResultColor.ERROR)).queue(
                    (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
            );
            return;
        }

        Guilds guild = new GuildRepository().getGuildByDiscordId(event.getGuild().getIdLong());
        List<Authorities> authorities = guild.getAuthorities();
        StringBuilder embedText = new StringBuilder();

        for(Role role : event.getMessage().getMentionedRoles()){
           Authorities deleteAuth = null;
            for(Authorities authority : authorities){
                if(authority.getDiscord_id() == role.getIdLong()) {
                    deleteAuth = authority;
                    break;
                }
            }

            if(deleteAuth != null){
                embedText.append("\u2705 Role **").append(role.getName()).append("** - successfully deleted from access list\n");
                guild.removeAuthority(deleteAuth);
                new GuildRepository().saveGuild(guild);
            } else {
                embedText.append("\u274C Role **").append(role.getName()).append("** - does not exist in access list\n");
            }
        }

        event.getChannel().sendMessage(new EmbedMessage().messageTitleTextColor("Result", embedText.toString(), ResultColor.SUCCESS)).queue(
                (m) -> m.delete().queueAfter(minutes, TimeUnit.MINUTES)
        );
    }
}