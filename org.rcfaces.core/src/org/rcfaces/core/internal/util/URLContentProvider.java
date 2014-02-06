/*
 * $Id: URLContentProvider.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.repository.AbstractRepository.AbstractContent;
import org.rcfaces.core.internal.repository.IContentRef;
import org.rcfaces.core.internal.repository.IRepository.IContent;
import org.rcfaces.core.internal.repository.IRepository.IContentProvider;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.repository.URLContentRef;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class URLContentProvider implements IContentProvider {

    private static final Log LOG = LogFactory.getLog(URLContentProvider.class);

    public static final IContentProvider SINGLETON = new URLContentProvider();

    protected URLContentProvider() {
    }

    public IContent getContent(IContentRef contentReference) {
        URLContentRef urlContentRef = (URLContentRef) contentReference;

        return new URLContent(urlContentRef);
    }

    public IContentRef[] searchCriteriaContentReference(
            IContentRef contentReference, ICriteria criteria) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Criteria version '" + criteria + "' not found for url '"
                    + contentReference + "'.");
        }

        return null;
    }

    protected boolean testURL(URLContentRef urlContentRef) {
        IContent content = getContent(urlContentRef);

        if (LOG.isTraceEnabled()) {
            LOG.trace("TestURL '" + urlContentRef.getURL() + "' criteria='"
                    + urlContentRef.getCriteria() + "'");
        }

        InputStream inputStream;
        try {
            inputStream = content.getInputStream();

        } catch (IOException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("URL '" + urlContentRef.getURL()
                        + "' does not exist !", ex);
            }
            return false;
        }

        if (inputStream == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("TestURL '" + urlContentRef.getURL() + "' criteria='"
                        + urlContentRef.getCriteria() + "' => NOT FOUND");
            }

            return false;
        }

        try {
            inputStream.close();

        } catch (IOException ex) {
            LOG.info("Can not close URL '" + urlContentRef.getURL() + "'.", ex);
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("TestURL '" + urlContentRef.getURL() + "' criteria='"
                    + urlContentRef.getCriteria() + "' => FOUND");
        }

        return true;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    protected static class URLContent extends AbstractContent {

        protected final URL url;

        protected final ICriteria criteria;

        private URLConnection urlConnection;

        private boolean opened;

        public URLContent(URL url, ICriteria criteria) {
            this.url = url;
            this.criteria = criteria;
        }

        public URLContent(URLContentRef urlContentRef) {
            this(urlContentRef.getURL(), urlContentRef.getCriteria());
        }

        public InputStream getInputStream() throws IOException {
            return getInputStream(false);
        }

        protected InputStream getInputStream(boolean toClose)
                throws IOException {
            if (opened) {
                throw new IOException("Already opened !");
            }
            opened = true;

            return openInputStream(toClose);
        }

        protected InputStream openInputStream(boolean toClose)
                throws IOException {
            return getURLConnection().getInputStream();
        }

        protected URLConnection getURLConnection() throws IOException {
            if (urlConnection != null) {
                return urlConnection;
            }

            urlConnection = url.openConnection();

            return urlConnection;
        }

        @Override
        public long getLastModified() throws IOException {
            URLConnection urlConnection = getURLConnection();

            return urlConnection.getLastModified();
        }

        @Override
        public long getLength() throws IOException {
            URLConnection urlConnection = getURLConnection();

            return urlConnection.getContentLength();
        }

        @Override
        public void release() {
            if (urlConnection == null || opened) {
                return;
            }

            try {
                InputStream in = getInputStream(true);
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }

            urlConnection = null;
        }
    }
}
