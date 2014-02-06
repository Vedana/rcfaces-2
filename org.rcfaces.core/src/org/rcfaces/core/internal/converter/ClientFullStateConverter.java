/*
 * $Id: ClientFullStateConverter.java,v 1.3 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.IClientFullStateCapability;
import org.rcfaces.core.component.capability.IHorizontalTextPositionCapability;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:03 $
 */
@SuppressWarnings("deprecation")
public class ClientFullStateConverter implements Converter {
    

    public static final Converter SINGLETON = new ClientFullStateConverter();

    private static final String NONE_CLIENT_FULL_STATE_NAME = "none";

    private static final String ONEWAY_CLIENT_FULL_STATE_NAME = "oneway";

    private static final String TWOWAYS_CLIENT_FULL_STATE_NAME = "twoways";

    private static final String FALSE_CLIENT_FULL_STATE_NAME = "false";

    private static final String TRUE_CLIENT_FULL_STATE_NAME = "true";

    private static final Integer DEFAULT_CLIENT_FULL_STATE = new Integer(
            IHorizontalTextPositionCapability.DEFAULT_POSITION);

    private static Map<String, Integer> FULL_STATES = new HashMap<String, Integer>(
            8);
    static {
        Integer i = new Integer(
                IClientFullStateCapability.NONE_CLIENT_FULL_STATE);
        FULL_STATES.put(NONE_CLIENT_FULL_STATE_NAME, i);

        i = new Integer(IClientFullStateCapability.ONEWAY_CLIENT_FULL_STATE);
        FULL_STATES.put(ONEWAY_CLIENT_FULL_STATE_NAME, i);

        i = new Integer(IClientFullStateCapability.TWOWAYS_CLIENT_FULL_STATE);
        FULL_STATES.put(TWOWAYS_CLIENT_FULL_STATE_NAME, i);

        i = new Integer(IClientFullStateCapability.FALSE_CLIENT_FULL_STATE);
        FULL_STATES.put(FALSE_CLIENT_FULL_STATE_NAME, i);

        i = new Integer(IClientFullStateCapability.TRUE_CLIENT_FULL_STATE);
        FULL_STATES.put(TRUE_CLIENT_FULL_STATE_NAME, i);
    }

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1
                || "default".equalsIgnoreCase(value)) {
            return DEFAULT_CLIENT_FULL_STATE;
        }

        value = value.toLowerCase();

        Integer i = FULL_STATES.get(value);
        if (i != null) {
            return i;
        }

        throw new IllegalArgumentException("Keyword '" + value
                + "' is not supported for a full-state type !");
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        if (value == null) {
            value = DEFAULT_CLIENT_FULL_STATE;
        }

        if ((value instanceof Integer) == false) {
            throw new IllegalArgumentException("Value must be an Integer !");
        }

        for (Iterator<Map.Entry<String, Integer>> it = FULL_STATES.entrySet()
                .iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();

            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException("Value '" + value
                + "' is not supported for a full-state type !");
    }

}
