/*
 * $Id: DateConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.internal.tools.CalendarTools;
import org.rcfaces.core.model.AbstractConverter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class DateConverter extends AbstractConverter {
    

    public static final Converter SINGLETON = new DateConverter();

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        return CalendarTools.parseValue(null, component, value, isLiteral());
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        return CalendarTools.formatDate(component, (Date) value, isLiteral());
    }

    protected boolean isLiteral() {
        return false;
    }
}
