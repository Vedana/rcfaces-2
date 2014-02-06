/*
 * $Id: FilterPropertiesConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.internal.util.StringList;
import org.rcfaces.core.lang.FilterPropertiesMap;
import org.rcfaces.core.model.AbstractConverter;
import org.rcfaces.core.model.IFilterProperties;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class FilterPropertiesConverter extends AbstractConverter {
    

    public static final Converter SINGLETON = new FilterPropertiesConverter();

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.String)
     */
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null) {
            return null;
        }

        Map map = StringList.parseTokensMap(value);

        return new FilterPropertiesMap(map);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.Object)
     */
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {

        IFilterProperties properties = (IFilterProperties) value;

        Map map = properties.toMap();

        return StringList.joinTokens(map);
    }

}
