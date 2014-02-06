/*
 * $Id: CalendarModeConverter.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.ICalendarModeCapability;
import org.rcfaces.core.model.AbstractConverter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public class CalendarModeConverter extends AbstractConverter {

    public static final Converter SINGLETON = new CalendarModeConverter();

    private static final Integer DEFAULT_CALENDAR_MODE = new Integer(
            ICalendarModeCapability.DEFAULT_CALENDAR_MODE);

    private static Map<String, Integer> CALENDAR_MODES = new HashMap<String, Integer>(
            5);
    static {
        CALENDAR_MODES.put(ICalendarModeCapability.DATE_CALENDAR_MODE_NAME,
                new Integer(ICalendarModeCapability.DATE_CALENDAR_MODE));
        CALENDAR_MODES.put(ICalendarModeCapability.PERIOD_CALENDAR_MODE_NAME,
                new Integer(ICalendarModeCapability.PERIOD_CALENDAR_MODE));
        CALENDAR_MODES.put(ICalendarModeCapability.DEFAULT_CALENDAR_MODE_NAME,
                new Integer(ICalendarModeCapability.DEFAULT_CALENDAR_MODE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext
     * , javax.faces.component.UIComponent, java.lang.String)
     */
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1 || "default".equals(value)) {
            return DEFAULT_CALENDAR_MODE;
        }

        value = value.toLowerCase();

        Integer i = CALENDAR_MODES.get(value);
        if (i != null) {
            return i;
        }

        throw new IllegalArgumentException("Keyword '" + value
                + "' is not supported for a calendar-mode type !");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext
     * , javax.faces.component.UIComponent, java.lang.Object)
     */
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        if (value == null) {
            value = DEFAULT_CALENDAR_MODE;
        }

        if ((value instanceof Integer) == false) {
            throw new IllegalArgumentException("Value must be an Integer !");
        }

        for (Map.Entry<String, Integer> entry : CALENDAR_MODES.entrySet()) {

            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException("Value '" + value
                + "' is not supported for a calendar-mode type !");
    }

    public static final String getName(int hiddenMode) {
        switch (hiddenMode) {
        case ICalendarModeCapability.DATE_CALENDAR_MODE:
            return ICalendarModeCapability.DATE_CALENDAR_MODE_NAME;

        case ICalendarModeCapability.PERIOD_CALENDAR_MODE:
            return ICalendarModeCapability.PERIOD_CALENDAR_MODE_NAME;
        }

        return null;
    }
}
