/*
 * $Id: AbstractListenerTag.java,v 1.1 2011/04/12 09:25:43 oeuillot Exp $
 */
package org.rcfaces.core.internal.taglib;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.util.ClassLocator;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:43 $
 */
public abstract class AbstractListenerTag extends TagSupport {

    private static final Log LOG = LogFactory.getLog(AbstractListenerTag.class);

    private ValueExpression type = null;

    public void setType(ValueExpression type) {
        this.type = type;
    }

    public void release() {
        this.type = null;
        super.release();
    }

    public int doStartTag() throws JspException {
        UIComponentClassicTagBase tag = UIComponentClassicTagBase
                .getParentUIComponentClassicTagBase(pageContext);
        if (tag == null) {
            // Object params[] = { this.getClass().getName() };
            throw new JspException("Invalid parent tag !");
        }

        if (!tag.getCreated()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Tag is not created !");
            }
            return SKIP_BODY;
        }

        UIComponent component = tag.getComponentInstance();
        if (component == null) {
            throw new JspException("Component is NULL");
        }

        if (type == null) {
            throw new JspException("Invalid type parameter.");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Prepare " + getListenerName() + " listener (type="
                    + type + ") for component '" + component.getId() + "'.");
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();

        String stype;
        if (type.isLiteralText() == false) {
            Object result = type.getValue(facesContext.getELContext());

            if ((result instanceof String) == false) {
                throw new JspException("Invalid value binding evaluation ! ("
                        + result + ")");
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Bound value=" + result + " expression=" + type);
            }

            stype = (String) result;

        } else {
            stype = type.getExpressionString();
        }

        Class listenerClass;
        try {
            listenerClass = ClassLocator.load(stype, this, facesContext);

        } catch (ClassNotFoundException e) {
            throw new JspException("Can not get class '" + stype + "'.", e);
        }

        Object listener;
        try {
            listener = listenerClass.newInstance();

        } catch (Throwable th) {
            throw new JspException("Can not instanciate listener class '"
                    + listenerClass + "'.", th);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Add " + getListenerName() + " listener '" + listener
                    + "' to component '" + component.getId() + "'.");
        }

        addListener(listener, component);

        return SKIP_BODY;

    }

    protected abstract String getListenerName();

    protected abstract void addListener(Object type, UIComponent component)
            throws JspException;
}
