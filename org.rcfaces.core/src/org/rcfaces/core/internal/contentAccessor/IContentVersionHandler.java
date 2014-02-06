/*
 * $Id: IContentVersionHandler.java,v 1.1 2011/04/12 09:25:28 oeuillot Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.RcfacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:28 $
 */
public interface IContentVersionHandler {

    String getId();

    IContentAccessor getVersionedContentAccessor(RcfacesContext rcfacesContext,
            FacesContext facesContext, IContentAccessor contentAccessor,
            IGeneratedResourceInformation[] contentInformation);

    String getVersionTag(RcfacesContext rcfacesContext,
            FacesContext facesContext, String relativeUrl,
            IContentAccessor contentAccessor,
            IGeneratedResourceInformation contentInformation);
}
