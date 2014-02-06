/*
 * $Id: ClassLoaderResourceLoaderFactory.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.internal.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public class ClassLoaderResourceLoaderFactory extends
        AbstractResourceLoaderFactory {

    private static final Log LOG = LogFactory
            .getLog(ClassLoaderResourceLoaderFactory.class);

    public String getName() {
        return "Load resource by classloader context";
    }

    public IResourceLoader loadResource(ServletContext context,
            HttpServletRequest request, HttpServletResponse response, String uri) {
        return new ClassLoaderResourceLoader(context, uri);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
     */
    private static class ClassLoaderResourceLoader implements IResourceLoader {

        private final ServletContext servletContext;

        private final String url;

        private URLConnection urlConnection;

        private boolean errored;

        private int contentLength;

        private long lastModified;

        private String contentType;

        public ClassLoaderResourceLoader(ServletContext servletContext,
                String url) {
            this.servletContext = servletContext;
            this.url = url;
        }

        public boolean isErrored() {
            if (errored == false && urlConnection == null) {
                openURLConnection();
            }
            return errored;
        }

        private void openURLConnection() {

            String url = this.url;
            if (url.startsWith("/") == false) {
                url = "/" + url;
            }

            URL resourceURL;
            try {
                resourceURL = servletContext.getResource(url);

            } catch (MalformedURLException ex) {
                LOG.error("Malformed url '" + url + "'.", ex);
                errored = true;
                return;
            }

            if (resourceURL == null) {
                LOG.error("Can not get resource specified by path '" + url
                        + "'.");
                errored = true;
                return;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Search resource '/" + url + "' => '" + resourceURL
                        + "'.");
            }

            try {
                urlConnection = resourceURL.openConnection();
                if (urlConnection == null) {
                    LOG.error("Can not get resource specified by path '" + url
                            + "'.");
                    errored = true;
                    return;
                }

                contentLength = urlConnection.getContentLength();
                lastModified = urlConnection.getLastModified();
                contentType = urlConnection.getContentType();

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Resource '"
                            + resourceURL
                            + "' contentType='"
                            + contentType
                            + "' contentLength="
                            + contentLength
                            + " lastModified="
                            + ((lastModified > 0) ? new Date(lastModified)
                                    .toString() : String.valueOf(lastModified)));
                }

            } catch (IOException ex) {
                LOG.error("Can not get content of resource '" + resourceURL
                        + "'.", ex);
                errored = true;
            }
        }

        public InputStream openStream() {
            if (isErrored()) {
                return null;
            }

            try {
                InputStream ins = urlConnection.getInputStream();
                if (ins != null) {
                    return ins;
                }

                LOG.error("Can not get resource specified by path '" + url
                        + "'.");
                errored = true;
                return null;

            } catch (IOException ex) {
                LOG.error("Can not get content of resource '" + url + "'.", ex);
                errored = true;
                return null;
            }
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

    }
}