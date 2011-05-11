package com.porpoise.common.date;

import java.util.Calendar;
import java.util.Date;

/**
 * Dates utility (prefer Joda-time if possible)
 */
public enum Dates {
    ; // uninstantiable

    /**
     * @param year
     *            the year
     * @param oneBasedMonth
     *            the month (1-12, inclusive)
     * @param day
     *            the day
     * @return a new date for the given year, (one-based) month and day
     */
    public static Date yearMonthDay(final int year, final int oneBasedMonth, final int day) {
        final Calendar cal = now();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, oneBasedMonth - 1);
        cal.set(Calendar.DATE, day);
        cal.clear();
        return cal.getTime();
    }

    /**
     * @return the calendar representing the time when this method is called
     */
    public static Calendar now() {
        return Calendar.getInstance();
    }

}
