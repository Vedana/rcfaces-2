/*
 * $Id: NumberFormatTypeConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.INumberFormatTypeCapability;
import org.rcfaces.core.model.AbstractConverter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class NumberFormatTypeConverter extends AbstractConverter {
    

    public static final Converter SINGLETON = new NumberFormatTypeConverter();

    private static final Integer DEFAULT_NUMBER_FORMAT_TYPE = new Integer(
            INumberFormatTypeCapability.NUMBER_FORMAT_TYPE);

    private static Map NUMBER_FORMAT_TYPES = new HashMap(5);
    static {
        NUMBER_FORMAT_TYPES.put(
                INumberFormatTypeCapability.NUMBER_FORMAT_TYPE_NAME,
                new Integer(INumberFormatTypeCapability.NUMBER_FORMAT_TYPE));
        NUMBER_FORMAT_TYPES.put(
                INumberFormatTypeCapability.INTEGER_FORMAT_TYPE_NAME,
                new Integer(INumberFormatTypeCapability.INTEGER_FORMAT_TYPE));
        NUMBER_FORMAT_TYPES.put(
                INumberFormatTypeCapability.PERCENT_FORMAT_TYPE_NAME,
                new Integer(INumberFormatTypeCapability.PERCENT_FORMAT_TYPE));
        NUMBER_FORMAT_TYPES.put(
                INumberFormatTypeCapability.CURRENCY_FORMAT_TYPE_NAME,
                new Integer(INumberFormatTypeCapability.CURRENCY_FORMAT_TYPE));
    }

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1 || "default".equals(value)) {
            return DEFAULT_NUMBER_FORMAT_TYPE;
        }

        value = value.toLowerCase();

        Integer i = (Integer) NUMBER_FORMAT_TYPES.get(value);
        if (i != null) {
            return i;
        }

        throw new IllegalArgumentException("Keyword '" + value
                + "' is not supported for a number format type !");
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        if (value == null) {
            return (String) NUMBER_FORMAT_TYPES.get(DEFAULT_NUMBER_FORMAT_TYPE);
        }

        if ((value instanceof Integer) == false) {
            throw new IllegalArgumentException("Value must be an Integer !");
        }

        for (Iterator it = NUMBER_FORMAT_TYPES.entrySet().iterator(); it
                .hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();

            if (value.equals(entry.getValue())) {
                return (String) entry.getKey();
            }
        }

        throw new IllegalArgumentException("Value '" + value
                + "' is not supported for a number format type !");
    }

    public static final String getName(int numberFormatType) {
        switch (numberFormatType) {
        case INumberFormatTypeCapability.INTEGER_FORMAT_TYPE:
            return INumberFormatTypeCapability.INTEGER_FORMAT_TYPE_NAME;

        case INumberFormatTypeCapability.NUMBER_FORMAT_TYPE:
            return INumberFormatTypeCapability.NUMBER_FORMAT_TYPE_NAME;

        case INumberFormatTypeCapability.PERCENT_FORMAT_TYPE:
            return INumberFormatTypeCapability.PERCENT_FORMAT_TYPE_NAME;

        case INumberFormatTypeCapability.CURRENCY_FORMAT_TYPE:
            return INumberFormatTypeCapability.CURRENCY_FORMAT_TYPE_NAME;
        }

        return null;
    }
}
