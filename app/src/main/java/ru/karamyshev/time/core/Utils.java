package ru.karamyshev.time.core;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.karamyshev.time.model.TimeType;

public class Utils {

    public static Calendar getEndDateForType(TimeType timeType, int shiftPeriod) {
        Calendar calendar = new GregorianCalendar();
        calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        switch (timeType) {
            case DAY:
                calendar.add(Calendar.DATE, 1);
                calendar.add(Calendar.MILLISECOND, -1);
                calendar.add(Calendar.DATE, shiftPeriod);
                break;
            case WEEK:
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                calendar.get(Calendar.DATE);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                calendar.add(Calendar.DATE, 1);
                calendar.add(Calendar.MILLISECOND, -1);
                calendar.add(Calendar.WEEK_OF_YEAR, shiftPeriod);
                break;
            case MONTH:
                calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
                calendar.add(Calendar.DATE, 1);
                calendar.add(Calendar.MILLISECOND, -1);
                calendar.add(Calendar.MONTH, shiftPeriod);
                break;
            case YEAR:
                calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
                calendar.add(Calendar.DATE, 1);
                calendar.add(Calendar.MILLISECOND, -1);
                calendar.add(Calendar.YEAR, shiftPeriod);
        }
        return calendar;
    }

    public static Calendar getStartDateForType(TimeType timeType, int shiftPeriod) {
        Calendar calendar = new GregorianCalendar();
        calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        switch (timeType) {
            case DAY:
                calendar.add(Calendar.DATE, shiftPeriod);
                break;
            case WEEK:
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                calendar.get(Calendar.DATE);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                calendar.add(Calendar.WEEK_OF_YEAR, shiftPeriod);
                break;
            case MONTH:
                calendar.set(Calendar.DATE, 1);
                calendar.add(Calendar.MONTH, shiftPeriod);
                break;
            case YEAR:
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                calendar.set(Calendar.DATE, 1);
                calendar.add(Calendar.YEAR, shiftPeriod);
        }
        return calendar;
    }
}
