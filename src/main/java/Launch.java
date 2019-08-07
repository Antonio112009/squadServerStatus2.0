import config.Config;
import listeners.Private;
import listeners.Public;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import runnable.AllTimers;

import java.util.logging.LogManager;

public class Launch {

    public static void main(String[] args) {

        try {
            LogManager.getLogManager().reset();
            JDA api = new JDABuilder(AccountType.BOT)
                    .setToken(Config.getProperty("TOKEN"))
                    .setStatus(OnlineStatus.IDLE)
                    .setActivity(Activity.watching("loading..."))
                    .setAutoReconnect(true)
                    .build().awaitReady();

            api.getPresence().setStatus(OnlineStatus.ONLINE);
            api.getPresence().setActivity(Activity.watching("Bot"));
            api.addEventListener(new Public());
            api.addEventListener(new Private());
            new AllTimers(api).start();
            System.out.println("Bot finished loading");

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

