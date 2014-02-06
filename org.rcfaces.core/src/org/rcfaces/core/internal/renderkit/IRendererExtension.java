/*
 * $Id: IRendererExtension.java,v 1.1 2011/04/12 09:25:40 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.renderkit;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:40 $
 */
public interface IRendererExtension {
    boolean getDecodesChildren();

    void decodeEnd(FacesContext context, UIComponent component);

    void decodeChildren(FacesContext context, UIComponent component);
}
