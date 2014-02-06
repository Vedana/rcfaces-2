/*
 * $Id: ByteConverter.java,v 1.3 2013/11/13 12:53:24 jbmeslin Exp $
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
public class ByteConverter extends AbstractNumberConverter {
    
    public ByteConverter() {
        setIntegerOnly(true);
        setMaxFractionDigits(3);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.trim().length() < 1) {
            Object v = getDefaultValue();
            if (v instanceof Byte) {
                return v;
            }
            if (v instanceof Number) {
                return new Byte(((Number) v).byteValue());
            }
            value = (String) v;
        }

        Number number = (Number) super.getAsObject(context, component, value);

        if (number == null || (number instanceof Byte)) {
            return number;
        }

        long l = number.longValue();
        if (l < Byte.MIN_VALUE || l > Byte.MAX_VALUE) {
            throw new ConverterException(
                    "Number can not be converted to byte. (value=" + l + ")");
        }

        return new Byte(number.byteValue());
    }
}
