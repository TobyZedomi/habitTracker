package habitTracker.service;

import habitTracker.business.Habit;
import habitTracker.persistence.HabitDao;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final HabitDao habitDao;
    private final PushNotificationService pushNotificationService;

    @Scheduled(fixedRate = 60000)
    public void checkReminders() {
        String currentTime = LocalTime.now(ZoneId.of("Europe/Dublin")).format(DateTimeFormatter.ofPattern("HH:mm"));
        log.info("Scheduler running at: {}", currentTime);

        List<Habit> allHabits = habitDao.getAllActiveHabitsWithReminders();
        log.info("Found {} habits with reminders", allHabits.size());

        for (Habit habit : allHabits) {
            log.info("Checking habit: {} reminder: {}", habit.getDescription(), habit.getHabit_reminder());
            if (currentTime.equals(habit.getHabit_reminder())) {
                log.info("Sending notification for habit: {}", habit.getDescription());
                pushNotificationService.sendToUser(
                        habit.getUsername(),
                        "Habit Reminder",
                        "Time to work on: " + habit.getDescription(),
                        String.valueOf(habit.getHabit_id())
                );
            }
        }
    }
}