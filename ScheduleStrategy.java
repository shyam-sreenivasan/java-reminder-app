import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/*
 * ScheduleStrategy - interface who concrete implementation should have getType, and getNextEligibleDateTime
 *          getNextEligibleDateTime - based on a reference time, provides the next eligible time based on the strategy implementation.
 * 
 * AbstractScheduleStrategy - holds a common field called type
 * 
 * FixedDatesStrategy
 *      - provides the next best date from the list of eligible dates.
 * 
 * RecurringScheduleStrategy - 
 *      - provies the next date based on the recurrence conditions like "every 3 hours" or "every 3 days" based on a referenceTime.
 */

public interface ScheduleStrategy {
    public String getType();
    public LocalDateTime getNextEligibleDateTime(LocalDateTime referenceDateTime);
}

@FunctionalInterface
interface StrategyConfigurator<T> {
    void configure(T strategy);
}

abstract class AbstractScheduleStrategy implements ScheduleStrategy {
    String type;
    public AbstractScheduleStrategy(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
}

class FixedDatesStrategy extends AbstractScheduleStrategy {
    List<LocalDateTime> dates = new ArrayList<>();

    public FixedDatesStrategy() {
        super("fixed_dates");
    }

    public FixedDatesStrategy(List<LocalDateTime> dates) {
        super("fixed_dates");
        this.dates = dates;
    }

    public List<LocalDateTime> getDates() {
        return dates;
    }

    public void setDates(List<LocalDateTime> dates) {
        this.dates = dates;
    }

    @Override
    public LocalDateTime getNextEligibleDateTime(LocalDateTime referenceTime) {
        if (referenceTime == null) {
            return dates.stream().findFirst().orElseThrow();
        }

        for(LocalDateTime date : dates) {
            System.out.println("Checking for date vs referenceTime " + date + " " + referenceTime);
            if (date.isAfter(referenceTime)) {
                return date;
            }
        }
        return null;
    }

    public FixedDatesStrategy remindOnDates(List<LocalDateTime> dates) {
        setDates(dates);
        return this;
       
    }
}

class RecurringScheduleStrategy extends AbstractScheduleStrategy {    
    TimeFrequency timeFrequency;
    int repeatingCount;
    LocalDateTime startingFrom;

    @Override
    public LocalDateTime getNextEligibleDateTime(LocalDateTime referenceDateTime) {
        if (referenceDateTime == null) {
            return startingFrom;
        }

        // Calculate the next eligible date based on timeFrequency
        switch (timeFrequency) {
            case HOURS -> {
                return referenceDateTime.plus(repeatingCount, ChronoUnit.HOURS);
            }

            case DAYS -> {
                return referenceDateTime.plus(repeatingCount, ChronoUnit.DAYS);
            }

            case WEEKS -> {
                return referenceDateTime.plus(repeatingCount * 7, ChronoUnit.DAYS); // 1 week = 7 days
            }
            case MONTHS -> {
                return referenceDateTime.plus(repeatingCount, ChronoUnit.MONTHS);
            }

            default -> throw new AssertionError("Unexpected TimeFrequency value: " + timeFrequency);
        }
    }

    enum TimeFrequency {
        DAYS, HOURS, WEEKS, MONTHS
    }

    public RecurringScheduleStrategy() {
        super("recurring");
    }

    public TimeFrequency getTimeFrequency() {
        return timeFrequency;
    }

    public void setTimeFrequency(TimeFrequency timeFrequency) {
        this.timeFrequency = timeFrequency;
    }

    public int getRepeatingCount() {
        return repeatingCount;
    }

    public void setRepeatingCount(int repeatingCount) {
        this.repeatingCount = repeatingCount;
    }

    public LocalDateTime getStartingFrom() {
        return startingFrom;
    }

    public void setStartingFrom(LocalDateTime startingFrom) {
        this.startingFrom = startingFrom;
    }

    public RecurringScheduleStrategy remindEvery(int count) {
        this.repeatingCount = count;
        return this;
    }

    public RecurringScheduleStrategy hours() {
        this.timeFrequency = TimeFrequency.HOURS;
        return this;
    }

    public RecurringScheduleStrategy startingFrom(LocalDateTime startDateTime) {
        this.startingFrom = startDateTime;
        return this;
    }
    
}