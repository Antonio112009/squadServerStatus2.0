package runnable;

import net.dv8tion.jda.api.JDA;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AllTimers {

    private JDA api;
    private Task task;

    public AllTimers(JDA api) {
        this.api = api;
    }

    public void start(){
        task = new Task(api);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        executorService.scheduleAtFixedRate(task::UpdateMessages, 0, 60, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(task::StatusUpdate, 0, 30, TimeUnit.SECONDS);


    }
}
