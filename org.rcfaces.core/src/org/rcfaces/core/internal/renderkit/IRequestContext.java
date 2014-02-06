/*
 * $Id: IRequestContext.java,v 1.1 2011/04/12 09:25:41 oeuillot Exp $
 */
package org.rcfaces.core.internal.renderkit;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:41 $
 */
public interface IRequestContext /* extends IReleasable */{

    IProcessContext getProcessContext();

    FacesContext getFacesContext();

    boolean isLockedClientAttributes();

    IComponentData getComponentData(UIComponent component, String componentId,
            Renderer renderer);

    String getComponentId(UIComponent component);
}
