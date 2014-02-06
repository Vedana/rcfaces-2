/*
 * $Id: OrderConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.model.AbstractConverter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class OrderConverter extends AbstractConverter {
    

    public static final Converter SINGLETON = new OrderConverter();

    private static final String ASCENDING_ORDER_NAME = "ascending";

    private static final String DESCENDING_ORDER_NAME = "descending";

    private static final Boolean DEFAULT_ORDER = Boolean.TRUE;

    private static Map ORDER_TYPES = new HashMap(5);
    static {
        ORDER_TYPES.put(ASCENDING_ORDER_NAME, Boolean.TRUE);

        ORDER_TYPES.put(DESCENDING_ORDER_NAME, Boolean.FALSE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.String)
     */
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1 || "default".equals(value)) {
            return DEFAULT_ORDER;
        }

        value = value.toLowerCase();

        Boolean b = (Boolean) ORDER_TYPES.get(value);
        if (b != null) {
            return b;
        }

        throw new IllegalArgumentException("Keyword '" + value
                + "' is not supported for an order type !");
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
            return (String) ORDER_TYPES.get(DEFAULT_ORDER);
        }

        if ((value instanceof Boolean) == false) {
            throw new IllegalArgumentException("Value must be a Boolean !");
        }

        for (Iterator it = ORDER_TYPES.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();

            if (value.equals(entry.getValue())) {
                return (String) entry.getKey();
            }
        }

        throw new IllegalArgumentException("Value '" + value
                + "' is not supported for an order type !");
    }

}
