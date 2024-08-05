/*
 * Notifiable - interface when implemented by a concrete class can be notified on any changes on <T> entity
 * 
 * User - implements Notifiable and whats to be notified when a Schedulable item is executed.
 */

interface Notifiable<T> {
    public void notifyMe(T obj); // unable to use the notify() as its an inbuilt function.
}

public class User implements Notifiable<Schedulable> {

    String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public void notifyMe(Schedulable schedulable) {
        System.out.println(String.format("Got notification for user %s for item %s", name, schedulable.getId()));
    }

}