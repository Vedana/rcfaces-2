/*
 * $Id: CameliaTag.java,v 1.3 2013/07/03 12:25:07 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.taglib;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentELTag;
import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.capability.IComponentLifeCycle;
import org.rcfaces.core.internal.capability.IRCFacesComponent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:07 $
 */
public abstract class CameliaTag extends UIComponentELTag {
    

    private static final Log LOG = LogFactory.getLog(CameliaTag.class);

    private static final boolean debugEnabled = LOG.isDebugEnabled();

    private ValueExpression myBinding;

    protected static final boolean getBool(String value) {
        return Boolean.valueOf(value).booleanValue();
    }

    protected static final Boolean getBoolean(String value) {
        if (value == null) {
            return null;
        }

        return Boolean.valueOf(value);
    }
    
    protected static final Number getNumber(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }

        if (value.indexOf('.') > 0) {
            Double dbl = Double.valueOf(value);

            return dbl;
        }

        long l = Long.parseLong(value);
        if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
            return new Integer((int) l);
        }

        return new Long(l);
    }

    protected static final Integer getInteger(String value) {
        if (value == null) {
            return null;
        }

        return Integer.valueOf(value);
    }

    protected static final int getInt(String value) {
        return Integer.parseInt(value);
    }

    protected static final double getDouble(String value) {
        return Double.parseDouble(value);
    }

    public void release() {
        myBinding = null;
        super.release();
    }

    public String getRendererType() {
        return null;
    }

    public int doAfterBody() throws JspException {
        try {
            return super.doAfterBody();

        } catch (RuntimeException ex) {
            LOG.error("Can not doAfterBody component '" + getId() + "'.", ex);

            throw ex;
        }
    }

    public int doEndTag() throws JspException {
        try {
            return super.doEndTag();

        } catch (RuntimeException ex) {
            LOG.error("Can not doEndTag component '" + getId() + "'.", ex);

            throw ex;
        }
    }

    public void doInitBody() throws JspException {
        try {
            super.doInitBody();

        } catch (RuntimeException ex) {
            LOG.error("Can not doInitBody component '" + getId() + "'.", ex);

            throw ex;
        }

    }

    public int doStartTag() throws JspException {
        try {
            return super.doStartTag();

        } catch (RuntimeException ex) {
            LOG.error("Can not doStartTag component '" + getId() + "'.", ex);

            throw ex;
        }
    }

    protected void setProperties(UIComponent component) {

        super.setProperties(component);
    }

    protected UIComponent createComponent(FacesContext facesContext,
            String newId) throws JspException {

        if (myBinding == null) {
            UIComponent component = super.createComponent(facesContext, newId);

            if (component instanceof IComponentLifeCycle) {
                IComponentLifeCycle componentLifeCycle = (IComponentLifeCycle) component;

                componentLifeCycle.initializePhase(facesContext, false);
            }

            if (debugEnabled) {
                LOG.debug("Create component for id '" + newId + "' returns '"
                        + component + "'.");
            }

            return component;
        }

        Object bindingValue = myBinding.getValue(getELContext());

        UIComponent component = super.createComponent(facesContext, newId);

        if (component instanceof IComponentLifeCycle) {
            IComponentLifeCycle componentLifeCycle = (IComponentLifeCycle) component;

            componentLifeCycle.initializePhase(facesContext,
                    bindingValue != null);
        }

        if (debugEnabled) {
            LOG.debug("Create component for id '" + newId + "' returns '"
                    + component + "'.");
        }

        return component;
    }

    public void setBinding(ValueExpression binding) throws JspException {
        this.myBinding = binding;

        super.setBinding(binding);
    }

    protected UIComponent findComponent(FacesContext context)
            throws JspException {

        if (debugEnabled) {
            LOG.debug("Find component '" + getId() + "' ... (current="
                    + getComponentInstance() + ")");
        }

        UIComponent component = super.findComponent(context);

        if (debugEnabled) {
            LOG.debug("Find component '" + getId() + "' returns '" + component
                    + "'");
        }

        return component;
    }

}
