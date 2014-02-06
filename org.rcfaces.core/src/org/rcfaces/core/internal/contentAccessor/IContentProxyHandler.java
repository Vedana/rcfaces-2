/*
 * $Id: IContentProxyHandler.java,v 1.1 2011/04/12 09:25:28 oeuillot Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.RcfacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:28 $
 */
public interface IContentProxyHandler {

    String getId();

    IContentAccessor getProxyedContentAccessor(RcfacesContext rcfacesContext,
            FacesContext facesContext, IContentAccessor returnContentAccessor,
            IGeneratedResourceInformation[] contentInformationRef);

}
