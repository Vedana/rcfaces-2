/*
 * $Id: AsyncDecodeModeConverter.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.IAsyncDecodeModeCapability;
import org.rcfaces.core.model.AbstractConverter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public class AsyncDecodeModeConverter extends AbstractConverter {

    private static final String DEFAULT_ASYNC_DECODE_MODE_NAME = "default";

    private static final String COMPLETE_ASYNC_DECODE_MODE_NAME = "complete";

    private static final String PARTIAL_ASYNC_DECODE_MODE_NAME = "partial";

    private static final Integer DEFAULT_ASYNC_DECODE_MODE = new Integer(
            IAsyncDecodeModeCapability.DEFAULT_ASYNC_DECODE_MODE);

    public static final Converter SINGLETON = new AsyncDecodeModeConverter();

    private static Map<String, Integer> ASYNC_DECODE_MODES = new HashMap<String, Integer>(
            5);
    static {
        ASYNC_DECODE_MODES.put(COMPLETE_ASYNC_DECODE_MODE_NAME, new Integer(
                IAsyncDecodeModeCapability.COMPLETE_ASYNC_DECODE_MODE));
        ASYNC_DECODE_MODES.put(PARTIAL_ASYNC_DECODE_MODE_NAME, new Integer(
                IAsyncDecodeModeCapability.PARTIAL_ASYNC_DECODE_MODE));
        ASYNC_DECODE_MODES.put(DEFAULT_ASYNC_DECODE_MODE_NAME, new Integer(
                IAsyncDecodeModeCapability.DEFAULT_ASYNC_DECODE_MODE));
    }

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1) {
            return DEFAULT_ASYNC_DECODE_MODE;
        }

        value = value.toLowerCase();

        Integer i = ASYNC_DECODE_MODES.get(value);
        if (i != null) {
            return i;
        }

        if ("default".equalsIgnoreCase(value)) { //merge jbm en trop ou pas ?
            return DEFAULT_ASYNC_DECODE_MODE;
        }

        throw new IllegalArgumentException("Keyword '" + value
                + "' is not supported for a async-decode type !");
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        if (value == null) {
            value = DEFAULT_ASYNC_DECODE_MODE;
        }

        if ((value instanceof Integer) == false) {
            throw new IllegalArgumentException("Value must be an Integer !");
        }

        for (Map.Entry<String, Integer> entry : ASYNC_DECODE_MODES.entrySet()) {

            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException("Value '" + value
                + "' is not supported for a async-decode-mode type !");
    }

    public static final String getName(int asyncDecodeMode) {
        switch (asyncDecodeMode) {
        case IAsyncDecodeModeCapability.COMPLETE_ASYNC_DECODE_MODE:
            return COMPLETE_ASYNC_DECODE_MODE_NAME;

        case IAsyncDecodeModeCapability.PARTIAL_ASYNC_DECODE_MODE:
            return PARTIAL_ASYNC_DECODE_MODE_NAME;
        }

        return null;
    }
}
