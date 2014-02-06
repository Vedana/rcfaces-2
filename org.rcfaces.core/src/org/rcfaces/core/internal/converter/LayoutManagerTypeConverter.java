/*
 * $Id: LayoutManagerTypeConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.ILayoutManagerCapability;
import org.rcfaces.core.model.AbstractConverter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class LayoutManagerTypeConverter extends AbstractConverter {
    

    private static final String INHERIT_MODE_NAME = "inherit";

    private static final String INHERITED_MODE_NAME = "inherited";

    private static final String NONE_MODE_NAME = "none";

    private static final String ABSOLUTE_MODE_NAME = "absolute";

    private static final Integer INHERITED_MODE = new Integer(
            ILayoutManagerCapability.INHERITED_LAYOUT_TYPE);

    public static final Converter SINGLETON = new LayoutManagerTypeConverter();

    private static Map LAYOUT_MODES = new HashMap(5);
    static {
        LAYOUT_MODES.put(INHERIT_MODE_NAME, new Integer(
                ILayoutManagerCapability.INHERITED_LAYOUT_TYPE));
        LAYOUT_MODES.put(INHERITED_MODE_NAME, new Integer(
                ILayoutManagerCapability.INHERITED_LAYOUT_TYPE));
        LAYOUT_MODES.put(NONE_MODE_NAME, new Integer(
                ILayoutManagerCapability.NONE_LAYOUT_TYPE));
        LAYOUT_MODES.put(ABSOLUTE_MODE_NAME, new Integer(
                ILayoutManagerCapability.ABSOLUTE_LAYOUT_TYPE));
    }

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1) {
            return INHERITED_MODE;
        }

        value = value.toLowerCase();

        Integer i = (Integer) LAYOUT_MODES.get(value);
        if (i != null) {
            return i;
        }

        if ("default".equalsIgnoreCase(value)) {
            return INHERITED_MODE;
        }

        throw new IllegalArgumentException("Keyword '" + value
                + "' is not supported for a layout manager mode !");
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        if (value == null) {
            return (String) LAYOUT_MODES.get(INHERITED_MODE);
        }

        if ((value instanceof Integer) == false) {
            throw new IllegalArgumentException("Value must be an Integer !");
        }

        for (Iterator it = LAYOUT_MODES.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();

            if (value.equals(entry.getValue())) {
                return (String) entry.getKey();
            }
        }

        throw new IllegalArgumentException("Value '" + value
                + "' is not supported for a layout manager mode !");
    }

    public static final String getName(int layoutManagerMode) {
        switch (layoutManagerMode) {
        case ILayoutManagerCapability.INHERITED_LAYOUT_TYPE:
            return INHERITED_MODE_NAME;

        case ILayoutManagerCapability.NONE_LAYOUT_TYPE:
            return NONE_MODE_NAME;

        case ILayoutManagerCapability.ABSOLUTE_LAYOUT_TYPE:
            return ABSOLUTE_MODE_NAME;
        }

        return null;
    }
}
