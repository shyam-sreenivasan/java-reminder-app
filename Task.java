
/*
 * 
 * Schedulable - any concrete class implementing this interface can be scheduled by the scheduler.
 * NotifiableProvider - any concrete class implementing this interface provides an entity to be notified (a Notifiable entity like a User)
 * 
 * Task - implements both Schedulable and NotifiableProvider
 *      - it can be scheduled by the scheduler
 *      - it can provide a notifiable entity which can be notified when task pick for execution.
 * 
 * Executable interface is not implemented since its only a reminder app.
 */
interface Schedulable {
    public String getId();
    public String getType();
    public void schedule();
    public ScheduleStrategy getScheduleStrategy();
    public void unschedule();
}

interface NotifiableProvider<T>{
    public Notifiable<T> getNotifiable();
}

class Task implements Schedulable, NotifiableProvider<Schedulable> {
    String name;
    User user;
    Scheduler scheduler = Scheduler.getInstance();
    private ScheduleStrategy scheduleStrategy;
    TaskStatus status;

    enum TaskStatus {
        CREATED, COMPLETED, DELETED
    }

    public Task(User user, String name) {
        this.name  = name;
        this.user = user;
        this.status = TaskStatus.CREATED;
    }

    public void markAsComplete() {
        scheduler.unschedule(this);
        status = TaskStatus.COMPLETED;
    }

    public void markAsDeleted() {
        scheduler.unschedule(this);
        status = TaskStatus.DELETED;
    }

    public String getName() {
        return name;
    }

    @Override
    public void schedule() {
        scheduler.schedule(this);
    }

    @Override
    public ScheduleStrategy getScheduleStrategy() {
        return scheduleStrategy;
    }

    @Override
    public void unschedule() {
        scheduler.unschedule(this);
    }

    @Override
    public Notifiable<Schedulable> getNotifiable() {
        return this.user;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public String getType() {
        return "task";
    }

    public Task withFixedDatesStrategy(StrategyConfigurator<FixedDatesStrategy> configurator) {
        FixedDatesStrategy fixedDatesStrategy = new FixedDatesStrategy();
        configurator.configure(fixedDatesStrategy);
        this.scheduleStrategy = fixedDatesStrategy;
        return this;
    }

    // Java functional interface implementation.
    public Task withRecurringScheduleStrategy(StrategyConfigurator<RecurringScheduleStrategy> configurator) {
        RecurringScheduleStrategy recurringScheduleStrategy = new RecurringScheduleStrategy();
        configurator.configure(recurringScheduleStrategy);
        this.scheduleStrategy = recurringScheduleStrategy;
        return this;
    }
}

