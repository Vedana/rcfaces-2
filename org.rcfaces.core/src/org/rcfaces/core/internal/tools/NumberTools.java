/*
 * $Id: NumberTools.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class NumberTools {
    

    private static final Map decimalFormatSymbolsByLocale;
    static {
        if (Constants.CACHED_LOCALE_FORMATS) {
            decimalFormatSymbolsByLocale = new HashMap(32);
        }
    }

    public static String getDefaultNumberFormatPattern(Locale locale) {
        return LocaleTools.getDefaultPattern(locale, LocaleTools.NUMBER_TYPE);
    }

    public static String getDefaultIntegerFormatPattern(Locale locale) {
        return LocaleTools.getDefaultPattern(locale, LocaleTools.INTEGER_TYPE);
    }

    public static String getDefaultPercentFormatPattern(Locale locale) {
        return LocaleTools.getDefaultPattern(locale, LocaleTools.PERCENT_TYPE);
    }

    public static String getDefaultCurrencyFormatPattern(Locale locale) {
        return LocaleTools.getDefaultPattern(locale, LocaleTools.CURRENCY_TYPE);
    }

    public static String normalizeNumberFormat(
            IComponentRenderContext componentRenderContext, String format) {
        return LocaleTools.normalizeFormat(componentRenderContext, format,
                LocaleTools.NUMBER_TYPE, Collections.EMPTY_MAP);
    }

    public static DecimalFormatSymbols getDefaultDecimalFormatSymbols() {
        return getDecimalFormatSymbols(Locale.ENGLISH);
    }

    public static DecimalFormatSymbols getDecimalFormatSymbols(Locale locale) {
        if (Constants.CACHED_LOCALE_FORMATS == false) {
            NumberFormat decimalFormat = NumberFormat.getInstance(locale);

            if ((decimalFormat instanceof DecimalFormat) == false) {
                decimalFormat = NumberFormat.getInstance(Locale.ENGLISH);
            }

            return ((DecimalFormat) decimalFormat).getDecimalFormatSymbols();
        }

        DecimalFormatSymbols symbols;
        synchronized (decimalFormatSymbolsByLocale) {
            symbols = (DecimalFormatSymbols) decimalFormatSymbolsByLocale
                    .get(locale);

            if (symbols != null) {
                return symbols;
            }

            NumberFormat decimalFormat = NumberFormat.getInstance(locale);

            if ((decimalFormat instanceof DecimalFormat) == false) {
                decimalFormat = NumberFormat.getInstance(Locale.ENGLISH);
            }

            symbols = ((DecimalFormat) decimalFormat).getDecimalFormatSymbols();

            decimalFormatSymbolsByLocale.put(locale, symbols);
        }

        return symbols;
    }

}
