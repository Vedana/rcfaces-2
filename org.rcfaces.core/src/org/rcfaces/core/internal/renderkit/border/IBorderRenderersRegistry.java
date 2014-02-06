/*
 * $Id: IBorderRenderersRegistry.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.renderkit.border;

import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public interface IBorderRenderersRegistry {
    IBorderRenderer getBorderRenderer(FacesContext facesContext,
            String renderKitId, String componentFamily,
            String componentRenderType, String borderType);
}
