/*
 * $Id: PartialRenderingContextFactory.java,v 1.1 2011/04/12 09:25:49 oeuillot Exp $
 */
package org.rcfaces.core.partialRendering;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.util.PartialRenderingContextImpl;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:49 $
 */
public class PartialRenderingContextFactory {
    private static final String PARTIAL_RENDERING_CONTEXT_PROPERTY = "org.rcfaces.core.PARTIAL_RENDERING_CONTEXT";

    public static IPartialRenderingContext get() {
        return get(null);
    }

    public static IPartialRenderingContext get(FacesContext facesContext) {
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        Map requestMap = facesContext.getExternalContext().getRequestMap();

        IPartialRenderingContext partialRenderingContext = (IPartialRenderingContext) requestMap
                .get(PARTIAL_RENDERING_CONTEXT_PROPERTY);
        if (partialRenderingContext != null) {
            return partialRenderingContext;
        }

        partialRenderingContext = new PartialRenderingContextImpl(facesContext);

        requestMap.put(PARTIAL_RENDERING_CONTEXT_PROPERTY,
                partialRenderingContext);

        return partialRenderingContext;
    }
}
