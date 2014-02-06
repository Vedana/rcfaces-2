/*
 * $Id: LocaleTools.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IComponentLocaleCapability;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.converter.LocaleConverter;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class LocaleTools {
    

    private static final Log LOG = LogFactory.getLog(LocaleTools.class);

    public static final boolean NORMALIZE_LOCALE_PARAMETER_SUPPORT = false;

    public static final int DATE_TYPE = 0;

    public static final int TIME_TYPE = 1;

    public static final int DATE_TIME_TYPE = 2;

    public static final int NUMBER_TYPE = 3;

    public static final int INTEGER_TYPE = 4;

    public static final int PERCENT_TYPE = 5;

    public static final int CURRENCY_TYPE = 6;

    public static final int MAX_TYPE = 6;

    private static final Map dateFormatByLocale;
    static {
        if (Constants.CACHED_LOCALE_FORMATS) {
            dateFormatByLocale = new HashMap(32);
        }
    }

    private static final int DEFAULT_STYLE_BY_TYPE[] = new int[MAX_TYPE + 1];
    static {
        DEFAULT_STYLE_BY_TYPE[0] = DateFormat.SHORT;
        DEFAULT_STYLE_BY_TYPE[1] = DateFormat.MEDIUM;
        DEFAULT_STYLE_BY_TYPE[2] = DateFormat.MEDIUM;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
     */
    protected static interface IFormatNormalizer {
        String normalizeFormat(IComponentRenderContext componentRenderContext,
                int type, String format, String param);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
     */
    protected static class LocaleDateTimeFormatNormalizer implements
            LocaleTools.IFormatNormalizer {
        

        private final int style;

        private final Map dateFormatByLocale = new HashMap();

        LocaleDateTimeFormatNormalizer(int style) {
            this.style = style;
        }

        public String normalizeFormat(
                IComponentRenderContext componentRenderContext, int type,
                String format, String param) {

            Locale locale = getLocale(componentRenderContext, param);

            return getFormatPattern(locale, style, type);
        }

        protected Locale getLocale(
                IComponentRenderContext componentRenderContext, String param) {
            Locale locale = null;

            if (param != null) {
                locale = (Locale) LocaleConverter.SINGLETON.getAsObject(null,
                        null, param);
                if (locale == null) {
                    throw new FacesException("Invalid locale name '" + param
                            + "'.");
                }
            }

            if (locale == null) {
                locale = componentRenderContext.getRenderContext()
                        .getProcessContext().getUserLocale();
            }

            return locale;
        }

        public DateFormat getDateFormat(Locale locale) {
            DateFormat dateFormat;
            synchronized (dateFormatByLocale) {
                dateFormat = (DateFormat) dateFormatByLocale.get(locale);
                if (dateFormat == null) {
                    dateFormat = DateFormat.getDateInstance(style, locale);
                    dateFormatByLocale.put(locale, dateFormat);
                }
            }
            return dateFormat;
        }

    }

    public static String normalizeFormat(
            IComponentRenderContext componentRenderContext, String format,
            int type, Map normalizers) {
        if (format == null || format.length() < 1) {
            return format;
        }

        String param = null;
        if (LocaleTools.NORMALIZE_LOCALE_PARAMETER_SUPPORT) {
            if (format.charAt(0) != '$') {
                return format;
            }

            format = format.substring(1);

            int idx = format.indexOf('(');
            if (idx >= 0) {
                param = format.substring(idx + 1);
                format = format.substring(0, idx);

                idx = param.lastIndexOf(')');
                if (idx < 0 || idx != param.length() - 1) {
                    throw new FacesException("Invalid date format '" + format
                            + "' parentheses are not correctly balanced.");
                }

                param = param.substring(0, idx);
            }
        }

        LocaleTools.IFormatNormalizer normalizer = (LocaleTools.IFormatNormalizer) normalizers
                .get(format.toUpperCase());
        if (normalizer == null) {
            return format;
        }

        return normalizer.normalizeFormat(componentRenderContext, type, format,
                param);
    }

    private static final CachedLocale getCachedLocale(Locale locale) {
        synchronized (dateFormatByLocale) {
            CachedLocale cachedLocale = (CachedLocale) dateFormatByLocale
                    .get(locale);
            if (cachedLocale != null) {
                return cachedLocale;
            }

            cachedLocale = new CachedLocale(locale);
            dateFormatByLocale.put(locale, cachedLocale);

            return cachedLocale;
        }
    }

    private static Format getFormatByType(Locale locale, TimeZone timeZone,
            int type, int style) {
        if (style < 0) {
            style = DEFAULT_STYLE_BY_TYPE[type];
        }

        Format format;
        switch (type) {
        case DATE_TYPE:
            format = DateFormat.getDateInstance(style, locale);
            break;

        case TIME_TYPE:
            format = DateFormat.getTimeInstance(style, locale);
            break;

        case DATE_TIME_TYPE:
            format = DateFormat.getDateTimeInstance(style, style, locale);
            break;

        case NUMBER_TYPE:
            return NumberFormat.getNumberInstance(locale);

        case INTEGER_TYPE:
            return NumberFormat.getIntegerInstance(locale);

        case PERCENT_TYPE:
            return NumberFormat.getPercentInstance(locale);

        case CURRENCY_TYPE:
            return NumberFormat.getCurrencyInstance(locale);

        default:
            LOG.error("Invalid format type=" + type);
            return null;
        }

        if (timeZone != null && (format instanceof DateFormat)) {
            DateFormat dateFormat = (DateFormat) format;

            if (dateFormat.getCalendar().getTimeZone().equals(timeZone) == false) {
                dateFormat.setTimeZone(timeZone);
            }
        }

        return format;
    }

    private static String getPattern(Format format) {
        if (format instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) format).toPattern();
        }

        if (format instanceof DecimalFormat) {
            return ((DecimalFormat) format).toPattern();
        }

        throw new FacesException("Can not get format pattern from format: "
                + format);

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
     */
    private static final class CachedLocale {
        private final Locale locale;

        private final Format defaultFormats[];

        private String patternsByType[][];

        public CachedLocale(Locale locale) {
            this.locale = locale;

            this.defaultFormats = new Format[MAX_TYPE + 1];
        }

        public Locale getLocale() {
            return locale;
        }

        public Format getDefaultFormat(int type) {
            Format format;

            synchronized (defaultFormats) {
                format = defaultFormats[type];
                if (format == null) {
                    format = getFormatByType(locale, null, type, -1);
                    defaultFormats[type] = format;
                }
            }

            return format;
        }

        public String getDefaultPattern(int type) {
            return getPattern(type, DEFAULT_STYLE_BY_TYPE[type]);
        }

        public String getPattern(int type, int style) {

            synchronized (this) {
                if (patternsByType == null) {
                    patternsByType = new String[MAX_TYPE + 1][];
                }

                String patterns[] = patternsByType[type];
                if (patterns != null) {
                    String pattern = patterns[style];
                    if (pattern != null) {
                        return pattern;
                    }
                } else {
                    patterns = new String[DateFormat.SHORT + 1];
                    patternsByType[type] = patterns;
                }

                boolean dateFormat = (type == DATE_TYPE);
                if (dateFormat && style == DateFormat.MEDIUM) {
                    // Remplace les 2 yy par 4 yyyy
                    // On retourne au format SHORT pour transformer ensuite le
                    // yy en yyyy
                    style = DateFormat.SHORT;
                }

                Format format;
                if (style == DEFAULT_STYLE_BY_TYPE[type]) {
                    format = getDefaultFormat(type);

                } else {
                    format = getFormatByType(locale, null, type, style);
                }

                String pattern;
                synchronized (format) {
                    pattern = LocaleTools.getPattern(format);
                }

                if (dateFormat) {
                    // Remplace les 2 yy en 4 yyyy
                    if (pattern.indexOf("yyy") < 0) {
                        int idx = pattern.indexOf("yy");
                        if (idx >= 0) {
                            pattern = pattern.substring(0, idx) + "yy"
                                    + pattern.substring(idx);
                        }
                    }
                }

                patterns[style] = pattern;

                return pattern;
            }
        }
    }

    public static String getFormatPattern(Locale locale, int style, int type) {

        if (Constants.CACHED_LOCALE_FORMATS == false) {
            Format format = getFormatByType(locale, null, type, style);

            return getPattern(format);
        }

        return LocaleTools.getCachedLocale(locale).getPattern(type, style);
    }

    public static Locale getLocale(UIComponent component, boolean literalValue) {
        if (literalValue) {
            return PageConfiguration.getLiteralLocale(null, component);
        }

        if (component instanceof IComponentLocaleCapability) {
            Locale locale = ((IComponentLocaleCapability) component)
                    .getComponentLocale();

            if (locale != null) {
                return locale;
            }
        }

        return ContextTools.getUserLocale(null);
    }

    public static Format getDefaultFormat(UIComponent component, int type,
            boolean literalValue) {

        Locale locale = getLocale(component, literalValue);

        if (Constants.CACHED_LOCALE_FORMATS == false) {
            return getFormatByType(locale, null, type, -1);
        }

        return LocaleTools.getCachedLocale(locale).getDefaultFormat(type);
    }

    public static String getDefaultPattern(Locale locale, int type) {

        if (Constants.CACHED_LOCALE_FORMATS == false) {
            Format format = getFormatByType(locale, null, type, -1);

            return getPattern(format);
        }

        return LocaleTools.getCachedLocale(locale).getDefaultPattern(type);
    }

}
