
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
/*
 * IScheduler - scheduler interface
 * Scheduler - implementation of the IScheduler
 *  - has map of Schedulables and their last Execution as key and value.
 *  - gets the next best time from the schedulable's schedule strategy and determines if it needs to be processed now.
 *  - notifies the Notifiable entity(User) when the schedulable is processed.
 * 
 * Execution - an entity that holds the latest execution information for a task.
 */
interface IScheduler {
    public void schedule(Schedulable schedulable);
    public void unschedule(Schedulable schedulable);
    public void run();
}

class Execution {
    LocalDateTime lastRunTime;

    public LocalDateTime getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(LocalDateTime lastRunTime) {
        this.lastRunTime = lastRunTime;
    }
}

public class Scheduler implements IScheduler {
    Map<Schedulable, Execution> schedulableExecutionMap = new HashMap<>();
    private static Scheduler instance;
    private Scheduler() {}

    public static Scheduler getInstance() {
        if (instance == null) {
            instance = new Scheduler();
        }

        return instance;
    }

    @Override
    public void run() {
        System.out.println("Scheduler execution started at " + LocalDateTime.now());
        for(Map.Entry<Schedulable, Execution> entry : schedulableExecutionMap.entrySet()) {
            Schedulable schedulable = entry.getKey();
            Execution execution = entry.getValue();
            ScheduleStrategy strategy = schedulable.getScheduleStrategy();

            System.out.println(String.format("Starting processing for %s: %s",schedulable.getType(), schedulable.getId()));

            System.out.println("Last execution at " + execution.getLastRunTime());


            LocalDateTime nextRun = strategy.getNextEligibleDateTime(execution.getLastRunTime());
            System.out.println("Got next run time as " + nextRun.toString());
            
            if (nextRun == null) {
                unschedule(schedulable);
                System.out.println("Unscheduled item" + schedulable.toString());
                continue;
            }
            
            if (nextRun.equals(LocalDateTime.now()) || nextRun.isBefore(LocalDateTime.now())) {
                execution.setLastRunTime(nextRun);
                schedulableExecutionMap.replace(schedulable, execution);
                notify(schedulable);
             } else {
                System.out.println("Skipping task for now");
             }
             System.out.println("                          -------------------------------                  ");
       }
       System.out.println("=========================");
    }

    public void notify(Schedulable schedulable) {
        if (schedulable instanceof NotifiableProvider<?> notifiableProvider) {
            @SuppressWarnings("unchecked")
            Notifiable<Schedulable> notifiable = (Notifiable<Schedulable>) notifiableProvider.getNotifiable();
            notifiable.notifyMe(schedulable);
            System.out.println("Notification sent for " + schedulable.getId());
        }   
    }

    @Override
    public void schedule(Schedulable schedulable) {
        this.schedulableExecutionMap.put(schedulable, new Execution());
        System.out.println("scheduled item " + schedulable.toString());
    }

    @Override
    public void unschedule(Schedulable schedulable) {
        schedulableExecutionMap.remove(schedulable);
    }
}