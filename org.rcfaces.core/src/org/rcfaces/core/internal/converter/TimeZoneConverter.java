/*
 * $Id: TimeZoneConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.model.AbstractConverter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class TimeZoneConverter extends AbstractConverter {
    

    public static final Converter SINGLETON = new TimeZoneConverter();

    private static final Map timeZonesById;
    static {
        String ids[] = TimeZone.getAvailableIDs();

        timeZonesById = new HashMap(ids.length);

        Locale defaultLocale = Locale.getDefault();
        if (Locale.ENGLISH.equals(defaultLocale)) {
            defaultLocale = null;
        }

        for (int i = 0; i < ids.length; i++) {
            TimeZone l = TimeZone.getTimeZone(ids[i]);

            timeZonesById.put(l.getID().toLowerCase(), l);
            timeZonesById.put(l.getDisplayName().toLowerCase(), l);

            if (defaultLocale != null) {
                timeZonesById.put(
                        l.getDisplayName(defaultLocale).toLowerCase(), l);
            }
        }

        TimeZone defaultTimeZone = TimeZone.getDefault();
        timeZonesById.put("default", defaultTimeZone);
    }

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value == null || value.length() < 1) {
            return null;
        }

        return timeZonesById.get(value.toLowerCase());
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        if (value == null) {
            return null;
        }

        TimeZone timeZone = (TimeZone) value;

        return timeZone.getID();
    }
}
