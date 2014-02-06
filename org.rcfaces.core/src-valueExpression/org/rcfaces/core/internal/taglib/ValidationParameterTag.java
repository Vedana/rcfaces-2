/*
 * $Id: ValidationParameterTag.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.taglib;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.rcfaces.core.internal.manager.IValidationParameters;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public class ValidationParameterTag extends TagSupport implements Tag {
    

    private static final long serialVersionUID = -1595891873980056092L;

    private static final boolean CLIENT_SIDE_DEFAULT_VALUE = true;

    private String name;

    private ValueExpression value;

    private boolean clientSide = CLIENT_SIDE_DEFAULT_VALUE;

    public final void setName(String name) {
        this.name = name;
    }

    public final void setValue(ValueExpression value) {
        this.value = value;
    }

    public boolean isClientSide() {
        return clientSide;
    }

    public void setClientSide(boolean clientSide) {
        this.clientSide = clientSide;
    }

    public int doStartTag() throws JspException {

        // Locate our parent UIComponentTag
        UIComponentClassicTagBase tag = UIComponentClassicTagBase
                .getParentUIComponentClassicTagBase(pageContext);
        if (tag == null) { // PENDING - i18n
            throw new JspException("Not nested in a UIComponentTag");
        }

        // Nothing to do unless this tag created a component
        if (!tag.getCreated()) {
            return (SKIP_BODY);
        }

        // FacesContext facesContext = FacesContext.getCurrentInstance();
        // Application application = facesContext.getApplication();

        UIComponent component = tag.getComponentInstance();
        if ((component instanceof IValidationParameters) == false) {
            throw new JspException(
                    "Component does not implement IValidationParameters");

        }

        IValidationParameters validatonParameterManager = (IValidationParameters) component;

        if (value.isLiteralText() == false) {
            validatonParameterManager.setValidationParameter(name, value,
                    clientSide);

        } else {
            validatonParameterManager.setValidationParameter(name, value,
                    clientSide);
        }

        return (SKIP_BODY);
    }

    public void release() {
        name = null;
        value = null;
        clientSide = CLIENT_SIDE_DEFAULT_VALUE;

        super.release();
    }

}
