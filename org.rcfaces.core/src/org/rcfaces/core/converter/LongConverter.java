/*
 * $Id: LongConverter.java,v 1.3 2013/11/13 12:53:24 jbmeslin Exp $
 */
package org.rcfaces.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:24 $
 */
public class LongConverter extends AbstractNumberConverter {

    public LongConverter() {
        setIntegerOnly(true);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.trim().length() < 1) {
            Object v = getDefaultValue();
            if (v instanceof Long) {
                return v;
            }
            if (v instanceof Number) {
                return new Long(((Number) v).longValue());
            }
            value = (String) v;
        }

        Number number = (Number) super.getAsObject(context, component, value);

        if (number == null || (number instanceof Long)) {
            return number;
        }

        double d = number.doubleValue();
        if (d < Long.MIN_VALUE || d > Long.MAX_VALUE) {
            throw new ConverterException(
                    "Number can not be converted to long. (value=" + d + ")");
        }

        return new Long(number.longValue());
    }
}
