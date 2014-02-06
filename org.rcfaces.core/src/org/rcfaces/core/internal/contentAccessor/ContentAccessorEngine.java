/*
 * $Id: ContentAccessorEngine.java,v 1.3 2013/11/13 12:53:23 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:23 $
 */
public class ContentAccessorEngine {
    
    private static final Log LOG = LogFactory
            .getLog(ContentAccessorEngine.class);

    public static IContentAccessor resolveURL(FacesContext facesContext,
            final IContentAccessor contentAccessor,
            final IGeneratedResourceInformation generatedInformation,
            final IGenerationResourceInformation generationInformation) {

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        IContentAccessorRegistry registry = rcfacesContext
                .getContentAccessorRegistry();

        IContentAccessorHandler handlers[] = registry
                .listContentAccessorHandlers(contentAccessor.getContentFamily());

        if (LOG.isDebugEnabled()) {
            LOG.debug("Try to resolve URL '" + contentAccessor + "'");
        }

        IContentAccessor returnContentAccessor = contentAccessor;

        IGeneratedResourceInformation contentInformationRef[] = new IGeneratedResourceInformation[] { generatedInformation };
        for (int i = 0; i < handlers.length; i++) {
            IContentAccessorHandler handler = handlers[i];

            IContentAccessor newContentAccessor = handler.handleContent(
                    facesContext, returnContentAccessor, contentInformationRef,
                    generationInformation);

            if (LOG.isDebugEnabled()) {
                LOG.debug("ContentAccessorHandler(" + handler.getId()
                        + ") returns " + newContentAccessor);
            }

            if (newContentAccessor != null) {
                returnContentAccessor = newContentAccessor;
                break;
            }
        }

        Object result = returnContentAccessor.getContentRef();
        if (result == null
                || result
                        .equals(ContentAccessorFactory.UNSUPPORTED_CONTENT_ACCESSOR)) {
            return null;
        }

        IContentVersionHandler contentVersionHandler = returnContentAccessor
                .getContentVersionHandler();
        if (contentVersionHandler != null) {
            IContentAccessor versionedContentAccessor = contentVersionHandler
                    .getVersionedContentAccessor(rcfacesContext, facesContext,
                            returnContentAccessor, contentInformationRef);

            if (LOG.isDebugEnabled()) {
                LOG.debug("contentVersionHandler("
                        + contentVersionHandler.getId() + ") returns "
                        + versionedContentAccessor);
            }

            if (versionedContentAccessor != null) {
                returnContentAccessor = versionedContentAccessor;
            }
        }

        IContentProxyHandler contentProxyHandler = returnContentAccessor
                .getContentProxyHandler();
        if (contentProxyHandler != null) {
            IContentAccessor proxyedContentAccessor = contentProxyHandler
                    .getProxyedContentAccessor(rcfacesContext, facesContext,
                            returnContentAccessor, contentInformationRef);

            if (LOG.isDebugEnabled()) {
                LOG.debug("contentProxyHandler(" + contentProxyHandler.getId()
                        + ") returns " + proxyedContentAccessor);
            }

            if (proxyedContentAccessor != null) {
                returnContentAccessor = proxyedContentAccessor;
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("ResolveURL '" + contentAccessor + "' returns '"
                    + returnContentAccessor + "'");
        }
        return returnContentAccessor;
    }
}
