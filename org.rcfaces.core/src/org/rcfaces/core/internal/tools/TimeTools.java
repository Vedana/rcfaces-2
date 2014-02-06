/*
 * $Id: TimeTools.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.lang.Time;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class TimeTools {
    

    public static Object parseValue(FacesContext context,
            UIComponent component, String value, boolean literalValue) {

        DateFormat dateFormat = getShortTimeFormat(component, literalValue);

        Date d;
        try {
            synchronized (dateFormat) {
                d = dateFormat.parse(value);

                return new Time(d, dateFormat.getCalendar());
            }

        } catch (ParseException e) {
            throw new FacesException("Can not parse time '" + value + "'.", e);
        }
    }

    public static String formatValue(UIComponent component, Time time,
            boolean literalValue) {
        DateFormat dateFormat = getShortTimeFormat(component, literalValue);

        synchronized (dateFormat) {
            Date date = time.getDate(dateFormat.getCalendar());

            return dateFormat.format(date);
        }
    }

    private static DateFormat getShortTimeFormat(UIComponent component,
            boolean literalValue) {
        return (DateFormat) LocaleTools.getDefaultFormat(component,
                LocaleTools.TIME_TYPE, literalValue);
    }

    public static String getDefaultTimeFormatPattern(Locale locale) {
        return LocaleTools.getDefaultPattern(locale, LocaleTools.TIME_TYPE);
    }

    public static String normalizeTimeFormat(
            IComponentRenderContext componentRenderContext, String format) {
        return LocaleTools.normalizeFormat(componentRenderContext, format,
                LocaleTools.TIME_TYPE, Collections.EMPTY_MAP);
    }

}
