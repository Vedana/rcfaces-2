/*
 * $Id: InputTypeConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.IInputTypeCapability;
import org.rcfaces.core.model.AbstractConverter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class InputTypeConverter extends AbstractConverter {
    

    private static final String CHECK_BOX_INPUT_TYPE_NAME = "check";

    private static final String PUSH_BUTTON_INPUT_TYPE_NAME = "push";

    private static final String RADIO_BUTTON_INPUT_TYPE_NAME = "radio";

    private static final String DROP_DOWN_INPUT_TYPE_NAME = "dropdown";

    private static final String RESET_INPUT_TYPE_NAME = "reset";

    private static final String SUBMIT_INPUT_TYPE_NAME = "submit";

    private static final String DEFAULT_INPUT_TYPE_NAME = "default";

    public static final Converter SINGLETON = new InputTypeConverter();

    private static Map INPUT_TYPES = new HashMap(5);
    static {
        INPUT_TYPES.put(CHECK_BOX_INPUT_TYPE_NAME, new Integer(
                IInputTypeCapability.AS_CHECK_BUTTON));
        INPUT_TYPES.put(PUSH_BUTTON_INPUT_TYPE_NAME, new Integer(
                IInputTypeCapability.AS_PUSH_BUTTON));
        INPUT_TYPES.put(RADIO_BUTTON_INPUT_TYPE_NAME, new Integer(
                IInputTypeCapability.AS_RADIO_BUTTON));
        INPUT_TYPES.put(DROP_DOWN_INPUT_TYPE_NAME, new Integer(
                IInputTypeCapability.AS_DROP_DOWN_MENU));
        INPUT_TYPES.put(RESET_INPUT_TYPE_NAME, new Integer(
                IInputTypeCapability.AS_RESET_BUTTON));
        INPUT_TYPES.put(SUBMIT_INPUT_TYPE_NAME, new Integer(
                IInputTypeCapability.AS_SUBMIT_BUTTON));
        INPUT_TYPES.put(DEFAULT_INPUT_TYPE_NAME, new Integer(
                IInputTypeCapability.AS_PUSH_BUTTON));
    }

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1) {
            value = DEFAULT_INPUT_TYPE_NAME;

        } else {
            value = value.toLowerCase();
        }

        Integer i = (Integer) INPUT_TYPES.get(value);
        if (i != null) {
            return i;
        }

        throw new IllegalArgumentException("Keyword '" + value
                + "' is not supported for a input-type type !");
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        if (value == null) {
            return DEFAULT_INPUT_TYPE_NAME;
        }

        if ((value instanceof Integer) == false) {
            throw new IllegalArgumentException("Value must be an Integer !");
        }

        for (Iterator it = INPUT_TYPES.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();

            if (value.equals(entry.getValue()) == false) {
                continue;
            }

            return (String) entry.getKey();
        }

        throw new IllegalArgumentException("Value '" + value
                + "' is not supported for a input-type type !");
    }

    public static final String getName(int inputType) {
        switch (inputType) {
        case IInputTypeCapability.AS_PUSH_BUTTON:
            return PUSH_BUTTON_INPUT_TYPE_NAME;

        case IInputTypeCapability.AS_CHECK_BUTTON:
            return CHECK_BOX_INPUT_TYPE_NAME;

        case IInputTypeCapability.AS_RADIO_BUTTON:
            return RADIO_BUTTON_INPUT_TYPE_NAME;

        case IInputTypeCapability.AS_DROP_DOWN_MENU:
            return DROP_DOWN_INPUT_TYPE_NAME;
        }

        return null;
    }
}
