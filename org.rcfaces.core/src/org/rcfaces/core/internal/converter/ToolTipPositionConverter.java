/*
 * $Id: ToolTipPositionConverter.java,v 1.1 2013/12/11 10:17:38 jbmeslin Exp $
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.IToolTipPositionCapability;
import org.rcfaces.core.model.AbstractConverter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/12/11 10:17:38 $
 */
public class ToolTipPositionConverter extends AbstractConverter {

    public static final Converter SINGLETON = new ToolTipPositionConverter();

    private static final Integer DEFAULT_TOOLTIP_POSITION;

    private static Map<String, Integer> TOOLTIP_POSITIONS = new HashMap<String, Integer>(
            5);
    static {
        TOOLTIP_POSITIONS.put(IToolTipPositionCapability.MOUSE_POSITION,
                Integer.valueOf(0));
        TOOLTIP_POSITIONS.put(
                IToolTipPositionCapability.MIDDLE_COMPONENT_POSITION,
                Integer.valueOf(1));
        TOOLTIP_POSITIONS.put(
                IToolTipPositionCapability.BOTTOM_COMPONENT_POSITION,
                Integer.valueOf(2));
        TOOLTIP_POSITIONS.put(
                IToolTipPositionCapability.LEFT_COMPONENT_POSITION,
                Integer.valueOf(4));
        TOOLTIP_POSITIONS.put(
                IToolTipPositionCapability.BOTTOM_LEFT_COMPONENT_POSITION,
                Integer.valueOf(8));
        TOOLTIP_POSITIONS.put(
                IToolTipPositionCapability.MIDDLE_LEFT_COMPONENT_POSITION,
                Integer.valueOf(12));
        TOOLTIP_POSITIONS.put(
                IToolTipPositionCapability.RIGHT_COMPONENT_POSITION,
                Integer.valueOf(16));
        TOOLTIP_POSITIONS.put(
                IToolTipPositionCapability.BOTTOM_RIGHT_COMPONENT_POSITION,
                Integer.valueOf(32));
        TOOLTIP_POSITIONS.put(
                IToolTipPositionCapability.MIDDLE_RIGHT_COMPONENT_POSITION,
                Integer.valueOf(48));

        DEFAULT_TOOLTIP_POSITION = TOOLTIP_POSITIONS
                .get(IToolTipPositionCapability.BOTTOM_LEFT_COMPONENT_POSITION);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext
     * , javax.faces.component.UIComponent, java.lang.String)
     */
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1 || "default".equals(value)) {
            return DEFAULT_TOOLTIP_POSITION;
        }

        value = value.toLowerCase();

        Integer i = TOOLTIP_POSITIONS.get(value);
        if (i != null) {
            return i;
        }

        throw new IllegalArgumentException("Keyword '" + value
                + "' is not supported for a tooltip-position type !");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext
     * , javax.faces.component.UIComponent, java.lang.Object)
     */
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        if (value == null) {
            value = DEFAULT_TOOLTIP_POSITION;
        }

        if ((value instanceof Integer) == false) {
            throw new IllegalArgumentException("Value must be an Integer !");
        }

        for (Map.Entry<String, Integer> entry : TOOLTIP_POSITIONS.entrySet()) {

            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException("Value '" + value
                + "' is not supported for a calendar-mode type !");
    }
}
