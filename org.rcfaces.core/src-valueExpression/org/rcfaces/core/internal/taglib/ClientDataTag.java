/*
 * $Id: ClientDataTag.java,v 1.3 2013/11/26 13:55:57 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.taglib;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.rcfaces.core.internal.manager.IClientDataManager;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/26 13:55:57 $
 */
public class ClientDataTag extends TagSupport implements Tag {
    

    private static final long serialVersionUID = -7633749361412060360L;

    private String name;

    private ValueExpression value;

    public void setName(String name) {
        this.name = name;
    }

    public final void setValue(ValueExpression value) {
        this.value = value;
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
        if ((component instanceof IClientDataManager) == false) {
            throw new JspException(
                    "Component does not implement IClientDataManager");

        }

        IClientDataManager clientDataCapability = (IClientDataManager) component;

        if (value.isLiteralText() == false) {
            clientDataCapability.setClientData(name, value);

        } else {
            clientDataCapability.setClientData(name, value);
        }

        return (SKIP_BODY);

    }

    /**
     * <p>
     * Release references to any acquired resources.
     */
    public void release() {
        name = null;
        value = null;

        super.release();
    }

}
