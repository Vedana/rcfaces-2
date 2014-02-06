/*
 * $Id: WeekDaysConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.ILiteralLocaleCapability;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.model.AbstractConverter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class WeekDaysConverter extends AbstractConverter {
    

    public static final Converter SINGLETON = new WeekDaysConverter();

    private static final Integer ZERO = new Integer(0);

    private static final Map namesOfDayOfWeek = new HashMap(8);

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.String)
     */
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1) {
            return ZERO;
        }
        StringTokenizer st = new StringTokenizer(value, ";, \t\r\n");
        if (st.hasMoreTokens() == false) {
            return ZERO;
        }

        Locale locale = null;
        if (component instanceof ILiteralLocaleCapability) {
            ILiteralLocaleCapability localizedAttributesCapability = (ILiteralLocaleCapability) component;

            locale = localizedAttributesCapability.getLiteralLocale();
        }

        if (locale == null) {
            locale = Locale.getDefault();
        }

        Map names;
        synchronized (namesOfDayOfWeek) {
            names = (Map) namesOfDayOfWeek.get(locale);

            if (names == null) {

                names = new HashMap(14 * 3);

                // Machine par défaut
                Locale defaultLocale = Locale.getDefault();
                if (defaultLocale.equals(Locale.ENGLISH) == false
                        && defaultLocale.equals(locale) == false) {
                    computeNamesOfDayOfWeek(names, defaultLocale);
                }

                // English par défaut
                computeNamesOfDayOfWeek(names, Locale.ENGLISH);

                computeNamesOfDayOfWeek(names, locale);

                namesOfDayOfWeek.put(locale, names);
            }
        }

        int mask = 0;
        for (; st.hasMoreTokens();) {
            String name = st.nextToken().toLowerCase();

            Integer idxOfDay = (Integer) names.get(name);
            if (idxOfDay == null) {
                throw new FacesException("Can not understand day of week '"
                        + name + "'. (current locale=" + locale + ")");
            }

            mask |= idxOfDay.intValue();
        }

        if (mask == 0) {
            return ZERO;
        }

        return new Integer(mask);
    }

    private void computeNamesOfDayOfWeek(Map map, Locale locale) {
        DateFormatSymbols dfs = new DateFormatSymbols(locale);

        String swd[] = dfs.getShortWeekdays();
        if (swd != null) {
            for (int i = 1; i < swd.length; i++) {
                String s = swd[i];
                if (s == null || s.length() < 1) {
                    continue;
                }

                map.put(s.toLowerCase(), new Integer(1 << (i - 1)));
            }
        }

        swd = dfs.getWeekdays();
        if (swd != null) {
            for (int i = 1; i < swd.length; i++) {
                String s = swd[i];
                if (s == null || s.length() < 1) {
                    continue;
                }

                map.put(s.toLowerCase(), new Integer(1 << (i - 1)));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.Object)
     */
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        int i = ((Number) value).intValue();
        if (i < 1) {
            return "";
        }

        Locale locale = null;
        if (component instanceof ILiteralLocaleCapability) {
            ILiteralLocaleCapability localizedAttributesCapability = (ILiteralLocaleCapability) component;

            locale = localizedAttributesCapability.getLiteralLocale();
        }

        if (locale == null) {
            locale = Locale.getDefault();
        }

        DateFormatSymbols dfs = new DateFormatSymbols(locale);
        String sws[] = dfs.getWeekdays();
        if (sws == null || sws.length < 1) {
            return "*** unknown locale ***";
        }

        StringAppender sb = new StringAppender(128);
        int idx = 0;
        for (; i > 0; i >>= 1, idx++) {
            if ((i & 1) == 0) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append(", ");
            }

            if (sws.length >= idx) {
                sb.append("?");
                continue;
            }

            sb.append(sws[idx]);
        }

        return sb.toString();
    }
}
