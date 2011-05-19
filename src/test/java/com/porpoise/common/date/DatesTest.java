package com.porpoise.common.date;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link Dates} utility
 */
public class DatesTest {

    /**
     * Test for {@link Dates#yearMonthDay(int, int, int)}
     */
    @Test
    public void testYearMonthDay() {
        final Date feb28 = Dates.yearMonthDay(2011, 2, 28);
        final Calendar cal = Dates.now();
        cal.setTime(feb28);
        Assert.assertEquals(2011, cal.get(Calendar.YEAR));
        Assert.assertEquals(28, cal.get(Calendar.DATE));
        Assert.assertEquals(1, cal.get(Calendar.MONTH));
        Assert.assertEquals(0, cal.get(Calendar.HOUR));
        Assert.assertEquals(0, cal.get(Calendar.MINUTE));
        Assert.assertEquals(0, cal.get(Calendar.SECOND));
        Assert.assertEquals(0, cal.get(Calendar.MILLISECOND));
    }

}
