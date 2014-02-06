/*
 * $Id: ContentAccessorFactory.java,v 1.4 2013/11/13 12:53:23 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.familly.IContentAccessors;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.lang.IContentFamily;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:23 $
 */
public class ContentAccessorFactory {

    private static final Log LOG = LogFactory
            .getLog(ContentAccessorFactory.class);

    public static final IContentAccessor UNSUPPORTED_CONTENT_ACCESSOR = new IContentAccessor() {

        public Object getAttribute(String attributeName) {
            return null;
        }

        public Map<String, Object> getAttributes() {
            return null;
        }

        public Object getContentRef() {
            return null;
        }

        public IContentVersionHandler getContentVersionHandler() {
            return null;
        }

        public IContentProxyHandler getContentProxyHandler() {
            return null;
        }

        public void setContentProxyHandler(
                IContentProxyHandler contentProxyHandler) {
        }

        public void setContentVersionHandler(
                IContentVersionHandler contentVersionHandler) {
        }

        public int getPathType() {
            return 0;
        }

        public IContentAccessor getParentAccessor() {
            return null;
        }

        public IContentFamily getContentFamily() {
            return null;
        }

        public String resolveURL(FacesContext facesContext,
                IGeneratedResourceInformation contentInformation,
                IGenerationResourceInformation generationInformation) {
            return null;
        }

        public String resolveURL(FacesContext facesContext,
                IGeneratedResourceInformation contentInformation,
                IGenerationResourceInformation generationInformation,
                int pathTypeMask) {
            return null;
        }

        public void setPathType(int pathType) {
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        public String convertToPathType(FacesContext facesContext,
                int targetPathType) {
            return null;
        }

        public IContentPath getParentContentPath() {
            return null;
        }

        public String getPath() {
            return null;
        }

    };

    protected static final IContentProxyHandler RESOURCE_CONTENT_PROXY_HANDLER = new AbstractContentProxyHandler() {

        public String getId() {
            return "ResourceProxyHandler(proxy)";
        }

        @SuppressWarnings("unused")
        public IContentAccessor getProxyedContentAccessor(
                RcfacesContext rcfacesContext, FacesContext facesContext,
                IContentAccessor contentAccessor,
                IGeneratedResourceInformation[] contentInformationRef) {

            if (Constants.RESOURCE_CONTENT_PROXY_SUPPORT == false) {
                return null;
            }

            IContentProxyHandler contentProxyHandler = rcfacesContext
                    .getDefaultContentProxyHandler();
            if (contentProxyHandler == null) {
                return null;
            }

            return contentProxyHandler.getProxyedContentAccessor(
                    rcfacesContext, facesContext, contentAccessor,
                    contentInformationRef);
        }

    };

    protected static final IContentVersionHandler RESOURCE_CONTENT_VERSION_HANDLER = new AbstractContentVersionHandler() {

        public String getId() {
            return "ResourceVersionHandler(proxy)";
        }

        @SuppressWarnings("unused")
        public IContentAccessor getVersionedContentAccessor(
                RcfacesContext rcfacesContext, FacesContext facesContext,
                IContentAccessor contentAccessor,
                IGeneratedResourceInformation[] contentInformation) {

            if (Constants.RESOURCE_CONTENT_VERSION_SUPPORT == false) {
                return null;
            }

            IContentVersionHandler contentVersionHandler = rcfacesContext
                    .getDefaultContentVersionHandler();
            if (contentVersionHandler == null) {
                return null;
            }

            return contentVersionHandler.getVersionedContentAccessor(
                    rcfacesContext, facesContext, contentAccessor,
                    contentInformation);
        }

        @SuppressWarnings("unused")
        public String getVersionTag(RcfacesContext rcfacesContext,
                FacesContext facesContext, String relativeUrl,
                IContentAccessor contentAccessor,
                IGeneratedResourceInformation contentInformation) {

            if (Constants.RESOURCE_CONTENT_VERSION_SUPPORT == false) {
                return null;
            }

            IContentVersionHandler contentVersionHandler = rcfacesContext
                    .getDefaultContentVersionHandler();
            if (contentVersionHandler == null) {
                return null;
            }

            return contentVersionHandler.getVersionTag(rcfacesContext,
                    facesContext, relativeUrl, contentAccessor,
                    contentInformation);
        }

    };

    public static IContentAccessor createFromWebResource(
            FacesContext facesContext, Object value, IContentFamily type) {

        IContentVersionHandler contentVersionHandler = RESOURCE_CONTENT_VERSION_HANDLER;
        IContentProxyHandler contentProxyHandler = RESOURCE_CONTENT_PROXY_HANDLER;
        if (IContentFamily.HELP.equals(type) || IContentFamily.JSP.equals(type)) {
            contentVersionHandler = null;
            contentProxyHandler = null;
        }

        return new BasicContentAccessor(facesContext, value, type,
                contentVersionHandler, contentProxyHandler);
    }

    public static IContentAccessor createFromWebResource(
            FacesContext facesContext, Object value, IContentAccessor parent) {

        return new BasicContentAccessor(facesContext, value, parent,
                IContentPath.UNDEFINED_PATH_TYPE);
    }

    public static IContentAccessors createSingleImageWebResource(
            FacesContext facesContext, Object value, IContentFamily image) {
        return new SimpleImageAccessor(facesContext, value, image,
                RESOURCE_CONTENT_VERSION_HANDLER,
                RESOURCE_CONTENT_PROXY_HANDLER);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:23 $
     */
    protected static class SimpleImageAccessor extends BasicContentAccessor
            implements IImageAccessors {

        public SimpleImageAccessor(FacesContext facesContext, Object url,
                IContentFamily contentType,
                IContentVersionHandler versionHandler,
                IContentProxyHandler contentProxyHandler) {
            super(facesContext, url, contentType, versionHandler,
                    contentProxyHandler);
        }

        public IContentAccessor getImageAccessor() {
            return this;
        }
    }

}
