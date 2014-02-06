/*
 * $Id: IComponentRenderContext.java,v 1.1 2011/04/12 09:25:41 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.renderkit;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:41 $
 */
public interface IComponentRenderContext {
    FacesContext getFacesContext();

    IRenderContext getRenderContext();

    UIComponent getComponent();

    String getComponentClientId();

    boolean containsAttribute(String key);

    Object getAttribute(String key);

    Object setAttribute(String key, Object value);

    Object removeAttribute(String key);

    boolean isComponentVisible();
}
