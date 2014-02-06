/*
 * $Id: AbstractHtmlComponentRenderContext.java,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.capability.IClientDataCapability;
import org.rcfaces.core.internal.renderkit.AbstractComponentRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
public abstract class AbstractHtmlComponentRenderContext extends
        AbstractComponentRenderContext implements IHtmlComponentRenderContext {

    private static final String CLIENT_DATA_COUNT_PROPERTY_NAME = "org.rcfaces.renderkit.html.CLIENT_DATA_COUNT";

    protected AbstractHtmlComponentRenderContext(FacesContext facesContext,
            UIComponent component, String componentClientId) {
        super(facesContext, component, componentClientId);
    }

    public boolean hasClientDatas(boolean clear) {
        UIComponent component = getComponent();
        if ((component instanceof IClientDataCapability) == false) {
            return false;
        }

        int clientDataCount = 0;
        if (clientDataCount < 0) {
            Integer cdc = (Integer) getAttribute(CLIENT_DATA_COUNT_PROPERTY_NAME);
            if (cdc != null) {
                clientDataCount = cdc.intValue();
            }
        }

        int count = ((IClientDataCapability) component).getClientDataCount();
        if (count <= clientDataCount) {
            return false;
        }

        if (clear == false) {
            return true;
        }

        clientDataCount = count;

        setAttribute(CLIENT_DATA_COUNT_PROPERTY_NAME, new Integer(
                clientDataCount));
        return true;
    }

    public IHtmlRenderContext getHtmlRenderContext() {
        return (IHtmlRenderContext) getRenderContext();
    }

    protected IHtmlRenderContext getRenderContext(FacesContext facesContext) {
        return HtmlRenderContext.getRenderContext(facesContext);
    }
}
