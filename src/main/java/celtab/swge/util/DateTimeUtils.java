package celtab.swge.util;

import java.time.Instant;
import java.util.Date;

public interface DateTimeUtils {

    default boolean isCurrentDateTimeInInterval(Date start, Date stop) {
        var currentDateTime = Instant.now();
        return !currentDateTime.isBefore(start.toInstant()) && !currentDateTime.isAfter(stop.toInstant());
    }

    default boolean isDateTimeInInterval(Date date, Date start, Date stop) {
        var currentDateTime = date.toInstant();
        return !currentDateTime.isBefore(start.toInstant()) && !currentDateTime.isAfter(stop.toInstant());
    }

    default boolean isIntervalInInterval(Date dateWithinStart, Date dateWithinStop, Date start, Date stop) {
        return isDateTimeInInterval(dateWithinStart, start, stop) && isDateTimeInInterval(dateWithinStop, start, stop);
    }

}
