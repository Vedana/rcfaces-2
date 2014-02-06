/*
 * $Id: IncludeResourceLoaderFactory.java,v 1.4 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.internal.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.ByteBufferInputStream;
import org.rcfaces.core.internal.lang.ByteBufferOutputStream;
import org.rcfaces.core.internal.util.IncludeHttpServletRequest;
import org.rcfaces.core.internal.util.IncludeHttpServletResponse;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:25 $
 */

public class IncludeResourceLoaderFactory extends AbstractResourceLoaderFactory {

    private static final Log LOG = LogFactory
            .getLog(IncludeResourceLoaderFactory.class);

    public String getName() {
        return "Load resource by servlet inclusion.";
    }

    public IResourceLoader loadResource(ServletContext context,
            HttpServletRequest request, HttpServletResponse response, String uri) {
        return new IncludeResourceLoader(context, request, response, uri);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:25 $
     */
    private static class IncludeResourceLoader implements IResourceLoader {
        private static final String TEMP_FILE_PREFIX = "include-resource.";

        private static final String TEMP_FILE_SUFFIX = ".tmp";

        private static final int BUFFER_DEFAULT_SIZE = 0;

        private static final String DEFAULT_CHARSET = "UTF-8";

        private static final long MEMORY_BUFFER_MAX_SIZE = 4096;

        private final ServletContext servletContext;

        private final HttpServletRequest servletRequest;

        private final HttpServletResponse servletResponse;

        private final String uri;

        private int contentLength;

        private long lastModified;

        private String contentType;

        private boolean errored;

        private boolean bufferInitialized;

        private File bufferFile;

        private byte bufferArray[];

        public IncludeResourceLoader(ServletContext servletContext,
                HttpServletRequest servletRequest,
                HttpServletResponse servletResponse, String uri) {
            this.servletContext = servletContext;
            this.servletRequest = servletRequest;
            this.servletResponse = servletResponse;
            this.uri = uri;
        }

        public int getContentLength() {
            if (isErrored()) {
                return -1;
            }
            return contentLength;
        }

        public long getLastModified() {
            if (isErrored()) {
                return -1;
            }
            return lastModified;
        }

        public String getContentType() {
            if (isErrored()) {
                return null;
            }
            return contentType;
        }

        public boolean isErrored() {
            if (errored == false && bufferInitialized == false) {
                getContent();
            }
            return errored;
        }

        public InputStream openStream() {
            if (isErrored()) {
                return null;
            }

            if (bufferArray != null) {
                return new ByteBufferInputStream(bufferArray);
            }

            if (bufferFile != null) {
                try {
                    return new FileInputStream(bufferFile);

                } catch (FileNotFoundException e) {
                    LOG.error("Can not open buffer file '" + bufferFile
                            + "' for resource url '" + uri + "'.", e);
                }
            }

            return null;
        }

        private void getContent() {
            if (bufferInitialized) {
                return;
            }
            bufferInitialized = true;

            File fileTemp;
            try {
                fileTemp = File.createTempFile(TEMP_FILE_PREFIX,
                        TEMP_FILE_SUFFIX);
            } catch (IOException ex) {
                LOG.error("Failed to call request " + uri + ".", ex);
                errored = true;
                return;
            }

            try {
                OutputStream output = null;
                if (fileTemp != null) {

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Use temp file='"
                                + fileTemp.getAbsolutePath()
                                + "' to store response of resource request '"
                                + uri + "'.");
                    }

                    try {
                        output = new FileOutputStream(fileTemp);

                    } catch (FileNotFoundException ex) {
                        LOG.error(
                                "Can not write into file '" + fileTemp + "'.",
                                ex);
                    }
                }
                if (output == null) {
                    output = new ByteBufferOutputStream(BUFFER_DEFAULT_SIZE);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Use memory byte array (size="
                                + BUFFER_DEFAULT_SIZE
                                + ") to store response of resource request '"
                                + uri + "'.");
                    }
                }

                ServletRequest request = new IncludeHttpServletRequest(
                        servletRequest);
                IncludeHttpServletResponse response = IncludeHttpServletResponse
                        .create(servletResponse, output, DEFAULT_CHARSET);

                String requestURI;
                if (uri.charAt(0) != '/') {
                    requestURI = "/" + uri;

                } else {
                    requestURI = uri;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Send request to get resource content of '"
                            + requestURI + "'.");
                }

                RequestDispatcher requestDispatcher = servletContext
                        .getRequestDispatcher(requestURI);
                if (requestDispatcher == null) {
                    LOG.error("Can not get request dispatcher for url '"
                            + requestURI + "'.");
                    errored = true;
                    return;
                }

                try {
                    requestDispatcher.include(request, response);

                } catch (Exception ex) {
                    LOG.error("Failed to call request " + uri + ".", ex);
                    errored = true;
                    return;
                }

                try {
                    output.close();

                } catch (IOException ex) {
                    LOG.error("Can not close buffer !", ex);
                }

                if (response.getStatus() != HttpServletResponse.SC_OK) {
                    LOG.error("Invalid response status: "
                            + response.getStatus());
                    errored = true;
                    return;
                }

                this.lastModified = response.getLastModified();
                this.contentLength = response.getContentLength();
                this.contentType = response.getContentType();

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Resource '"
                            + uri
                            + "' contentType='"
                            + contentType
                            + "' contentLength="
                            + contentLength
                            + " lastModified="
                            + ((lastModified > 0) ? new Date(lastModified)
                                    .toString() : String.valueOf(lastModified)));
                }

                if (fileTemp != null) {
                    bufferFile = fileTemp;
                    bufferFile.deleteOnExit();

                } else {
                    bufferArray = ((ByteBufferOutputStream) output)
                            .toByteArray();
                }

            } finally {
                if (errored && fileTemp != null) {
                    fileTemp.delete();
                }
            }
        }

        @Override
        protected void finalize() throws Throwable {
            if (bufferFile != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Finalize resource loader, delete file '"
                            + bufferFile.getAbsolutePath() + "'.");
                }
                try {
                    bufferFile.delete();

                } catch (Throwable th) {
                    LOG.error(
                            "Can not delete file '"
                                    + bufferFile.getAbsolutePath() + "'.", th);
                }
                bufferFile = null;
            }

            super.finalize();
        }
    }

}
