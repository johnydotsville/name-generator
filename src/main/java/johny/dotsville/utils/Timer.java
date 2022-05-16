package johny.dotsville.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;

public class Timer {
    public static <T> T runWithTimeTrack(Callable func, String actionName) throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime from = LocalDateTime.from(start);
        System.out.println("Начали " + actionName + ": " + start);

        T result = (T)func.call();

        LocalDateTime finish = LocalDateTime.now();
        System.out.println("Закончили " + actionName + ": " + LocalDateTime.now());
        long millis = from.until(finish, ChronoUnit.MILLIS);
        System.out.println("Потрачено на " + actionName + ": " + millis);

        return result;
    }
}
