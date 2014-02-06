/*
 * $Id: LocaleConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.model.AbstractConverter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class LocaleConverter extends AbstractConverter {
    

    public static final Converter SINGLETON = new LocaleConverter();

    private static final Map localesByName;
    static {
        Locale ls[] = Locale.getAvailableLocales();

        localesByName = new HashMap(ls.length);

        for (int i = 0; i < ls.length; i++) {
            Locale l = ls[i];

            String key = getKey(l);
            if (key == null) {
                continue;
            }

            localesByName.put(key, l);
        }

        Locale defaultLocale = Locale.getDefault();
        localesByName.put("default", defaultLocale);
    }

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value == null || value.length() < 1) {
            return null;
        }

        return localesByName.get(value.toLowerCase());
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        if (value == null) {
            return null;
        }

        Locale locale = (Locale) value;

        return getKey(locale);
    }

    private static String getKey(Locale locale) {
        String lg = locale.getLanguage();
        if (lg == null || lg.length() < 1) {
            return null;
        }

        StringAppender sb = new StringAppender(24);
        sb.append(lg.toLowerCase());

        String cnt = locale.getCountry();
        if (cnt != null && cnt.length() > 0) {
            sb.append('_');
            sb.append(cnt.toLowerCase());

            String var = locale.getVariant();
            if (var != null && var.length() > 0) {
                sb.append('_');
                sb.append(var.toLowerCase());
            }
        }

        return sb.toString();
    }

}
