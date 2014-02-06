/*
 * $Id: AbstractNumberConverter.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 */
package org.rcfaces.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public class AbstractNumberConverter extends
        javax.faces.convert.NumberConverter {

    public static final Converter SINGLETON = new AbstractNumberConverter();

    private Object defaultValue;

    public final Object getDefaultValue() {
        return defaultValue;
    }

    public final void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public final void setDefaultValue(Number defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (context == null) {
            context = FacesContext.getCurrentInstance();
        }

        return super.getAsObject(context, component, value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        if (context == null) {
            context = FacesContext.getCurrentInstance();
        }

        return super.getAsString(context, component, value);
    }

}
