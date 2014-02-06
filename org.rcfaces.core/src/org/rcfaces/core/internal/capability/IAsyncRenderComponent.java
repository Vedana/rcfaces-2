/*
 * $Id: IAsyncRenderComponent.java,v 1.1 2011/04/12 09:25:38 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.capability;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.renderkit.IAsyncRenderer;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:38 $
 */
public interface IAsyncRenderComponent {

    IAsyncRenderer getAsyncRenderer(FacesContext facesContext);
}
