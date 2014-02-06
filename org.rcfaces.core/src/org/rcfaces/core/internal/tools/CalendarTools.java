/*
 * $Id: CalendarTools.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IComponentLocaleCapability;
import org.rcfaces.core.component.capability.IComponentTimeZoneCapability;
import org.rcfaces.core.component.capability.IDateFormatCapability;
import org.rcfaces.core.internal.renderkit.AbstractProcessContext;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.tools.LocaleTools.LocaleDateTimeFormatNormalizer;
import org.rcfaces.core.lang.Period;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class CalendarTools {
    

    private static final Log LOG = LogFactory.getLog(CalendarTools.class);

    private static final Date EMPTY_DATE[] = new Date[0];

    private static final Period EMPTY_PERIODS[] = new Period[0];

    private static final int DEFAULT_CENTURY = 1900;

    private static final Map DATE_KEYWORDS = new HashMap(4);

    static {
        DATE_KEYWORDS.put("now", new IDateKeyword() {
            

            public Date getDate(DateFormat dateFormat) {
                return new Date();
            }

        });
        DATE_KEYWORDS.put("today", new DayDateKeyword() {
            

            public Date getDate(DateFormat dateFormat) {
                synchronized (dateFormat) {
                    Calendar calendar = getDayCalendar(dateFormat);

                    return calendar.getTime();
                }
            }
        });

        DATE_KEYWORDS.put("yesterday", new DayDateKeyword() {
            

            public Date getDate(DateFormat dateFormat) {
                synchronized (dateFormat) {
                    Calendar calendar = getDayCalendar(dateFormat);

                    calendar.add(Calendar.DATE, -1);

                    return calendar.getTime();
                }
            }
        });
        DATE_KEYWORDS.put("tomorrow", new DayDateKeyword() {
            

            public Date getDate(DateFormat dateFormat) {
                synchronized (dateFormat) {
                    Calendar calendar = getDayCalendar(dateFormat);

                    calendar.add(Calendar.DATE, 1);

                    return calendar.getTime();
                }
            }
        });
    }

    private static final Map DATE_NORMALIZERS;

    static {
        DATE_NORMALIZERS = new HashMap(4);
        DATE_NORMALIZERS.put("SHORT", new LocaleDateTimeFormatNormalizer(
                DateFormat.SHORT));
        DATE_NORMALIZERS.put("MEDIUM", new LocaleDateTimeFormatNormalizer(
                DateFormat.MEDIUM));
        DATE_NORMALIZERS.put("LONG", new LocaleDateTimeFormatNormalizer(
                DateFormat.LONG));
        DATE_NORMALIZERS.put("FULL", new LocaleDateTimeFormatNormalizer(
                DateFormat.FULL));
    }

    private static Map TIME_ZONE_BY_COUNTRY = new HashMap();
    static {
        addTimeZone("de", "Europe/Berlin");
        addTimeZone("fr", "Europe/Paris");
        addTimeZone("gb", "Europe/London");
        addTimeZone("es", "Europe/Madrid");
    }

    private static void addTimeZone(String countryName, String timeZoneId) {
        TIME_ZONE_BY_COUNTRY.put(countryName.toLowerCase(), TimeZone
                .getTimeZone(timeZoneId));
    }

    /*
     * public static void setDate(AbstractCalendarComponent component, String
     * date) { DateFormat dateFormat = getShortDateFormat(component, xxx);
     * 
     * Date d = parseDate(dateFormat, date);
     * 
     * component.setValue(d); }
     * 
     * public static void setPeriod(AbstractCalendarComponent component, String
     * period) { DateFormat dateFormat = getShortDateFormat(component, xxx);
     * 
     * Date ds[] = parsePeriod(dateFormat, period);
     * 
     * component.setValue(ds); }
     */

    private static Period parsePeriod(DateFormat dateFormat, String dates) {
        StringTokenizer st = new StringTokenizer(dates, ":");
        int cnt = st.countTokens();
        if (cnt == 0) {
            return null;
        }

        Date d = parseDate(dateFormat, st.nextToken());
        if (d == null) {
            return null;
        }

        if (cnt == 1) {
            return new Period(d, d);
        }

        Date d2 = parseDate(dateFormat, st.nextToken());
        if (d2 == null) {
            return null;
        }

        return new Period(d, d2);
    }

    /*
     * private static Date computeNext(FacesContext facesContext, UIComponent
     * calendarComponent, Date date) { Calendar calendar =
     * getAttributesCalendar(facesContext, calendarComponent);
     * 
     * calendar.setTime(date); calendar.add(Calendar.DATE, 1);
     * 
     * return calendar.getTime(); }
     */

    private static Period[] parsePeriods(DateFormat dateFormat, String dates) {
        List l = null;
        StringTokenizer st = new StringTokenizer(dates, ",");
        for (; st.hasMoreTokens();) {
            String token = st.nextToken();

            Period ds = parsePeriod(dateFormat, token);
            if (ds == null) {
                continue;
            }

            if (l == null) {
                l = new ArrayList();
            }

            l.add(ds);
        }

        Period ds[] = EMPTY_PERIODS;
        if (l != null) {
            ds = (Period[]) l.toArray(new Period[l.size()]);
        }

        return ds;
    }

    private static Date[] parseDates(DateFormat dateFormat, String dates) {
        List l = null;
        StringTokenizer st = new StringTokenizer(dates, ",");
        for (; st.hasMoreTokens();) {
            String token = st.nextToken();

            Date ds = parseDate(dateFormat, token);
            if (ds == null) {
                continue;
            }

            if (l == null) {
                l = new ArrayList();
            }

            l.add(ds);
        }

        Date ds[] = EMPTY_DATE;
        if (l != null) {
            ds = (Date[]) l.toArray(new Date[l.size()]);
        }

        return ds;
    }

    /*
     * 
     * public static final Date parseDate(FacesContext facesContext, UIComponent
     * calendarComponent, String date) { }
     */

    private static final Date parseDate(DateFormat dateFormat, String date) {

        IDateKeyword dateKeyword = (IDateKeyword) DATE_KEYWORDS.get(date
                .toLowerCase());
        if (dateKeyword != null) {
            return dateKeyword.getDate(dateFormat);
        }

        try {
            synchronized (dateFormat) {
                return dateFormat.parse(date);
            }

        } catch (ParseException e) {
            throw new FacesException("Can not parse date '" + date + "'.", e);
        }
    }

    private static interface IDateKeyword {
        Date getDate(DateFormat dateFormat);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
     */
    private static abstract class DayDateKeyword implements IDateKeyword {
        

        protected final Calendar getDayCalendar(DateFormat dateFormat) {

            Calendar calendar = dateFormat.getCalendar();
            calendar.setTime(new Date());

            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);

            return calendar;
        }

    }

    public static Object parseValue(IProcessContext processContext,
            UIComponent component, String value, boolean literalValue) {

        DateFormat dateFormat = getDateFormat(processContext, component,
                literalValue);

        if (value.indexOf(':') >= 0) {
            return parsePeriods(dateFormat, value);
        }

        if (value.indexOf(',') >= 0) {
            return parseDates(dateFormat, value);
        }

        return parseDate(dateFormat, value);
    }

    public static Date parseDate(IProcessContext processContext,
            UIComponent component, String value, boolean literalValue) {

        DateFormat dateFormat = getDateFormat(processContext, component,
                literalValue);

        return parseDate(dateFormat, value);
    }

    public static String formatDate(UIComponent calendarComponent, Date date,
            boolean literalValue) {
        DateFormat dateFormat = getDateFormat(null, calendarComponent,
                literalValue);
        synchronized (dateFormat) {
            return dateFormat.format(date);
        }
    }

    public static Date parseTwoDigitYearDate(UIComponent component,
            String value, boolean literalValue) {
        if (value == null || value.length() < 1) {
            return null;
        }

        boolean onlyDigit = true;
        char chs[] = value.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            if (Character.isDigit(chs[i])) {
                continue;
            }

            onlyDigit = false;
            break;
        }

        if (onlyDigit == false) {
            DateFormat dateFormat = getDateFormat(null, component, literalValue);

            return parseDate(dateFormat, value);
        }

        // Il n'y a que l'année de specifier !

        Calendar calendar = getCalendar(null, component, literalValue);

        int year = Integer.parseInt(value);
        if (year < 1000) {
            throw new FacesException(
                    "You must specify two digit year attribute with value more than 1000.");
        }

        calendar.set(year, 0, 1);
        return calendar.getTime();
    }

    private static DateFormat getDateFormat(IProcessContext processContext,
            UIComponent component, boolean literalValue) {

        DateFormat dateFormat = null;
        if (component instanceof IDateFormatCapability) {
            String dateFormatString = ((IDateFormatCapability) component)
                    .getDateFormat();

            if (dateFormatString != null) {
                Locale locale = LocaleTools.getLocale(component, literalValue);

                LocaleDateTimeFormatNormalizer formatNormalizer = (LocaleDateTimeFormatNormalizer) DATE_NORMALIZERS
                        .get(dateFormatString.toUpperCase());

                if (formatNormalizer != null) {
                    return formatNormalizer.getDateFormat(locale);
                }

                try {
                    dateFormat = new SimpleDateFormat(dateFormatString, locale);

                } catch (IllegalArgumentException ex) {
                    throw new FacesException(
                            "Illegal simple date format pattern '"
                                    + dateFormatString + "' for component '"
                                    + component.getId() + "'", ex);
                }
            }
        }

        if (dateFormat == null) {
            dateFormat = (DateFormat) LocaleTools.getDefaultFormat(component,
                    LocaleTools.DATE_TYPE, literalValue);
        }

        TimeZone timeZone = getCalendar(processContext, component, literalValue)
                .getTimeZone();
        if (dateFormat.getCalendar().getTimeZone().equals(timeZone) == false) {
            synchronized (dateFormat) {
                dateFormat = (DateFormat) dateFormat.clone();
            }

            dateFormat.setTimeZone(timeZone);
        }

        return dateFormat;
    }

    public static String getDateFormatPattern(Locale locale, int style) {
        return LocaleTools.getFormatPattern(locale, style,
                LocaleTools.DATE_TYPE);
    }

    public static String normalizeFormat(
            IComponentRenderContext componentRenderContext, String format) {
        return LocaleTools.normalizeFormat(componentRenderContext, format,
                LocaleTools.DATE_TYPE, DATE_NORMALIZERS);
    }

    public static String getDefaultPattern(Locale locale) {
        return LocaleTools.getDefaultPattern(locale, LocaleTools.DATE_TYPE);
    }

    public static Calendar getCalendar(IComponentWriter writer) {
        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        return getCalendar(componentRenderContext.getRenderContext()
                .getProcessContext(), componentRenderContext.getComponent(),
                false);
    }

    public static Calendar getCalendar(IProcessContext processContext,
            UIComponent component, boolean literalValue) {
        Locale locale = null;
        TimeZone timeZone = null;

        if (literalValue) {
            if (processContext == null) {
                processContext = AbstractProcessContext.getProcessContext(null);
            }

            locale = PageConfiguration.getLiteralLocale(processContext,
                    component);
            timeZone = PageConfiguration.getLiteralTimeZone(processContext,
                    component);

        } else {

            if (component instanceof IComponentLocaleCapability) {
                locale = ((IComponentLocaleCapability) component)
                        .getComponentLocale();
            }

            if (component instanceof IComponentTimeZoneCapability) {
                timeZone = ((IComponentTimeZoneCapability) component)
                        .getComponentTimeZone();
            }

            if (locale == null && timeZone == null) {

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Component[" + component.getId()
                            + "] => returns user calendar !");
                }

                if (processContext == null) {
                    processContext = AbstractProcessContext
                            .getProcessContext(null);
                }

                return processContext.getUserCalendar();
            }

            if (locale == null) {
                if (processContext == null) {
                    processContext = AbstractProcessContext
                            .getProcessContext(null);
                }

                if (processContext != null) {
                    locale = processContext.getUserLocale();
                }
            }

            if (timeZone == null) {
                if (processContext == null) {
                    processContext = AbstractProcessContext
                            .getProcessContext(null);
                }

                if (processContext != null) {
                    timeZone = processContext.getUserTimeZone();
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Component[" + component.getId() + "] literal="
                        + literalValue + " => Locale=" + locale + " timeZone="
                        + timeZone);
            }
        }

        if (locale != null && timeZone == null) {
            // Il faut trouver la timeZone associée au locale
            String country = locale.getCountry();
            if (country != null) {
                timeZone = getTimeZoneFromCountry(country);
            }

            if (timeZone == null) {
                timeZone = processContext.getDefaultTimeZone();
            }
        }

        if (locale != null && timeZone != null) {
            return Calendar.getInstance(timeZone, locale);
        }

        if (locale != null) {
            return Calendar.getInstance(locale);
        }

        if (timeZone != null) {
            return Calendar.getInstance(timeZone);
        }

        return Calendar.getInstance();
    }

    private static TimeZone getTimeZoneFromCountry(String country) {
        country = country.toLowerCase();

        return (TimeZone) TIME_ZONE_BY_COUNTRY.get(country);
    }
}
