/*
 * $Id: HiddenModeConverter.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.IHiddenModeCapability;
import org.rcfaces.core.model.AbstractConverter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class HiddenModeConverter extends AbstractConverter {
    

    private static final Integer DEFAULT_HIDDEN_MODE = new Integer(
            IHiddenModeCapability.DEFAULT_HIDDEN_MODE);

    private static final String IGNORE_HIDDEN_MODE_NAME = "ignore";

    private static final String SERVER_HIDDEN_MODE_NAME = "server";

    private static final String PHANTOM_HIDDEN_MODE_NAME = "phantom";

    private static final String DEFAULT_HIDDEN_MODE_NAME = "default";

    private static final String CLIENT_HIDDEN_MODE_NAME = "client";

    public static final Converter SINGLETON = new HiddenModeConverter();

    private static Map HIDDEN_MODES = new HashMap(5);
    static {
        HIDDEN_MODES.put(IGNORE_HIDDEN_MODE_NAME, new Integer(
                IHiddenModeCapability.IGNORE_HIDDEN_MODE));
        HIDDEN_MODES.put(SERVER_HIDDEN_MODE_NAME, new Integer(
                IHiddenModeCapability.SERVER_HIDDEN_MODE));
        HIDDEN_MODES.put(PHANTOM_HIDDEN_MODE_NAME, new Integer(
                IHiddenModeCapability.PHANTOM_HIDDEN_MODE));
        HIDDEN_MODES.put(DEFAULT_HIDDEN_MODE_NAME, new Integer(
                IHiddenModeCapability.DEFAULT_HIDDEN_MODE));
        HIDDEN_MODES.put(CLIENT_HIDDEN_MODE_NAME, new Integer(
                IHiddenModeCapability.CLIENT_HIDDEN_MODE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.String)
     */
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1) {
            return DEFAULT_HIDDEN_MODE;
        }

        value = value.toLowerCase();

        Integer i = (Integer) HIDDEN_MODES.get(value);
        if (i != null) {
            return i;
        }

        if ("default".equalsIgnoreCase(value)) {
            return DEFAULT_HIDDEN_MODE;
        }

        throw new IllegalArgumentException("Keyword '" + value
                + "' is not supported for a hidden-mode type !");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.Object)
     */
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        if (value == null) {
            return DEFAULT_HIDDEN_MODE_NAME;
        }

        if ((value instanceof Integer) == false) {
            throw new IllegalArgumentException("Value must be an Integer !");
        }

        for (Iterator it = HIDDEN_MODES.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();

            if (value.equals(entry.getValue()) == false) {
                continue;
            }

            return (String) entry.getKey();
        }

        throw new IllegalArgumentException("Value '" + value
                + "' is not supported for a hidden-mode type !");
    }

    public static final String getName(int hiddenMode) {
        switch (hiddenMode) {
        case IHiddenModeCapability.IGNORE_HIDDEN_MODE:
            return IGNORE_HIDDEN_MODE_NAME;

        case IHiddenModeCapability.PHANTOM_HIDDEN_MODE:
            return PHANTOM_HIDDEN_MODE_NAME;

        case IHiddenModeCapability.SERVER_HIDDEN_MODE:
            return SERVER_HIDDEN_MODE_NAME;
        }

        return null;
    }
}
