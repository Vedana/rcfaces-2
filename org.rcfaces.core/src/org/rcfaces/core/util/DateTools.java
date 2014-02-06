/*
 * $Id: DateTools.java,v 1.1 2011/04/12 09:25:44 oeuillot Exp $
 */
package org.rcfaces.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:44 $
 */
public class DateTools {
    private static final Calendar dateCalendar = Calendar.getInstance(TimeZone
            .getTimeZone("GMT"));

    private static final Calendar timeCalendar = (Calendar) dateCalendar
            .clone();

    public static Date normalizeDate(Date d) {
        synchronized (dateCalendar) {
            dateCalendar.setTime(d);

            int millisecond = dateCalendar.get(Calendar.MILLISECOND);
            int second = dateCalendar.get(Calendar.SECOND);
            int minute = dateCalendar.get(Calendar.MINUTE);
            int hour = dateCalendar.get(Calendar.HOUR_OF_DAY);

            if (millisecond == 0 && second == 0 && minute == 0 && hour == 0) {
                return d;
            }

            if (millisecond != 0) {
                dateCalendar.set(Calendar.MILLISECOND, 0);
            }
            if (second != 0) {
                dateCalendar.set(Calendar.SECOND, 0);
            }
            if (minute != 0) {
                dateCalendar.set(Calendar.MINUTE, 0);
            }
            if (hour != 0) {
                dateCalendar.set(Calendar.HOUR_OF_DAY, 0);
            }

            return dateCalendar.getTime();
        }
    }

    public static Date normalizeTime(Date d) {
        synchronized (timeCalendar) {
            timeCalendar.setTime(d);

            timeCalendar.set(2000, 0, 1);

            return timeCalendar.getTime();
        }
    }
}
