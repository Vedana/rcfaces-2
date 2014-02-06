/*
 * $Id: ClientDatesStrategyConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.IClientDatesStrategyCapability;
import org.rcfaces.core.model.AbstractConverter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class ClientDatesStrategyConverter extends AbstractConverter {
    

    public static final Converter SINGLETON = new ClientDatesStrategyConverter();

    private static final String ALL_DATES_STRATEGY = "all";

    private static final String YEAR_DATES_STRATEGY = "year";

    private static final String MONTH_DATES_STRATEGY = "month";

    private static final String DEFAULT_DATES_STRATEGY = "default";

    private static final Object DEFAULT_DATES_STRATEGY_INTEGER = new Integer(
            IClientDatesStrategyCapability.DEFAULT_DATES_STRATEGY);

    private static Map CLIENT_DATES_STRATEGIES = new HashMap(5);
    static {
        CLIENT_DATES_STRATEGIES.put(ALL_DATES_STRATEGY, new Integer(
                IClientDatesStrategyCapability.ALL_DATES_STRATEGY));
        CLIENT_DATES_STRATEGIES.put(YEAR_DATES_STRATEGY, new Integer(
                IClientDatesStrategyCapability.YEAR_DATES_STRATEGY));
        CLIENT_DATES_STRATEGIES.put(MONTH_DATES_STRATEGY, new Integer(
                IClientDatesStrategyCapability.MONTH_DATES_STRATEGY));
        CLIENT_DATES_STRATEGIES.put(DEFAULT_DATES_STRATEGY,
                DEFAULT_DATES_STRATEGY_INTEGER);
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
            return DEFAULT_DATES_STRATEGY_INTEGER;
        }

        value = value.toLowerCase();

        Integer i = (Integer) CLIENT_DATES_STRATEGIES.get(value);
        if (i != null) {
            return i;
        }

        if ("default".equalsIgnoreCase(value)) {
            return DEFAULT_DATES_STRATEGY_INTEGER;
        }

        throw new IllegalArgumentException("Keyword '" + value
                + "' is not supported as a client dates strategy !");
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
            return DEFAULT_DATES_STRATEGY;
        }

        if ((value instanceof Integer) == false) {
            throw new IllegalArgumentException("Value must be an Integer !");
        }

        for (Iterator it = CLIENT_DATES_STRATEGIES.entrySet().iterator(); it
                .hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();

            if (value.equals(entry.getValue()) == false) {
                continue;
            }

            return (String) entry.getKey();
        }

        throw new IllegalArgumentException("Value '" + value
                + "' is not supported as a client dates strategy !");
    }
}
