/*
 * $Id: CameliaConverterTag.java,v 1.2 2014/01/07 13:48:20 jbmeslin Exp $
 */
package org.rcfaces.core.internal.taglib;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.webapp.ConverterELTag;
import javax.servlet.jsp.JspException;

public class CameliaConverterTag extends ConverterELTag {

    private static final long serialVersionUID = 1L;

    private ValueExpression binding = null;

    private ValueExpression converterId = null;

    private String defaultConverterId;

    public void setConverterId(ValueExpression converterId) {
        this.converterId = converterId;
    }

    public void setBinding(ValueExpression binding) {
        this.binding = binding;
    }

    // -------------------------------------------- Methods from ConverterELTag

    protected void setConverterId(String converterId) {
        this.defaultConverterId = converterId;
    }

    @Override
    protected Converter createConverter() throws JspException {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        Converter converter = null;

        // If "binding" is set, use it to create a converter instance.
        if (binding != null) {
            try {
                converter = (Converter) binding.getValue(elContext);
                if (converter != null) {
                    return converter;
                }
            } catch (Exception e) {
                throw new JspException(e);
            }
        }

        // If "converterId" is set, use it to create the converter
        // instance. If "converterId" and "binding" are both set, store the
        // converter instance in the value of the property represented by
        // the ValueExpression 'binding'.

        String cid = null;
        if (converterId != null) {
            cid = (String) converterId.getValue(elContext);
        }
        
        if (defaultConverterId != null) {
            cid = defaultConverterId;
        }

        if (cid != null) {
            try {
                converter = facesContext.getApplication().createConverter(cid);
                if (converter != null && binding != null) {
                    binding.setValue(elContext, converter);
                }
            } catch (Exception e) {
                throw new JspException(e);
            }
        }

        return converter;
    }

    @Override
    public void release() {
        super.release();

        binding = null;
        converterId = null;
        defaultConverterId = null;
    }

}
