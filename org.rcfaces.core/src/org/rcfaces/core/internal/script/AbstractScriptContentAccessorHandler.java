/*
 * $Id: AbstractScriptContentAccessorHandler.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 */
package org.rcfaces.core.internal.script;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.contentAccessor.AbstractCompositeContentAccessorHandler;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public abstract class AbstractScriptContentAccessorHandler extends
        AbstractCompositeContentAccessorHandler implements
        IScriptContentAccessorHandler {
    

    public static IScriptContentAccessorHandler getScriptContentAccessorHandler(
            FacesContext facesContext) {
        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        return (IScriptContentAccessorHandler) rcfacesContext
                .getProvidersRegistry().getProvider(SCRIPT_CONTENT_PROVIDER_ID);
    }
}
