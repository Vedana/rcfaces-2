/*
 * $Id: IResourceProxyHandler.java,v 1.1 2011/04/12 09:25:47 oeuillot Exp $
 */
package org.rcfaces.core.internal.contentProxy;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:47 $
 */
public interface IResourceProxyHandler {

    String ID = "org.rcfaces.core.RESOURCE_PROXY_PROVIDER";

    boolean isEnabled();

    boolean isFiltredResourcesEnabled();

    String computeProxyedURL(FacesContext facesContext,
            IContentAccessor contentAccessor,
            IGeneratedResourceInformation[] contentInformationRef, String url);

    boolean isFrameworkResourcesEnabled();
}
