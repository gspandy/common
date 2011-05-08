package com.porpoise.common.date;

import java.util.Calendar;
import java.util.Date;

public enum Dates {
    ; // uninstantiable

    public static Date yearMonthDay(final int year, final int oneBasedMonth, final int day) {
        final Calendar cal = now();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, oneBasedMonth - 1);
        cal.set(Calendar.DATE, day);
        cal.clear();
        return cal.getTime();
    }

    public static Calendar now() {
        return Calendar.getInstance();
    }

}
