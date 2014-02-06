/*
 * $Id: Time.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 */
package org.rcfaces.core.lang;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public class Time extends DefaultAdaptable implements Serializable,
		Comparable<Time> {

    private static final long serialVersionUID = -5495525689862764775L;

    private int time;

    public Time() {
        this(0, 0, 0, 0);
    }

    public Time(int hours) {
        this(hours, 0, 0, 0);
    }

    public Time(int hours, int minutes) {
        this(hours, minutes, 0, 0);
    }

    public Time(int hours, int minutes, int seconds) {
        this(hours, minutes, seconds, 0);
    }

    public Time(int hours, int minutes, int seconds, int millis) {
        this.time = ((((hours * 60) + minutes) * 60) + seconds) * 1000 + millis;
    }

    public Time(Date d, TimeZone timeZone) {
        this(d, Calendar.getInstance(timeZone));
    }

    public Time(Date d, Locale locale) {
        this(d, Calendar.getInstance(locale));
    }

    public Time(Date d, Calendar calendar) {
        int hours;
        int minutes;
        int seconds;
        int millis;

        synchronized (calendar) {
            calendar.setTime(d);

            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);
            millis = calendar.get(Calendar.MILLISECOND);
        }

        this.time = ((((hours * 60) + minutes) * 60) + seconds) * 1000 + millis;
    }

    public int getHours() {
        return time / (60 * 60 * 1000);
    }

    public int getMinutes() {
        return (time / (60 * 1000)) % 60;
    }

    public int getSeconds() {
        return (time / 1000) % 60;
    }

    public int getMillis() {
        return time % 1000;
    }

	public int compareTo(Time o) {
		return time - o.time;
    }

    @Override
    public int hashCode() {
        return time;
    }

    public long getTime() {
        return time;
    }

    public Date getDate(TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);

        return getDate(calendar);
    }

    public Date getDate(Locale locale) {
        if (locale == null) {
            locale = FacesContext.getCurrentInstance().getViewRoot()
                    .getLocale();
        }
        Calendar calendar = Calendar.getInstance(locale);

        return getDate(calendar);
    }

    public Date getDate(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, getHours());
        calendar.set(Calendar.MINUTE, getMinutes());
        calendar.set(Calendar.SECOND, getSeconds());
        calendar.set(Calendar.MILLISECOND, getMillis());

        return calendar.getTime();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Time other = (Time) obj;
        if (time != other.time)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[Time " + getHours() + ":" + getMinutes() + ":" + getSeconds()
                + "." + getMillis() + "]";
    }
}