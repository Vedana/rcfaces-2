/*
 * $Id: PatchedNumberConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.NumberConverter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class PatchedNumberConverter extends NumberConverter {
    

    private static final boolean ENABLE_PATCHED_NUMBER_CONVERTER = true;

    private Locale locale;

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (ENABLE_PATCHED_NUMBER_CONVERTER == false) {
            return super.getAsObject(context, component, value);
        }

        if (context == null || component == null) {
            throw new NullPointerException();
        }

        try {

            // If the specified value is null or zero-length, return null
            if (value == null) {
                return (null);
            }
            value = value.trim();
            if (value.length() < 1) {
                return (null);
            }

            // Identify the Locale to use for parsing
            Locale locale = getLocale(context);

            // Create and configure the parser to be used
            NumberFormat parser = getNumberFormat(locale);
            parser.setParseIntegerOnly(isIntegerOnly());

            String pattern = getPattern();
            String type = getType();

            if (((pattern != null) && !pattern.equals(""))
                    || "currency".equals(type)) {
                configureCurrency(parser);
            }
            configureFormatter(parser);

            // Perform the requested parsing
            return (parser.parse(value));

        } catch (ConverterException e) {
            throw e;
        } catch (ParseException e) {
            // PENDING(craigmcc) - i18n
            throw new ConverterException("Error parsing '" + value + "'");
        }

    }

    private void configureFormatter(NumberFormat formatter) {

        boolean groupingUsed = isGroupingUsed();

        formatter.setGroupingUsed(groupingUsed);
    }

    private Locale getLocale(FacesContext context) {

        // PENDING(craigmcc) - JSTL localization context?
        Locale locale = this.locale;
        if (locale == null) {
            locale = context.getViewRoot().getLocale();
        }
        return (locale);

    }

    private NumberFormat getNumberFormat(Locale locale) {

        String pattern = getPattern();
        String type = getType();

        if (pattern == null && type == null) {
            throw new IllegalArgumentException("Either pattern or type must"
                    + " be specified.");
        }

        // PENDING(craigmcc) - Implement pooling if needed for performance?

        // If pattern is specified, type is ignored
        if (pattern != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
            return (new DecimalFormat(pattern, symbols));
        }

        // Create an instance based on the specified type
        else if (type.equals("currency")) {
            return (NumberFormat.getCurrencyInstance(locale));
        } else if (type.equals("number")) {
            return (NumberFormat.getNumberInstance(locale));
        } else if (type.equals("percent")) {
            return (NumberFormat.getPercentInstance(locale));
        } else {
            // PENDING(craigmcc) - i18n
            throw new ConverterException(new IllegalArgumentException(type));
        }

    }

    private void configureCurrency(NumberFormat formatter) {

        // Implementation copied from JSTL's FormatNumberSupport.setCurrency()

        String code = null;
        String symbol = null;

        String currencyCode = getCurrencyCode();
        String currencySymbol = getCurrencySymbol();

        if ((currencyCode == null) && (currencySymbol == null)) {
            return;
        }

        if ((currencyCode != null) && (currencySymbol != null)) {
            code = currencyCode;
        } else if (currencyCode == null) {
            symbol = currencySymbol;
        } else {
            code = currencyCode;
        }

        DecimalFormat df = (DecimalFormat) formatter;

        DecimalFormatSymbols dfs;
        boolean symbolsModified = false;
        if (code != null) {
            Currency currency = Currency.getInstance(code);

            formatter.setCurrency(currency);

            dfs = df.getDecimalFormatSymbols();
        } else {
            dfs = df.getDecimalFormatSymbols();

            dfs.setCurrencySymbol(symbol);
            symbolsModified = true;
        }

        char cs = dfs.getGroupingSeparator();
        // Traitement du Non breaking space !
        if (cs == '\u00A0') {
            dfs.setGroupingSeparator(' ');
            symbolsModified = true;
        }

        if (symbolsModified) {
            df.setDecimalFormatSymbols(dfs);
        }
    }

}
