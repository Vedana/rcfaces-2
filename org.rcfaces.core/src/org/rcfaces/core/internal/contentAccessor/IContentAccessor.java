/*
 * $Id: IContentAccessor.java,v 1.2 2013/11/13 12:53:23 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.rcfaces.core.lang.IContentFamily;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:23 $
 */
public interface IContentAccessor extends IContentPath {

    String FILTER_SEPARATOR = "::";

    IContentFamily getContentFamily();

    Object getAttribute(String attributeName);

    Map<String, Object> getAttributes();

    Object getContentRef();

    String resolveURL(FacesContext facesContext,
            IGeneratedResourceInformation contentInformation,
            IGenerationResourceInformation generationInformation);

    String resolveURL(FacesContext facesContext,
            IGeneratedResourceInformation contentInformation,
            IGenerationResourceInformation generationInformation,
            int pathTypeMask);

    IContentAccessor getParentAccessor();

    IContentVersionHandler getContentVersionHandler();

    void setContentVersionHandler(IContentVersionHandler contentVersionHandler);

    IContentProxyHandler getContentProxyHandler();

    void setContentProxyHandler(IContentProxyHandler contentProxyHandler);
}
