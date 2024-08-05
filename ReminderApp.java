import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*--------------------------------------------------------------------------
         * API 1
         * app.newTask(new User("Shyam"), "House keeping")
            .withFixedDatesStrategy(
                fixedDatesStrategy -> fixedDatesStrategy.remindOnDates(dates)
            )
            .schedule();

        app.newTask(new User("Sowmya"), "Dance class")
            .withRecurringDateTimeStrategy(
                s -> s.remindEvery(3)
                        .hours()
                        .startingFrom(LocalDateTime.now())
            )
            .schedule();
         * 
         * 
         * ------------------------ API 2 ----------------------------------------
         * (English language like) Not implemented Yet.
         * 
         * app.newTask()
         *   .name("House keeping")
         *   .remind("every 3 days starting from 2024-04-01 00:33:45 except on SUNDAY");
         *   .schedule();
         * 
         * app.newTask()
         *   .name("House keeping")
         *   .remind("on 2024-04-01 00:33:45, 2024-04-05 22:33:45, 2024-05-27 00:33:45");
         *   .schedule();
         * ------------------------------------------------------------------------------
         */
public class ReminderApp {

    List<Task> tasks = new ArrayList<>();
    public static void main(String[] args) {
        ReminderApp app = new ReminderApp();
        List<LocalDateTime> dates = Arrays.asList(
            LocalDateTime.of(2024, 8, 5, 14, 14, 0),
            LocalDateTime.of(2024, 8, 6, 0, 0, 0),
            LocalDateTime.of(2024, 8, 8, 0, 0, 0)
        );
        
        app.newTask(new User("Shyam"), "House keeping")
            .withFixedDatesStrategy(
                fixedDatesStrategy -> fixedDatesStrategy.remindOnDates(dates)
            )
            .schedule();

        app.newTask(new User("Sowmya"), "Dance class")
            .withRecurringScheduleStrategy(
                s -> s.remindEvery(3)
                        .hours()
                        .startingFrom(LocalDateTime.now())
            )
            .schedule();

        Scheduler scheduler = Scheduler.getInstance();
        scheduler.run();
        scheduler.run();
    }

    public Task newTask(User user, String name) {
        Task task = new Task(user, name);
        tasks.add(task);
        return task;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void markTaskAsCompleted(String name) {
        Task task = getTask(name);
        task.markAsComplete();
        
    }

    public void deleteTask(String name) {
        Task task = getTask(name);
        task.markAsDeleted();
    }

    public Task getTask(String name) {
        return tasks.stream()
        .filter(t -> t.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Task not found with name " + name));
    }
}