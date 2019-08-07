package listeners;

import config.Config;
import discord_server.discord.Access;
import discord_server.discord.Channel;
import discord_server.discord.General;
import discord_server.server.Server;
import embedMesssage.EmbedMessage;
import entities.Guilds;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import repository.AuthorityRepository;
import repository.GuildRepository;

import javax.annotation.Nonnull;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Public extends ListenerAdapter {

    private int minutes = Integer.valueOf(Config.getProperty("time_expire"));

    private GuildRepository guildRepository = new GuildRepository();

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        Objects.requireNonNull(event.getJDA().getUserById(Objects.requireNonNull(Config.getProperty("special_id")))).openPrivateChannel().queue(
                (channel) ->
                        channel.sendMessage(new EmbedMessage().onJoinDiscordServer(event).build())
        );

        try {
            TextChannel defChannel = event.getGuild().getDefaultChannel();

            if (defChannel != null) {
                defChannel.sendMessage(new EmbedMessage().GreetingMessage(event)).queue();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        Guilds guilds = new Guilds();
        guilds.setDiscord_id(event.getGuild().getIdLong());
        guildRepository.saveGuild(guilds);
    }






    //TODO: удалить данные о сервере.
    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        guildRepository.deleteGuildByDiscordId(event.getGuild().getIdLong());
    }





    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {

        if(event.getAuthor().isBot()) return;

        String contexts = event.getMessage().getContentRaw().replaceAll("\\s{2,}", " ").trim();

        String command = contexts.split(" ")[0].toLowerCase();

        if(!contexts.toLowerCase().startsWith("ss/"))
            return;

        event.getMessage().delete().queueAfter(minutes,TimeUnit.MINUTES);

        //Публичные команды
        if(command.equals("ss/about")){
            new General(event).showAbout();
            return;
        }

        if(command.equals("ss/credit") || command.equals("ss/credits")){
            new General(event).showCredits();
            return;
        }

        if(command.equals("ss/link")){
            new General(event).showInviteLink();
            return;
        }


        //Команды для админов

        if(command.equals("ss/exit")){
            return;
        }

        if (!(giveAccess(event) || event.getAuthor().getId().equals(Config.getProperty("special_id"))))
            return;

        //Check how many threads operates now
        if(command.equals("ss/tr")){
            event.getMessage().delete().queue();
            // Get the managed bean for the thread system of the Java
            // virtual machine.
            ThreadMXBean bean = ManagementFactory.getThreadMXBean();

            // Get the current number of live threads including both
            // daemon and non-daemon threads.
            int threadCount = bean.getThreadCount();
            event.getChannel().sendMessage("Thread Count = " + threadCount).queue(
                    (message) -> message.delete().queueAfter(5, TimeUnit.SECONDS)
            );
            return;
        }

        if(command.equals("ss/help")){
            new General(event).showHelp();
            return;
        }

        if(command.equals("ss/guide")){
            new General(event).showGuide();
            return;
        }

        /*
        Channel actions
         */
        if(command.equals("ss/channel")){
            new Channel(event).showChannel();
            return;
        }

        if(command.equals("ss/addchannel")){
            new Channel(event).addChannel();
            return;
        }

        if(command.equals("ss/editchannel")){
            new Channel(event).editChannel();
            return;
        }

        /*
        Server actions
         */
        if(command.equals("ss/servers")){
            new Server(event).showServers();
            return;
        }

        if(command.equals("ss/addserver")){
            new Server(event).addNewServer();
            return;
        }

        if(command.equals("ss/editserver")){
            new Server(event).editServer();
            return;
        }

        if(command.equals("ss/deleteserver")){
            new Server(event).deleteServer();
            return;
        }


        if(command.equals("ss/access")){
            new Access(event).showAccess();
            return;
        }

        if(command.equals("ss/addrole") || command.equals("ss/addroles")){
            new Access(event).addAccess();
            return;
        }

        if(command.equals("ss/removerole") || command.equals("ss/removeroles")){
            new Access(event).removeAccess();
            return;
        }

        if(command.equals("ss/clean")){
            new General(event).cleanChannel();
            return;
        }

        event.getChannel().sendMessage("Invalid command").queue(
                (message) -> message.delete().queueAfter(minutes, TimeUnit.MINUTES)
        );
    }


    private boolean giveAccess(GuildMessageReceivedEvent event){
        for(Permission permission : Objects.requireNonNull(event.getMember()).getPermissions()) {
            if (permission.equals(Permission.MANAGE_SERVER))
                return true;
        }

        //Проверить, что юзер есть в базе
        for (Role role : event.getMember().getRoles()){
            if(new AuthorityRepository().isAuthorityExistByDiscordId(role.getIdLong()))
                return true;
        }

        return  false;
    }
}
