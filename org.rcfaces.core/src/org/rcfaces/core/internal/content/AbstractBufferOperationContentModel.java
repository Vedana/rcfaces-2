/*
 * $Id: AbstractBufferOperationContentModel.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.contentStorage.GZipedResolvedContent;
import org.rcfaces.core.internal.contentStorage.IResolvedContent;
import org.rcfaces.core.internal.lang.ByteBufferOutputStream;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory.IResourceLoader;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public abstract class AbstractBufferOperationContentModel extends
        AbstractOperationContentModel implements IOperationContentLoader {

    private static final long serialVersionUID = -7085466442454979874L;

    private static final Log LOG = LogFactory
            .getLog(AbstractBufferOperationContentModel.class);

    private static final int INITIAL_BUFFER_SIZE = 8 * 1024;

    public AbstractBufferOperationContentModel(String resourceURL,
            String versionId, String operationId,
            String filterParametersToParse, IBufferOperation styleOperation,
            String specifiedResourceKey) {
        super(resourceURL, versionId, operationId, filterParametersToParse,
                styleOperation, specifiedResourceKey);
    }

    @Override
    protected IResourceLoaderFactory getResourceLoaderFactory(
            FacesContext facesContext) {

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        IResourceLoaderFactory resourceLoaderFactory;
        if (rcfacesContext.isDesignerMode()) {
            resourceLoaderFactory = Constants.getDesignerImageLoaderFactory();

        } else {
            resourceLoaderFactory = Constants.getImageLoaderFactory();
        }

        return resourceLoaderFactory;
    }

    @Override
    protected IResolvedContent getResolvedContent() {
        return new GZipedResolvedContent(this);
    }

    protected abstract String getCharsetFromStream(InputStream inputStream);

    protected abstract String getDefaultMimeType();

    protected abstract boolean isMimeTypeValid(String downloadedContentType);

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    public static class ContentInformation {
        private String charSet;

        private long lastModified;

        private long length;

        public final String getCharSet() {
            return charSet;
        }

        public final long getLastModified() {
            return lastModified;
        }

        public final long getLength() {
            return length;
        }

        public void merge(ContentInformation newContentInformation) {
            if (newContentInformation.lastModified > lastModified) {
                lastModified = newContentInformation.lastModified;
            }

            length += newContentInformation.getLength();
        }
    }

    public String loadContent(FacesContext facesContext,
            IResourceLoaderFactory resourceLoaderFactory, String path,
            String defaultCharset, ContentInformation contentInfoRef[]) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        ExternalContext externalContext = facesContext.getExternalContext();

        HttpServletRequest request = (HttpServletRequest) externalContext
                .getRequest();
        HttpServletResponse response = (HttpServletResponse) externalContext
                .getResponse();
        ServletContext context = (ServletContext) externalContext.getContext();

        IResourceLoader resourceLoader = resourceLoaderFactory.loadResource(
                context, request, response, path);

        String downloadedContentType = resourceLoader.getContentType();

        if (downloadedContentType != null) {
            int idx = downloadedContentType.indexOf(';');
            if (idx > 0) {
                downloadedContentType = downloadedContentType.substring(0, idx);
            }
        }

        if (downloadedContentType == null
                || isMimeTypeValid(downloadedContentType) == false) {
            LOG.error("Different content types requested='"
                    + getDefaultMimeType() + "' loaded='"
                    + downloadedContentType + "' for path '" + path + "'.");

            return null;
        }

        InputStream inputStream = resourceLoader.openStream();

        if (inputStream == null) {
            LOG.error("Can not get resource specified by path '" + path + "'.");

            return null;
        }

        ContentInformation contentInfo = new ContentInformation();

        if (contentInfoRef != null) {
            contentInfoRef[0] = contentInfo;
        }

        try {
            int size = INITIAL_BUFFER_SIZE;
            long originalSize = resourceLoader.getContentLength();
            contentInfo.length = originalSize;

            if (originalSize > 0 && originalSize < INITIAL_BUFFER_SIZE) {
                size = (int) originalSize + 256;
            }

            ByteBufferOutputStream bous = new ByteBufferOutputStream(size);

            byte b[] = new byte[size];
            for (;;) {
                int ret = inputStream.read(b);
                if (ret <= 0) {
                    break;
                }

                bous.write(b, 0, ret);
            }

            bous.close();

            byte buffer[] = bous.toByteArray();

            String charSet = getCharsetFromStream(new ByteArrayInputStream(
                    buffer));

            if (charSet == null) {
                charSet = defaultCharset;
            }

            contentInfo.charSet = charSet;
            contentInfo.lastModified = resourceLoader.getLastModified();

            return new String(buffer, charSet);

        } catch (IOException ex) {
            LOG.error("Can not make buffer", ex);
            return null;

        } finally {
            try {
                inputStream.close();

            } catch (IOException ex) {
                LOG.debug("Can not close resource '" + path + "'.", ex);
            }
        }
    }

}