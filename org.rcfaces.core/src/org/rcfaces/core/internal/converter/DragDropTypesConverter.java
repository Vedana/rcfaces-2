/*
 * $Id: DragDropTypesConverter.java,v 1.3 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.model.AbstractConverter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:03 $
 */
public class DragDropTypesConverter extends AbstractConverter {
    

    public static final Converter SINGLETON = new DragDropTypesConverter();

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value == null) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(value, ",");

        List<String> list = new ArrayList<String>();

        for (; st.hasMoreTokens();) {
            String token = st.nextToken().trim();

            list.add(token);
        }

        return list.toArray(new String[list.size()]);
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        throw new FacesException("Not implemented !");
    }
}
