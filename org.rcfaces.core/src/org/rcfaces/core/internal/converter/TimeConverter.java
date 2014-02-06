/*
 * $Id: TimeConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.internal.tools.TimeTools;
import org.rcfaces.core.lang.Time;
import org.rcfaces.core.model.AbstractConverter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class TimeConverter extends AbstractConverter {
    

    public static final Converter SINGLETON = new TimeConverter();

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value == null || value.trim().length() < 1) {
            return null;
        }

        return TimeTools.parseValue(context, component, value, isLiteral());
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        if (value == null) {
            return null;
        }

        return TimeTools.formatValue(component, (Time) value, isLiteral());
    }

    protected boolean isLiteral() {
        return false;
    }
}
