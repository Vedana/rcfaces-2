/*
 * $Id: AbstractTreeRendererProcessor.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.renderkit.tools;

import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public abstract class AbstractTreeRendererProcessor implements
        IComponentTreeRenderProcessor {
    

    protected final FacesContext facesContext;

    protected AbstractTreeRendererProcessor(FacesContext facesContext) {
        this.facesContext = facesContext;
    }
}
